package com.platform.catalog.application.service;

import com.platform.catalog.application.dto.*;
import com.platform.catalog.application.mapper.ProductMapper;
import com.platform.catalog.domain.event.ProductCreatedEvent;
import com.platform.catalog.domain.model.*;
import com.platform.catalog.domain.repository.CategoryRepository;
import com.platform.catalog.domain.repository.ProductRepository;
import com.platform.catalog.domain.repository.ProductVariantRepository;
import com.platform.core.client.RestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.domain.Money;
import com.platform.core.dto.PagedResponse;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.core.security.SecurityUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final DomainEventPublisher eventPublisher;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;

    private RestClient iamRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        UUID vendorId = SecurityUtils.getCurrentVendorId()
                .orElseThrow(() -> new BusinessException("NOT_VENDOR", "Only vendors can create products"));

        // Check vendor active status via REST
        Boolean isActive = iamRestClient.get(
                "/api/v1/vendors/{vendorId}/active", Boolean.class, vendorId);
        if (isActive == null || !isActive) {
            throw new BusinessException("VENDOR_INACTIVE", "Vendor account is not active");
        }

        if (productRepository.existsBySku(request.getSku())) {
            throw new BusinessException("DUPLICATE_SKU", "SKU already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id",
                        request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .slug(generateSlug(request.getName()))
                .sku(request.getSku())
                .shortDescription(request.getShortDescription())
                .description(request.getDescription())
                .basePrice(Money.of(request.getBasePrice(), request.getCurrency()))
                .salePrice(request.getSalePrice() != null ?
                        Money.of(request.getSalePrice(), request.getCurrency()) : null)
                .category(category)
                .status(ProductStatus.PENDING_APPROVAL)
                .minOrderQuantity(request.getMinOrderQuantity() != null ?
                        request.getMinOrderQuantity() : 1)
                .maxOrderQuantity(request.getMaxOrderQuantity())
                .weight(request.getWeight())
                .weightUnit(request.getWeightUnit())
                .build();

        product.setVendorId(vendorId);

        // Add variants
        if (request.getVariants() != null) {
            request.getVariants().forEach(variantReq -> {
                ProductVariant variant = ProductVariant.builder()
                        .sku(variantReq.getSku())
                        .name(variantReq.getName())
                        .price(Money.of(variantReq.getPrice(), request.getCurrency()))
                        .attributes(variantReq.getAttributes())
                        .build();
                product.addVariant(variant);
            });
        }

        Product saved = productRepository.save(product);

        eventPublisher.publish(new ProductCreatedEvent(
                saved.getId().toString(),
                vendorId.toString(),
                saved.getName()));

        return productMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#slug")
    public ProductDetailResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndStatus(slug, ProductStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));

        product.incrementViewCount();
        
        ProductDetailResponse response = productMapper.toDetailResponse(product);
        // Fetch vendor name via REST
        String vendorName = iamRestClient.get(
                "/api/v1/vendors/{vendorId}/name", String.class, product.getVendorId());
        response.setVendorName(vendorName != null ? vendorName : "Unknown Vendor");
        return response;
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(ProductSearchRequest searchRequest,
                                                          Pageable pageable) {
        Specification<Product> spec = Specification.where(isActive());

        if (searchRequest.getCategoryId() != null) {
            spec = spec.and(inCategory(searchRequest.getCategoryId()));
        }
        if (searchRequest.getMinPrice() != null) {
            spec = spec.and(minPrice(searchRequest.getMinPrice()));
        }
        if (searchRequest.getMaxPrice() != null) {
            spec = spec.and(maxPrice(searchRequest.getMaxPrice()));
        }
        if (searchRequest.getBrandId() != null) {
            spec = spec.and(byBrand(searchRequest.getBrandId()));
        }
        if (searchRequest.getKeyword() != null) {
            spec = spec.and(containsKeyword(searchRequest.getKeyword()));
        }
        if (searchRequest.getVendorId() != null) {
            spec = spec.and(byVendor(searchRequest.getVendorId()));
        }
        if (searchRequest.getMinRating() != null) {
            spec = spec.and(minRating(searchRequest.getMinRating()));
        }

        Page<Product> page = productRepository.findAll(spec, pageable);
        return PagedResponse.from(page.map(productMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getVendorProducts(UUID vendorId, Pageable pageable) {
        Page<Product> page = productRepository.findByVendorIdAndDeletedFalse(vendorId, pageable);
        return PagedResponse.from(page.map(productMapper::toResponse));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        Product product = findProductForCurrentVendor(productId);

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getBasePrice() != null) {
            product.setBasePrice(Money.of(request.getBasePrice(),
                    product.getBasePrice().getCurrencyCode()));
        }
        if (request.getSalePrice() != null) {
            product.setSalePrice(Money.of(request.getSalePrice(),
                    product.getBasePrice().getCurrencyCode()));
        }

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Transactional
    public void approveProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.approve();
        productRepository.save(product);

        eventPublisher.publish(new com.platform.catalog.domain.event.ProductApprovedEvent(
                productId.toString(), product.getVendorId().toString()));
    }

    private Product findProductForCurrentVendor(UUID productId) {
        UUID vendorId = SecurityUtils.getCurrentVendorId()
                .orElseThrow(() -> new BusinessException("NOT_VENDOR", "Not a vendor"));

        return productRepository.findByIdAndVendorId(productId, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    public Product findById(UUID productId){
        return productRepository.findById(productId).orElse(null);
    }
    private String generateSlug(String name) {
        String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        return slug;
    }

    // Specifications
    private Specification<Product> isActive() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("status"), ProductStatus.ACTIVE),
                cb.equal(root.get("deleted"), false)
        );
    }

    private Specification<Product> inCategory(UUID categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Product> minPrice(java.math.BigDecimal minPrice) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("basePrice").get("amount"), minPrice);
    }

    private Specification<Product> maxPrice(java.math.BigDecimal maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("basePrice").get("amount"), maxPrice);
    }

    private Specification<Product> byBrand(UUID brandId) {
        return (root, query, cb) -> cb.equal(root.get("brand").get("id"), brandId);
    }

    private Specification<Product> byVendor(UUID vendorId) {
        return (root, query, cb) -> cb.equal(root.get("vendorId"), vendorId);
    }

    private Specification<Product> containsKeyword(String keyword) {
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")
        );
    }

    private Specification<Product> minRating(java.math.BigDecimal rating) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("averageRating"), rating);
    }

    public BigDecimal getVariantPrice(UUID productId, UUID variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", "id", variantId));
        // Optional: verify variant belongs to productId
        if (!variant.getProduct().getId().equals(productId)) {
            throw new BusinessException("VARIANT_NOT_BELONG_TO_PRODUCT",
                    "Variant does not belong to the specified product");
        }
        return variant.getPrice().getAmount();
    }

    public Optional<Category> findActiveById(UUID categoryId){
        return categoryRepository.findByActiveTrue(categoryId);
    }
    public PagedResponse<ProductDetailResponse.CategoryInfo> findActiveById(Pageable pageable){
        Page<Category> categories = categoryRepository.findByActiveTrue(pageable);
        return PagedResponse.from(categories.map(productMapper::mapCategory));
    }
}
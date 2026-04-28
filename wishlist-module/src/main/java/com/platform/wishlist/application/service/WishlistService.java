package com.platform.wishlist.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.dto.PagedResponse;
import com.platform.core.exception.BusinessException;
import com.platform.core.security.SecurityUtils;
import com.platform.wishlist.application.dto.AddToWishlistRequest;
import com.platform.wishlist.application.dto.WishlistItemResponse;
import com.platform.wishlist.application.mapper.WishlistMapper;
import com.platform.wishlist.domain.model.WishlistItem;
import com.platform.wishlist.domain.repository.WishlistRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistMapper mapper;
    private final RestClientFactory restClientFactory;

    @Value("${module.catalog.url:http://localhost:8080}")
    private String catalogBaseUrl;
    private ResilientRestClient catalogRestClient;

    @PostConstruct
    public void init() {
        catalogRestClient = restClientFactory.create(catalogBaseUrl, 10);
    }

    @Transactional
    public WishlistItemResponse addItem(AddToWishlistRequest request) {
        UUID customerId = SecurityUtils.getCurrentUserId();
        if (wishlistRepository.findByCustomerIdAndProductId(customerId, request.getProductId()).isPresent()) {
            throw new BusinessException("ALREADY_IN_WISHLIST", "Product already in wishlist");
        }
        WishlistItem item = mapper.toEntity(request, customerId);
        item = wishlistRepository.save(item);
        return mapper.toResponse(item);
    }

        @Transactional(readOnly = true)
    public PagedResponse<WishlistItemResponse> getWishlist(UUID customerId, Pageable pageable) {
        Page<WishlistItem> page = wishlistRepository.findByCustomerId(customerId, pageable);
        PagedResponse<WishlistItemResponse> response = PagedResponse.from(page.map(mapper::toResponse));
        if (response.getContent() != null) {
            response.getContent().forEach(this::enrichWishlistItem);
        }
        return response;
    }

    @Transactional
    public void removeItem(UUID productId) {
        UUID customerId = SecurityUtils.getCurrentUserId();
        wishlistRepository.deleteByCustomerIdAndProductId(customerId, productId);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(UUID productId) {
        UUID customerId = SecurityUtils.getCurrentUserId();
        return wishlistRepository.findByCustomerIdAndProductId(customerId, productId).isPresent();
    }

    @Transactional(readOnly = true)
    public long getWishlistCount() {
        UUID customerId = SecurityUtils.getCurrentUserId();
        return wishlistRepository.countByCustomerId(customerId);
    }

    private void enrichWishlistItem(WishlistItemResponse item) {
        if (item != null) {
            try {
                var info = catalogRestClient.get(
                        "/api/v1/products/{productId}/info-mini", CatalogProductInfo.class, item.getProductId());
                if (info != null) {
                    item.setProductName(info.name());
                    item.setProductImage(info.imageUrl());
                }
            } catch (Exception ex) {
                log.warn("Failed to enrich wishlist item {}", item.getProductId());
            }
        }
    }
    private record CatalogProductInfo(UUID productId, String name, String slug, String imageUrl) {}
}
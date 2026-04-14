package com.platform.catalog.domain.repository;

import com.platform.catalog.domain.model.Product;
import com.platform.catalog.domain.model.ProductStatus;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends SoftDeleteRepository<Product> {

    // Basic finders
    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySlugAndStatus(String slug, ProductStatus status);

    Optional<Product> findBySku(String sku);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    // Vendor-specific
    Page<Product> findByVendorIdAndDeletedFalse(UUID vendorId, Pageable pageable);

    Optional<Product> findByIdAndVendorId(UUID id, UUID vendorId);

    // Status-based
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByStatusAndDeletedFalse(ProductStatus status, Pageable pageable);

    // Featured products
    Page<Product> findByFeaturedTrueAndStatusAndDeletedFalse(
            ProductStatus status, Pageable pageable);

    // Search specifications (custom queries)
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.deleted = false " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword,
                                  @Param("status") ProductStatus status,
                                  Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.deleted = false " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR p.basePrice.amount >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.basePrice.amount <= :maxPrice)")
    Page<Product> filterProducts(@Param("categoryId") UUID categoryId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("status") ProductStatus status,
                                 Pageable pageable);

    // Counts for vendor dashboard
    long countByVendorIdAndStatus(UUID vendorId, ProductStatus status);

    long countByVendorIdAndDeletedFalse(UUID vendorId);

    // Batch updates
    @Query("UPDATE Product p SET p.status = :newStatus WHERE p.id IN :productIds")
    void bulkUpdateStatus(@Param("productIds") List<UUID> productIds,
                          @Param("newStatus") ProductStatus newStatus);
}
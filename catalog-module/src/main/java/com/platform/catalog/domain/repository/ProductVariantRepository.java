package com.platform.catalog.domain.repository;

import com.platform.catalog.domain.model.ProductVariant;
import com.platform.core.repository.BaseRepository;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends SoftDeleteRepository<ProductVariant> {

    Optional<ProductVariant> findBySku(String sku);

    List<ProductVariant> findByProductId(UUID productId);

    List<ProductVariant> findByProductIdAndActiveTrue(UUID productId);

    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.sku = :sku")
    Optional<ProductVariant> findByProductIdAndSku(@Param("productId") UUID productId, @Param("sku") String sku);

    boolean existsBySku(String sku);
}
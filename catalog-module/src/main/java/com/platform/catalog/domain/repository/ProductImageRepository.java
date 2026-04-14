package com.platform.catalog.domain.repository;

import com.platform.core.repository.SoftDeleteRepository;
import com.platform.catalog.domain.model.ProductImage;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository
public interface ProductImageRepository extends SoftDeleteRepository<ProductImage> {
    List<ProductImage> findByProductIdOrderBySortOrderAsc(UUID productId);
}

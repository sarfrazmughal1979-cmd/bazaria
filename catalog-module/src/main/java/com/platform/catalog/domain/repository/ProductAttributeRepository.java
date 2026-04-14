package com.platform.catalog.domain.repository;

import com.platform.core.repository.SoftDeleteRepository;
import com.platform.catalog.domain.model.ProductAttribute;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends SoftDeleteRepository<ProductAttribute> {}

package com.platform.catalog.application.service;

import com.platform.core.service.AbstractCrudService;
import com.platform.catalog.domain.model.ProductVariant;
import com.platform.catalog.domain.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
@Service
public class ProductVariantCrudService extends AbstractCrudService<ProductVariant, ProductVariantRepository> {
    public ProductVariantCrudService(ProductVariantRepository repository) { super(repository, "ProductVariant"); }
}

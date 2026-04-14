package com.platform.catalog.application.service;

import com.platform.core.service.AbstractCrudService;
import com.platform.catalog.domain.model.ProductImage;
import com.platform.catalog.domain.repository.ProductImageRepository;
import org.springframework.stereotype.Service;
@Service
public class ProductImageCrudService extends AbstractCrudService<ProductImage, ProductImageRepository> {
    public ProductImageCrudService(ProductImageRepository repository) { super(repository, "ProductImage"); }
}

package com.platform.catalog.application.service;
import com.platform.core.service.AbstractCrudService;
import com.platform.catalog.domain.model.ProductAttribute;
import com.platform.catalog.domain.repository.ProductAttributeRepository;
import org.springframework.stereotype.Service;
@Service
public class ProductAttributeCrudService extends AbstractCrudService<ProductAttribute, ProductAttributeRepository> {
    public ProductAttributeCrudService(ProductAttributeRepository repository) { super(repository, "ProductAttribute"); }
}

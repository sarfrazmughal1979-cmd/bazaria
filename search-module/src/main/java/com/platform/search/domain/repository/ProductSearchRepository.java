package com.platform.search.domain.repository;

import com.platform.search.domain.model.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    Page<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
    Page<ProductDocument> findByCategoryId(String categoryId, Pageable pageable);
    Page<ProductDocument> findByCategoryIdAndEffectivePriceBetween(String categoryId, Double minPrice, Double maxPrice, Pageable pageable);
    List<ProductDocument> findByVendorId(String vendorId);
}
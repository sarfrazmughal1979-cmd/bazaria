package com.platform.catalog.domain.repository;

import com.platform.catalog.domain.model.Category;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends SoftDeleteRepository<Category> {

    // Basic finders
    Optional<Category> findBySlug(String slug);

    Optional<Category> findBySlugAndActiveTrue(String slug);
    Optional<Category> findByActiveTrue(UUID id);

    boolean existsBySlug(String slug);

    // Hierarchical queries
    List<Category> findByParentIdAndActiveTrueOrderBySortOrderAsc(UUID parentId);

    List<Category> findByParentIsNullAndActiveTrueOrderBySortOrderAsc();

    @Query("SELECT c FROM Category c WHERE c.path LIKE CONCAT(:path, '%') AND c.active = true")
    List<Category> findByPathStartingWith(@Param("path") String path);

    // Active categories
    Page<Category> findByActiveTrue(Pageable pageable);

    List<Category> findByActiveTrueOrderBySortOrderAsc();

    // Search
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Category> findByNameContainingIgnoreCase(@Param("name") String name);

    // Count products in category (including subcategories - uses path)
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.path LIKE CONCAT(:path, '%') AND p.status = 'ACTIVE'")
    long countActiveProductsInCategoryTree(@Param("path") String path);

    // Update operations
    @Query("UPDATE Category c SET c.active = :active WHERE c.id = :id")
    void setActiveStatus(@Param("id") UUID id, @Param("active") boolean active);
}
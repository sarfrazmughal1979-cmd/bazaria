package com.platform.core.repository;

import com.platform.core.domain.AuditableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface SoftDeleteRepository<T extends AuditableEntity> extends BaseRepository<T> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<T> findAllActive();

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    Page<T> findAllActive(Pageable pageable);

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deleted = false")
    Optional<T> findActiveById(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void softDelete(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = false, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void restore(@Param("id") UUID id);

    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = false")
    long countActive();
}
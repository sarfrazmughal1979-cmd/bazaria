package com.platform.core.service;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.dto.PagedResponse;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.core.repository.SoftDeleteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Generic base service for CRUD operations using SoftDeleteRepository.
 *
 * @param <E> Entity type (extends AuditableEntity)
 * @param <R> Repository type (extends SoftDeleteRepository<E>)
 */
public abstract class AbstractCrudService<E extends AuditableEntity, R extends SoftDeleteRepository<E>> {

    protected final R repository;
    protected final String entityName;


    protected AbstractCrudService(R repository, String entityName){
        this.repository = repository;
        this.entityName = entityName;
    }
    @Transactional(readOnly = true)
    public PagedResponse<E> findAll(Pageable pageable) {
        Page<E> page = repository.findAll(
                (Specification<E>) (root, query, cb) -> cb.equal(root.get("deleted"), false),
                pageable
        );
        return PagedResponse.from(page);
    }

    public E findById(UUID id) {
        return repository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName, "id", id));
    }

    @Transactional
    public E save(E entity) {
        return repository.save(entity);
    }
    @Transactional
    public E update(UUID id, E updatedEntity) {
        E existing = findById(id);
        // Copy updatable fields (override in subclass for custom logic)
        copyUpdatableFields(updatedEntity, existing);
        return repository.save(existing);
    }
    @Transactional
    public void delete(UUID id) {
        repository.softDelete(id);
    }

    /**
     * Hook for subclasses to copy fields from source to target.
     * Override this to customize which fields are updated.
     */
    protected void copyUpdatableFields(E source, E target) {
        // Default: do nothing – subclasses should override
    }
}
package com.platform.core.api;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.core.repository.SoftDeleteRepository;
import com.platform.core.service.AbstractCrudService;
import jakarta.validation.Valid;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.function.Function;

/**
 * Generic base controller for CRUD operations.
 * Subclasses must provide mapper functions to convert between entities and DTOs.
 *
 * @param <E> Entity type
 */
public abstract class AbstractCrudController<E extends AuditableEntity, R extends SoftDeleteRepository<E>>
        extends AbstractBaseController {

    protected final AbstractCrudService<E, R> service;

    protected AbstractCrudController(AbstractCrudService<E, R> service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<E>>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        PagedResponse<E> page = service.findAll(pageable);
        return success(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<E>> getById(@PathVariable UUID id) {
        E dto = service.findById(id);
        return success(dto);
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<E>> create(@Valid @RequestBody E request) {
        E response = service.save(request);
        return created(response, getCreateSuccessMessage());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<E>> update(@PathVariable UUID id, @Valid @RequestBody E request) {
        E response = service.update(id, request);
        return success(response, getUpdateSuccessMessage());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return success(getDeleteSuccessMessage());
    }

    // ============================================================
    // Overridable hooks for custom messages and entity names
    // ============================================================

    protected String getEntityName() {
        return "Entity";
    }

    protected String getCreateSuccessMessage() {
        return getEntityName() + " created successfully";
    }

    protected String getUpdateSuccessMessage() {
        return getEntityName() + " updated successfully";
    }

    protected String getDeleteSuccessMessage() {
        return getEntityName() + " deleted successfully";
    }
}
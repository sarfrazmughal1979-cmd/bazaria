package com.platform.iam.domain.repository;

import com.platform.core.repository.SoftDeleteRepository;
import com.platform.iam.domain.model.Vendor;
import com.platform.iam.domain.model.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendorRepository extends SoftDeleteRepository<Vendor> {

    Optional<Vendor> findByUserId(UUID userId);

    Optional<Vendor> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByUserId(UUID userId);

    Page<Vendor> findByStatus(VendorStatus status, Pageable pageable);

    Page<Vendor> findByDeletedFalse(Pageable pageable);
}
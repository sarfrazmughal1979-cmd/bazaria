package com.platform.settlement.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.settlement.domain.model.VendorAccount;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendorAccountRepository extends BaseRepository<VendorAccount> {
    Optional<VendorAccount> findByVendorId(UUID vendorId);
    boolean existsByVendorId(UUID vendorId);
}
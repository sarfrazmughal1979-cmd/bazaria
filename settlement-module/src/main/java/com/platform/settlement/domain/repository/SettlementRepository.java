package com.platform.settlement.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.settlement.domain.model.Settlement;
import com.platform.settlement.domain.model.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementRepository extends BaseRepository<Settlement> {
    Page<Settlement> findByVendorId(UUID vendorId, Pageable pageable);
    List<Settlement> findByVendorIdAndStatus(UUID vendorId, SettlementStatus status);
    List<Settlement> findByStatusAndPeriodEndBefore(SettlementStatus status, Instant date);
}
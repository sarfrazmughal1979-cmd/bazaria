package com.platform.settlement.domain.repository;
import com.platform.core.repository.SoftDeleteRepository;
import com.platform.settlement.domain.model.SettlementItem;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementItemRepository extends SoftDeleteRepository<SettlementItem> {}

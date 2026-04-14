package com.platform.payment.domain.repository;
import com.platform.core.repository.SoftDeleteRepository;
import com.platform.payment.domain.model.Refund;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends SoftDeleteRepository<Refund> {}

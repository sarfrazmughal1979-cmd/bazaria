package com.platform.order.domain.repository;
import com.platform.core.repository.SoftDeleteRepository;
import com.platform.order.domain.model.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface ReturnRequestRepository extends SoftDeleteRepository<ReturnRequest> {
    Page<ReturnRequest> findByCustomerId(UUID customerId, Pageable pageable);
}

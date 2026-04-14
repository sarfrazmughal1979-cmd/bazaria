package com.platform.payment.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.payment.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction> {

    List<Transaction> findByPaymentIdOrderByCreatedAtAsc(UUID paymentId);

    Page<Transaction> findByPaymentId(UUID paymentId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.type = :type AND t.createdAt >= :since")
    List<Transaction> findByTypeAndCreatedAfter(@Param("type") String type, @Param("since") Instant since);
}
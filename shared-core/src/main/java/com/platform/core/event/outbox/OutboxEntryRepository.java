package com.platform.core.event.outbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEntryRepository extends JpaRepository<OutboxEntry, UUID> {
    List<OutboxEntry> findByProcessedFalseAndOccurredOnBeforeOrderByOccurredOnAsc(Instant before, Pageable pageable);
    Page<OutboxEntry> findByProcessedFalseAndOccurredOnBefore(Instant before, Pageable pageable);
}
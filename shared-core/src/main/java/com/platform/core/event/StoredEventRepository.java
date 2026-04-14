package com.platform.core.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface StoredEventRepository extends JpaRepository<StoredEvent, UUID> {

    List<StoredEvent> findByAggregateTypeAndAggregateId(String aggregateType, String aggregateId);

    List<StoredEvent> findByProcessedFalseAndOccurredOnBefore(Instant before);

    List<StoredEvent> findByEventTypeAndOccurredOnBetween(String eventType, Instant from, Instant to);
}
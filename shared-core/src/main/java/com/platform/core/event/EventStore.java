package com.platform.core.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStore {

    private final StoredEventRepository storedEventRepository;
    private final ObjectMapper objectMapper;

    public void store(DomainEvent event) {
        try {
            StoredEvent storedEvent = StoredEvent.builder()
                    .eventId(event.getEventId())
                    .eventType(event.getEventType())
                    .aggregateType(event.getAggregateType())
                    .aggregateId(event.getAggregateId())
                    .payload(objectMapper.writeValueAsString(event))
                    .occurredOn(event.getOccurredOn())
                    .processed(false)
                    .build();

            storedEventRepository.save(storedEvent);
            log.debug("Stored event: {} for aggregate: {}",
                    event.getEventType(), event.getAggregateId());
        } catch (Exception e) {
            log.error("Failed to store domain event: {}", event.getEventType(), e);
        }
    }
}
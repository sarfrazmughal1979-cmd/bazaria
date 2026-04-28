package com.platform.core.event.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.core.event.DomainEvent;
import com.platform.core.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher implements DomainEventPublisher {

    private final OutboxEntryRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(DomainEvent event) {
        try {
            OutboxEntry entry = OutboxEntry.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(event.getAggregateType())
                    .aggregateId(event.getAggregateId())
                    .eventType(event.getClass().getName())
                    .payload(objectMapper.writeValueAsString(event))
                    .occurredOn(event.getOccurredOn())
                    .processed(false)
                    .build();
            outboxRepository.save(entry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write outbox entry", e);
        }
    }

    @Override
    public void publishAsync(DomainEvent event) {
        // Asynchronous version still writes to outbox; relay will dispatch later
        publish(event);
    }
}
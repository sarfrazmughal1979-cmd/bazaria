package com.platform.core.event.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.core.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxEntryRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher springEventPublisher;

    @Scheduled(fixedDelayString = "${outbox.relay.delay:5000}")
    @Transactional
    public void processOutbox() {
        PageRequest page = PageRequest.of(0, 100);
        var entries = outboxRepository.findByProcessedFalseAndOccurredOnBefore(Instant.now(), page);
        for (OutboxEntry entry : entries.getContent()) {
            try {
                DomainEvent event = objectMapper.readValue(entry.getPayload(),
                        (Class<DomainEvent>) Class.forName(entry.getEventType()));
                springEventPublisher.publishEvent(event);
                entry.setProcessed(true);
                outboxRepository.save(entry);
                log.debug("Dispatched outbox event: {}", entry.getId());
            } catch (Exception e) {
                log.error("Failed to dispatch outbox event {}: {}", entry.getId(), e.getMessage());
                // Leave as unprocessed; will retry on next schedule
            }
        }
    }
}
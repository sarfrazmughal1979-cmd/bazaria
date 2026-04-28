package com.platform.core.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher  { //implements DomainEventPublisher

    private final ApplicationEventPublisher applicationEventPublisher;
    private final EventStore eventStore;

//    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing domain event: {} [{}]", event.getEventType(), event.getEventId());
        eventStore.store(event);
        applicationEventPublisher.publishEvent(event);
    }

    @Async("domainEventExecutor")
//    @Override
    public void publishAsync(DomainEvent event) {
        publish(event);
    }
}
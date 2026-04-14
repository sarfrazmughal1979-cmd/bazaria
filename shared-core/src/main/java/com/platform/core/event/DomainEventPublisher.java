package com.platform.core.event;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

    void publishAsync(DomainEvent event);
}
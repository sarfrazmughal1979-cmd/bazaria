package com.platform.core.event;
import lombok.NoArgsConstructor;

import lombok.Getter;

@Getter
public abstract class IntegrationEvent extends DomainEvent {

    private  String source;

    protected IntegrationEvent(String source) {
        super();
        this.source = source;
    }
}
package com.platform.common.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class UserLoggedInEvent extends DomainEvent {
    private final String userId;
    private final String sessionId;

    public UserLoggedInEvent(String userId, String sessionId) {
        super();
        this.userId = userId;
        this.sessionId = sessionId;
    }

    @Override public String getAggregateId() { return userId; }
    @Override public String getAggregateType() { return "User"; }
}
package com.platform.common.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class UserLoggedInEvent extends DomainEvent {
    private  String userId;
    private  String sessionId;

    public UserLoggedInEvent(String userId, String sessionId) {
        super();
        this.userId = userId;
        this.sessionId = sessionId;
    }

    @Override public String getAggregateId() { return userId; }
    @Override public String getAggregateType() { return "User"; }
}
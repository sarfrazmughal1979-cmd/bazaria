package com.platform.common.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoggedInEvent extends DomainEvent {
    private  String userId;
    private  String sessionId;

	@JsonCreator
    public UserLoggedInEvent(@JsonProperty("userId") String userId, @JsonProperty("sessionId") String sessionId) {
        super();
        this.userId = userId;
        this.sessionId = sessionId;
    }

    @Override public String getAggregateId() { return userId; }
    @Override public String getAggregateType() { return "User"; }
}
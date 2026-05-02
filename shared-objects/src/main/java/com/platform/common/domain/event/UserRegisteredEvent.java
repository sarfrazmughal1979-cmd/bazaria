package com.platform.common.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {

    private final String userId;
    private final String email;
    private final String role;

    public UserRegisteredEvent(String userId, String email, String role) {
        super();
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }

    @Override
    public String getAggregateType() {
        return "User";
    }
}
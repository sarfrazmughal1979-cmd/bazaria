package com.platform.common.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class UserRegisteredEvent extends DomainEvent {

    private  String userId;
    private  String email;
    private  String role;

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
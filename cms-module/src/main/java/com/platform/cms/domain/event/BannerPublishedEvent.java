package com.platform.cms.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class BannerPublishedEvent extends DomainEvent {
    private final String bannerId;
    private final String position;

    public BannerPublishedEvent(String bannerId, String position) {
        super();
        this.bannerId = bannerId;
        this.position = position;
    }

    @Override
    public String getAggregateId() {
        return bannerId;
    }

    @Override
    public String getAggregateType() {
        return "Banner";
    }
}
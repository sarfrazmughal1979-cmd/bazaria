package com.platform.cms.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class BannerPublishedEvent extends DomainEvent {
    private  String bannerId;
    private  String position;

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
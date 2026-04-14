package com.platform.cms.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class PageUpdatedEvent extends DomainEvent {
    private final String pageId;
    private final String slug;

    public PageUpdatedEvent(String pageId, String slug) {
        super();
        this.pageId = pageId;
        this.slug = slug;
    }

    @Override
    public String getAggregateId() {
        return pageId;
    }

    @Override
    public String getAggregateType() {
        return "Page";
    }
}
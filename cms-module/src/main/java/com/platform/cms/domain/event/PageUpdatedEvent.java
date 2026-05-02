package com.platform.cms.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class PageUpdatedEvent extends DomainEvent {
    private  String pageId;
    private  String slug;

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
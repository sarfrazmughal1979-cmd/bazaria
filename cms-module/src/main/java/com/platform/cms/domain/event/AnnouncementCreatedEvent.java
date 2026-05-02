package com.platform.cms.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {
    private final String announcementId;
    private final String title;

    public AnnouncementCreatedEvent(String announcementId, String title) {
        super();
        this.announcementId = announcementId;
        this.title = title;
    }

    @Override
    public String getAggregateId() {
        return announcementId;
    }

    @Override
    public String getAggregateType() {
        return "Announcement";
    }
}
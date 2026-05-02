package com.platform.cms.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class AnnouncementCreatedEvent extends DomainEvent {
    private  String announcementId;
    private  String title;

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
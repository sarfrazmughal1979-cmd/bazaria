package com.platform.cms.domain.repository;

import com.platform.cms.domain.model.Announcement;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AnnouncementRepository extends SoftDeleteRepository<Announcement> {

    @Query("SELECT a FROM Announcement a WHERE a.active = true AND " +
           "(a.startDate IS NULL OR a.startDate <= :now) AND " +
           "(a.endDate IS NULL OR a.endDate >= :now) " +
           "ORDER BY a.priority DESC, a.createdAt DESC")
    List<Announcement> findActiveAnnouncements(@Param("now") Instant now);

    @Query("SELECT a FROM Announcement a WHERE a.active = true AND " +
           "(a.startDate IS NULL OR a.startDate <= :now) AND " +
           "(a.endDate IS NULL OR a.endDate >= :now) AND " +
           "(a.targetPages = 'ALL' OR a.targetPages LIKE CONCAT('%', :page, '%')) " +
           "ORDER BY a.priority DESC, a.createdAt DESC")
    List<Announcement> findActiveAnnouncementsForPage(@Param("now") Instant now,
                                                       @Param("page") String page);
}
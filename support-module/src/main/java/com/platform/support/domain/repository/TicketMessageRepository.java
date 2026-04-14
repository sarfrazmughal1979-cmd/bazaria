package com.platform.support.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.support.domain.model.TicketMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketMessageRepository extends BaseRepository<TicketMessage> {

    Page<TicketMessage> findByTicketIdOrderByCreatedAtAsc(UUID ticketId, Pageable pageable);

    List<TicketMessage> findByTicketIdAndInternalFalseOrderByCreatedAtAsc(UUID ticketId);

    @Modifying
    @Query("UPDATE TicketMessage m SET m.readAt = CURRENT_TIMESTAMP WHERE m.ticket.id = :ticketId AND m.senderId != :readerId")
    void markMessagesAsRead(@Param("ticketId") UUID ticketId, @Param("readerId") UUID readerId);

    long countByTicketIdAndInternalFalseAndReadAtIsNullAndSenderIdNot(UUID ticketId, UUID readerId);
}
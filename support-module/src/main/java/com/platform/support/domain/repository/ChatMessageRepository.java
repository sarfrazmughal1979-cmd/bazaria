package com.platform.support.domain.repository;

import com.platform.support.domain.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    // Find messages by room ID, ordered by timestamp (descending for latest first)
    List<ChatMessage> findByRoomIdOrderByTimestampDesc(String roomId);

    // Paginated version
    Page<ChatMessage> findByRoomIdOrderByTimestampDesc(String roomId, Pageable pageable);

    // Find unread messages for a recipient
    List<ChatMessage> findByRecipientIdAndIsReadFalse(UUID recipientId);

}
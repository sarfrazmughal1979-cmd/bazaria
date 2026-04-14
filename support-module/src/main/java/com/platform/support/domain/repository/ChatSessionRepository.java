package com.platform.support.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.support.domain.model.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends BaseRepository<ChatSession> {

    Optional<ChatSession> findByCustomerIdAndStatus(UUID customerId, String status);

    Optional<ChatSession> findByVendorIdAndStatus(UUID vendorId, String status);

    List<ChatSession> findByAgentIdAndStatus(UUID agentId, String status);

    @Query("SELECT cs FROM ChatSession cs WHERE (cs.customerId = :userId OR cs.vendorId = :userId OR cs.agentId = :userId) AND cs.status = 'ACTIVE'")
    Optional<ChatSession> findActiveSessionForUser(@Param("userId") UUID userId);
}
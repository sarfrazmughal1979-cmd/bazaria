package com.platform.support.domain.repository;

import com.platform.core.repository.SoftDeleteRepository;
import com.platform.support.domain.model.Ticket;
import com.platform.support.domain.model.TicketPriority;
import com.platform.support.domain.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends SoftDeleteRepository<Ticket> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    Page<Ticket> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Ticket> findByVendorId(UUID vendorId, Pageable pageable);

    Page<Ticket> findByAssignedTo(UUID assignedTo, Pageable pageable);

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.status IN :statuses AND t.priority = :priority")
    Page<Ticket> findByStatusesAndPriority(@Param("statuses") List<TicketStatus> statuses,
                                           @Param("priority") TicketPriority priority,
                                           Pageable pageable);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status IN :openStatuses")
    long countOpenTickets(@Param("openStatuses") List<TicketStatus> openStatuses);

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (t.first_response_at - t.created_at))) " +
            "FROM support_tickets t " +
            "WHERE t.first_response_at IS NOT NULL AND t.created_at >= :since",
            nativeQuery = true)
    Double getAverageFirstResponseTime(@Param("since") Instant since);

    @Query("SELECT AVG(t.customerRating) FROM Ticket t WHERE t.customerRating IS NOT NULL")
    Double getAverageCustomerRating();
}
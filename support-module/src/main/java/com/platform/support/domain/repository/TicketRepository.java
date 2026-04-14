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

    @Query("SELECT t FROM SupportTicket t WHERE t.status IN :statuses AND t.priority = :priority")
    Page<Ticket> findByStatusesAndPriority(@Param("statuses") List<TicketStatus> statuses,
                                           @Param("priority") TicketPriority priority,
                                           Pageable pageable);

    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.status IN :openStatuses")
    long countOpenTickets(@Param("openStatuses") List<TicketStatus> openStatuses);

    @Query("SELECT AVG(EXTRACT(EPOCH FROM (t.firstResponseAt - t.createdAt))) FROM SupportTicket t " +
           "WHERE t.firstResponseAt IS NOT NULL AND t.createdAt >= :since")
    Double getAverageFirstResponseTime(@Param("since") Instant since);

    @Query("SELECT AVG(t.customerRating) FROM SupportTicket t WHERE t.customerRating IS NOT NULL")
    Double getAverageCustomerRating();
}
package com.platform.inventory.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.core.repository.SoftDeleteRepository;
import com.platform.inventory.domain.model.StockReservation;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockReservationRepository extends SoftDeleteRepository<StockReservation> {

    List<StockReservation> findByStatusAndExpiresAtBefore(String status, Instant expiresAt);
}
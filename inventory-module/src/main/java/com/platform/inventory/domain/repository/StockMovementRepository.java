package com.platform.inventory.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.core.repository.SoftDeleteRepository;
import com.platform.inventory.domain.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends SoftDeleteRepository<StockMovement> {

    List<StockMovement> findByInventoryItemIdOrderByCreatedAtDesc(UUID inventoryItemId);

    Page<StockMovement> findByInventoryItemId(UUID inventoryItemId, Pageable pageable);

    List<StockMovement> findByInventoryItemIdAndCreatedAtBetween(
            UUID inventoryItemId, Instant start, Instant end);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.inventoryItem.productId = :productId ORDER BY sm.createdAt DESC")
    List<StockMovement> findByProductId(@Param("productId") UUID productId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.referenceId = :referenceId ORDER BY sm.createdAt DESC")
    List<StockMovement> findByReferenceId(@Param("referenceId") UUID referenceId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.type = :type AND sm.createdAt >= :since")
    List<StockMovement> findByTypeAndCreatedAfter(@Param("type") String type, @Param("since") Instant since);
}
package com.platform.inventory.domain.repository;

import com.platform.core.repository.SoftDeleteRepository;
import com.platform.inventory.domain.model.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends SoftDeleteRepository<InventoryItem> {

    Optional<InventoryItem> findByProductIdAndVariantIdAndWarehouseId(
            UUID productId, UUID variantId, UUID warehouseId);

    default Optional<InventoryItem> findByProductIdAndVariantId(UUID productId, UUID variantId) {
        return findByProductIdAndVariantIdAndWarehouseId(productId, variantId, null);
    }

    List<InventoryItem> findByProductId(UUID productId);

    List<InventoryItem> findByVariantId(UUID variantId);

    Page<InventoryItem> findByWarehouseId(UUID warehouseId, Pageable pageable);

    @Query("SELECT i FROM InventoryItem i WHERE i.quantity - i.reservedQuantity <= i.reorderPoint AND i.active = true")
    List<InventoryItem> findItemsNeedingReorder();

    @Query("SELECT i FROM InventoryItem i WHERE i.productId IN :productIds")
    List<InventoryItem> findByProductIds(@Param("productIds") List<UUID> productIds);

    @Modifying
    @Query("UPDATE InventoryItem i SET i.quantity = i.quantity + :delta WHERE i.id = :id")
    int incrementQuantity(@Param("id") UUID id, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE InventoryItem i SET i.reservedQuantity = i.reservedQuantity + :delta WHERE i.id = :id")
    int incrementReservedQuantity(@Param("id") UUID id, @Param("delta") int delta);

    boolean existsByProductIdAndVariantId(UUID productId, UUID variantId);
}
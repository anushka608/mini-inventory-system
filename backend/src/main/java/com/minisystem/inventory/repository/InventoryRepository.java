package com.minisystem.inventory.repository;

import com.minisystem.inventory.entity.Inventory;
import com.minisystem.inventory.enums.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    Optional<Inventory> findBySkuIgnoreCaseAndMrpAndBatchNoIgnoreCase(
            String sku, Double mrp, String batchNo);

//    List<Inventory> findBySkuIgnoreCaseAndMrpAndStatusAndExpiryDateAfter(
//            String sku, Double mrp, InventoryStatus status, LocalDate date);

    List<Inventory>findBySkuIgnoreCaseAndMrpAndStatusAndExpiryDateGreaterThanEqual(String sku, Double mrp, InventoryStatus inventoryStatus, LocalDate now);

//    List<Inventory> findBySku(String sku);
//
//    Optional<Inventory> findBySkuAndBatchNo(String sku, String batchNo);

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.status = 'EXPIRED'
        WHERE i.expiryDate < CURRENT_TIMESTAMP
        AND i.status <> 'EXPIRED'
    """)
    int markExpiredItems();
}
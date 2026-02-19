package com.minisystem.inventory.repository;

import com.minisystem.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    Optional<Inventory> findBySkuAndMrpAndBatchNo(String sku, Double mrp, String batchNo);

    List<Inventory> findBySkuAndMrpAndStatusAndExpiryDateAfter(
            String sku, Double mrp, String status, LocalDate date);

//    List<Inventory> findBySku(String sku);
//
//    Optional<Inventory> findBySkuAndBatchNo(String sku, String batchNo);
}
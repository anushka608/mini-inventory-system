package com.minisystem.inventory.dto;

import com.minisystem.inventory.enums.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchUpdate {
    private String batchNo;
    @jakarta.validation.constraints.Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    private String sku;
    private Double mrp;
    // generateKey( sku|batchNo|mrp)
    // for ADD
    private InventoryStatus status;
    private LocalDate expiryDate;
}
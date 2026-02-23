package com.minisystem.inventory.dto;

import com.minisystem.inventory.enums.InventoryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InventoryDto {

    private String sku;
    private Double mrp;
    private String batchNo;
    private Integer quantity;
    private InventoryStatus status;
    private LocalDate expiryDate;
}
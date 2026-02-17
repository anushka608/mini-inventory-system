package com.minisystem.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventorySummary {
    private String sku;
    private Integer goodQty;
    private Integer damagedQty;
    private Integer expiredQty;
}
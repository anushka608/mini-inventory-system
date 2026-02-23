package com.minisystem.inventory.dto;

import lombok.Data;

@Data
public class InventoryQuery {
    private String sku;
    private Double mrp;
}

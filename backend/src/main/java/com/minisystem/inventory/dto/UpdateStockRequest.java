package com.minisystem.inventory.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateStockRequest {
    private String sku;
    private List<BatchUpdate> batches;
}
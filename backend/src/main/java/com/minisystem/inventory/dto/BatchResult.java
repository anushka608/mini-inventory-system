package com.minisystem.inventory.dto;

import com.minisystem.inventory.enums.InventoryStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchResult {

    private String batchNo;
    private Integer previousQty;
    private Integer updatedQty;
    private InventoryStatus status;
}
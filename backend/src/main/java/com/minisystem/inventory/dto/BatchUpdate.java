package com.minisystem.inventory.dto;

import lombok.Data;

@Data
public class BatchUpdate {
    private String batchNo;
    private Double mrp;
    private Integer quantity;
}
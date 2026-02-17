package com.minisystem.inventory.dto;

import lombok.Data;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

@Data
public class AddStockRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String batchNo;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    @Positive
    private Double mrp;

    private LocalDate expiryDate;

    @NotBlank
    private String status;
}
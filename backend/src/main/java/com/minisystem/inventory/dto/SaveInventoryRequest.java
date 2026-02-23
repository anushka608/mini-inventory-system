package com.minisystem.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SaveInventoryRequest {

    @NotBlank
    private String operation;   // ADD / DEDUCT

    //bulk multi-sku support
//    private List<SkuUpdate> items;
    private List<BatchUpdate> items;
//    map<String ,List<BatchUpdate>>
}
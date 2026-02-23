package com.minisystem.inventory.service;

import com.minisystem.inventory.dto.*;

import java.util.List;
import java.util.Map;

public interface InventoryService {

    List<InventoryDto> getAll();

    List<InventoryDto> getAvailable(String sku, Double mrp);

    Map<String, List<InventoryDto>> getAvailableBulk(List<InventoryQuery> queries);
    List<InventorySummary> getSummary();

    SaveInventoryResponse save(SaveInventoryRequest req);
}
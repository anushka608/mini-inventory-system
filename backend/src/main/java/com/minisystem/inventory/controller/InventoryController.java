package com.minisystem.inventory.controller;

import com.minisystem.inventory.dto.*;
import com.minisystem.inventory.entity.Inventory;
import com.minisystem.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping("/add")
    public Inventory add(@Valid @RequestBody AddStockRequest req){
        return service.addStock(req);
    }

    @GetMapping("/available")
    public List<Inventory> available(
            @RequestParam String sku,
            @RequestParam Double mrp){
        return service.getAvailable(sku,mrp);
    }

    @PutMapping("/update")
    public String update(@RequestBody UpdateStockRequest req){
        service.updateStock(req);
        return "Stock updated";
    }

    @GetMapping("/all")
    public List<Inventory> all(){
        return service.getAll();
    }

    @GetMapping("/summary")
    public List<InventorySummary> summary(){
        return service.getSummary();
    }
}
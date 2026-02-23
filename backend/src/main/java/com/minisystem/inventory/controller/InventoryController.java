package com.minisystem.inventory.controller;

import com.minisystem.inventory.dto.*;
import com.minisystem.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

//    @PostMapping("/add")
//    public Inventory add(@Valid @RequestBody AddStockRequest req){
//        return service.addStock(req);
//    }

//    @GetMapping("/available")
//    public ResponseEntity<ApiResponse<List<InventoryDto>>> available(
//            @RequestParam String sku,
//            @RequestParam Double mrp){
//
//        List<InventoryDto> result = service.getAvailable(sku, mrp);
//
//        return ResponseEntity.ok(
//                ApiResponse.<List<InventoryDto>>builder()
//                        .success(true)
//                        .message("Available inventory fetched")
//                        .data(result)
//                        .build()
//        );
//    }

    /*
     * BULK AVAILABLE INVENTORY
     * POST /api/inventory/available
     * Accepts list of queries and returns matching inventory grouped in a map
     */
    @PostMapping("/available")
    public ResponseEntity<ApiResponse<Map<String,List<InventoryDto>>>> bulkAvailable(
            @Valid @RequestBody List<InventoryQuery> queries){

        return ResponseEntity.ok(
                ApiResponse.<Map<String,List<InventoryDto>>>builder()
                        .success(true)
                        .message("Bulk inventory fetched")
                        .data(service.getAvailableBulk(queries))
                        .build()
        );
    }

//    @PutMapping("/update")
//    public ResponseEntity<String> update(@RequestBody UpdateStockRequest req){
//        service.updateStock(req);
//        return ResponseEntity.ok("Stock updated");
//    }

    /*
     * GET ALL INVENTORY
     * GET /api/inventory/all
     * Returns list of all inventory records
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> all(){

        List<InventoryDto> result = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<InventoryDto>>builder()
                        .success(true)
                        .message("All inventory fetched")
                        .data(result)
                        .build()
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<InventorySummary>>> summary(){

        List<InventorySummary> result = service.getSummary();

        return ResponseEntity.ok(
                ApiResponse.<List<InventorySummary>>builder()
                        .success(true)
                        .message("Inventory summary fetched")
                        .data(result)
                        .build()
        );
    }

    /*
     * SAVE INVENTORY OPERATION
     * POST /api/inventory/save
     *
     * Used for:
     *  - Adding stock (operation = ADD)
     *  - Deducting stock (operation = DEDUCT)
     *
     * Request body -> SaveInventoryRequest
     * Response -> SaveInventoryResponse
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<SaveInventoryResponse>> save(
            @Valid @RequestBody SaveInventoryRequest req){

        SaveInventoryResponse response = service.save(req);

        return ResponseEntity.ok(
                ApiResponse.<SaveInventoryResponse>builder()
                        .success(true)
                        // Dynamic message based on operation type
                        .message(
                                "ADD".equalsIgnoreCase(req.getOperation()) ? "Stock Added Successfully" : "Stock Deducted Successfully"
                        )
                        .data(response)
                        .build()
        );
    }
}


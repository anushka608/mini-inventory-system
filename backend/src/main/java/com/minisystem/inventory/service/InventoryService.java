package com.minisystem.inventory.service;

import com.minisystem.inventory.dto.*;
import com.minisystem.inventory.entity.Inventory;
import com.minisystem.inventory.exception.InsufficientStockException;
import com.minisystem.inventory.exception.ResourceNotFoundException;
import com.minisystem.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repo;
    private final InventoryEventPublisher publisher;

    // ADD OR UPDATE STOCK
    @Transactional
    public Inventory addStock(AddStockRequest req){

        Inventory inv = repo.findBySkuAndMrpAndBatchNo(
                        req.getSku(), req.getMrp(), req.getBatchNo())
                .orElse(null);

        if(inv!=null){
            inv.setQuantity(inv.getQuantity()+req.getQuantity());
        }else{
            inv = Inventory.builder()
                    .sku(req.getSku())
                    .mrp(req.getMrp())
                    .batchNo(req.getBatchNo())
                    .quantity(req.getQuantity())
                    .status(req.getStatus())
                    .expiryDate(req.getExpiryDate())
                    .build();
        }

        Inventory saved = repo.save(inv);
        publisher.publishUpdate(saved);
        return saved;
    }

    // GET AVAILABLE STOCK
    public List<Inventory> getAvailable(String sku, Double mrp){
        return repo.findBySkuAndMrpAndStatusAndExpiryDateAfter(
                sku, mrp, "GOOD", LocalDate.now());
    }

    // UPDATE MULTIPLE BATCHES
    @Transactional
    public void updateStock(UpdateStockRequest req){

        for(BatchUpdate b : req.getBatches()){

            Inventory inv = repo.findBySkuAndMrpAndBatchNo(
                    req.getSku(), req.getMrp(), b.getBatchNo()
            ).orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

            if(inv.getQuantity() < b.getQuantity()){
                throw new InsufficientStockException("Insufficient quantity");
            }

            inv.setQuantity(inv.getQuantity() - b.getQuantity());

            Inventory saved = repo.save(inv);

            publisher.publishUpdate(saved);
        }
    }

    public List<Inventory> getAll(){
        return repo.findAll();
    }

    public List<InventorySummary> getSummary(){

        List<Inventory> list = repo.findAll();

        return list.stream()
                .collect(Collectors.groupingBy(Inventory::getSku))
                .entrySet().stream()
                .map(e -> {

                    int good = e.getValue().stream()
                            .filter(i -> "GOOD".equals(i.getStatus())
                                    && (i.getExpiryDate()==null ||
                                    i.getExpiryDate().isAfter(LocalDate.now())))
                            .mapToInt(Inventory::getQuantity).sum();

                    int damaged = e.getValue().stream()
                            .filter(i -> "DAMAGED".equals(i.getStatus()))
                            .mapToInt(Inventory::getQuantity).sum();

                    int expired = e.getValue().stream()
                            .filter(i -> i.getExpiryDate()!=null &&
                                    i.getExpiryDate().isBefore(LocalDate.now()))
                            .mapToInt(Inventory::getQuantity).sum();

                    return new InventorySummary(e.getKey(), good, damaged, expired);

                }).toList();
    }
}
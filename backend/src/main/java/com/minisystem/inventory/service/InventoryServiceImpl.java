package com.minisystem.inventory.service;

import com.minisystem.inventory.dto.*;
import com.minisystem.inventory.entity.Inventory;
import com.minisystem.inventory.enums.InventoryStatus;
import com.minisystem.inventory.exception.InsufficientStockException;
import com.minisystem.inventory.exception.ResourceNotFoundException;
import com.minisystem.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repo;

    // ADD OR UPDATE STOCK
//    @Transactional
//    public Inventory addStock(AddStockRequest req){
//
//        Inventory inv = repo.findBySkuAndMrpAndBatchNo(
//                        req.getSku(), req.getMrp(), req.getBatchNo())
//                .orElse(null);
//
//        if(inv!=null){
//            inv.setQuantity(inv.getQuantity()+req.getQuantity());
//        }else{
//            inv = Inventory.builder()
//                    .sku(req.getSku())
//                    .mrp(req.getMrp())
//                    .batchNo(req.getBatchNo())
//                    .quantity(req.getQuantity())
//                    .status(req.getStatus())
//                    .expiryDate(req.getExpiryDate())
//                    .build();
//        }
//
//        Inventory saved = repo.save(inv);
//        publisher.publishUpdate(saved);
//        return saved;
//    }

    // GET AVAILABLE STOCK
    @Override
    public List<InventoryDto> getAvailable(String sku, Double mrp) {

        return repo.findBySkuIgnoreCaseAndMrpAndStatusAndExpiryDateGreaterThanEqual(
                        sku, mrp, InventoryStatus.GOOD, LocalDate.now())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public Map<String, List<InventoryDto>> getAvailableBulk(List<InventoryQuery> queries) {

        return queries.stream().collect(Collectors.toMap(
                q -> q.getSku() + "||" + q.getMrp(),
                q -> getAvailable(q.getSku(), q.getMrp()),
                (existing, ignored) -> existing
        ));
    }

    // UPDATE MULTIPLE BATCHES
//    @Transactional
//    public void updateStock(UpdateStockRequest req){
//
//        for(BatchUpdate b : req.getBatches()){
//
//            Inventory inv = repo.findBySkuAndMrpAndBatchNo(
//                    req.getSku(), req.getMrp(), b.getBatchNo()
//            ).orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
//
//            if(inv.getQuantity() < b.getQuantity()){
//                throw new InsufficientStockException("Insufficient quantity");
//            }
//
//            inv.setQuantity(inv.getQuantity() - b.getQuantity());
//
//            Inventory saved = repo.save(inv);
//
//            publisher.publishUpdate(saved);
//        }
//    }

    public List<InventoryDto> getAll() {

        return repo.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private InventoryDto mapToDto(Inventory inv) {

        return InventoryDto.builder()
                .sku(inv.getSku())
                .mrp(inv.getMrp())
                .batchNo(inv.getBatchNo())
                .quantity(inv.getQuantity())
                .status(inv.getStatus())
                .expiryDate(inv.getExpiryDate())
                .build();
    }

    public List<InventorySummary> getSummary() {

        List<Inventory> list = repo.findAll();

        return list.stream()
                .collect(Collectors.groupingBy(Inventory::getSku))
                .entrySet().stream()
                .map(e -> {

                    int good = e.getValue().stream()
                            .filter(i -> i.getStatus() == InventoryStatus.GOOD)
                            .mapToInt(Inventory::getQuantity).sum();

                    int damaged = e.getValue().stream()
                            .filter(i -> i.getStatus() == InventoryStatus.DAMAGED)
                            .mapToInt(Inventory::getQuantity).sum();

                    int expired = e.getValue().stream()
                            .filter(i -> i.getStatus() == InventoryStatus.EXPIRED)
                            .mapToInt(Inventory::getQuantity).sum();

                    return new InventorySummary(e.getKey(), good, damaged, expired);

                }).toList();
    }


    @Override
    @Transactional
    public SaveInventoryResponse save(SaveInventoryRequest req) {

        if (req.getItems() == null || req.getItems().isEmpty())
            throw new IllegalArgumentException("Batches required");

        List<BatchResult> results = new ArrayList<>();

        switch (req.getOperation().toUpperCase()) {

            // ADD STOCK  (INBOUND)
            case "ADD":

                for (BatchUpdate b : req.getItems()) {

                    Inventory inv = repo
                            .findBySkuIgnoreCaseAndMrpAndBatchNoIgnoreCase(
                                    b.getSku(),
                                    b.getMrp(),
                                    b.getBatchNo()
                            )
                            .orElse(null);

                    int previous = inv != null ? inv.getQuantity() : 0;

                    if (inv == null) {

                        inv = Inventory.builder()
                                .sku(b.getSku())
                                .mrp(b.getMrp())
                                .batchNo(b.getBatchNo())
                                .quantity(b.getQuantity())
                                .status(b.getStatus() == null ? InventoryStatus.GOOD : b.getStatus())
                                .expiryDate(b.getExpiryDate())
                                .build();

                    } else {

                        inv.setQuantity(inv.getQuantity() + b.getQuantity());

                        if (b.getStatus() != null)
                            inv.setStatus(InventoryStatus.valueOf(String.valueOf(b.getStatus())));

                        if (b.getExpiryDate() != null)
                            inv.setExpiryDate(b.getExpiryDate());
                    }

                    repo.save(inv);

                    results.add(
                            BatchResult.builder()
                                    .batchNo(inv.getBatchNo())
                                    .previousQty(previous)
                                    .updatedQty(inv.getQuantity())
                                    .status(inv.getStatus())
                                    .build()
                    );
                }

                return SaveInventoryResponse.builder()
                        .results(results)
                        .build();


            // DEDUCT STOCK (OUTBOUND BULK)
            case "DEDUCT":

                for (BatchUpdate b : req.getItems()) {

                    Inventory inv = repo
                            .findBySkuIgnoreCaseAndMrpAndBatchNoIgnoreCase(
                                    b.getSku(),
                                    b.getMrp(),
                                    b.getBatchNo()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Batch not found: "
                                                    + b.getSku() + "-" + b.getBatchNo()
                                    )
                            );

                    int previous = inv.getQuantity();
                    int deduct = b.getQuantity();

                    if (previous < deduct)
                        throw new InsufficientStockException(
                                "Insufficient qty for "
                                        + b.getSku() + " batch " + b.getBatchNo()
                        );

                    int newQty = previous - deduct;

                    if (newQty <= 0) {

                        repo.delete(inv);

                        results.add(
                                BatchResult.builder()
                                        .batchNo(inv.getBatchNo())
                                        .previousQty(previous)
                                        .updatedQty(0)
                                        .status(InventoryStatus.REMOVED)
                                        .build()
                        );

                    } else {

                        inv.setQuantity(newQty);
                        repo.save(inv);

                        results.add(
                                BatchResult.builder()
                                        .batchNo(inv.getBatchNo())
                                        .previousQty(previous)
                                        .updatedQty(newQty)
                                        .status(inv.getStatus())
                                        .build()
                        );
                    }
                }

                return SaveInventoryResponse.builder()
                        .results(results)
                        .build();

            default:
                throw new IllegalArgumentException("Operation must be ADD or DEDUCT");
        }
    }
}
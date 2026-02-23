package com.minisystem.inventory.service;

import com.minisystem.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryExpiryService {

    private final InventoryRepository repo;

    @Transactional
    public int markExpiredItems(){
        return repo.markExpiredItems();
    }
}
package com.minisystem.inventory.scheduler;

import com.minisystem.inventory.service.InventoryExpiryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryExpiryScheduler {

    private final InventoryExpiryService expiryService;

    @PostConstruct
    public void runOnStartup(){
        int updated = expiryService.markExpiredItems();
        log.info("Startup expiry check → {} items updated", updated);
    }

    @Scheduled(cron = "0 0 0 * * *") // midnight daily
    public void runDaily(){
        int updated = expiryService.markExpiredItems();
        log.info("Daily expiry check → {} items updated", updated);
    }
}
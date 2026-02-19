package com.minisystem.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryEventPublisher {

    private final SimpMessagingTemplate template;

    public void publishUpdate(Object data){
        template.convertAndSend("/topic/inventory", data);
    }
}
package com.ecommerce.inventory.event;

import com.ecommerce.contracts.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

@Component
public class InventorySagaListener {
    private final KafkaTemplate<String, OrderEvent> kafka;

    public InventorySagaListener(KafkaTemplate<String, OrderEvent> kafka) {
        this.kafka = kafka;
    }

    @KafkaListener(topics = "ecommerce.events", groupId = "inventory-saga")
    public void onOrderCreated(OrderEvent event) {
        if (!"ORDER_CREATED".equals(event.type())) return;
        String type = event.total().signum() >= 0 ? "INVENTORY_RESERVED" : "INVENTORY_REJECTED";
        try {
            kafka.send("ecommerce.events", event.orderId().toString(),
                    new OrderEvent(UUID.randomUUID(), type, event.orderId(), event.total(), "inventory-result", Instant.now())).get();
        } catch (Exception failure) {
            throw new IllegalStateException("Unable to publish inventory result", failure);
        }
    }
}

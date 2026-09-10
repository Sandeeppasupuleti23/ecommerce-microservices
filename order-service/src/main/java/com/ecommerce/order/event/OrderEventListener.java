package com.ecommerce.order.event;

import com.ecommerce.contracts.OrderEvent;
import com.ecommerce.order.service.OrderSagaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    private final OrderSagaService saga;

    public OrderEventListener(OrderSagaService saga) {
        this.saga = saga;
    }

    @KafkaListener(topics = "ecommerce.events", groupId = "order-saga")
    public void onEvent(OrderEvent event) {
        saga.handle(event);
    }
}
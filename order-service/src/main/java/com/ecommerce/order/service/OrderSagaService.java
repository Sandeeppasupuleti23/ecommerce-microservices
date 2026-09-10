package com.ecommerce.order.service;

import com.ecommerce.contracts.OrderEvent;
import com.ecommerce.order.domain.OrderAggregate;
import com.ecommerce.order.domain.OrderEventEntity;
import com.ecommerce.order.repository.OrderEventRepository;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class OrderSagaService {
    private final OrderRepository orders;
    private final OrderEventRepository events;
    private final KafkaTemplate<String, OrderEvent> kafka;

    public OrderSagaService(OrderRepository orders, OrderEventRepository events, KafkaTemplate<String, OrderEvent> kafka) {
        this.orders = orders;
        this.events = events;
        this.kafka = kafka;
    }

    @Transactional
    public OrderAggregate create(BigDecimal total) {
        UUID orderId = UUID.randomUUID();
        OrderAggregate order = orders.save(new OrderAggregate(orderId, total));
        publish(orderId, total, "ORDER_CREATED", "order-created");
        return order;
    }

    public void handle(OrderEvent event) {
        orders.findById(event.orderId()).ifPresent(order -> {
            if ("CONFIRMED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) return;
            String currentStatus = order.getStatus();
            String nextStatus = switch (event.type()) {
                case "INVENTORY_RESERVED" -> "PENDING_PAYMENT";
                case "PAYMENT_AUTHORIZED" -> "CONFIRMED";
                case "INVENTORY_REJECTED", "PAYMENT_FAILED" -> "CANCELLED";
                default -> order.getStatus();
            };
            if (!"ORDER_CREATED".equals(event.type()) && !events.existsById(event.eventId())) {
                events.save(new OrderEventEntity(event.eventId(), event.orderId(), event.type(), event.total(), event.payload(), event.occurredAt(), true));
            }
            order.setStatus(nextStatus);
            orders.save(order);
            if ("INVENTORY_RESERVED".equals(event.type()) && "PENDING_INVENTORY".equals(currentStatus)) {
                publish(order.getId(), order.getTotal(), "PAYMENT_REQUESTED", "payment-requested");
            }
        });
    }

    @Transactional(readOnly = true)
    public OrderAggregate replay(UUID orderId) {
        OrderAggregate replayed = null;
        for (OrderEventEntity event : events.findByOrderIdOrderByOccurredAtAsc(orderId)) {
            if (replayed == null && "ORDER_CREATED".equals(event.getType())) {
                replayed = new OrderAggregate(orderId, event.getTotal());
            }
            if (replayed == null) continue;
            switch (event.getType()) {
                case "INVENTORY_RESERVED" -> replayed.setStatus("PENDING_PAYMENT");
                case "PAYMENT_AUTHORIZED" -> replayed.setStatus("CONFIRMED");
                case "INVENTORY_REJECTED", "PAYMENT_FAILED" -> replayed.setStatus("CANCELLED");
                default -> { }
            }
        }
        return replayed;
    }

    @Scheduled(fixedDelayString = "${saga.outbox-delay-ms:1000}")
    @Transactional
    public void publishOutbox() {
        for (OrderEventEntity storedEvent : events.findTop100ByPublishedFalseOrderByOccurredAtAsc()) {
            try {
                OrderEvent event = storedEvent.toEvent();
                kafka.send("ecommerce.events", event.orderId().toString(), event).get();
                storedEvent.markPublished();
                events.save(storedEvent);
            } catch (Exception ignored) {
                return;
            }
        }
    }

    private void publish(UUID orderId, BigDecimal total, String type, String payload) {
        OrderEvent event = new OrderEvent(UUID.randomUUID(), type, orderId, total, payload, Instant.now());
        events.save(new OrderEventEntity(event.eventId(), orderId, type, total, payload, event.occurredAt()));
    }
}

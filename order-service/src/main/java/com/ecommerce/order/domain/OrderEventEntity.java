package com.ecommerce.order.domain;

import com.ecommerce.contracts.OrderEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_events")
public class OrderEventEntity {
    @Id
    private UUID eventId;
    private UUID orderId;
    private String type;
    private BigDecimal total;
    private String payload;
    private Instant occurredAt;
    private boolean published;

    protected OrderEventEntity() {
    }

    public OrderEventEntity(UUID eventId, UUID orderId, String type, BigDecimal total, String payload, Instant occurredAt) {
        this(eventId, orderId, type, total, payload, occurredAt, false);
    }

    public OrderEventEntity(UUID eventId, UUID orderId, String type, BigDecimal total, String payload, Instant occurredAt, boolean published) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.type = type;
        this.total = total;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.published = published;
    }

    public OrderEvent toEvent() { return new OrderEvent(eventId, type, orderId, total, payload, occurredAt); }
    public void markPublished() { this.published = true; }
    public UUID getOrderId() { return orderId; }
    public String getType() { return type; }
    public BigDecimal getTotal() { return total; }
    public Instant getOccurredAt() { return occurredAt; }
}

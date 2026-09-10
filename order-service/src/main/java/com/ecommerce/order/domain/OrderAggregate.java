package com.ecommerce.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderAggregate {
    @Id
    private UUID id;
    private BigDecimal total;
    private String status;
    private Instant createdAt;

    protected OrderAggregate() {
    }

    public OrderAggregate(UUID id, BigDecimal total) {
        this.id = id;
        this.total = total;
        this.status = "PENDING_INVENTORY";
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public BigDecimal getTotal() { return total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}

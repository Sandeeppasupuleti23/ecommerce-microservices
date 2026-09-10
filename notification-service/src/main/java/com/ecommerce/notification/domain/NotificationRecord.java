package com.ecommerce.notification.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationRecord {
    @Id
    private UUID id;
    private UUID orderId;
    private String type;
    private Instant createdAt;

    protected NotificationRecord() {
    }

    public NotificationRecord(UUID orderId, String type) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.type = type;
        this.createdAt = Instant.now();
    }
}

package com.ecommerce.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private UUID id;
    @Column(unique = true, nullable = false)
    private UUID orderId;
    private BigDecimal amount;
    private String status;

    protected Payment() {
    }

    public Payment(UUID orderId, BigDecimal amount, String status) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }
}

package com.ecommerce.contracts;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderEvent(
        UUID eventId,
        String type,
        UUID orderId,
        BigDecimal total,
        String payload,
        Instant occurredAt) {
}
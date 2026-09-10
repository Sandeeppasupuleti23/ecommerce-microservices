package com.ecommerce.payment.event;

import com.ecommerce.contracts.OrderEvent;
import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentSagaListener {
    private final PaymentRepository payments;
    private final KafkaTemplate<String, OrderEvent> kafka;

    public PaymentSagaListener(PaymentRepository payments, KafkaTemplate<String, OrderEvent> kafka) {
        this.payments = payments;
        this.kafka = kafka;
    }

    @KafkaListener(topics = "ecommerce.events", groupId = "payment-saga")
    @CircuitBreaker(name = "paymentProcessor", fallbackMethod = "paymentUnavailable")
    @Retry(name = "paymentProcessor", fallbackMethod = "paymentUnavailable")
    public void onPaymentRequested(OrderEvent event) {
        if (!"PAYMENT_REQUESTED".equals(event.type())) return;
        if (payments.existsByOrderId(event.orderId())) return;
        payments.save(new Payment(event.orderId(), event.total(), "AUTHORIZED"));
        publish(event, "PAYMENT_AUTHORIZED");
    }

    public void paymentUnavailable(OrderEvent event, Throwable failure) {
        throw new IllegalStateException("Payment processing is temporarily unavailable", failure);
    }

    private void publish(OrderEvent source, String type) {
        try {
            kafka.send("ecommerce.events", source.orderId().toString(),
                    new OrderEvent(UUID.randomUUID(), type, source.orderId(), source.total(), "payment-result", Instant.now())).get();
        } catch (Exception failure) {
            throw new IllegalStateException("Unable to publish payment result", failure);
        }
    }
}

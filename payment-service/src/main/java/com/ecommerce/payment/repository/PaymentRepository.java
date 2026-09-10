package com.ecommerce.payment.repository;

import com.ecommerce.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
	boolean existsByOrderId(UUID orderId);
}

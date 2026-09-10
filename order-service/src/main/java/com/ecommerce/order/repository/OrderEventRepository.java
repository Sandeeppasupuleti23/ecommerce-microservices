package com.ecommerce.order.repository;

import com.ecommerce.order.domain.OrderEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderEventRepository extends JpaRepository<OrderEventEntity, UUID> {
	java.util.List<OrderEventEntity> findTop100ByPublishedFalseOrderByOccurredAtAsc();
	java.util.List<OrderEventEntity> findByOrderIdOrderByOccurredAtAsc(UUID orderId);
}

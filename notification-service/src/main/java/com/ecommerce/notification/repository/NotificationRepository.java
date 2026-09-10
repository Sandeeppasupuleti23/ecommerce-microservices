package com.ecommerce.notification.repository;

import com.ecommerce.notification.domain.NotificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationRecord, UUID> {
	boolean existsByOrderIdAndType(UUID orderId, String type);
}

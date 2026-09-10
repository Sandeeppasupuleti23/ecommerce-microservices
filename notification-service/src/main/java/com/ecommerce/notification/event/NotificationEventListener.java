package com.ecommerce.notification.event;

import com.ecommerce.contracts.OrderEvent;
import com.ecommerce.notification.domain.NotificationRecord;
import com.ecommerce.notification.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {
    private final NotificationRepository notifications;

    public NotificationEventListener(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @KafkaListener(topics = "ecommerce.events", groupId = "notification-saga")
    public void onOrderEvent(OrderEvent event) {
        if ("PAYMENT_AUTHORIZED".equals(event.type()) || "PAYMENT_FAILED".equals(event.type())
                || "INVENTORY_REJECTED".equals(event.type())) {
            if (notifications.existsByOrderIdAndType(event.orderId(), event.type())) return;
            notifications.save(new NotificationRecord(event.orderId(), event.type()));
        }
    }
}

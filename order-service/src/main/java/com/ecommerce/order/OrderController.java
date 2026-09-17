package com.ecommerce.order;

import com.ecommerce.order.domain.OrderAggregate;
import com.ecommerce.order.service.OrderSagaService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderSagaService saga;

    public OrderController(OrderSagaService saga) {
        this.saga = saga;
    }

    @GetMapping
    public String getOrders() {
        return "Order Service is working!";
    }

    @org.springframework.web.bind.annotation.PostMapping
    @PreAuthorize("hasAuthority('SCOPE_orders.write')")
    public OrderAggregate createOrder(@org.springframework.web.bind.annotation.RequestBody Map<String, BigDecimal> request) {
        return saga.create(request.getOrDefault("total", BigDecimal.ZERO));
    }

    @org.springframework.web.bind.annotation.GetMapping("/{orderId}")
    public OrderAggregate getOrder(@org.springframework.web.bind.annotation.PathVariable("orderId") UUID orderId) {
        return saga.replay(orderId);
    }
}

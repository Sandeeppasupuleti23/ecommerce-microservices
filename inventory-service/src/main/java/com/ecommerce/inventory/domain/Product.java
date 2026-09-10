package com.ecommerce.inventory.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;

@Document("products")
public class Product {
    @Id
    private UUID id;
    private String sku;
    private int available;

    protected Product() {
    }

    public Product(String sku, int available) {
        this.id = UUID.randomUUID();
        this.sku = sku;
        this.available = available;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public int getAvailable() { return available; }
    public void setSku(String sku) { this.sku = sku; }
    public void setAvailable(int available) { this.available = available; }
}

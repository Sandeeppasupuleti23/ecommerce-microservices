package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.UUID;

public interface ProductRepository extends MongoRepository<Product, UUID> {
	java.util.Optional<Product> findBySku(String sku);
}

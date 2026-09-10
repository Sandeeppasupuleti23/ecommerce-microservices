package com.ecommerce.inventory;

import com.ecommerce.inventory.domain.Product;
import com.ecommerce.inventory.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final ProductRepository products;

    public InventoryController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping
    public List<Product> getInventory() {
        return products.findAll();
    }

    @GetMapping("/{sku}")
    public Product getProduct(@PathVariable String sku) {
        return products.findBySku(sku).orElseThrow();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public Product addProduct(@RequestBody Product product) {
        return products.save(product);
    }
}
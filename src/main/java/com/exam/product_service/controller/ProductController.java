package com.exam.product_service.controller;

import com.exam.product_service.model.Product;
import com.exam.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductRepository productRepository;
    private final org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    public ProductController(ProductRepository productRepository, org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping
    public List<Product> findAll() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable String id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Product save(@RequestBody Product product) {
        try {
            log.info("Saving product: {}", product.getNombre());
            // Simulate random failure for testing retries
            if (Math.random() < 0.3) {
                throw new RuntimeException("Simulated failure during product creation");
            }
            return productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product, sending to retry topic: {}", e.getMessage());
            
            java.util.Map<String, Object> wrappedPayload = new java.util.HashMap<>();
            wrappedPayload.put("data", product);
            wrappedPayload.put("sendEmail", java.util.Map.of("status", "PENDING", "message", ""));
            wrappedPayload.put("updateRetryJobs", java.util.Map.of("status", "PENDING", "message", ""));
            
            kafkaTemplate.send("product_retry_jobs", wrappedPayload);
            throw e;
        }
    }

    @PostMapping("/retry")
    public Product retry(@RequestBody Product product) {
        log.info("Retrying save for product: {}", product.getNombre());
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    public Product updateById(@PathVariable String id, @RequestBody Product product) {
        log.info("Updating product with id: {}", id);
        product.setId(id);
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        log.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
    }
}

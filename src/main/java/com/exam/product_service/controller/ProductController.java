package com.exam.product_service.controller;

import com.exam.product_service.model.Product;
import com.exam.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
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
    public ResponseEntity<?> retry(@RequestBody Product product) {
        if (product == null || product.getNombre() == null) {
            log.error("Received invalid product data during retry: {}", product);
            return ResponseEntity.badRequest().body("Invalid product data: nombre is required");
        }
        log.info("Retrying save for product: {}", product.getNombre());
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public Product updateById(@PathVariable String id, @RequestBody Product product) {
        log.info("Updating product with id: {}", id);
        product.setId(id);
        return productRepository.save(product);
    }

    @PutMapping("/{id}/stock/reduce")
    public ResponseEntity<?> reduceStock(@PathVariable String id, @RequestParam int quantity) {
        log.info("Reducing stock for product {} by {}", id, quantity);
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            if (product.getStock() >= quantity) {
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.badRequest().body("Insufficient stock for product " + id);
            }
        }
        return ResponseEntity.notFound().build();
    }
}

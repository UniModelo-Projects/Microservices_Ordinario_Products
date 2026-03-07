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

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
        log.info("Saving product: {}", product.getNombre());
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

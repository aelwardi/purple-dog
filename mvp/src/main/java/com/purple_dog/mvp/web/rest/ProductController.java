package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.ProductCreateRequest;
import com.purple_dog.mvp.dto.ProductResponse;
import com.purple_dog.mvp.dto.ProductSearchRequest;
import com.purple_dog.mvp.entities.ProductStatus;
import com.purple_dog.mvp.exceptions.ProductException;
import com.purple_dog.mvp.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest request) {
        try {
            ProductResponse response = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ProductException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProduct(id));
        } catch (ProductException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ProductResponse>> getBySeller(
            @PathVariable Long sellerId,
            @RequestParam(name = "status", required = false) ProductStatus status) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId, status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(
            @RequestParam(value = "q", required = false) String text,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "saleType", required = false) com.purple_dog.mvp.entities.SaleType saleType,
            @RequestParam(value = "status", required = false) ProductStatus status,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "availableOnly", required = false) Boolean availableOnly) {
        ProductSearchRequest req = new ProductSearchRequest(text, categoryId, saleType, status, minPrice, maxPrice,
                availableOnly);
        return ResponseEntity.ok(productService.search(req));
    }

    @PostMapping("/{productId}/favorite")
    public ResponseEntity<?> addFavorite(
            @PathVariable Long productId,
            @RequestParam Long userId) {
        try {
            productService.addFavorite(userId, productId);
            return ResponseEntity.ok("Added to favorites");
        } catch (ProductException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}/favorite")
    public ResponseEntity<?> removeFavorite(
            @PathVariable Long productId,
            @RequestParam Long userId) {
        productService.removeFavorite(userId, productId);
        return ResponseEntity.ok("Removed from favorites");
    }

    @GetMapping("/favorites/{userId}")
    public ResponseEntity<List<ProductResponse>> listFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(productService.listFavorites(userId));
    }
}

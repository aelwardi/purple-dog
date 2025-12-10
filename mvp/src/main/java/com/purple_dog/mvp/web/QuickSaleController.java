package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.QuickSaleCreateDTO;
import com.purple_dog.mvp.dto.QuickSaleResponseDTO;
import com.purple_dog.mvp.dto.QuickSaleUpdateDTO;
import com.purple_dog.mvp.services.QuickSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/quick-sales")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QuickSaleController {

    private final QuickSaleService quickSaleService;

    /**
     * Créer une vente rapide
     */
    @PostMapping
    public ResponseEntity<QuickSaleResponseDTO> createQuickSale(@Valid @RequestBody QuickSaleCreateDTO dto) {
        log.info("Request to create quick sale for product: {}", dto.getProductId());
        QuickSaleResponseDTO response = quickSaleService.createQuickSale(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer une vente rapide par ID
     */
    @GetMapping("/{quickSaleId}")
    public ResponseEntity<QuickSaleResponseDTO> getQuickSaleById(@PathVariable Long quickSaleId) {
        log.info("Request to get quick sale: {}", quickSaleId);
        QuickSaleResponseDTO quickSale = quickSaleService.getQuickSaleById(quickSaleId);
        return ResponseEntity.ok(quickSale);
    }

    /**
     * Récupérer la vente rapide d'un produit
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<QuickSaleResponseDTO> getQuickSaleByProductId(@PathVariable Long productId) {
        log.info("Request to get quick sale for product: {}", productId);
        QuickSaleResponseDTO quickSale = quickSaleService.getQuickSaleByProductId(productId);
        return ResponseEntity.ok(quickSale);
    }

    /**
     * Récupérer toutes les ventes rapides disponibles
     */
    @GetMapping("/available")
    public ResponseEntity<List<QuickSaleResponseDTO>> getAvailableQuickSales() {
        log.info("Request to get all available quick sales");
        List<QuickSaleResponseDTO> quickSales = quickSaleService.getAvailableQuickSales();
        return ResponseEntity.ok(quickSales);
    }

    /**
     * Récupérer les ventes rapides d'un vendeur
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<QuickSaleResponseDTO>> getSellerQuickSales(@PathVariable Long sellerId) {
        log.info("Request to get quick sales for seller: {}", sellerId);
        List<QuickSaleResponseDTO> quickSales = quickSaleService.getSellerQuickSales(sellerId);
        return ResponseEntity.ok(quickSales);
    }

    /**
     * Récupérer les ventes rapides dans un budget
     */
    @GetMapping("/by-price/{maxPrice}")
    public ResponseEntity<List<QuickSaleResponseDTO>> getQuickSalesByMaxPrice(@PathVariable BigDecimal maxPrice) {
        log.info("Request to get quick sales with max price: {}", maxPrice);
        List<QuickSaleResponseDTO> quickSales = quickSaleService.getQuickSalesByMaxPrice(maxPrice);
        return ResponseEntity.ok(quickSales);
    }

    /**
     * Récupérer les ventes rapides acceptant les offres
     */
    @GetMapping("/accepting-offers")
    public ResponseEntity<List<QuickSaleResponseDTO>> getQuickSalesAcceptingOffers() {
        log.info("Request to get quick sales accepting offers");
        List<QuickSaleResponseDTO> quickSales = quickSaleService.getQuickSalesAcceptingOffers();
        return ResponseEntity.ok(quickSales);
    }

    /**
     * Mettre à jour une vente rapide
     */
    @PutMapping("/{quickSaleId}")
    public ResponseEntity<QuickSaleResponseDTO> updateQuickSale(
            @PathVariable Long quickSaleId,
            @Valid @RequestBody QuickSaleUpdateDTO dto) {

        log.info("Request to update quick sale: {}", quickSaleId);
        QuickSaleResponseDTO response = quickSaleService.updateQuickSale(quickSaleId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Marquer une vente rapide comme vendue
     */
    @PatchMapping("/{quickSaleId}/mark-sold")
    public ResponseEntity<QuickSaleResponseDTO> markAsSold(@PathVariable Long quickSaleId) {
        log.info("Request to mark quick sale as sold: {}", quickSaleId);
        QuickSaleResponseDTO response = quickSaleService.markAsSold(quickSaleId);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer une vente rapide
     */
    @DeleteMapping("/{quickSaleId}")
    public ResponseEntity<Void> deleteQuickSale(@PathVariable Long quickSaleId) {
        log.info("Request to delete quick sale: {}", quickSaleId);
        quickSaleService.deleteQuickSale(quickSaleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Compter les ventes d'un vendeur
     */
    @GetMapping("/seller/{sellerId}/count")
    public ResponseEntity<Long> countSellerQuickSales(@PathVariable Long sellerId) {
        log.info("Request to count quick sales for seller: {}", sellerId);
        long count = quickSaleService.countSellerQuickSales(sellerId);
        return ResponseEntity.ok(count);
    }

    /**
     * Compter les ventes vendues d'un vendeur
     */
    @GetMapping("/seller/{sellerId}/count-sold")
    public ResponseEntity<Long> countSellerSoldQuickSales(@PathVariable Long sellerId) {
        log.info("Request to count sold quick sales for seller: {}", sellerId);
        long count = quickSaleService.countSellerSoldQuickSales(sellerId);
        return ResponseEntity.ok(count);
    }
}


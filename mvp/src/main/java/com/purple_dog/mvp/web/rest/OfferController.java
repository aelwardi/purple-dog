package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.OfferCreateDTO;
import com.purple_dog.mvp.dto.OfferResponseDTO;
import com.purple_dog.mvp.entities.OfferStatus;
import com.purple_dog.mvp.services.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Offer Management", description = "APIs for managing offers")
@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OfferController {

    private final OfferService offerService;

    /**
     * Créer une offre
     */
    @PostMapping
    public ResponseEntity<OfferResponseDTO> createOffer(@Valid @RequestBody OfferCreateDTO dto) {
        log.info("Request to create offer for quick sale: {}", dto.getQuickSaleId());
        OfferResponseDTO response = offerService.createOffer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer une offre par ID
     */
    @GetMapping("/{offerId}")
    public ResponseEntity<OfferResponseDTO> getOfferById(@PathVariable Long offerId) {
        log.info("Request to get offer: {}", offerId);
        OfferResponseDTO offer = offerService.getOfferById(offerId);
        return ResponseEntity.ok(offer);
    }

    /**
     * Récupérer les offres d'une vente rapide
     */
    @GetMapping("/quick-sale/{quickSaleId}")
    public ResponseEntity<List<OfferResponseDTO>> getQuickSaleOffers(@PathVariable Long quickSaleId) {
        log.info("Request to get offers for quick sale: {}", quickSaleId);
        List<OfferResponseDTO> offers = offerService.getQuickSaleOffers(quickSaleId);
        return ResponseEntity.ok(offers);
    }

    /**
     * Récupérer les offres d'un acheteur
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OfferResponseDTO>> getBuyerOffers(@PathVariable Long buyerId) {
        log.info("Request to get offers for buyer: {}", buyerId);
        List<OfferResponseDTO> offers = offerService.getBuyerOffers(buyerId);
        return ResponseEntity.ok(offers);
    }

    /**
     * Récupérer les offres reçues par un vendeur
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OfferResponseDTO>> getSellerOffers(@PathVariable Long sellerId) {
        log.info("Request to get offers for seller: {}", sellerId);
        List<OfferResponseDTO> offers = offerService.getSellerOffers(sellerId);
        return ResponseEntity.ok(offers);
    }

    /**
     * Récupérer les offres par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OfferResponseDTO>> getOffersByStatus(@PathVariable OfferStatus status) {
        log.info("Request to get offers with status: {}", status);
        List<OfferResponseDTO> offers = offerService.getOffersByStatus(status);
        return ResponseEntity.ok(offers);
    }

    /**
     * Accepter une offre
     */
    @PostMapping("/{offerId}/accept")
    public ResponseEntity<OfferResponseDTO> acceptOffer(@PathVariable Long offerId) {
        log.info("Request to accept offer: {}", offerId);
        OfferResponseDTO response = offerService.acceptOffer(offerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Rejeter une offre
     */
    @PostMapping("/{offerId}/reject")
    public ResponseEntity<OfferResponseDTO> rejectOffer(@PathVariable Long offerId) {
        log.info("Request to reject offer: {}", offerId);
        OfferResponseDTO response = offerService.rejectOffer(offerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Annuler une offre
     */
    @PostMapping("/{offerId}/cancel")
    public ResponseEntity<OfferResponseDTO> cancelOffer(@PathVariable Long offerId) {
        log.info("Request to cancel offer: {}", offerId);
        OfferResponseDTO response = offerService.cancelOffer(offerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Compter les offres d'un acheteur
     */
    @GetMapping("/buyer/{buyerId}/count")
    public ResponseEntity<Long> countBuyerOffers(@PathVariable Long buyerId) {
        log.info("Request to count offers for buyer: {}", buyerId);
        long count = offerService.countBuyerOffers(buyerId);
        return ResponseEntity.ok(count);
    }

    /**
     * Compter les offres en attente d'une vente rapide
     */
    @GetMapping("/quick-sale/{quickSaleId}/count-pending")
    public ResponseEntity<Long> countPendingOffers(@PathVariable Long quickSaleId) {
        log.info("Request to count pending offers for quick sale: {}", quickSaleId);
        long count = offerService.countPendingOffers(quickSaleId);
        return ResponseEntity.ok(count);
    }
}

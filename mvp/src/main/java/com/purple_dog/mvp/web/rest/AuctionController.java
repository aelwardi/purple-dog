package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.AuctionDTO;
import com.purple_dog.mvp.dto.AuctionResponse;
import com.purple_dog.mvp.dto.CreateAuctionRequest;
import com.purple_dog.mvp.exceptions.AuctionException;
import com.purple_dog.mvp.services.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    /**
     * Récupère toutes les enchères
     * GET /api/auctions
     */
    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAll() {
        List<AuctionResponse> auctions = auctionService.getAllAuctions();
        return ResponseEntity.ok(auctions);
    }

    /**
     * Récupère une enchère par son ID
     * GET /api/auctions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionById(@PathVariable Long id) {
        try {
            AuctionResponse auction = auctionService.getAuctionById(id);
            return ResponseEntity.ok(auction);
        } catch (AuctionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Récupère toutes les enchères actives
     * GET /api/auctions/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<AuctionResponse>> getActiveAuctions() {
        List<AuctionResponse> activeAuctions = auctionService.getActiveAuctions();
        return ResponseEntity.ok(activeAuctions);
    }

    /**
     * Récupère toutes les enchères clôturées
     * GET /api/auctions/closed
     */
    @GetMapping("/closed")
    public ResponseEntity<List<AuctionResponse>> getClosedAuctions() {
        List<AuctionResponse> closedAuctions = auctionService.getClosedAuctions();
        return ResponseEntity.ok(closedAuctions);
    }

    /**
     * Crée une nouvelle enchère avec prix de démarrage par défaut (-10%)
     * POST /api/auctions
     * Body: {
     * "productId": 1,
     * "desiredPrice": 100.00
     * }
     */
    @PostMapping
    public ResponseEntity<?> createAuctionWithDefault(
            @RequestBody CreateAuctionRequest request) {
        try {
            AuctionResponse auction = auctionService.createAuctionFromRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(auction);
        } catch (AuctionException e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Met à jour une enchère
     * PUT /api/auctions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuction(
            @PathVariable Long id,
            @RequestBody AuctionDTO auctionDTO) {
        try {
            AuctionResponse updated = auctionService.updateAuction(id, auctionDTO);
            return ResponseEntity.ok(updated);
        } catch (AuctionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Clôture une enchère
     * PUT /api/auctions/{id}/close
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<?> closeAuction(@PathVariable Long id) {
        try {
            auctionService.closeAuction(id);
            return ResponseEntity.ok("Auction closed successfully");
        } catch (AuctionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Vérifie si le prix de réserve a été atteint
     * GET /api/auctions/{id}/reserve-met
     */
    @GetMapping("/{id}/reserve-met")
    public ResponseEntity<?> isReserveMet(@PathVariable Long id) {
        try {
            boolean isMet = auctionService.isReservePriceMet(id);
            return ResponseEntity.ok("Reserve price met: " + isMet);
        } catch (AuctionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Supprime une enchère
     * DELETE /api/auctions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuction(@PathVariable Long id) {
        try {
            auctionService.deleteAuction(id);
            return ResponseEntity.ok("Auction deleted successfully");
        } catch (AuctionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }
}

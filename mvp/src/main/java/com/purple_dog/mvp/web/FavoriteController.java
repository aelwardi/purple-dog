package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.FavoriteDTO;
import com.purple_dog.mvp.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Ajouter un produit aux favoris
     */
    @PostMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<FavoriteDTO> addToFavorites(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        log.info("Request to add product {} to favorites for user {}", productId, userId);
        FavoriteDTO response = favoriteService.addToFavorites(userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retirer un produit des favoris
     */
    @DeleteMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<Void> removeFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        log.info("Request to remove product {} from favorites for user {}", productId, userId);
        favoriteService.removeFromFavorites(userId, productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer les favoris d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteDTO>> getUserFavorites(@PathVariable Long userId) {
        log.info("Request to get favorites for user: {}", userId);
        List<FavoriteDTO> favorites = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    /**
     * Vérifier si un produit est en favoris
     */
    @GetMapping("/user/{userId}/product/{productId}/check")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        log.info("Request to check if product {} is favorite for user {}", productId, userId);
        boolean isFavorite = favoriteService.isFavorite(userId, productId);
        return ResponseEntity.ok(isFavorite);
    }

    /**
     * Compter les favoris d'un utilisateur
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countUserFavorites(@PathVariable Long userId) {
        log.info("Request to count favorites for user: {}", userId);
        long count = favoriteService.countUserFavorites(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Compter combien d'utilisateurs ont mis ce produit en favori
     */
    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> countProductFavorites(@PathVariable Long productId) {
        log.info("Request to count favorites for product: {}", productId);
        long count = favoriteService.countProductFavorites(productId);
        return ResponseEntity.ok(count);
    }

    /**
     * Supprimer tous les favoris d'un utilisateur
     */
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<Void> clearUserFavorites(@PathVariable Long userId) {
        log.info("Request to clear all favorites for user: {}", userId);
        favoriteService.clearUserFavorites(userId);
        return ResponseEntity.noContent().build();
    }
}


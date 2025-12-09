package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.FavoriteRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.ProductRepository;
import com.purple_dog.mvp.dto.FavoriteDTO;
import com.purple_dog.mvp.entities.Favorite;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.Product;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PersonRepository personRepository;
    private final ProductRepository productRepository;

    /**
     * Ajouter un produit aux favoris
     */
    public FavoriteDTO addToFavorites(Long userId, Long productId) {
        log.info("Adding product {} to favorites for user {}", productId, userId);

        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateResourceException("Product already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        favorite = favoriteRepository.save(favorite);
        log.info("Product added to favorites successfully");

        return mapToDTO(favorite);
    }

    /**
     * Retirer un produit des favoris
     */
    public void removeFromFavorites(Long userId, Long productId) {
        log.info("Removing product {} from favorites for user {}", productId, userId);

        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ResourceNotFoundException("Favorite not found");
        }

        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        log.info("Product removed from favorites successfully");
    }

    /**
     * Récupérer les favoris d'un utilisateur
     */
    public List<FavoriteDTO> getUserFavorites(Long userId) {
        log.info("Fetching favorites for user: {}", userId);

        if (!personRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return favoriteRepository.findByUserIdWithProducts(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Vérifier si un produit est en favoris
     */
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * Compter les favoris d'un utilisateur
     */
    public long countUserFavorites(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }

    /**
     * Compter combien d'utilisateurs ont mis ce produit en favori
     */
    public long countProductFavorites(Long productId) {
        return favoriteRepository.countByProductId(productId);
    }

    /**
     * Supprimer tous les favoris d'un utilisateur
     */
    public void clearUserFavorites(Long userId) {
        log.info("Clearing all favorites for user: {}", userId);

        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        favoriteRepository.deleteAll(favorites);

        log.info("All favorites cleared for user: {}", userId);
    }

    private FavoriteDTO mapToDTO(Favorite favorite) {
        Product product = favorite.getProduct();

        Person seller = product.getSeller();
        String sellerName = seller != null ?
                seller.getFirstName() + " " + seller.getLastName() : "N/A";

        String mainImage = product.getPhotos() != null && !product.getPhotos().isEmpty()
                ? product.getPhotos().getFirst().getUrl()
                : null;

        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .productId(product.getId())
                .productTitle(product.getTitle())
                .productDescription(product.getDescription())
                .productPrice(product.getEstimatedValue())
                .productMainImage(mainImage)
                .sellerName(sellerName)
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}


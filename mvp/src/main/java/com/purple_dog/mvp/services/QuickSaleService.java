package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.OfferRepository;
import com.purple_dog.mvp.dao.ProductRepository;
import com.purple_dog.mvp.dao.QuickSaleRepository;
import com.purple_dog.mvp.dto.QuickSaleCreateDTO;
import com.purple_dog.mvp.dto.QuickSaleResponseDTO;
import com.purple_dog.mvp.dto.QuickSaleUpdateDTO;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.Product;
import com.purple_dog.mvp.entities.QuickSale;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuickSaleService {

    private final QuickSaleRepository quickSaleRepository;
    private final OfferRepository offerRepository;
    private final ProductRepository productRepository;

    /**
     * Créer une vente rapide
     */
    public QuickSaleResponseDTO createQuickSale(QuickSaleCreateDTO dto) {
        log.info("Creating quick sale for product: {}", dto.getProductId());

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

        if (quickSaleRepository.existsByProductId(dto.getProductId())) {
            throw new DuplicateResourceException("Quick sale already exists for this product");
        }

        if (dto.getAcceptOffers() != null && dto.getAcceptOffers() && dto.getMinimumOfferPrice() != null) {
            if (dto.getMinimumOfferPrice().compareTo(dto.getFixedPrice()) >= 0) {
                throw new InvalidOperationException("Minimum offer price must be less than fixed price");
            }
        }

        QuickSale quickSale = QuickSale.builder()
                .product(product)
                .fixedPrice(dto.getFixedPrice())
                .acceptOffers(dto.getAcceptOffers() != null ? dto.getAcceptOffers() : true)
                .minimumOfferPrice(dto.getMinimumOfferPrice())
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .build();

        quickSale = quickSaleRepository.save(quickSale);
        log.info("Quick sale created successfully with id: {}", quickSale.getId());

        return mapToResponseDTO(quickSale);
    }

    /**
     * Récupérer une vente rapide par ID
     */
    public QuickSaleResponseDTO getQuickSaleById(Long quickSaleId) {
        log.info("Fetching quick sale: {}", quickSaleId);

        QuickSale quickSale = quickSaleRepository.findByIdWithDetails(quickSaleId)
                .orElseThrow(() -> new ResourceNotFoundException("Quick sale not found with id: " + quickSaleId));

        return mapToResponseDTO(quickSale);
    }

    /**
     * Récupérer la vente rapide d'un produit
     */
    public QuickSaleResponseDTO getQuickSaleByProductId(Long productId) {
        log.info("Fetching quick sale for product: {}", productId);

        QuickSale quickSale = quickSaleRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No quick sale found for product: " + productId));

        return mapToResponseDTO(quickSale);
    }

    /**
     * Récupérer toutes les ventes rapides disponibles
     */
    public List<QuickSaleResponseDTO> getAvailableQuickSales() {
        log.info("Fetching all available quick sales");

        return quickSaleRepository.findByIsAvailableTrue().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les ventes rapides d'un vendeur
     */
    public List<QuickSaleResponseDTO> getSellerQuickSales(Long sellerId) {
        log.info("Fetching quick sales for seller: {}", sellerId);

        return quickSaleRepository.findBySellerId(sellerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les ventes rapides dans un budget
     */
    public List<QuickSaleResponseDTO> getQuickSalesByMaxPrice(BigDecimal maxPrice) {
        log.info("Fetching quick sales with max price: {}", maxPrice);

        return quickSaleRepository.findAvailableByMaxPrice(maxPrice).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les ventes rapides acceptant les offres
     */
    public List<QuickSaleResponseDTO> getQuickSalesAcceptingOffers() {
        log.info("Fetching quick sales accepting offers");

        return quickSaleRepository.findAvailableAcceptingOffers().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour une vente rapide
     */
    public QuickSaleResponseDTO updateQuickSale(Long quickSaleId, QuickSaleUpdateDTO dto) {
        log.info("Updating quick sale: {}", quickSaleId);

        QuickSale quickSale = quickSaleRepository.findById(quickSaleId)
                .orElseThrow(() -> new ResourceNotFoundException("Quick sale not found with id: " + quickSaleId));

        if (!quickSale.getIsAvailable()) {
            throw new InvalidOperationException("Cannot update sold quick sale");
        }

        if (dto.getFixedPrice() != null) {
            quickSale.setFixedPrice(dto.getFixedPrice());
        }

        if (dto.getAcceptOffers() != null) {
            quickSale.setAcceptOffers(dto.getAcceptOffers());
        }

        if (dto.getMinimumOfferPrice() != null) {
            if (dto.getMinimumOfferPrice().compareTo(quickSale.getFixedPrice()) >= 0) {
                throw new InvalidOperationException("Minimum offer price must be less than fixed price");
            }
            quickSale.setMinimumOfferPrice(dto.getMinimumOfferPrice());
        }

        if (dto.getIsAvailable() != null) {
            quickSale.setIsAvailable(dto.getIsAvailable());
        }

        quickSale = quickSaleRepository.save(quickSale);
        log.info("Quick sale updated successfully");

        return mapToResponseDTO(quickSale);
    }

    /**
     * Marquer une vente rapide comme vendue
     */
    public QuickSaleResponseDTO markAsSold(Long quickSaleId) {
        log.info("Marking quick sale as sold: {}", quickSaleId);

        QuickSale quickSale = quickSaleRepository.findById(quickSaleId)
                .orElseThrow(() -> new ResourceNotFoundException("Quick sale not found with id: " + quickSaleId));

        if (!quickSale.getIsAvailable()) {
            throw new InvalidOperationException("Quick sale already sold");
        }

        quickSale.setIsAvailable(false);
        quickSale.setSoldAt(LocalDateTime.now());

        quickSale = quickSaleRepository.save(quickSale);
        log.info("Quick sale marked as sold");

        return mapToResponseDTO(quickSale);
    }

    /**
     * Supprimer une vente rapide
     */
    public void deleteQuickSale(Long quickSaleId) {
        log.info("Deleting quick sale: {}", quickSaleId);

        QuickSale quickSale = quickSaleRepository.findById(quickSaleId)
                .orElseThrow(() -> new ResourceNotFoundException("Quick sale not found with id: " + quickSaleId));

        if (!quickSale.getIsAvailable()) {
            throw new InvalidOperationException("Cannot delete sold quick sale");
        }

        quickSaleRepository.delete(quickSale);
        log.info("Quick sale deleted successfully");
    }

    /**
     * Compter les ventes d'un vendeur
     */
    public long countSellerQuickSales(Long sellerId) {
        return quickSaleRepository.countBySellerId(sellerId);
    }

    /**
     * Compter les ventes vendues d'un vendeur
     */
    public long countSellerSoldQuickSales(Long sellerId) {
        return quickSaleRepository.countSoldBySellerId(sellerId);
    }

    private QuickSaleResponseDTO mapToResponseDTO(QuickSale quickSale) {
        Product product = quickSale.getProduct();
        Person seller = product.getSeller();

        String mainImage = product.getPhotos() != null && !product.getPhotos().isEmpty()
                ? product.getPhotos().get(0).getUrl()
                : null;

        long offersCount = offerRepository.countByQuickSaleId(quickSale.getId());
        long pendingOffersCount = offerRepository.countPendingByQuickSaleId(quickSale.getId());

        return QuickSaleResponseDTO.builder()
                .id(quickSale.getId())
                .productId(product.getId())
                .productTitle(product.getTitle())
                .productDescription(product.getDescription())
                .productMainImage(mainImage)
                .fixedPrice(quickSale.getFixedPrice())
                .acceptOffers(quickSale.getAcceptOffers())
                .minimumOfferPrice(quickSale.getMinimumOfferPrice())
                .isAvailable(quickSale.getIsAvailable())
                .createdAt(quickSale.getCreatedAt())
                .soldAt(quickSale.getSoldAt())
                .sellerId(seller.getId())
                .sellerName(seller.getFirstName() + " " + seller.getLastName())
                .offersCount(offersCount)
                .pendingOffersCount(pendingOffersCount)
                .build();
    }
}


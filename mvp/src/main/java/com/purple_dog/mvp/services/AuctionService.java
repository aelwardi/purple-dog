package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AuctionRepository;
import com.purple_dog.mvp.dto.AuctionDTO;
import com.purple_dog.mvp.dto.AuctionResponse;
import com.purple_dog.mvp.dto.CreateAuctionRequest;
import com.purple_dog.mvp.entities.Auction;
import com.purple_dog.mvp.entities.Product;
import com.purple_dog.mvp.exceptions.AuctionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductService productService;

    private static final long DEFAULT_AUCTION_DURATION_DAYS = 7;
    private static final BigDecimal DEFAULT_STARTING_PRICE_REDUCTION = new BigDecimal("0.10"); // 10%
    private static final BigDecimal DEFAULT_BID_INCREMENT = new BigDecimal("10.00");

    /**
     * Crée une nouvelle enchère basée sur une demande
     * 
     * @param request la demande de création d'enchère
     * @return l'enchère créée
     */
    public AuctionResponse createAuctionFromRequest(CreateAuctionRequest request) {
        // Validation
        if (request.getProductId() == null) {
            throw new AuctionException("Product ID is required");
        }
        if (request.getDesiredPrice() == null || request.getDesiredPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AuctionException("Desired price must be greater than 0");
        }

        // Récupérer le produit
        Product product = productService.getProductById(request.getProductId());

        // Vérifier qu'il n'existe pas déjà une enchère pour ce produit
        if (auctionRepository.findByProduct(product).isPresent()) {
            throw new AuctionException("An auction already exists for this product");
        }

        // Créer l'enchère
        Auction auction = new Auction();
        auction.setProduct(product);
        auction.setReservePrice(request.getDesiredPrice());

        // Calcul du prix de démarrage
        BigDecimal startingPrice;
        if (request.getCustomStartingPrice() != null &&
                request.getCustomStartingPrice().compareTo(BigDecimal.ZERO) > 0) {
            startingPrice = request.getCustomStartingPrice();
        } else {
            // Par défaut : -10% du prix souhaité
            startingPrice = request.getDesiredPrice().multiply(
                    BigDecimal.ONE.subtract(DEFAULT_STARTING_PRICE_REDUCTION));
        }

        auction.setStartingPrice(startingPrice);
        auction.setCurrentPrice(startingPrice);

        // Incrément d'enchère
        if (request.getBidIncrement() != null && request.getBidIncrement().compareTo(BigDecimal.ZERO) > 0) {
            auction.setBidIncrement(request.getBidIncrement());
        } else {
            auction.setBidIncrement(DEFAULT_BID_INCREMENT);
        }

        // Durée par défaut : 1 semaine
        LocalDateTime now = LocalDateTime.now();
        auction.setStartDate(now);
        auction.setEndDate(now.plusDays(DEFAULT_AUCTION_DURATION_DAYS));

        auction.setIsActive(true);
        auction.setReservePriceMet(false);
        auction.setTotalBids(0);

        Auction saved = auctionRepository.save(auction);
        return convertToResponse(saved);
    }

    /**
     * Crée une nouvelle enchère basée sur le prix souhaité du produit
     * 
     * @param product             le produit à mettre en enchère
     * @param desiredPrice        le prix souhaité (prix minimum de réserve)
     * @param customStartingPrice prix de démarrage personnalisé (optionnel)
     * @return l'enchère créée
     */
    public Auction createAuction(Product product, BigDecimal desiredPrice, BigDecimal customStartingPrice) {
        if (product == null) {
            throw new AuctionException("Product cannot be null");
        }
        if (desiredPrice == null || desiredPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AuctionException("Desired price must be greater than 0");
        }

        // Vérifier qu'il n'existe pas déjà une enchère pour ce produit
        if (auctionRepository.findByProduct(product).isPresent()) {
            throw new AuctionException("An auction already exists for this product");
        }

        Auction auction = new Auction();
        auction.setProduct(product);
        auction.setReservePrice(desiredPrice);

        // Calcul du prix de démarrage
        BigDecimal startingPrice;
        if (customStartingPrice != null && customStartingPrice.compareTo(BigDecimal.ZERO) > 0) {
            startingPrice = customStartingPrice;
        } else {
            // Par défaut : -10% du prix souhaité
            startingPrice = desiredPrice.multiply(
                    BigDecimal.ONE.subtract(DEFAULT_STARTING_PRICE_REDUCTION));
        }

        auction.setStartingPrice(startingPrice);
        auction.setCurrentPrice(startingPrice);

        // Durée par défaut : 1 semaine
        LocalDateTime now = LocalDateTime.now();
        auction.setStartDate(now);
        auction.setEndDate(now.plusDays(DEFAULT_AUCTION_DURATION_DAYS));

        auction.setIsActive(true);
        auction.setReservePriceMet(false);
        auction.setTotalBids(0);

        return auctionRepository.save(auction);
    }

    /**
     * Récupère toutes les enchères
     */
    public List<AuctionResponse> getAllAuctions() {
        return auctionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une enchère par son ID
     */
    public AuctionResponse getAuctionById(Long id) {
        return auctionRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new AuctionException("Auction not found with id: " + id));
    }

    /**
     * Récupère les enchères actives uniquement
     */
    public List<AuctionResponse> getActiveAuctions() {
        return auctionRepository.findActiveAuctions().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les enchères clôturées
     */
    public List<AuctionResponse> getClosedAuctions() {
        return auctionRepository.findClosedAuctions().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour une enchère existante
     */
    public AuctionResponse updateAuction(Long id, AuctionDTO auctionDTO) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionException("Auction not found with id: " + id));

        if (auctionDTO.getStartingPrice() != null &&
                auctionDTO.getStartingPrice().compareTo(BigDecimal.ZERO) > 0) {
            auction.setStartingPrice(auctionDTO.getStartingPrice());
        }
        if (auctionDTO.getBidIncrement() != null &&
                auctionDTO.getBidIncrement().compareTo(BigDecimal.ZERO) > 0) {
            auction.setBidIncrement(auctionDTO.getBidIncrement());
        }

        Auction updated = auctionRepository.save(auction);
        return convertToResponse(updated);
    }

    /**
     * Clôture une enchère
     */
    public void closeAuction(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionException("Auction not found with id: " + id));

        if (!auction.getIsActive()) {
            throw new AuctionException("Auction is already closed");
        }

        auction.setIsActive(false);
        auctionRepository.save(auction);
    }

    /**
     * Vérifie si le prix de réserve a été atteint
     */
    public boolean isReservePriceMet(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException("Auction not found with id: " + auctionId));
        return auction.getCurrentPrice().compareTo(auction.getReservePrice()) >= 0;
    }

    /**
     * Met à jour le statut du prix de réserve
     */
    public void updateReservePriceStatus(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException("Auction not found with id: " + auctionId));
        auction.setReservePriceMet(isReservePriceMet(auctionId));
        auctionRepository.save(auction);
    }

    /**
     * Supprime une enchère
     */
    public void deleteAuction(Long id) {
        if (!auctionRepository.existsById(id)) {
            throw new AuctionException("Auction not found with id: " + id);
        }
        auctionRepository.deleteById(id);
    }

    /**
     * Convertit une entité Auction en Response DTO
     */
    private AuctionResponse convertToResponse(Auction auction) {
        return new AuctionResponse(
                auction.getId(),
                auction.getProduct().getId(),
                auction.getReservePrice(),
                auction.getStartingPrice(),
                auction.getReservePrice(),
                auction.getCurrentPrice(),
                auction.getBidIncrement(),
                auction.getStartDate(),
                auction.getEndDate(),
                auction.getIsActive(),
                auction.getReservePriceMet(),
                auction.getCurrentWinner() != null ? auction.getCurrentWinner().getId() : null,
                auction.getTotalBids(),
                auction.getCreatedAt());
    }
}

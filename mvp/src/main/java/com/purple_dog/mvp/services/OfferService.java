package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.OfferRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.QuickSaleRepository;
import com.purple_dog.mvp.dto.OfferCreateDTO;
import com.purple_dog.mvp.dto.OfferResponseDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
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
public class OfferService {

    private final OfferRepository offerRepository;
    private final QuickSaleRepository quickSaleRepository;
    private final PersonRepository personRepository;
    private final EmailSenderService emailSenderService;

    /**
     * Créer une offre
     */
    public OfferResponseDTO createOffer(OfferCreateDTO dto) {
        log.info("Creating offer for quick sale: {}", dto.getQuickSaleId());

        QuickSale quickSale = quickSaleRepository.findByIdWithDetails(dto.getQuickSaleId())
                .orElseThrow(() -> new ResourceNotFoundException("Quick sale not found with id: " + dto.getQuickSaleId()));

        if (!quickSale.getIsAvailable()) {
            throw new InvalidOperationException("Quick sale is no longer available");
        }

        if (!quickSale.getAcceptOffers()) {
            throw new InvalidOperationException("This quick sale does not accept offers");
        }

        Person buyer = personRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with id: " + dto.getBuyerId()));

        if (quickSale.getProduct().getSeller().getId().equals(dto.getBuyerId())) {
            throw new InvalidOperationException("Seller cannot make an offer on their own product");
        }

        if (quickSale.getMinimumOfferPrice() != null &&
            dto.getAmount().compareTo(quickSale.getMinimumOfferPrice()) < 0) {
            throw new InvalidOperationException("Offer amount is below minimum offer price");
        }

        if (dto.getAmount().compareTo(quickSale.getFixedPrice()) > 0) {
            throw new InvalidOperationException("Offer amount cannot exceed fixed price");
        }

        Offer offer = Offer.builder()
                .quickSale(quickSale)
                .buyer(buyer)
                .amount(dto.getAmount())
                .message(dto.getMessage())
                .status(OfferStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        offer = offerRepository.save(offer);
        log.info("Offer created successfully with id: {}", offer.getId());

        try {
            sendOfferNotificationToSeller(offer);
        } catch (Exception e) {
            log.error("Failed to send offer notification: {}", e.getMessage());
        }

        return mapToResponseDTO(offer);
    }

    /**
     * Récupérer une offre par ID
     */
    public OfferResponseDTO getOfferById(Long offerId) {
        log.info("Fetching offer: {}", offerId);

        Offer offer = offerRepository.findByIdWithDetails(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + offerId));

        return mapToResponseDTO(offer);
    }

    /**
     * Récupérer les offres d'une vente rapide
     */
    public List<OfferResponseDTO> getQuickSaleOffers(Long quickSaleId) {
        log.info("Fetching offers for quick sale: {}", quickSaleId);

        return offerRepository.findByQuickSaleIdOrderByCreatedAtDesc(quickSaleId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les offres d'un acheteur
     */
    public List<OfferResponseDTO> getBuyerOffers(Long buyerId) {
        log.info("Fetching offers for buyer: {}", buyerId);

        return offerRepository.findByBuyerId(buyerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les offres reçues par un vendeur
     */
    public List<OfferResponseDTO> getSellerOffers(Long sellerId) {
        log.info("Fetching offers for seller: {}", sellerId);

        return offerRepository.findBySellerId(sellerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les offres par statut
     */
    public List<OfferResponseDTO> getOffersByStatus(OfferStatus status) {
        log.info("Fetching offers with status: {}", status);

        return offerRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Accepter une offre
     */
    public OfferResponseDTO acceptOffer(Long offerId) {
        log.info("Accepting offer: {}", offerId);

        Offer offer = offerRepository.findByIdWithDetails(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + offerId));

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new InvalidOperationException("Only pending offers can be accepted");
        }

        if (!offer.getQuickSale().getIsAvailable()) {
            throw new InvalidOperationException("Quick sale is no longer available");
        }

        offer.setStatus(OfferStatus.ACCEPTED);
        offer.setRespondedAt(LocalDateTime.now());

        List<Offer> otherOffers = offerRepository.findByQuickSaleIdAndStatus(
                offer.getQuickSale().getId(), OfferStatus.PENDING);

        for (Offer otherOffer : otherOffers) {
            if (!otherOffer.getId().equals(offerId)) {
                otherOffer.setStatus(OfferStatus.REJECTED);
                otherOffer.setRespondedAt(LocalDateTime.now());
            }
        }

        offerRepository.saveAll(otherOffers);
        offer = offerRepository.save(offer);

        log.info("Offer accepted successfully");

        try {
            sendOfferAcceptedEmail(offer);
        } catch (Exception e) {
            log.error("Failed to send acceptance email: {}", e.getMessage());
        }

        return mapToResponseDTO(offer);
    }

    /**
     * Rejeter une offre
     */
    public OfferResponseDTO rejectOffer(Long offerId) {
        log.info("Rejecting offer: {}", offerId);

        Offer offer = offerRepository.findByIdWithDetails(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + offerId));

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new InvalidOperationException("Only pending offers can be rejected");
        }

        offer.setStatus(OfferStatus.REJECTED);
        offer.setRespondedAt(LocalDateTime.now());

        offer = offerRepository.save(offer);
        log.info("Offer rejected successfully");

        try {
            sendOfferRejectedEmail(offer);
        } catch (Exception e) {
            log.error("Failed to send rejection email: {}", e.getMessage());
        }

        return mapToResponseDTO(offer);
    }

    /**
     * Annuler une offre (par l'acheteur)
     */
    public OfferResponseDTO cancelOffer(Long offerId) {
        log.info("Cancelling offer: {}", offerId);

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + offerId));

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new InvalidOperationException("Only pending offers can be cancelled");
        }

        offer.setStatus(OfferStatus.CANCELLED);
        offer.setRespondedAt(LocalDateTime.now());

        offer = offerRepository.save(offer);
        log.info("Offer cancelled successfully");

        return mapToResponseDTO(offer);
    }

    /**
     * Compter les offres d'un acheteur
     */
    public long countBuyerOffers(Long buyerId) {
        return offerRepository.countByBuyerId(buyerId);
    }

    /**
     * Compter les offres en attente d'une vente rapide
     */
    public long countPendingOffers(Long quickSaleId) {
        return offerRepository.countPendingByQuickSaleId(quickSaleId);
    }

    private void sendOfferNotificationToSeller(Offer offer) {
        Person seller = offer.getQuickSale().getProduct().getSeller();
        Person buyer = offer.getBuyer();

        emailSenderService.sendSimpleEmail(
            seller.getEmail(),
            "Nouvelle offre sur " + offer.getQuickSale().getProduct().getTitle(),
            String.format(
                "Bonjour %s,\n\n" +
                "Vous avez reçu une nouvelle offre de %s %s pour votre produit '%s'.\n\n" +
                "Montant de l'offre : %.2f €\n" +
                "Message : %s\n\n" +
                "Connectez-vous pour accepter ou rejeter cette offre.\n\n" +
                "Cordialement,\n" +
                "L'équipe Purple Dog",
                seller.getFirstName(),
                buyer.getFirstName(),
                buyer.getLastName(),
                offer.getQuickSale().getProduct().getTitle(),
                offer.getAmount(),
                offer.getMessage() != null ? offer.getMessage() : "Aucun message"
            )
        );
    }

    private void sendOfferAcceptedEmail(Offer offer) {
        Person buyer = offer.getBuyer();

        emailSenderService.sendSimpleEmail(
            buyer.getEmail(),
            "Votre offre a été acceptée !",
            String.format(
                "Bonjour %s,\n\n" +
                "Bonne nouvelle ! Votre offre de %.2f € pour '%s' a été acceptée.\n\n" +
                "Vous pouvez maintenant procéder au paiement.\n\n" +
                "Cordialement,\n" +
                "L'équipe Purple Dog",
                buyer.getFirstName(),
                offer.getAmount(),
                offer.getQuickSale().getProduct().getTitle()
            )
        );
    }

    private void sendOfferRejectedEmail(Offer offer) {
        Person buyer = offer.getBuyer();

        emailSenderService.sendSimpleEmail(
            buyer.getEmail(),
            "Votre offre a été refusée",
            String.format(
                "Bonjour %s,\n\n" +
                "Malheureusement, votre offre de %.2f € pour '%s' a été refusée.\n\n" +
                "Vous pouvez faire une nouvelle offre ou acheter au prix fixe.\n\n" +
                "Cordialement,\n" +
                "L'équipe Purple Dog",
                buyer.getFirstName(),
                offer.getAmount(),
                offer.getQuickSale().getProduct().getTitle()
            )
        );
    }

    private OfferResponseDTO mapToResponseDTO(Offer offer) {
        QuickSale quickSale = offer.getQuickSale();
        Product product = quickSale.getProduct();
        Person buyer = offer.getBuyer();
        Person seller = product.getSeller();

        return OfferResponseDTO.builder()
                .id(offer.getId())
                .quickSaleId(quickSale.getId())
                .productTitle(product.getTitle())
                .fixedPrice(quickSale.getFixedPrice())
                .buyerId(buyer.getId())
                .buyerName(buyer.getFirstName() + " " + buyer.getLastName())
                .buyerEmail(buyer.getEmail())
                .amount(offer.getAmount())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .createdAt(offer.getCreatedAt())
                .respondedAt(offer.getRespondedAt())
                .sellerId(seller.getId())
                .sellerName(seller.getFirstName() + " " + seller.getLastName())
                .build();
    }
}
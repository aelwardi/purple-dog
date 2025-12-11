package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AuctionRepository;
import com.purple_dog.mvp.dao.BidRepository;
import com.purple_dog.mvp.dao.ProfessionalRepository;
import com.purple_dog.mvp.dto.BidResponse;
import com.purple_dog.mvp.dto.PlaceBidRequest;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.BidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final ProfessionalRepository professionalRepository;

    /**
     * Calcule le palier d'enchère selon le prix actuel
     * < 100€ → paliers de 10€
     * 100-500€ → paliers de 50€
     * 500-1000€ → paliers de 100€
     * 1000-5000€ → paliers de 200€
     * > 5000€ → paliers de 500€
     */
    public BigDecimal calculateBidIncrement(BigDecimal currentPrice) {
        if (currentPrice.compareTo(new BigDecimal("100")) < 0) {
            return new BigDecimal("10");
        } else if (currentPrice.compareTo(new BigDecimal("500")) < 0) {
            return new BigDecimal("50");
        } else if (currentPrice.compareTo(new BigDecimal("1000")) < 0) {
            return new BigDecimal("100");
        } else if (currentPrice.compareTo(new BigDecimal("5000")) < 0) {
            return new BigDecimal("200");
        } else {
            return new BigDecimal("500");
        }
    }

    /**
     * Place une enchère (manuelle ou automatique)
     */
    public BidResponse placeBid(PlaceBidRequest request) {
        // Validation
        if (request.getAuctionId() == null || request.getBidderId() == null || request.getAmount() == null) {
            throw new BidException("Auction ID, Bidder ID and Amount are required");
        }

        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new BidException("Auction not found with id: " + request.getAuctionId()));

        Professional bidder = professionalRepository.findById(request.getBidderId())
                .orElseThrow(() -> new BidException("Professional not found with id: " + request.getBidderId()));

        // Vérifier que l'enchère est active
        if (auction.getStatus() != AuctionStatus.ACTIVE && auction.getStatus() != AuctionStatus.EXTENDED) {
            throw new BidException("Auction is not active. Current status: " + auction.getStatus());
        }

        // Vérifier que l'enchère n'est pas terminée
        if (LocalDateTime.now().isAfter(auction.getEndDate())) {
            throw new BidException("Auction has ended");
        }

        // Vérifier que le vendeur ne peut pas enchérir sur sa propre enchère
        if (auction.getProduct().getSeller().getId().equals(bidder.getId())) {
            throw new BidException("Seller cannot bid on their own auction");
        }

        BigDecimal currentPrice = auction.getCurrentPrice();
        BigDecimal minBidIncrement = calculateBidIncrement(currentPrice);
        BigDecimal minimumBid = currentPrice.add(minBidIncrement);

        // Vérifier que le montant respecte le palier minimum
        if (request.getAmount().compareTo(minimumBid) < 0) {
            throw new BidException(String.format(
                    "Bid amount must be at least %s (current price: %s + increment: %s)",
                    minimumBid, currentPrice, minBidIncrement));
        }

        // Gérer les enchères automatiques existantes
        List<Bid> existingBids = bidRepository.findByAuctionIdOrderByAmountDesc(auction.getId());

        // Trouver l'enchère gagnante actuelle
        Bid currentWinningBid = existingBids.stream()
                .filter(Bid::getIsWinning)
                .findFirst()
                .orElse(null);

        // Créer la nouvelle enchère
        Bid newBid = new Bid();
        newBid.setAuction(auction);
        newBid.setBidder(bidder);
        newBid.setAmount(request.getAmount());
        newBid.setMaxAmount(request.getMaxAmount());
        newBid.setIsAutoBid(false);
        newBid.setBidDate(LocalDateTime.now());
        newBid.setIsWinning(false);

        // Logique des enchères automatiques
        if (currentWinningBid != null && currentWinningBid.getMaxAmount() != null) {
            // Il y a une enchère automatique en cours
            BigDecimal competitorMaxAmount = currentWinningBid.getMaxAmount();

            if (request.getMaxAmount() != null) {
                // Bataille d'enchères automatiques
                if (request.getMaxAmount().compareTo(competitorMaxAmount) > 0) {
                    // Le nouvel enchérisseur a un max plus élevé
                    BigDecimal newWinningAmount = competitorMaxAmount.add(minBidIncrement);
                    newBid.setAmount(newWinningAmount);
                    newBid.setIsWinning(true);
                    currentWinningBid.setIsWinning(false);
                } else if (request.getMaxAmount().compareTo(competitorMaxAmount) < 0) {
                    // L'enchérisseur actuel a un max plus élevé
                    BigDecimal competitorNewAmount = request.getMaxAmount().add(minBidIncrement);

                    // Créer une contre-enchère automatique
                    Bid autoBid = new Bid();
                    autoBid.setAuction(auction);
                    autoBid.setBidder(currentWinningBid.getBidder());
                    autoBid.setAmount(competitorNewAmount);
                    autoBid.setMaxAmount(competitorMaxAmount);
                    autoBid.setIsAutoBid(true);
                    autoBid.setBidDate(LocalDateTime.now());
                    autoBid.setIsWinning(true);

                    currentWinningBid.setIsWinning(false);
                    bidRepository.save(autoBid);
                } else {
                    // Montants max égaux - premier arrivé gagne
                    newBid.setIsWinning(true);
                    currentWinningBid.setIsWinning(false);
                }
            } else {
                // Enchère manuelle contre enchère automatique
                if (request.getAmount().compareTo(competitorMaxAmount) >= 0) {
                    // L'enchère manuelle dépasse le max automatique
                    newBid.setIsWinning(true);
                    currentWinningBid.setIsWinning(false);
                } else {
                    // L'enchère automatique contre-attaque
                    BigDecimal competitorNewAmount = request.getAmount().add(minBidIncrement);
                    if (competitorNewAmount.compareTo(competitorMaxAmount) <= 0) {
                        Bid autoBid = new Bid();
                        autoBid.setAuction(auction);
                        autoBid.setBidder(currentWinningBid.getBidder());
                        autoBid.setAmount(competitorNewAmount);
                        autoBid.setMaxAmount(competitorMaxAmount);
                        autoBid.setIsAutoBid(true);
                        autoBid.setBidDate(LocalDateTime.now());
                        autoBid.setIsWinning(true);

                        currentWinningBid.setIsWinning(false);
                        bidRepository.save(autoBid);
                        auction.setCurrentPrice(competitorNewAmount);
                    } else {
                        newBid.setIsWinning(true);
                        currentWinningBid.setIsWinning(false);
                    }
                }
            }
        } else {
            // Pas d'enchère automatique en cours
            newBid.setIsWinning(true);
            if (currentWinningBid != null) {
                currentWinningBid.setIsWinning(false);
            }
        }

        // Sauvegarder l'enchère
        Bid savedBid = bidRepository.save(newBid);

        // Mettre à jour l'enchère
        if (newBid.getIsWinning()) {
            auction.setCurrentPrice(newBid.getAmount());
            auction.setWinner(bidder);
        }
        auction.setTotalBids(auction.getTotalBids() + 1);

        // Vérifier si le prix de réserve est atteint
        if (!auction.getReservePriceMet() &&
                auction.getCurrentPrice().compareTo(auction.getReservePrice()) >= 0) {
            auction.setReservePriceMet(true);
        }

        // Gérer l'extension automatique (bataille à H-1)
        if (auction.getIsAutoExtendEnabled()) {
            long minutesUntilEnd = ChronoUnit.MINUTES.between(LocalDateTime.now(), auction.getEndDate());
            if (minutesUntilEnd <= 60 && minutesUntilEnd > 0) {
                // Prolonger de 10 minutes
                auction.setEndDate(auction.getEndDate().plusMinutes(10));
                auction.setStatus(AuctionStatus.EXTENDED);
            }
        }

        auctionRepository.save(auction);

        return toBidResponse(savedBid);
    }

    /**
     * Récupère toutes les enchères d'une auction
     */
    public List<BidResponse> getAuctionBids(Long auctionId) {
        List<Bid> bids = bidRepository.findByAuctionIdOrderByAmountDesc(auctionId);
        return bids.stream()
                .map(this::toBidResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les enchères d'un professionnel
     */
    public List<BidResponse> getBidderBids(Long bidderId) {
        List<Bid> bids = bidRepository.findByBidderIdOrderByBidDateDesc(bidderId);
        return bids.stream()
                .map(this::toBidResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère l'enchère gagnante actuelle
     */
    public BidResponse getCurrentWinningBid(Long auctionId) {
        return bidRepository.findCurrentWinningBid(auctionId)
                .map(this::toBidResponse)
                .orElse(null);
    }

    /**
     * Calcule le prochain montant d'enchère suggéré
     */
    public BigDecimal getNextBidAmount(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BidException("Auction not found"));

        BigDecimal currentPrice = auction.getCurrentPrice();
        BigDecimal increment = calculateBidIncrement(currentPrice);
        return currentPrice.add(increment);
    }

    private BidResponse toBidResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setAuctionId(bid.getAuction().getId());
        response.setBidderId(bid.getBidder().getId());
        response.setBidderName(bid.getBidder().getFirstName() + " " + bid.getBidder().getLastName());
        response.setAmount(bid.getAmount());
        response.setMaxAmount(bid.getMaxAmount());
        response.setIsAutoBid(bid.getIsAutoBid());
        response.setIsWinning(bid.getIsWinning());
        response.setBidDate(bid.getBidDate());
        return response;
    }
}

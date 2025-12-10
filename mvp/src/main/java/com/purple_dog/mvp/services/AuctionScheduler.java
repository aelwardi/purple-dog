package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AuctionRepository;
import com.purple_dog.mvp.entities.Auction;
import com.purple_dog.mvp.entities.AuctionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;

    /**
     * Clôture automatiquement les enchères expirées
     * S'exécute toutes les heures
     */
    @Scheduled(fixedDelay = 3600000) // 1 heure en millisecondes
    public void closeExpiredAuctions() {
        log.info("Starting scheduled task to close expired auctions");

        LocalDateTime now = LocalDateTime.now();
        List<Auction> auctions = auctionRepository.findAll();

        auctions.stream()
                .filter(a -> a.getStatus() == AuctionStatus.ACTIVE || a.getStatus() == AuctionStatus.EXTENDED)
                .filter(a -> a.getEndDate().isBefore(now))
                .forEach(auction -> {
                    // Déterminer le statut final
                    if (auction.getReservePriceMet()) {
                        auction.setStatus(AuctionStatus.SOLD);
                        log.info("Auction {} has been automatically closed and SOLD", auction.getId());
                    } else {
                        auction.setStatus(AuctionStatus.UNSOLD);
                        log.info("Auction {} has been automatically closed and UNSOLD (reserve price not met)",
                                auction.getId());
                    }
                    auctionRepository.save(auction);
                });

        log.info("Scheduled task to close expired auctions completed");
    }
}

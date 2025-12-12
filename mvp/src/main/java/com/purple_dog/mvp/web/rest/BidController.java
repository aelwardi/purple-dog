package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.BidResponse;
import com.purple_dog.mvp.dto.PlaceBidRequest;
import com.purple_dog.mvp.exceptions.BidException;
import com.purple_dog.mvp.services.BidService;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Bid Management", description = "APIs for managing bids")
@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
@Slf4j
public class BidController {

    private final BidService bidService;
    private final PersonRepository personRepository;

    /**
     * Place une enchère sur une auction
     * POST /api/bids
     */
    @PostMapping
    public ResponseEntity<?> placeBid(@RequestBody PlaceBidRequest request, Principal principal) {
        try {
            log.info("POST /bids called - auctionId={}, bidderId(incoming)={}, principal={}", request.getAuctionId(), request.getBidderId(), principal==null?"null":principal.getName());
            // If bidderId not provided, attempt to resolve from authenticated user
            if (request.getBidderId() == null) {
                if (principal == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication required to place a bid");
                }
                String username = principal.getName();
                Optional<Person> personOpt = personRepository.findByEmail(username);
                if (personOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authenticated user not found");
                }
                Person p = personOpt.get();
                if (p.getRole() == null || p.getRole() != UserRole.PROFESSIONAL) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only professionals can place bids");
                }
                request.setBidderId(p.getId());
            }

            BidResponse response = bidService.placeBid(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Récupère toutes les enchères d'une auction
     * GET /api/bids/auction/{auctionId}
     */
    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidResponse>> getAuctionBids(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getAuctionBids(auctionId));
    }

    /**
     * Récupère toutes les enchères d'un professionnel
     * GET /api/bids/bidder/{bidderId}
     */
    @GetMapping("/bidder/{bidderId}")
    public ResponseEntity<List<BidResponse>> getBidderBids(@PathVariable Long bidderId) {
        return ResponseEntity.ok(bidService.getBidderBids(bidderId));
    }

    /**
     * Récupère l'enchère gagnante actuelle d'une auction
     * GET /api/bids/auction/{auctionId}/winning
     */
    @GetMapping("/auction/{auctionId}/winning")
    public ResponseEntity<?> getCurrentWinningBid(@PathVariable Long auctionId) {
        BidResponse winningBid = bidService.getCurrentWinningBid(auctionId);
        if (winningBid != null) {
            return ResponseEntity.ok(winningBid);
        }
        return ResponseEntity.ok("No winning bid yet");
    }

    /**
     * Récupère le prochain montant d'enchère suggéré
     * GET /api/bids/auction/{auctionId}/next-amount
     */
    @GetMapping("/auction/{auctionId}/next-amount")
    public ResponseEntity<BigDecimal> getNextBidAmount(@PathVariable Long auctionId) {
        try {
            return ResponseEntity.ok(bidService.getNextBidAmount(auctionId));
        } catch (BidException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

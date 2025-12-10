package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.PlatformResponseDTO;
import com.purple_dog.mvp.dto.PlatformUpdateDTO;
import com.purple_dog.mvp.entities.PlatformStatus;
import com.purple_dog.mvp.services.PlatformService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/platform")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PlatformController {

    private final PlatformService platformService;

    /**
     * Récupérer les informations publiques de la plateforme
     * PUBLIC - Accessible à tous
     */
    @GetMapping
    public ResponseEntity<PlatformResponseDTO> getPlatformInfo() {
        log.info("Request to get platform information (PUBLIC)");
        PlatformResponseDTO platform = platformService.getPlatformInfo();
        return ResponseEntity.ok(platform);
    }

    /**
     * Mettre à jour les informations de la plateforme
     * ADMIN ONLY
     */
    @PutMapping("/admin")
    public ResponseEntity<PlatformResponseDTO> updatePlatform(
            @Valid @RequestBody PlatformUpdateDTO dto) {

        log.info("Request to update platform information (ADMIN)");
        PlatformResponseDTO response = platformService.updatePlatform(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Mettre à jour les statistiques de la plateforme
     * ADMIN ONLY
     */
    @PatchMapping("/admin/statistics")
    public ResponseEntity<Void> updateStatistics(
            @RequestParam(required = false) Long totalUsers,
            @RequestParam(required = false) Long totalProducts,
            @RequestParam(required = false) Long totalTransactions,
            @RequestParam(required = false) BigDecimal totalRevenue) {

        log.info("Request to update platform statistics (ADMIN)");
        platformService.updateStatistics(totalUsers, totalProducts, totalTransactions, totalRevenue);
        return ResponseEntity.ok().build();
    }

    /**
     * Mettre à jour le statut de la plateforme
     * ADMIN ONLY
     */
    @PatchMapping("/admin/status")
    public ResponseEntity<Void> updateStatus(@RequestParam PlatformStatus status) {
        log.info("Request to update platform status to: {} (ADMIN)", status);
        platformService.updateStatus(status);
        return ResponseEntity.ok().build();
    }
}


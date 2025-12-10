package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.AlertCreateDTO;
import com.purple_dog.mvp.dto.AlertResponseDTO;
import com.purple_dog.mvp.dto.AlertUpdateDTO;
import com.purple_dog.mvp.services.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    /**
     * Créer une alerte
     */
    @PostMapping
    public ResponseEntity<AlertResponseDTO> createAlert(@Valid @RequestBody AlertCreateDTO dto) {
        log.info("Request to create alert for user: {}", dto.getUserId());
        AlertResponseDTO response = alertService.createAlert(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer une alerte par ID
     */
    @GetMapping("/{alertId}")
    public ResponseEntity<AlertResponseDTO> getAlertById(@PathVariable Long alertId) {
        log.info("Request to get alert: {}", alertId);
        AlertResponseDTO alert = alertService.getAlertById(alertId);
        return ResponseEntity.ok(alert);
    }

    /**
     * Récupérer les alertes d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AlertResponseDTO>> getUserAlerts(@PathVariable Long userId) {
        log.info("Request to get alerts for user: {}", userId);
        List<AlertResponseDTO> alerts = alertService.getUserAlerts(userId);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Récupérer les alertes actives d'un utilisateur
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<AlertResponseDTO>> getActiveUserAlerts(@PathVariable Long userId) {
        log.info("Request to get active alerts for user: {}", userId);
        List<AlertResponseDTO> alerts = alertService.getActiveUserAlerts(userId);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Mettre à jour une alerte
     */
    @PutMapping("/{alertId}")
    public ResponseEntity<AlertResponseDTO> updateAlert(
            @PathVariable Long alertId,
            @Valid @RequestBody AlertUpdateDTO dto) {

        log.info("Request to update alert: {}", alertId);
        AlertResponseDTO response = alertService.updateAlert(alertId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Activer/Désactiver une alerte
     */
    @PatchMapping("/{alertId}/toggle")
    public ResponseEntity<AlertResponseDTO> toggleAlert(@PathVariable Long alertId) {
        log.info("Request to toggle alert: {}", alertId);
        AlertResponseDTO response = alertService.toggleAlert(alertId);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer une alerte
     */
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        log.info("Request to delete alert: {}", alertId);
        alertService.deleteAlert(alertId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Compter les alertes actives d'un utilisateur
     */
    @GetMapping("/user/{userId}/count-active")
    public ResponseEntity<Long> countActiveUserAlerts(@PathVariable Long userId) {
        log.info("Request to count active alerts for user: {}", userId);
        long count = alertService.countActiveUserAlerts(userId);
        return ResponseEntity.ok(count);
    }
}


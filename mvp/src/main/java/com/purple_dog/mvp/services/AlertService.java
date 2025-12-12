package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AlertRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.AlertCreateDTO;
import com.purple_dog.mvp.dto.AlertResponseDTO;
import com.purple_dog.mvp.dto.AlertUpdateDTO;
import com.purple_dog.mvp.entities.Alert;
import com.purple_dog.mvp.entities.Category;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.Product;
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
public class AlertService {

    private final AlertRepository alertRepository;
    private final PersonRepository personRepository;
    private final InAppNotificationService inAppNotificationService;

    /**
     * Créer une alerte
     */
    public AlertResponseDTO createAlert(AlertCreateDTO dto) {
        log.info("Creating alert for user: {}", dto.getUserId());

        Person user = personRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        if (dto.getMinPrice() != null && dto.getMaxPrice() != null) {
            if (dto.getMinPrice().compareTo(dto.getMaxPrice()) > 0) {
                throw new InvalidOperationException("Minimum price cannot be greater than maximum price");
            }
        }

        Category category = new Category();
        category.setId(dto.getCategoryId());

        Alert alert = Alert.builder()
                .user(user)
                .category(category)
                .keywords(dto.getKeywords())
                .minPrice(dto.getMinPrice())
                .maxPrice(dto.getMaxPrice())
                .condition(dto.getCondition())
                .active(true)
                .emailNotification(dto.getEmailNotification() != null ? dto.getEmailNotification() : true)
                .inAppNotification(dto.getInAppNotification() != null ? dto.getInAppNotification() : true)
                .createdAt(LocalDateTime.now())
                .build();

        alert = alertRepository.save(alert);
        log.info("Alert created successfully with id: {}", alert.getId());

        return mapToResponseDTO(alert);
    }

    /**
     * Récupérer une alerte par ID
     */
    public AlertResponseDTO getAlertById(Long alertId) {
        log.info("Fetching alert: {}", alertId);

        Alert alert = alertRepository.findByIdWithDetails(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + alertId));

        return mapToResponseDTO(alert);
    }

    /**
     * Récupérer les alertes d'un utilisateur
     */
    public List<AlertResponseDTO> getUserAlerts(Long userId) {
        log.info("Fetching alerts for user: {}", userId);

        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les alertes actives d'un utilisateur
     */
    public List<AlertResponseDTO> getActiveUserAlerts(Long userId) {
        log.info("Fetching active alerts for user: {}", userId);

        return alertRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour une alerte
     */
    public AlertResponseDTO updateAlert(Long alertId, AlertUpdateDTO dto) {
        log.info("Updating alert: {}", alertId);

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + alertId));

        if (dto.getKeywords() != null) {
            alert.setKeywords(dto.getKeywords());
        }

        if (dto.getMinPrice() != null) {
            alert.setMinPrice(dto.getMinPrice());
        }

        if (dto.getMaxPrice() != null) {
            alert.setMaxPrice(dto.getMaxPrice());
        }

        if (alert.getMinPrice() != null && alert.getMaxPrice() != null) {
            if (alert.getMinPrice().compareTo(alert.getMaxPrice()) > 0) {
                throw new InvalidOperationException("Minimum price cannot be greater than maximum price");
            }
        }

        if (dto.getCondition() != null) {
            alert.setCondition(dto.getCondition());
        }

        if (dto.getActive() != null) {
            alert.setActive(dto.getActive());
        }

        if (dto.getEmailNotification() != null) {
            alert.setEmailNotification(dto.getEmailNotification());
        }

        if (dto.getInAppNotification() != null) {
            alert.setInAppNotification(dto.getInAppNotification());
        }

        alert = alertRepository.save(alert);
        log.info("Alert updated successfully");

        return mapToResponseDTO(alert);
    }

    /**
     * Activer/Désactiver une alerte
     */
    public AlertResponseDTO toggleAlert(Long alertId) {
        log.info("Toggling alert: {}", alertId);

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + alertId));

        alert.setActive(!alert.getActive());
        alert = alertRepository.save(alert);

        log.info("Alert {} {}", alertId, alert.getActive() ? "activated" : "deactivated");

        return mapToResponseDTO(alert);
    }

    /**
     * Supprimer une alerte
     */
    public void deleteAlert(Long alertId) {
        log.info("Deleting alert: {}", alertId);

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + alertId));

        alertRepository.delete(alert);
        log.info("Alert deleted successfully");
    }

    /**
     * Vérifier si un produit correspond à une alerte et notifier
     */
    public void checkProductAgainstAlerts(Product product) {
        log.info("Checking product {} against alerts", product.getId());

        List<Alert> activeAlerts = alertRepository.findByActiveTrue();

        for (Alert alert : activeAlerts) {
            if (productMatchesAlert(product, alert)) {
                log.info("Product matches alert {}", alert.getId());

                alert.setLastTriggeredAt(LocalDateTime.now());
                alertRepository.save(alert);

                // Créer une notification in-app
                if (alert.getInAppNotification()) {
                    try {
                        inAppNotificationService.createAlertMatchNotification(
                            alert.getUser().getId(),
                            product.getId(),
                            product.getTitle(),
                            alert.getId()
                        );
                        log.info("✅ Alert match notification created for user {}", alert.getUser().getId());
                    } catch (Exception e) {
                        log.error("❌ Failed to create alert match notification: {}", e.getMessage());
                    }
                }

                if (alert.getEmailNotification()) {
                    sendAlertEmail(alert, product);
                }
            }
        }
    }

    /**
     * Compter les alertes actives d'un utilisateur
     */
    public long countActiveUserAlerts(Long userId) {
        return alertRepository.countActiveByUserId(userId);
    }

    private boolean productMatchesAlert(Product product, Alert alert) {
        if (!product.getCategory().getId().equals(alert.getCategory().getId())) {
            return false;
        }

        if (alert.getKeywords() != null && !alert.getKeywords().isEmpty()) {
            String keywords = alert.getKeywords().toLowerCase();
            String title = product.getTitle().toLowerCase();
            String description = product.getDescription().toLowerCase();

            if (!title.contains(keywords) && !description.contains(keywords)) {
                return false;
            }
        }

        if (product.getEstimatedValue() != null) {
            if (alert.getMinPrice() != null && product.getEstimatedValue().compareTo(alert.getMinPrice()) < 0) {
                return false;
            }
            if (alert.getMaxPrice() != null && product.getEstimatedValue().compareTo(alert.getMaxPrice()) > 0) {
                return false;
            }
        }

        if (alert.getCondition() != null && !alert.getCondition().equals(product.getProductCondition())) {
            return false;
        }

        return true;
    }

    private void sendAlertEmail(Alert alert, Product product) {
        // TODO: Implémenter avec EmailSenderService
        log.info("Sending alert email to user {} for product {}", alert.getUser().getId(), product.getId());
    }

    private AlertResponseDTO mapToResponseDTO(Alert alert) {
        return AlertResponseDTO.builder()
                .id(alert.getId())
                .userId(alert.getUser().getId())
                .categoryId(alert.getCategory().getId())
                .categoryName(alert.getCategory().getName())
                .keywords(alert.getKeywords())
                .minPrice(alert.getMinPrice())
                .maxPrice(alert.getMaxPrice())
                .condition(alert.getCondition())
                .active(alert.getActive())
                .emailNotification(alert.getEmailNotification())
                .inAppNotification(alert.getInAppNotification())
                .createdAt(alert.getCreatedAt())
                .lastTriggeredAt(alert.getLastTriggeredAt())
                .build();
    }
}


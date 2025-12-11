package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.CarrierCreateDTO;
import com.purple_dog.mvp.dto.CarrierDTO;
import com.purple_dog.mvp.dto.CarrierUpdateDTO;
import com.purple_dog.mvp.services.CarrierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Carrier Management", description = "APIs for managing carriers")
@RestController
@RequestMapping("/carriers")
@RequiredArgsConstructor
@Slf4j
public class CarrierController {

    private final CarrierService carrierService;

    /**
     * Créer un nouveau transporteur
     */
    @PostMapping
    public ResponseEntity<CarrierDTO> createCarrier(@Valid @RequestBody CarrierCreateDTO dto) {
        log.info("Request to create carrier: {}", dto.getName());
        CarrierDTO response = carrierService.createCarrier(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer tous les transporteurs
     */
    @GetMapping
    public ResponseEntity<List<CarrierDTO>> getAllCarriers() {
        log.info("Request to get all carriers");
        List<CarrierDTO> carriers = carrierService.getAllCarriers();
        return ResponseEntity.ok(carriers);
    }

    /**
     * Récupérer les transporteurs actifs
     */
    @GetMapping("/active")
    public ResponseEntity<List<CarrierDTO>> getActiveCarriers() {
        log.info("Request to get active carriers");
        List<CarrierDTO> carriers = carrierService.getActiveCarriers();
        return ResponseEntity.ok(carriers);
    }

    /**
     * Récupérer un transporteur par ID
     */
    @GetMapping("/{carrierId}")
    public ResponseEntity<CarrierDTO> getCarrierById(@PathVariable Long carrierId) {
        log.info("Request to get carrier: {}", carrierId);
        CarrierDTO carrier = carrierService.getCarrierById(carrierId);
        return ResponseEntity.ok(carrier);
    }

    /**
     * Récupérer un transporteur par code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<CarrierDTO> getCarrierByCode(@PathVariable String code) {
        log.info("Request to get carrier by code: {}", code);
        CarrierDTO carrier = carrierService.getCarrierByCode(code);
        return ResponseEntity.ok(carrier);
    }

    /**
     * Récupérer les transporteurs dans un budget
     */
    @GetMapping("/by-price/{maxPrice}")
    public ResponseEntity<List<CarrierDTO>> getCarriersByMaxPrice(@PathVariable BigDecimal maxPrice) {
        log.info("Request to get carriers with max price: {}", maxPrice);
        List<CarrierDTO> carriers = carrierService.getCarriersByMaxPrice(maxPrice);
        return ResponseEntity.ok(carriers);
    }

    /**
     * Mettre à jour un transporteur
     */
    @PutMapping("/{carrierId}")
    public ResponseEntity<CarrierDTO> updateCarrier(
            @PathVariable Long carrierId,
            @Valid @RequestBody CarrierUpdateDTO dto) {

        log.info("Request to update carrier: {}", carrierId);
        CarrierDTO response = carrierService.updateCarrier(carrierId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Activer/Désactiver un transporteur
     */
    @PatchMapping("/{carrierId}/toggle-active")
    public ResponseEntity<CarrierDTO> toggleCarrierActive(@PathVariable Long carrierId) {
        log.info("Request to toggle carrier active status: {}", carrierId);
        CarrierDTO response = carrierService.toggleCarrierActive(carrierId);
        return ResponseEntity.ok(response);
    }

    /**
     * Compter les livraisons d'un transporteur
     */
    @GetMapping("/{carrierId}/deliveries/count")
    public ResponseEntity<Long> countCarrierDeliveries(@PathVariable Long carrierId) {
        log.info("Request to count deliveries for carrier: {}", carrierId);
        long count = carrierService.countCarrierDeliveries(carrierId);
        return ResponseEntity.ok(count);
    }

    /**
     * Supprimer un transporteur
     */
    @DeleteMapping("/{carrierId}")
    public ResponseEntity<Void> deleteCarrier(@PathVariable Long carrierId) {
        log.info("Request to delete carrier: {}", carrierId);
        carrierService.deleteCarrier(carrierId);
        return ResponseEntity.noContent().build();
    }
}


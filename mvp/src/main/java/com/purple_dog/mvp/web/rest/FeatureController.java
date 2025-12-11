package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.FeatureCreateDTO;
import com.purple_dog.mvp.dto.FeatureDTO;
import com.purple_dog.mvp.dto.FeatureUpdateDTO;
import com.purple_dog.mvp.services.FeatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Feature Management", description = "APIs for managing features")
@RestController
@RequestMapping("/features")
@RequiredArgsConstructor
@Slf4j
public class FeatureController {

    private final FeatureService featureService;

    /**
     * Créer une nouvelle feature
     */
    @PostMapping
    public ResponseEntity<FeatureDTO> createFeature(@Valid @RequestBody FeatureCreateDTO dto) {
        log.info("Request to create feature: {}", dto.getName());
        FeatureDTO response = featureService.createFeature(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer toutes les features
     */
    @GetMapping
    public ResponseEntity<List<FeatureDTO>> getAllFeatures() {
        log.info("Request to get all features");
        List<FeatureDTO> features = featureService.getAllFeatures();
        return ResponseEntity.ok(features);
    }

    /**
     * Récupérer toutes les features actives
     */
    @GetMapping("/active")
    public ResponseEntity<List<FeatureDTO>> getActiveFeatures() {
        log.info("Request to get active features");
        List<FeatureDTO> features = featureService.getActiveFeatures();
        return ResponseEntity.ok(features);
    }

    /**
     * Récupérer une feature par ID
     */
    @GetMapping("/{featureId}")
    public ResponseEntity<FeatureDTO> getFeatureById(@PathVariable Long featureId) {
        log.info("Request to get feature: {}", featureId);
        FeatureDTO feature = featureService.getFeatureById(featureId);
        return ResponseEntity.ok(feature);
    }

    /**
     * Récupérer une feature par code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<FeatureDTO> getFeatureByCode(@PathVariable String code) {
        log.info("Request to get feature by code: {}", code);
        FeatureDTO feature = featureService.getFeatureByCode(code);
        return ResponseEntity.ok(feature);
    }

    /**
     * Récupérer les features d'un plan
     */
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<FeatureDTO>> getFeaturesByPlanId(@PathVariable Long planId) {
        log.info("Request to get features for plan: {}", planId);
        List<FeatureDTO> features = featureService.getFeaturesByPlanId(planId);
        return ResponseEntity.ok(features);
    }

    /**
     * Mettre à jour une feature
     */
    @PutMapping("/{featureId}")
    public ResponseEntity<FeatureDTO> updateFeature(
            @PathVariable Long featureId,
            @Valid @RequestBody FeatureUpdateDTO dto) {

        log.info("Request to update feature: {}", featureId);
        FeatureDTO response = featureService.updateFeature(featureId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Activer/Désactiver une feature
     */
    @PatchMapping("/{featureId}/toggle-active")
    public ResponseEntity<FeatureDTO> toggleFeatureActive(@PathVariable Long featureId) {
        log.info("Request to toggle feature active status: {}", featureId);
        FeatureDTO response = featureService.toggleFeatureActive(featureId);
        return ResponseEntity.ok(response);
    }

    /**
     * Compter les plans utilisant une feature
     */
    @GetMapping("/{featureId}/plans/count")
    public ResponseEntity<Long> countFeaturePlans(@PathVariable Long featureId) {
        log.info("Request to count plans for feature: {}", featureId);
        long count = featureService.countFeaturePlans(featureId);
        return ResponseEntity.ok(count);
    }

    /**
     * Supprimer une feature
     */
    @DeleteMapping("/{featureId}")
    public ResponseEntity<Void> deleteFeature(@PathVariable Long featureId) {
        log.info("Request to delete feature: {}", featureId);
        featureService.deleteFeature(featureId);
        return ResponseEntity.noContent().build();
    }
}

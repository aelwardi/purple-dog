package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.PlanCreateDTO;
import com.purple_dog.mvp.dto.PlanResponseDTO;
import com.purple_dog.mvp.dto.PlanUpdateDTO;
import com.purple_dog.mvp.entities.PlanType;
import com.purple_dog.mvp.services.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PlanController {

    private final PlanService planService;

    /**
     * Créer un nouveau plan
     */
    @PostMapping
    public ResponseEntity<PlanResponseDTO> createPlan(@Valid @RequestBody PlanCreateDTO dto) {
        log.info("Request to create plan: {}", dto.getName());
        PlanResponseDTO response = planService.createPlan(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer tous les plans
     */
    @GetMapping
    public ResponseEntity<List<PlanResponseDTO>> getAllPlans() {
        log.info("Request to get all plans");
        List<PlanResponseDTO> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    /**
     * Récupérer tous les plans actifs
     */
    @GetMapping("/active")
    public ResponseEntity<List<PlanResponseDTO>> getActivePlans() {
        log.info("Request to get active plans");
        List<PlanResponseDTO> plans = planService.getActivePlans();
        return ResponseEntity.ok(plans);
    }

    /**
     * Récupérer tous les plans actifs avec features
     */
    @GetMapping("/active/with-features")
    public ResponseEntity<List<PlanResponseDTO>> getActivePlansWithFeatures() {
        log.info("Request to get active plans with features");
        List<PlanResponseDTO> plans = planService.getActivePlansWithFeatures();
        return ResponseEntity.ok(plans);
    }

    /**
     * Récupérer un plan par ID
     */
    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponseDTO> getPlanById(@PathVariable Long planId) {
        log.info("Request to get plan: {}", planId);
        PlanResponseDTO plan = planService.getPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    /**
     * Récupérer un plan par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<PlanResponseDTO> getPlanByType(@PathVariable PlanType type) {
        log.info("Request to get plan by type: {}", type);
        PlanResponseDTO plan = planService.getPlanByType(type);
        return ResponseEntity.ok(plan);
    }

    /**
     * Mettre à jour un plan
     */
    @PutMapping("/{planId}")
    public ResponseEntity<PlanResponseDTO> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody PlanUpdateDTO dto) {

        log.info("Request to update plan: {}", planId);
        PlanResponseDTO response = planService.updatePlan(planId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Ajouter une feature à un plan
     */
    @PostMapping("/{planId}/features/{featureId}")
    public ResponseEntity<PlanResponseDTO> addFeatureToPlan(
            @PathVariable Long planId,
            @PathVariable Long featureId) {

        log.info("Request to add feature {} to plan {}", featureId, planId);
        PlanResponseDTO response = planService.addFeatureToPlan(planId, featureId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retirer une feature d'un plan
     */
    @DeleteMapping("/{planId}/features/{featureId}")
    public ResponseEntity<PlanResponseDTO> removeFeatureFromPlan(
            @PathVariable Long planId,
            @PathVariable Long featureId) {

        log.info("Request to remove feature {} from plan {}", featureId, planId);
        PlanResponseDTO response = planService.removeFeatureFromPlan(planId, featureId);
        return ResponseEntity.ok(response);
    }

    /**
     * Activer/Désactiver un plan
     */
    @PatchMapping("/{planId}/toggle-active")
    public ResponseEntity<PlanResponseDTO> togglePlanActive(@PathVariable Long planId) {
        log.info("Request to toggle plan active status: {}", planId);
        PlanResponseDTO response = planService.togglePlanActive(planId);
        return ResponseEntity.ok(response);
    }

    /**
     * Compter les abonnés d'un plan
     */
    @GetMapping("/{planId}/subscribers/count")
    public ResponseEntity<Long> countPlanSubscribers(@PathVariable Long planId) {
        log.info("Request to count subscribers for plan: {}", planId);
        long count = planService.countPlanSubscribers(planId);
        return ResponseEntity.ok(count);
    }

    /**
     * Supprimer un plan
     */
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long planId) {
        log.info("Request to delete plan: {}", planId);
        planService.deletePlan(planId);
        return ResponseEntity.noContent().build();
    }
}


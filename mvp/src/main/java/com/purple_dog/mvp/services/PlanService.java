package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.FeatureRepository;
import com.purple_dog.mvp.dao.PlanRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.Feature;
import com.purple_dog.mvp.entities.Plan;
import com.purple_dog.mvp.entities.PlanType;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlanService {

    private final PlanRepository planRepository;
    private final FeatureRepository featureRepository;

    /**
     * Créer un nouveau plan
     */
    public PlanResponseDTO createPlan(PlanCreateDTO dto) {
        log.info("Creating plan: {}", dto.getName());

        if (planRepository.existsByType(dto.getType())) {
            throw new DuplicateResourceException("Plan with type " + dto.getType() + " already exists");
        }

        List<Feature> features = new ArrayList<>();
        if (dto.getFeatureIds() != null && !dto.getFeatureIds().isEmpty()) {
            features = featureRepository.findAllById(dto.getFeatureIds());
        }

        Plan plan = Plan.builder()
                .type(dto.getType())
                .name(dto.getName())
                .description(dto.getDescription())
                .monthlyPrice(dto.getMonthlyPrice())
                .annualPrice(dto.getAnnualPrice())
                .maxListings(dto.getMaxListings())
                .maxPhotosPerListing(dto.getMaxPhotosPerListing())
                .featuredListings(dto.getFeaturedListings() != null ? dto.getFeaturedListings() : false)
                .prioritySupport(dto.getPrioritySupport() != null ? dto.getPrioritySupport() : false)
                .analyticsAccess(dto.getAnalyticsAccess() != null ? dto.getAnalyticsAccess() : false)
                .customBranding(dto.getCustomBranding() != null ? dto.getCustomBranding() : false)
                .commissionRate(dto.getCommissionRate())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .features(features)
                .build();

        plan = planRepository.save(plan);
        log.info("Plan created successfully with id: {}", plan.getId());

        return mapToResponseDTO(plan);
    }

    /**
     * Récupérer tous les plans
     */
    public List<PlanResponseDTO> getAllPlans() {
        log.info("Fetching all plans");
        return planRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les plans actifs
     */
    public List<PlanResponseDTO> getActivePlans() {
        log.info("Fetching active plans");
        return planRepository.findByActiveTrueOrderByMonthlyPriceAsc().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les plans actifs avec leurs features
     */
    public List<PlanResponseDTO> getActivePlansWithFeatures() {
        log.info("Fetching active plans with features");
        return planRepository.findAllActiveWithFeatures().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un plan par ID
     */
    public PlanResponseDTO getPlanById(Long planId) {
        log.info("Fetching plan with id: {}", planId);

        Plan plan = planRepository.findByIdWithFeatures(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

        return mapToResponseDTO(plan);
    }

    /**
     * Récupérer un plan par type
     */
    public PlanResponseDTO getPlanByType(PlanType type) {
        log.info("Fetching plan with type: {}", type);

        Plan plan = planRepository.findByTypeWithFeatures(type)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with type: " + type));

        return mapToResponseDTO(plan);
    }

    /**
     * Mettre à jour un plan
     */
    public PlanResponseDTO updatePlan(Long planId, PlanUpdateDTO dto) {
        log.info("Updating plan: {}", planId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

        // Vérifier le type si modifié
        if (dto.getType() != null && !dto.getType().equals(plan.getType())) {
            if (planRepository.existsByType(dto.getType())) {
                throw new DuplicateResourceException("Plan with type " + dto.getType() + " already exists");
            }
            plan.setType(dto.getType());
        }

        if (dto.getName() != null) {
            plan.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            plan.setDescription(dto.getDescription());
        }
        if (dto.getMonthlyPrice() != null) {
            plan.setMonthlyPrice(dto.getMonthlyPrice());
        }
        if (dto.getAnnualPrice() != null) {
            plan.setAnnualPrice(dto.getAnnualPrice());
        }
        if (dto.getMaxListings() != null) {
            plan.setMaxListings(dto.getMaxListings());
        }
        if (dto.getMaxPhotosPerListing() != null) {
            plan.setMaxPhotosPerListing(dto.getMaxPhotosPerListing());
        }
        if (dto.getFeaturedListings() != null) {
            plan.setFeaturedListings(dto.getFeaturedListings());
        }
        if (dto.getPrioritySupport() != null) {
            plan.setPrioritySupport(dto.getPrioritySupport());
        }
        if (dto.getAnalyticsAccess() != null) {
            plan.setAnalyticsAccess(dto.getAnalyticsAccess());
        }
        if (dto.getCustomBranding() != null) {
            plan.setCustomBranding(dto.getCustomBranding());
        }
        if (dto.getCommissionRate() != null) {
            plan.setCommissionRate(dto.getCommissionRate());
        }
        if (dto.getActive() != null) {
            plan.setActive(dto.getActive());
        }

        if (dto.getFeatureIds() != null) {
            List<Feature> features = featureRepository.findAllById(dto.getFeatureIds());
            plan.setFeatures(features);
        }

        plan = planRepository.save(plan);
        log.info("Plan updated successfully");

        return mapToResponseDTO(plan);
    }

    /**
     * Ajouter une feature à un plan
     */
    public PlanResponseDTO addFeatureToPlan(Long planId, Long featureId) {
        log.info("Adding feature {} to plan {}", featureId, planId);

        Plan plan = planRepository.findByIdWithFeatures(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));

        if (!plan.getFeatures().contains(feature)) {
            plan.getFeatures().add(feature);
            plan = planRepository.save(plan);
            log.info("Feature added to plan successfully");
        } else {
            log.info("Feature already associated with plan");
        }

        return mapToResponseDTO(plan);
    }

    /**
     * Retirer une feature d'un plan
     */
    public PlanResponseDTO removeFeatureFromPlan(Long planId, Long featureId) {
        log.info("Removing feature {} from plan {}", featureId, planId);

        Plan plan = planRepository.findByIdWithFeatures(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));

        plan.getFeatures().remove(feature);
        plan = planRepository.save(plan);
        log.info("Feature removed from plan successfully");

        return mapToResponseDTO(plan);
    }

    /**
     * Activer/Désactiver un plan
     */
    public PlanResponseDTO togglePlanActive(Long planId) {
        log.info("Toggling plan active status: {}", planId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

        plan.setActive(!plan.getActive());
        plan = planRepository.save(plan);

        log.info("Plan active status toggled to: {}", plan.getActive());
        return mapToResponseDTO(plan);
    }

    /**
     * Supprimer un plan
     */
    public void deletePlan(Long planId) {
        log.info("Deleting plan: {}", planId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

        long subscribers = planRepository.countSubscribersByPlanId(planId);
        if (subscribers > 0) {
            throw new IllegalStateException("Cannot delete plan with active subscribers. Count: " + subscribers);
        }

        planRepository.delete(plan);
        log.info("Plan deleted successfully");
    }

    /**
     * Compter les abonnés d'un plan
     */
    public long countPlanSubscribers(Long planId) {
        return planRepository.countSubscribersByPlanId(planId);
    }

    private PlanResponseDTO mapToResponseDTO(Plan plan) {
        BigDecimal annualSavings = plan.getMonthlyPrice()
                .multiply(BigDecimal.valueOf(12))
                .subtract(plan.getAnnualPrice());

        long subscribersCount = planRepository.countSubscribersByPlanId(plan.getId());

        List<FeatureDTO> featureDTOs = plan.getFeatures() != null ?
                plan.getFeatures().stream()
                        .map(this::mapFeatureToDTO)
                        .collect(Collectors.toList()) : new ArrayList<>();

        return PlanResponseDTO.builder()
                .id(plan.getId())
                .type(plan.getType())
                .name(plan.getName())
                .description(plan.getDescription())
                .monthlyPrice(plan.getMonthlyPrice())
                .annualPrice(plan.getAnnualPrice())
                .maxListings(plan.getMaxListings())
                .maxPhotosPerListing(plan.getMaxPhotosPerListing())
                .featuredListings(plan.getFeaturedListings())
                .prioritySupport(plan.getPrioritySupport())
                .analyticsAccess(plan.getAnalyticsAccess())
                .customBranding(plan.getCustomBranding())
                .commissionRate(plan.getCommissionRate())
                .active(plan.getActive())
                .features(featureDTOs)
                .subscribersCount(subscribersCount)
                .annualSavings(annualSavings)
                .build();
    }

    private FeatureDTO mapFeatureToDTO(Feature feature) {
        long plansCount = featureRepository.countPlansByFeatureId(feature.getId());

        return FeatureDTO.builder()
                .id(feature.getId())
                .name(feature.getName())
                .code(feature.getCode())
                .description(feature.getDescription())
                .icon(feature.getIcon())
                .active(feature.getActive())
                .plansCount(plansCount)
                .build();
    }
}


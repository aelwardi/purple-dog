package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.FeatureRepository;
import com.purple_dog.mvp.dto.FeatureCreateDTO;
import com.purple_dog.mvp.dto.FeatureDTO;
import com.purple_dog.mvp.dto.FeatureUpdateDTO;
import com.purple_dog.mvp.entities.Feature;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeatureService {

    private final FeatureRepository featureRepository;

    /**
     * Créer une nouvelle feature
     */
    public FeatureDTO createFeature(FeatureCreateDTO dto) {
        log.info("Creating feature: {}", dto.getName());

        if (featureRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Feature with code " + dto.getCode() + " already exists");
        }

        if (featureRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Feature with name " + dto.getName() + " already exists");
        }

        Feature feature = Feature.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        feature = featureRepository.save(feature);
        log.info("Feature created successfully with id: {}", feature.getId());

        return mapToDTO(feature);
    }

    /**
     * Récupérer toutes les features
     */
    public List<FeatureDTO> getAllFeatures() {
        log.info("Fetching all features");
        return featureRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les features actives
     */
    public List<FeatureDTO> getActiveFeatures() {
        log.info("Fetching active features");
        return featureRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer une feature par ID
     */
    public FeatureDTO getFeatureById(Long featureId) {
        log.info("Fetching feature with id: {}", featureId);

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));

        return mapToDTO(feature);
    }

    /**
     * Récupérer une feature par code
     */
    public FeatureDTO getFeatureByCode(String code) {
        log.info("Fetching feature with code: {}", code);

        Feature feature = featureRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with code: " + code));

        return mapToDTO(feature);
    }

    /**
     * Récupérer les features d'un plan
     */
    public List<FeatureDTO> getFeaturesByPlanId(Long planId) {
        log.info("Fetching features for plan: {}", planId);

        return featureRepository.findByPlanId(planId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour une feature
     */
    public FeatureDTO updateFeature(Long featureId, FeatureUpdateDTO dto) {
        log.info("Updating feature: {}", featureId);

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));

        if (dto.getCode() != null && !dto.getCode().equals(feature.getCode())) {
            if (featureRepository.existsByCode(dto.getCode())) {
                throw new DuplicateResourceException("Feature with code " + dto.getCode() + " already exists");
            }
            feature.setCode(dto.getCode());
        }

        if (dto.getName() != null && !dto.getName().equals(feature.getName())) {
            if (featureRepository.existsByName(dto.getName())) {
                throw new DuplicateResourceException("Feature with name " + dto.getName() + " already exists");
            }
            feature.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            feature.setDescription(dto.getDescription());
        }
        if (dto.getIcon() != null) {
            feature.setIcon(dto.getIcon());
        }
        if (dto.getActive() != null) {
            feature.setActive(dto.getActive());
        }

        feature = featureRepository.save(feature);
        log.info("Feature updated successfully");

        return mapToDTO(feature);
    }

    /**
     * Activer/Désactiver une feature
     */
    public FeatureDTO toggleFeatureActive(Long featureId) {
        log.info("Toggling feature active status: {}", featureId);

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));

        feature.setActive(!feature.getActive());
        feature = featureRepository.save(feature);

        log.info("Feature active status toggled to: {}", feature.getActive());
        return mapToDTO(feature);
    }

    /**
     * Supprimer une feature
     */
    public void deleteFeature(Long featureId) {
        log.info("Deleting feature: {}", featureId);

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));

        // Vérifier qu'aucun plan n'utilise cette feature
        long plansCount = featureRepository.countPlansByFeatureId(featureId);
        if (plansCount > 0) {
            throw new IllegalStateException("Cannot delete feature used by " + plansCount + " plan(s)");
        }

        featureRepository.delete(feature);
        log.info("Feature deleted successfully");
    }

    /**
     * Compter les plans utilisant une feature
     */
    public long countFeaturePlans(Long featureId) {
        return featureRepository.countPlansByFeatureId(featureId);
    }

    private FeatureDTO mapToDTO(Feature feature) {
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


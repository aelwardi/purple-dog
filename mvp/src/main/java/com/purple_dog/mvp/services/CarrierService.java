package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.CarrierRepository;
import com.purple_dog.mvp.dto.CarrierCreateDTO;
import com.purple_dog.mvp.dto.CarrierDTO;
import com.purple_dog.mvp.dto.CarrierUpdateDTO;
import com.purple_dog.mvp.entities.Carrier;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CarrierService {

    private final CarrierRepository carrierRepository;

    /**
     * Créer un nouveau transporteur
     */
    public CarrierDTO createCarrier(CarrierCreateDTO dto) {
        log.info("Creating carrier: {}", dto.getName());

        if (carrierRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Carrier with code " + dto.getCode() + " already exists");
        }

        if (carrierRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Carrier with name " + dto.getName() + " already exists");
        }

        Carrier carrier = Carrier.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .logo(dto.getLogo())
                .apiEndpoint(dto.getApiEndpoint())
                .trackingUrlPattern(dto.getTrackingUrlPattern())
                .basePrice(dto.getBasePrice())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .description(dto.getDescription())
                .build();

        carrier = carrierRepository.save(carrier);
        log.info("Carrier created successfully with id: {}", carrier.getId());

        return mapToDTO(carrier);
    }

    /**
     * Récupérer tous les transporteurs
     */
    public List<CarrierDTO> getAllCarriers() {
        log.info("Fetching all carriers");
        return carrierRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les transporteurs actifs
     */
    public List<CarrierDTO> getActiveCarriers() {
        log.info("Fetching active carriers");
        return carrierRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un transporteur par ID
     */
    public CarrierDTO getCarrierById(Long carrierId) {
        log.info("Fetching carrier with id: {}", carrierId);

        Carrier carrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + carrierId));

        return mapToDTO(carrier);
    }

    /**
     * Récupérer un transporteur par code
     */
    public CarrierDTO getCarrierByCode(String code) {
        log.info("Fetching carrier with code: {}", code);

        Carrier carrier = carrierRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with code: " + code));

        return mapToDTO(carrier);
    }

    /**
     * Récupérer les transporteurs disponibles dans un budget
     */
    public List<CarrierDTO> getCarriersByMaxPrice(BigDecimal maxPrice) {
        log.info("Fetching carriers with max price: {}", maxPrice);

        return carrierRepository.findAvailableCarriersByMaxPrice(maxPrice).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour un transporteur
     */
    public CarrierDTO updateCarrier(Long carrierId, CarrierUpdateDTO dto) {
        log.info("Updating carrier: {}", carrierId);

        Carrier carrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + carrierId));

        // Vérifier le code si modifié
        if (dto.getCode() != null && !dto.getCode().equals(carrier.getCode())) {
            if (carrierRepository.existsByCode(dto.getCode())) {
                throw new DuplicateResourceException("Carrier with code " + dto.getCode() + " already exists");
            }
            carrier.setCode(dto.getCode());
        }

        if (dto.getName() != null && !dto.getName().equals(carrier.getName())) {
            if (carrierRepository.existsByName(dto.getName())) {
                throw new DuplicateResourceException("Carrier with name " + dto.getName() + " already exists");
            }
            carrier.setName(dto.getName());
        }

        if (dto.getLogo() != null) {
            carrier.setLogo(dto.getLogo());
        }
        if (dto.getApiEndpoint() != null) {
            carrier.setApiEndpoint(dto.getApiEndpoint());
        }
        if (dto.getTrackingUrlPattern() != null) {
            carrier.setTrackingUrlPattern(dto.getTrackingUrlPattern());
        }
        if (dto.getBasePrice() != null) {
            carrier.setBasePrice(dto.getBasePrice());
        }
        if (dto.getActive() != null) {
            carrier.setActive(dto.getActive());
        }
        if (dto.getDescription() != null) {
            carrier.setDescription(dto.getDescription());
        }

        carrier = carrierRepository.save(carrier);
        log.info("Carrier updated successfully");

        return mapToDTO(carrier);
    }

    /**
     * Activer/Désactiver un transporteur
     */
    public CarrierDTO toggleCarrierActive(Long carrierId) {
        log.info("Toggling carrier active status: {}", carrierId);

        Carrier carrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + carrierId));

        carrier.setActive(!carrier.getActive());
        carrier = carrierRepository.save(carrier);

        log.info("Carrier active status toggled to: {}", carrier.getActive());
        return mapToDTO(carrier);
    }

    /**
     * Supprimer un transporteur
     */
    public void deleteCarrier(Long carrierId) {
        log.info("Deleting carrier: {}", carrierId);

        Carrier carrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + carrierId));

        // Vérifier qu'aucune livraison n'utilise ce transporteur
        long deliveriesCount = carrierRepository.countDeliveriesByCarrierId(carrierId);
        if (deliveriesCount > 0) {
            throw new IllegalStateException("Cannot delete carrier with existing deliveries. Count: " + deliveriesCount);
        }

        carrierRepository.delete(carrier);
        log.info("Carrier deleted successfully");
    }

    /**
     * Compter les livraisons d'un transporteur
     */
    public long countCarrierDeliveries(Long carrierId) {
        return carrierRepository.countDeliveriesByCarrierId(carrierId);
    }

    // Méthode privée

    private CarrierDTO mapToDTO(Carrier carrier) {
        long deliveriesCount = carrierRepository.countDeliveriesByCarrierId(carrier.getId());

        return CarrierDTO.builder()
                .id(carrier.getId())
                .name(carrier.getName())
                .code(carrier.getCode())
                .logo(carrier.getLogo())
                .trackingUrlPattern(carrier.getTrackingUrlPattern())
                .basePrice(carrier.getBasePrice())
                .active(carrier.getActive())
                .description(carrier.getDescription())
                .deliveriesCount(deliveriesCount)
                .build();
    }
}


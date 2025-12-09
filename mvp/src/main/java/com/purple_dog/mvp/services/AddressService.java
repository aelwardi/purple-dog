package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AddressRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.AddressCreateDTO;
import com.purple_dog.mvp.dto.AddressResponseDTO;
import com.purple_dog.mvp.dto.AddressUpdateDTO;
import com.purple_dog.mvp.entities.Address;
import com.purple_dog.mvp.entities.Person;
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
public class AddressService {

    private final AddressRepository addressRepository;
    private final PersonRepository personRepository;

    private static final int MAX_ADDRESSES_PER_USER = 10;

    /**
     * Créer une nouvelle adresse pour un utilisateur
     */
    public AddressResponseDTO createAddress(Long personId, AddressCreateDTO dto) {
        log.info("Creating address for person: {}", personId);

        // Vérifier que la personne existe
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + personId));

        // Vérifier le nombre maximum d'adresses
        long addressCount = addressRepository.countByPersonId(personId);
        if (addressCount >= MAX_ADDRESSES_PER_USER) {
            throw new InvalidOperationException("Maximum number of addresses (" + MAX_ADDRESSES_PER_USER + ") reached");
        }

        // Si c'est la première adresse ou si isDefault est true, la définir comme défaut
        boolean shouldBeDefault = addressCount == 0 || (dto.getIsDefault() != null && dto.getIsDefault());

        if (shouldBeDefault) {
            // Retirer le flag default des autres adresses
            addressRepository.resetDefaultForPerson(personId);
        }

        // Créer l'adresse
        Address address = Address.builder()
                .person(person)
                .label(dto.getLabel())
                .street(dto.getStreet())
                .complement(dto.getComplement())
                .city(dto.getCity())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .isDefault(shouldBeDefault)
                .createdAt(LocalDateTime.now())
                .build();

        address = addressRepository.save(address);
        log.info("Address created successfully with id: {}", address.getId());

        return mapToResponseDTO(address);
    }

    /**
     * Récupérer toutes les adresses d'un utilisateur
     */
    public List<AddressResponseDTO> getUserAddresses(Long personId) {
        log.info("Fetching addresses for person: {}", personId);

        if (!personRepository.existsById(personId)) {
            throw new ResourceNotFoundException("Person not found with id: " + personId);
        }

        return addressRepository.findByPersonIdOrderByIsDefaultDescCreatedAtDesc(personId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer une adresse spécifique
     */
    public AddressResponseDTO getAddress(Long addressId, Long personId) {
        log.info("Fetching address {} for person {}", addressId, personId);

        Address address = addressRepository.findByIdAndPersonId(addressId, personId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        return mapToResponseDTO(address);
    }

    /**
     * Récupérer l'adresse par défaut d'un utilisateur
     */
    public AddressResponseDTO getDefaultAddress(Long personId) {
        log.info("Fetching default address for person: {}", personId);

        if (!personRepository.existsById(personId)) {
            throw new ResourceNotFoundException("Person not found with id: " + personId);
        }

        Address address = addressRepository.findByPersonIdAndIsDefault(personId, true)
                .orElseThrow(() -> new ResourceNotFoundException("No default address found for person: " + personId));

        return mapToResponseDTO(address);
    }

    /**
     * Mettre à jour une adresse
     */
    public AddressResponseDTO updateAddress(Long addressId, Long personId, AddressUpdateDTO dto) {
        log.info("Updating address {} for person {}", addressId, personId);

        Address address = addressRepository.findByIdAndPersonId(addressId, personId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        // Mettre à jour les champs si fournis
        if (dto.getLabel() != null) {
            address.setLabel(dto.getLabel());
        }
        if (dto.getStreet() != null) {
            address.setStreet(dto.getStreet());
        }
        if (dto.getComplement() != null) {
            address.setComplement(dto.getComplement());
        }
        if (dto.getCity() != null) {
            address.setCity(dto.getCity());
        }
        if (dto.getPostalCode() != null) {
            address.setPostalCode(dto.getPostalCode());
        }
        if (dto.getCountry() != null) {
            address.setCountry(dto.getCountry());
        }

        // Gérer le changement de l'adresse par défaut
        if (dto.getIsDefault() != null && dto.getIsDefault() && !address.getIsDefault()) {
            // Retirer le flag default des autres adresses
            addressRepository.resetDefaultForPerson(personId);
            address.setIsDefault(true);
        }

        address = addressRepository.save(address);
        log.info("Address updated successfully");

        return mapToResponseDTO(address);
    }

    /**
     * Définir une adresse comme adresse par défaut
     */
    public AddressResponseDTO setDefaultAddress(Long addressId, Long personId) {
        log.info("Setting address {} as default for person {}", addressId, personId);

        Address address = addressRepository.findByIdAndPersonId(addressId, personId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!address.getIsDefault()) {
            // Retirer le flag default des autres adresses
            addressRepository.resetDefaultForPerson(personId);

            address.setIsDefault(true);
            address = addressRepository.save(address);
        }

        log.info("Address set as default successfully");
        return mapToResponseDTO(address);
    }

    /**
     * Supprimer une adresse
     */
    public void deleteAddress(Long addressId, Long personId) {
        log.info("Deleting address {} for person {}", addressId, personId);

        Address address = addressRepository.findByIdAndPersonId(addressId, personId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        boolean wasDefault = address.getIsDefault();
        addressRepository.delete(address);

        // Si c'était l'adresse par défaut, définir une autre adresse comme défaut
        if (wasDefault) {
            List<Address> remainingAddresses = addressRepository.findByPersonIdOrderByIsDefaultDescCreatedAtDesc(personId);
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
                log.info("New default address set: {}", newDefault.getId());
            }
        }

        log.info("Address deleted successfully");
    }

    /**
     * Compter les adresses d'un utilisateur
     */
    public long countUserAddresses(Long personId) {
        return addressRepository.countByPersonId(personId);
    }

    /**
     * Vérifier si un utilisateur a des adresses
     */
    public boolean hasAddresses(Long personId) {
        return addressRepository.countByPersonId(personId) > 0;
    }

    // Méthode privée de mapping

    private AddressResponseDTO mapToResponseDTO(Address address) {
        String fullAddress = buildFullAddress(address);

        return AddressResponseDTO.builder()
                .id(address.getId())
                .label(address.getLabel())
                .street(address.getStreet())
                .complement(address.getComplement())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .fullAddress(fullAddress)
                .build();
    }

    private String buildFullAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getStreet());

        if (address.getComplement() != null && !address.getComplement().trim().isEmpty()) {
            sb.append(", ").append(address.getComplement());
        }

        sb.append(", ").append(address.getPostalCode())
          .append(" ").append(address.getCity())
          .append(", ").append(address.getCountry());

        return sb.toString();
    }
}


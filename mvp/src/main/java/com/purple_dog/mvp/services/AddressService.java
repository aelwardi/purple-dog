package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AddressRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.AddressCreateDTO;
import com.purple_dog.mvp.dto.AddressResponseDTO;
import com.purple_dog.mvp.dto.AddressUpdateDTO;
import com.purple_dog.mvp.entities.Address;
import com.purple_dog.mvp.entities.Person;
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
public class AddressService {

    private final AddressRepository addressRepository;
    private final PersonRepository personRepository;

    public AddressResponseDTO createAddress(AddressCreateDTO dto) {
        log.debug("Creating address for person: {}", dto.getPersonId());

        Person person = personRepository.findById(dto.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + dto.getPersonId()));

        // Si cette adresse est définie par défaut et qu'une adresse par défaut existe déjà
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            removeExistingDefaultAddress(dto.getPersonId());
        }

        Address address = new Address();
        address.setPerson(person);
        address.setLabel(dto.getLabel());
        address.setStreet(dto.getStreet());
        address.setComplement(dto.getComplement());
        address.setCity(dto.getCity());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry() != null ? dto.getCountry() : "France");
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully with ID: {}", savedAddress.getId());

        return mapToResponseDTO(savedAddress);
    }

    @Transactional(readOnly = true)
    public AddressResponseDTO getAddressById(Long id) {
        log.debug("Fetching address with ID: {}", id);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
        return mapToResponseDTO(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAllAddresses() {
        log.debug("Fetching all addresses");
        return addressRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByPerson(Long personId) {
        log.debug("Fetching addresses for person: {}", personId);
        return addressRepository.findByPersonId(personId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressResponseDTO getDefaultAddressByPerson(Long personId) {
        log.debug("Fetching default address for person: {}", personId);
        Address address = addressRepository.findByPersonIdAndIsDefaultTrue(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Default address not found for person: " + personId));
        return mapToResponseDTO(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByCity(String city) {
        log.debug("Fetching addresses by city: {}", city);
        return addressRepository.findByCity(city).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByCountry(String country) {
        log.debug("Fetching addresses by country: {}", country);
        return addressRepository.findByCountry(country).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public AddressResponseDTO updateAddress(Long id, AddressUpdateDTO dto) {
        log.debug("Updating address with ID: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

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

        if (dto.getIsDefault() != null && Boolean.TRUE.equals(dto.getIsDefault())) {
            removeExistingDefaultAddress(address.getPerson().getId());
            address.setIsDefault(true);
        } else if (dto.getIsDefault() != null) {
            address.setIsDefault(false);
        }

        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated successfully with ID: {}", updatedAddress.getId());

        return mapToResponseDTO(updatedAddress);
    }

    public AddressResponseDTO setDefaultAddress(Long id) {
        log.debug("Setting address {} as default", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        removeExistingDefaultAddress(address.getPerson().getId());
        address.setIsDefault(true);

        Address updatedAddress = addressRepository.save(address);
        log.info("Address {} set as default successfully", updatedAddress.getId());

        return mapToResponseDTO(updatedAddress);
    }

    public void deleteAddress(Long id) {
        log.debug("Deleting address with ID: {}", id);

        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found with id: " + id);
        }

        addressRepository.deleteById(id);
        log.info("Address deleted successfully with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public long countAddressesByPerson(Long personId) {
        return addressRepository.countByPersonId(personId);
    }

    private void removeExistingDefaultAddress(Long personId) {
        addressRepository.findByPersonIdAndIsDefaultTrue(personId)
                .ifPresent(existingDefault -> {
                    existingDefault.setIsDefault(false);
                    addressRepository.save(existingDefault);
                });
    }

    private AddressResponseDTO mapToResponseDTO(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .personId(address.getPerson().getId())
                .personName(address.getPerson().getFirstName() + " " + address.getPerson().getLastName())
                .label(address.getLabel())
                .street(address.getStreet())
                .complement(address.getComplement())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }
}

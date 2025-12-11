package com.purple_dog.mvp.web.rest;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.purple_dog.mvp.dto.AddressCreateDTO;
import com.purple_dog.mvp.dto.AddressResponseDTO;
import com.purple_dog.mvp.dto.AddressUpdateDTO;
import com.purple_dog.mvp.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Address Management", description = "APIs for managing user addresses")
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

    /**
     * Créer une nouvelle adresse
     */
    @PostMapping("/person/{personId}")
    public ResponseEntity<AddressResponseDTO> createAddress(
            @PathVariable Long personId,
            @Valid @RequestBody AddressCreateDTO dto) {

        log.info("Request to create address for person: {}", personId);
        AddressResponseDTO response = addressService.createAddress(personId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer toutes les adresses d'un utilisateur
     */
    @GetMapping("/person/{personId}")
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(@PathVariable Long personId) {
        log.info("Request to get addresses for person: {}", personId);
        List<AddressResponseDTO> addresses = addressService.getUserAddresses(personId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Récupérer une adresse spécifique
     */
    @GetMapping("/{addressId}/person/{personId}")
    public ResponseEntity<AddressResponseDTO> getAddress(
            @PathVariable Long addressId,
            @PathVariable Long personId) {

        log.info("Request to get address {} for person {}", addressId, personId);
        AddressResponseDTO address = addressService.getAddress(addressId, personId);
        return ResponseEntity.ok(address);
    }

    /**
     * Récupérer l'adresse par défaut
     */
    @GetMapping("/person/{personId}/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddress(@PathVariable Long personId) {
        log.info("Request to get default address for person: {}", personId);
        AddressResponseDTO address = addressService.getDefaultAddress(personId);
        return ResponseEntity.ok(address);
    }

    /**
     * Mettre à jour une adresse
     */
    @PutMapping("/{addressId}/person/{personId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long addressId,
            @PathVariable Long personId,
            @Valid @RequestBody AddressUpdateDTO dto) {

        log.info("Request to update address {} for person {}", addressId, personId);
        AddressResponseDTO response = addressService.updateAddress(addressId, personId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Définir une adresse comme adresse par défaut
     */
    @PutMapping("/{addressId}/person/{personId}/set-default")
    public ResponseEntity<AddressResponseDTO> setDefaultAddress(
            @PathVariable Long addressId,
            @PathVariable Long personId) {

        log.info("Request to set address {} as default for person {}", addressId, personId);
        AddressResponseDTO response = addressService.setDefaultAddress(addressId, personId);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer une adresse
     */
    @DeleteMapping("/{addressId}/person/{personId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            @PathVariable Long personId) {

        log.info("Request to delete address {} for person {}", addressId, personId);
        addressService.deleteAddress(addressId, personId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Compter les adresses d'un utilisateur
     */
    @GetMapping("/person/{personId}/count")
    public ResponseEntity<Long> countUserAddresses(@PathVariable Long personId) {
        log.info("Request to count addresses for person: {}", personId);
        long count = addressService.countUserAddresses(personId);
        return ResponseEntity.ok(count);
    }

    /**
     * Vérifier si un utilisateur a des adresses
     */
    @GetMapping("/person/{personId}/has-addresses")
    public ResponseEntity<Boolean> hasAddresses(@PathVariable Long personId) {
        log.info("Request to check if person {} has addresses", personId);
        boolean hasAddresses = addressService.hasAddresses(personId);
        return ResponseEntity.ok(hasAddresses);
    }
}


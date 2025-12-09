package com.purple_dog.mvp.web;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@Valid @RequestBody AddressCreateDTO dto) {
        log.info("POST /addresses - Creating address for person: {}", dto.getPersonId());
        AddressResponseDTO created = addressService.createAddress(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long id) {
        log.info("GET /addresses/{} - Fetching address", id);
        AddressResponseDTO address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        log.info("GET /addresses - Fetching all addresses");
        List<AddressResponseDTO> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByPerson(@PathVariable Long personId) {
        log.info("GET /addresses/person/{} - Fetching addresses by person", personId);
        List<AddressResponseDTO> addresses = addressService.getAddressesByPerson(personId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/person/{personId}/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddressByPerson(@PathVariable Long personId) {
        log.info("GET /addresses/person/{}/default - Fetching default address for person", personId);
        AddressResponseDTO address = addressService.getDefaultAddressByPerson(personId);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByCity(@PathVariable String city) {
        log.info("GET /addresses/city/{} - Fetching addresses by city", city);
        List<AddressResponseDTO> addresses = addressService.getAddressesByCity(city);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByCountry(@PathVariable String country) {
        log.info("GET /addresses/country/{} - Fetching addresses by country", country);
        List<AddressResponseDTO> addresses = addressService.getAddressesByCountry(country);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressUpdateDTO dto) {
        log.info("PUT /addresses/{} - Updating address", id);
        AddressResponseDTO updated = addressService.updateAddress(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/set-default")
    public ResponseEntity<AddressResponseDTO> setDefaultAddress(@PathVariable Long id) {
        log.info("PATCH /addresses/{}/set-default - Setting address as default", id);
        AddressResponseDTO updated = addressService.setDefaultAddress(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAddress(@PathVariable Long id) {
        log.info("DELETE /addresses/{} - Deleting address", id);
        addressService.deleteAddress(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Address deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/person/{personId}")
    public ResponseEntity<Map<String, Long>> countAddressesByPerson(@PathVariable Long personId) {
        log.info("GET /addresses/count/person/{} - Counting addresses by person", personId);
        Map<String, Long> count = new HashMap<>();
        count.put("count", addressService.countAddressesByPerson(personId));
        return ResponseEntity.ok(count);
    }
}

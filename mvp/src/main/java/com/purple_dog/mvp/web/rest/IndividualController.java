package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.IndividualCreateDTO;
import com.purple_dog.mvp.dto.IndividualResponseDTO;
import com.purple_dog.mvp.dto.IndividualUpdateDTO;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.services.IndividualService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Individual Management", description = "APIs for managing individual users")
@RestController
@RequestMapping("/individuals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class IndividualController {

    private final IndividualService individualService;

    @PostMapping
    public ResponseEntity<IndividualResponseDTO> createIndividual(@Valid @RequestBody IndividualCreateDTO dto) {
        log.info("POST /individuals - Creating individual with email: {}", dto.getEmail());
        IndividualResponseDTO created = individualService.createIndividual(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndividualResponseDTO> getIndividualById(@PathVariable Long id) {
        log.info("GET /individuals/{} - Fetching individual", id);
        IndividualResponseDTO individual = individualService.getIndividualById(id);
        return ResponseEntity.ok(individual);
    }

    @GetMapping
    public ResponseEntity<List<IndividualResponseDTO>> getAllIndividuals() {
        log.info("GET /individuals - Fetching all individuals");
        List<IndividualResponseDTO> individuals = individualService.getAllIndividuals();
        return ResponseEntity.ok(individuals);
    }

    @GetMapping("/verified/{verified}")
    public ResponseEntity<List<IndividualResponseDTO>> getIndividualsByVerificationStatus(
            @PathVariable Boolean verified) {
        log.info("GET /individuals/verified/{} - Fetching individuals by verification status", verified);
        List<IndividualResponseDTO> individuals = individualService.getIndividualsByVerificationStatus(verified);
        return ResponseEntity.ok(individuals);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndividualResponseDTO> updateIndividual(
            @PathVariable Long id,
            @Valid @RequestBody IndividualUpdateDTO dto) {
        log.info("PUT /individuals/{} - Updating individual", id);
        IndividualResponseDTO updated = individualService.updateIndividual(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteIndividual(@PathVariable Long id) {
        log.info("DELETE /individuals/{} - Deleting individual", id);
        individualService.deleteIndividual(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Individual deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/verify-identity")
    public ResponseEntity<IndividualResponseDTO> verifyIdentity(@PathVariable Long id) {
        log.info("PATCH /individuals/{}/verify-identity - Verifying identity", id);
        IndividualResponseDTO verified = individualService.verifyIdentity(id);
        return ResponseEntity.ok(verified);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IndividualResponseDTO> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status) {
        log.info("PATCH /individuals/{}/status - Updating account status to {}", id, status);
        IndividualResponseDTO updated = individualService.updateAccountStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countIndividuals() {
        log.info("GET /individuals/count - Counting individuals");
        Map<String, Long> counts = new HashMap<>();
        counts.put("total", individualService.countIndividuals());
        counts.put("verified", individualService.countVerifiedIndividuals());
        return ResponseEntity.ok(counts);
    }
}


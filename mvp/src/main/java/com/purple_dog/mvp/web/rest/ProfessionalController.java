package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.ProfessionalCreateDTO;
import com.purple_dog.mvp.dto.ProfessionalResponseDTO;
import com.purple_dog.mvp.dto.ProfessionalUpdateDTO;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.services.ProfessionalService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Professional Management", description = "APIs for managing professionals")
@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping
    public ResponseEntity<ProfessionalResponseDTO> createProfessional(@Valid @RequestBody ProfessionalCreateDTO dto) {
        log.info("POST /professionals - Creating professional with email: {}", dto.getEmail());
        ProfessionalResponseDTO created = professionalService.createProfessional(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalResponseDTO> getProfessionalById(@PathVariable Long id) {
        log.info("GET /professionals/{} - Fetching professional", id);
        ProfessionalResponseDTO professional = professionalService.getProfessionalById(id);
        return ResponseEntity.ok(professional);
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalResponseDTO>> getAllProfessionals() {
        log.info("GET /professionals - Fetching all professionals");
        List<ProfessionalResponseDTO> professionals = professionalService.getAllProfessionals();
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/certified/{certified}")
    public ResponseEntity<List<ProfessionalResponseDTO>> getCertifiedProfessionals(
            @PathVariable Boolean certified) {
        log.info("GET /professionals/certified/{} - Fetching professionals by certification status", certified);
        List<ProfessionalResponseDTO> professionals = professionalService.getCertifiedProfessionals(certified);
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<ProfessionalResponseDTO>> getProfessionalsBySpecialty(
            @PathVariable String specialty) {
        log.info("GET /professionals/specialty/{} - Fetching professionals by specialty", specialty);
        List<ProfessionalResponseDTO> professionals = professionalService.getProfessionalsBySpecialty(specialty);
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProfessionalResponseDTO>> searchByCompanyName(
            @RequestParam String keyword) {
        log.info("GET /professionals/search?keyword={} - Searching professionals by company name", keyword);
        List<ProfessionalResponseDTO> professionals = professionalService.searchByCompanyName(keyword);
        return ResponseEntity.ok(professionals);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessionalResponseDTO> updateProfessional(
            @PathVariable Long id,
            @Valid @RequestBody ProfessionalUpdateDTO dto) {
        log.info("PUT /professionals/{} - Updating professional", id);
        ProfessionalResponseDTO updated = professionalService.updateProfessional(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProfessional(@PathVariable Long id) {
        log.info("DELETE /professionals/{} - Deleting professional", id);
        professionalService.deleteProfessional(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Professional deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/certify")
    public ResponseEntity<ProfessionalResponseDTO> certifyProfessional(@PathVariable Long id) {
        log.info("PATCH /professionals/{}/certify - Certifying professional", id);
        ProfessionalResponseDTO certified = professionalService.certifyProfessional(id);
        return ResponseEntity.ok(certified);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProfessionalResponseDTO> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status) {
        log.info("PATCH /professionals/{}/status - Updating account status to {}", id, status);
        ProfessionalResponseDTO updated = professionalService.updateAccountStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countProfessionals() {
        log.info("GET /professionals/count - Counting professionals");
        Map<String, Long> counts = new HashMap<>();
        counts.put("total", professionalService.countProfessionals());
        counts.put("certified", professionalService.countCertifiedProfessionals());
        return ResponseEntity.ok(counts);
    }
}

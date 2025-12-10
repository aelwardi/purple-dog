package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.AdminCreateDTO;
import com.purple_dog.mvp.dto.AdminResponseDTO;
import com.purple_dog.mvp.dto.UserUpdateDTO;
import com.purple_dog.mvp.services.AdminService;
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
@RequestMapping("/admins")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(@Valid @RequestBody AdminCreateDTO dto) {
        log.info("POST /admins - Creating admin with email: {}", dto.getEmail());
        AdminResponseDTO created = adminService.createAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
        log.info("GET /admins/{} - Fetching admin", id);
        AdminResponseDTO admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }

    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        log.info("GET /admins - Fetching all admins");
        List<AdminResponseDTO> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/super-admins")
    public ResponseEntity<List<AdminResponseDTO>> getSuperAdmins() {
        log.info("GET /admins/super-admins - Fetching super admins");
        List<AdminResponseDTO> superAdmins = adminService.getSuperAdmins();
        return ResponseEntity.ok(superAdmins);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        log.info("PUT /admins/{} - Updating admin", id);
        AdminResponseDTO updated = adminService.updateAdmin(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/permissions")
    public ResponseEntity<AdminResponseDTO> updatePermissions(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        log.info("PATCH /admins/{}/permissions - Updating permissions", id);
        String permissions = payload.get("permissions");
        AdminResponseDTO updated = adminService.updatePermissions(id, permissions);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/promote")
    public ResponseEntity<AdminResponseDTO> promoteToSuperAdmin(@PathVariable Long id) {
        log.info("PATCH /admins/{}/promote - Promoting to super admin", id);
        AdminResponseDTO promoted = adminService.promotToSuperAdmin(id);
        return ResponseEntity.ok(promoted);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAdmin(@PathVariable Long id) {
        log.info("DELETE /admins/{} - Deleting admin", id);
        adminService.deleteAdmin(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countAdmins() {
        log.info("GET /admins/count - Counting admins");
        Map<String, Long> counts = new HashMap<>();
        counts.put("total", adminService.countAdmins());
        counts.put("superAdmins", adminService.countSuperAdmins());
        return ResponseEntity.ok(counts);
    }
}


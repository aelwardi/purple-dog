package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.DocumentResponseDTO;
import com.purple_dog.mvp.dto.DocumentUploadDTO;
import com.purple_dog.mvp.dto.DocumentVerificationDTO;
import com.purple_dog.mvp.entities.DocumentStatus;
import com.purple_dog.mvp.services.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload un document pour un utilisateur
     */
    @PostMapping(value = "/upload/{personId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> uploadDocument(
            @PathVariable Long personId,
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute DocumentUploadDTO dto) {

        log.info("Request to upload document for person: {}", personId);
        DocumentResponseDTO response = documentService.uploadDocument(personId, dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer tous les documents d'un utilisateur
     */
    @GetMapping("/user/{personId}")
    public ResponseEntity<List<DocumentResponseDTO>> getUserDocuments(@PathVariable Long personId) {
        log.info("Request to get documents for person: {}", personId);
        List<DocumentResponseDTO> documents = documentService.getUserDocuments(personId);
        return ResponseEntity.ok(documents);
    }

    /**
     * Récupérer les documents d'un utilisateur par statut
     */
    @GetMapping("/user/{personId}/status/{status}")
    public ResponseEntity<List<DocumentResponseDTO>> getUserDocumentsByStatus(
            @PathVariable Long personId,
            @PathVariable DocumentStatus status) {

        log.info("Request to get documents for person: {} with status: {}", personId, status);
        List<DocumentResponseDTO> documents = documentService.getUserDocumentsByStatus(personId, status);
        return ResponseEntity.ok(documents);
    }

    /**
     * Récupérer un document par son ID
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(@PathVariable Long documentId) {
        log.info("Request to get document: {}", documentId);
        DocumentResponseDTO document = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(document);
    }

    /**
     * Récupérer tous les documents en attente de vérification (Admin)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<DocumentResponseDTO>> getAllPendingDocuments() {
        log.info("Request to get all pending documents");
        List<DocumentResponseDTO> documents = documentService.getAllPendingDocuments();
        return ResponseEntity.ok(documents);
    }

    /**
     * Vérifier un document (Approuver ou Rejeter) - Admin uniquement
     */
    @PutMapping("/{documentId}/verify/{adminId}")
    public ResponseEntity<DocumentResponseDTO> verifyDocument(
            @PathVariable Long documentId,
            @PathVariable Long adminId,
            @Valid @RequestBody DocumentVerificationDTO dto) {

        log.info("Request to verify document: {} by admin: {}", documentId, adminId);
        DocumentResponseDTO response = documentService.verifyDocument(documentId, adminId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer un document
     */
    @DeleteMapping("/{documentId}/user/{personId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long documentId,
            @PathVariable Long personId) {

        log.info("Request to delete document: {} for person: {}", documentId, personId);
        documentService.deleteDocument(documentId, personId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Compter les documents approuvés d'un utilisateur
     */
    @GetMapping("/user/{personId}/approved/count")
    public ResponseEntity<Long> countApprovedDocuments(@PathVariable Long personId) {
        log.info("Request to count approved documents for person: {}", personId);
        long count = documentService.countApprovedDocuments(personId);
        return ResponseEntity.ok(count);
    }
}

package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.DocumentRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.DocumentResponseDTO;
import com.purple_dog.mvp.dto.DocumentUploadDTO;
import com.purple_dog.mvp.dto.DocumentVerificationDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PersonRepository personRepository;

    private static final String UPLOAD_DIR = "uploads/documents/";

    public DocumentResponseDTO uploadDocument(Long personId, DocumentUploadDTO dto, MultipartFile file) {
        log.info("Uploading document for person: {}", personId);

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + personId));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Document document = Document.builder()
                    .person(person)
                    .documentType(dto.getDocumentType())
                    .fileName(originalFilename)
                    .fileUrl("/uploads/documents/" + uniqueFilename)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .description(dto.getDescription())
                    .status(DocumentStatus.PENDING)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            document = documentRepository.save(document);
            log.info("Document uploaded successfully with id: {}", document.getId());

            return mapToResponseDTO(document);

        } catch (IOException e) {
            log.error("Error uploading document", e);
            throw new RuntimeException("Failed to upload document: " + e.getMessage());
        }
    }

    public List<DocumentResponseDTO> getUserDocuments(Long personId) {
        log.info("Fetching documents for person: {}", personId);

        if (!personRepository.existsById(personId)) {
            throw new ResourceNotFoundException("Person not found with id: " + personId);
        }

        return documentRepository.findByPersonId(personId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentResponseDTO> getUserDocumentsByStatus(Long personId, DocumentStatus status) {
        log.info("Fetching documents for person: {} with status: {}", personId, status);

        return documentRepository.findByPersonIdAndStatus(personId, status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public DocumentResponseDTO getDocumentById(Long documentId) {
        log.info("Fetching document with id: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        return mapToResponseDTO(document);
    }

    public List<DocumentResponseDTO> getAllPendingDocuments() {
        log.info("Fetching all pending documents");

        return documentRepository.findByStatus(DocumentStatus.PENDING).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public DocumentResponseDTO verifyDocument(Long documentId, Long adminId, DocumentVerificationDTO dto) {
        log.info("Verifying document: {} by admin: {}", documentId, adminId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        Admin admin = (Admin) personRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + adminId));

        if (dto.getStatus() == DocumentStatus.REJECTED &&
            (dto.getRejectionReason() == null || dto.getRejectionReason().trim().isEmpty())) {
            throw new IllegalArgumentException("Rejection reason is required when rejecting a document");
        }

        document.setStatus(dto.getStatus());
        document.setRejectionReason(dto.getRejectionReason());
        document.setVerifiedAt(LocalDateTime.now());
        document.setVerifiedBy(admin);

        document = documentRepository.save(document);
        log.info("Document verified successfully with status: {}", dto.getStatus());

        return mapToResponseDTO(document);
    }

    public void deleteDocument(Long documentId, Long personId) {
        log.info("Deleting document: {} for person: {}", documentId, personId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        if (!document.getPerson().getId().equals(personId)) {
            throw new IllegalArgumentException("Document does not belong to person: " + personId);
        }

        try {
            Path filePath = Paths.get(document.getFileUrl().substring(1)); // Remove leading '/'
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", document.getFileUrl(), e);
        }

        documentRepository.delete(document);
        log.info("Document deleted successfully");
    }

    public long countApprovedDocuments(Long personId) {
        return documentRepository.countByPersonIdAndStatus(personId, DocumentStatus.APPROVED);
    }

    public boolean hasApprovedDocument(Long personId, DocumentType documentType) {
        return documentRepository.findByPersonIdAndDocumentTypeAndStatus(
                personId, documentType, DocumentStatus.APPROVED
        ).isPresent();
    }

    private DocumentResponseDTO mapToResponseDTO(Document document) {
        return DocumentResponseDTO.builder()
                .id(document.getId())
                .personId(document.getPerson().getId())
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .description(document.getDescription())
                .status(document.getStatus())
                .rejectionReason(document.getRejectionReason())
                .verifiedAt(document.getVerifiedAt())
                .verifiedById(document.getVerifiedBy() != null ? document.getVerifiedBy().getId() : null)
                .verifiedByName(document.getVerifiedBy() != null ?
                        document.getVerifiedBy().getFirstName() + " " + document.getVerifiedBy().getLastName() : null)
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}


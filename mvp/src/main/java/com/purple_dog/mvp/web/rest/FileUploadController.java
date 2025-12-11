package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.services.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "File Upload", description = "APIs for uploading photos and documents")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload multiple photos
     * POST /api/upload/photos
     */
    @Operation(summary = "Upload product photos", description = "Upload multiple photos for a product (max 10, 5MB each)")
    @PostMapping("/photos")
    public ResponseEntity<?> uploadPhotos(@RequestParam("photos") List<MultipartFile> photos) {
        try {
            log.info("Uploading {} photos", photos.size());

            if (photos.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No photos provided"));
            }

            if (photos.size() > 10) {
                return ResponseEntity.badRequest().body(Map.of("error", "Maximum 10 photos allowed"));
            }

            List<String> urls = fileUploadService.uploadPhotos(photos);

            if (urls.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No valid photos uploaded"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("urls", urls);
            response.put("count", urls.size());
            response.put("message", "Photos uploaded successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error uploading photos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload photos: " + e.getMessage()));
        }
    }

    /**
     * Upload a single document
     * POST /api/upload/document
     */
    @Operation(summary = "Upload a document", description = "Upload a document (PDF, image, max 10MB)")
    @PostMapping("/document")
    public ResponseEntity<?> uploadDocument(@RequestParam("document") MultipartFile document) {
        try {
            log.info("Uploading document: {}", document.getOriginalFilename());

            if (document.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No document provided"));
            }

            String url = fileUploadService.uploadDocument(document);

            Map<String, Object> response = new HashMap<>();
            response.put("url", url);
            response.put("filename", document.getOriginalFilename());
            response.put("message", "Document uploaded successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error uploading document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload document: " + e.getMessage()));
        }
    }

    /**
     * Delete a file
     * DELETE /api/upload/file
     */
    @Operation(summary = "Delete a file", description = "Delete an uploaded file by its URL")
    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("url") String url) {
        try {
            log.info("Deleting file: {}", url);
            fileUploadService.deleteFile(url);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete file: " + e.getMessage()));
        }
    }
}


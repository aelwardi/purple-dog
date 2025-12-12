package com.purple_dog.mvp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service pour gérer l'upload de fichiers (photos et documents)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.upload.photos.dir:uploads/photos}")
    private String photosDir;

    @Value("${file.upload.documents.dir:uploads/documents}")
    private String documentsDir;

    @Value("${server.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Upload multiple photos and return their URLs
     * @param files List of photo files
     * @return List of public URLs
     */
    public List<String> uploadPhotos(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();

        // Create photos directory if it doesn't exist
        Path photosPath = Paths.get(photosDir);
        if (!Files.exists(photosPath)) {
            Files.createDirectories(photosPath);
            log.info("Created photos directory: {}", photosPath.toAbsolutePath());
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("Invalid file type: {}. Skipping.", contentType);
                continue;
            }

            // Validate file size (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                log.warn("File too large: {} bytes. Skipping.", file.getSize());
                continue;
            }

            String url = uploadFile(file, photosPath);
            urls.add(url);
            log.info("Uploaded photo: {}", url);
        }

        return urls;
    }

    /**
     * Upload a single document and return its URL
     * @param file Document file
     * @return Public URL
     */
    public String uploadDocument(MultipartFile file) throws IOException {
        // Create documents directory if it doesn't exist
        Path documentsPath = Paths.get(documentsDir);
        if (!Files.exists(documentsPath)) {
            Files.createDirectories(documentsPath);
            log.info("Created documents directory: {}", documentsPath.toAbsolutePath());
        }

        // Validate file type (PDF, images)
        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
            throw new IOException("Invalid document type: " + contentType);
        }

        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("Document too large: " + file.getSize() + " bytes");
        }

        String url = uploadFile(file, documentsPath);
        log.info("Uploaded document: {}", url);
        return url;
    }

    /**
     * Upload a file to the specified directory
     * @param file Multipart file
     * @param targetDir Target directory path
     * @return Public URL of the uploaded file
     */
    private String uploadFile(MultipartFile file, Path targetDir) throws IOException {
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String filename = timestamp + "_" + uniqueId + extension;

        // Save file
        Path filePath = targetDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return public URL (uploads are mapped via FileStorageConfig)
        String relativePath = targetDir.getFileName() + "/" + filename;
        return baseUrl + "/uploads/" + relativePath;
    }

    /**
     * Delete a file by its URL
     * @param url File URL
     */
    public void deleteFile(String url) {
        try {
            // Extract filename from URL
            String filename = url.substring(url.lastIndexOf("/") + 1);

            // Try photos directory first
            Path photoPath = Paths.get(photosDir, filename);
            if (Files.exists(photoPath)) {
                Files.delete(photoPath);
                log.info("Deleted file: {}", photoPath);
                return;
            }

            // Try documents directory
            Path docPath = Paths.get(documentsDir, filename);
            if (Files.exists(docPath)) {
                Files.delete(docPath);
                log.info("Deleted file: {}", docPath);
            }
        } catch (Exception e) {
            log.error("Error deleting file: {}", url, e);
        }
    }
}

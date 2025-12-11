package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Document;
import com.purple_dog.mvp.entities.DocumentStatus;
import com.purple_dog.mvp.entities.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByPersonId(Long personId);

    List<Document> findByPersonIdAndStatus(Long personId, DocumentStatus status);

    List<Document> findByPersonIdAndDocumentType(Long personId, DocumentType documentType);

    Optional<Document> findByPersonIdAndDocumentTypeAndStatus(Long personId, DocumentType documentType, DocumentStatus status);

    List<Document> findByStatus(DocumentStatus status);

    boolean existsByPersonIdAndDocumentType(Long personId, DocumentType documentType);

    long countByPersonIdAndStatus(Long personId, DocumentStatus status);
}


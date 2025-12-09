package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.DocumentRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.DocumentResponseDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private DocumentService documentService;

    private Individual individual;
    private Document document;

    @BeforeEach
    void setUp() {
        individual = Individual.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.INDIVIDUAL)
                .build();

        document = Document.builder()
                .id(1L)
                .person(individual)
                .documentType(DocumentType.IDENTITY_CARD)
                .fileName("test.pdf")
                .fileUrl("/uploads/documents/test.pdf")
                .fileType("application/pdf")
                .fileSize(1024L)
                .status(DocumentStatus.PENDING)
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetDocumentById_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        DocumentResponseDTO result = documentService.getDocumentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(DocumentType.IDENTITY_CARD, result.getDocumentType());
        assertEquals("test.pdf", result.getFileName());
        verify(documentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDocumentById_NotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            documentService.getDocumentById(1L);
        });
    }

    @Test
    void testCountApprovedDocuments() {
        when(documentRepository.countByPersonIdAndStatus(1L, DocumentStatus.APPROVED))
                .thenReturn(5L);

        long count = documentService.countApprovedDocuments(1L);

        assertEquals(5L, count);
        verify(documentRepository, times(1))
                .countByPersonIdAndStatus(1L, DocumentStatus.APPROVED);
    }

    @Test
    void testHasApprovedDocument() {
        when(documentRepository.findByPersonIdAndDocumentTypeAndStatus(
                1L, DocumentType.IDENTITY_CARD, DocumentStatus.APPROVED))
                .thenReturn(Optional.of(document));

        boolean hasDocument = documentService.hasApprovedDocument(1L, DocumentType.IDENTITY_CARD);

        assertTrue(hasDocument);
        verify(documentRepository, times(1))
                .findByPersonIdAndDocumentTypeAndStatus(1L, DocumentType.IDENTITY_CARD, DocumentStatus.APPROVED);
    }

    @Test
    void testDeleteDocument_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        assertDoesNotThrow(() -> {
            documentService.deleteDocument(1L, 1L);
        });

        verify(documentRepository, times(1)).delete(document);
    }

    @Test
    void testDeleteDocument_UnauthorizedUser() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.deleteDocument(1L, 999L); // Wrong personId
        });
    }
}

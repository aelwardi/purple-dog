package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.DocumentResponseDTO;
import com.purple_dog.mvp.entities.DocumentStatus;
import com.purple_dog.mvp.entities.DocumentType;
import com.purple_dog.mvp.services.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DocumentService documentService;

        @Test
        @WithMockUser(username = "test@example.com", roles = {"USER"})
        void testGetUserDocuments() throws Exception {
                DocumentResponseDTO doc1 = DocumentResponseDTO.builder()
                                .id(1L)
                                .personId(1L)
                                .documentType(DocumentType.IDENTITY_CARD)
                                .fileName("test.pdf")
                                .fileUrl("/uploads/documents/test.pdf")
                                .status(DocumentStatus.APPROVED)
                                .uploadedAt(LocalDateTime.now())
                                .build();

                List<DocumentResponseDTO> documents = Arrays.asList(doc1);
                when(documentService.getUserDocuments(1L)).thenReturn(documents);

                mockMvc.perform(get("/api/documents/user/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].documentType").value("IDENTITY_CARD"))
                                .andExpect(jsonPath("$[0].status").value("APPROVED"));
        }

        @Test
        @WithMockUser(username = "test@example.com", roles = {"USER"})
        void testGetDocumentById() throws Exception {
                DocumentResponseDTO doc = DocumentResponseDTO.builder()
                                .id(1L)
                                .personId(1L)
                                .documentType(DocumentType.KBIS_EXTRACT)
                                .fileName("kbis.pdf")
                                .fileUrl("/uploads/documents/kbis.pdf")
                                .status(DocumentStatus.PENDING)
                                .uploadedAt(LocalDateTime.now())
                                .build();

                when(documentService.getDocumentById(1L)).thenReturn(doc);

                mockMvc.perform(get("/api/documents/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.documentType").value("KBIS_EXTRACT"));
        }

        @Test
        @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
        void testCountApprovedDocuments() throws Exception {
                when(documentService.countApprovedDocuments(1L)).thenReturn(5L);

                mockMvc.perform(get("/api/documents/user/1/approved/count")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string("5"));
        }
}

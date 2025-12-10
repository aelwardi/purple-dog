package com.purple_dog.mvp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageContentFilterTest {

    private MessageContentFilter filter;

    @BeforeEach
    void setUp() {
        filter = new MessageContentFilter();
    }

    @Test
    void testFilterEmail_ShouldBlock() {
        String content = "Contactez-moi sur jean.dupont@email.com pour plus d'infos";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertTrue(result.shouldBlock());
        assertTrue(result.wasFiltered());
        assertTrue(result.getDetectedPatterns().contains("EMAIL"));
        assertNotNull(result.getBlockReason());
        assertNull(result.getFilteredContent());
    }

    @Test
    void testFilterPhone_French_ShouldBlock() {
        String content = "Appelez-moi au 06 12 34 56 78";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertTrue(result.shouldBlock());
        assertTrue(result.wasFiltered());
        assertTrue(result.getDetectedPatterns().contains("PHONE"));
    }

    @Test
    void testFilterPhone_International_ShouldBlock() {
        String content = "Mon numéro est +33 6 12 34 56 78";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertTrue(result.shouldBlock());
        assertTrue(result.getDetectedPatterns().contains("PHONE"));
    }

    @Test
    void testFilterURL_ShouldBlock() {
        String content = "Visitez mon site https://www.example.com";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertTrue(result.shouldBlock());
        assertTrue(result.wasFiltered());
        assertTrue(result.getDetectedPatterns().contains("URL"));
    }

    @Test
    void testFilterWhatsApp_ShouldBlock() {
        String content = "Contactez-moi sur WhatsApp";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertTrue(result.shouldBlock());
        assertTrue(result.getDetectedPatterns().contains("WHATSAPP"));
    }

    @Test
    void testFilterTelegram_ShouldBlock() {
        String content = "Ajoutez-moi sur telegram";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertTrue(result.shouldBlock());
        assertTrue(result.getDetectedPatterns().contains("TELEGRAM"));
    }

    @Test
    void testFilterSocialMedia_ShouldBlock() {
        String content = "Suivez-moi sur Instagram";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertTrue(result.shouldBlock());
        assertTrue(result.getDetectedPatterns().contains("SOCIAL_MEDIA"));
    }

    @Test
    void testMultiplePatterns_ShouldBlockAndDetectAll() {
        String content = "Mon email est test@test.com et mon tel 06 12 34 56 78";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertTrue(result.shouldBlock());
        assertTrue(result.wasFiltered());
        assertTrue(result.getDetectedPatterns().contains("EMAIL"));
        assertTrue(result.getDetectedPatterns().contains("PHONE"));
    }

    @Test
    void testKeywordsOnly_ShouldNotBlock() {
        String content = "Je peux vous appeler demain pour discuter";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertFalse(result.shouldBlock());
        assertTrue(result.wasFiltered()); // Détecté mais pas bloqué
        assertNotNull(result.getFilteredContent());
    }

    @Test
    void testCleanMessage_ShouldPass() {
        String content = "Bonjour, votre produit est-il toujours disponible?";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertFalse(result.shouldBlock());
        assertFalse(result.wasFiltered());
        assertEquals(content, result.getFilteredContent());
        assertNull(result.getBlockReason());
    }

    @Test
    void testEmptyMessage_ShouldPass() {
        String content = "";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        assertFalse(result.shouldBlock());
        assertFalse(result.wasFiltered());
    }

    @Test
    void testNullMessage_ShouldPass() {
        MessageContentFilter.FilterResult result = filter.filterContent(null);

        assertFalse(result.shouldBlock());
        assertFalse(result.wasFiltered());
    }

    @Test
    void testContainsSensitiveInfo_Email() {
        assertTrue(filter.containsSensitiveInfo("Contact: user@example.com"));
    }

    @Test
    void testContainsSensitiveInfo_Phone() {
        assertTrue(filter.containsSensitiveInfo("Tel: 06 12 34 56 78"));
    }

    @Test
    void testContainsSensitiveInfo_URL() {
        assertTrue(filter.containsSensitiveInfo("Site: www.example.com"));
    }

    @Test
    void testContainsSensitiveInfo_CleanMessage() {
        assertFalse(filter.containsSensitiveInfo("Bonjour, comment allez-vous?"));
    }

    @Test
    void testFilteredContentReplacement() {
        String content = "Mon email est test@test.com";
        MessageContentFilter.FilterResult result = filter.filterContent(content);

        // Le contenu ne devrait pas être retourné car bloqué
        assertNull(result.getFilteredContent());
        assertTrue(result.shouldBlock());
    }

    @Test
    void testCaseInsensitiveDetection() {
        String content1 = "Contactez-moi sur WHATSAPP";
        String content2 = "contactez-moi sur WhatsApp";
        String content3 = "contactez-moi sur whatsapp";

        assertTrue(filter.filterContent(content1).shouldBlock());
        assertTrue(filter.filterContent(content2).shouldBlock());
        assertTrue(filter.filterContent(content3).shouldBlock());
    }

    @Test
    void testEmailVariations() {
        assertTrue(filter.containsSensitiveInfo("user@domain.com"));
        assertTrue(filter.containsSensitiveInfo("user.name@domain.co.uk"));
        assertTrue(filter.containsSensitiveInfo("user+tag@domain.com"));
        assertTrue(filter.containsSensitiveInfo("user_name@domain.com"));
    }

    @Test
    void testPhoneVariations() {
        assertTrue(filter.containsSensitiveInfo("0612345678"));
        assertTrue(filter.containsSensitiveInfo("06 12 34 56 78"));
        assertTrue(filter.containsSensitiveInfo("06.12.34.56.78"));
        assertTrue(filter.containsSensitiveInfo("06-12-34-56-78"));
        assertTrue(filter.containsSensitiveInfo("+33 6 12 34 56 78"));
        assertTrue(filter.containsSensitiveInfo("+33612345678"));
    }
}


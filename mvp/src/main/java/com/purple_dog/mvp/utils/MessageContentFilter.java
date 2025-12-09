package com.purple_dog.mvp.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service pour filtrer les informations sensibles dans les messages
 * Bloque ou filtre les numéros de téléphone, emails, URLs, etc.
 */
@Component
@Slf4j
public class MessageContentFilter {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}|" + // Format français
                       "\\b(?:\\+\\d{1,3})?[\\s.-]?\\(?\\d{2,4}\\)?[\\s.-]?\\d{2,4}[\\s.-]?\\d{2,4}[\\s.-]?\\d{0,4}\\b");

    private static final Pattern URL_PATTERN =
        Pattern.compile("(?:https?://|www\\.|ftp://)[^\\s/$.?#].[^\\s]*|" +
                       "\\b[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\\.[a-zA-Z]{2,}\\b",
                       Pattern.CASE_INSENSITIVE);

    private static final Pattern WHATSAPP_PATTERN =
        Pattern.compile("\\bwhatsapp\\b|\\bwa\\.me\\b|\\bwha\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern TELEGRAM_PATTERN =
        Pattern.compile("\\btelegram\\b|\\bt\\.me\\b|\\btg\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern SOCIAL_MEDIA_PATTERN =
        Pattern.compile("\\b(?:facebook|fb|instagram|insta|ig|twitter|snapchat|snap|tiktok|linkedin)\\b",
                       Pattern.CASE_INSENSITIVE);

    private static final String[] CONTACT_KEYWORDS = {
        "appel", "appelle", "téléphone", "tel", "phone", "numéro", "numero",
        "mail", "email", "e-mail", "contacter", "contact", "joindre",
        "whatsapp", "telegram", "messenger", "discord", "skype",
        "hors", "dehors", "plateforme", "direct", "directement"
    };

    /**
     * Filtre le contenu d'un message et retourne le résultat
     */
    public FilterResult filterContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new FilterResult(content, false, false, new ArrayList<>(), null);
        }

        List<String> detectedPatterns = new ArrayList<>();
        String filteredContent = content;
        boolean shouldBlock = false;

        Matcher emailMatcher = EMAIL_PATTERN.matcher(filteredContent);
        if (emailMatcher.find()) {
            detectedPatterns.add("EMAIL");
            filteredContent = emailMatcher.replaceAll("[EMAIL MASQUÉ]");
            shouldBlock = true;
            log.warn("Email detected and filtered in message");
        }

        Matcher phoneMatcher = PHONE_PATTERN.matcher(filteredContent);
        if (phoneMatcher.find()) {
            detectedPatterns.add("PHONE");
            filteredContent = phoneMatcher.replaceAll("[NUMÉRO MASQUÉ]");
            shouldBlock = true;
            log.warn("Phone number detected and filtered in message");
        }

        Matcher urlMatcher = URL_PATTERN.matcher(filteredContent);
        if (urlMatcher.find()) {
            detectedPatterns.add("URL");
            filteredContent = urlMatcher.replaceAll("[LIEN MASQUÉ]");
            shouldBlock = true;
            log.warn("URL detected and filtered in message");
        }

        Matcher whatsappMatcher = WHATSAPP_PATTERN.matcher(filteredContent);
        if (whatsappMatcher.find()) {
            detectedPatterns.add("WHATSAPP");
            shouldBlock = true;
            log.warn("WhatsApp reference detected in message");
        }

        Matcher telegramMatcher = TELEGRAM_PATTERN.matcher(filteredContent);
        if (telegramMatcher.find()) {
            detectedPatterns.add("TELEGRAM");
            shouldBlock = true;
            log.warn("Telegram reference detected in message");
        }

        Matcher socialMatcher = SOCIAL_MEDIA_PATTERN.matcher(filteredContent);
        if (socialMatcher.find()) {
            detectedPatterns.add("SOCIAL_MEDIA");
            shouldBlock = true;
            log.warn("Social media reference detected in message");
        }

        String contentLower = content.toLowerCase();
        for (String keyword : CONTACT_KEYWORDS) {
            if (contentLower.contains(keyword)) {
                detectedPatterns.add("KEYWORD:" + keyword.toUpperCase());
            }
        }

        String blockReason = null;
        if (shouldBlock) {
            blockReason = "Message bloqué : contient des informations de contact non autorisées ("
                         + String.join(", ", detectedPatterns) + ")";
        }

        boolean wasFiltered = !detectedPatterns.isEmpty();

        return new FilterResult(
            shouldBlock ? null : filteredContent,
            wasFiltered,
            shouldBlock,
            detectedPatterns,
            blockReason
        );
    }

    /**
     * Vérifie si un message contient des informations suspectes sans filtrer
     */
    public boolean containsSensitiveInfo(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        return EMAIL_PATTERN.matcher(content).find() ||
               PHONE_PATTERN.matcher(content).find() ||
               URL_PATTERN.matcher(content).find() ||
               WHATSAPP_PATTERN.matcher(content).find() ||
               TELEGRAM_PATTERN.matcher(content).find() ||
               SOCIAL_MEDIA_PATTERN.matcher(content).find();
    }

    /**
     * Classe pour retourner le résultat du filtrage
     */
    public static class FilterResult {
        private final String filteredContent;
        private final boolean wasFiltered;
        private final boolean shouldBlock;
        private final List<String> detectedPatterns;
        private final String blockReason;

        public FilterResult(String filteredContent, boolean wasFiltered, boolean shouldBlock,
                           List<String> detectedPatterns, String blockReason) {
            this.filteredContent = filteredContent;
            this.wasFiltered = wasFiltered;
            this.shouldBlock = shouldBlock;
            this.detectedPatterns = detectedPatterns;
            this.blockReason = blockReason;
        }

        public String getFilteredContent() {
            return filteredContent;
        }

        public boolean wasFiltered() {
            return wasFiltered;
        }

        public boolean shouldBlock() {
            return shouldBlock;
        }

        public List<String> getDetectedPatterns() {
            return detectedPatterns;
        }

        public String getBlockReason() {
            return blockReason;
        }

        public String getFilteredWordsString() {
            return detectedPatterns.isEmpty() ? null : String.join(", ", detectedPatterns);
        }
    }
}


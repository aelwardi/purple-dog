package com.purple_dog.mvp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Service d'envoi d'emails centralisé pour toute l'application
 * Supporte les emails texte simple et HTML avec templates Thymeleaf
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from:noreply@purpledog.com}")
    private String fromEmail;

    @Value("${app.name:Purple Dog}")
    private String appName;

    @Value("${app.url:http://localhost:3000}")
    private String appUrl;

    /**
     * Envoyer un email texte simple
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            log.info("Sending simple email to: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Envoyer un email HTML avec template
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            log.info("Sending HTML email to: {} using template: {}", to, templateName);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            variables.put("appName", appName);
            variables.put("appUrl", appUrl);

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Envoyer un email avec plusieurs destinataires
     */
    @Async
    public void sendEmailToMultiple(String[] to, String subject, String text) {
        try {
            log.info("Sending email to {} recipients", to.length);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent successfully to {} recipients", to.length);

        } catch (Exception e) {
            log.error("Failed to send email to multiple recipients: {}", e.getMessage());
        }
    }

    /**
     * Envoyer un email HTML avec pièces jointes
     */
    @Async
    public void sendHtmlEmailWithAttachments(
            String to,
            String subject,
            String templateName,
            Map<String, Object> variables,
            Map<String, byte[]> attachments) {

        try {
            log.info("Sending HTML email with {} attachments to: {}",
                    attachments != null ? attachments.size() : 0, to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            variables.put("appName", appName);
            variables.put("appUrl", appUrl);

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setText(htmlContent, true);

            if (attachments != null && !attachments.isEmpty()) {
                for (Map.Entry<String, byte[]> attachment : attachments.entrySet()) {
                    helper.addAttachment(attachment.getKey(),
                        () -> new java.io.ByteArrayInputStream(attachment.getValue()));
                }
            }

            mailSender.send(mimeMessage);
            log.info("HTML email with attachments sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send HTML email with attachments to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Envoyer un email simple avec pièces jointes
     */
    @Async
    public void sendEmailWithAttachments(
            String to,
            String subject,
            String text,
            Map<String, byte[]> attachments) {

        try {
            log.info("Sending email with {} attachments to: {}",
                    attachments != null ? attachments.size() : 0, to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            if (attachments != null && !attachments.isEmpty()) {
                for (Map.Entry<String, byte[]> attachment : attachments.entrySet()) {
                    helper.addAttachment(attachment.getKey(),
                        () -> new java.io.ByteArrayInputStream(attachment.getValue()));
                }
            }

            mailSender.send(mimeMessage);
            log.info("Email with attachments sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email with attachments to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Envoyer un email avec fichier depuis le système de fichiers
     */
    @Async
    public void sendEmailWithFileAttachment(
            String to,
            String subject,
            String text,
            String filePath) {

        try {
            log.info("Sending email with file attachment {} to: {}", filePath, to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // Ajouter le fichier
            java.io.File file = new java.io.File(filePath);
            if (file.exists()) {
                helper.addAttachment(file.getName(), file);
                log.info("File attachment added: {}", file.getName());
            } else {
                log.warn("File not found: {}", filePath);
            }

            mailSender.send(mimeMessage);
            log.info("Email with file attachment sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email with file attachment to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Email de bienvenue après inscription
     */
    public void sendWelcomeEmail(String to, String userName) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "loginUrl", appUrl + "/login"
        );

        sendHtmlEmail(
            to,
            "Bienvenue sur " + appName + " !",
            "welcome-email",
            variables
        );
    }

    /**
     * Email de confirmation de commande
     */
    public void sendOrderConfirmationEmail(String to, String orderNumber, String userName, String totalAmount) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "orderNumber", orderNumber,
            "totalAmount", totalAmount,
            "orderUrl", appUrl + "/orders/" + orderNumber
        );

        sendHtmlEmail(
            to,
            "Confirmation de commande " + orderNumber,
            "order-confirmation",
            variables
        );
    }

    /**
     * Email de notification d'expédition
     */
    public void sendShippingNotificationEmail(String to, String orderNumber, String trackingNumber, String trackingUrl) {
        Map<String, Object> variables = Map.of(
            "orderNumber", orderNumber,
            "trackingNumber", trackingNumber,
            "trackingUrl", trackingUrl
        );

        sendHtmlEmail(
            to,
            "Votre commande " + orderNumber + " a été expédiée",
            "shipping-notification",
            variables
        );
    }

    /**
     * Email de notification de livraison
     */
    public void sendDeliveryNotificationEmail(String to, String orderNumber) {
        Map<String, Object> variables = Map.of(
            "orderNumber", orderNumber,
            "orderUrl", appUrl + "/orders/" + orderNumber
        );

        sendHtmlEmail(
            to,
            "Votre commande " + orderNumber + " a été livrée",
            "delivery-notification",
            variables
        );
    }

    /**
     * Email de confirmation de paiement
     */
    public void sendPaymentConfirmationEmail(String to, String userName, String amount, String paymentId) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "amount", amount,
            "paymentId", paymentId,
            "invoiceUrl", appUrl + "/invoices/" + paymentId
        );

        sendHtmlEmail(
            to,
            "Confirmation de paiement - " + amount,
            "payment-confirmation",
            variables
        );
    }

    /**
     * Email de notification de remboursement
     */
    public void sendRefundNotificationEmail(String to, String orderNumber, String amount, String reason) {
        Map<String, Object> variables = Map.of(
            "orderNumber", orderNumber,
            "amount", amount,
            "reason", reason
        );

        sendHtmlEmail(
            to,
            "Remboursement effectué - Commande " + orderNumber,
            "refund-notification",
            variables
        );
    }

    /**
     * Email de réinitialisation de mot de passe
     */
    public void sendPasswordResetEmail(String to, String userName, String resetToken) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "resetUrl", appUrl + "/reset-password?token=" + resetToken,
            "expirationTime", "24 heures"
        );

        sendHtmlEmail(
            to,
            "Réinitialisation de votre mot de passe",
            "password-reset",
            variables
        );
    }

    /**
     * Email de vérification de compte
     */
    public void sendAccountVerificationEmail(String to, String userName, String verificationToken) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "verificationUrl", appUrl + "/verify?token=" + verificationToken
        );

        sendHtmlEmail(
            to,
            "Vérifiez votre compte " + appName,
            "account-verification",
            variables
        );
    }

    /**
     * Email de notification nouveau message
     */
    public void sendNewMessageNotificationEmail(String to, String senderName, String messagePreview) {
        Map<String, Object> variables = Map.of(
            "senderName", senderName,
            "messagePreview", messagePreview,
            "messagesUrl", appUrl + "/messages"
        );

        sendHtmlEmail(
            to,
            "Nouveau message de " + senderName,
            "new-message-notification",
            variables
        );
    }

    /**
     * Email de réponse au ticket support
     */
    public void sendSupportTicketReplyEmail(String to, String ticketNumber, String replyMessage) {
        Map<String, Object> variables = Map.of(
            "ticketNumber", ticketNumber,
            "replyMessage", replyMessage,
            "ticketUrl", appUrl + "/support/tickets/" + ticketNumber
        );

        sendHtmlEmail(
            to,
            "Réponse à votre ticket " + ticketNumber,
            "support-ticket-reply",
            variables
        );
    }

    /**
     * Email de notification document approuvé
     */
    public void sendDocumentApprovedEmail(String to, String userName, String documentType) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "documentType", documentType,
            "dashboardUrl", appUrl + "/dashboard"
        );

        sendHtmlEmail(
            to,
            "Document approuvé - " + documentType,
            "document-approved",
            variables
        );
    }

    /**
     * Email de notification document rejeté
     */
    public void sendDocumentRejectedEmail(String to, String userName, String documentType, String reason) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "documentType", documentType,
            "reason", reason,
            "uploadUrl", appUrl + "/documents/upload"
        );

        sendHtmlEmail(
            to,
            "Document rejeté - " + documentType,
            "document-rejected",
            variables
        );
    }

    /**
     * Email de notification nouvelle offre sur enchère
     */
    public void sendNewBidNotificationEmail(String to, String productTitle, String bidAmount, String bidderName) {
        Map<String, Object> variables = Map.of(
            "productTitle", productTitle,
            "bidAmount", bidAmount,
            "bidderName", bidderName
        );

        sendHtmlEmail(
            to,
            "Nouvelle enchère sur " + productTitle,
            "new-bid-notification",
            variables
        );
    }

    /**
     * Email de notification fin d'enchère gagnée
     */
    public void sendAuctionWonEmail(String to, String productTitle, String finalAmount) {
        Map<String, Object> variables = Map.of(
            "productTitle", productTitle,
            "finalAmount", finalAmount,
            "checkoutUrl", appUrl + "/checkout"
        );

        sendHtmlEmail(
            to,
            "Félicitations ! Vous avez remporté l'enchère",
            "auction-won",
            variables
        );
    }

    /**
     * Email de notification compte Professional approuvé
     */
    public void sendProfessionalAccountApprovedEmail(String to, String userName) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "dashboardUrl", appUrl + "/professional/dashboard"
        );

        sendHtmlEmail(
            to,
            "Votre compte professionnel a été approuvé",
            "professional-approved",
            variables
        );
    }

    /**
     * Email de newsletter
     */
    public void sendNewsletterEmail(String to, String subject, String content) {
        Map<String, Object> variables = Map.of(
            "content", content,
            "unsubscribeUrl", appUrl + "/unsubscribe"
        );

        sendHtmlEmail(
            to,
            subject,
            "newsletter",
            variables
        );
    }

    /**
     * Email de facture avec PDF en pièce jointe
     */
    public void sendInvoiceEmail(String to, String userName, String invoiceNumber, byte[] invoicePdf) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "invoiceNumber", invoiceNumber,
            "invoiceUrl", appUrl + "/invoices/" + invoiceNumber
        );

        Map<String, byte[]> attachments = Map.of(
            "facture-" + invoiceNumber + ".pdf", invoicePdf
        );

        sendHtmlEmailWithAttachments(
            to,
            "Facture " + invoiceNumber,
            "invoice-email",
            variables,
            attachments
        );
    }

    /**
     * Email avec document certifié (certificat d'authenticité)
     */
    public void sendAuthenticationCertificateEmail(
            String to,
            String userName,
            String productTitle,
            byte[] certificatePdf) {

        Map<String, Object> variables = Map.of(
            "userName", userName,
            "productTitle", productTitle
        );

        Map<String, byte[]> attachments = Map.of(
            "certificat-authenticite.pdf", certificatePdf
        );

        sendHtmlEmailWithAttachments(
            to,
            "Certificat d'authenticité - " + productTitle,
            "certificate-email",
            variables,
            attachments
        );
    }

    /**
     * Email avec rapport d'expertise
     */
    public void sendExpertiseReportEmail(
            String to,
            String userName,
            String productTitle,
            byte[] reportPdf,
            byte[] reportImages) {

        Map<String, Object> variables = Map.of(
            "userName", userName,
            "productTitle", productTitle
        );

        Map<String, byte[]> attachments = Map.of(
            "rapport-expertise.pdf", reportPdf,
            "photos-expertise.zip", reportImages
        );

        sendHtmlEmailWithAttachments(
            to,
            "Rapport d'expertise - " + productTitle,
            "expertise-report",
            variables,
            attachments
        );
    }

    /**
     * Email avec étiquette de livraison
     */
    public void sendShippingLabelEmail(
            String to,
            String orderNumber,
            String trackingNumber,
            byte[] shippingLabelPdf) {

        Map<String, Object> variables = Map.of(
            "orderNumber", orderNumber,
            "trackingNumber", trackingNumber
        );

        Map<String, byte[]> attachments = Map.of(
            "etiquette-" + trackingNumber + ".pdf", shippingLabelPdf
        );

        sendHtmlEmailWithAttachments(
            to,
            "Étiquette de livraison - Commande " + orderNumber,
            "shipping-label-email",
            variables,
            attachments
        );
    }

    /**
     * Email avec contrat de vente
     */
    public void sendSalesContractEmail(
            String to,
            String userName,
            String productTitle,
            byte[] contractPdf) {

        Map<String, Object> variables = Map.of(
            "userName", userName,
            "productTitle", productTitle
        );

        Map<String, byte[]> attachments = Map.of(
            "contrat-vente.pdf", contractPdf
        );

        sendHtmlEmailWithAttachments(
            to,
            "Contrat de vente - " + productTitle,
            "sales-contract",
            variables,
            attachments
        );
    }

    /**
     * Email avec reçu de paiement
     */
    public void sendPaymentReceiptEmail(
            String to,
            String userName,
            String orderNumber,
            String amount,
            byte[] receiptPdf) {

        Map<String, Object> variables = Map.of(
            "userName", userName,
            "orderNumber", orderNumber,
            "amount", amount
        );

        Map<String, byte[]> attachments = Map.of(
            "recu-" + orderNumber + ".pdf", receiptPdf
        );

        sendHtmlEmailWithAttachments(
            to,
            "Reçu de paiement - " + amount + "€",
            "payment-receipt",
            variables,
            attachments
        );
    }

    /**
     * Email avec plusieurs documents (commande complète)
     */
    public void sendCompleteOrderDocumentsEmail(
            String to,
            String userName,
            String orderNumber,
            Map<String, byte[]> documents) {

        Map<String, Object> variables = Map.of(
            "userName", userName,
            "orderNumber", orderNumber,
            "documentsCount", documents.size()
        );

        sendHtmlEmailWithAttachments(
            to,
            "Documents de commande " + orderNumber,
            "order-documents",
            variables,
            documents
        );
    }
}


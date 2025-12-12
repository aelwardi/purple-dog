package com.purple_dog.mvp.services;

import com.purple_dog.mvp.entities.Order;
import com.purple_dog.mvp.entities.Payment;
import com.purple_dog.mvp.entities.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service de notifications par email pour les événements importants
 * - Inscription utilisateur
 * - Création de commande
 * - Paiement confirmé
 * - Paiement échoué
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailSenderService emailSenderService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Envoyer un email de bienvenue lors de l'inscription
     */
    @Async
    public void sendWelcomeEmail(Person user) {
        try {
            log.info("📧 Envoi email de bienvenue à: {}", user.getEmail());

            String subject = "🎉 Bienvenue sur Purple Dog !";

            Map<String, Object> variables = new HashMap<>();
            variables.put("firstName", user.getFirstName());
            variables.put("lastName", user.getLastName());
            variables.put("email", user.getEmail());
            variables.put("role", getRoleName(user.getRole().name()));

            // Essayer d'envoyer avec template HTML, sinon fallback sur email simple
            try {
                emailSenderService.sendHtmlEmail(
                    user.getEmail(),
                    subject,
                    "welcome-email",
                    variables
                );
            } catch (Exception e) {
                log.warn("Template HTML non trouvé, envoi email simple");
                sendSimpleWelcomeEmail(user, subject);
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de bienvenue: {}", e.getMessage());
        }
    }

    /**
     * Envoyer un email simple de bienvenue (fallback)
     */
    private void sendSimpleWelcomeEmail(Person user, String subject) {
        String text = String.format("""
            Bonjour %s %s,
            
            Bienvenue sur Purple Dog ! 🎉
            
            Votre compte %s a été créé avec succès.
            Vous pouvez maintenant vous connecter et profiter de notre plateforme.
            
            Votre email de connexion : %s
            
            Si vous avez des questions, n'hésitez pas à nous contacter.
            
            Cordialement,
            L'équipe Purple Dog
            """,
            user.getFirstName(),
            user.getLastName(),
            getRoleName(user.getRole().name()),
            user.getEmail()
        );

        emailSenderService.sendSimpleEmail(user.getEmail(), subject, text);
    }

    /**
     * Envoyer un email de confirmation de commande
     */
    @Async
    public void sendOrderConfirmationEmail(Order order, Person buyer, Person seller) {
        try {
            log.info("📧 Envoi email de confirmation de commande à: {}", buyer.getEmail());

            String subject = "✅ Confirmation de votre commande #" + order.getOrderNumber();

            Map<String, Object> variables = new HashMap<>();
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("buyerName", buyer.getFirstName() + " " + buyer.getLastName());
            variables.put("sellerName", seller.getFirstName() + " " + seller.getLastName());
            variables.put("productPrice", formatPrice(order.getProductPrice()));
            variables.put("shippingCost", formatPrice(order.getShippingCost()));
            variables.put("platformFee", formatPrice(order.getPlatformFee()));
            variables.put("totalAmount", formatPrice(order.getTotalAmount()));
            variables.put("orderDate", order.getCreatedAt().format(DATE_FORMATTER));
            variables.put("status", getOrderStatusName(order.getStatus().name()));

            try {
                emailSenderService.sendHtmlEmail(
                    buyer.getEmail(),
                    subject,
                    "order-confirmation",
                    variables
                );
            } catch (Exception e) {
                log.warn("Template HTML non trouvé, envoi email simple");
                sendSimpleOrderConfirmationEmail(order, buyer, seller, subject);
            }

            // Notifier aussi le vendeur
            sendOrderNotificationToSeller(order, seller, buyer);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de confirmation: {}", e.getMessage());
        }
    }

    /**
     * Envoyer un email simple de confirmation de commande (fallback)
     */
    private void sendSimpleOrderConfirmationEmail(Order order, Person buyer, Person seller, String subject) {
        String text = String.format("""
            Bonjour %s %s,
            
            Votre commande a été créée avec succès ! ✅
            
            📦 DÉTAILS DE LA COMMANDE
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Numéro de commande : %s
            Date : %s
            Statut : %s
            
            💰 MONTANTS
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Prix du produit : %s
            Frais de livraison : %s
            Frais de plateforme : %s
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            TOTAL : %s
            
            👤 VENDEUR
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            %s %s
            
            Vous recevrez un email de confirmation une fois le paiement validé.
            
            Cordialement,
            L'équipe Purple Dog
            """,
            buyer.getFirstName(),
            buyer.getLastName(),
            order.getOrderNumber(),
            order.getCreatedAt().format(DATE_FORMATTER),
            getOrderStatusName(order.getStatus().name()),
            formatPrice(order.getProductPrice()),
            formatPrice(order.getShippingCost()),
            formatPrice(order.getPlatformFee()),
            formatPrice(order.getTotalAmount()),
            seller.getFirstName(),
            seller.getLastName()
        );

        emailSenderService.sendSimpleEmail(buyer.getEmail(), subject, text);
    }

    /**
     * Notifier le vendeur d'une nouvelle commande
     */
    @Async
    public void sendOrderNotificationToSeller(Order order, Person seller, Person buyer) {
        try {
            log.info("📧 Notification vendeur pour commande: {}", order.getOrderNumber());

            String subject = "🛍️ Nouvelle commande #" + order.getOrderNumber();

            String text = String.format("""
                Bonjour %s %s,
                
                Vous avez reçu une nouvelle commande ! 🎉
                
                📦 DÉTAILS DE LA COMMANDE
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Numéro : %s
                Date : %s
                Montant : %s
                
                👤 ACHETEUR
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                %s %s
                
                Connectez-vous à votre espace vendeur pour gérer cette commande.
                
                Cordialement,
                L'équipe Purple Dog
                """,
                seller.getFirstName(),
                seller.getLastName(),
                order.getOrderNumber(),
                order.getCreatedAt().format(DATE_FORMATTER),
                formatPrice(order.getTotalAmount()),
                buyer.getFirstName(),
                buyer.getLastName()
            );

            emailSenderService.sendSimpleEmail(seller.getEmail(), subject, text);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de notification au vendeur: {}", e.getMessage());
        }
    }

    /**
     * Envoyer un email de confirmation de paiement
     */
    @Async
    public void sendPaymentConfirmationEmail(Payment payment, Person user, Order order) {
        try {
            log.info("📧 Envoi email de confirmation de paiement à: {}", user.getEmail());

            String subject = "✅ Paiement confirmé - Commande #" + order.getOrderNumber();

            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFirstName() + " " + user.getLastName());
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("amount", formatPrice(payment.getAmount()));
            variables.put("paymentDate", payment.getCreatedAt().format(DATE_FORMATTER));
            variables.put("paymentId", payment.getId().toString());
            variables.put("paymentMethod", "Carte bancaire");

            try {
                emailSenderService.sendHtmlEmail(
                    user.getEmail(),
                    subject,
                    "payment-confirmation",
                    variables
                );
            } catch (Exception e) {
                log.warn("Template HTML non trouvé, envoi email simple");
                sendSimplePaymentConfirmationEmail(payment, user, order, subject);
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de confirmation de paiement: {}", e.getMessage());
        }
    }

    /**
     * Envoyer un email simple de confirmation de paiement (fallback)
     */
    private void sendSimplePaymentConfirmationEmail(Payment payment, Person user, Order order, String subject) {
        String text = String.format("""
            Bonjour %s %s,
            
            Votre paiement a été confirmé avec succès ! ✅
            
            💳 DÉTAILS DU PAIEMENT
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Montant payé : %s
            Date : %s
            ID de paiement : #%s
            Méthode : %s
            
            📦 COMMANDE ASSOCIÉE
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Numéro : %s
            Montant total : %s
            
            Votre commande est maintenant en cours de traitement.
            Vous recevrez un email dès que votre colis sera expédié.
            
            Merci de votre confiance !
            
            Cordialement,
            L'équipe Purple Dog
            """,
            user.getFirstName(),
            user.getLastName(),
            formatPrice(payment.getAmount()),
            payment.getCreatedAt().format(DATE_FORMATTER),
            payment.getId(),
            "Carte bancaire",
            order.getOrderNumber(),
            formatPrice(order.getTotalAmount())
        );

        emailSenderService.sendSimpleEmail(user.getEmail(), subject, text);
    }

    /**
     * Envoyer un email en cas d'échec de paiement
     */
    @Async
    public void sendPaymentFailedEmail(Payment payment, Person user, Order order, String errorMessage) {
        try {
            log.info("📧 Envoi email d'échec de paiement à: {}", user.getEmail());

            String subject = "❌ Échec du paiement - Commande #" + order.getOrderNumber();

            String text = String.format("""
                Bonjour %s %s,
                
                Nous n'avons pas pu traiter votre paiement. ❌
                
                💳 DÉTAILS DU PAIEMENT
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Montant : %s
                Date de la tentative : %s
                Raison : %s
                
                📦 COMMANDE
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Numéro : %s
                
                🔄 PROCHAINES ÉTAPES
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Veuillez :
                1. Vérifier les informations de votre carte bancaire
                2. Vous assurer d'avoir suffisamment de fonds
                3. Réessayer le paiement depuis votre espace client
                
                Si le problème persiste, contactez notre service client.
                
                Cordialement,
                L'équipe Purple Dog
                """,
                user.getFirstName(),
                user.getLastName(),
                formatPrice(payment.getAmount()),
                payment.getCreatedAt().format(DATE_FORMATTER),
                errorMessage != null ? errorMessage : "Erreur inconnue",
                order.getOrderNumber()
            );

            emailSenderService.sendSimpleEmail(user.getEmail(), subject, text);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email d'échec de paiement: {}", e.getMessage());
        }
    }

    /**
     * Envoyer un email de réinitialisation de mot de passe
     */
    @Async
    public void sendPasswordResetEmail(Person user, String resetToken) {
        try {
            log.info("📧 Envoi email de réinitialisation de mot de passe à: {}", user.getEmail());

            String subject = "🔑 Réinitialisation de votre mot de passe";

            String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;

            String text = String.format("""
                Bonjour %s %s,
                
                Vous avez demandé à réinitialiser votre mot de passe.
                
                Cliquez sur le lien ci-dessous pour créer un nouveau mot de passe :
                
                %s
                
                ⚠️ Ce lien expire dans 1 heure.
                
                Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.
                Votre mot de passe actuel restera inchangé.
                
                Cordialement,
                L'équipe Purple Dog
                """,
                user.getFirstName(),
                user.getLastName(),
                resetUrl
            );

            emailSenderService.sendSimpleEmail(user.getEmail(), subject, text);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de réinitialisation: {}", e.getMessage());
        }
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Formater un montant en devise
     */
    private String formatPrice(BigDecimal amount) {
        if (amount == null) return "0,00 €";
        return String.format("%,.2f €", amount);
    }

    /**
     * Obtenir le nom du rôle en français
     */
    private String getRoleName(String role) {
        return switch (role) {
            case "INDIVIDUAL" -> "Particulier";
            case "PROFESSIONAL" -> "Professionnel";
            case "ADMIN" -> "Administrateur";
            default -> role;
        };
    }

    /**
     * Obtenir le nom du statut de commande en français
     */
    private String getOrderStatusName(String status) {
        return switch (status) {
            case "PENDING" -> "En attente";
            case "PAYMENT_PENDING" -> "Paiement en attente";
            case "PAID" -> "Payée";
            case "PROCESSING" -> "En cours de traitement";
            case "SHIPPED" -> "Expédiée";
            case "DELIVERED" -> "Livrée";
            case "COMPLETED" -> "Terminée";
            case "CANCELLED" -> "Annulée";
            default -> status;
        };
    }
}


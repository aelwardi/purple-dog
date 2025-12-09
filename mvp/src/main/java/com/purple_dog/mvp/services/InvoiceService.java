package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.InvoiceRepository;
import com.purple_dog.mvp.dao.OrderRepository;
import com.purple_dog.mvp.dto.InvoiceCreateDTO;
import com.purple_dog.mvp.dto.InvoiceResponseDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final EmailSenderService emailSenderService;

    @Value("${app.invoice.storage-path:/invoices}")
    private String invoiceStoragePath;

    /**
     * Créer une facture pour une commande
     */
    public InvoiceResponseDTO createInvoice(InvoiceCreateDTO dto) {
        log.info("Creating invoice for order: {}", dto.getOrderId());

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        if (invoiceRepository.existsByOrderId(dto.getOrderId())) {
            throw new InvalidOperationException("Invoice already exists for order: " + dto.getOrderId());
        }

        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.SHIPPED
                && order.getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidOperationException("Cannot create invoice for unpaid order");
        }

        String invoiceNumber = generateInvoiceNumber();

        String pdfUrl = generateInvoicePdf(order, invoiceNumber);

        Invoice invoice = Invoice.builder()
                .order(order)
                .invoiceNumber(invoiceNumber)
                .pdfUrl(pdfUrl)
                .issuedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        invoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully: {}", invoiceNumber);

        try {
            sendInvoiceByEmail(invoice);
        } catch (Exception e) {
            log.error("Failed to send invoice email: {}", e.getMessage());
        }

        return mapToResponseDTO(invoice);
    }

    /**
     * Récupérer une facture par ID
     */
    public InvoiceResponseDTO getInvoiceById(Long invoiceId) {
        log.info("Fetching invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        return mapToResponseDTO(invoice);
    }

    /**
     * Récupérer une facture par numéro
     */
    public InvoiceResponseDTO getInvoiceByNumber(String invoiceNumber) {
        log.info("Fetching invoice by number: {}", invoiceNumber);

        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with number: " + invoiceNumber));

        return mapToResponseDTO(invoice);
    }

    /**
     * Récupérer la facture d'une commande
     */
    public InvoiceResponseDTO getInvoiceByOrderId(Long orderId) {
        log.info("Fetching invoice for order: {}", orderId);

        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No invoice found for order: " + orderId));

        return mapToResponseDTO(invoice);
    }

    /**
     * Récupérer les factures d'un acheteur
     */
    public List<InvoiceResponseDTO> getBuyerInvoices(Long buyerId) {
        log.info("Fetching invoices for buyer: {}", buyerId);

        return invoiceRepository.findByBuyerId(buyerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les factures d'un vendeur
     */
    public List<InvoiceResponseDTO> getSellerInvoices(Long sellerId) {
        log.info("Fetching invoices for seller: {}", sellerId);

        return invoiceRepository.findBySellerId(sellerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les factures par période
     */
    public List<InvoiceResponseDTO> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching invoices from {} to {}", startDate, endDate);

        return invoiceRepository.findByDateRange(startDate, endDate).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Renvoyer une facture par email
     */
    public void resendInvoiceEmail(Long invoiceId) {
        log.info("Resending invoice email: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        sendInvoiceByEmail(invoice);
        log.info("Invoice email resent successfully");
    }

    /**
     * Télécharger le PDF d'une facture
     */
    public byte[] downloadInvoicePdf(Long invoiceId) {
        log.info("Downloading invoice PDF: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        // TODO: Lire le PDF depuis le système de fichiers ou S3
        return new byte[0];
    }

    /**
     * Compter les factures d'un acheteur
     */
    public long countBuyerInvoices(Long buyerId) {
        return invoiceRepository.countByBuyerId(buyerId);
    }

    /**
     * Compter les factures d'un vendeur
     */
    public long countSellerInvoices(Long sellerId) {
        return invoiceRepository.countBySellerId(sellerId);
    }

    // Méthodes privées

    /**
     * Générer un numéro de facture unique
     * Format: INV-YYYYMMDD-XXXXX
     */
    private String generateInvoiceNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%05d", (int) (Math.random() * 100000));
        String invoiceNumber = "INV-" + date + "-" + randomPart;

        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            randomPart = String.format("%05d", (int) (Math.random() * 100000));
            invoiceNumber = "INV-" + date + "-" + randomPart;
        }

        return invoiceNumber;
    }

    /**
     * Générer le PDF de la facture
     */
    private String generateInvoicePdf(Order order, String invoiceNumber) {
        // TODO: Intégration avec une bibliothèque PDF (iText, Flying Saucer, etc.)
        String pdfPath = invoiceStoragePath + "/" + invoiceNumber + ".pdf";

        log.info("Invoice PDF generated: {}", pdfPath);
        return pdfPath;
    }

    /**
     * Envoyer la facture par email
     */
    private void sendInvoiceByEmail(Invoice invoice) {
        Order order = invoice.getOrder();
        Person buyer = order.getBuyer();

        // TODO: Générer le PDF réel et l'envoyer en pièce jointe
        emailSenderService.sendSimpleEmail(
            buyer.getEmail(),
            "Facture " + invoice.getInvoiceNumber(),
            String.format(
                "Bonjour %s,\n\n" +
                "Veuillez trouver ci-joint votre facture %s pour la commande %s.\n\n" +
                "Montant total: %.2f €\n\n" +
                "Cordialement,\n" +
                "L'équipe Purple Dog",
                buyer.getFirstName(),
                invoice.getInvoiceNumber(),
                order.getOrderNumber(),
                order.getTotalAmount()
            )
        );
    }

    private InvoiceResponseDTO mapToResponseDTO(Invoice invoice) {
        Order order = invoice.getOrder();
        Person buyer = order.getBuyer();
        Person seller = order.getSeller();

        Address billingAddress = order.getBillingAddress();
        String buyerAddress = billingAddress != null ?
                String.format("%s, %s %s, %s",
                    billingAddress.getStreet(),
                    billingAddress.getPostalCode(),
                    billingAddress.getCity(),
                    billingAddress.getCountry()) : "N/A";

        return InvoiceResponseDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .pdfUrl(invoice.getPdfUrl())
                .issuedAt(invoice.getIssuedAt())
                .createdAt(invoice.getCreatedAt())
                .buyerName(buyer.getFirstName() + " " + buyer.getLastName())
                .buyerEmail(buyer.getEmail())
                .buyerAddress(buyerAddress)
                .sellerName(seller.getFirstName() + " " + seller.getLastName())
                .sellerEmail(seller.getEmail())
                .productPrice(order.getProductPrice())
                .shippingCost(order.getShippingCost())
                .platformFee(order.getPlatformFee())
                .totalAmount(order.getTotalAmount())
                .build();
    }
}
package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.InvoiceCreateDTO;
import com.purple_dog.mvp.dto.InvoiceResponseDTO;
import com.purple_dog.mvp.services.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    /**
     * Créer une facture pour une commande
     */
    @PostMapping
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@Valid @RequestBody InvoiceCreateDTO dto) {
        log.info("Request to create invoice for order: {}", dto.getOrderId());
        InvoiceResponseDTO response = invoiceService.createInvoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer une facture par ID
     */
    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long invoiceId) {
        log.info("Request to get invoice: {}", invoiceId);
        InvoiceResponseDTO invoice = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(invoice);
    }

    /**
     * Récupérer une facture par numéro
     */
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        log.info("Request to get invoice by number: {}", invoiceNumber);
        InvoiceResponseDTO invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(invoice);
    }

    /**
     * Récupérer la facture d'une commande
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByOrderId(@PathVariable Long orderId) {
        log.info("Request to get invoice for order: {}", orderId);
        InvoiceResponseDTO invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }

    /**
     * Récupérer les factures d'un acheteur
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<InvoiceResponseDTO>> getBuyerInvoices(@PathVariable Long buyerId) {
        log.info("Request to get invoices for buyer: {}", buyerId);
        List<InvoiceResponseDTO> invoices = invoiceService.getBuyerInvoices(buyerId);
        return ResponseEntity.ok(invoices);
    }

    /**
     * Récupérer les factures d'un vendeur
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<InvoiceResponseDTO>> getSellerInvoices(@PathVariable Long sellerId) {
        log.info("Request to get invoices for seller: {}", sellerId);
        List<InvoiceResponseDTO> invoices = invoiceService.getSellerInvoices(sellerId);
        return ResponseEntity.ok(invoices);
    }

    /**
     * Récupérer les factures par période
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<InvoiceResponseDTO>> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Request to get invoices from {} to {}", startDate, endDate);
        List<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate);
        return ResponseEntity.ok(invoices);
    }

    /**
     * Renvoyer une facture par email
     */
    @PostMapping("/{invoiceId}/resend-email")
    public ResponseEntity<Void> resendInvoiceEmail(@PathVariable Long invoiceId) {
        log.info("Request to resend invoice email: {}", invoiceId);
        invoiceService.resendInvoiceEmail(invoiceId);
        return ResponseEntity.ok().build();
    }

    /**
     * Télécharger le PDF d'une facture
     */
    @GetMapping("/{invoiceId}/download")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long invoiceId) {
        log.info("Request to download invoice PDF: {}", invoiceId);

        byte[] pdfBytes = invoiceService.downloadInvoicePdf(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + invoiceId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Compter les factures d'un acheteur
     */
    @GetMapping("/buyer/{buyerId}/count")
    public ResponseEntity<Long> countBuyerInvoices(@PathVariable Long buyerId) {
        log.info("Request to count invoices for buyer: {}", buyerId);
        long count = invoiceService.countBuyerInvoices(buyerId);
        return ResponseEntity.ok(count);
    }

    /**
     * Compter les factures d'un vendeur
     */
    @GetMapping("/seller/{sellerId}/count")
    public ResponseEntity<Long> countSellerInvoices(@PathVariable Long sellerId) {
        log.info("Request to count invoices for seller: {}", sellerId);
        long count = invoiceService.countSellerInvoices(sellerId);
        return ResponseEntity.ok(count);
    }
}


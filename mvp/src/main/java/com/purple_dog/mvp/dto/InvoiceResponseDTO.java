package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDTO {

    private Long id;
    private String invoiceNumber;
    private Long orderId;
    private String orderNumber;
    private String pdfUrl;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;

    private String buyerName;
    private String buyerEmail;
    private String buyerAddress;

    private String sellerName;
    private String sellerEmail;

    private BigDecimal productPrice;
    private BigDecimal shippingCost;
    private BigDecimal platformFee;
    private BigDecimal totalAmount;
}


package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.*;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PersonRepository personRepository;
    private final FavoriteRepository favoriteRepository;
    private final QuickSaleRepository quickSaleRepository;
    private final AuctionRepository auctionRepository;

    /**
     * Crée un produit et prépare les photos/documents.
     */
    public ProductResponse createProduct(ProductCreateRequest request) {
        validateCreateRequest(request);

        Person seller = personRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ProductException("Seller not found with id: " + request.getSellerId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ProductException("Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        product.setSeller(seller);
        product.setCategory(category);
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setProductCondition(request.getProductCondition());
        product.setSaleType(request.getSaleType());
        product.setEstimatedValue(request.getEstimatedValue());
        product.setBrand(request.getBrand());
        product.setYearOfManufacture(request.getYearOfManufacture());
        product.setOrigin(request.getOrigin());
        product.setAuthenticityCertificate(request.getAuthenticityCertificate());
        product.setHasDocumentation(Boolean.TRUE.equals(request.getHasDocumentation()));
        product.setWidthCm(request.getWidthCm());
        product.setHeightCm(request.getHeightCm());
        product.setDepthCm(request.getDepthCm());
        product.setWeightKg(request.getWeightKg());
        product.setStatus(ProductStatus.ACTIVE);

        // Photos
        int order = 0;
        for (String url : request.getPhotoUrls()) {
            Photo photo = new Photo();
            photo.setProduct(product);
            photo.setUrl(url);
            photo.setDisplayOrder(order++);
            photo.setIsPrimary(order == 1);
            product.getPhotos().add(photo);
        }

        // Documents
        if (request.getDocuments() != null) {
            for (DocumentUploadDTO docReq : request.getDocuments()) {
                Document doc = new Document();
                doc.setProduct(product);
                doc.setFileName(docReq.getFileName());
                doc.setFileUrl(docReq.getFileUrl());
                doc.setDocumentType(docReq.getDocumentType());
                doc.setDescription(docReq.getDescription());
                product.getDocuments().add(doc);
            }
        }

        Product saved = productRepository.save(product);

        // Create QuickSale or Auction based on saleType
        if (saved.getSaleType() == SaleType.QUICK_SALE) {
            QuickSale quickSale = QuickSale.builder()
                    .product(saved)
                    .fixedPrice(saved.getEstimatedValue())
                    .isAvailable(true)
                    .build();
            quickSaleRepository.save(quickSale);
            saved.setQuickSale(quickSale);
            productRepository.save(saved);
        } else if (saved.getSaleType() == SaleType.AUCTION) {
            // For auction, create Auction entity if needed
            Auction auction = new Auction();
            auction.setProduct(saved);
            auction.setStartingPrice(saved.getEstimatedValue());
            auction.setCurrentPrice(saved.getEstimatedValue());
            auction.setStatus(AuctionStatus.ACTIVE);
            auctionRepository.save(auction);
            saved.setAuction(auction);
            productRepository.save(saved);
        }

        return toResponse(saved);
    }

    /**
     * Met à jour un produit existant.
     */
    public ProductResponse updateProduct(Long id, ProductCreateRequest request) {
        validateCreateRequest(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with id: " + id));

        // Vérifier que le vendeur est le propriétaire du produit
        if (!product.getSeller().getId().equals(request.getSellerId())) {
            throw new ProductException("You are not authorized to update this product");
        }

        // Mettre à jour les champs
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ProductException("Category not found with id: " + request.getCategoryId()));

        product.setCategory(category);
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setProductCondition(request.getProductCondition());
        product.setSaleType(request.getSaleType());
        product.setEstimatedValue(request.getEstimatedValue());
        product.setBrand(request.getBrand());
        product.setYearOfManufacture(request.getYearOfManufacture());
        product.setOrigin(request.getOrigin());
        product.setAuthenticityCertificate(request.getAuthenticityCertificate());
        product.setHasDocumentation(Boolean.TRUE.equals(request.getHasDocumentation()));
        product.setWidthCm(request.getWidthCm());
        product.setHeightCm(request.getHeightCm());
        product.setDepthCm(request.getDepthCm());
        product.setWeightKg(request.getWeightKg());

        // Mise à jour des photos si nécessaire
        if (request.getPhotoUrls() != null && !request.getPhotoUrls().isEmpty()) {
            product.getPhotos().clear();
            int order = 0;
            for (String url : request.getPhotoUrls()) {
                Photo photo = new Photo();
                photo.setProduct(product);
                photo.setUrl(url);
                photo.setDisplayOrder(order++);
                photo.setIsPrimary(order == 1);
                product.getPhotos().add(photo);
            }
        }

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    /**
     * Supprime un produit.
     */
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with id: " + id));

        // Ne pas supprimer si le produit est vendu
        if (product.getStatus() == ProductStatus.SOLD) {
            throw new ProductException("Cannot delete a sold product");
        }

        // Supprimer les photos et documents associés (cascade devrait gérer ça)
        productRepository.delete(product);
    }

    /**
     * Récupération d'un produit par id.
     */
    public ProductResponse getProduct(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ProductException("Product not found with id: " + id));
    }

    /**
     * Produits d'un vendeur.
     */
    public List<ProductResponse> getProductsBySeller(Long sellerId, ProductStatus status) {
        List<Product> products = status == null
                ? productRepository.findAllBySellerId(sellerId)
                : productRepository.findAllBySellerIdAndStatus(sellerId, status);
        return products.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Recherche avec filtres.
     */
    public List<ProductResponse> search(ProductSearchRequest req) {
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        if (req.getText() != null && !req.getText().isBlank()) {
            String like = "%" + req.getText().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)));
        }
        if (req.getCategoryId() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("category").get("id"), req.getCategoryId()));
        }
        if (req.getSaleType() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("saleType"), req.getSaleType()));
        }
        if (req.getStatus() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), req.getStatus()));
        }
        if (req.getMinPrice() != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("estimatedValue"), req.getMinPrice()));
        }
        if (req.getMaxPrice() != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("estimatedValue"), req.getMaxPrice()));
        }
        if (Boolean.TRUE.equals(req.getAvailableOnly())) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), ProductStatus.ACTIVE));
        }

        return productRepository.findAll(spec).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Ajoute un favori.
     */
    public void addFavorite(Long userId, Long productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            return;
        }
        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new ProductException("User not found with id: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found with id: " + productId));
        Favorite fav = new Favorite();
        fav.setUser(user);
        fav.setProduct(product);
        favoriteRepository.save(fav);
    }

    /**
     * Supprime un favori.
     */
    public void removeFavorite(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    /**
     * Liste les produits favoris d'un utilisateur.
     */
    public List<ProductResponse> listFavorites(Long userId) {
        return favoriteRepository.findByUserIdWithProducts(userId).stream()
                .map(Favorite::getProduct)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void validateCreateRequest(ProductCreateRequest request) {
        if (request.getSellerId() == null) {
            throw new ProductException("Seller id is required");
        }
        if (request.getCategoryId() == null) {
            throw new ProductException("Category id is required");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ProductException("Title is required");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ProductException("Description is required");
        }
        if (request.getProductCondition() == null) {
            throw new ProductException("Product condition is required");
        }
        if (request.getSaleType() == null) {
            throw new ProductException("Sale type is required");
        }
        if (request.getEstimatedValue() == null || request.getEstimatedValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductException("Estimated value must be positive");
        }
        // Photo validation: minimum 5, maximum 10
        if (request.getPhotoUrls() == null || request.getPhotoUrls().isEmpty()) {
            throw new ProductException("At least 5 photos are required");
        }
        if (request.getPhotoUrls().size() < 5) {
            throw new ProductException("At least 5 photos are required");
        }
        if (request.getPhotoUrls().size() > 10) {
            throw new ProductException("Maximum 10 photos allowed");
        }
    }

    /**
     * Compat pour services existants
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with id: " + id));
    }

    public boolean productExists(Long id) {
        return productRepository.existsById(id);
    }

    private ProductResponse toResponse(Product product) {
        List<PhotoResponse> photos = product.getPhotos().stream()
                .map(p -> new PhotoResponse(p.getId(), p.getUrl(), p.getDisplayOrder(), p.getIsPrimary()))
                .collect(Collectors.toList());

        List<DocumentResponseDTO> docs = product.getDocuments().stream()
                .map(d -> new DocumentResponseDTO(
                        d.getId(),
                        d.getProduct().getSeller().getId(),
                        d.getDocumentType(),
                        d.getFileName(),
                        d.getFileUrl(),
                        d.getFileType(),
                        d.getFileSize(),
                        d.getDescription(),
                        d.getStatus(),
                        d.getRejectionReason(),
                        d.getVerifiedAt(),
                        d.getVerifiedBy() != null ? d.getVerifiedBy().getId() : null,
                        d.getVerifiedBy() != null
                                ? d.getVerifiedBy().getFirstName() + " " + d.getVerifiedBy().getLastName()
                                : null,
                        d.getUploadedAt()))
                .collect(Collectors.toList());

        // Seller info
        ProductResponse.SellerInfo seller = new ProductResponse.SellerInfo(
                product.getSeller().getId(),
                product.getSeller().getFirstName(),
                product.getSeller().getLastName(),
                product.getSeller().getEmail()
        );

        // Category info
        ProductResponse.CategoryInfo category = new ProductResponse.CategoryInfo(
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getDescription()
        );

        // Determine price: prefer QuickSale.fixedPrice when available
        java.math.BigDecimal responsePrice = product.getEstimatedValue();
        ProductStatus responseStatus = product.getStatus();

        Long quickSaleId = null;
        Long auctionId = null;
        AuctionResponse auctionDto = null; // DTO to include in product response

        try {
            QuickSale qs = product.getQuickSale();
            if (qs != null) {
                quickSaleId = qs.getId();
                if (qs.getIsAvailable() != null && qs.getIsAvailable()) {
                    responsePrice = qs.getFixedPrice();
                } else if (qs.getIsAvailable() != null && !qs.getIsAvailable()) {
                    // If quick sale exists but is not available, ensure product reported as SOLD
                    responseStatus = ProductStatus.SOLD;
                    // If there was a fixed price, still expose it as last price
                    if (qs.getFixedPrice() != null) {
                        responsePrice = qs.getFixedPrice();
                    }
                }
            }

            Auction auction = product.getAuction();
            if (auction != null) {
                auctionId = auction.getId();
                // For auctions, price is currentPrice if available
                if (auction.getCurrentPrice() != null) {
                    responsePrice = auction.getCurrentPrice();
                }
                // Build AuctionResponse DTO for frontend use
                auctionDto = new AuctionResponse(
                        auction.getId(),
                        product.getId(),
                        auction.getReservePrice(),
                        auction.getStartingPrice(),
                        auction.getReservePrice(),
                        auction.getCurrentPrice(),
                        auction.getBidIncrement(),
                        auction.getStartDate(),
                        auction.getEndDate(),
                        auction.getStatus(),
                        auction.getReservePriceMet(),
                        auction.getWinner() != null ? auction.getWinner().getId() : null,
                        auction.getTotalBids()
                );
            }
        } catch (Exception e) {
            // In case lazy loading or other issue occurs, fallback silently
            // log could be added here if desired
        }

        return new ProductResponse(
                product.getId(),
                product.getSeller().getId(),
                product.getCategory().getId(),
                product.getTitle(),
                product.getDescription(),
                product.getProductCondition(),
                responseStatus,
                product.getSaleType(),
                responsePrice, // price
                product.getEstimatedValue(), // estimatedValue
                product.getBrand(),
                product.getYearOfManufacture(),
                product.getOrigin(),
                product.getAuthenticityCertificate(),
                product.getHasDocumentation(),
                product.getWidthCm(),
                product.getHeightCm(),
                product.getDepthCm(),
                product.getWeightKg(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                photos,
                docs,
                seller,
                category,
                quickSaleId,
                auctionId,
                auctionDto
        );
    }
}

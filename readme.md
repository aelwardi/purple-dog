# PURPLE DOG - Plateforme de Vente d'Objets de Valeur

## Documentation de la Base de Données

Cette documentation complète contient :
---

## Structure de la Base de Données

## Diagramme Simplifié des Relations Principales

```
┌─────────────────────────────────────────────────────────────────┐
│                        PERSON (Abstract)                         │
│  ┌────────────┐    ┌──────────────┐    ┌──────────────┐        │
│  │ Particulier│    │ Professional │    │    Admin     │        │
│  │  (vend)    │    │(achète/vend) │    │   (gère)     │        │
│  └────────────┘    └──────────────┘    └──────────────┘        │
└─────────────────────────────────────────────────────────────────┘
           │                   │                    │
           │                   │                    │
           ▼                   ▼                    ▼
    ┌───────────┐      ┌─────────────┐     ┌──────────────┐
    │  Address  │      │  Specialty  │     │Support Ticket│
    │ Favorite  │      │   Forfait   │     │   Settings   │
    │Notification│      │  Interest   │     └──────────────┘
    └───────────┘      └─────────────┘
           │
           ▼
    ┌──────────────────────────────────────────────┐
    │              PRODUCT                          │
    │  ┌────────┐  ┌────────┐  ┌────────────┐     │
    │  │ Photos │  │Document│  │  Category  │     │
    │  └────────┘  └────────┘  └────────────┘     │
    │                                               │
    │  ┌─────────────────┐  ┌─────────────────┐  │
    │  │   ENCHERES      │  │  VENTE RAPIDE   │  │
    │  │  (Auction)      │  │  (Quick Sale)   │  │
    │  │                 │  │                 │  │
    │  │  ┌──────────┐   │  │  ┌──────────┐  │  │
    │  │  │   BID    │   │  │  │  OFFER   │  │  │
    │  │  └──────────┘   │  │  └──────────┘  │  │
    │  └─────────────────┘  └─────────────────┘  │
    └──────────────────────────────────────────────┘
                          │
                          ▼
              ┌─────────────────────┐
              │       ORDER         │
              │                     │
              │  ┌────────────┐    │
              │  │  Payment   │    │
              │  │  Livraison │    │
              │  │  Facture   │    │
              │  └────────────┘    │
              └─────────────────────┘
```


### Tables Principales (31 au total)

#### Utilisateurs
- **Person** (table parent abstraite)
  - Particulier (hérite de Person)
  - Professional (hérite de Person)  
  - Admin (hérite de Person)
- **Address** - Adresses des utilisateurs
- **Specialty** - Spécialités des professionnels

#### Catégories et Produits
- **Category** - 13 catégories d'objets
- **Product** - Objets mis en vente
- **Photo** - Photos des produits (min 10)
- **Document** - Documents/certificats

#### Ventes
- **Encheres** - Système d'enchères
- **Bid** - Enchères individuelles
- **VenteRapide** - Ventes rapides
- **Offer** - Offres sur ventes rapides
- **Favorite** - Favoris des utilisateurs

#### Commandes et Paiements
- **Order** - Commandes
- **Payment** - Paiements Stripe
- **Livraison** - Gestion des livraisons
- **Transporteur** - Liste des transporteurs
- **Facture** - Factures générées

#### Abonnements
- **Forfait** - Plans d'abonnement
- **Feature** - Fonctionnalités débloquables

#### Communication
- **Conversation** - Conversations entre users
- **Message** - Messages dans conversations
- **SupportTicket** - Tickets support
- **TicketMessage** - Messages dans tickets

#### Notifications
- **Notification** - Notifications in-app et email
- **Alert** - Alertes configurables

#### Système
- **PlatformReview** - Avis sur la plateforme
- **PlatformSettings** - Configuration globale
- **ActivityLog** - Audit trail

---

## Relations Clés

### Relations One-to-One
- Product ↔ Encheres
- Product ↔ VenteRapide
- Product ↔ Order
- Order ↔ Payment
- Order ↔ Livraison
- Order ↔ Facture

### Relations One-to-Many
- Person → Address (1:N)
- Person → Product (1:N)
- Product → Photo (1:N, min 10)
- Product → Document (1:N)
- Encheres → Bid (1:N)
- Category → Product (1:N)

### Relations Many-to-Many
- Professional ↔ Specialty
- Professional ↔ Category (intérêts)
- Forfait ↔ Feature
- Conversation ↔ Person (participants)

---

## Fonctionnalités Principales

### Pour les Particuliers
Inscription gratuite
Vente d'objets uniquement
Mode enchères ou vente rapide
Suivi des ventes
Messagerie avec acheteurs

### Pour les Professionnels  
Forfait 49€/mois (1 mois gratuit)
Achat et vente d'objets
Participation aux enchères
Offres sur ventes rapides
Recherche avancée avec filtres
Favoris et historique
Notifications email/in-app

### Pour les Admins
Gestion des utilisateurs
Modération des produits
Configuration des commissions
Gestion des transporteurs
Gestion des forfaits/features
Support tickets
Statistiques et analytics

---

## Système d'Enchères

### Fonctionnement
- Durée : **7 jours** par défaut
- Prix de départ : **-10%** du prix souhaité (modifiable)
- Extension : **+10 minutes** si enchère à H-1
- Prix de réserve : Prix minimum souhaité par le vendeur

### Paliers d'Enchères
- < 100€ → paliers de **10€**
- 100-500€ → paliers de **50€**
- 500-1000€ → paliers de **100€**
- 1000-5000€ → paliers de **200€**
- etc.

### Enchères Automatiques
- L'acheteur définit un montant max
- Le système enchérit automatiquement par paliers
- Notification si le montant max est atteint

---

## Flux de Paiement

1. **Validation de l'achat** → Professional valide dans 24h
2. **Paiement Stripe** → Fonds **bloqués** (HELD)
3. **Livraison planifiée** → Transporteur récupère l'objet
4. **Signature digitale** → Acheteur signe la réception
5. **Libération des fonds** → 3-5 jours après signature
6. **Facture générée** → PDF automatique

### Commissions
- **Acheteur** : +3% du prix
- **Vendeur** : -2% du prix
- **Modifiable** par catégorie via admin

---

## Contraintes de Publication

### Photos
- Minimum **10 photos** obligatoires
- Format : JPG, PNG
- Taille max : **10 MB** par photo
- Types : avant, arrière, dessus, dessous, signature, détails

### Documents
- Certificat d'authenticité
- Preuve d'achat
- Taille max : **20 MB** par document

### Informations Obligatoires
- Nom de l'objet
- Catégorie
- Description détaillée
- Dimensions (L x H x P en cm)
- Poids (en kg)
- Prix souhaité
- Mode de vente (Enchères ou Vente Rapide)

---

## Données Initiales

### Catégories (13)
1. Bijoux & montres
2. Meubles anciens
3. Objets d'art & tableaux
4. Objets de collection
5. Vins & spiritueux
6. Instruments de musique
7. Livres anciens & manuscrits
8. Mode & accessoires de luxe
9. Horlogerie & pendules
10. Photographies anciennes
11. Vaisselle & argenterie
12. Sculptures & objets décoratifs
13. Véhicules de collection

### Forfaits (2)
1. **Particulier Gratuit** (0€)
2. **Professionnel** (49€/mois, 1er mois gratuit)

### Features (6)
1. Annonces illimitées
2. Accès aux enchères
3. Accès vente rapide
4. Recherche avancée
5. Support prioritaire
6. Analytics dashboard

### Transporteurs (5)
1. Cocolis
2. The Packengers
3. DHL Express
4. UPS
5. Colissimo

---

## Sécurité

### Encryption
- Mots de passe : **BCrypt** (cost 12+)
- API Keys : **AES-256**
- SIRET : **Encrypted**

### RGPD
- Anonymisation des particuliers
- Consentement newsletter
- Droit à l'oubli (soft delete)
- Export des données

### Audit
- Activity Log pour toutes les actions
- Rétention 2 ans minimum

---

## Statistiques Disponibles

### Métriques Business
- Volume de ventes par catégorie
- Temps moyen de vente
- Prix moyen par catégorie
- Taux de réussite des enchères
- GMV (Gross Merchandise Value)
- Chiffre d'affaires commissions

### Métriques Utilisateurs
- DAU / MAU / WAU
- Taux de conversion
- Taux de rétention
- NPS moyen

---

##  Stack Technique

### Backend
- **Framework** : Spring Boot 3.x
- **Database** : PostgreSQL 14+
- **Cache** : Redis
- **Message Queue** : RabbitMQ
- **WebSocket** : Spring WebSocket
- **Storage** : AWS S3 / MinIO
- **Email** : SendGrid / AWS SES
- **Payment** : Stripe API

### Frontend
- **Framework** : React
- **Real-time** : Socket.io
- **State** : Redux / Vuex
- **UI** : Material-UI / Ant Design

---

# PURPLE DOG - Structure Complète de la Base de Données

## Diagramme des Relations

```
Person (Abstract)
├── Particulier (Inheritance)
├── Professional (Inheritance)
└── Admin (Inheritance)

Product
├── Photos (One-to-Many)
├── Documents (One-to-Many)
├── Category (Many-to-One)
├── Encheres (One-to-One)
└── VenteRapide (One-to-One)

Order
├── Payment (One-to-One)
├── Livraison (One-to-One)
└── Facture (One-to-One)
```

---

## 🗂️ Classes et Attributs Détaillés

### 1. **Person (Table Parent - Stratégie JOINED ou SINGLE_TABLE)**
Classe abstraite représentant tous les utilisateurs de la plateforme.

**Attributs:**
- `id` (Long, PK, Auto-generated)
- `firstName` (String, NOT NULL)
- `lastName` (String, NOT NULL)
- `email` (String, UNIQUE, NOT NULL)
- `isEmailConfirmed` (Boolean, default: false)
- `password` (String, NOT NULL, hashed)
- `dateOfBirth` (LocalDate, NOT NULL)
- `phone` (String)
- `profilePicture` (String, URL)
- `isNewsletterSubscriber` (Boolean, default: false)
- `userType` (Enum: PARTICULIER, PROFESSIONAL, ADMIN)
- `isActive` (Boolean, default: true)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Relations:**
- `addresses` (One-to-Many → Address)
- `productsOwned` (One-to-Many → Product)
- `orders` (One-to-Many → Order)
- `reviews` (One-to-Many → PlatformReview)
- `ticketsCreated` (One-to-Many → SupportTicket)
- `conversations` (Many-to-Many → Conversation)
- `notifications` (One-to-Many → Notification)
- `favorites` (One-to-Many → Favorite)

---

### 2. **Particulier extends Person**
Représente un utilisateur particulier (vendeur uniquement).

**Attributs supplémentaires:**
- `isVerified` (Boolean, default: false)
- `isOver18Certified` (Boolean, NOT NULL)

**Relations héritées de Person**

---

### 3. **Professional extends Person**
Représente un professionnel (acheteur et vendeur).

**Attributs supplémentaires:**
- `companyName` (String, NOT NULL)
- `jobTitle` (String)
- `siret` (String, UNIQUE, NOT NULL)
- `kbisDocument` (String, URL)
- `isVerified` (Boolean, default: false)
- `mandateSignedAt` (LocalDateTime)

**Relations supplémentaires:**
- `specialties` (Many-to-Many → Specialty)
- `objectsOfInterest` (Many-to-Many → Category)
- `forfait` (Many-to-One → Forfait)
- `forfaitStartDate` (LocalDateTime)
- `forfaitEndDate` (LocalDateTime)

---

### 4. **Admin extends Person**
Représente les administrateurs de la plateforme.

**Attributs supplémentaires:**
- `role` (Enum: SUPER_ADMIN, MODERATOR, SUPPORT)
- `permissions` (String, JSON)

---

### 5. **Address**
Gère les adresses des utilisateurs.

**Attributs:**
- `id` (Long, PK)
- `street` (String, NOT NULL)
- `city` (String, NOT NULL)
- `state` (String)
- `zipCode` (String, NOT NULL)
- `country` (String, NOT NULL, default: "France")
- `addressType` (Enum: BILLING, SHIPPING, BOTH)
- `isPrimary` (Boolean, default: false)
- `createdAt` (LocalDateTime)

**Relations:**
- `person` (Many-to-One → Person)

---

### 6. **Category**
Catégories des objets (bijoux, meubles, etc.).

**Attributs:**
- `id` (Long, PK)
- `name` (String, UNIQUE, NOT NULL)
- `description` (Text)
- `commissionRate` (Double, default: 2.0) // Commission spécifique par catégorie
- `icon` (String, URL)
- `isActive` (Boolean, default: true)
- `createdAt` (LocalDateTime)

**Relations:**
- `products` (One-to-Many → Product)
- `professionalInterests` (Many-to-Many → Professional)

**Exemples de catégories:**
- Bijoux & montres
- Meubles anciens
- Objets d'art & tableaux
- Objets de collection
- Vins & spiritueux
- Instruments de musique
- Livres anciens
- Mode & accessoires de luxe
- Horlogerie
- Photographies anciennes
- Vaisselle & argenterie
- Sculptures
- Véhicules de collection

---

### 7. **Specialty**
Spécialités des professionnels.

**Attributs:**
- `id` (Long, PK)
- `name` (String, UNIQUE, NOT NULL)
- `description` (Text)

**Relations:**
- `professionals` (Many-to-Many → Professional)

---

### 8. **Product**
Représente un objet mis en vente.

**Attributs:**
- `id` (Long, PK)
- `name` (String, NOT NULL)
- `description` (Text, NOT NULL)
- `width` (Double) // en cm
- `height` (Double) // en cm
- `depth` (Double) // en cm
- `weight` (Double) // en kg
- `desiredPrice` (BigDecimal, NOT NULL)
- `saleMode` (Enum: AUCTION, QUICK_SALE)
- `status` (Enum: DRAFT, PUBLISHED, SOLD, CANCELLED, EXPIRED)
- `viewsCount` (Integer, default: 0)
- `isPublished` (Boolean, default: false)
- `publishedAt` (LocalDateTime)
- `soldAt` (LocalDateTime)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Relations:**
- `owner` (Many-to-One → Person)
- `category` (Many-to-One → Category)
- `photos` (One-to-Many → Photo)
- `documents` (One-to-Many → Document)
- `encheres` (One-to-One → Encheres)
- `venteRapide` (One-to-One → VenteRapide)
- `offers` (One-to-Many → Offer)
- `favorites` (One-to-Many → Favorite)
- `order` (One-to-One → Order)
- `conversations` (One-to-Many → Conversation)

---

### 9. **Photo**
Photos des produits (minimum 10).

**Attributs:**
- `id` (Long, PK)
- `url` (String, NOT NULL)
- `description` (String) // avant, arrière, signature, etc.
- `displayOrder` (Integer)
- `uploadedAt` (LocalDateTime)

**Relations:**
- `product` (Many-to-One → Product)

---

### 10. **Document**
Documents attachés aux produits (certificats, preuves d'achat).

**Attributs:**
- `id` (Long, PK)
- `documentType` (Enum: CERTIFICATE, PROOF_OF_PURCHASE, KBIS, OTHER)
- `documentUrl` (String, NOT NULL)
- `fileName` (String)
- `fileSize` (Long) // en bytes
- `uploadDate` (LocalDateTime)

**Relations:**
- `product` (Many-to-One → Product)
- `person` (Many-to-One → Person) // Pour les documents d'inscription

---

### 11. **Encheres (Auction)**
Gère le système d'enchères.

**Attributs:**
- `id` (Long, PK)
- `startingPrice` (BigDecimal, NOT NULL) // Prix de départ (-10% par défaut)
- `reservePrice` (BigDecimal, NOT NULL) // Prix minimum souhaité
- `currentPrice` (BigDecimal)
- `startDate` (LocalDateTime, NOT NULL)
- `endDate` (LocalDateTime, NOT NULL) // +7 jours par défaut
- `bidIncrement` (BigDecimal) // Palier d'enchères
- `status` (Enum: PENDING, ACTIVE, EXTENDED, ENDED, SOLD, UNSOLD)
- `totalBids` (Integer, default: 0)
- `isAutoExtendEnabled` (Boolean, default: true) // Extension de 10min si enchère à h-1

**Relations:**
- `product` (One-to-One → Product)
- `bids` (One-to-Many → Bid)
- `winner` (Many-to-One → Professional)

**Règles de paliers d'enchères:**
- < 100€ : paliers de 10€
- 100-500€ : paliers de 50€
- 500-1000€ : paliers de 100€
- 1000-5000€ : paliers de 200€
- etc.

---

### 12. **Bid**
Enchères individuelles placées par les professionnels.

**Attributs:**
- `id` (Long, PK)
- `amount` (BigDecimal, NOT NULL)
- `maxAmount` (BigDecimal) // Pour enchères automatiques
- `isAutoBid` (Boolean, default: false)
- `bidDate` (LocalDateTime)
- `isWinning` (Boolean, default: false)

**Relations:**
- `encheres` (Many-to-One → Encheres)
- `bidder` (Many-to-One → Professional)

---

### 13. **VenteRapide (Quick Sale)**
Gère les ventes rapides à prix fixe.

**Attributs:**
- `id` (Long, PK)
- `fixedPrice` (BigDecimal, NOT NULL)
- `isNegotiable` (Boolean, default: true)
- `publishedAt` (LocalDateTime)
- `expiresAt` (LocalDateTime)
- `status` (Enum: AVAILABLE, PENDING_VALIDATION, SOLD, EXPIRED)

**Relations:**
- `product` (One-to-One → Product)
- `offers` (One-to-Many → Offer)
- `winner` (Many-to-One → Professional)

---

### 14. **Offer**
Offres faites sur les produits en vente rapide.

**Attributs:**
- `id` (Long, PK)
- `amount` (BigDecimal, NOT NULL)
- `message` (Text)
- `status` (Enum: PENDING, ACCEPTED, REJECTED, EXPIRED)
- `offerDate` (LocalDateTime)
- `responseDate` (LocalDateTime)
- `validUntil` (LocalDateTime) // 24h pour valider si accepté

**Relations:**
- `product` (Many-to-One → Product)
- `buyer` (Many-to-One → Professional)
- `seller` (Many-to-One → Person)

---

### 15. **Favorite**
Objets mis en favoris par les utilisateurs.

**Attributs:**
- `id` (Long, PK)
- `addedAt` (LocalDateTime)

**Relations:**
- `person` (Many-to-One → Person)
- `product` (Many-to-One → Product)

---

### 16. **Order**
Commandes finalisées.

**Attributs:**
- `id` (Long, PK)
- `orderNumber` (String, UNIQUE, NOT NULL)
- `orderDate` (LocalDateTime)
- `totalAmount` (BigDecimal) // Prix objet + frais
- `buyerCommission` (BigDecimal) // 3% pour l'acheteur
- `sellerCommission` (BigDecimal) // 2% pour le vendeur
- `shippingCost` (BigDecimal)
- `status` (Enum: PENDING_PAYMENT, PAID, PREPARING_SHIPMENT, SHIPPED, DELIVERED, COMPLETED, CANCELLED, REFUNDED)
- `validatedAt` (LocalDateTime)
- `completedAt` (LocalDateTime)

**Relations:**
- `product` (One-to-One → Product)
- `buyer` (Many-to-One → Professional)
- `seller` (Many-to-One → Person)
- `payment` (One-to-One → Payment)
- `livraison` (One-to-One → Livraison)
- `facture` (One-to-One → Facture)

---

### 17. **Payment**
Paiements via Stripe.

**Attributs:**
- `id` (Long, PK)
- `stripePaymentId` (String, UNIQUE)
- `amount` (BigDecimal, NOT NULL)
- `currency` (String, default: "EUR")
- `status` (Enum: PENDING, AUTHORIZED, CAPTURED, HELD, RELEASED, FAILED, REFUNDED)
- `paymentMethod` (String) // card, sepa, etc.
- `paymentDate` (LocalDateTime)
- `releaseDate` (LocalDateTime) // Après signature de livraison + 3-5 jours
- `stripeMetadata` (String, JSON)

**Relations:**
- `order` (One-to-One → Order)
- `payer` (Many-to-One → Professional)

---

### 18. **Livraison (Shipping)**
Gestion des livraisons via API transporteurs.

**Attributs:**
- `id` (Long, PK)
- `trackingNumber` (String)
- `carrier` (Enum: COCOLIS, THE_PACKENGERS, DHL, UPS, CUSTOM)
- `carrierName` (String)
- `pickupDate` (LocalDate)
- `deliveryDate` (LocalDate)
- `estimatedDeliveryDate` (LocalDate)
- `status` (Enum: PENDING, SCHEDULED, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED)
- `packagingInstructions` (Text)
- `signature` (String) // Signature numérique de réception
- `signedAt` (LocalDateTime)
- `shippingCost` (BigDecimal)

**Relations:**
- `order` (One-to-One → Order)
- `shippingAddress` (Many-to-One → Address)
- `billingAddress` (Many-to-One → Address)

---

### 19. **Transporteur (Carrier)**
Liste des transporteurs disponibles.

**Attributs:**
- `id` (Long, PK)
- `name` (String, UNIQUE, NOT NULL)
- `apiKey` (String, encrypted)
- `apiUrl` (String)
- `logo` (String, URL)
- `isActive` (Boolean, default: true)
- `baseCost` (BigDecimal)
- `configuration` (String, JSON)

---

### 20. **Facture (Invoice)**
Factures générées automatiquement.

**Attributs:**
- `id` (Long, PK)
- `invoiceNumber` (String, UNIQUE, NOT NULL)
- `invoiceDate` (LocalDateTime)
- `dueDate` (LocalDateTime)
- `subtotal` (BigDecimal)
- `taxAmount` (BigDecimal)
- `totalAmount` (BigDecimal)
- `invoiceType` (Enum: PURCHASE, SALE, COMMISSION)
- `pdfUrl` (String)
- `status` (Enum: DRAFT, SENT, PAID, OVERDUE, CANCELLED)

**Relations:**
- `order` (One-to-One → Order)
- `recipient` (Many-to-One → Person)

---

### 21. **Forfait (Subscription Plan)**
Plans d'abonnement pour les professionnels.

**Attributs:**
- `id` (Long, PK)
- `name` (String, NOT NULL) // Gratuit, Premium, etc.
- `description` (Text)
- `price` (BigDecimal) // 49€ pour pro, 0€ pour particulier
- `billingPeriod` (Enum: MONTHLY, YEARLY, FREE)
- `trialPeriodDays` (Integer, default: 30) // 1 mois gratuit
- `isActive` (Boolean, default: true)
- `maxListings` (Integer) // Nombre d'objets max (null = illimité)
- `createdAt` (LocalDateTime)

**Relations:**
- `features` (Many-to-Many → Feature)
- `subscribers` (One-to-Many → Professional)

---

### 22. **Feature (Fonctionnalité)**
Fonctionnalités débloquées par les forfaits.

**Attributs:**
- `id` (Long, PK)
- `name` (String, UNIQUE, NOT NULL)
- `description` (Text)
- `featureKey` (String, UNIQUE) // CODE pour identifier la feature
- `isActive` (Boolean, default: true)

**Relations:**
- `forfaits` (Many-to-Many → Forfait)

**Exemples de features:**
- UNLIMITED_LISTINGS
- ADVANCED_SEARCH
- PRIORITY_SUPPORT
- AUCTION_ACCESS
- QUICK_SALE_ACCESS
- ANALYTICS_DASHBOARD

---

### 23. **Conversation**
Conversations entre utilisateurs (hors support).

**Attributs:**
- `id` (Long, PK)
- `subject` (String)
- `status` (Enum: ACTIVE, ARCHIVED, CLOSED)
- `createdAt` (LocalDateTime)
- `lastMessageAt` (LocalDateTime)

**Relations:**
- `product` (Many-to-One → Product)
- `participants` (Many-to-Many → Person)
- `messages` (One-to-Many → Message)

---

### 24. **Message**
Messages dans les conversations.

**Attributs:**
- `id` (Long, PK)
- `content` (Text, NOT NULL)
- `isRead` (Boolean, default: false)
- `sentAt` (LocalDateTime)
- `attachments` (String, JSON) // URLs des fichiers attachés

**Relations:**
- `conversation` (Many-to-One → Conversation)
- `sender` (Many-to-One → Person)

---

### 25. **SupportTicket**
Tickets de support (admin ↔ users).

**Attributs:**
- `id` (Long, PK)
- `ticketNumber` (String, UNIQUE, NOT NULL)
- `title` (String, NOT NULL)
- `content` (Text, NOT NULL)
- `priority` (Enum: LOW, MEDIUM, HIGH, URGENT)
- `status` (Enum: OPEN, IN_PROGRESS, WAITING_RESPONSE, RESOLVED, CLOSED)
- `category` (Enum: TECHNICAL, BILLING, PRODUCT, OTHER)
- `createdAt` (LocalDateTime)
- `closedAt` (LocalDateTime)

**Relations:**
- `creator` (Many-to-One → Person)
- `assignedTo` (Many-to-One → Admin)
- `messages` (One-to-Many → TicketMessage)

---

### 26. **TicketMessage**
Messages dans les tickets de support.

**Attributs:**
- `id` (Long, PK)
- `content` (Text, NOT NULL)
- `attachments` (String, JSON)
- `isFromAdmin` (Boolean, default: false)
- `sentAt` (LocalDateTime)

**Relations:**
- `ticket` (Many-to-One → SupportTicket)
- `sender` (Many-to-One → Person)

---

### 27. **Notification**
Notifications pour les utilisateurs.

**Attributs:**
- `id` (Long, PK)
- `type` (Enum: NEW_MESSAGE, NEW_OFFER, PRODUCT_SOLD, OUTBID, WON_AUCTION, LOST_AUCTION, PAYMENT_RECEIVED, SHIPMENT_UPDATE, NEW_FAVORITE, PRICE_DROP)
- `title` (String, NOT NULL)
- `message` (Text)
- `isRead` (Boolean, default: false)
- `isSent` (Boolean, default: false) // Email envoyé
- `relatedEntityType` (Enum: PRODUCT, OFFER, ORDER, CONVERSATION, AUCTION)
- `relatedEntityId` (Long)
- `createdAt` (LocalDateTime)
- `readAt` (LocalDateTime)

**Relations:**
- `recipient` (Many-to-One → Person)

---

### 28. **Alert**
Alertes configurées par les utilisateurs.

**Attributs:**
- `id` (Long, PK)
- `alertType` (Enum: NEW_PRODUCT_IN_CATEGORY, PRICE_DROP, AUCTION_ENDING, OUTBID)
- `isActive` (Boolean, default: true)
- `notifyByEmail` (Boolean, default: true)
- `notifyInApp` (Boolean, default: true)
- `createdAt` (LocalDateTime)

**Relations:**
- `person` (Many-to-One → Person)
- `category` (Many-to-One → Category)
- `product` (Many-to-One → Product)

---

### 29. **PlatformReview (Avis Plateforme)**
Avis sur la plateforme Purple Dog.

**Attributs:**
- `id` (Long, PK)
- `rating` (Integer, 1-5 étoiles)
- `npsScore` (Integer, 1-10)
- `comment` (Text)
- `suggestions` (Text)
- `reviewDate` (LocalDateTime)
- `isPublished` (Boolean, default: false)

**Relations:**
- `reviewer` (Many-to-One → Person)

---

### 30. **PlatformSettings**
Configuration globale de la plateforme (gérée par admin).

**Attributs:**
- `id` (Long, PK)
- `settingKey` (String, UNIQUE, NOT NULL)
- `settingValue` (Text, JSON)
- `description` (Text)
- `updatedAt` (LocalDateTime)

**Exemples de settings:**
- BUYER_COMMISSION_RATE: 3.0
- SELLER_COMMISSION_RATE: 2.0
- AUCTION_DURATION_DAYS: 7
- AUCTION_EXTENSION_MINUTES: 10
- DEFAULT_TRIAL_PERIOD_DAYS: 30

---

### 31. **ActivityLog**
Historique des actions importantes (audit trail).

**Attributs:**
- `id` (Long, PK)
- `action` (Enum: LOGIN, LOGOUT, CREATE_PRODUCT, EDIT_PRODUCT, PLACE_BID, MAKE_OFFER, PAYMENT, etc.)
- `entityType` (String)
- `entityId` (Long)
- `details` (String, JSON)
- `ipAddress` (String)
- `userAgent` (String)
- `createdAt` (LocalDateTime)

**Relations:**
- `person` (Many-to-One → Person)

---

## Résumé des Relations

### Relations One-to-One
- Product ↔ Encheres
- Product ↔ VenteRapide
- Product ↔ Order
- Order ↔ Payment
- Order ↔ Livraison
- Order ↔ Facture

### Relations One-to-Many
- Person → Address
- Person → Product (vendeur)
- Person → Order (acheteur)
- Person → Notification
- Person → PlatformReview
- Product → Photo (minimum 10)
- Product → Document
- Product → Offer
- Encheres → Bid
- Category → Product
- Conversation → Message
- SupportTicket → TicketMessage

### Relations Many-to-Many
- Professional ↔ Specialty
- Professional ↔ Category (objets d'intérêt)
- Forfait ↔ Feature
- Conversation ↔ Person (participants)

### Relations Many-to-One
- Professional → Forfait
- Product → Category
- Bid → Professional
- Bid → Encheres
- Offer → Professional
- Payment → Professional
- Message → Person (sender)
- Notification → Person
- Alert → Person
- Favorite → Person
- Favorite → Product

---

## Indexes Recommandés pour Performance

```sql
-- Performance indexes essentiels
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_owner ON product(owner_id);
CREATE INDEX idx_product_sale_mode ON product(sale_mode);
CREATE INDEX idx_encheres_status ON encheres(status);
CREATE INDEX idx_encheres_end_date ON encheres(end_date);
CREATE INDEX idx_order_status ON order_table(status);
CREATE INDEX idx_order_buyer ON order_table(buyer_id);
CREATE INDEX idx_person_email ON person(email);
CREATE INDEX idx_person_type ON person(user_type);
CREATE INDEX idx_notification_recipient ON notification(recipient_id, is_read);
CREATE INDEX idx_message_conversation ON message(conversation_id);
CREATE INDEX idx_bid_encheres ON bid(encheres_id);
CREATE INDEX idx_offer_product ON offer(product_id);
CREATE INDEX idx_favorite_person_product ON favorite(person_id, product_id);
```

---

##  Règles de Sécurité et Confidentialité

1. **Anonymisation des particuliers:** Seul le prénom est visible pour les professionnels
2. **Encryption:**
    - Mots de passe hashés avec BCrypt (cost factor minimum 12)
    - API keys des transporteurs encryptées en base
    - Données sensibles (SIRET, coordonnées bancaires) encryptées
3. **Validation email:** Obligatoire avant première action sur la plateforme
4. **RGPD:**
    - Consentement explicite pour newsletter
    - Possibilité de supprimer son compte (soft delete)
    - Export des données personnelles sur demande
5. **Audit trail:** Toutes les actions importantes sont loguées dans ActivityLog
6. **Paiements sécurisés:**
    - Fonds bloqués (held) jusqu'à signature de livraison
    - Libération automatique 3-5 jours après signature
7. **Rate limiting:** Protection contre les abus (enchères, offres, etc.)

---

## Statistiques et KPIs à Prévoir

### Métriques Business
- Nombre d'objets vendus par catégorie
- Temps moyen de vente (auction vs quick sale)
- Prix moyen par catégorie
- Taux de conversion enchères vs vente rapide
- Taux de réussite des enchères (atteinte du prix de réserve)
- Chiffre d'affaires par commission (acheteur + vendeur)
- GMV (Gross Merchandise Value)

### Métriques Utilisateurs
- Utilisateurs actifs (DAU/MAU/WAU)
- Taux de conversion inscription → première vente
- Taux de rétention (J7, J30, J90)
- NPS moyen
- Nombre d'abonnés premium vs gratuit

### Métriques Produits
- Nombre de vues par produit
- Ratio favoris / vues
- Temps moyen avant première offre
- Nombre moyen d'enchères par auction

---

## Points

### Priorité HAUTE
1. **Système d'enchères:** Cœur du projet - nécessite WebSocket pour temps réel
2. **Gestion des paiements Stripe:** Hold funds + release automatique
3. **Notifications:** Email + in-app en temps réel
4. **Upload photos:** Minimum 10 photos obligatoires
5. **Authentification et sécurité:** JWT + refresh tokens

### Priorité MOYENNE
1. **API transporteurs:** Intégration Cocolis, DHL, UPS
2. **Système d'alertes:** Notifications configurables
3. **Chat en temps réel:** WebSocket pour messagerie
4. **Génération PDF factures:** Automatique après vente
5. **Back office admin:** Dashboard complet

### Priorité BASSE (Nice to have)
1. **Analytics avancées:** Dashboard statistiques détaillées
2. **Export données:** Pour compliance RGPD
3. **API publique:** Pour partenaires potentiels
4. **Mobile responsive:** Version mobile optimisée

---

## Stack Technique

### Backend
- **Framework:** Spring Boot
- **Database:** PostgreSQL
- **Cache:** Redis
- **Message Queue:** RabbitMQ ou Kafka (notifications, optional)
- **WebSocket:** Spring WebSocket (enchères + chat temps réel)
- **Email:** SendGrid SMTP
- **Payment:** Stripe API
- **Security:** Spring Security + JWT

### Frontend
- **Framework:** React
- **Real-time:** Socket.io ou SockJS
- **UI Components:** Material-UI ou Ant Design
- **Forms:** React Hook Form ou Formik

### DevOps
- **Containerization:** Docker + Docker Compose
- **Logs:** ELK Stack (Elasticsearch + Logstash + Kibana....)
- **APM:** New Relic ou Datadog

---

## Notes

1. **Stratégie d'héritage:** Utiliser `@Inheritance(strategy = InheritanceType.JOINED)` pour Person
2. **Soft Delete:** Préférer `isActive=false` plutôt que suppression réelle
3. **Audit:** Utiliser `@EntityListeners(AuditingEntityListener.class)` pour createdAt/updatedAt
4. **Transactions:** Attention aux transactions longues dans le système d'enchères
5. **Concurrence:** Gérer les race conditions sur les enchères avec locks optimistes
6. **Pagination:** Toujours paginer les listes (produits, enchères, messages)
7. **Validation:** Utiliser Bean Validation (JSR-303) pour toutes les entités
8. **DTO Pattern:** Ne jamais exposer les entités JPA directement dans les API

---

## Prochaines

1. Créer les entités JPA dans `mvp/src/main/java/com/purple_dog/mvp/entities/`
2. Créer les repositories dans `mvp/src/main/java/com/purple_dog/mvp/dao/`
3. Créer les DTOs dans `mvp/src/main/java/com/purple_dog/mvp/dto/`
4. Créer les services dans `mvp/src/main/java/com/purple_dog/mvp/services/`
5. Créer les controllers dans `mvp/src/main/java/com/purple_dog/mvp/web/`
6. Configurer Spring Security
7. Implémenter WebSocket pour enchères temps réel
8. Intégrer Stripe
9. Développer le front-end
10. Tests unitaires et d'intégration


-- =====================================================
-- PURPLE DOG - Script SQL Complet de la Base de Données
-- =====================================================

-- Suppression des tables existantes (pour réinitialisation)
DROP TABLE IF EXISTS activity_log CASCADE;
DROP TABLE IF EXISTS platform_settings CASCADE;
DROP TABLE IF EXISTS platform_review CASCADE;
DROP TABLE IF EXISTS alert CASCADE;
DROP TABLE IF EXISTS notification CASCADE;
DROP TABLE IF EXISTS ticket_message CASCADE;
DROP TABLE IF EXISTS support_ticket CASCADE;
DROP TABLE IF EXISTS message CASCADE;
DROP TABLE IF EXISTS conversation_participants CASCADE;
DROP TABLE IF EXISTS conversation CASCADE;
DROP TABLE IF EXISTS forfait_features CASCADE;
DROP TABLE IF EXISTS feature CASCADE;
DROP TABLE IF EXISTS facture CASCADE;
DROP TABLE IF EXISTS livraison CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS order_table CASCADE;
DROP TABLE IF EXISTS favorite CASCADE;
DROP TABLE IF EXISTS offer CASCADE;
DROP TABLE IF EXISTS bid CASCADE;
DROP TABLE IF EXISTS vente_rapide CASCADE;
DROP TABLE IF EXISTS encheres CASCADE;
DROP TABLE IF EXISTS photo CASCADE;
DROP TABLE IF EXISTS document CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS professional_specialties CASCADE;
DROP TABLE IF EXISTS professional_interests CASCADE;
DROP TABLE IF EXISTS specialty CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS address CASCADE;
DROP TABLE IF EXISTS professional CASCADE;
DROP TABLE IF EXISTS particulier CASCADE;
DROP TABLE IF EXISTS admin CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS transporteur CASCADE;
DROP TABLE IF EXISTS forfait CASCADE;

-- Suppression des types ENUM
DROP TYPE IF EXISTS user_type_enum CASCADE;
DROP TYPE IF EXISTS address_type_enum CASCADE;
DROP TYPE IF EXISTS sale_mode_enum CASCADE;
DROP TYPE IF EXISTS product_status_enum CASCADE;
DROP TYPE IF EXISTS document_type_enum CASCADE;
DROP TYPE IF EXISTS encheres_status_enum CASCADE;
DROP TYPE IF EXISTS vente_rapide_status_enum CASCADE;
DROP TYPE IF EXISTS offer_status_enum CASCADE;
DROP TYPE IF EXISTS order_status_enum CASCADE;
DROP TYPE IF EXISTS payment_status_enum CASCADE;
DROP TYPE IF EXISTS shipping_status_enum CASCADE;
DROP TYPE IF EXISTS carrier_enum CASCADE;
DROP TYPE IF EXISTS invoice_type_enum CASCADE;
DROP TYPE IF EXISTS invoice_status_enum CASCADE;
DROP TYPE IF EXISTS billing_period_enum CASCADE;
DROP TYPE IF EXISTS conversation_status_enum CASCADE;
DROP TYPE IF EXISTS ticket_status_enum CASCADE;
DROP TYPE IF EXISTS ticket_priority_enum CASCADE;
DROP TYPE IF EXISTS ticket_category_enum CASCADE;
DROP TYPE IF EXISTS notification_type_enum CASCADE;
DROP TYPE IF EXISTS alert_type_enum CASCADE;
DROP TYPE IF EXISTS related_entity_type_enum CASCADE;
DROP TYPE IF EXISTS admin_role_enum CASCADE;
DROP TYPE IF EXISTS action_type_enum CASCADE;

-- =====================================================
-- CRÉATION DES TYPES ENUM
-- =====================================================

CREATE TYPE user_type_enum AS ENUM ('PARTICULIER', 'PROFESSIONAL', 'ADMIN');
CREATE TYPE address_type_enum AS ENUM ('BILLING', 'SHIPPING', 'BOTH');
CREATE TYPE sale_mode_enum AS ENUM ('AUCTION', 'QUICK_SALE');
CREATE TYPE product_status_enum AS ENUM ('DRAFT', 'PUBLISHED', 'SOLD', 'CANCELLED', 'EXPIRED');
CREATE TYPE document_type_enum AS ENUM ('CERTIFICATE', 'PROOF_OF_PURCHASE', 'KBIS', 'OTHER');
CREATE TYPE encheres_status_enum AS ENUM ('PENDING', 'ACTIVE', 'EXTENDED', 'ENDED', 'SOLD', 'UNSOLD');
CREATE TYPE vente_rapide_status_enum AS ENUM ('AVAILABLE', 'PENDING_VALIDATION', 'SOLD', 'EXPIRED');
CREATE TYPE offer_status_enum AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED');
CREATE TYPE order_status_enum AS ENUM ('PENDING_PAYMENT', 'PAID', 'PREPARING_SHIPMENT', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED', 'REFUNDED');
CREATE TYPE payment_status_enum AS ENUM ('PENDING', 'AUTHORIZED', 'CAPTURED', 'HELD', 'RELEASED', 'FAILED', 'REFUNDED');
CREATE TYPE shipping_status_enum AS ENUM ('PENDING', 'SCHEDULED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'FAILED');
CREATE TYPE carrier_enum AS ENUM ('COCOLIS', 'THE_PACKENGERS', 'DHL', 'UPS', 'CUSTOM');
CREATE TYPE invoice_type_enum AS ENUM ('PURCHASE', 'SALE', 'COMMISSION');
CREATE TYPE invoice_status_enum AS ENUM ('DRAFT', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED');
CREATE TYPE billing_period_enum AS ENUM ('MONTHLY', 'YEARLY', 'FREE');
CREATE TYPE conversation_status_enum AS ENUM ('ACTIVE', 'ARCHIVED', 'CLOSED');
CREATE TYPE ticket_status_enum AS ENUM ('OPEN', 'IN_PROGRESS', 'WAITING_RESPONSE', 'RESOLVED', 'CLOSED');
CREATE TYPE ticket_priority_enum AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'URGENT');
CREATE TYPE ticket_category_enum AS ENUM ('TECHNICAL', 'BILLING', 'PRODUCT', 'OTHER');
CREATE TYPE notification_type_enum AS ENUM ('NEW_MESSAGE', 'NEW_OFFER', 'PRODUCT_SOLD', 'OUTBID', 'WON_AUCTION', 'LOST_AUCTION', 'PAYMENT_RECEIVED', 'SHIPMENT_UPDATE', 'NEW_FAVORITE', 'PRICE_DROP');
CREATE TYPE alert_type_enum AS ENUM ('NEW_PRODUCT_IN_CATEGORY', 'PRICE_DROP', 'AUCTION_ENDING', 'OUTBID');
CREATE TYPE related_entity_type_enum AS ENUM ('PRODUCT', 'OFFER', 'ORDER', 'CONVERSATION', 'AUCTION');
CREATE TYPE admin_role_enum AS ENUM ('SUPER_ADMIN', 'MODERATOR', 'SUPPORT');
CREATE TYPE action_type_enum AS ENUM ('LOGIN', 'LOGOUT', 'CREATE_PRODUCT', 'EDIT_PRODUCT', 'DELETE_PRODUCT', 'PLACE_BID', 'MAKE_OFFER', 'ACCEPT_OFFER', 'PAYMENT', 'UPDATE_PROFILE');

-- =====================================================
-- TABLE: forfait (doit être créée avant person/professional)
-- =====================================================
CREATE TABLE forfait (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL,
description TEXT,
price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
billing_period billing_period_enum NOT NULL DEFAULT 'MONTHLY',
trial_period_days INTEGER DEFAULT 30,
is_active BOOLEAN DEFAULT TRUE,
max_listings INTEGER,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: feature
-- =====================================================
CREATE TABLE feature (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL UNIQUE,
description TEXT,
feature_key VARCHAR(50) NOT NULL UNIQUE,
is_active BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: forfait_features (Many-to-Many)
-- =====================================================
CREATE TABLE forfait_features (
forfait_id BIGINT NOT NULL REFERENCES forfait(id) ON DELETE CASCADE,
feature_id BIGINT NOT NULL REFERENCES feature(id) ON DELETE CASCADE,
PRIMARY KEY (forfait_id, feature_id)
);

-- =====================================================
-- TABLE: person (Table parent pour inheritance)
-- =====================================================
CREATE TABLE person (
id BIGSERIAL PRIMARY KEY,
first_name VARCHAR(100) NOT NULL,
last_name VARCHAR(100) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE,
is_email_confirmed BOOLEAN DEFAULT FALSE,
password VARCHAR(255) NOT NULL,
date_of_birth DATE NOT NULL,
phone VARCHAR(20),
profile_picture VARCHAR(500),
is_newsletter_subscriber BOOLEAN DEFAULT FALSE,
user_type user_type_enum NOT NULL,
is_active BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: particulier (extends person)
-- =====================================================
CREATE TABLE particulier (
id BIGINT PRIMARY KEY REFERENCES person(id) ON DELETE CASCADE,
is_verified BOOLEAN DEFAULT FALSE,
is_over_18_certified BOOLEAN NOT NULL
);

-- =====================================================
-- TABLE: professional (extends person)
-- =====================================================
CREATE TABLE professional (
id BIGINT PRIMARY KEY REFERENCES person(id) ON DELETE CASCADE,
company_name VARCHAR(255) NOT NULL,
job_title VARCHAR(100),
siret VARCHAR(14) NOT NULL UNIQUE,
kbis_document VARCHAR(500),
is_verified BOOLEAN DEFAULT FALSE,
mandate_signed_at TIMESTAMP,
forfait_id BIGINT REFERENCES forfait(id),
forfait_start_date TIMESTAMP,
forfait_end_date TIMESTAMP
);

-- =====================================================
-- TABLE: admin (extends person)
-- =====================================================
CREATE TABLE admin (
id BIGINT PRIMARY KEY REFERENCES person(id) ON DELETE CASCADE,
role admin_role_enum NOT NULL DEFAULT 'SUPPORT',
permissions TEXT -- JSON format
);

-- =====================================================
-- TABLE: address
-- =====================================================
CREATE TABLE address (
id BIGSERIAL PRIMARY KEY,
person_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
street VARCHAR(255) NOT NULL,
city VARCHAR(100) NOT NULL,
state VARCHAR(100),
zip_code VARCHAR(20) NOT NULL,
country VARCHAR(100) NOT NULL DEFAULT 'France',
address_type address_type_enum NOT NULL,
is_primary BOOLEAN DEFAULT FALSE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: category
-- =====================================================
CREATE TABLE category (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL UNIQUE,
description TEXT,
commission_rate DECIMAL(5,2) DEFAULT 2.0,
icon VARCHAR(500),
is_active BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: specialty
-- =====================================================
CREATE TABLE specialty (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL UNIQUE,
description TEXT
);

-- =====================================================
-- TABLE: professional_specialties (Many-to-Many)
-- =====================================================
CREATE TABLE professional_specialties (
professional_id BIGINT NOT NULL REFERENCES professional(id) ON DELETE CASCADE,
specialty_id BIGINT NOT NULL REFERENCES specialty(id) ON DELETE CASCADE,
PRIMARY KEY (professional_id, specialty_id)
);

-- =====================================================
-- TABLE: professional_interests (Many-to-Many)
-- =====================================================
CREATE TABLE professional_interests (
professional_id BIGINT NOT NULL REFERENCES professional(id) ON DELETE CASCADE,
category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE CASCADE,
PRIMARY KEY (professional_id, category_id)
);

-- =====================================================
-- TABLE: product
-- =====================================================
CREATE TABLE product (
id BIGSERIAL PRIMARY KEY,
owner_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
category_id BIGINT NOT NULL REFERENCES category(id),
name VARCHAR(255) NOT NULL,
description TEXT NOT NULL,
width DECIMAL(10,2), -- cm
height DECIMAL(10,2), -- cm
depth DECIMAL(10,2), -- cm
weight DECIMAL(10,2), -- kg
desired_price DECIMAL(10,2) NOT NULL,
sale_mode sale_mode_enum,
status product_status_enum DEFAULT 'DRAFT',
views_count INTEGER DEFAULT 0,
is_published BOOLEAN DEFAULT FALSE,
published_at TIMESTAMP,
sold_at TIMESTAMP,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: photo
-- =====================================================
CREATE TABLE photo (
id BIGSERIAL PRIMARY KEY,
product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
url VARCHAR(500) NOT NULL,
description VARCHAR(255),
display_order INTEGER,
uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: document
-- =====================================================
CREATE TABLE document (
id BIGSERIAL PRIMARY KEY,
product_id BIGINT REFERENCES product(id) ON DELETE CASCADE,
person_id BIGINT REFERENCES person(id) ON DELETE CASCADE,
document_type document_type_enum NOT NULL,
document_url VARCHAR(500) NOT NULL,
file_name VARCHAR(255),
file_size BIGINT,
upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: encheres (Auction)
-- =====================================================
CREATE TABLE encheres (
id BIGSERIAL PRIMARY KEY,
product_id BIGINT NOT NULL UNIQUE REFERENCES product(id) ON DELETE CASCADE,
winner_id BIGINT REFERENCES professional(id),
starting_price DECIMAL(10,2) NOT NULL,
reserve_price DECIMAL(10,2) NOT NULL,
current_price DECIMAL(10,2),
start_date TIMESTAMP NOT NULL,
end_date TIMESTAMP NOT NULL,
bid_increment DECIMAL(10,2),
status encheres_status_enum DEFAULT 'PENDING',
total_bids INTEGER DEFAULT 0,
is_auto_extend_enabled BOOLEAN DEFAULT TRUE
);

-- =====================================================
-- TABLE: bid
-- =====================================================
CREATE TABLE bid (
id BIGSERIAL PRIMARY KEY,
encheres_id BIGINT NOT NULL REFERENCES encheres(id) ON DELETE CASCADE,
bidder_id BIGINT NOT NULL REFERENCES professional(id) ON DELETE CASCADE,
amount DECIMAL(10,2) NOT NULL,
max_amount DECIMAL(10,2), -- Pour enchères automatiques
is_auto_bid BOOLEAN DEFAULT FALSE,
bid_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
is_winning BOOLEAN DEFAULT FALSE
);

-- =====================================================
-- TABLE: vente_rapide (Quick Sale)
-- =====================================================
CREATE TABLE vente_rapide (
id BIGSERIAL PRIMARY KEY,
product_id BIGINT NOT NULL UNIQUE REFERENCES product(id) ON DELETE CASCADE,
winner_id BIGINT REFERENCES professional(id),
fixed_price DECIMAL(10,2) NOT NULL,
is_negotiable BOOLEAN DEFAULT TRUE,
published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
expires_at TIMESTAMP,
status vente_rapide_status_enum DEFAULT 'AVAILABLE'
);

-- =====================================================
-- TABLE: offer
-- =====================================================
CREATE TABLE offer (
id BIGSERIAL PRIMARY KEY,
product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
buyer_id BIGINT NOT NULL REFERENCES professional(id) ON DELETE CASCADE,
seller_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
amount DECIMAL(10,2) NOT NULL,
message TEXT,
status offer_status_enum DEFAULT 'PENDING',
offer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
response_date TIMESTAMP,
valid_until TIMESTAMP
);

-- =====================================================
-- TABLE: favorite
-- =====================================================
CREATE TABLE favorite (
id BIGSERIAL PRIMARY KEY,
person_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
UNIQUE(person_id, product_id)
);

-- =====================================================
-- TABLE: order_table
-- =====================================================
CREATE TABLE order_table (
id BIGSERIAL PRIMARY KEY,
product_id BIGINT NOT NULL UNIQUE REFERENCES product(id) ON DELETE CASCADE,
buyer_id BIGINT NOT NULL REFERENCES professional(id) ON DELETE CASCADE,
seller_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
order_number VARCHAR(50) NOT NULL UNIQUE,
order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
total_amount DECIMAL(10,2) NOT NULL,
buyer_commission DECIMAL(10,2), -- 3%
seller_commission DECIMAL(10,2), -- 2%
shipping_cost DECIMAL(10,2),
status order_status_enum DEFAULT 'PENDING_PAYMENT',
validated_at TIMESTAMP,
completed_at TIMESTAMP
);

-- =====================================================
-- TABLE: payment
-- =====================================================
CREATE TABLE payment (
id BIGSERIAL PRIMARY KEY,
order_id BIGINT NOT NULL UNIQUE REFERENCES order_table(id) ON DELETE CASCADE,
payer_id BIGINT NOT NULL REFERENCES professional(id) ON DELETE CASCADE,
stripe_payment_id VARCHAR(255) UNIQUE,
amount DECIMAL(10,2) NOT NULL,
currency VARCHAR(3) DEFAULT 'EUR',
status payment_status_enum DEFAULT 'PENDING',
payment_method VARCHAR(50),
payment_date TIMESTAMP,
release_date TIMESTAMP,
stripe_metadata TEXT -- JSON format
);

-- =====================================================
-- TABLE: transporteur
-- =====================================================
CREATE TABLE transporteur (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL UNIQUE,
api_key VARCHAR(500), -- encrypted
api_url VARCHAR(500),
logo VARCHAR(500),
is_active BOOLEAN DEFAULT TRUE,
base_cost DECIMAL(10,2),
configuration TEXT -- JSON format
);

-- =====================================================
-- TABLE: livraison (Shipping)
-- =====================================================
CREATE TABLE livraison (
id BIGSERIAL PRIMARY KEY,
order_id BIGINT NOT NULL UNIQUE REFERENCES order_table(id) ON DELETE CASCADE,
shipping_address_id BIGINT NOT NULL REFERENCES address(id),
billing_address_id BIGINT NOT NULL REFERENCES address(id),
tracking_number VARCHAR(255),
carrier carrier_enum,
carrier_name VARCHAR(100),
pickup_date DATE,
delivery_date DATE,
estimated_delivery_date DATE,
status shipping_status_enum DEFAULT 'PENDING',
packaging_instructions TEXT,
signature TEXT,
signed_at TIMESTAMP,
shipping_cost DECIMAL(10,2)
);

-- =====================================================
-- TABLE: facture (Invoice)
-- =====================================================
CREATE TABLE facture (
id BIGSERIAL PRIMARY KEY,
order_id BIGINT NOT NULL UNIQUE REFERENCES order_table(id) ON DELETE CASCADE,
recipient_id BIGINT NOT NULL REFERENCES person(id),
invoice_number VARCHAR(50) NOT NULL UNIQUE,
invoice_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
due_date TIMESTAMP,
subtotal DECIMAL(10,2) NOT NULL,
tax_amount DECIMAL(10,2),
total_amount DECIMAL(10,2) NOT NULL,
invoice_type invoice_type_enum NOT NULL,
pdf_url VARCHAR(500),
status invoice_status_enum DEFAULT 'DRAFT'
);

-- =====================================================
-- TABLE: conversation
-- =====================================================
CREATE TABLE conversation (
id BIGSERIAL PRIMARY KEY,
product_id BIGINT REFERENCES product(id) ON DELETE CASCADE,
subject VARCHAR(255),
status conversation_status_enum DEFAULT 'ACTIVE',
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
last_message_at TIMESTAMP
);

-- =====================================================
-- TABLE: conversation_participants (Many-to-Many)
-- =====================================================
CREATE TABLE conversation_participants (
conversation_id BIGINT NOT NULL REFERENCES conversation(id) ON DELETE CASCADE,
person_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
PRIMARY KEY (conversation_id, person_id)
);

-- =====================================================
-- TABLE: message
-- =====================================================
CREATE TABLE message (
id BIGSERIAL PRIMARY KEY,
conversation_id BIGINT NOT NULL REFERENCES conversation(id) ON DELETE CASCADE,
sender_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
content TEXT NOT NULL,
is_read BOOLEAN DEFAULT FALSE,
sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
attachments TEXT -- JSON format
);

-- =====================================================
-- TABLE: support_ticket
-- =====================================================
CREATE TABLE support_ticket (
id BIGSERIAL PRIMARY KEY,
creator_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
assigned_to_id BIGINT REFERENCES admin(id),
ticket_number VARCHAR(50) NOT NULL UNIQUE,
title VARCHAR(255) NOT NULL,
content TEXT NOT NULL,
priority ticket_priority_enum DEFAULT 'MEDIUM',
status ticket_status_enum DEFAULT 'OPEN',
category ticket_category_enum DEFAULT 'OTHER',
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
closed_at TIMESTAMP
);

-- =====================================================
-- TABLE: ticket_message
-- =====================================================
CREATE TABLE ticket_message (
id BIGSERIAL PRIMARY KEY,
ticket_id BIGINT NOT NULL REFERENCES support_ticket(id) ON DELETE CASCADE,
sender_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
content TEXT NOT NULL,
attachments TEXT, -- JSON format
is_from_admin BOOLEAN DEFAULT FALSE,
sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: notification
-- =====================================================
CREATE TABLE notification (
id BIGSERIAL PRIMARY KEY,
recipient_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
type notification_type_enum NOT NULL,
title VARCHAR(255) NOT NULL,
message TEXT,
is_read BOOLEAN DEFAULT FALSE,
is_sent BOOLEAN DEFAULT FALSE,
related_entity_type related_entity_type_enum,
related_entity_id BIGINT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
read_at TIMESTAMP
);

-- =====================================================
-- TABLE: alert
-- =====================================================
CREATE TABLE alert (
id BIGSERIAL PRIMARY KEY,
person_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
category_id BIGINT REFERENCES category(id),
product_id BIGINT REFERENCES product(id),
alert_type alert_type_enum NOT NULL,
is_active BOOLEAN DEFAULT TRUE,
notify_by_email BOOLEAN DEFAULT TRUE,
notify_in_app BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: platform_review
-- =====================================================
CREATE TABLE platform_review (
id BIGSERIAL PRIMARY KEY,
reviewer_id BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,
rating INTEGER CHECK (rating >= 1 AND rating <= 5),
nps_score INTEGER CHECK (nps_score >= 1 AND nps_score <= 10),
comment TEXT,
suggestions TEXT,
review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
is_published BOOLEAN DEFAULT FALSE
);

-- =====================================================
-- TABLE: platform_settings
-- =====================================================
CREATE TABLE platform_settings (
id BIGSERIAL PRIMARY KEY,
setting_key VARCHAR(100) NOT NULL UNIQUE,
setting_value TEXT NOT NULL,
description TEXT,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLE: activity_log
-- =====================================================
CREATE TABLE activity_log (
id BIGSERIAL PRIMARY KEY,
person_id BIGINT REFERENCES person(id) ON DELETE SET NULL,
action action_type_enum NOT NULL,
entity_type VARCHAR(100),
entity_id BIGINT,
details TEXT, -- JSON format
ip_address VARCHAR(45),
user_agent TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CRÉATION DES INDEX POUR PERFORMANCE
-- =====================================================

-- Person
CREATE INDEX idx_person_email ON person(email);
CREATE INDEX idx_person_type ON person(user_type);
CREATE INDEX idx_person_is_active ON person(is_active);

-- Professional
CREATE INDEX idx_professional_siret ON professional(siret);
CREATE INDEX idx_professional_forfait ON professional(forfait_id);

-- Product
CREATE INDEX idx_product_owner ON product(owner_id);
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_sale_mode ON product(sale_mode);
CREATE INDEX idx_product_published_at ON product(published_at);

-- Encheres
CREATE INDEX idx_encheres_product ON encheres(product_id);
CREATE INDEX idx_encheres_status ON encheres(status);
CREATE INDEX idx_encheres_end_date ON encheres(end_date);

-- Bid
CREATE INDEX idx_bid_encheres ON bid(encheres_id);
CREATE INDEX idx_bid_bidder ON bid(bidder_id);
CREATE INDEX idx_bid_date ON bid(bid_date);

-- Offer
CREATE INDEX idx_offer_product ON offer(product_id);
CREATE INDEX idx_offer_buyer ON offer(buyer_id);
CREATE INDEX idx_offer_status ON offer(status);

-- Order
CREATE INDEX idx_order_buyer ON order_table(buyer_id);
CREATE INDEX idx_order_seller ON order_table(seller_id);
CREATE INDEX idx_order_status ON order_table(status);
CREATE INDEX idx_order_date ON order_table(order_date);

-- Favorite
CREATE INDEX idx_favorite_person ON favorite(person_id);
CREATE INDEX idx_favorite_product ON favorite(product_id);

-- Notification
CREATE INDEX idx_notification_recipient ON notification(recipient_id);
CREATE INDEX idx_notification_is_read ON notification(is_read);
CREATE INDEX idx_notification_created_at ON notification(created_at);

-- Message
CREATE INDEX idx_message_conversation ON message(conversation_id);
CREATE INDEX idx_message_sender ON message(sender_id);

-- Support Ticket
CREATE INDEX idx_ticket_creator ON support_ticket(creator_id);
CREATE INDEX idx_ticket_status ON support_ticket(status);
CREATE INDEX idx_ticket_assigned ON support_ticket(assigned_to_id);

-- Activity Log
CREATE INDEX idx_activity_person ON activity_log(person_id);
CREATE INDEX idx_activity_created_at ON activity_log(created_at);

-- =====================================================
-- INSERTION DE DONNÉES INITIALES
-- =====================================================

-- Forfaits par défaut
INSERT INTO forfait (name, description, price, billing_period, trial_period_days, max_listings) VALUES
('Particulier Gratuit', 'Forfait gratuit pour les particuliers - vente uniquement', 0.00, 'FREE', 0, NULL),
('Professionnel', 'Forfait professionnel avec accès complet à la plateforme', 49.00, 'MONTHLY', 30, NULL);

-- Features
INSERT INTO feature (name, description, feature_key) VALUES
('Annonces illimitées', 'Publier un nombre illimité d''objets', 'UNLIMITED_LISTINGS'),
('Accès aux enchères', 'Participer et créer des enchères', 'AUCTION_ACCESS'),
('Accès vente rapide', 'Vendre et acheter en vente rapide', 'QUICK_SALE_ACCESS'),
('Recherche avancée', 'Filtres de recherche avancés', 'ADVANCED_SEARCH'),
('Support prioritaire', 'Support client prioritaire', 'PRIORITY_SUPPORT'),
('Analytics', 'Tableau de bord analytique', 'ANALYTICS_DASHBOARD');

-- Association forfait-features
INSERT INTO forfait_features (forfait_id, feature_id)
SELECT f.id, fe.id
FROM forfait f
CROSS JOIN feature fe
WHERE f.name = 'Professionnel';

INSERT INTO forfait_features (forfait_id, feature_id)
SELECT f.id, fe.id
FROM forfait f
CROSS JOIN feature fe
WHERE f.name = 'Particulier Gratuit' AND fe.feature_key IN ('QUICK_SALE_ACCESS', 'AUCTION_ACCESS');

-- Catégories
INSERT INTO category (name, description, commission_rate) VALUES
('Bijoux & montres', 'Bijoux, montres de luxe et accessoires précieux', 2.0),
('Meubles anciens', 'Mobilier ancien et d''époque', 2.0),
('Objets d''art & tableaux', 'Peintures, sculptures et œuvres d''art', 2.5),
('Objets de collection', 'Jouets, timbres, monnaies et objets de collection', 2.0),
('Vins & spiritueux', 'Vins et spiritueux de collection', 3.0),
('Instruments de musique', 'Instruments de musique anciens et de collection', 2.0),
('Livres anciens & manuscrits', 'Livres rares et manuscrits', 2.0),
('Mode & accessoires de luxe', 'Sacs, chaussures, vêtements de marque', 2.5),
('Horlogerie & pendules', 'Horlogerie ancienne et pendules', 2.0),
('Photographies anciennes', 'Photographies et appareils vintage', 2.0),
('Vaisselle & argenterie', 'Vaisselle, argenterie et cristallerie', 2.0),
('Sculptures & objets décoratifs', 'Sculptures et objets d''art décoratifs', 2.5),
('Véhicules de collection', 'Automobiles, motos et bateaux de collection', 3.5);

-- Spécialités
INSERT INTO specialty (name, description) VALUES
('Antiquités', 'Spécialiste en antiquités'),
('Art moderne', 'Expert en art moderne et contemporain'),
('Horlogerie', 'Spécialiste montres et horlogerie'),
('Numismatique', 'Expert en monnaies et médailles'),
('Philatélie', 'Expert en timbres'),
('Mobilier', 'Spécialiste meubles anciens'),
('Bijouterie', 'Expert en bijoux et pierres précieuses'),
('Vinification', 'Expert en vins et spiritueux'),
('Art asiatique', 'Spécialiste art asiatique'),
('Tableaux anciens', 'Expert en peintures anciennes');

-- Transporteurs
INSERT INTO transporteur (name, is_active, base_cost) VALUES
('Cocolis', TRUE, 0.00),
('The Packengers', TRUE, 0.00),
('DHL Express', TRUE, 25.00),
('UPS', TRUE, 20.00),
('Colissimo', TRUE, 15.00);

-- Configuration globale de la plateforme
INSERT INTO platform_settings (setting_key, setting_value, description) VALUES
('BUYER_COMMISSION_RATE', '3.0', 'Taux de commission acheteur en %'),
('SELLER_COMMISSION_RATE', '2.0', 'Taux de commission vendeur en %'),
('AUCTION_DURATION_DAYS', '7', 'Durée par défaut des enchères en jours'),
('AUCTION_EXTENSION_MINUTES', '10', 'Extension en minutes si enchère de dernière minute'),
('DEFAULT_TRIAL_PERIOD_DAYS', '30', 'Période d''essai gratuite en jours pour professionnels'),
('MIN_AUCTION_PHOTOS', '10', 'Nombre minimum de photos pour publier un objet'),
('ORDER_VALIDATION_HOURS', '24', 'Heures pour valider un achat après remport'),
('PAYMENT_RELEASE_DAYS', '5', 'Jours avant libération du paiement après signature'),
('MAX_PHOTO_SIZE_MB', '10', 'Taille maximale d''une photo en MB'),
('MAX_DOCUMENT_SIZE_MB', '20', 'Taille maximale d''un document en MB');

-- =====================================================
-- TRIGGERS POUR UPDATED_AT
-- =====================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_person_updated_at BEFORE UPDATE ON person
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_product_updated_at BEFORE UPDATE ON product
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_forfait_updated_at BEFORE UPDATE ON forfait
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- VUES UTILES
-- =====================================================

-- Vue des produits actifs avec informations du vendeur
CREATE OR REPLACE VIEW v_active_products AS
SELECT
p.id,
p.name,
p.description,
p.desired_price,
p.sale_mode,
p.status,
p.views_count,
p.published_at,
c.name as category_name,
per.first_name as seller_first_name,
per.user_type as seller_type,
COUNT(DISTINCT ph.id) as photo_count,
COUNT(DISTINCT f.id) as favorite_count
FROM product p
JOIN person per ON p.owner_id = per.id
JOIN category c ON p.category_id = c.id
LEFT JOIN photo ph ON p.id = ph.product_id
LEFT JOIN favorite f ON p.id = f.product_id
WHERE p.status = 'PUBLISHED' AND p.is_published = TRUE
GROUP BY p.id, p.name, p.description, p.desired_price, p.sale_mode, p.status,
p.views_count, p.published_at, c.name, per.first_name, per.user_type;

-- Vue des enchères actives
CREATE OR REPLACE VIEW v_active_auctions AS
SELECT
e.id,
e.current_price,
e.reserve_price,
e.start_date,
e.end_date,
e.total_bids,
e.status,
p.name as product_name,
p.id as product_id,
c.name as category_name
FROM encheres e
JOIN product p ON e.product_id = p.id
JOIN category c ON p.category_id = c.id
WHERE e.status IN ('ACTIVE', 'EXTENDED')
AND e.end_date > CURRENT_TIMESTAMP;

-- Vue des statistiques par catégorie
CREATE OR REPLACE VIEW v_category_stats AS
SELECT
c.id,
c.name,
COUNT(p.id) as total_products,
COUNT(CASE WHEN p.status = 'SOLD' THEN 1 END) as sold_products,
AVG(CASE WHEN p.status = 'SOLD' THEN p.desired_price END) as avg_sale_price,
SUM(CASE WHEN p.status = 'SOLD' THEN p.desired_price END) as total_revenue
FROM category c
LEFT JOIN product p ON c.id = p.category_id
GROUP BY c.id, c.name;

-- =====================================================
-- COMMENTAIRES SUR LES TABLES
-- =====================================================

COMMENT ON TABLE person IS 'Table parent pour tous les utilisateurs (particuliers, professionnels, admins)';
COMMENT ON TABLE particulier IS 'Utilisateurs particuliers - peuvent uniquement vendre';
COMMENT ON TABLE professional IS 'Utilisateurs professionnels - peuvent acheter et vendre';
COMMENT ON TABLE admin IS 'Administrateurs de la plateforme';
COMMENT ON TABLE product IS 'Objets mis en vente sur la plateforme';
COMMENT ON TABLE encheres IS 'Système d''enchères pour les produits';
COMMENT ON TABLE bid IS 'Enchères individuelles placées par les professionnels';
COMMENT ON TABLE vente_rapide IS 'Ventes rapides à prix fixe ou négociable';
COMMENT ON TABLE offer IS 'Offres faites sur les produits en vente rapide';
COMMENT ON TABLE order_table IS 'Commandes finalisées';
COMMENT ON TABLE payment IS 'Paiements via Stripe avec fonds bloqués';
COMMENT ON TABLE livraison IS 'Gestion des livraisons via API transporteurs';
COMMENT ON TABLE facture IS 'Factures générées automatiquement';
COMMENT ON TABLE notification IS 'Notifications in-app et email pour les utilisateurs';
COMMENT ON TABLE platform_settings IS 'Configuration globale de la plateforme (modifiable par admin)';

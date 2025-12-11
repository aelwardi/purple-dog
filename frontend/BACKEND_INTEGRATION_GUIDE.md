# Guide d'Intégration Backend-Frontend

## Introduction

Ce guide explique comment le frontend React est connecté au backend Spring Boot et comment utiliser les services API pour la plateforme Purple Dog de vente d'oeuvres de collection.

## Configuration

### Variables d'Environnement

Le fichier `.env` contient la configuration de l'API :

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Purple Dog
VITE_APP_URL=http://localhost:5173
VITE_ENV=development
```

### Configuration CORS

Le backend est configuré pour accepter les requêtes du frontend via `WebConfig.java` :
- Origines autorisées : `http://localhost:5173`, `http://localhost:3000`, `http://localhost:4173`
- Méthodes autorisées : GET, POST, PUT, DELETE, PATCH, OPTIONS
- Credentials autorisés : Oui

## Services API Disponibles

Tous les services sont accessibles via `src/services/index.js` :

```javascript
import { 
  authService, 
  productService, 
  categoryService,
  orderService,
  messagingService,
  auctionService,
  individualService,
  professionalService
} from '../services';
```

### 1. Service d'Authentification (authService)

**Endpoints disponibles :**

```javascript
// Connexion
const response = await authService.login({ email, password });

// Inscription individu
const user = await authService.registerIndividual(userData);

// Inscription professionnel
const pro = await authService.registerProfessional(userData);

// Déconnexion
await authService.logout();

// Mot de passe oublié
await authService.forgotPassword(email);

// Réinitialisation mot de passe
await authService.resetPassword(token, newPassword);

// Vérifier si authentifié
const isAuth = authService.isAuthenticated();

// Obtenir l'utilisateur courant
const currentUser = authService.getCurrentUser();
```

### 2. Service Produits (productService)

```javascript
// Récupérer tous les produits (oeuvres de collection)
const products = await productService.getAll();

// Récupérer un produit par ID
const product = await productService.getById(productId);

// Rechercher des produits
const results = await productService.search({ 
  keyword: 'sculpture',
  category: 'art-contemporain',
  minPrice: 1000,
  maxPrice: 5000
});

// Créer un produit
const newProduct = await productService.create(productData);

// Mettre à jour un produit
await productService.update(productId, updateData);

// Supprimer un produit
await productService.delete(productId);

// Uploader des images
await productService.uploadImages(productId, files, (progress) => {
  console.log(`Upload: ${progress}%`);
});
```

### 3. Service Catégories (categoryService)

```javascript
// Récupérer toutes les catégories
const categories = await categoryService.getAll();

// Récupérer les catégories racine
const rootCategories = await categoryService.getRootCategories();

// Récupérer les sous-catégories
const subCategories = await categoryService.getSubCategories(parentId);
```

### 4. Service Individus (individualService)

```javascript
// Récupérer un individu
const individual = await individualService.getById(id);

// Mettre à jour un individu
await individualService.update(id, updateData);

// Vérifier l'identité
await individualService.verifyIdentity(id);

// Mettre à jour le statut
await individualService.updateAccountStatus(id, 'ACTIVE');
```

### 5. Service Professionnels (professionalService)

```javascript
// Récupérer un professionnel
const professional = await professionalService.getById(id);

// Rechercher par SIRET
const pro = await professionalService.getBySiret(siret);

// Mettre à jour
await professionalService.update(id, updateData);
```

### 6. Service Commandes (orderService)

```javascript
// Créer une commande
const order = await orderService.create(orderData);

// Récupérer commandes acheteur
const buyerOrders = await orderService.getByBuyer(buyerId);

// Récupérer commandes vendeur
const sellerOrders = await orderService.getBySeller(sellerId);

// Mettre à jour le statut
await orderService.updateStatus(orderId, 'SHIPPED');

// Annuler une commande
await orderService.cancel(orderId, 'Raison de l annulation');
```

### 7. Service Messagerie (messagingService)

```javascript
// Récupérer les conversations
const conversations = await messagingService.getConversations(userId);

// Démarrer une conversation
const conversation = await messagingService.startConversation({
  participant1Id: userId,
  participant2Id: otherUserId,
  productId: productId
});

// Envoyer un message
await messagingService.sendMessage(conversationId, messageContent);

// Marquer comme lu
await messagingService.markAsRead(messageId);
```

### 8. Service Enchères (auctionService)

```javascript
// Récupérer les enchères actives
const auctions = await auctionService.getActive();

// Créer une enchère
const auction = await auctionService.create(auctionData);

// Placer une offre
await auctionService.placeBid(auctionId, amount);

// Récupérer les offres
const bids = await auctionService.getBids(auctionId);
```

## Utilisation dans les Composants React

### Exemple 1 : Page de Connexion

```javascript
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services';
import { useErrorHandler } from '../hooks/useErrorHandler';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { handleError } = useErrorHandler();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await authService.login({ email, password });
      
      // Redirection selon le type d'utilisateur
      if (response.userType === 'INDIVIDUAL') {
        navigate('/dashboard/individual');
      } else if (response.userType === 'PROFESSIONAL') {
        navigate('/dashboard/professional');
      }
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input 
        type="email" 
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      <input 
        type="password" 
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      <button type="submit" disabled={loading}>
        {loading ? 'Connexion...' : 'Se connecter'}
      </button>
    </form>
  );
}
```

### Exemple 2 : Liste d'Oeuvres de Collection

```javascript
import { useState, useEffect } from 'react';
import { productService } from '../services';
import { useErrorHandler } from '../hooks/useErrorHandler';

function CollectionList() {
  const [artworks, setArtworks] = useState([]);
  const [loading, setLoading] = useState(true);
  const { handleError } = useErrorHandler();

  useEffect(() => {
    loadArtworks();
  }, []);

  const loadArtworks = async () => {
    try {
      const data = await productService.getAll();
      setArtworks(data);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Chargement...</div>;

  return (
    <div className="grid grid-cols-3 gap-4">
      {artworks.map(artwork => (
        <div key={artwork.id} className="border p-4">
          <h3>{artwork.name}</h3>
          <p>{artwork.artist}</p>
          <p>{artwork.price} EUR</p>
        </div>
      ))}
    </div>
  );
}
```

### Exemple 3 : Recherche d'Oeuvres

```javascript
import { useState } from 'react';
import { productService } from '../services';

function SearchArtworks() {
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState([]);

  const handleSearch = async () => {
    try {
      const data = await productService.search({ keyword });
      setResults(data);
    } catch (error) {
      console.error('Erreur de recherche:', error);
    }
  };

  return (
    <div>
      <input 
        type="text"
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        placeholder="Rechercher une oeuvre..."
      />
      <button onClick={handleSearch}>Rechercher</button>
      
      <div className="results">
        {results.map(artwork => (
          <div key={artwork.id}>{artwork.name}</div>
        ))}
      </div>
    </div>
  );
}
```

### Exemple 4 : Upload d'Images d'Oeuvre

```javascript
import { useState } from 'react';
import { productService } from '../services';

function ArtworkImageUpload({ artworkId }) {
  const [files, setFiles] = useState([]);
  const [progress, setProgress] = useState(0);

  const handleUpload = async () => {
    try {
      await productService.uploadImages(
        artworkId, 
        files, 
        (progressEvent) => {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          setProgress(percentCompleted);
        }
      );
      alert('Images uploadées avec succès');
    } catch (error) {
      console.error('Erreur d upload:', error);
    }
  };

  return (
    <div>
      <input 
        type="file"
        multiple
        onChange={(e) => setFiles(Array.from(e.target.files))}
      />
      <button onClick={handleUpload}>Uploader</button>
      {progress > 0 && <div>Progression: {progress}%</div>}
    </div>
  );
}
```

## Gestion de l'Authentification

Le token JWT est automatiquement géré par `apiClient.js` :

1. **Lors de la connexion** : Le token est sauvegardé dans `localStorage`
2. **Requêtes API** : Le token est automatiquement ajouté dans le header `Authorization`
3. **Expiration** : En cas de 401, l'utilisateur est redirigé vers la page de connexion

## Gestion des Erreurs

Utilisez le hook `useErrorHandler` pour gérer les erreurs :

```javascript
import { useErrorHandler } from '../hooks/useErrorHandler';

function MyComponent() {
  const { handleError } = useErrorHandler();

  const fetchData = async () => {
    try {
      const data = await productService.getAll();
    } catch (error) {
      handleError(error);
    }
  };
}
```

## Démarrage

### Backend (Spring Boot)

```bash
cd mvp
./mvnw spring-boot:run
```

Le backend sera accessible sur `http://localhost:8080`

### Frontend (React + Vite)

```bash
cd frontend
npm install
npm run dev
```

Le frontend sera accessible sur `http://localhost:5173`

## Structure des Données

### Exemple de Réponse API

**Oeuvre de collection :**
```json
{
  "id": 1,
  "name": "Sculpture Moderne",
  "description": "Sculpture en bronze représentant...",
  "artist": "Jean Dubois",
  "year": 2023,
  "price": 4500.00,
  "stock": 1,
  "category": {
    "id": 5,
    "name": "Sculptures"
  },
  "seller": {
    "id": 10,
    "email": "galerie@example.com"
  },
  "createdAt": "2025-01-10T10:30:00Z"
}
```

**Commande :**
```json
{
  "id": 1,
  "orderNumber": "ORD-20250110-001",
  "totalAmount": 4500.00,
  "status": "PENDING",
  "buyer": { ... },
  "items": [
    {
      "product": { ... },
      "quantity": 1,
      "unitPrice": 4500.00
    }
  ]
}
```

## Endpoints Backend

Base URL : `http://localhost:8080/api`

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/auth/login` | POST | Connexion |
| `/auth/logout` | POST | Déconnexion |
| `/individuals` | POST | Créer un individu |
| `/individuals/{id}` | GET | Récupérer un individu |
| `/professionals` | POST | Créer un professionnel |
| `/professionals/{id}` | GET | Récupérer un professionnel |
| `/products` | GET | Liste des oeuvres |
| `/products` | POST | Créer une oeuvre |
| `/products/{id}` | GET | Détails d'une oeuvre |
| `/products/search` | GET | Rechercher des oeuvres |
| `/categories` | GET | Liste des catégories |
| `/orders` | POST | Créer une commande |
| `/orders/buyer/{id}` | GET | Commandes d'un acheteur |
| `/messaging/conversations/{userId}` | GET | Conversations |
| `/auctions/active` | GET | Enchères actives |

## Conseils

1. **Toujours utiliser try-catch** pour les appels API
2. **Gérer les états de chargement** pour une meilleure UX
3. **Valider les données** côté frontend avant l'envoi
4. **Utiliser les hooks personnalisés** (`useErrorHandler`, `useApiError`)
5. **Tester avec des données mockées** avant l'intégration réelle

## Debugging

### Vérifier la connexion :
```javascript
// Dans la console du navigateur
console.log(import.meta.env.VITE_API_BASE_URL);
```

### Voir les logs API :
Les logs sont automatiquement affichés dans la console (voir `utils/logger.js`)

### Problèmes CORS :
- Vérifier que le backend est configuré avec les bonnes origines
- S'assurer que le backend est bien démarré
- Vérifier l'URL dans le fichier `.env`

## Ressources

- [Documentation Axios](https://axios-http.com/)
- [React Query](https://tanstack.com/query/latest) - Pour un meilleur cache et gestion d'état
- [Documentation Spring Boot](https://spring.io/projects/spring-boot)

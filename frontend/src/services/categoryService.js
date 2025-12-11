import api from './api';
import { mockProducts } from '../data/mockProducts';

/// API

/**
 * Service pour les opérations liées aux enchères
 */

import { api } from '../utils/apiClient';

export const categoryService = {
  /**
   * Récupérer une catégorie par ID
   */
  getById: async (id) => {
    return await api.get(`/categories/${id}`);
  },

  /**
   * Récupérer toutes les catégories
   */
  getAll: async () => {
    return await api.get('/categories');
  },

  /**
   * Récupérer les catégories actives
   */
  getActive: async () => {
    return await api.get('/categories/active');
  },

  /**
   * Récupérer les produits d'une catégorie
   */
  getProducts: async (categoryId) => {
    return await api.get(`/categories/${categoryId}/products`);
  },

  /**
   * Créer une catégorie
   */
  create: async (categoryData) => {
    return await api.post('/categories', categoryData);
  },

  /**
   * Placer une offre
   */
  placeBid: async (auctionId, amount) => {
    return await api.post(`/auctions/${auctionId}/bids`, { amount });
  },
  /**
   * Supprimer une enchère
   */
  delete: async (id) => {
    return await api.delete(`/auctions/${id}`);
  },

};


// Service pour gérer les catégories et leurs produits

export const getCategories = async () => {
  // Simuler un délai d'API
  await new Promise(resolve => setTimeout(resolve, 300));

  // Catégories avec leurs informations complètes
  return [
    {
      id: 1,
      name: 'Bijoux & montres',
      icon: '💎',
      count: 1250,
      image: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=800&h=400&fit=crop',
      description: 'Découvrez notre sélection de bijoux anciens, montres de collection et accessoires précieux.'
    },
    {
      id: 2,
      name: 'Meubles anciens',
      icon: '🪑',
      count: 850,
      image: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&h=400&fit=crop',
      description: 'Mobilier d\'époque, meubles de style et pièces de collection pour votre intérieur.'
    },
    {
      id: 3,
      name: 'Objets d\'art & tableaux',
      icon: '🎨',
      count: 2100,
      image: 'https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?w=800&h=400&fit=crop',
      description: 'Peintures, sculptures, gravures et œuvres d\'art de toutes les époques.'
    },
    {
      id: 4,
      name: 'Objets de collection',
      icon: '🏺',
      count: 670,
      image: 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=800&h=400&fit=crop',
      description: 'Porcelaines, céramiques, objets décoratifs et pièces de collection uniques.'
    },
    {
      id: 5,
      name: 'Vins & spiritueux',
      icon: '🍷',
      count: 430,
      image: 'https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=800&h=400&fit=crop',
      description: 'Grands crus, millésimes d\'exception et spiritueux de collection.'
    },
    {
      id: 6,
      name: 'Instruments de musique',
      icon: '🎸',
      count: 320,
      image: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=800&h=400&fit=crop',
      description: 'Instruments anciens, violons de maître et pièces musicales rares.'
    },
    {
      id: 7,
      name: 'Livres anciens',
      icon: '📚',
      count: 540,
      image: 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=800&h=400&fit=crop',
      description: 'Manuscrits, éditions originales et ouvrages de bibliophilie.'
    },
    {
      id: 8,
      name: 'Mode & accessoires',
      icon: '👜',
      count: 980,
      image: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=800&h=400&fit=crop',
      description: 'Haute couture vintage, maroquinerie de luxe et accessoires de mode.'
    },
    {
      id: 9,
      name: 'Horlogerie',
      icon: '⏰',
      count: 410,
      image: 'https://images.unsplash.com/photo-1509048191080-d2984bad6ae5?w=800&h=400&fit=crop',
      description: 'Montres de prestige, horloges anciennes et pièces d\'horlogerie fine.'
    },
    {
      id: 10,
      name: 'Photographies',
      icon: '📷',
      count: 290,
      image: 'https://images.unsplash.com/photo-1452780212940-6f5c0d14d848?w=800&h=400&fit=crop',
      description: 'Photographies d\'art, tirages vintage et œuvres photographiques.'
    },
    {
      id: 11,
      name: 'Vaisselle & argenterie',
      icon: '🍽️',
      count: 560,
      image: 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800&h=400&fit=crop',
      description: 'Services de table, argenterie fine et arts de la table.'
    },
    {
      id: 12,
      name: 'Sculptures',
      icon: '🗿',
      count: 380,
      image: 'https://images.unsplash.com/photo-1513519245088-0e3a94918c58?w=800&h=400&fit=crop',
      description: 'Sculptures anciennes, bronzes d\'art et œuvres sculpturales.'
    }
  ];
};

export const getCategoryById = async (categoryId) => {
  const categories = await getCategories();
  return categories.find(cat => cat.id === parseInt(categoryId));
};

export const getCategoryProducts = async (categoryId, filters = {}) => {
  // Simuler un délai d'API
  await new Promise(resolve => setTimeout(resolve, 500));

  // Mapping des catégories aux mots-clés pour filtrer les produits mock
  const categoryKeywords = {
    1: ['bijoux', 'montre', 'or', 'diamant', 'bracelet', 'collier'], // Bijoux & montres
    2: ['fauteuil', 'meuble', 'commode', 'armoire', 'table', 'chaise'], // Meubles
    3: ['tableau', 'peinture', 'toile', 'art', 'lithographie', 'gravure'], // Art & tableaux
    4: ['vase', 'porcelaine', 'céramique', 'collection', 'statuette'], // Objets de collection
    5: ['vin', 'spiritueux', 'bouteille', 'cognac', 'champagne'], // Vins
    6: ['piano', 'violon', 'guitare', 'instrument'], // Instruments
    7: ['livre', 'manuscrit', 'édition', 'ouvrage'], // Livres
    8: ['sac', 'mode', 'couture', 'accessoire', 'vintage'], // Mode
    9: ['horloge', 'montre', 'pendule', 'horlogerie'], // Horlogerie
    10: ['photo', 'photographie', 'tirage', 'cliché'], // Photos
    11: ['service', 'argent', 'vaisselle', 'couverts', 'thé'], // Vaisselle
    12: ['sculpture', 'bronze', 'statue', 'buste'] // Sculptures
  };

  // Obtenir les mots-clés pour la catégorie
  const keywords = categoryKeywords[parseInt(categoryId)] || [];

  // Filtrer les produits par catégorie (recherche dans le titre et la description)
  let filteredProducts = mockProducts.filter(product => {
    if (keywords.length === 0) return true;

    const searchText = (product.title + ' ' + product.description).toLowerCase();
    return keywords.some(keyword => searchText.includes(keyword.toLowerCase()));
  });

  // Appliquer les filtres additionnels
  if (filters.priceMin) {
    filteredProducts = filteredProducts.filter(product =>
      product.price >= parseFloat(filters.priceMin)
    );
  }

  if (filters.priceMax) {
    filteredProducts = filteredProducts.filter(product =>
      product.price <= parseFloat(filters.priceMax)
    );
  }

  if (filters.condition) {
    filteredProducts = filteredProducts.filter(product =>
      product.condition === filters.condition
    );
  }

  if (filters.saleType) {
    filteredProducts = filteredProducts.filter(product =>
      product.saleType === filters.saleType
    );
  }

  if (filters.seller) {
    filteredProducts = filteredProducts.filter(product =>
      product.seller?.name?.toLowerCase().includes(filters.seller.toLowerCase())
    );
  }

  // Ajouter des données simulées pour l'affichage
  return filteredProducts.map(product => ({
    ...product,
    views: Math.floor(Math.random() * 1000) + 50,
    isFavorited: Math.random() > 0.8 // 20% de chance d'être en favoris
  }));
};

export const searchProductsInCategory = async (categoryId, searchQuery) => {
  const categoryProducts = await getCategoryProducts(categoryId);

  if (!searchQuery) return categoryProducts;

  const query = searchQuery.toLowerCase();
  return categoryProducts.filter(product =>
    product.title.toLowerCase().includes(query) ||
    product.description.toLowerCase().includes(query) ||
    product.seller?.name?.toLowerCase().includes(query)
  );
};

// Fonction utilitaire pour obtenir les statistiques d'une catégorie
export const getCategoryStats = async (categoryId) => {
  const products = await getCategoryProducts(categoryId);

  const stats = {
    totalProducts: products.length,
    averagePrice: products.reduce((sum, p) => sum + p.price, 0) / products.length,
    priceRange: {
      min: Math.min(...products.map(p => p.price)),
      max: Math.max(...products.map(p => p.price))
    },
    saleTypes: {
      auction: products.filter(p => p.saleType === 'auction').length,
      direct: products.filter(p => p.saleType === 'direct').length
    },
    conditions: products.reduce((acc, p) => {
      acc[p.condition] = (acc[p.condition] || 0) + 1;
      return acc;
    }, {}),
    topSellers: [...new Set(products.map(p => p.seller?.name))].slice(0, 5)
  };

  return stats;
};

// Compatibilité avec l'ancien service
const categoryService = {
  getAllCategories: getCategories,
  getActiveCategories: getCategories,
  getCategory: getCategoryById,
  getCategoryProducts,
  searchProductsInCategory,
  getCategoryStats
};

export default categoryService;

/**
 * Service pour les opérations liées aux produits
 */

import { api } from '../utils/apiClient';

export const productService = {
  /**
   * Récupérer un produit par ID
   */
  getById: async (id) => {
    return await api.get(`/products/${id}`);
  },

  /**
   * Récupérer tous les produits
   */
  getAll: async () => {
    return await api.get('/products');
  },

  /**
   * Rechercher des produits
   */
  search: async (params) => {
    const queryString = new URLSearchParams(params).toString();
    return await api.get(`/products/search?${queryString}`);
  },

  /**
   * Récupérer produits par catégorie
   */
  getByCategory: async (categoryId) => {
    return await api.get(`/products/category/${categoryId}`);
  },

  /**
   * Récupérer produits par vendeur
   */
  getBySeller: async (sellerId) => {
    return await api.get(`/products/seller/${sellerId}`);
  },

  /**
   * Créer un produit
   */
  create: async (productData) => {
    return await api.post('/products', productData);
  },

  /**
   * Mettre à jour un produit
   */
  update: async (id, productData) => {
    return await api.put(`/products/${id}`, productData);
  },

  /**
   * Supprimer un produit
   */
  delete: async (id) => {
    return await api.delete(`/products/${id}`);
  },

  /**
   * Uploader des images de produit
   */
  uploadImages: async (productId, files, onUploadProgress) => {
    return await api.uploadMultiple(`/products/${productId}/images`, files, onUploadProgress);
  },

  /**
   * Supprimer une image de produit
   */
  deleteImage: async (productId, imageId) => {
    return await api.delete(`/products/${productId}/images/${imageId}`);
  },

  /**
   * Mettre à jour le statut d'un produit
   */
  updateStatus: async (id, status) => {
    return await api.patch(`/products/${id}/status?status=${status}`);
  },

  /**
   * Récupérer les statistiques des produits
   */
  getStatistics: async () => {
    return await api.get('/products/statistics');
  },
};

export default productService;

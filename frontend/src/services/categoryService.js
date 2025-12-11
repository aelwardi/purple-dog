/**
 * Service pour les opérations liées aux catégories
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
   * Récupérer les catégories parentes (niveau 0)
   */
  getRootCategories: async () => {
    return await api.get('/categories/root');
  },

  /**
   * Récupérer les sous-catégories d'une catégorie
   */
  getSubCategories: async (parentId) => {
    return await api.get(`/categories/${parentId}/subcategories`);
  },

  /**
   * Créer une catégorie
   */
  create: async (categoryData) => {
    return await api.post('/categories', categoryData);
  },

  /**
   * Mettre à jour une catégorie
   */
  update: async (id, categoryData) => {
    return await api.put(`/categories/${id}`, categoryData);
  },

  /**
   * Supprimer une catégorie
   */
  delete: async (id) => {
    return await api.delete(`/categories/${id}`);
  },
};

export default categoryService;

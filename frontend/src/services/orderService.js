/**
 * Service pour les opérations liées aux commandes
 */

import { api } from '../utils/apiClient';

export const orderService = {
  /**
   * Récupérer une commande par ID
   */
  getById: async (id) => {
    return await api.get(`/orders/${id}`);
  },

  /**
   * Récupérer toutes les commandes
   */
  getAll: async () => {
    return await api.get('/orders');
  },

  /**
   * Récupérer les commandes d'un acheteur
   */
  getByBuyer: async (buyerId) => {
    return await api.get(`/orders/buyer/${buyerId}`);
  },

  /**
   * Récupérer les commandes d'un vendeur
   */
  getBySeller: async (sellerId) => {
    return await api.get(`/orders/seller/${sellerId}`);
  },

  /**
   * Créer une commande
   */
  create: async (orderData) => {
    return await api.post('/orders', orderData);
  },

  /**
   * Mettre à jour le statut d'une commande
   */
  updateStatus: async (id, status) => {
    return await api.patch(`/orders/${id}/status?status=${status}`);
  },

  /**
   * Annuler une commande
   */
  cancel: async (id, reason) => {
    return await api.post(`/orders/${id}/cancel`, { reason });
  },

  /**
   * Récupérer les statistiques des commandes
   */
  getStatistics: async () => {
    return await api.get('/orders/statistics');
  },
};

export default orderService;

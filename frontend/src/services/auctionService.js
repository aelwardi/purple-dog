/**
 * Service pour les opérations liées aux enchères
 */

import { api } from '../utils/apiClient';

export const auctionService = {
  /**
   * Récupérer une enchère par ID
   */
  getById: async (id) => {
    return await api.get(`/auctions/${id}`);
  },

  /**
   * Récupérer toutes les enchères
   */
  getAll: async () => {
    return await api.get('/auctions');
  },

  /**
   * Récupérer les enchères actives
   */
  getActive: async () => {
    return await api.get('/auctions/active');
  },

  /**
   * Récupérer les enchères d'un produit
   */
  getByProduct: async (productId) => {
    return await api.get(`/auctions/product/${productId}`);
  },

  /**
   * Créer une enchère
   */
  create: async (auctionData) => {
    return await api.post('/auctions', auctionData);
  },

  /**
   * Placer une offre
   */
  placeBid: async (auctionId, amount) => {
    return await api.post(`/auctions/${auctionId}/bids`, { amount });
  },

  /**
   * Récupérer les offres d'une enchère
   */
  getBids: async (auctionId) => {
    return await api.get(`/auctions/${auctionId}/bids`);
  },

  /**
   * Terminer une enchère
   */
  end: async (auctionId) => {
    return await api.post(`/auctions/${auctionId}/end`);
  },

  /**
   * Annuler une enchère
   */
  cancel: async (auctionId, reason) => {
    return await api.post(`/auctions/${auctionId}/cancel`, { reason });
  },
};

export default auctionService;

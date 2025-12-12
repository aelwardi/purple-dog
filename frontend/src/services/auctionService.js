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
    // backend may not have this endpoint; if it does, adjust. Fallback to search by auctions if implemented
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
   * Backend expects POST /bids with body { auctionId, bidderId, amount, maxAmount }
   */
  placeBid: async (auctionId, amount, bidderId = null, maxAmount = null) => {
    const payload = { auctionId, amount };
    if (bidderId) payload.bidderId = bidderId;
    if (maxAmount) payload.maxAmount = maxAmount;
    return await api.post('/bids', payload);
  },

  /**
   * Récupérer les offres d'une enchère
   * Backend endpoint: GET /bids/auction/{auctionId}
   */
  getBids: async (auctionId) => {
    return await api.get(`/bids/auction/${auctionId}`);
  },

  /**
   * Notifier les enchérisseurs inférieurs après une nouvelle offre
   */
  notifyLowerBidders: async (auctionId, bidId) => {
    return await api.post(`/bids/${bidId}/notify-lower-bidders`); // backend may not implement this; safe attempt
  },

  /**
   * Terminer une enchère
   */
  end: async (auctionId) => {
    return await api.post(`/auctions/${auctionId}/close`);
  },

  /**
   * Annuler une enchère
   */
  cancel: async (auctionId, reason) => {
    return await api.post(`/auctions/${auctionId}/cancel`, { reason });
  },
};

export default auctionService;

/**
 * Service pour les opérations liées aux enchères
 */

import { api } from '../utils/apiClient';
import { mockAuctions, getActiveAuctions, getClosedAuctions, getAuctionById } from '../data/mockAuctions';

// Mode développement: utiliser les données mock
const USE_MOCK_DATA = true;

export const auctionService = {
  /**
   * Récupérer une enchère par ID
   */
  getById: async (id) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 300));
      const auction = getAuctionById(id);
      if (!auction) {
        throw new Error('Enchère introuvable');
      }
      return { data: auction };
    }
    return await api.get(`/auctions/${id}`);
  },

  /**
   * Récupérer toutes les enchères
   */
  getAll: async () => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 300));
      return { data: mockAuctions };
    }
    return await api.get('/auctions');
  },

  /**
   * Récupérer les enchères actives
   */
  getActive: async () => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 300));
      return { data: getActiveAuctions() };
    }
    return await api.get('/auctions/active');
  },

  /**
   * Récupérer les enchères terminées
   */
  getClosed: async () => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 300));
      return { data: getClosedAuctions() };
    }
    return await api.get('/auctions/closed');
  },

  /**
   * Récupérer les enchères d'un produit
   */
  getByProduct: async (productId) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 300));
      const auctions = mockAuctions.filter(a => a.product?.id === parseInt(productId));
      return { data: auctions };
    }
    return await api.get(`/auctions/product/${productId}`);
  },

  /**
   * Créer une enchère
   */
  create: async (auctionData) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 500));
      console.log('Mock: Creating auction', auctionData);
      return { data: { id: Date.now(), ...auctionData } };
    }
    return await api.post('/auctions', auctionData);
  },

  /**
   * Placer une offre
   * Backend expects POST /bids with body { auctionId, bidderId, amount, maxAmount }
   */
  placeBid: async (auctionId, amount, bidderId = null, maxAmount = null) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 500));
      console.log('Mock: Placing bid', { auctionId, amount, bidderId, maxAmount });
      const newBid = {
        id: Date.now(),
        auctionId,
        bidderId: bidderId || Math.floor(Math.random() * 1000) + 100,
        bidderDisplayName: 'Vous',
        amount,
        bidDate: new Date().toISOString()
      };
      return { data: newBid };
    }
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
    // Ce sera géré par bidService.js
    return await api.get(`/bids/auction/${auctionId}`);
  },

  /**
   * Notifier les enchérisseurs inférieurs après une nouvelle offre
   */
  notifyLowerBidders: async (auctionId, bidId) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 200));
      console.log('Mock: Notifying lower bidders', { auctionId, bidId });
      return { data: { success: true } };
    }
    return await api.post(`/bids/${bidId}/notify-lower-bidders`);
  },

  /**
   * Terminer une enchère
   */
  end: async (auctionId) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 500));
      console.log('Mock: Ending auction', auctionId);
      return { data: { success: true } };
    }
    return await api.post(`/auctions/${auctionId}/close`);
  },

  /**
   * Annuler une enchère
   */
  cancel: async (auctionId, reason) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 500));
      console.log('Mock: Cancelling auction', { auctionId, reason });
      return { data: { success: true } };
    }
    return await api.post(`/auctions/${auctionId}/cancel`, { reason });
  },

  /**
   * Vérifier si le prix de réserve est atteint
   */
  isReserveMet: async (id) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 200));
      const auction = getAuctionById(id);
      const isMet = auction ? auction.currentPrice >= auction.reservePrice : false;
      return { data: isMet };
    }
    return await api.get(`/auctions/${id}/reserve-met`);
  },
};

export default auctionService;

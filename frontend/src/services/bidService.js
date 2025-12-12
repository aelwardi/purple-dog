/**
 * Service pour les opérations liées aux enchères (bids)
 */

import { api } from '../utils/apiClient';

export const bidService = {
  /**
   * Placer une enchère
   */
  placeBid: async (bidData) => {
    return await api.post('/bids', bidData);
  },

  /**
   * Récupérer toutes les enchères d'une auction
   */
  getAuctionBids: async (auctionId) => {
    return await api.get(`/bids/auction/${auctionId}`);
  },

  /**
   * Récupérer les enchères d'un enchérisseur
   */
  getBidderBids: async (bidderId) => {
    return await api.get(`/bids/bidder/${bidderId}`);
  },

  /**
   * Récupérer l'enchère gagnante actuelle
   */
  getCurrentWinningBid: async (auctionId) => {
    return await api.get(`/bids/auction/${auctionId}/winning`);
  },

  /**
   * Récupérer le prochain montant d'enchère suggéré
   */
  getNextBidAmount: async (auctionId) => {
    return await api.get(`/bids/auction/${auctionId}/next-amount`);
  },
};

export default bidService;

/**
 * Service pour les opérations liées aux enchères (bids)
 */

import { api } from '../utils/apiClient';
import { 
  getBidsByAuctionId, 
  getCurrentWinningBid, 
  getNextBidAmount,
  getBidsByBidderId 
} from '../data/mockBids';
import { getAuctionById } from '../data/mockAuctions';

// Mode développement: utiliser les données mock
const USE_MOCK_DATA = true;

export const bidService = {
  /**
   * Placer une enchère
   */
  placeBid: async (bidData) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 500));
      console.log('Mock: Placing bid', bidData);
      // Simuler la création d'une enchère
      const newBid = {
        id: Date.now(),
        auctionId: bidData.auctionId,
        bidderId: Math.floor(Math.random() * 1000) + 100,
        bidderDisplayName: 'Vous',
        amount: bidData.amount,
        bidDate: new Date().toISOString()
      };
      return { data: newBid };
    }
    return await api.post('/bids', bidData);
  },

  /**
   * Récupérer toutes les enchères d'une auction
   */
  getAuctionBids: async (auctionId) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 200));
      const bids = getBidsByAuctionId(auctionId);
      return { data: bids };
    }
    return await api.get(`/bids/auction/${auctionId}`);
  },

  /**
   * Récupérer les enchères d'un enchérisseur
   */
  getBidderBids: async (bidderId) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 200));
      const bids = getBidsByBidderId(bidderId);
      return { data: bids };
    }
    return await api.get(`/bids/bidder/${bidderId}`);
  },

  /**
   * Récupérer l'enchère gagnante actuelle
   */
  getCurrentWinningBid: async (auctionId) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 200));
      const winningBid = getCurrentWinningBid(auctionId);
      return { data: winningBid };
    }
    return await api.get(`/bids/auction/${auctionId}/winning`);
  },

  /**
   * Récupérer le prochain montant d'enchère suggéré
   */
  getNextBidAmount: async (auctionId) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 200));
      const auction = getAuctionById(auctionId);
      if (!auction) {
        throw new Error('Enchère introuvable');
      }
      const nextAmount = getNextBidAmount(auctionId, auction.currentPrice);
      return { data: nextAmount };
    }
    return await api.get(`/bids/auction/${auctionId}/next-amount`);
  },
};

export default bidService;

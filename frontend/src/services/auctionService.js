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
      // Simuler un délai réseau
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
   * Mettre à jour une enchère
   */
  update: async (id, auctionData) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 500));
      console.log('Mock: Updating auction', id, auctionData);
      return { data: { id, ...auctionData } };
    }
    return await api.put(`/auctions/${id}`, auctionData);
  },

  /**
   * Clôturer une enchère
   */
  close: async (id) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 500));
      console.log('Mock: Closing auction', id);
      return { data: { success: true } };
    }
    return await api.put(`/auctions/${id}/close`);
  },

  /**
   * Supprimer une enchère
   */
  delete: async (id) => {
    if (USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 500));
      console.log('Mock: Deleting auction', id);
      return { data: { success: true } };
    }
    return await api.delete(`/auctions/${id}`);
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

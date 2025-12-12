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
   * Récupérer les enchères terminées
   */
  getClosed: async () => {
    return await api.get('/auctions/closed');
  },

  /**
   * Créer une enchère
   */
  create: async (auctionData) => {
    return await api.post('/auctions', auctionData);
  },

  /**
   * Mettre à jour une enchère
   */
  update: async (id, auctionData) => {
    return await api.put(`/auctions/${id}`, auctionData);
  },

  /**
   * Clôturer une enchère
   */
  close: async (id) => {
    return await api.put(`/auctions/${id}/close`);
  },

  /**
   * Supprimer une enchère
   */
  delete: async (id) => {
    return await api.delete(`/auctions/${id}`);
  },

  /**
   * Vérifier si le prix de réserve est atteint
   */
  isReserveMet: async (id) => {
    return await api.get(`/auctions/${id}/reserve-met`);
  },
};

export default auctionService;

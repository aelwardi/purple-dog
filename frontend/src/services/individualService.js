/**
 * Service pour les opérations liées aux individus
 */

import { api } from '../utils/apiClient';

export const individualService = {
  /**
   * Récupérer un individu par ID
   */
  getById: async (id) => {
    return await api.get(`/individuals/${id}`);
  },

  /**
   * Récupérer tous les individus
   */
  getAll: async () => {
    return await api.get('/individuals');
  },

  /**
   * Récupérer individus par statut de vérification
   */
  getByVerificationStatus: async (verified) => {
    return await api.get(`/individuals/verified/${verified}`);
  },

  /**
   * Mettre à jour un individu
   */
  update: async (id, updateData) => {
    return await api.put(`/individuals/${id}`, updateData);
  },

  /**
   * Supprimer un individu
   */
  delete: async (id) => {
    return await api.delete(`/individuals/${id}`);
  },

  /**
   * Vérifier l'identité d'un individu
   */
  verifyIdentity: async (id) => {
    return await api.patch(`/individuals/${id}/verify-identity`);
  },

  /**
   * Mettre à jour le statut du compte
   */
  updateAccountStatus: async (id, status) => {
    return await api.patch(`/individuals/${id}/status?status=${status}`);
  },

  /**
   * Compter les individus
   */
  count: async () => {
    return await api.get('/individuals/count');
  },
};

export default individualService;

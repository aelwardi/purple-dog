/**
 * Service pour les opérations liées aux professionnels
 */

import { api } from '../utils/apiClient';

export const professionalService = {
  /**
   * Récupérer un professionnel par ID
   */
  getById: async (id) => {
    return await api.get(`/professionals/${id}`);
  },

  /**
   * Récupérer tous les professionnels
   */
  getAll: async () => {
    return await api.get('/professionals');
  },

  /**
   * Récupérer professionnels par statut de vérification
   */
  getByVerificationStatus: async (verified) => {
    return await api.get(`/professionals/verified/${verified}`);
  },

  /**
   * Récupérer professionnels par SIRET
   */
  getBySiret: async (siret) => {
    return await api.get(`/professionals/siret/${siret}`);
  },

  /**
   * Mettre à jour un professionnel
   */
  update: async (id, updateData) => {
    return await api.put(`/professionals/${id}`, updateData);
  },

  /**
   * Supprimer un professionnel
   */
  delete: async (id) => {
    return await api.delete(`/professionals/${id}`);
  },

  /**
   * Vérifier l'identité d'un professionnel
   */
  verifyIdentity: async (id) => {
    return await api.patch(`/professionals/${id}/verify-identity`);
  },

  /**
   * Mettre à jour le statut du compte
   */
  updateAccountStatus: async (id, status) => {
    return await api.patch(`/professionals/${id}/status?status=${status}`);
  },

  /**
   * Compter les professionnels
   */
  count: async () => {
    return await api.get('/professionals/count');
  },
};

export default professionalService;

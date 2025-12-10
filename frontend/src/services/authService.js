/**
 * Service d'authentification
 * Gère toutes les opérations liées à l'authentification
 */

import { api } from '../utils/apiClient';

export const authService = {
  /**
   * Connexion utilisateur
   */
  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    if (response.token) {
      localStorage.setItem('token', response.token);
      localStorage.setItem('userType', response.userType);
      localStorage.setItem('userId', response.userId);
    }
    return response;
  },

  /**
   * Inscription individu
   */
  registerIndividual: async (userData) => {
    const response = await api.post('/individuals', userData);
    return response;
  },

  /**
   * Inscription professionnel
   */
  registerProfessional: async (userData) => {
    const response = await api.post('/professionals', userData);
    return response;
  },

  /**
   * Déconnexion
   */
  logout: async () => {
    try {
      await api.post('/auth/logout');
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('userType');
      localStorage.removeItem('userId');
    }
  },

  /**
   * Mot de passe oublié
   */
  forgotPassword: async (email) => {
    return await api.post('/auth/forgot-password', { email });
  },

  /**
   * Réinitialisation du mot de passe
   */
  resetPassword: async (token, newPassword) => {
    return await api.post('/auth/reset-password', { token, password: newPassword });
  },

  /**
   * Vérification email
   */
  verifyEmail: async (token) => {
    return await api.post('/auth/verify-email', { token });
  },

  /**
   * Récupérer l'utilisateur connecté
   */
  getCurrentUser: () => {
    return {
      token: localStorage.getItem('token'),
      userType: localStorage.getItem('userType'),
      userId: localStorage.getItem('userId'),
    };
  },

  /**
   * Vérifier si l'utilisateur est authentifié
   */
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },
};

export default authService;

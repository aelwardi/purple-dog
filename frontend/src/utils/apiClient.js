/**
 * Client API configuré avec Axios et intercepteurs
 * Gère automatiquement les erreurs, l'authentification et le logging
 */

import axios from 'axios';
import logger from './logger';
import { NetworkError } from './errorHandler';

// Configuration de base
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
const API_TIMEOUT = 30000; // 30 secondes

/**
 * Instance Axios configurée
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

/**
 * Intercepteur de requête
 * Ajoute le token d'authentification et log les requêtes
 */
apiClient.interceptors.request.use(
  (config) => {
    const startTime = Date.now();
    config.metadata = { startTime };

    // Ajout du token d'authentification si disponible
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Log de la requête
    logger.apiRequest(config.method, config.url, config.data);

    return config;
  },
  (error) => {
    logger.error('Request interceptor error', error);
    return Promise.reject(error);
  }
);

/**
 * Intercepteur de réponse
 * Gère les erreurs globales et log les réponses
 */
apiClient.interceptors.response.use(
  (response) => {
    // Calcul du temps de réponse
    const duration = Date.now() - response.config.metadata.startTime;

    // Log de la réponse
    logger.apiResponse(
      response.config.method,
      response.config.url,
      response.status,
      response.data,
      duration
    );

    return response;
  },
  (error) => {
    // Log de l'erreur
    logger.apiError(
      error.config?.method || 'UNKNOWN',
      error.config?.url || 'UNKNOWN',
      error
    );

    // Gestion des erreurs réseau
    if (!error.response) {
      // Pas de réponse du serveur (timeout, network error, etc.)
      if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
        return Promise.reject(new NetworkError('La requête a expiré. Veuillez réessayer.'));
      }
      return Promise.reject(new NetworkError());
    }

    // Gestion des erreurs 401 - Token expiré ou invalide
    if (error.response.status === 401) {
      const currentPath = window.location.pathname;
      
      // Ne pas rediriger si on est déjà sur la page de login
      if (currentPath !== '/login' && currentPath !== '/register') {
        // Nettoyer le localStorage
        localStorage.removeItem('token');
        localStorage.removeItem('userType');
        localStorage.removeItem('userEmail');
        
        // Rediriger vers login avec l'URL de retour
        setTimeout(() => {
          window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`;
        }, 1000);
      }
    }

    return Promise.reject(error);
  }
);

/**
 * Méthodes utilitaires pour les requêtes API
 */
export const api = {
  /**
   * GET request
   */
  get: async (url, config = {}) => {
    try {
      const response = await apiClient.get(url, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * POST request
   */
  post: async (url, data = {}, config = {}) => {
    try {
      const response = await apiClient.post(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * PUT request
   */
  put: async (url, data = {}, config = {}) => {
    try {
      const response = await apiClient.put(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * PATCH request
   */
  patch: async (url, data = {}, config = {}) => {
    try {
      const response = await apiClient.patch(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * DELETE request
   */
  delete: async (url, config = {}) => {
    try {
      const response = await apiClient.delete(url, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Upload de fichier
   */
  upload: async (url, file, onUploadProgress = null) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const config = {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      };

      if (onUploadProgress) {
        config.onUploadProgress = onUploadProgress;
      }

      const response = await apiClient.post(url, formData, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Upload multiple files
   */
  uploadMultiple: async (url, files, onUploadProgress = null) => {
    try {
      const formData = new FormData();
      files.forEach((file, index) => {
        formData.append(`files[${index}]`, file);
      });

      const config = {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      };

      if (onUploadProgress) {
        config.onUploadProgress = onUploadProgress;
      }

      const response = await apiClient.post(url, formData, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Download de fichier
   */
  download: async (url, filename) => {
    try {
      const response = await apiClient.get(url, {
        responseType: 'blob',
      });

      // Créer un lien de téléchargement
      const downloadUrl = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(downloadUrl);

      return true;
    } catch (error) {
      throw error;
    }
  },
};

/**
 * Méthodes d'authentification
 */
export const authApi = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  logout: () => api.post('/auth/logout'),
  refreshToken: () => api.post('/auth/refresh'),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  resetPassword: (token, password) => api.post('/auth/reset-password', { token, password }),
  verifyEmail: (token) => api.post('/auth/verify-email', { token }),
};

/**
 * Configuration du token d'authentification
 */
export const setAuthToken = (token) => {
  if (token) {
    localStorage.setItem('token', token);
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    localStorage.removeItem('token');
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

/**
 * Récupération du token d'authentification
 */
export const getAuthToken = () => {
  return localStorage.getItem('token');
};

/**
 * Suppression du token d'authentification
 */
export const clearAuthToken = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userType');
  localStorage.removeItem('userEmail');
  delete apiClient.defaults.headers.common['Authorization'];
};

export default apiClient;

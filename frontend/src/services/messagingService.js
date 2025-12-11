/**
 * Service pour les opérations de messagerie
 */

import { api } from '../utils/apiClient';

export const messagingService = {
  /**
   * Récupérer toutes les conversations d'un utilisateur
   */
  getConversations: async (userId) => {
    return await api.get(`/messaging/conversations/${userId}`);
  },

  /**
   * Récupérer une conversation par ID
   */
  getConversationById: async (conversationId) => {
    return await api.get(`/messaging/conversations/detail/${conversationId}`);
  },

  /**
   * Démarrer une nouvelle conversation
   */
  startConversation: async (conversationData) => {
    return await api.post('/messaging/conversations/start', conversationData);
  },

  /**
   * Récupérer les messages d'une conversation
   */
  getMessages: async (conversationId) => {
    return await api.get(`/messaging/${conversationId}/messages`);
  },

  /**
   * Envoyer un message
   */
  sendMessage: async (conversationId, content) => {
    return await api.post(`/messaging/${conversationId}/messages`, { content });
  },

  /**
   * Marquer un message comme lu
   */
  markAsRead: async (messageId) => {
    return await api.patch(`/messaging/messages/${messageId}/read`);
  },

  /**
   * Supprimer une conversation
   */
  deleteConversation: async (conversationId) => {
    return await api.delete(`/messaging/conversations/${conversationId}`);
  },
};

export default messagingService;

import api from './api';

const favoriteService = {
  /**
   * Get user's favorites
   * @param {number} userId - User ID
   * @returns {Promise<Array>} List of favorite products
   */
  getUserFavorites: async (userId) => {
    const response = await api.get(`/favorites/user/${userId}`);
    return response.data;
  },

  /**
   * Add product to favorites
   * @param {number} userId - User ID
   * @param {number} productId - Product ID
   * @returns {Promise<Object>} Response
   */
  addToFavorites: async (userId, productId) => {
    const response = await api.post(`/favorites/user/${userId}/product/${productId}`);
    return response.data;
  },

  /**
   * Remove product from favorites
   * @param {number} userId - User ID
   * @param {number} productId - Product ID
   * @returns {Promise<void>}
   */
  removeFromFavorites: async (userId, productId) => {
    const response = await api.delete(`/favorites/user/${userId}/product/${productId}`);
    return response.data;
  },

  /**
   * Check if product is in favorites
   * @param {number} userId - User ID
   * @param {number} productId - Product ID
   * @returns {Promise<boolean>} True if favorite
   */
  isFavorite: async (userId, productId) => {
    const response = await api.get(`/favorites/user/${userId}/product/${productId}/check`);
    return response.data;
  },

  /**
   * Get favorites count for user
   * @param {number} userId - User ID
   * @returns {Promise<number>} Number of favorites
   */
  getFavoritesCount: async (userId) => {
    const response = await api.get(`/favorites/user/${userId}/count`);
    return response.data;
  }
};

export default favoriteService;


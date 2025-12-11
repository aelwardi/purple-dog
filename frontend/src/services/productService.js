import api from './api';

const productService = {
  /**
   * Create a new product listing
   * @param {Object} productData
   * @returns {Promise<Object>} Created product
   */
  createProduct: async (productData) => {
    const response = await api.post('/products', productData);
    return response.data;
  },

  /**
   * Get product by ID
   * @param {number} productId
   * @returns {Promise<Object>} Product details
   */
  getProduct: async (productId) => {
    const response = await api.get(`/products/${productId}`);
    return response.data;
  },

  /**
   * Get products by seller
   * @param {number} sellerId
   * @param {string} status - Optional filter by status
   * @returns {Promise<Array>} List of products
   */
  getProductsBySeller: async (sellerId, status = null) => {
    const params = status ? { status } : {};
    const response = await api.get(`/products/seller/${sellerId}`, { params });
    return response.data;
  },

  /**
   * Search products
   * @param {Object} searchParams
   * @returns {Promise<Array>} List of products
   */
  searchProducts: async (searchParams) => {
    const response = await api.get('/products/search', { params: searchParams });
    return response.data;
  },

  /**
   * Add product to favorites
   * @param {number} userId
   * @param {number} productId
   * @returns {Promise<Object>} Response
   */
  addFavorite: async (userId, productId) => {
    const response = await api.post(`/products/${productId}/favorite`, null, {
      params: { userId }
    });
    return response.data;
  },

  /**
   * Remove product from favorites
   * @param {number} userId
   * @param {number} productId
   * @returns {Promise<Object>} Response
   */
  removeFavorite: async (userId, productId) => {
    const response = await api.delete(`/products/${productId}/favorite`, {
      params: { userId }
    });
    return response.data;
  },

  /**
   * Get user's favorite products
   * @param {number} userId
   * @returns {Promise<Array>} List of favorite products
   */
  getFavorites: async (userId) => {
    const response = await api.get(`/products/favorites/${userId}`);
    return response.data;
  },
};

export default productService;

import api from './api';

const categoryService = {
  /**
   * Get all categories
   * @returns {Promise<Array>} List of all categories
   */
  getAllCategories: async () => {
    const response = await api.get('/categories');
    return response.data;
  },

  /**
   * Get active categories only
   * @returns {Promise<Array>} List of active categories
   */
  getActiveCategories: async () => {
    const response = await api.get('/categories/active');
    return response.data;
  },

  /**
   * Get category by ID
   * @param {number} categoryId
   * @returns {Promise<Object>} Category details
   */
  getCategory: async (categoryId) => {
    const response = await api.get(`/categories/${categoryId}`);
    return response.data;
  },
};

export default categoryService;


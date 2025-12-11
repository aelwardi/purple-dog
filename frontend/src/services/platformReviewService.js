import api from './api';

const platformReviewService = {
  /**
   * Create a platform review/feedback
   * @param {Object} reviewData
   * @returns {Promise<Object>} Created review
   */
  createReview: async (reviewData) => {
    const response = await api.post('/platform/reviews', reviewData);
    return response.data;
  },

  /**
   * Get all reviews (Admin only)
   * @returns {Promise<Array>} List of reviews
   */
  getAllReviews: async () => {
    const response = await api.get('/platform/reviews/admin/all');
    return response.data;
  },

  /**
   * Get pending reviews (Admin only)
   * @returns {Promise<Array>} List of pending reviews
   */
  getPendingReviews: async () => {
    const response = await api.get('/platform/reviews/admin/pending');
    return response.data;
  },

  /**
   * Get reviews by status (Admin only)
   * @param {string} status - PENDING, APPROVED, REJECTED
   * @returns {Promise<Array>} List of reviews
   */
  getReviewsByStatus: async (status) => {
    const response = await api.get(`/platform/reviews/admin/status/${status}`);
    return response.data;
  },

  /**
   * Get review by ID (Admin only)
   * @param {number} reviewId
   * @returns {Promise<Object>} Review details
   */
  getReviewById: async (reviewId) => {
    const response = await api.get(`/platform/reviews/admin/${reviewId}`);
    return response.data;
  }
};

export default platformReviewService;


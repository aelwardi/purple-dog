import api from './api';

const orderService = {
  /**
   * Get order by ID
   * @param {number} orderId
   * @returns {Promise<Object>} Order details
   */
  getOrderById: async (orderId) => {
    const response = await api.get(`/orders/${orderId}`);
    return response.data;
  },

  /**
   * Get order by order number
   * @param {string} orderNumber
   * @returns {Promise<Object>} Order details
   */
  getOrderByNumber: async (orderNumber) => {
    const response = await api.get(`/orders/number/${orderNumber}`);
    return response.data;
  },

  /**
   * Get all orders (admin)
   * @returns {Promise<Array>} List of orders
   */
  getAllOrders: async () => {
    const response = await api.get('/orders');
    return response.data;
  },

  /**
   * Get orders for a buyer
   * @param {number} buyerId
   * @returns {Promise<Array>} List of buyer's orders
   */
  getBuyerOrders: async (buyerId) => {
    const response = await api.get(`/orders/buyer/${buyerId}`);
    return response.data;
  },

  /**
   * Get orders for a seller
   * @param {number} sellerId
   * @returns {Promise<Array>} List of seller's orders
   */
  getSellerOrders: async (sellerId) => {
    const response = await api.get(`/orders/seller/${sellerId}`);
    return response.data;
  },

  /**
   * Get orders by status
   * @param {string} status
   * @returns {Promise<Array>} List of orders with status
   */
  getOrdersByStatus: async (status) => {
    const response = await api.get(`/orders/status/${status}`);
    return response.data;
  },

  /**
   * Get orders for a person (buyer or seller)
   * @param {number} personId
   * @returns {Promise<Array>} List of person's orders
   */
  getPersonOrders: async (personId) => {
    const response = await api.get(`/orders/person/${personId}`);
    return response.data;
  },

  /**
   * Create a new order
   * @param {Object} orderData
   * @returns {Promise<Object>} Created order
   */
  createOrder: async (orderData) => {
    const response = await api.post('/orders', orderData);
    return response.data;
  },

  /**
   * Update order status
   * @param {number} orderId
   * @param {string} status
   * @returns {Promise<Object>} Updated order
   */
  updateOrderStatus: async (orderId, status) => {
    const response = await api.patch(`/orders/${orderId}/status`, null, {
      params: { status }
    });
    return response.data;
  },

  /**
   * Cancel an order
   * @param {number} orderId
   * @returns {Promise<Object>} Cancelled order
   */
  cancelOrder: async (orderId) => {
    const response = await api.patch(`/orders/${orderId}/cancel`);
    return response.data;
  },

  /**
   * Confirm order delivery
   * @param {number} orderId
   * @returns {Promise<Object>} Order with delivery confirmed
   */
  confirmDelivery: async (orderId) => {
    const response = await api.patch(`/orders/${orderId}/confirm-delivery`);
    return response.data;
  },

  /**
   * Get order count by status
   * @param {string} status
   * @returns {Promise<number>} Number of orders
   */
  getOrderCountByStatus: async (status) => {
    const response = await api.get(`/orders/count/status/${status}`);
    return response.data;
  },

  /**
   * Get order count for buyer
   * @param {number} buyerId
   * @returns {Promise<number>} Number of orders
   */
  getBuyerOrderCount: async (buyerId) => {
    const response = await api.get(`/orders/count/buyer/${buyerId}`);
    return response.data;
  },

  /**
   * Get order count for seller
   * @param {number} sellerId
   * @returns {Promise<number>} Number of orders
   */
  getSellerOrderCount: async (sellerId) => {
    const response = await api.get(`/orders/count/seller/${sellerId}`);
    return response.data;
  }
};

export default orderService;

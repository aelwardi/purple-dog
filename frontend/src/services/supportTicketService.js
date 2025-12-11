import api from './api';

const supportTicketService = {
  /**
   * Create a new support ticket
   * @param {number} userId - User ID
   * @param {Object} ticketData - { subject, description, priority, category }
   * @returns {Promise<Object>} Created ticket
   */
  createTicket: async (userId, ticketData) => {
    const response = await api.post(`/support/tickets/user/${userId}`, ticketData);
    return response.data;
  },

  /**
   * Get current user's tickets
   * @param {number} userId - User ID
   * @returns {Promise<Array>} List of tickets
   */
  getMyTickets: async (userId) => {
    const response = await api.get(`/support/tickets/user/${userId}`);
    return response.data;
  },

  /**
   * Get a specific ticket by ID
   * @param {number} ticketId
   * @returns {Promise<Object>} Ticket details
   */
  getTicketById: async (ticketId) => {
    const response = await api.get(`/support/tickets/${ticketId}`);
    return response.data;
  },

  /**
   * Get a specific ticket by number
   * @param {string} ticketNumber
   * @returns {Promise<Object>} Ticket details
   */
  getTicketByNumber: async (ticketNumber) => {
    const response = await api.get(`/support/tickets/number/${ticketNumber}`);
    return response.data;
  },

  /**
   * Update ticket (user can add messages)
   * @param {string} ticketNumber
   * @param {Object} updateData
   * @returns {Promise<Object>} Updated ticket
   */
  updateTicket: async (ticketNumber, updateData) => {
    const response = await api.put(`/support/tickets/${ticketNumber}`, updateData);
    return response.data;
  },

  /**
   * Add a message to a ticket
   * @param {number} ticketId - Ticket ID
   * @param {string} message
   * @returns {Promise<Object>} Response
   */
  addMessage: async (ticketId, message) => {
    const response = await api.post(`/support/tickets/${ticketId}/messages`, { message });
    return response.data;
  },

  /**
   * Close a ticket
   * @param {number} ticketId - Ticket ID
   * @returns {Promise<Object>} Response
   */
  closeTicket: async (ticketId) => {
    const response = await api.put(`/support/tickets/${ticketId}/close`);
    return response.data;
  },
};

export default supportTicketService;


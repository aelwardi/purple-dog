import apiClient from '../utils/apiClient';

const adminService = {
  // Get overall dashboard statistics
  getDashboardStats: async () => {
    const response = await apiClient.get('/admin/dashboard/stats');
    return response.data;
  },

  // Get detailed user statistics
  getUserStats: async () => {
    const response = await apiClient.get('/admin/dashboard/users/stats');
    return response.data;
  },

  // Get detailed product statistics
  getProductStats: async () => {
    const response = await apiClient.get('/admin/dashboard/products/stats');
    return response.data;
  },

  // Get detailed revenue statistics
  getRevenueStats: async () => {
    const response = await apiClient.get('/admin/dashboard/revenue/stats');
    return response.data;
  },

  // Get recent activity logs
  getRecentActivity: async (limit = 20) => {
    const response = await apiClient.get('/admin/dashboard/activity', {
      params: { limit }
    });
    return response.data;
  },

  // Get paginated list of all users
  getAllUsers: async (page = 0, size = 10, sortBy = 'createdAt', direction = 'DESC') => {
    const response = await apiClient.get('/admin/dashboard/users', {
      params: { page, size, sortBy, direction }
    });
    return response.data;
  }
};

export default adminService;

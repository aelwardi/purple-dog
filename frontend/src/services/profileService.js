import api from './api';

const profileService = {
  /**
   * Get current user profile
   * @returns {Promise<Object>} User profile data
   */
  getCurrentProfile: async () => {
    const response = await api.get('/profile');
    return response.data;
  },

  /**
   * Update current user profile
   * @param {Object} profileData - Profile data to update
   * @returns {Promise<Object>} Updated profile data
   */
  updateProfile: async (profileData) => {
    const response = await api.put('/profile', profileData);
    return response.data;
  },

  /**
   * Change password
   * @param {Object} passwordData - { currentPassword, newPassword, confirmPassword }
   * @returns {Promise<Object>} Response message
   */
  changePassword: async (passwordData) => {
    const response = await api.put('/profile/change-password', passwordData);
    return response.data;
  },

  /**
   * Delete current user account
   * @returns {Promise<Object>} Response message
   */
  deleteAccount: async () => {
    const response = await api.delete('/profile');
    return response.data;
  },
};

export default profileService;


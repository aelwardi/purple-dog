import api from './api';

/**
 * Service for file uploads
 */
const uploadService = {
  /**
   * Upload multiple photos
   * @param {File[]} photos - Array of photo files
   * @returns {Promise<string[]>} Array of uploaded photo URLs
   */
  uploadPhotos: async (photos) => {
    const formData = new FormData();

    // Append each photo to FormData
    photos.forEach(photo => {
      formData.append('photos', photo);
    });

    const response = await api.post('/upload/photos', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    return response.data.urls;
  },

  /**
   * Upload a single document
   * @param {File} document - Document file
   * @returns {Promise<string>} Uploaded document URL
   */
  uploadDocument: async (document) => {
    const formData = new FormData();
    formData.append('document', document);

    const response = await api.post('/upload/document', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    return response.data.url;
  },

  /**
   * Delete an uploaded file
   * @param {string} url - File URL to delete
   * @returns {Promise<Object>} Response
   */
  deleteFile: async (url) => {
    const response = await api.delete('/upload/file', {
      params: { url }
    });
    return response.data;
  }
};

export default uploadService;

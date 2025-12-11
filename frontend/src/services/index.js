/**
 * Point d'entrée centralisé pour tous les services API
 * Facilite l'importation et l'utilisation des services dans l'application
 */

export { default as authService } from './authService';
export { default as individualService } from './individualService';
export { default as professionalService } from './professionalService';
export { default as productService } from './productService';
export { default as categoryService } from './categoryService';
export { default as orderService } from './orderService';
export { default as messagingService } from './messagingService';
export { default as auctionService } from './auctionService';

// Export du client API de base
export { api, setAuthToken, getAuthToken, clearAuthToken } from '../utils/apiClient';

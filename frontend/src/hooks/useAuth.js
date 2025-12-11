import { useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';

/**
 * Hook to access authentication context
 * @returns {Object} Auth context with user, login, logout, etc.
 */
export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
};

import React, { createContext, useState, useEffect, useCallback } from 'react';
import authService from '../services/authService';
import { setAuthToken, clearAuthToken } from '../utils/apiClient';

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  /**
   * Initialize auth state from localStorage
   */
  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem('accessToken');
      const storedUser = localStorage.getItem('user');

      if (token && storedUser) {
        try {
          // Ensure api client header is set
          setAuthToken(token);

          // Verify token is still valid by fetching user
          const userData = await authService.getCurrentUser();
          setUser(userData);
          setIsAuthenticated(true);
        } catch (error) {
          // Token invalid, clear storage
          console.error('Token validation failed:', error);
          clearAuthToken();
          localStorage.removeItem('user');
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  /**
   * Login function
   */
  const login = useCallback(async (credentials) => {
    try {
      const response = await authService.login(credentials);

      // Store tokens and user info
      localStorage.setItem('accessToken', response.accessToken);
      if (response.refreshToken) {
        localStorage.setItem('refreshToken', response.refreshToken);
      }
      localStorage.setItem('user', JSON.stringify(response.user));

      // Ensure api client header is set
      setAuthToken(response.accessToken);

      setUser(response.user);
      setIsAuthenticated(true);

      return response;
    } catch (error) {
      throw error;
    }
  }, []);

  /**
   * Register individual function
   */
  const registerIndividual = useCallback(async (userData) => {
    try {
      const response = await authService.registerIndividual(userData);

      // Store tokens and user info
      localStorage.setItem('accessToken', response.accessToken);
      if (response.refreshToken) {
        localStorage.setItem('refreshToken', response.refreshToken);
      }
      localStorage.setItem('user', JSON.stringify(response.user));

      // Ensure api client header is set
      setAuthToken(response.accessToken);

      setUser(response.user);
      setIsAuthenticated(true);

      return response;
    } catch (error) {
      throw error;
    }
  }, []);

  /**
   * Register professional function
   */
  const registerProfessional = useCallback(async (userData) => {
    try {
      const response = await authService.registerProfessional(userData);

      // Store tokens and user info
      localStorage.setItem('accessToken', response.accessToken);
      if (response.refreshToken) {
        localStorage.setItem('refreshToken', response.refreshToken);
      }
      localStorage.setItem('user', JSON.stringify(response.user));

      // Ensure api client header is set
      setAuthToken(response.accessToken);

      setUser(response.user);
      setIsAuthenticated(true);

      return response;
    } catch (error) {
      throw error;
    }
  }, []);

  /**
   * Logout function
   */
  const logout = useCallback(async () => {
    try {
      await authService.logout();
    } finally {
      setUser(null);
      setIsAuthenticated(false);
      clearAuthToken();
    }
  }, []);

  /**
   * Update user info
   */
  const updateUser = useCallback((userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  }, []);

  const value = {
    user,
    loading,
    isAuthenticated,
    login,
    registerIndividual,
    registerProfessional,
    logout,
    updateUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

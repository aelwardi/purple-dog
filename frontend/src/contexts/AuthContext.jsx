import React, { createContext, useState, useEffect, useContext } from 'react';
import { authService } from '../services';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Charger l'utilisateur depuis localStorage au démarrage
  useEffect(() => {
    const loadUser = () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (currentUser.token) {
          setUser({
            token: currentUser.token,
            userType: currentUser.userType,
            userId: currentUser.userId,
          });
        }
      } catch (error) {
        console.error('Error loading user:', error);
      } finally {
        setLoading(false);
      }
    };

    loadUser();
  }, []);

  const login = async (credentials) => {
    const response = await authService.login(credentials);
    const userData = {
      token: response.token,
      userType: response.userType,
      userId: response.userId,
    };
    setUser(userData);
    return response;
  };

  const logout = async () => {
    try {
      await authService.logout();
    } finally {
      setUser(null);
      authService.clearAuthToken();
    }
  };

  const register = async (userData, type = 'individual') => {
    if (type === 'individual') {
      return await authService.registerIndividual(userData);
    } else {
      return await authService.registerProfessional(userData);
    }
  };

  const isAuthenticated = () => {
    return !!user && !!user.token;
  };

  const value = {
    user,
    loading,
    login,
    logout,
    register,
    isAuthenticated,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;

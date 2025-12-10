import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import DashboardIndividualPage from './DashboardIndividualPage';
import DashboardProfessionalPage from './DashboardProfessionalPage';

const DashboardPage = () => {
  const { user, loading } = useAuth();

  // Afficher loader pendant chargement
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Chargement...</p>
        </div>
      </div>
    );
  }

  // Rediriger vers login si pas d'utilisateur
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Render le dashboard approprié selon le type d'utilisateur
  if (user.userType === 'INDIVIDUAL') {
    return <DashboardIndividualPage />;
  } else if (user.userType === 'PROFESSIONAL') {
    return <DashboardProfessionalPage />;
  } else {
    // Type invalide, rediriger vers login
    return <Navigate to="/login" replace />;
  }
};

export default DashboardPage;

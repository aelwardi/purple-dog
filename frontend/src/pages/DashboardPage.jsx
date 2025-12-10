import React from 'react';
import { useSearchParams, Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import DashboardIndividualPage from './DashboardIndividualPage';
import DashboardProfessionalPage from './DashboardProfessionalPage';

const DashboardPage = () => {
  const [searchParams] = useSearchParams();
  const { user, loading } = useAuth();
  const typeParam = searchParams.get('type');

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  let dashboardType = typeParam;

  if (!dashboardType && user) {
    if (user.role === 'INDIVIDUAL') {
      dashboardType = 'individual';
    } else if (user.role === 'PROFESSIONAL') {
      dashboardType = 'professional';
    } else if (user.role === 'ADMIN') {
      dashboardType = 'admin';
    }
  }

  if (dashboardType === 'individual') {
    return <DashboardIndividualPage />;
  } else if (dashboardType === 'professional') {
    return <DashboardProfessionalPage />;
  } else if (dashboardType === 'admin') {
    return <DashboardProfessionalPage />; // For now, use professional dashboard for admin
  } else {
    // Invalid type or no user, redirect to login
    return <Navigate to="/login" replace />;
  }
};

export default DashboardPage;

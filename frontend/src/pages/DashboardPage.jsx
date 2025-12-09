import React from 'react';
import { useSearchParams, Navigate } from 'react-router-dom';
import DashboardIndividualPage from './DashboardIndividualPage';
import DashboardProfessionalPage from './DashboardProfessionalPage';

const DashboardPage = () => {
  const [searchParams] = useSearchParams();
  const type = searchParams.get('type');

  // Redirect to login if no type specified
  if (!type) {
    return <Navigate to="/login" replace />;
  }

  // Render the appropriate dashboard based on type
  if (type === 'individual') {
    return <DashboardIndividualPage />;
  } else if (type === 'professional') {
    return <DashboardProfessionalPage />;
  } else {
    // Invalid type, redirect to login
    return <Navigate to="/login" replace />;
  }
};

export default DashboardPage;

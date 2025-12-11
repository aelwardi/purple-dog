import React from 'react';
import { useSearchParams } from 'react-router-dom';
import RegisterIndividualPage from './RegisterIndividualPage';
import RegisterProfessionalPage from './RegisterProfessionalPage';

const RegisterPage = () => {
  const [searchParams] = useSearchParams();
  const type = searchParams.get('type'); // 'individual' ou 'professional'

  if (type === 'professional') {
    return <RegisterProfessionalPage />;
  }

  // Par défaut, afficher le formulaire particulier
  return <RegisterIndividualPage />;
};

export default RegisterPage;

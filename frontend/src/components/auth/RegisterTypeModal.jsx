import React from 'react';
import { useNavigate } from 'react-router-dom';
import { UserCircleIcon, BuildingOfficeIcon, XMarkIcon } from '@heroicons/react/24/outline';
import Modal from '../common/Modal';
import Card from '../common/Card';

const RegisterTypeModal = ({ isOpen, onClose }) => {
  const navigate = useNavigate();

  const handleSelectType = (type) => {
    navigate(`/register?type=${type}`);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <div className="text-center mb-6">
        <h2 className="text-3xl font-display font-bold text-gray-900 mb-2">
          Créer un compte
        </h2>
        <p className="text-gray-600">
          Choisissez le type de compte qui vous correspond
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Particulier */}
        <button
          onClick={() => handleSelectType('individual')}
          className="group relative p-8 bg-white border-2 border-gray-200 rounded-xl hover:border-purple-500 hover:shadow-lg transition-all duration-300 text-left"
        >
          <div className="flex flex-col items-center text-center">
            <div className="w-20 h-20 bg-purple-100 rounded-full flex items-center justify-center mb-4 group-hover:bg-purple-600 transition-colors">
              <UserCircleIcon className="w-10 h-10 text-purple-600 group-hover:text-white transition-colors" />
            </div>
            <h3 className="text-xl font-display font-semibold text-gray-900 mb-2">
              Particulier
            </h3>
            <p className="text-sm text-gray-600 mb-4">
              Je souhaite vendre mes objets de valeur
            </p>
            <ul className="text-xs text-gray-500 space-y-2 text-left w-full">
              <li className="flex items-start">
                <span className="text-green-500 mr-2">✓</span>
                <span>Inscription gratuite</span>
              </li>
              <li className="flex items-start">
                <span className="text-green-500 mr-2">✓</span>
                <span>Vente aux professionnels</span>
              </li>
              <li className="flex items-start">
                <span className="text-green-500 mr-2">✓</span>
                <span>Identité anonymisée</span>
              </li>
            </ul>
          </div>
          <div className="mt-6 px-4 py-2 bg-purple-600 text-white rounded-lg font-medium text-center group-hover:bg-purple-700 transition-colors">
            S'inscrire
          </div>
        </button>

        {/* Professionnel */}
        <button
          onClick={() => handleSelectType('professional')}
          className="group relative p-8 bg-white border-2 border-gray-200 rounded-xl hover:border-purple-500 hover:shadow-lg transition-all duration-300 text-left"
        >
          <div className="flex flex-col items-center text-center">
            <div className="w-20 h-20 bg-purple-100 rounded-full flex items-center justify-center mb-4 group-hover:bg-purple-600 transition-colors">
              <BuildingOfficeIcon className="w-10 h-10 text-purple-600 group-hover:text-white transition-colors" />
            </div>
            <h3 className="text-xl font-display font-semibold text-gray-900 mb-2">
              Professionnel
            </h3>
            <p className="text-sm text-gray-600 mb-4">
              Je souhaite acheter et vendre des objets
            </p>
            <ul className="text-xs text-gray-500 space-y-2 text-left w-full">
              <li className="flex items-start">
                <span className="text-green-500 mr-2">✓</span>
                <span>1 mois gratuit, puis 49€/mois</span>
              </li>
              <li className="flex items-start">
                <span className="text-green-500 mr-2">✓</span>
                <span>Accès illimité aux objets</span>
              </li>
              <li className="flex items-start">
                <span className="text-green-500 mr-2">✓</span>
                <span>Enchères et vente rapide</span>
              </li>
            </ul>
          </div>
          <div className="mt-6 px-4 py-2 bg-purple-600 text-white rounded-lg font-medium text-center group-hover:bg-purple-700 transition-colors">
            S'inscrire
          </div>
        </button>
      </div>

      <p className="text-center text-sm text-gray-500 mt-6">
        Déjà inscrit ?{' '}
        <button
          onClick={() => {
            navigate('/login');
            onClose();
          }}
          className="text-purple-600 hover:text-purple-700 font-medium"
        >
          Se connecter
        </button>
      </p>
    </Modal>
  );
};

export default RegisterTypeModal;

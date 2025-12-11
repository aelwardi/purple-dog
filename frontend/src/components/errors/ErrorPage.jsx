/**
 * Error Page Component
 * Page d'erreur générique pour 404, 500, etc.
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../common/Button';
import Card from '../common/Card';

const ErrorPage = ({ 
  code = 404, 
  title = 'Page non trouvée',
  message = 'La page que vous recherchez n\'existe pas ou a été déplacée.',
  showHomeButton = true,
  showBackButton = true,
}) => {
  const navigate = useNavigate();

  const errorConfig = {
    404: {
      icon: '🔍',
      title: 'Page non trouvée',
      message: 'La page que vous recherchez n\'existe pas ou a été déplacée.',
      bgColor: 'bg-blue-100',
      textColor: 'text-blue-600',
    },
    500: {
      icon: '⚠️',
      title: 'Erreur serveur',
      message: 'Une erreur s\'est produite sur le serveur. Nous travaillons à la résoudre.',
      bgColor: 'bg-red-100',
      textColor: 'text-red-600',
    },
    403: {
      icon: '⛔',
      title: 'Accès refusé',
      message: 'Vous n\'avez pas les permissions nécessaires pour accéder à cette page.',
      bgColor: 'bg-yellow-100',
      textColor: 'text-yellow-600',
    },
  };

  const config = errorConfig[code] || errorConfig[404];

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="max-w-2xl w-full">
        <Card className="text-center">
          {/* Code d'erreur */}
          <div className="mb-6">
            <div className={`inline-flex items-center justify-center w-24 h-24 ${config.bgColor} rounded-full mb-4`}>
              <span className="text-5xl">{config.icon}</span>
            </div>
            <h1 className={`text-6xl font-bold ${config.textColor} mb-2`}>
              {code}
            </h1>
          </div>

          {/* Titre et message */}
          <h2 className="text-3xl font-display font-bold text-gray-900 mb-4">
            {title || config.title}
          </h2>
          <p className="text-gray-600 mb-8 text-lg max-w-md mx-auto">
            {message || config.message}
          </p>

          {/* Actions */}
          <div className="flex flex-col sm:flex-row gap-4 justify-center mb-8">
            {showHomeButton && (
              <Button
                variant="primary"
                onClick={() => navigate('/')}
                className="min-w-[160px]"
              >
                Retour à l'accueil
              </Button>
            )}
            {showBackButton && (
              <Button
                variant="outline"
                onClick={() => navigate(-1)}
                className="min-w-[160px]"
              >
                Retour en arrière
              </Button>
            )}
          </div>

          {/* Suggestions */}
          <div className="pt-6 border-t border-gray-200">
            <p className="text-sm font-medium text-gray-900 mb-3">
              Que souhaitez-vous faire ?
            </p>
            <div className="flex flex-wrap gap-2 justify-center">
              <button
                onClick={() => navigate('/search')}
                className="text-sm text-purple-600 hover:text-purple-700 hover:underline"
              >
                Rechercher un produit
              </button>
              <span className="text-gray-300">•</span>
              <button
                onClick={() => navigate('/about')}
                className="text-sm text-purple-600 hover:text-purple-700 hover:underline"
              >
                En savoir plus
              </button>
              <span className="text-gray-300">•</span>
              <button
                onClick={() => navigate('/contact')}
                className="text-sm text-purple-600 hover:text-purple-700 hover:underline"
              >
                Nous contacter
              </button>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default ErrorPage;

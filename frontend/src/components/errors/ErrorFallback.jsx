/**
 * Error Fallback Component
 * Interface de secours affichée quand une erreur React se produit
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../common/Button';
import Card from '../common/Card';

const ErrorFallback = ({ error, errorInfo, resetError }) => {
  const navigate = useNavigate();
  const isDevelopment = import.meta.env.MODE === 'development';

  const handleGoHome = () => {
    resetError();
    navigate('/');
  };

  const handleReload = () => {
    resetError();
    window.location.reload();
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="max-w-2xl w-full">
        <Card className="text-center">
          {/* Icône d'erreur */}
          <div className="mb-6">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-red-100 rounded-full">
              <svg
                className="w-10 h-10 text-red-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                />
              </svg>
            </div>
          </div>

          {/* Titre */}
          <h1 className="text-3xl font-display font-bold text-gray-900 mb-4">
            Oups ! Une erreur s'est produite
          </h1>

          {/* Message */}
          <p className="text-gray-600 mb-6 text-lg">
            Nous sommes désolés, quelque chose s'est mal passé. Notre équipe a été notifiée et travaille sur le problème.
          </p>

          {/* Détails de l'erreur en développement */}
          {isDevelopment && error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6 text-left">
              <h3 className="text-sm font-semibold text-red-900 mb-2">
                Détails de l'erreur (MODE DEV):
              </h3>
              <div className="space-y-2">
                <div>
                  <p className="text-xs font-medium text-red-800">Message:</p>
                  <p className="text-xs text-red-700 font-mono bg-red-100 p-2 rounded mt-1 overflow-auto">
                    {error.message || 'Erreur inconnue'}
                  </p>
                </div>
                {error.stack && (
                  <div>
                    <p className="text-xs font-medium text-red-800 mb-1">Stack trace:</p>
                    <pre className="text-xs text-red-700 font-mono bg-red-100 p-2 rounded overflow-auto max-h-40">
                      {error.stack}
                    </pre>
                  </div>
                )}
                {errorInfo?.componentStack && (
                  <div>
                    <p className="text-xs font-medium text-red-800 mb-1">Component stack:</p>
                    <pre className="text-xs text-red-700 font-mono bg-red-100 p-2 rounded overflow-auto max-h-40">
                      {errorInfo.componentStack}
                    </pre>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button
              variant="primary"
              onClick={handleReload}
              className="min-w-[160px]"
            >
              Recharger la page
            </Button>
            <Button
              variant="outline"
              onClick={handleGoHome}
              className="min-w-[160px]"
            >
              Retour à l'accueil
            </Button>
          </div>

          {/* Informations supplémentaires */}
          <div className="mt-8 pt-6 border-t border-gray-200">
            <p className="text-sm text-gray-500">
              Si le problème persiste, veuillez contacter notre support à{' '}
              <a
                href="mailto:support@purple-dog.com"
                className="text-purple-600 hover:text-purple-700 font-medium"
              >
                support@purple-dog.com
              </a>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default ErrorFallback;

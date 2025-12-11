import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useErrorHandler } from '../hooks/useErrorHandler';
import authService from '../services/authService';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import Logo from '../components/common/Logo';
import { CheckCircleIcon } from '@heroicons/react/24/outline';

const resetPasswordSchema = z.object({
  password: z.string().min(8, 'Le mot de passe doit contenir au moins 8 caractères'),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Les mots de passe ne correspondent pas',
  path: ['confirmPassword'],
});

const ResetPasswordPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { showSuccess, handleError } = useErrorHandler();
  const [resetSuccess, setResetSuccess] = useState(false);
  const [token, setToken] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(resetPasswordSchema),
  });

  useEffect(() => {
    const tokenParam = searchParams.get('token');
    if (!tokenParam) {
      handleError(new Error('Token manquant'));
      navigate('/');
    } else {
      setToken(tokenParam);
    }
  }, [searchParams, navigate, handleError]);

  const onSubmit = async (data) => {
    try {
      await authService.resetPassword(token, data.password);
      setResetSuccess(true);
      showSuccess('Mot de passe réinitialisé avec succès !');

      // Redirect to homepage after 3 seconds
      setTimeout(() => {
        navigate('/');
      }, 3000);
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Une erreur est survenue';
      handleError(new Error(errorMessage));
    }
  };

  if (!token) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        {/* Logo */}
        <div className="text-center mb-8">
          <Logo className="mx-auto mb-4" />
          <h2 className="text-3xl font-bold text-gray-900">
            Réinitialiser le mot de passe
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Choisissez un nouveau mot de passe sécurisé
          </p>
        </div>

        <Card className="p-8">
          {!resetSuccess ? (
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <Input
                label="Nouveau mot de passe"
                type="password"
                {...register('password')}
                error={errors.password?.message}
                placeholder="••••••••"
                disabled={isSubmitting}
              />

              <Input
                label="Confirmer le mot de passe"
                type="password"
                {...register('confirmPassword')}
                error={errors.confirmPassword?.message}
                placeholder="••••••••"
                disabled={isSubmitting}
              />

              {/* Password requirements */}
              <div className="bg-gray-50 p-3 rounded-lg">
                <p className="text-xs text-gray-600 mb-1">Le mot de passe doit contenir :</p>
                <ul className="text-xs text-gray-500 space-y-0.5">
                  <li>✓ Au moins 8 caractères</li>
                  <li>✓ Une majuscule et une minuscule</li>
                  <li>✓ Un chiffre</li>
                </ul>
              </div>

              <Button
                type="submit"
                variant="primary"
                className="w-full"
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Réinitialisation...' : 'Réinitialiser le mot de passe'}
              </Button>
            </form>
          ) : (
            <div className="text-center py-6">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
                <CheckCircleIcon className="w-10 h-10 text-green-600" />
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">
                Mot de passe réinitialisé !
              </h3>
              <p className="text-sm text-gray-600 mb-4">
                Votre mot de passe a été changé avec succès. Vous allez être redirigé vers la page d'accueil...
              </p>
              <div className="flex justify-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600"></div>
              </div>
            </div>
          )}
        </Card>

        {/* Back to home link */}
        <div className="mt-6 text-center">
          <button
            onClick={() => navigate('/')}
            className="text-sm text-purple-600 hover:text-purple-700"
          >
            Retour à l'accueil
          </button>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordPage;


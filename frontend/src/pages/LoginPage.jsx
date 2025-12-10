import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema } from '../schemas/authSchemas';
import { useErrorHandler } from '../hooks/useErrorHandler';
import { authService } from '../services';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';

const LoginPage = () => {
  const navigate = useNavigate();
  const { showSuccess, handleError } = useErrorHandler();
  
  const {
    register,
    handleSubmit: handleFormSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = async (data) => {
    try {
      const response = await authService.login(data);
      
      showSuccess('Connexion réussie !');
      
      // Redirection selon le type d'utilisateur
      if (response.userType === 'INDIVIDUAL') {
        navigate('/dashboard?type=individual');
      } else if (response.userType === 'PROFESSIONAL') {
        navigate('/dashboard?type=professional');
      } else {
        navigate('/dashboard');
      }
    } catch (error) {
      handleError(error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-display font-bold text-gray-900">
            Connexion
          </h2>
          <p className="mt-2 text-gray-600">
            Connectez-vous à votre compte Purple Dog
          </p>
        </div>

        <Card>
          <form onSubmit={handleFormSubmit(onSubmit)} className="space-y-6">
            <Input
              label="Email"
              type="email"
              {...register('email')}
              error={errors.email?.message}
              placeholder="votre@email.com"
              disabled={isSubmitting}
            />

            <Input
              label="Mot de passe"
              type="password"
              {...register('password')}
              error={errors.password?.message}
              placeholder="••••••••"
              disabled={isSubmitting}
            />

            <div className="flex items-center justify-between">
              <label className="flex items-center">
                <input type="checkbox" className="rounded border-gray-300 text-purple-600 focus:ring-purple-500" />
                <span className="ml-2 text-sm text-gray-600">Se souvenir de moi</span>
              </label>
              <Link to="/forgot-password" className="text-sm text-purple-600 hover:text-purple-700">
                Mot de passe oublié ?
              </Link>
            </div>

            <Button 
              type="submit" 
              variant="primary" 
              className="w-full"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Connexion...' : 'Se connecter'}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Pas encore de compte ?{' '}
              <Link to="/register" className="font-medium text-purple-600 hover:text-purple-700">
                S'inscrire
              </Link>
            </p>
          </div>

          <div className="mt-4 p-4 bg-purple-50 rounded-lg">
            <p className="text-xs text-purple-900 font-semibold mb-2">Comptes de test :</p>
            <p className="text-xs text-purple-700">Particulier: particulier@gmail.com / 12345678</p>
            <p className="text-xs text-purple-700">Professionnel: professionnel@gmail.com / 12345678</p>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default LoginPage;

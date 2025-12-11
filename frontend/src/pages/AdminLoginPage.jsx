import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema } from '../schemas/authSchemas';
import { useErrorHandler } from '../hooks/useErrorHandler';
import { useAuth } from '../hooks/useAuth';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';

const AdminLoginPage = () => {
  const navigate = useNavigate();
  const { showSuccess, handleError } = useErrorHandler();
  const { login, isAuthenticated, user } = useAuth();

  // Redirect if already authenticated as admin
  React.useEffect(() => {
    if (isAuthenticated && user?.role === 'ADMIN') {
      navigate('/admin/dashboard');
    }
  }, [isAuthenticated, user, navigate]);

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
      const response = await login(data);

      // Verify user is an admin
      if (response.user.role !== 'ADMIN') {
        handleError(new Error('Accès refusé. Cette page est réservée aux administrateurs.'));
        // Logout the non-admin user
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        return;
      }

      showSuccess('Connexion administrateur réussie !');
      navigate('/admin/dashboard');
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Email ou mot de passe incorrect';
      handleError(new Error(errorMessage));
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-red-50 via-orange-50 to-yellow-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <div className="bg-red-100 p-4 rounded-full w-20 h-20 flex items-center justify-center">
              <span className="text-4xl">🛡️</span>
            </div>
          </div>
          <h2 className="text-3xl font-display font-bold text-gray-900">
            Espace Administrateur
          </h2>
          <p className="mt-2 text-gray-600">
            Connexion réservée aux administrateurs Purple Dog
          </p>
        </div>

        <Card className="shadow-xl border-t-4 border-red-500">
          <div className="mb-6 p-3 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-sm text-red-800 text-center font-medium">
              ⚠️ Accès restreint
            </p>
          </div>

          <form onSubmit={handleFormSubmit(onSubmit)} className="space-y-6">
            <Input
              label="Email administrateur"
              type="email"
              {...register('email')}
              error={errors.email?.message}
              placeholder="admin@purple-dog.com"
              disabled={isSubmitting}
              className="border-red-200 focus:border-red-500 focus:ring-red-500"
            />

            <Input
              label="Mot de passe"
              type="password"
              {...register('password')}
              error={errors.password?.message}
              placeholder="••••••••"
              disabled={isSubmitting}
              className="border-red-200 focus:border-red-500 focus:ring-red-500"
            />

            <Button 
              type="submit" 
              className="w-full bg-red-600 hover:bg-red-700 focus:ring-red-500"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Connexion...' : 'Se connecter en tant qu\'admin'}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <Link 
              to="/" 
              className="text-sm text-gray-600 hover:text-gray-900 flex items-center justify-center gap-2"
            >
              ← Retour à l'accueil
            </Link>
          </div>

          <div className="mt-4 p-4 bg-gray-50 rounded-lg border border-gray-200">
            <p className="text-xs text-gray-900 font-semibold mb-3">🔑 Comptes Admin Disponibles :</p>

            {/* Super Admin */}
            <div className="mb-3 p-2 bg-purple-50 border border-purple-200 rounded">
              <p className="text-xs text-purple-900 font-semibold mb-1">👑 Super Admin</p>
              <div className="space-y-0.5 text-xs">
                <p className="text-gray-700">
                  <span className="font-medium">Email:</span> a.elwardi98@gmail.com
                </p>
                <p className="text-gray-700">
                  <span className="font-medium">Mot de passe:</span> SuperAdmin@2025
                </p>
                <p className="text-xs text-purple-600 italic mt-1">
                  Permissions complètes sur la plateforme
                </p>
              </div>
            </div>

            {/* Test Admin */}
            <div className="p-2 bg-blue-50 border border-blue-200 rounded">
              <p className="text-xs text-blue-900 font-semibold mb-1">👤 Admin Test</p>
              <div className="space-y-0.5 text-xs">
                <p className="text-gray-700">
                  <span className="font-medium">Email:</span> admin@purpledog.com
                </p>
                <p className="text-gray-700">
                  <span className="font-medium">Mot de passe:</span> Admin@123
                </p>
                <p className="text-xs text-blue-600 italic mt-1">
                  Permissions standards d'administration
                </p>
              </div>
            </div>

            <div className="mt-2 p-2 bg-yellow-50 border border-yellow-200 rounded">
              <p className="text-xs text-yellow-800">
                ⚠️ Changez les mots de passe après la première connexion
              </p>
            </div>
          </div>
        </Card>

        <div className="mt-6 text-center">
          <p className="text-sm text-gray-500">
            Vous êtes un utilisateur ?{' '}
            <Link to="/login" className="font-medium text-red-600 hover:text-red-700">
              Connexion utilisateur
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default AdminLoginPage;

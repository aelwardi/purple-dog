import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { XMarkIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import Modal from '../common/Modal';
import Input from '../common/Input';
import Button from '../common/Button';

const loginSchema = z.object({
  email: z.string().email('Email invalide'),
  password: z.string().min(8, 'Le mot de passe doit contenir au moins 8 caractères'),
});

const LoginModal = ({ isOpen, onClose, onSwitchToRegister }) => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { showSuccess, handleError } = useErrorHandler();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data) => {
    try {
      const response = await login(data);
      showSuccess('Connexion réussie !');
      reset();
      onClose();

      // Redirect based on user role
      if (response.user.role === 'INDIVIDUAL') {
        navigate('/dashboard?type=individual');
      } else if (response.user.role === 'PROFESSIONAL') {
        navigate('/dashboard?type=professional');
      } else {
        navigate('/dashboard');
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Email ou mot de passe incorrect';
      handleError(new Error(errorMessage));
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="md">
      <div className="relative">
        {/* Header */}
        <div className="flex items-center justify-between mb-4 pb-3 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">Connexion</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <XMarkIcon className="w-5 h-5" />
          </button>
        </div>

        {/* Description */}
        <p className="text-sm text-gray-600 mb-4">
          Connectez-vous à votre compte Purple Dog
        </p>

        {/* Form */}
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
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
              <input
                type="checkbox"
                className="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
              />
              <span className="ml-2 text-sm text-gray-600">Se souvenir de moi</span>
            </label>
            <button
              type="button"
              className="text-sm text-purple-600 hover:text-purple-700"
              onClick={() => {
                onClose();
                navigate('/forgot-password');
              }}
            >
              Mot de passe oublié ?
            </button>
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

        {/* Divider */}
        <div className="relative my-4">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-300"></div>
          </div>
          <div className="relative flex justify-center text-xs">
            <span className="px-2 bg-white text-gray-500">Nouveau sur Purple Dog ?</span>
          </div>
        </div>

        {/* Register Link */}
        <button
          onClick={() => {
            onClose();
            onSwitchToRegister();
          }}
          className="w-full py-2 px-4 border-2 border-purple-600 text-purple-600 rounded-lg text-sm font-medium hover:bg-purple-50 transition-colors"
        >
          Créer un compte
        </button>
      </div>
    </Modal>
  );
};

export default LoginModal;


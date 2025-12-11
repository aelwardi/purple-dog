import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { XMarkIcon } from '@heroicons/react/24/outline';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import Modal from '../common/Modal';
import Input from '../common/Input';
import Button from '../common/Button';
import authService from '../../services/authService';

const forgotPasswordSchema = z.object({
  email: z.string().email('Email invalide'),
});

const ForgotPasswordModal = ({ isOpen, onClose, onBackToLogin }) => {
  const { showSuccess, handleError } = useErrorHandler();
  const [emailSent, setEmailSent] = React.useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm({
    resolver: zodResolver(forgotPasswordSchema),
  });

  const onSubmit = async (data) => {
    try {
      await authService.forgotPassword(data.email);
      setEmailSent(true);
      showSuccess('Email de réinitialisation envoyé !');
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Une erreur est survenue';
      handleError(new Error(errorMessage));
    }
  };

  const handleClose = () => {
    reset();
    setEmailSent(false);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="md">
      <div className="relative">
        {/* Header */}
        <div className="flex items-center justify-between mb-4 pb-3 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">Mot de passe oublié</h2>
          <button
            onClick={handleClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <XMarkIcon className="w-5 h-5" />
          </button>
        </div>

        {!emailSent ? (
          <>
            {/* Description */}
            <p className="text-sm text-gray-600 mb-4">
              Entrez votre email et nous vous enverrons un lien pour réinitialiser votre mot de passe.
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

              <Button
                type="submit"
                variant="primary"
                className="w-full"
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Envoi...' : 'Envoyer le lien'}
              </Button>
            </form>

            {/* Back to login */}
            <div className="mt-4 text-center">
              <button
                onClick={() => {
                  handleClose();
                  onBackToLogin();
                }}
                className="text-sm text-purple-600 hover:text-purple-700"
              >
                Retour à la connexion
              </button>
            </div>
          </>
        ) : (
          <>
            {/* Success message */}
            <div className="bg-green-50 border border-green-200 rounded-lg p-4 mb-4">
              <div className="flex items-start">
                <svg
                  className="w-5 h-5 text-green-600 mt-0.5 mr-3"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                <div>
                  <h3 className="text-sm font-semibold text-green-900 mb-1">
                    Email envoyé !
                  </h3>
                  <p className="text-sm text-green-700">
                    Nous vous avons envoyé un email avec un lien pour réinitialiser votre mot de passe.
                    Vérifiez votre boîte de réception.
                  </p>
                </div>
              </div>
            </div>

            <p className="text-xs text-gray-500 mb-4">
              Le lien expirera dans 1 heure. Si vous ne recevez pas l'email, vérifiez vos spams.
            </p>

            <Button
              variant="primary"
              className="w-full"
              onClick={handleClose}
            >
              Fermer
            </Button>
          </>
        )}
      </div>
    </Modal>
  );
};

export default ForgotPasswordModal;


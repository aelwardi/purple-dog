import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { forgotPasswordSchema } from '../schemas/authSchemas';
import { useErrorHandler } from '../hooks/useErrorHandler';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import { EnvelopeIcon } from '@heroicons/react/24/outline';

const ForgotPasswordPage = () => {
  const { showSuccess, handleError, showInfo } = useErrorHandler();
  const [emailSent, setEmailSent] = useState(false);

  const {
    register,
    handleSubmit: handleFormSubmit,
    formState: { errors, isSubmitting },
    watch,
  } = useForm({
    resolver: zodResolver(forgotPasswordSchema),
    defaultValues: {
      email: '',
    },
  });

  const email = watch('email');

  const onSubmit = async (data) => {
    try {
      // Simulation d'un appel API
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Simuler l'envoi d'email (remplacer par authApi.forgotPassword(data.email))
      console.log('Reset email sent to:', data.email);
      
      setEmailSent(true);
      showSuccess('Un email de réinitialisation a été envoyé !');
      showInfo('Vérifiez votre boîte de réception et vos spams.');
      
    } catch (error) {
      handleError(error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-purple-100 rounded-full mb-4">
            <EnvelopeIcon className="w-8 h-8 text-purple-600" />
          </div>
          <h2 className="text-3xl font-display font-bold text-gray-900">
            Mot de passe oublié ?
          </h2>
          <p className="mt-2 text-gray-600">
            Pas de problème ! Entrez votre email et nous vous enverrons un lien de réinitialisation.
          </p>
        </div>

        <Card>
          {!emailSent ? (
            <>
              <form onSubmit={handleFormSubmit(onSubmit)} className="space-y-6">
                <Input
                  label="Adresse email"
                  type="email"
                  {...register('email')}
                  error={errors.email?.message}
                  placeholder="votre@email.com"
                  disabled={isSubmitting}
                  autoFocus
                />

                <Button 
                  type="submit" 
                  variant="primary" 
                  className="w-full"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Envoi en cours...' : 'Envoyer le lien de réinitialisation'}
                </Button>
              </form>

              <div className="mt-6 text-center">
                <Link 
                  to="/login" 
                  className="text-sm text-purple-600 hover:text-purple-700 font-medium inline-flex items-center gap-1"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                  </svg>
                  Retour à la connexion
                </Link>
              </div>

              <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                <p className="text-xs text-blue-900 font-medium mb-1">💡 Conseil :</p>
                <p className="text-xs text-blue-700">
                  Si vous ne recevez pas l'email dans les 5 minutes, vérifiez vos spams ou réessayez.
                </p>
              </div>
            </>
          ) : (
            // État après envoi d'email
            <div className="text-center py-4">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
                <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
              </div>
              
              <h3 className="text-xl font-bold text-gray-900 mb-2">
                Email envoyé !
              </h3>
              
              <p className="text-gray-600 mb-4">
                Nous avons envoyé un lien de réinitialisation à :
              </p>
              
              <p className="font-medium text-purple-600 mb-6">
                {email}
              </p>

              <div className="space-y-3">
                <Button 
                  variant="primary" 
                  className="w-full"
                  onClick={() => window.location.href = 'mailto:'}
                >
                  Ouvrir ma boîte email
                </Button>

                <Button 
                  variant="outline" 
                  className="w-full"
                  onClick={() => setEmailSent(false)}
                >
                  Renvoyer l'email
                </Button>

                <Link to="/login" className="block">
                  <Button variant="ghost" className="w-full">
                    Retour à la connexion
                  </Button>
                </Link>
              </div>

              <div className="mt-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                <p className="text-xs text-yellow-900 font-medium mb-1">⏱️ N'oubliez pas :</p>
                <p className="text-xs text-yellow-700">
                  Le lien de réinitialisation expire dans 1 heure pour des raisons de sécurité.
                </p>
              </div>
            </div>
          )}
        </Card>

        {/* Aide supplémentaire */}
        <div className="mt-6 text-center">
          <p className="text-sm text-gray-600">
            Besoin d'aide ?{' '}
            <Link to="/contact" className="text-purple-600 hover:text-purple-700 font-medium">
              Contactez le support
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { XMarkIcon, CheckIcon, UserCircleIcon, BuildingOfficeIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import Modal from '../common/Modal';
import Input from '../common/Input';
import Button from '../common/Button';

// Validation schemas
const step1Schema = z.object({
  accountType: z.enum(['INDIVIDUAL', 'PROFESSIONAL'], {
    required_error: 'Veuillez sélectionner un type de compte',
  }),
});

const step2SchemaIndividual = z.object({
  firstName: z.string().min(2, 'Le prénom doit contenir au moins 2 caractères'),
  lastName: z.string().min(2, 'Le nom doit contenir au moins 2 caractères'),
  email: z.string().email('Email invalide'),
  phone: z.string().min(10, 'Numéro de téléphone invalide'),
});

const step2SchemaProfessional = z.object({
  firstName: z.string().min(2, 'Le prénom doit contenir au moins 2 caractères'),
  lastName: z.string().min(2, 'Le nom doit contenir au moins 2 caractères'),
  email: z.string().email('Email invalide'),
  phone: z.string().min(10, 'Numéro de téléphone invalide'),
  companyName: z.string().min(2, 'Le nom de l\'entreprise est requis'),
  siret: z.string().min(14, 'Le SIRET doit contenir 14 chiffres'),
});

const step3Schema = z.object({
  password: z.string().min(8, 'Le mot de passe doit contenir au moins 8 caractères'),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Les mots de passe ne correspondent pas',
  path: ['confirmPassword'],
});

const RegisterModal = ({ isOpen, onClose, onSwitchToLogin }) => {
  const navigate = useNavigate();
  const { registerIndividual, registerProfessional } = useAuth();
  const { showSuccess, handleError } = useErrorHandler();

  const [currentStep, setCurrentStep] = useState(1);
  const [accountType, setAccountType] = useState(null);
  const [formData, setFormData] = useState({});

  const steps = [
    { number: 1, title: 'Type de compte' },
    { number: 2, title: 'Informations' },
    { number: 3, title: 'Mot de passe' },
  ];

  // Step 1 - Account Type
  const Step1 = () => {
    return (
      <div className="space-y-3">
        <h3 className="text-base font-semibold text-gray-900 mb-3">
          Choisissez votre type de compte
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          {/* Individual Account */}
          <button
            onClick={() => {
              setAccountType('INDIVIDUAL');
              setCurrentStep(2);
            }}
            className={`p-4 border-2 rounded-lg transition-all hover:border-purple-500 hover:shadow-md ${
              accountType === 'INDIVIDUAL' ? 'border-purple-500 bg-purple-50' : 'border-gray-200'
            }`}
          >
            <div className="flex flex-col items-center text-center">
              <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mb-3">
                <UserCircleIcon className="w-6 h-6 text-purple-600" />
              </div>
              <h4 className="text-base font-semibold text-gray-900 mb-1">
                Particulier
              </h4>
              <p className="text-xs text-gray-600 mb-2">
                Je souhaite vendre mes objets de valeur
              </p>
              <ul className="text-xs text-gray-500 space-y-1">
                <li>✓ Inscription gratuite</li>
                <li>✓ Vente aux professionnels</li>
                <li>✓ Identité anonymisée</li>
              </ul>
            </div>
          </button>

          {/* Professional Account */}
          <button
            onClick={() => {
              setAccountType('PROFESSIONAL');
              setCurrentStep(2);
            }}
            className={`p-4 border-2 rounded-lg transition-all hover:border-purple-500 hover:shadow-md ${
              accountType === 'PROFESSIONAL' ? 'border-purple-500 bg-purple-50' : 'border-gray-200'
            }`}
          >
            <div className="flex flex-col items-center text-center">
              <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mb-3">
                <BuildingOfficeIcon className="w-6 h-6 text-purple-600" />
              </div>
              <h4 className="text-base font-semibold text-gray-900 mb-1">
                Professionnel
              </h4>
              <p className="text-xs text-gray-600 mb-2">
                Je souhaite acheter et vendre des objets
              </p>
              <ul className="text-xs text-gray-500 space-y-1">
                <li>✓ 1 mois gratuit puis 49€/mois</li>
                <li>✓ Accès aux objets exclusifs</li>
                <li>✓ Outils professionnels</li>
              </ul>
            </div>
          </button>
        </div>
      </div>
    );
  };

  // Step 2 - Personal Information
  const Step2 = () => {
    const schema = accountType === 'INDIVIDUAL' ? step2SchemaIndividual : step2SchemaProfessional;

    const {
      register,
      handleSubmit,
      formState: { errors },
    } = useForm({
      resolver: zodResolver(schema),
      defaultValues: formData,
    });

    const onSubmit = (data) => {
      setFormData({ ...formData, ...data });
      setCurrentStep(3);
    };

    return (
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <h3 className="text-base font-semibold text-gray-900 mb-3">
          Informations personnelles
        </h3>

        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Prénom"
            {...register('firstName')}
            error={errors.firstName?.message}
            placeholder="John"
          />
          <Input
            label="Nom"
            {...register('lastName')}
            error={errors.lastName?.message}
            placeholder="Doe"
          />
        </div>

        <Input
          label="Email"
          type="email"
          {...register('email')}
          error={errors.email?.message}
          placeholder="john.doe@example.com"
        />

        <Input
          label="Téléphone"
          {...register('phone')}
          error={errors.phone?.message}
          placeholder="+33 6 12 34 56 78"
        />

        {accountType === 'PROFESSIONAL' && (
          <>
            <Input
              label="Nom de l'entreprise"
              {...register('companyName')}
              error={errors.companyName?.message}
              placeholder="Purple Dog SARL"
            />
            <Input
              label="SIRET"
              {...register('siret')}
              error={errors.siret?.message}
              placeholder="12345678901234"
            />
          </>
        )}

        <div className="flex gap-3 pt-2">
          <Button
            type="button"
            variant="outline"
            onClick={() => setCurrentStep(1)}
            className="flex-1"
          >
            Retour
          </Button>
          <Button type="submit" variant="primary" className="flex-1">
            Continuer
          </Button>
        </div>
      </form>
    );
  };

  // Step 3 - Password
  const Step3 = () => {
    const {
      register,
      handleSubmit,
      formState: { errors, isSubmitting },
    } = useForm({
      resolver: zodResolver(step3Schema),
    });

    const onSubmit = async (data) => {
      try {
        const finalData = {
          ...formData,
          password: data.password,
          bio: null,
          profilePicture: null,
        };

        if (accountType === 'INDIVIDUAL') {
          await registerIndividual(finalData);
        } else {
          await registerProfessional(finalData);
        }

        showSuccess('Inscription réussie ! Bienvenue sur Purple Dog 🎉');
        onClose();
        navigate(`/dashboard?type=${accountType === 'INDIVIDUAL' ? 'individual' : 'professional'}`);
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Une erreur est survenue';
        handleError(new Error(errorMessage));
      }
    };

    return (
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <h3 className="text-base font-semibold text-gray-900 mb-3">
          Sécurisez votre compte
        </h3>

        <Input
          label="Mot de passe"
          type="password"
          {...register('password')}
          error={errors.password?.message}
          placeholder="••••••••"
        />

        <Input
          label="Confirmer le mot de passe"
          type="password"
          {...register('confirmPassword')}
          error={errors.confirmPassword?.message}
          placeholder="••••••••"
        />

        <div className="bg-gray-50 p-3 rounded-lg">
          <p className="text-xs text-gray-600 mb-1">Le mot de passe doit contenir :</p>
          <ul className="text-xs text-gray-500 space-y-0.5">
            <li>✓ Au moins 8 caractères</li>
            <li>✓ Une majuscule et une minuscule</li>
            <li>✓ Un chiffre</li>
          </ul>
        </div>

        <div className="flex gap-3 pt-2">
          <Button
            type="button"
            variant="outline"
            onClick={() => setCurrentStep(2)}
            className="flex-1"
          >
            Retour
          </Button>
          <Button
            type="submit"
            variant="primary"
            className="flex-1"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Inscription...' : 'Créer mon compte'}
          </Button>
        </div>
      </form>
    );
  };

  const handleClose = () => {
    setCurrentStep(1);
    setAccountType(null);
    setFormData({});
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="lg">
      <div className="relative">
        {/* Header */}
        <div className="flex items-center justify-between mb-4 pb-3 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">Créer un compte</h2>
          <button
            onClick={handleClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <XMarkIcon className="w-5 h-5" />
          </button>
        </div>

        {/* Step Indicator */}
        <div className="mb-4">
          <div className="flex items-center justify-between">
            {steps.map((step, index) => (
              <React.Fragment key={step.number}>
                <div className="flex flex-col items-center flex-1">
                  <div
                    className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold transition-colors ${
                      currentStep > step.number
                        ? 'bg-green-500 text-white'
                        : currentStep === step.number
                        ? 'bg-purple-600 text-white'
                        : 'bg-gray-200 text-gray-500'
                    }`}
                  >
                    {currentStep > step.number ? (
                      <CheckIcon className="w-4 h-4" />
                    ) : (
                      step.number
                    )}
                  </div>
                  <span
                    className={`text-xs mt-1 font-medium ${
                      currentStep >= step.number ? 'text-gray-900' : 'text-gray-400'
                    }`}
                  >
                    {step.title}
                  </span>
                </div>
                {index < steps.length - 1 && (
                  <div
                    className={`flex-1 h-0.5 mx-2 mb-5 rounded transition-colors ${
                      currentStep > step.number ? 'bg-green-500' : 'bg-gray-200'
                    }`}
                  />
                )}
              </React.Fragment>
            ))}
          </div>
        </div>

        {/* Step Content */}
        <div className="mb-4">
          {currentStep === 1 && <Step1 />}
          {currentStep === 2 && <Step2 />}
          {currentStep === 3 && <Step3 />}
        </div>

        {/* Footer */}
        {currentStep === 1 && (
          <>
            <div className="relative my-4">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300"></div>
              </div>
              <div className="relative flex justify-center text-xs">
                <span className="px-2 bg-white text-gray-500">Déjà inscrit ?</span>
              </div>
            </div>

            <button
              onClick={() => {
                handleClose();
                onSwitchToLogin();
              }}
              className="w-full py-2 px-4 border-2 border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition-colors"
            >
              Se connecter
            </button>
          </>
        )}
      </div>
    </Modal>
  );
};

export default RegisterModal;


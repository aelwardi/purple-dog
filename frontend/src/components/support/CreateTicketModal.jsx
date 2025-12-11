import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { XMarkIcon, CheckCircleIcon } from '@heroicons/react/24/outline';
import Modal from '../common/Modal';
import Input from '../common/Input';
import Button from '../common/Button';

const ticketSchema = z.object({
  subject: z.string().min(5, 'Le sujet doit contenir au moins 5 caractères').max(200),
  description: z.string().min(10, 'La description doit contenir au moins 10 caractères'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH']),
  category: z.string().optional(),
});

const CreateTicketModal = ({ isOpen, onClose, onSuccess }) => {
  const [currentStep, setCurrentStep] = useState(1);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    trigger,
  } = useForm({
    resolver: zodResolver(ticketSchema),
    defaultValues: {
      priority: 'MEDIUM',
      category: '',
    },
  });

  const steps = [
    { number: 1, title: 'Sujet', description: 'De quoi s\'agit-il ?' },
    { number: 2, title: 'Détails', description: 'Plus d\'informations' },
    { number: 3, title: 'Description', description: 'Décrivez le problème' },
  ];

  const handleClose = () => {
    setCurrentStep(1);
    reset();
    onClose();
  };

  const handleNext = async () => {
    let fieldsToValidate = [];
    if (currentStep === 1) fieldsToValidate = ['subject'];
    if (currentStep === 2) fieldsToValidate = ['priority', 'category'];

    const isValid = await trigger(fieldsToValidate);
    if (isValid) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handlePrevious = () => {
    setCurrentStep(currentStep - 1);
  };

  const onSubmit = async (data) => {
    await onSuccess(data);
    handleClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="md">
      <div className="p-6">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-900">Nouveau Ticket</h2>
          <button
            onClick={handleClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <XMarkIcon className="w-6 h-6" />
          </button>
        </div>

        {/* Steps Indicator */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            {steps.map((step, index) => (
              <React.Fragment key={step.number}>
                <div className="flex flex-col items-center flex-1">
                  <div
                    className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-semibold transition-all ${
                      currentStep >= step.number
                        ? 'bg-purple-600 text-white'
                        : 'bg-gray-200 text-gray-500'
                    }`}
                  >
                    {currentStep > step.number ? (
                      <CheckCircleIcon className="w-6 h-6" />
                    ) : (
                      step.number
                    )}
                  </div>
                  <div className="mt-2 text-center">
                    <p className={`text-xs font-medium ${currentStep >= step.number ? 'text-purple-600' : 'text-gray-500'}`}>
                      {step.title}
                    </p>
                    <p className="text-xs text-gray-500">{step.description}</p>
                  </div>
                </div>
                {index < steps.length - 1 && (
                  <div
                    className={`flex-1 h-0.5 mx-4 mt-[-40px] transition-all ${
                      currentStep > step.number ? 'bg-purple-600' : 'bg-gray-200'
                    }`}
                  />
                )}
              </React.Fragment>
            ))}
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit(onSubmit)}>
          {/* Step 1: Subject */}
          {currentStep === 1 && (
            <div className="space-y-4 min-h-[200px]">
              <Input
                label="Sujet du ticket"
                required
                {...register('subject')}
                error={errors.subject?.message}
                placeholder="Ex: Problème de connexion"
                autoFocus
              />
              <p className="text-sm text-gray-600">
                Donnez un titre clair et concis à votre demande
              </p>
            </div>
          )}

          {/* Step 2: Priority & Category */}
          {currentStep === 2 && (
            <div className="space-y-4 min-h-[200px]">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Catégorie
                </label>
                <select
                  {...register('category')}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                >
                  <option value="">Sélectionner une catégorie</option>
                  <option value="TECHNIQUE">Problème technique</option>
                  <option value="COMPTE">Mon compte</option>
                  <option value="PRODUIT">Question sur un produit</option>
                  <option value="PAIEMENT">Paiement</option>
                  <option value="LIVRAISON">Livraison</option>
                  <option value="AUTRE">Autre</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Priorité <span className="text-red-500">*</span>
                </label>
                <select
                  {...register('priority')}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                >
                  <option value="LOW">🟢 Basse - Question générale</option>
                  <option value="MEDIUM">🟡 Moyenne - Problème normal</option>
                  <option value="HIGH">🔴 Haute - Problème urgent</option>
                </select>
              </div>
            </div>
          )}

          {/* Step 3: Description */}
          {currentStep === 3 && (
            <div className="space-y-4 min-h-[200px]">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description <span className="text-red-500">*</span>
                </label>
                <textarea
                  {...register('description')}
                  rows={6}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Décrivez votre problème en détail..."
                />
                {errors.description && (
                  <p className="text-sm text-red-600 mt-1">{errors.description.message}</p>
                )}
              </div>
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
                <p className="text-xs text-blue-900 font-medium mb-1">💡 Conseils</p>
                <ul className="text-xs text-blue-700 space-y-1">
                  <li>• Soyez précis et détaillé</li>
                  <li>• Incluez les étapes pour reproduire le problème</li>
                  <li>• Notre équipe répondra sous 24-48h</li>
                </ul>
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="flex gap-3 mt-6 pt-6 border-t border-gray-200">
            {currentStep > 1 && (
              <Button
                type="button"
                variant="outline"
                onClick={handlePrevious}
                className="flex-1"
              >
                Précédent
              </Button>
            )}
            {currentStep < 3 ? (
              <Button
                type="button"
                variant="primary"
                onClick={handleNext}
                className="flex-1"
              >
                Suivant
              </Button>
            ) : (
              <Button
                type="submit"
                variant="primary"
                className="flex-1"
              >
                Créer le ticket
              </Button>
            )}
          </div>
        </form>
      </div>
    </Modal>
  );
};

export default CreateTicketModal;


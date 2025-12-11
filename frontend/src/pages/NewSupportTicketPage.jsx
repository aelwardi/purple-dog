import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { TicketIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import { useErrorHandler } from '../hooks/useErrorHandler';
import supportTicketService from '../services/supportTicketService';
import Input from '../components/common/Input';
import Button from '../components/common/Button';

const ticketSchema = z.object({
  subject: z.string().min(5, 'Le sujet doit contenir au moins 5 caractères').max(200, 'Le sujet ne peut pas dépasser 200 caractères'),
  description: z.string().min(10, 'La description doit contenir au moins 10 caractères'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH']),
  category: z.string().optional(),
});

const NewSupportTicketPage = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showSuccess, showError } = useErrorHandler();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(ticketSchema),
    defaultValues: {
      priority: 'MEDIUM',
      category: '',
    },
  });

  const onSubmit = async (data) => {
    if (!user?.id) {
      showError('Utilisateur non connecté');
      return;
    }

    setIsSubmitting(true);
    try {
      const ticket = await supportTicketService.createTicket(user.id, data);
      showSuccess('Ticket créé avec succès !');
      navigate(`/support/tickets/${ticket.ticketNumber}`);
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Erreur lors de la création du ticket';
      showError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top Bar */}
      <div className="bg-white border-b border-gray-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-4">
              <button
                onClick={() => navigate('/support')}
                className="text-gray-600 hover:text-purple-600 transition-colors"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
              </button>
              <h1 className="text-xl font-semibold text-gray-900 flex items-center gap-2">
                <TicketIcon className="w-6 h-6 text-purple-600" />
                Nouveau Ticket de Support
              </h1>
            </div>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="mb-6">
            <h2 className="text-lg font-semibold text-gray-900">Créer un ticket de support</h2>
            <p className="text-sm text-gray-600 mt-1">
              Décrivez votre problème ou votre question et notre équipe vous répondra dans les plus brefs délais.
            </p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            {/* Subject */}
            <Input
              label="Sujet"
              required
              {...register('subject')}
              error={errors.subject?.message}
              placeholder="Ex: Problème de connexion, question sur un produit..."
              disabled={isSubmitting}
            />

            {/* Category */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Catégorie
              </label>
              <select
                {...register('category')}
                className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                disabled={isSubmitting}
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

            {/* Priority */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Priorité <span className="text-red-500">*</span>
              </label>
              <select
                {...register('priority')}
                className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                disabled={isSubmitting}
              >
                <option value="LOW">Basse - Question générale</option>
                <option value="MEDIUM">Moyenne - Problème normal</option>
                <option value="HIGH">Haute - Problème urgent</option>
              </select>
              {errors.priority && (
                <p className="text-sm text-red-600 mt-1">{errors.priority.message}</p>
              )}
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description <span className="text-red-500">*</span>
              </label>
              <textarea
                {...register('description')}
                rows={6}
                className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Décrivez votre problème ou votre question en détail..."
                disabled={isSubmitting}
              />
              {errors.description && (
                <p className="text-sm text-red-600 mt-1">{errors.description.message}</p>
              )}
              <p className="text-xs text-gray-500 mt-1">
                Plus vous donnez de détails, plus nous pourrons vous aider rapidement.
              </p>
            </div>

            {/* Info box */}
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <div className="flex gap-3">
                <svg className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                </svg>
                <div className="flex-1">
                  <p className="text-sm font-medium text-blue-900 mb-1">
                    À savoir
                  </p>
                  <ul className="text-sm text-blue-700 space-y-1 list-disc list-inside">
                    <li>Notre équipe vous répondra dans les 24-48h</li>
                    <li>Vous recevrez un email avec le numéro de votre ticket</li>
                    <li>Vous pourrez suivre l'évolution de votre ticket</li>
                  </ul>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="flex gap-3 pt-4 border-t border-gray-200">
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate('/support')}
                disabled={isSubmitting}
                className="flex-1"
              >
                Annuler
              </Button>
              <Button
                type="submit"
                variant="primary"
                disabled={isSubmitting}
                className="flex-1"
              >
                {isSubmitting ? 'Création...' : 'Créer le ticket'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default NewSupportTicketPage;


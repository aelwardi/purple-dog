import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import {
  PaperAirplaneIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import { useErrorHandler } from '../hooks/useErrorHandler';
import supportTicketService from '../services/supportTicketService';
import Button from '../components/common/Button';
import ConfirmModal from '../components/common/ConfirmModal';

const TicketDetailPage = () => {
  const { ticketNumber } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showSuccess, showError, handleError } = useErrorHandler();

  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [sendingMessage, setSendingMessage] = useState(false);
  const [showCloseConfirm, setShowCloseConfirm] = useState(false);
  const messagesEndRef = useRef(null);

  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  // Reusable load ticket function
  const loadTicket = useCallback(async () => {
    try {
      setLoading(true);
      const data = await supportTicketService.getTicketByNumber(ticketNumber);
      setTicket(data);
    } catch (error) {
      handleError(error);
      navigate('/support');
    } finally {
      setLoading(false);
    }
  }, [ticketNumber, handleError, navigate]);

  // Load ticket only once or when ticketNumber changes
  useEffect(() => {
    loadTicket();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ticketNumber]); // Only reload when ticketNumber changes

  // Scroll to bottom when messages change
  useEffect(() => {
    scrollToBottom();
  }, [ticket?.ticketMessages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const onSubmitMessage = async (data) => {
    if (!data.message.trim()) return;

    setSendingMessage(true);
    try {
      await supportTicketService.addMessage(ticketNumber, data.message);
      reset();
      await loadTicket(); // Reload to get new message
      showSuccess('Message envoyé');
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Erreur lors de l\'envoi du message';
      showError(errorMessage);
    } finally {
      setSendingMessage(false);
    }
  };

  const handleCloseTicket = async () => {
    try {
      await supportTicketService.closeTicket(ticketNumber);
      showSuccess('Ticket fermé');
      setShowCloseConfirm(false);
      await loadTicket();
    } catch (error) {
      handleError(error);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'OPEN': return 'bg-blue-100 text-blue-800';
      case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-800';
      case 'RESOLVED': return 'bg-green-100 text-green-800';
      case 'CLOSED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'OPEN': return 'Ouvert';
      case 'IN_PROGRESS': return 'En cours';
      case 'RESOLVED': return 'Résolu';
      case 'CLOSED': return 'Fermé';
      default: return status;
    }
  };

  const getPriorityLabel = (priority) => {
    switch (priority) {
      case 'HIGH': return 'Haute';
      case 'MEDIUM': return 'Moyenne';
      case 'LOW': return 'Basse';
      default: return priority;
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  if (!ticket) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-600">Ticket non trouvé</p>
          <Button onClick={() => navigate('/support')} className="mt-4">
            Retour aux tickets
          </Button>
        </div>
      </div>
    );
  }

  const canSendMessage = ticket.status !== 'CLOSED';

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
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
              <div>
                <div className="flex items-center gap-3">
                  <h1 className="text-lg font-semibold text-gray-900">
                    Ticket #{ticket.ticketNumber}
                  </h1>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(ticket.status)}`}>
                    {getStatusLabel(ticket.status)}
                  </span>
                </div>
                <p className="text-sm text-gray-600">{ticket.subject}</p>
              </div>
            </div>

            {canSendMessage && (
              <Button
                onClick={() => setShowCloseConfirm(true)}
                variant="outline"
                className="border-red-600 text-red-600 hover:bg-red-50"
              >
                <XMarkIcon className="w-5 h-5 mr-2" />
                Fermer le ticket
              </Button>
            )}
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 flex flex-col">
        <div className="flex-1 grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Messages */}
          <div className="lg:col-span-2 flex flex-col bg-white rounded-lg shadow-sm border border-gray-200">
            {/* Messages List */}
            <div className="flex-1 overflow-y-auto p-6 space-y-4">
              {/* Initial ticket description */}
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <div className="flex items-start gap-3">
                  <div className="w-8 h-8 bg-purple-600 rounded-full flex items-center justify-center text-white font-semibold text-sm flex-shrink-0">
                    {user?.firstName?.[0]}{user?.lastName?.[0]}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="font-medium text-gray-900">Vous</span>
                      <span className="text-xs text-gray-500">
                        {new Date(ticket.createdAt).toLocaleString('fr-FR')}
                      </span>
                    </div>
                    <p className="text-sm text-gray-700 whitespace-pre-wrap">{ticket.description}</p>
                  </div>
                </div>
              </div>

              {/* Messages */}
              {ticket.ticketMessages && ticket.ticketMessages.length > 0 ? (
                ticket.ticketMessages.map((message) => (
                  <div
                    key={message.id}
                    className={`flex items-start gap-3 ${
                      message.isAdminResponse ? '' : 'flex-row-reverse'
                    }`}
                  >
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center text-white font-semibold text-sm flex-shrink-0 ${
                      message.isAdminResponse ? 'bg-green-600' : 'bg-purple-600'
                    }`}>
                      {message.isAdminResponse ? 'S' : user?.firstName?.[0]}
                    </div>
                    <div className={`flex-1 min-w-0 ${message.isAdminResponse ? '' : 'text-right'}`}>
                      <div className="flex items-center gap-2 mb-1">
                        <span className="font-medium text-gray-900">
                          {message.isAdminResponse ? 'Support' : 'Vous'}
                        </span>
                        <span className="text-xs text-gray-500">
                          {new Date(message.createdAt).toLocaleString('fr-FR')}
                        </span>
                      </div>
                      <div className={`inline-block px-4 py-2 rounded-lg ${
                        message.isAdminResponse
                          ? 'bg-gray-100 text-gray-900'
                          : 'bg-purple-600 text-white'
                      }`}>
                        <p className="text-sm whitespace-pre-wrap">{message.message}</p>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <p className="text-sm">En attente de réponse du support...</p>
                </div>
              )}

              <div ref={messagesEndRef} />
            </div>

            {/* Message Input */}
            {canSendMessage ? (
              <form onSubmit={handleSubmit(onSubmitMessage)} className="border-t border-gray-200 p-4">
                <div className="flex gap-2">
                  <textarea
                    {...register('message', { required: 'Le message est requis' })}
                    rows={2}
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
                    placeholder="Écrire un message..."
                    disabled={sendingMessage}
                  />
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={sendingMessage}
                    className="self-end"
                  >
                    <PaperAirplaneIcon className="w-5 h-5" />
                  </Button>
                </div>
                {errors.message && (
                  <p className="text-sm text-red-600 mt-1">{errors.message.message}</p>
                )}
              </form>
            ) : (
              <div className="border-t border-gray-200 p-4 bg-gray-50 text-center">
                <p className="text-sm text-gray-600">
                  Ce ticket est fermé. Vous ne pouvez plus envoyer de messages.
                </p>
              </div>
            )}
          </div>

          {/* Sidebar Info */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-5">
              <h3 className="text-base font-semibold text-gray-900 mb-4">Informations</h3>

              <div className="space-y-3">
                <div>
                  <p className="text-xs text-gray-500 mb-1">Statut</p>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(ticket.status)}`}>
                    {getStatusLabel(ticket.status)}
                  </span>
                </div>

                <div>
                  <p className="text-xs text-gray-500 mb-1">Priorité</p>
                  <p className="text-sm font-medium text-gray-900">{getPriorityLabel(ticket.priority)}</p>
                </div>

                {ticket.category && (
                  <div>
                    <p className="text-xs text-gray-500 mb-1">Catégorie</p>
                    <p className="text-sm font-medium text-gray-900">{ticket.category}</p>
                  </div>
                )}

                <div>
                  <p className="text-xs text-gray-500 mb-1">Créé le</p>
                  <p className="text-sm text-gray-900">
                    {new Date(ticket.createdAt).toLocaleString('fr-FR')}
                  </p>
                </div>

                {ticket.updatedAt && (
                  <div>
                    <p className="text-xs text-gray-500 mb-1">Dernière mise à jour</p>
                    <p className="text-sm text-gray-900">
                      {new Date(ticket.updatedAt).toLocaleString('fr-FR')}
                    </p>
                  </div>
                )}

                {ticket.assignedAdminName && (
                  <div>
                    <p className="text-xs text-gray-500 mb-1">Assigné à</p>
                    <p className="text-sm font-medium text-gray-900">{ticket.assignedAdminName}</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Close Confirmation Modal */}
      <ConfirmModal
        isOpen={showCloseConfirm}
        onClose={() => setShowCloseConfirm(false)}
        onConfirm={handleCloseTicket}
        title="Fermer le ticket"
        message="Êtes-vous sûr de vouloir fermer ce ticket ? Vous ne pourrez plus envoyer de messages."
        confirmText="Oui, fermer"
        variant="warning"
      />
    </div>
  );
};

export default TicketDetailPage;


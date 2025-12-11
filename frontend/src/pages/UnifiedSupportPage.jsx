import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  TicketIcon,
  PlusIcon,
  MagnifyingGlassIcon,
  PaperAirplaneIcon,
  XMarkIcon,
  FunnelIcon,
} from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import { useErrorHandler } from '../hooks/useErrorHandler';
import supportTicketService from '../services/supportTicketService';
import Button from '../components/common/Button';
import Header from '../components/common/Header';
import CreateTicketModal from '../components/support/CreateTicketModal';
import ConfirmModal from '../components/common/ConfirmModal';

const UnifiedSupportPage = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showSuccess, showError, handleError } = useErrorHandler();

  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [filter, setFilter] = useState('all');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showCloseConfirm, setShowCloseConfirm] = useState(false);

  // Message states
  const [messageText, setMessageText] = useState('');
  const [sendingMessage, setSendingMessage] = useState(false);
  const [optimisticMessages, setOptimisticMessages] = useState([]);

  const messagesEndRef = useRef(null);

  // Load tickets
  useEffect(() => {
    if (!user?.id) return;

    const loadTickets = async () => {
      try {
        setLoading(true);
        const data = await supportTicketService.getMyTickets(user.id);
        setTickets(data);

        // Select first ticket if none selected
        if (!selectedTicket && data.length > 0) {
          setSelectedTicket(data[0]);
        }
      } catch (error) {
        handleError(error);
      } finally {
        setLoading(false);
      }
    };

    loadTickets();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id]);

  // Scroll to bottom when messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [selectedTicket?.ticketMessages, optimisticMessages]);

  // Filter tickets
  const filteredTickets = tickets.filter(ticket => {
    const matchesFilter = filter === 'all' || ticket.status.toLowerCase() === filter.toLowerCase();
    const matchesSearch =
      ticket.subject.toLowerCase().includes(searchQuery.toLowerCase()) ||
      ticket.ticketNumber.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesFilter && matchesSearch;
  });

  // Create ticket
  const handleCreateTicket = async (data) => {
    try {
      const newTicket = await supportTicketService.createTicket(user.id, data);
      setTickets([newTicket, ...tickets]);
      setSelectedTicket(newTicket);
      showSuccess('Ticket créé avec succès !');
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Erreur lors de la création du ticket';
      showError(errorMessage);
    }
  };

  // Send message with optimistic update
  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!messageText.trim() || !selectedTicket) return;

    const tempMessage = {
      id: Date.now(),
      message: messageText,
      isAdminResponse: false,
      createdAt: new Date().toISOString(),
      isOptimistic: true,
    };

    // Add optimistic message
    setOptimisticMessages([...optimisticMessages, tempMessage]);
    setMessageText('');
    setSendingMessage(true);

    try {
      // Send to backend
      await supportTicketService.addMessage(selectedTicket.id, messageText);

      // Reload ticket to get actual message
      const updatedTicket = await supportTicketService.getTicketByNumber(selectedTicket.ticketNumber);

      // Update in list
      setTickets(tickets.map(t =>
        t.id === updatedTicket.id ? updatedTicket : t
      ));

      // Update selected
      setSelectedTicket(updatedTicket);

      // Clear optimistic messages
      setOptimisticMessages([]);

    } catch {
      // Remove optimistic message on error
      setOptimisticMessages(optimisticMessages.filter(m => m.id !== tempMessage.id));
      showError('Erreur lors de l\'envoi du message');
    } finally {
      setSendingMessage(false);
    }
  };

  // Close ticket
  const handleCloseTicket = async () => {
    try {
      await supportTicketService.closeTicket(selectedTicket.id);

      // Reload ticket
      const updatedTicket = await supportTicketService.getTicketByNumber(selectedTicket.ticketNumber);

      // Update in list
      setTickets(tickets.map(t =>
        t.id === updatedTicket.id ? updatedTicket : t
      ));

      // Update selected
      setSelectedTicket(updatedTicket);

      showSuccess('Ticket fermé');
      setShowCloseConfirm(false);
    } catch (error) {
      handleError(error);
    }
  };

  // Status helpers
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

  const allMessages = selectedTicket?.ticketMessages
    ? [...selectedTicket.ticketMessages, ...optimisticMessages]
    : optimisticMessages;

  const canSendMessage = selectedTicket?.status !== 'CLOSED';

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Header */}
      <Header />

      {/* Main Content */}
      <div className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {/* Page Title and Action */}
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-3">
            <TicketIcon className="w-8 h-8 text-purple-600" />
            <h1 className="text-2xl font-bold text-gray-900">Support</h1>
          </div>
          <Button
            onClick={() => setShowCreateModal(true)}
            variant="primary"
            className="flex items-center gap-2"
          >
            <PlusIcon className="w-5 h-5" />
            <span className="hidden sm:inline">Nouveau Ticket</span>
          </Button>
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
          {/* Tickets List */}
          <div className="lg:col-span-1 bg-white rounded-lg shadow-sm border border-gray-200 flex flex-col max-h-[calc(100vh-180px)]">
            {/* Search & Filter */}
            <div className="p-4 border-b border-gray-200 space-y-3">
              <div className="relative">
                <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="Rechercher..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent text-sm"
                />
              </div>

              <div className="flex items-center gap-2">
                <FunnelIcon className="w-4 h-4 text-gray-400" />
                <select
                  value={filter}
                  onChange={(e) => setFilter(e.target.value)}
                  className="flex-1 px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
                >
                  <option value="all">Tous</option>
                  <option value="open">Ouverts</option>
                  <option value="in_progress">En cours</option>
                  <option value="resolved">Résolus</option>
                  <option value="closed">Fermés</option>
                </select>
              </div>
            </div>

            {/* Tickets */}
            <div className="flex-1 overflow-y-auto">
              {loading ? (
                <div className="flex items-center justify-center py-12">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600"></div>
                </div>
              ) : filteredTickets.length === 0 ? (
                <div className="text-center py-12 px-4">
                  <TicketIcon className="w-12 h-12 text-gray-300 mx-auto mb-3" />
                  <p className="text-sm text-gray-600">Aucun ticket trouvé</p>
                </div>
              ) : (
                <div className="divide-y divide-gray-200">
                  {filteredTickets.map((ticket) => (
                    <button
                      key={ticket.id}
                      onClick={() => {
                        setSelectedTicket(ticket);
                        setOptimisticMessages([]);
                      }}
                      className={`w-full text-left p-4 hover:bg-gray-50 transition-colors ${
                        selectedTicket?.id === ticket.id ? 'bg-purple-50 border-l-4 border-purple-600' : ''
                      }`}
                    >
                      <div className="flex items-start justify-between gap-2 mb-2">
                        <h3 className="font-medium text-gray-900 text-sm line-clamp-1">
                          {ticket.subject}
                        </h3>
                        <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium flex-shrink-0 ${getStatusColor(ticket.status)}`}>
                          {getStatusLabel(ticket.status)}
                        </span>
                      </div>
                      <p className="text-xs text-gray-500 mb-1">#{ticket.ticketNumber}</p>
                      <p className="text-xs text-gray-600 line-clamp-2">{ticket.description}</p>
                      <p className="text-xs text-gray-400 mt-2">
                        {new Date(ticket.createdAt).toLocaleDateString('fr-FR')}
                      </p>
                    </button>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Conversation */}
          <div className="lg:col-span-2 bg-white rounded-lg shadow-sm border border-gray-200 flex flex-col max-h-[calc(100vh-180px)]">
            {selectedTicket ? (
              <>
                {/* Header */}
                <div className="p-4 border-b border-gray-200">
                  <div className="flex items-center justify-between">
                    <div>
                      <h2 className="text-lg font-semibold text-gray-900">{selectedTicket.subject}</h2>
                      <div className="flex items-center gap-3 mt-1">
                        <span className="text-xs text-gray-500">#{selectedTicket.ticketNumber}</span>
                        <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${getStatusColor(selectedTicket.status)}`}>
                          {getStatusLabel(selectedTicket.status)}
                        </span>
                      </div>
                    </div>
                    {canSendMessage && (
                      <Button
                        onClick={() => setShowCloseConfirm(true)}
                        variant="outline"
                        size="sm"
                        className="border-red-600 text-red-600 hover:bg-red-50"
                      >
                        <XMarkIcon className="w-4 h-4 mr-1" />
                        Fermer
                      </Button>
                    )}
                  </div>
                </div>

                {/* Messages */}
                <div className="flex-1 overflow-y-auto p-4 space-y-4">
                  {/* Initial message */}
                  <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                    <div className="flex items-start gap-3">
                      <div className="w-8 h-8 bg-purple-600 rounded-full flex items-center justify-center text-white font-semibold text-sm flex-shrink-0">
                        {user?.firstName?.[0]}{user?.lastName?.[0]}
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 mb-1">
                          <span className="font-medium text-gray-900 text-sm">Vous</span>
                          <span className="text-xs text-gray-500">
                            {new Date(selectedTicket.createdAt).toLocaleString('fr-FR')}
                          </span>
                        </div>
                        <p className="text-sm text-gray-700 whitespace-pre-wrap">{selectedTicket.description}</p>
                      </div>
                    </div>
                  </div>

                  {/* Messages */}
                  {allMessages.map((message) => (
                    <div
                      key={message.id}
                      className={`flex items-start gap-3 ${message.isAdminResponse ? '' : 'flex-row-reverse'}`}
                    >
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center text-white font-semibold text-sm flex-shrink-0 ${
                        message.isAdminResponse ? 'bg-green-600' : 'bg-purple-600'
                      } ${message.isOptimistic ? 'opacity-50' : ''}`}>
                        {message.isAdminResponse ? 'S' : user?.firstName?.[0]}
                      </div>
                      <div className={`flex-1 min-w-0 ${message.isAdminResponse ? '' : 'text-right'}`}>
                        <div className="flex items-center gap-2 mb-1">
                          <span className="font-medium text-gray-900 text-sm">
                            {message.isAdminResponse ? 'Support' : 'Vous'}
                          </span>
                          <span className="text-xs text-gray-500">
                            {new Date(message.createdAt).toLocaleString('fr-FR')}
                          </span>
                          {message.isOptimistic && (
                            <span className="text-xs text-gray-400 italic">Envoi...</span>
                          )}
                        </div>
                        <div className={`inline-block px-4 py-2 rounded-lg ${
                          message.isAdminResponse
                            ? 'bg-gray-100 text-gray-900'
                            : 'bg-purple-600 text-white'
                        } ${message.isOptimistic ? 'opacity-70' : ''}`}>
                          <p className="text-sm whitespace-pre-wrap">{message.message}</p>
                        </div>
                      </div>
                    </div>
                  ))}

                  <div ref={messagesEndRef} />
                </div>

                {/* Input */}
                {canSendMessage ? (
                  <form onSubmit={handleSendMessage} className="p-4 border-t border-gray-200">
                    <div className="flex gap-2">
                      <input
                        type="text"
                        value={messageText}
                        onChange={(e) => setMessageText(e.target.value)}
                        placeholder="Écrire un message..."
                        className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                        disabled={sendingMessage}
                      />
                      <Button
                        type="submit"
                        variant="primary"
                        disabled={sendingMessage || !messageText.trim()}
                      >
                        <PaperAirplaneIcon className="w-5 h-5" />
                      </Button>
                    </div>
                  </form>
                ) : (
                  <div className="p-4 border-t border-gray-200 bg-gray-50 text-center">
                    <p className="text-sm text-gray-600">Ce ticket est fermé</p>
                  </div>
                )}
              </>
            ) : (
              <div className="flex-1 flex items-center justify-center">
                <div className="text-center">
                  <TicketIcon className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-600">Sélectionnez un ticket pour voir la conversation</p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Create Modal */}
      <CreateTicketModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        onSuccess={handleCreateTicket}
      />

      {/* Close Confirmation */}
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

export default UnifiedSupportPage;


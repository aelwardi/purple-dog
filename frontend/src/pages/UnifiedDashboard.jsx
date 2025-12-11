import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  PlusIcon,
  ClipboardDocumentListIcon,
  UserCircleIcon,
  HeartIcon,
  ShoppingBagIcon,
  StarIcon,
  TicketIcon,
  MagnifyingGlassIcon,
  KeyIcon,
  TrashIcon,
  PaperAirplaneIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import { useErrorHandler } from '../hooks/useErrorHandler';
import Header from '../components/common/Header';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import ConfirmModal from '../components/common/ConfirmModal';
import CreateTicketModal from '../components/support/CreateTicketModal';
import ProductListingForm from '../components/products/ProductListingForm';
import MyProducts from '../components/dashboard/MyProducts';
import MyFavorites from '../components/dashboard/MyFavorites';
import MyPurchases from '../components/dashboard/MyPurchases';
import supportTicketService from '../services/supportTicketService';
import profileService from '../services/profileService';

// Validation schemas
const profileSchema = z.object({
  firstName: z.string().min(2, 'Le prénom doit contenir au moins 2 caractères'),
  lastName: z.string().min(2, 'Le nom doit contenir au moins 2 caractères'),
  email: z.string().email('Email invalide'),
  phone: z.string().min(10, 'Téléphone invalide'),
  bio: z.string().optional(),
  companyName: z.string().optional(),
  siret: z.string().optional(),
});

const passwordSchema = z.object({
  currentPassword: z.string().min(1, 'Le mot de passe actuel est requis'),
  newPassword: z.string().min(8, 'Le nouveau mot de passe doit contenir au moins 8 caractères'),
  confirmPassword: z.string().min(1, 'La confirmation est requise'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: 'Les mots de passe ne correspondent pas',
  path: ['confirmPassword'],
});

const UnifiedDashboard = () => {
  console.log('UnifiedDashboard: Component mounting');

  const navigate = useNavigate();
  const { user, updateUser, logout } = useAuth();

  console.log('UnifiedDashboard: User loaded', user);

  const { showSuccess, showError, handleError } = useErrorHandler();
  const [activeTab, setActiveTab] = useState('overview');
  const [showListingForm, setShowListingForm] = useState(false);

  // Support states
  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showCloseConfirm, setShowCloseConfirm] = useState(false);
  const [messageText, setMessageText] = useState('');
  const [optimisticMessages, setOptimisticMessages] = useState([]);

  // Profile states
  const [profileTab, setProfileTab] = useState('info');
  const [profileStep, setProfileStep] = useState(1);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showProfileUpdateConfirm, setShowProfileUpdateConfirm] = useState(false);
  const [pendingProfileData, setPendingProfileData] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  // Determine if user is professional
  const isProfessional = user?.role === 'PROFESSIONAL';

  // Profile form
  const { register: registerProfile, handleSubmit: handleSubmitProfile, formState: { errors: profileErrors }, reset: resetProfile } = useForm({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
      phone: user?.phone || '',
      bio: user?.bio || '',
      companyName: user?.companyName || '',
      siret: user?.siret || '',
    }
  });

  // Password form
  const { register: registerPassword, handleSubmit: handleSubmitPassword, formState: { errors: passwordErrors }, reset: resetPassword } = useForm({
    resolver: zodResolver(passwordSchema)
  });

  // Update profile form when user changes
  useEffect(() => {
    if (user) {
      resetProfile({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        phone: user.phone || '',
        bio: user.bio || '',
        companyName: user.companyName || '',
        siret: user.siret || '',
      });
    }
  }, [user, resetProfile]);

  // Stats cards based on user type
  const statsCards = isProfessional ? [
    { title: 'Objets en vente', value: '0', icon: <ClipboardDocumentListIcon className="w-6 h-6" />, color: 'purple' },
    { title: 'Objets achetés', value: '0', icon: <ShoppingBagIcon className="w-6 h-6" />, color: 'green' },
    { title: 'Favoris', value: '0', icon: <HeartIcon className="w-6 h-6" />, color: 'red' },
    { title: 'Enchères actives', value: '0', icon: <ClipboardDocumentListIcon className="w-6 h-6" />, color: 'blue' },
  ] : [
    { title: 'Objets en vente', value: '0', icon: <ClipboardDocumentListIcon className="w-6 h-6" />, color: 'purple' },
    { title: 'Objets vendus', value: '0', icon: <ClipboardDocumentListIcon className="w-6 h-6" />, color: 'green' },
    { title: 'Messages', value: '0', icon: <TicketIcon className="w-6 h-6" />, color: 'blue' },
  ];

  // Menu items based on user type
  const menuItems = isProfessional ? [
    { id: 'overview', label: 'Vue d\'ensemble', icon: <ClipboardDocumentListIcon className="w-5 h-5" /> },
    { id: 'search', label: 'Rechercher des objets', icon: <MagnifyingGlassIcon className="w-5 h-5" /> },
    { id: 'sell', label: 'Vendre un objet', icon: <PlusIcon className="w-5 h-5" /> },
    { id: 'myObjects', label: 'Mes objets en vente', icon: <ClipboardDocumentListIcon className="w-5 h-5" /> },
    { id: 'favorites', label: 'Mes favoris', icon: <HeartIcon className="w-5 h-5" /> },
    { id: 'purchases', label: 'Mes achats', icon: <ShoppingBagIcon className="w-5 h-5" /> },
    { id: 'support', label: 'Support', icon: <TicketIcon className="w-5 h-5" /> },
    { id: 'profile', label: 'Mon profil', icon: <UserCircleIcon className="w-5 h-5" /> },
  ] : [
    { id: 'overview', label: 'Vue d\'ensemble', icon: <ClipboardDocumentListIcon className="w-5 h-5" /> },
    { id: 'sell', label: 'Vendre un objet', icon: <PlusIcon className="w-5 h-5" /> },
    { id: 'myObjects', label: 'Mes objets', icon: <ClipboardDocumentListIcon className="w-5 h-5" /> },
    { id: 'support', label: 'Support', icon: <TicketIcon className="w-5 h-5" /> },
    { id: 'profile', label: 'Mon profil', icon: <UserCircleIcon className="w-5 h-5" /> },
  ];

  // Load tickets when support tab is active
  useEffect(() => {
    const loadTickets = async () => {
      if (!user?.id) return;
      try {
        const data = await supportTicketService.getMyTickets(user.id);
        setTickets(data);
        if (!selectedTicket && data.length > 0) {
          setSelectedTicket(data[0]);
        }
      } catch (err) {
        handleError(err);
      }
    };

    if (activeTab === 'support' && user?.id) {
      loadTickets();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeTab, user?.id]);

  const handleCreateTicket = async (data) => {
    try {
      const newTicket = await supportTicketService.createTicket(user.id, data);
      setTickets([newTicket, ...tickets]);
      setSelectedTicket(newTicket);
      showSuccess('Ticket créé avec succès !');
    } catch {
      showError('Erreur lors de la création du ticket');
    }
  };

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!messageText.trim() || !selectedTicket) return;

    const tempMessage = {
      id: `temp-${Date.now()}`,
      message: messageText,
      isAdminResponse: false,
      createdAt: new Date().toISOString(),
      isOptimistic: true,
    };

    const currentMessage = messageText;
    setOptimisticMessages([...optimisticMessages, tempMessage]);
    setMessageText('');

    try {
      // Envoyer le message
      await supportTicketService.addMessage(selectedTicket.id, currentMessage);

      // Recharger le ticket avec les nouveaux messages
      const updatedTicket = await supportTicketService.getTicketById(selectedTicket.id);

      // Mettre à jour la liste des tickets et le ticket sélectionné
      setTickets(tickets.map(t => t.id === updatedTicket.id ? updatedTicket : t));
      setSelectedTicket(updatedTicket);
      setOptimisticMessages([]);

      console.log('Message envoyé avec succès', updatedTicket);
    } catch (error) {
      console.error('Erreur lors de l\'envoi du message:', error);
      setOptimisticMessages(optimisticMessages.filter(m => m.id !== tempMessage.id));
      showError('Erreur lors de l\'envoi du message');
    }
  };

  const handleCloseTicket = async () => {
    try {
      await supportTicketService.closeTicket(selectedTicket.id);
      const updatedTicket = await supportTicketService.getTicketByNumber(selectedTicket.ticketNumber);
      setTickets(tickets.map(t => t.id === updatedTicket.id ? updatedTicket : t));
      setSelectedTicket(updatedTicket);
      showSuccess('Ticket fermé');
      setShowCloseConfirm(false);
    } catch (error) {
      handleError(error);
    }
  };

  const handleFeedbackClick = () => {
    navigate('/feedback');
  };

  const handleProductSubmit = (product) => {
    console.log('Product published:', product);
    setShowListingForm(false);
    setActiveTab('myObjects');
    // TODO: Refresh products list
  };

  // Profile handlers
  const onSubmitProfile = async (data) => {
    // Afficher le modal de confirmation au lieu de sauvegarder directement
    setPendingProfileData(data);
    setShowProfileUpdateConfirm(true);
  };

  const handleConfirmProfileUpdate = async () => {
    if (!pendingProfileData) return;

    try {
      await profileService.updateProfile(pendingProfileData);
      // Préserver le rôle et autres propriétés de l'utilisateur
      await updateUser({
        ...pendingProfileData,
        role: user.role, // Préserver le rôle
        id: user.id,     // Préserver l'ID
      });
      showSuccess('Profil mis à jour avec succès');
      setProfileStep(1); // Retour au step 1 après enregistrement
      setShowProfileUpdateConfirm(false);
      setPendingProfileData(null);
    } catch (error) {
      handleError(error);
      setShowProfileUpdateConfirm(false);
    }
  };

  const onSubmitPassword = async (data) => {
    try {
      await profileService.changePassword({
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
      });
      showSuccess('Mot de passe modifié avec succès');
      resetPassword();
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Erreur lors du changement de mot de passe';
      showError(errorMessage);
    }
  };

  const handleDeleteAccount = async () => {
    setIsDeleting(true);
    try {
      await profileService.deleteAccount();
      showSuccess('Compte supprimé avec succès');
      await logout();
      navigate('/');
    } catch (error) {
      handleError(error);
      setIsDeleting(false);
    }
  };

  // Get page title based on active tab
  const getPageTitle = () => {
    const currentItem = menuItems.find(item => item.id === activeTab);
    return currentItem ? currentItem.label : 'Dashboard';
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header Principal */}
      <Header />

      {/* Dashboard Layout */}
      <div className="flex h-[calc(100vh-80px)]">
        {/* Sidebar Menu - Fixed */}
        <div className="w-64 bg-white border-r border-gray-200 flex-shrink-0">
          <div className="h-full flex flex-col">
            {/* Dashboard Title */}
            <div className="p-6 border-b border-gray-200">
              <h2 className="text-lg font-semibold text-gray-900">Dashboard</h2>
              <p className="text-sm text-gray-500 mt-1">
                {isProfessional ? 'Professionnel' : 'Particulier'}
              </p>
              {isProfessional && (
                <p className="text-xs text-purple-600 mt-1">Abonnement actif</p>
              )}
            </div>

            {/* Menu Items */}
            <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
              {menuItems.map((item) => (
                <button
                  key={item.id}
                  onClick={() => {
                    setActiveTab(item.id);
                    if (item.id === 'sell') {
                      setShowListingForm(true);
                    } else {
                      setShowListingForm(false);
                    }
                  }}
                  className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                    activeTab === item.id
                      ? 'bg-purple-100 text-purple-700 font-medium'
                      : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  {item.icon}
                  <span className="text-sm">{item.label}</span>
                </button>
              ))}
            </nav>

            {/* Feedback Button */}
            <div className="p-4 border-t border-gray-200">
              <Button
                variant="outline"
                className="w-full flex items-center justify-center gap-2 border-purple-300 text-purple-700 hover:bg-purple-50"
                onClick={handleFeedbackClick}
              >
                <StarIcon className="w-5 h-5" />
                <span className="text-sm">Donner mon avis</span>
              </Button>
            </div>
          </div>
        </div>

        {/* Main Content Area - Scrollable */}
        <div className="flex-1 overflow-y-auto">
          <div className="p-8">
            {/* Page Title */}
            <div className="mb-6">
              <h1 className="text-2xl font-bold text-gray-900">{getPageTitle()}</h1>
            </div>

            {/* Content based on active tab */}
            {activeTab === 'overview' && (
              <div className="space-y-6">
                {/* Stats */}
                <div className={`grid gap-6 ${isProfessional ? 'md:grid-cols-2 lg:grid-cols-4' : 'md:grid-cols-3'}`}>
                  {statsCards.map((stat, index) => (
                    <Card key={index} className="p-6">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm text-gray-600 mb-1">{stat.title}</p>
                          <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
                        </div>
                        <div className={`p-3 ${
                          stat.color === 'purple' ? 'bg-purple-100 text-purple-600' :
                          stat.color === 'green' ? 'bg-green-100 text-green-600' :
                          stat.color === 'red' ? 'bg-red-100 text-red-600' :
                          'bg-blue-100 text-blue-600'
                        } rounded-lg`}>
                          {stat.icon}
                        </div>
                      </div>
                    </Card>
                  ))}
                </div>

                {/* Quick Actions */}
                <Card className="p-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-4">Actions rapides</h2>
                  <div className={`grid gap-4 ${isProfessional ? 'md:grid-cols-3' : 'md:grid-cols-2'}`}>
                    {isProfessional && (
                      <Button
                        variant="primary"
                        onClick={() => setActiveTab('search')}
                        className="h-24 flex flex-col items-center justify-center"
                      >
                        <MagnifyingGlassIcon className="w-8 h-8 mb-2" />
                        <span>Rechercher</span>
                      </Button>
                    )}
                    <Button
                      variant="primary"
                      onClick={() => {
                        setActiveTab('sell');
                        setShowListingForm(true);
                      }}
                      className="h-24 flex flex-col items-center justify-center"
                    >
                      <PlusIcon className="w-8 h-8 mb-2" />
                      <span>Vendre</span>
                    </Button>
                    {isProfessional && (
                      <Button
                        variant="outline"
                        onClick={() => setActiveTab('favorites')}
                        className="h-24 flex flex-col items-center justify-center"
                      >
                        <HeartIcon className="w-8 h-8 mb-2" />
                        <span>Favoris</span>
                      </Button>
                    )}
                  </div>
                </Card>

                {/* Welcome Message */}
                <Card className="p-6 bg-purple-50 border-purple-200">
                  <h3 className="text-lg font-semibold text-purple-900 mb-2">
                    {isProfessional ? 'Bienvenue sur Purple Dog Pro' : 'Bienvenue sur Purple Dog'}
                  </h3>
                  <p className="text-purple-700 mb-4">
                    {isProfessional
                      ? 'Accédez à des objets d\'exception et développez votre activité.'
                      : 'Vendez et achetez des objets d\'occasion en toute simplicité.'
                    }
                  </p>
                  <div className="flex gap-3">
                    {isProfessional && (
                      <Button
                        variant="primary"
                        size="small"
                        onClick={() => setActiveTab('search')}
                      >
                        Explorer les objets
                      </Button>
                    )}
                    <Button
                      variant="outline"
                      size="small"
                      onClick={() => {
                        setActiveTab('sell');
                        setShowListingForm(true);
                      }}
                    >
                      Vendre un objet
                    </Button>
                  </div>
                </Card>
              </div>
            )}

            {activeTab === 'search' && isProfessional && (
              <Card className="p-6">
                <div className="mb-6">
                  <input
                    type="text"
                    placeholder="Rechercher par nom, catégorie..."
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  />
                </div>
                <div className="text-center py-12">
                  <MagnifyingGlassIcon className="w-16 h-16 mx-auto text-gray-400 mb-4" />
                  <p className="text-gray-600 mb-4">Aucun objet disponible pour le moment</p>
                  <p className="text-sm text-gray-500">Les objets mis en vente apparaîtront ici</p>
                </div>
              </Card>
            )}

            {activeTab === 'sell' && showListingForm && (
              <div>
                <ProductListingForm
                  onSubmit={handleProductSubmit}
                  onCancel={() => {
                    setShowListingForm(false);
                    setActiveTab('overview');
                  }}
                />
              </div>
            )}

            {activeTab === 'myObjects' && (
              <MyProducts />
            )}

            {activeTab === 'favorites' && isProfessional && (
              <MyFavorites />
            )}

            {activeTab === 'purchases' && isProfessional && (
              <MyPurchases />
            )}

            {/* Support Tab */}
            {activeTab === 'support' && (
              <div className="space-y-6">
                {/* Header avec bouton nouveau ticket */}
                <div className="flex items-center justify-end">
                  <Button
                    variant="primary"
                    onClick={() => setShowCreateModal(true)}
                    className="flex items-center gap-2"
                  >
                    <PlusIcon className="w-5 h-5" />
                    Nouveau Ticket
                  </Button>
                </div>

                {/* Support Content */}
                <div className="grid lg:grid-cols-3 gap-6" style={{ height: 'calc(100vh - 320px)' }}>
                  {/* Tickets List */}
                  <Card className="lg:col-span-1 flex flex-col h-full">
                    <div className="p-4 border-b border-gray-200">
                      <h3 className="font-semibold text-gray-900">Mes Tickets</h3>
                    </div>
                    {tickets.length === 0 ? (
                      <div className="flex-1 flex items-center justify-center p-8">
                        <div className="text-center">
                          <TicketIcon className="w-12 h-12 mx-auto text-gray-400 mb-2" />
                          <p className="text-sm text-gray-600">Aucun ticket</p>
                        </div>
                      </div>
                    ) : (
                      <div className="flex-1 overflow-y-auto p-4">
                        <div className="space-y-2">
                          {tickets.map((ticket) => (
                            <button
                              key={ticket.id}
                              onClick={() => {
                                setSelectedTicket(ticket);
                                setOptimisticMessages([]);
                              }}
                              className={`w-full text-left p-3 rounded-lg transition-colors ${
                                selectedTicket?.id === ticket.id
                                  ? 'bg-purple-50 border-l-4 border-purple-600'
                                  : 'hover:bg-gray-50 border-l-4 border-transparent'
                              }`}
                            >
                              <p className="font-medium text-sm text-gray-900 line-clamp-1">
                                {ticket.subject}
                              </p>
                              <p className="text-xs text-gray-500 mt-1">#{ticket.ticketNumber}</p>
                              <span className={`inline-block mt-2 px-2 py-0.5 rounded-full text-xs font-medium ${
                                ticket.status === 'OPEN' ? 'bg-blue-100 text-blue-800' :
                                ticket.status === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' :
                                ticket.status === 'RESOLVED' ? 'bg-green-100 text-green-800' :
                                'bg-gray-100 text-gray-800'
                              }`}>
                                {ticket.status === 'OPEN' ? 'Ouvert' :
                                 ticket.status === 'IN_PROGRESS' ? 'En cours' :
                                 ticket.status === 'RESOLVED' ? 'Résolu' : 'Fermé'}
                              </span>
                            </button>
                          ))}
                        </div>
                      </div>
                    )}
                  </Card>

                  {/* Conversation */}
                  <Card className="lg:col-span-2 flex flex-col h-full">
                    {selectedTicket ? (
                      <>
                        {/* Header */}
                        <div className="p-4 border-b border-gray-200">
                          <div className="flex items-center justify-between">
                            <div>
                              <h3 className="font-semibold text-gray-900">{selectedTicket.subject}</h3>
                              <p className="text-xs text-gray-500">#{selectedTicket.ticketNumber}</p>
                            </div>
                            {selectedTicket.status !== 'CLOSED' && (
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => setShowCloseConfirm(true)}
                                className="border-red-600 text-red-600"
                              >
                                <XMarkIcon className="w-4 h-4 mr-1" />
                                Fermer
                              </Button>
                            )}
                          </div>
                        </div>

                        {/* Messages */}
                        <div className="flex-1 overflow-y-auto p-4 space-y-3">
                          {/* Initial message */}
                          <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
                            <p className="text-sm font-medium text-gray-900 mb-1">Vous</p>
                            <p className="text-sm text-gray-700">{selectedTicket.description}</p>
                            <p className="text-xs text-gray-500 mt-2">
                              {new Date(selectedTicket.createdAt).toLocaleString('fr-FR')}
                            </p>
                          </div>

                          {/* Messages - ensure we handle both ticketMessages and messages fields */}
                          {(() => {
                            const messages = selectedTicket.ticketMessages || selectedTicket.messages || [];
                            const allMessages = [...messages, ...optimisticMessages];

                            return allMessages.map((message, index) => (
                              <div
                                key={message.id || `msg-${index}`}
                                className={`flex ${message.isAdminResponse ? '' : 'flex-row-reverse'}`}
                              >
                                <div className={`max-w-[70%] rounded-lg p-3 ${
                                  message.isAdminResponse
                                    ? 'bg-gray-100 text-gray-900'
                                    : 'bg-purple-600 text-white'
                                } ${message.isOptimistic ? 'opacity-70' : ''}`}>
                                  <p className="text-xs font-medium mb-1">
                                    {message.isAdminResponse ? 'Support' : 'Vous'}
                                  </p>
                                  <p className="text-sm">{message.message}</p>
                                  {message.createdAt && (
                                    <p className={`text-xs mt-1 ${
                                      message.isAdminResponse ? 'text-gray-500' : 'text-purple-200'
                                    }`}>
                                      {new Date(message.createdAt).toLocaleString('fr-FR')}
                                    </p>
                                  )}
                                </div>
                              </div>
                            ));
                          })()}
                        </div>

                        {/* Input */}
                        {selectedTicket.status !== 'CLOSED' ? (
                          <form onSubmit={handleSendMessage} className="p-4 border-t border-gray-200">
                            <div className="flex gap-2">
                              <input
                                type="text"
                                value={messageText}
                                onChange={(e) => setMessageText(e.target.value)}
                                placeholder="Écrire un message..."
                                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
                              />
                              <Button type="submit" variant="primary">
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
                          <p className="text-gray-600">Sélectionnez un ticket</p>
                        </div>
                      </div>
                    )}
                  </Card>
                </div>
              </div>
            )}

            {/* Profile Tab */}
            {activeTab === 'profile' && (
              <div className="space-y-6">
                {/* Tabs */}
                <div className="border-b border-gray-200">
                  <div className="flex space-x-8">
                    <button
                      onClick={() => setProfileTab('info')}
                      className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                        profileTab === 'info'
                          ? 'border-purple-600 text-purple-600'
                          : 'border-transparent text-gray-500 hover:text-gray-700'
                      }`}
                    >
                      <UserCircleIcon className="w-5 h-5 inline mr-2" />
                      Informations
                    </button>
                    <button
                      onClick={() => setProfileTab('password')}
                      className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                        profileTab === 'password'
                          ? 'border-purple-600 text-purple-600'
                          : 'border-transparent text-gray-500 hover:text-gray-700'
                      }`}
                    >
                      <KeyIcon className="w-5 h-5 inline mr-2" />
                      Mot de passe
                    </button>
                    <button
                      onClick={() => setProfileTab('danger')}
                      className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                        profileTab === 'danger'
                          ? 'border-red-600 text-red-600'
                          : 'border-transparent text-gray-500 hover:text-gray-700'
                      }`}
                    >
                      <TrashIcon className="w-5 h-5 inline mr-2" />
                      Zone dangereuse
                    </button>
                  </div>
                </div>

                {/* Info Tab */}
                {profileTab === 'info' && (
                  <Card className="p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-6">Informations personnelles</h3>

                    {/* Steps Indicator */}
                    <div className="flex items-center justify-center mb-8">
                      <div className="flex items-center space-x-4">
                        {/* Step 1 */}
                        <div className="flex items-center">
                          <div className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold ${
                            profileStep === 1 ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-600'
                          }`}>
                            1
                          </div>
                          <span className={`ml-2 text-sm font-medium ${
                            profileStep === 1 ? 'text-purple-600' : 'text-gray-500'
                          }`}>
                            Identité
                          </span>
                        </div>
                        <div className="w-12 h-0.5 bg-gray-300"></div>

                        {/* Step 2 */}
                        <div className="flex items-center">
                          <div className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold ${
                            profileStep === 2 ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-600'
                          }`}>
                            2
                          </div>
                          <span className={`ml-2 text-sm font-medium ${
                            profileStep === 2 ? 'text-purple-600' : 'text-gray-500'
                          }`}>
                            Contact
                          </span>
                        </div>
                        <div className="w-12 h-0.5 bg-gray-300"></div>

                        {/* Step 3 */}
                        <div className="flex items-center">
                          <div className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold ${
                            profileStep === 3 ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-600'
                          }`}>
                            3
                          </div>
                          <span className={`ml-2 text-sm font-medium ${
                            profileStep === 3 ? 'text-purple-600' : 'text-gray-500'
                          }`}>
                            {isProfessional ? 'Entreprise' : 'Bio'}
                          </span>
                        </div>
                      </div>
                    </div>

                    <form
                      onSubmit={(e) => {
                        // Empêcher la soumission si on n'est pas au dernier step
                        if (profileStep < 3) {
                          e.preventDefault();
                          return;
                        }
                        // Sinon, soumettre normalement
                        handleSubmitProfile(onSubmitProfile)(e);
                      }}
                      className="space-y-6"
                    >
                      {/* Step 1: Identité */}
                      {profileStep === 1 && (
                        <div className="space-y-4">
                          <div className="grid md:grid-cols-2 gap-4">
                            <Input
                              label="Prénom"
                              required
                              {...registerProfile('firstName')}
                              error={profileErrors.firstName?.message}
                            />
                            <Input
                              label="Nom"
                              required
                              {...registerProfile('lastName')}
                              error={profileErrors.lastName?.message}
                            />
                          </div>
                          <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">Type de compte</label>
                            <input
                              type="text"
                              value={isProfessional ? 'Professionnel' : 'Particulier'}
                              disabled
                              className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50"
                            />
                          </div>
                        </div>
                      )}

                      {/* Step 2: Contact */}
                      {profileStep === 2 && (
                        <div className="space-y-4">
                          <Input
                            label="Email"
                            type="email"
                            required
                            {...registerProfile('email')}
                            error={profileErrors.email?.message}
                          />
                          <Input
                            label="Téléphone"
                            required
                            {...registerProfile('phone')}
                            error={profileErrors.phone?.message}
                          />
                        </div>
                      )}

                      {/* Step 3: Bio ou Entreprise */}
                      {profileStep === 3 && (
                        <div className="space-y-4">
                          {isProfessional ? (
                            // Professionnel: Entreprise + SIRET + Bio
                            <>
                              <div className="grid md:grid-cols-2 gap-4">
                                <Input
                                  label="Nom de l'entreprise"
                                  {...registerProfile('companyName')}
                                  error={profileErrors.companyName?.message}
                                />
                                <Input
                                  label="SIRET"
                                  {...registerProfile('siret')}
                                  error={profileErrors.siret?.message}
                                />
                              </div>
                              <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Bio</label>
                                <textarea
                                  {...registerProfile('bio')}
                                  rows={4}
                                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                  placeholder="Décrivez votre entreprise..."
                                />
                                {profileErrors.bio && (
                                  <p className="text-sm text-red-600 mt-1">{profileErrors.bio.message}</p>
                                )}
                              </div>
                            </>
                          ) : (
                            // Particulier: Bio uniquement
                            <div>
                              <label className="block text-sm font-medium text-gray-700 mb-2">Bio</label>
                              <textarea
                                {...registerProfile('bio')}
                                rows={6}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                placeholder="Parlez-nous de vous..."
                              />
                              {profileErrors.bio && (
                                <p className="text-sm text-red-600 mt-1">{profileErrors.bio.message}</p>
                              )}
                            </div>
                          )}
                        </div>
                      )}

                      {/* Navigation Buttons */}
                      <div className="flex justify-between pt-4 border-t border-gray-200">
                        <div>
                          {profileStep > 1 && (
                            <Button
                              type="button"
                              variant="outline"
                              onClick={(e) => {
                                e.preventDefault();
                                setProfileStep(profileStep - 1);
                              }}
                            >
                              Précédent
                            </Button>
                          )}
                        </div>
                        <div className="flex gap-3">
                          {profileStep < 3 ? (
                            <Button
                              type="button"
                              variant="primary"
                              onClick={(e) => {
                                e.preventDefault();
                                setProfileStep(profileStep + 1);
                              }}
                            >
                              Suivant
                            </Button>
                          ) : (
                            <Button type="submit" variant="primary">
                              Enregistrer les modifications
                            </Button>
                          )}
                        </div>
                      </div>
                    </form>
                  </Card>
                )}

                {/* Password Tab */}
                {profileTab === 'password' && (
                  <Card className="p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-4">Changer le mot de passe</h3>
                    <form onSubmit={handleSubmitPassword(onSubmitPassword)} className="space-y-4 max-w-md">
                      <Input
                        label="Mot de passe actuel"
                        type="password"
                        required
                        {...registerPassword('currentPassword')}
                        error={passwordErrors.currentPassword?.message}
                      />
                      <Input
                        label="Nouveau mot de passe"
                        type="password"
                        required
                        {...registerPassword('newPassword')}
                        error={passwordErrors.newPassword?.message}
                      />
                      <Input
                        label="Confirmer le nouveau mot de passe"
                        type="password"
                        required
                        {...registerPassword('confirmPassword')}
                        error={passwordErrors.confirmPassword?.message}
                      />
                      <div className="pt-4">
                        <Button type="submit" variant="primary">
                          Changer le mot de passe
                        </Button>
                      </div>
                    </form>
                  </Card>
                )}

                {/* Danger Zone Tab */}
                {profileTab === 'danger' && (
                  <Card className="p-6 border-red-200 bg-red-50">
                    <h3 className="text-lg font-semibold text-red-900 mb-2">Zone dangereuse</h3>
                    <p className="text-sm text-red-700 mb-4">
                      Les actions suivantes sont irréversibles. Une fois votre compte supprimé, toutes vos données seront définitivement effacées.
                    </p>
                    <Button
                      variant="outline"
                      className="border-red-600 text-red-600 hover:bg-red-50"
                      onClick={() => setShowDeleteConfirm(true)}
                      disabled={isDeleting}
                    >
                      <TrashIcon className="w-5 h-5 inline mr-2" />
                      Supprimer mon compte
                    </Button>
                  </Card>
                )}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Modals */}
      <CreateTicketModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        onSuccess={handleCreateTicket}
      />

      <ConfirmModal
        isOpen={showCloseConfirm}
        onClose={() => setShowCloseConfirm(false)}
        onConfirm={handleCloseTicket}
        title="Fermer le ticket"
        message="Êtes-vous sûr de vouloir fermer ce ticket ?"
        confirmText="Oui, fermer"
        variant="warning"
      />

      <ConfirmModal
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDeleteAccount}
        title="Supprimer mon compte"
        message="Êtes-vous vraiment sûr de vouloir supprimer votre compte ? Cette action est irréversible et toutes vos données seront définitivement supprimées."
        confirmText="Oui, supprimer mon compte"
        variant="danger"
      />

      <ConfirmModal
        isOpen={showProfileUpdateConfirm}
        onClose={() => {
          setShowProfileUpdateConfirm(false);
          setPendingProfileData(null);
        }}
        onConfirm={handleConfirmProfileUpdate}
        title="Confirmer la modification du profil"
        message="Êtes-vous sûr de vouloir enregistrer ces modifications ?"
        confirmText="Oui, enregistrer"
        variant="primary"
      />
    </div>
  );
};

export default UnifiedDashboard;


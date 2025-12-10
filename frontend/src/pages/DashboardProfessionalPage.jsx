import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { PlusIcon, ClipboardDocumentListIcon, UserCircleIcon, MagnifyingGlassIcon, HeartIcon, ShoppingBagIcon, StarIcon, ArrowRightOnRectangleIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import { useErrorHandler } from '../hooks/useErrorHandler';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import ProductListingForm from '../components/products/ProductListingForm';

const DashboardProfessionalPage = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const { showSuccess, handleError } = useErrorHandler();
  const [activeTab, setActiveTab] = useState('overview');
  const [showListingForm, setShowListingForm] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const userMenuRef = useRef(null);

  // Close user menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setUserMenuOpen(false);
      }
    };

    if (userMenuOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [userMenuOpen]);

  const handleLogout = async () => {
    try {
      await logout();
      showSuccess('Déconnexion réussie');
      navigate('/');
    } catch (error) {
      handleError(error);
    }
  };


  const statsCards = [
    { title: 'Objets en vente', value: '0', icon: <ClipboardDocumentListIcon className="w-6 h-6" />, color: 'purple' },
    { title: 'Objets achetés', value: '0', icon: <ShoppingBagIcon className="w-6 h-6" />, color: 'green' },
    { title: 'Favoris', value: '0', icon: <HeartIcon className="w-6 h-6" />, color: 'red' },
    { title: 'Enchères actives', value: '0', icon: <ClipboardDocumentListIcon className="w-6 h-6" />, color: 'blue' },
  ];

  const menuItems = [
    { id: 'overview', label: 'Vue d\'ensemble', icon: <ClipboardDocumentListIcon className="w-5 h-5" /> },
    { id: 'search', label: 'Rechercher des objets', icon: <MagnifyingGlassIcon className="w-5 h-5" /> },
    { id: 'sell', label: 'Vendre un objet', icon: <PlusIcon className="w-5 h-5" /> },
    { id: 'myObjects', label: 'Mes objets en vente', icon: <ClipboardDocumentListIcon className="w-5 h-5" /> },
    { id: 'favorites', label: 'Mes favoris', icon: <HeartIcon className="w-5 h-5" /> },
    { id: 'purchases', label: 'Mes achats', icon: <ShoppingBagIcon className="w-5 h-5" /> },
    { id: 'profile', label: 'Mon profil', icon: <UserCircleIcon className="w-5 h-5" /> },
  ];

  const handleFeedbackClick = () => {
    navigate('/feedback');
  };

  const handleProductSubmit = (formData) => {
    console.log('Product listing:', formData);
    // TODO: Send to backend
    setShowListingForm(false);
    setActiveTab('myObjects');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top Bar */}
      <div className="bg-white border-b border-gray-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div>
              <h1 className="text-xl font-semibold text-gray-900">
                Dashboard Professionnel
              </h1>
              <p className="text-xs text-purple-600">Abonnement actif • 1 mois gratuit</p>
            </div>
            <div className="relative" ref={userMenuRef}>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setUserMenuOpen(!userMenuOpen);
                }}
                className="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors"
              >
                <div className="w-8 h-8 bg-purple-600 rounded-full flex items-center justify-center text-white font-semibold text-sm">
                  {user?.firstName?.[0]}{user?.lastName?.[0]}
                </div>
                <div className="text-left">
                  <p className="text-sm font-medium text-gray-700">
                    {user?.firstName} {user?.lastName}
                  </p>
                  {user?.companyName && (
                    <p className="text-xs text-gray-500">{user.companyName}</p>
                  )}
                </div>
                <svg
                  className={`w-4 h-4 text-gray-500 transition-transform ${userMenuOpen ? 'rotate-180' : ''}`}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              {/* User Dropdown Menu */}
              {userMenuOpen && (
                <div className="absolute right-0 mt-2 w-56 bg-white rounded-lg shadow-lg py-2 z-50 border border-gray-200 animate-fadeIn">
                  <div className="px-4 py-3 border-b border-gray-100">
                    <p className="text-sm font-semibold text-gray-900">{user?.firstName} {user?.lastName}</p>
                    <p className="text-xs text-gray-500 mt-1">{user?.email}</p>
                    {user?.companyName && (
                      <p className="text-xs text-purple-600 mt-1">{user.companyName}</p>
                    )}
                  </div>
                  <Link
                    to="/dashboard"
                    className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    onClick={() => setUserMenuOpen(false)}
                  >
                    Mon Dashboard
                  </Link>
                  <Link
                    to="/profile"
                    className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    onClick={() => setUserMenuOpen(false)}
                  >
                    Mon Profil
                  </Link>
                  <hr className="my-2" />
                  <button
                    onClick={() => {
                      setUserMenuOpen(false);
                      handleLogout();
                    }}
                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                  >
                    <ArrowRightOnRectangleIcon className="h-4 w-4" />
                    <span>Se déconnecter</span>
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid lg:grid-cols-4 gap-8">
          {/* Sidebar */}
          <div className="lg:col-span-1">
            <Card className="p-4">
              <nav className="space-y-2">
                {menuItems.map((item) => (
                  <button
                    key={item.id}
                    onClick={() => {
                      if (item.id === 'profile') {
                        navigate('/profile');
                      } else {
                        setActiveTab(item.id);
                        if (item.id === 'sell') {
                          setShowListingForm(true);
                        } else {
                          setShowListingForm(false);
                        }
                      }
                    }}
                    className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                      activeTab === item.id
                        ? 'bg-purple-100 text-purple-700 font-medium'
                        : 'text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    {item.icon}
                    {item.label}
                  </button>
                ))}
              </nav>

              {/* Feedback Button */}
              <div className="mt-4">
                <Button 
                  variant="outline" 
                  className="w-full flex items-center justify-center gap-2 border-purple-300 text-purple-700 hover:bg-purple-50"
                  onClick={handleFeedbackClick}
                >
                  <StarIcon className="w-5 h-5" />
                  Donner mon avis
                </Button>
              </div>
            </Card>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-3">
            {activeTab === 'overview' && (
              <div className="space-y-6">
                {/* Stats */}
                <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
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
                  <div className="grid md:grid-cols-3 gap-4">
                    <Button
                      variant="primary"
                      onClick={() => setActiveTab('search')}
                      className="h-24 flex flex-col items-center justify-center"
                    >
                      <MagnifyingGlassIcon className="w-8 h-8 mb-2" />
                      Rechercher
                    </Button>
                    <Button
                      variant="primary"
                      onClick={() => {
                        setActiveTab('sell');
                        setShowListingForm(true);
                      }}
                      className="h-24 flex flex-col items-center justify-center"
                    >
                      <PlusIcon className="w-8 h-8 mb-2" />
                      Vendre
                    </Button>
                    <Button
                      variant="outline"
                      onClick={() => setActiveTab('favorites')}
                      className="h-24 flex flex-col items-center justify-center"
                    >
                      <HeartIcon className="w-8 h-8 mb-2" />
                      Favoris
                    </Button>
                  </div>
                </Card>

                {/* Welcome Message */}
                <Card className="p-6 bg-purple-50 border-purple-200">
                  <h3 className="text-lg font-semibold text-purple-900 mb-2">
                    Bienvenue sur Purple Dog Pro
                  </h3>
                  <p className="text-purple-700 mb-4">
                    Accédez à des objets d'exception et développez votre activité.
                  </p>
                  <div className="flex gap-3">
                    <Button
                      variant="primary"
                      size="small"
                      onClick={() => setActiveTab('search')}
                    >
                      Explorer les objets
                    </Button>
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

            {activeTab === 'search' && (
              <Card className="p-6">
                <h2 className="text-2xl font-display font-bold text-gray-900 mb-6">
                  Rechercher des objets
                </h2>
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
                <div className="mb-6">
                  <h2 className="text-2xl font-display font-bold text-gray-900 mb-2">
                    Vendre un objet
                  </h2>
                  <p className="text-gray-600">
                    Remplissez le formulaire ci-dessous pour mettre votre objet en vente
                  </p>
                </div>
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
              <Card className="p-6">
                <h2 className="text-2xl font-display font-bold text-gray-900 mb-4">
                  Mes objets en vente
                </h2>
                <div className="text-center py-12">
                  <ClipboardDocumentListIcon className="w-16 h-16 mx-auto text-gray-400 mb-4" />
                  <p className="text-gray-600 mb-4">Vous n'avez pas encore d'objets en vente</p>
                  <Button variant="primary" onClick={() => {
                    setActiveTab('sell');
                    setShowListingForm(true);
                  }}>
                    Vendre mon premier objet
                  </Button>
                </div>
              </Card>
            )}

            {activeTab === 'favorites' && (
              <Card className="p-6">
                <h2 className="text-2xl font-display font-bold text-gray-900 mb-4">
                  Mes favoris
                </h2>
                <div className="text-center py-12">
                  <HeartIcon className="w-16 h-16 mx-auto text-gray-400 mb-4" />
                  <p className="text-gray-600 mb-4">Vous n'avez pas encore de favoris</p>
                  <Button variant="primary" onClick={() => setActiveTab('search')}>
                    Explorer les objets
                  </Button>
                </div>
              </Card>
            )}

            {activeTab === 'purchases' && (
              <Card className="p-6">
                <h2 className="text-2xl font-display font-bold text-gray-900 mb-4">
                  Mes achats
                </h2>
                <div className="text-center py-12">
                  <ShoppingBagIcon className="w-16 h-16 mx-auto text-gray-400 mb-4" />
                  <p className="text-gray-600 mb-4">Vous n'avez pas encore effectué d'achats</p>
                  <Button variant="primary" onClick={() => setActiveTab('search')}>
                    Explorer les objets
                  </Button>
                </div>
              </Card>
            )}

            {activeTab === 'profile' && (
              <Card className="p-6">
                <h2 className="text-2xl font-display font-bold text-gray-900 mb-6">
                  Mon profil professionnel
                </h2>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
                    <input 
                      type="email" 
                      value={user?.email || ''}
                      disabled
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Type de compte</label>
                    <input 
                      type="text" 
                      value="Professionnel" 
                      disabled 
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Abonnement</label>
                    <input 
                      type="text" 
                      value="Actif - 1 mois gratuit puis 49€/mois" 
                      disabled 
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50"
                    />
                  </div>
                  <div className="pt-4">
                    <Button variant="primary">Modifier mon profil</Button>
                  </div>
                </div>
              </Card>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardProfessionalPage;

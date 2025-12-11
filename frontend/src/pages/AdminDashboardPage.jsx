import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  UsersIcon,
  ShoppingBagIcon,
  CurrencyEuroIcon,
  ChartBarIcon,
  ClipboardDocumentListIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon,
  ClockIcon,
  ArrowRightOnRectangleIcon
} from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';

const AdminDashboardPage = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/admin');
  };

  // Données statistiques mockées - à remplacer par de vraies données API
  const stats = {
    totalUsers: 1247,
    totalProducts: 3589,
    totalRevenue: 125640,
    pendingOrders: 23,
    activeAuctions: 45,
    reportedItems: 8
  };

  const recentActivities = [
    { id: 1, type: 'user', message: 'Nouvel utilisateur inscrit', time: 'Il y a 5 min', status: 'success' },
    { id: 2, type: 'product', message: 'Nouveau produit publié', time: 'Il y a 12 min', status: 'success' },
    { id: 3, type: 'report', message: 'Produit signalé', time: 'Il y a 23 min', status: 'warning' },
    { id: 4, type: 'order', message: 'Commande complétée', time: 'Il y a 1h', status: 'success' },
    { id: 5, type: 'user', message: 'Compte suspendu', time: 'Il y a 2h', status: 'danger' }
  ];

  const formatPrice = (amount) => {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">
                Dashboard Administrateur
              </h1>
              <p className="mt-1 text-sm text-gray-500">
                Bienvenue, {user?.firstName} {user?.lastName}
              </p>
            </div>
            <div className="flex items-center gap-4">
              <Badge variant={user?.role === 'ADMIN' ? 'danger' : 'secondary'}>
                {user?.role === 'ADMIN' ? '👑 Admin' : user?.role}
              </Badge>
              <Button
                variant="outline"
                size="small"
                onClick={handleLogout}
                icon={<ArrowRightOnRectangleIcon className="w-4 h-4" />}
              >
                Déconnexion
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          {/* Total Users */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Utilisateurs</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stats.totalUsers.toLocaleString()}
                </p>
                <p className="text-sm text-green-600 mt-1">+12% ce mois</p>
              </div>
              <div className="p-3 bg-blue-100 rounded-full">
                <UsersIcon className="w-8 h-8 text-blue-600" />
              </div>
            </div>
          </Card>

          {/* Total Products */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Produits</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stats.totalProducts.toLocaleString()}
                </p>
                <p className="text-sm text-green-600 mt-1">+8% ce mois</p>
              </div>
              <div className="p-3 bg-purple-100 rounded-full">
                <ShoppingBagIcon className="w-8 h-8 text-purple-600" />
              </div>
            </div>
          </Card>

          {/* Total Revenue */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Revenus</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {formatPrice(stats.totalRevenue)}
                </p>
                <p className="text-sm text-green-600 mt-1">+15% ce mois</p>
              </div>
              <div className="p-3 bg-green-100 rounded-full">
                <CurrencyEuroIcon className="w-8 h-8 text-green-600" />
              </div>
            </div>
          </Card>

          {/* Pending Orders */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Commandes en attente</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stats.pendingOrders}
                </p>
                <button className="text-sm text-purple-600 mt-1 hover:text-purple-700">
                  Voir détails →
                </button>
              </div>
              <div className="p-3 bg-yellow-100 rounded-full">
                <ClockIcon className="w-8 h-8 text-yellow-600" />
              </div>
            </div>
          </Card>

          {/* Active Auctions */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Enchères actives</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stats.activeAuctions}
                </p>
                <button className="text-sm text-purple-600 mt-1 hover:text-purple-700">
                  Voir détails →
                </button>
              </div>
              <div className="p-3 bg-orange-100 rounded-full">
                <ChartBarIcon className="w-8 h-8 text-orange-600" />
              </div>
            </div>
          </Card>

          {/* Reported Items */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Signalements</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stats.reportedItems}
                </p>
                <button className="text-sm text-red-600 mt-1 hover:text-red-700">
                  Traiter →
                </button>
              </div>
              <div className="p-3 bg-red-100 rounded-full">
                <ExclamationTriangleIcon className="w-8 h-8 text-red-600" />
              </div>
            </div>
          </Card>
        </div>

        {/* Recent Activity */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="p-6">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold text-gray-900">Activité récente</h2>
              <button className="text-sm text-purple-600 hover:text-purple-700">
                Voir tout →
              </button>
            </div>
            <div className="space-y-4">
              {recentActivities.map((activity) => (
                <div key={activity.id} className="flex items-start gap-3">
                  <div className={`p-2 rounded-full ${
                    activity.status === 'success' ? 'bg-green-100' :
                    activity.status === 'warning' ? 'bg-yellow-100' :
                    activity.status === 'danger' ? 'bg-red-100' : 'bg-gray-100'
                  }`}>
                    {activity.status === 'success' ? (
                      <CheckCircleIcon className={`w-5 h-5 ${
                        activity.status === 'success' ? 'text-green-600' : 'text-gray-600'
                      }`} />
                    ) : activity.status === 'warning' ? (
                      <ClockIcon className="w-5 h-5 text-yellow-600" />
                    ) : (
                      <ExclamationTriangleIcon className="w-5 h-5 text-red-600" />
                    )}
                  </div>
                  <div className="flex-1">
                    <p className="text-sm font-medium text-gray-900">{activity.message}</p>
                    <p className="text-xs text-gray-500 mt-1">{activity.time}</p>
                  </div>
                </div>
              ))}
            </div>
          </Card>

          {/* Quick Actions */}
          <Card className="p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-6">Actions rapides</h2>
            <div className="space-y-3">
              <Button
                variant="outline"
                className="w-full justify-start"
                icon={<UsersIcon className="w-5 h-5" />}
                onClick={() => alert('Gestion des utilisateurs - À implémenter')}
              >
                Gérer les utilisateurs
              </Button>
              <Button
                variant="outline"
                className="w-full justify-start"
                icon={<ShoppingBagIcon className="w-5 h-5" />}
                onClick={() => alert('Gestion des produits - À implémenter')}
              >
                Gérer les produits
              </Button>
              <Button
                variant="outline"
                className="w-full justify-start"
                icon={<ClipboardDocumentListIcon className="w-5 h-5" />}
                onClick={() => alert('Gestion des commandes - À implémenter')}
              >
                Gérer les commandes
              </Button>
              <Button
                variant="outline"
                className="w-full justify-start"
                icon={<ExclamationTriangleIcon className="w-5 h-5" />}
                onClick={() => alert('Signalements - À implémenter')}
              >
                Traiter les signalements
              </Button>
              <Button
                variant="outline"
                className="w-full justify-start"
                icon={<ChartBarIcon className="w-5 h-5" />}
                onClick={() => alert('Statistiques - À implémenter')}
              >
                Voir les statistiques détaillées
              </Button>
            </div>
          </Card>
        </div>

        {/* Info Message */}
        <div className="mt-8">
          <Card className="p-4 bg-blue-50 border-blue-200">
            <div className="flex items-start gap-3">
              <div className="p-2 bg-blue-100 rounded-full">
                <ClipboardDocumentListIcon className="w-5 h-5 text-blue-600" />
              </div>
              <div>
                <h3 className="text-sm font-semibold text-blue-900">
                  Dashboard en développement
                </h3>
                <p className="text-sm text-blue-700 mt-1">
                  Cette page est une version simplifiée du dashboard administrateur.
                  Les fonctionnalités complètes de gestion seront ajoutées progressivement.
                </p>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboardPage;


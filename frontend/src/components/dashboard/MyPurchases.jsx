import React, { useState, useEffect } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import orderService from '../../services/orderService';
import Card from '../common/Card';
import Button from '../common/Button';
import {
  TruckIcon,
  CheckCircleIcon,
  ClockIcon,
  XCircleIcon,
  EyeIcon,
  ChatBubbleLeftIcon
} from '@heroicons/react/24/outline';

const MyPurchases = () => {
  const { user } = useAuth();
  const { handleError, showSuccess } = useErrorHandler();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL'); // ALL, PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

  useEffect(() => {
    loadOrders();
  }, [user]);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const data = await orderService.getBuyerOrders(user.id);
      console.debug('MyPurchases.loadOrders: raw response:', data);
      // Handle common API shapes: array, { content: [...] }, { data: [...] }
      if (data && Array.isArray(data.content)) {
        setOrders(data.content);
      } else if (data && Array.isArray(data.data)) {
        setOrders(data.data);
      } else if (Array.isArray(data)) {
        setOrders(data);
      } else if (data) {
        // Some endpoints return a single object; coerce to array
        setOrders([data]);
      } else {
        setOrders([]);
      }
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  // Defensive: always treat orders as an array
  const ordersArray = Array.isArray(orders) ? orders : [];
  const filteredOrders = filter === 'ALL'
    ? ordersArray
    : ordersArray.filter(order => {
        // order.status can be an object or string; normalize to string
        const s = order?.status && typeof order.status === 'string' ? order.status : (order?.status?.name || '');
        return s === filter;
      });

  const getStatusConfig = (status) => {
    const configs = {
      PENDING: {
        label: 'En attente',
        icon: ClockIcon,
        class: 'bg-yellow-100 text-yellow-800 border-yellow-200'
      },
      CONFIRMED: {
        label: 'Confirmée',
        icon: CheckCircleIcon,
        class: 'bg-blue-100 text-blue-800 border-blue-200'
      },
      SHIPPED: {
        label: 'Expédiée',
        icon: TruckIcon,
        class: 'bg-purple-100 text-purple-800 border-purple-200'
      },
      DELIVERED: {
        label: 'Livrée',
        icon: CheckCircleIcon,
        class: 'bg-green-100 text-green-800 border-green-200'
      },
      CANCELLED: {
        label: 'Annulée',
        icon: XCircleIcon,
        class: 'bg-red-100 text-red-800 border-red-200'
      }
    };

    return configs[status] || {
      label: status,
      icon: ClockIcon,
      class: 'bg-gray-100 text-gray-800 border-gray-200'
    };
  };

  const handleConfirmDelivery = async (orderId) => {
    try {
      await orderService.confirmDelivery(orderId);
      showSuccess('Livraison confirmée');
      loadOrders();
    } catch (error) {
      handleError(error);
    }
  };

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir annuler cette commande ?')) {
      return;
    }

    try {
      await orderService.cancelOrder(orderId);
      showSuccess('Commande annulée');
      loadOrders();
    } catch (error) {
      handleError(error);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Info et filtres */}
      <div className="flex flex-col gap-4">
        <div className="flex justify-end">
          <p className="text-gray-600">{orders.length} commande(s)</p>
        </div>

        {/* Filtres */}
        <div className="flex gap-2 overflow-x-auto pb-2">
          <button
            onClick={() => setFilter('ALL')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap transition-colors ${
              filter === 'ALL'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Toutes ({orders.length})
          </button>
          <button
            onClick={() => setFilter('PENDING')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap transition-colors ${
              filter === 'PENDING'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            En attente
          </button>
          <button
            onClick={() => setFilter('CONFIRMED')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap transition-colors ${
              filter === 'CONFIRMED'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Confirmées
          </button>
          <button
            onClick={() => setFilter('SHIPPED')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap transition-colors ${
              filter === 'SHIPPED'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Expédiées
          </button>
          <button
            onClick={() => setFilter('DELIVERED')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap transition-colors ${
              filter === 'DELIVERED'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Livrées
          </button>
        </div>
      </div>

      {/* Liste des commandes */}
      {filteredOrders.length === 0 ? (
        <Card className="p-12 text-center">
          <div className="text-gray-400 text-6xl mb-4">🛍️</div>
          <h3 className="text-xl font-semibold text-gray-700 mb-2">
            Aucun achat trouvé
          </h3>
          <p className="text-gray-500 mb-6">
            {filter === 'ALL'
              ? "Vous n'avez pas encore effectué d'achat"
              : `Vous n'avez pas de commande avec ce statut`}
          </p>
          {filter === 'ALL' && (
            <Button variant="primary" onClick={() => window.location.hash = '#/search'}>
              Parcourir les objets
            </Button>
          )}
        </Card>
      ) : (
        <div className="space-y-4">
          {filteredOrders.map((order) => {
            const statusConfig = getStatusConfig(order.status);
            const StatusIcon = statusConfig.icon;

            return (
              <Card key={order.id} className="p-6 hover:shadow-lg transition-shadow">
                <div className="flex flex-col md:flex-row gap-6">
                  {/* Image du produit */}
                  <div className="w-full md:w-32 h-32 bg-gray-200 rounded-lg overflow-hidden flex-shrink-0">
                    {order.product?.photos && order.product.photos.length > 0 ? (
                      <img
                        src={order.product.photos[0].url}
                        alt={order.product.title}
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="flex items-center justify-center h-full text-gray-400">
                        <span className="text-3xl">📦</span>
                      </div>
                    )}
                  </div>

                  {/* Détails */}
                  <div className="flex-1 space-y-3">
                    {/* Header */}
                    <div className="flex justify-between items-start">
                      <div>
                        <h3 className="font-semibold text-lg text-gray-900">
                          {order.product?.title || 'Produit'}
                        </h3>
                        <p className="text-sm text-gray-600">
                          Commande n° {order.orderNumber}
                        </p>
                      </div>

                      <div className={`flex items-center gap-2 px-3 py-1 rounded-full border ${statusConfig.class}`}>
                        <StatusIcon className="w-4 h-4" />
                        <span className="text-sm font-medium">{statusConfig.label}</span>
                      </div>
                    </div>

                    {/* Info vendeur */}
                    <div className="flex items-center gap-4 text-sm">
                      <div>
                        <span className="text-gray-500">Vendeur : </span>
                        <span className="font-medium text-gray-900">
                          {order.seller?.firstName} {order.seller?.lastName}
                        </span>
                      </div>
                      <div>
                        <span className="text-gray-500">Date : </span>
                        <span className="font-medium text-gray-900">
                          {new Date(order.createdAt).toLocaleDateString('fr-FR')}
                        </span>
                      </div>
                    </div>

                    {/* Prix */}
                    <div className="flex items-center gap-2">
                      <span className="text-2xl font-bold text-purple-600">
                        {order.totalAmount?.toLocaleString('fr-FR')} €
                      </span>
                    </div>

                    {/* Adresse de livraison */}
                    {order.shippingAddress && (
                      <div className="text-sm">
                        <span className="text-gray-500">Livraison : </span>
                        <span className="text-gray-900">
                          {order.shippingAddress.street}, {order.shippingAddress.city}
                        </span>
                      </div>
                    )}

                    {/* Actions */}
                    <div className="flex gap-2 pt-3 border-t">
                      <button
                        className="flex items-center gap-2 px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition-colors"
                      >
                        <EyeIcon className="w-4 h-4" />
                        <span className="text-sm font-medium">Détails</span>
                      </button>

                      <button
                        className="flex items-center gap-2 px-4 py-2 bg-blue-100 hover:bg-blue-200 text-blue-700 rounded-lg transition-colors"
                      >
                        <ChatBubbleLeftIcon className="w-4 h-4" />
                        <span className="text-sm font-medium">Contacter</span>
                      </button>

                      {order.status === 'SHIPPED' && (
                        <button
                          onClick={() => handleConfirmDelivery(order.id)}
                          className="flex items-center gap-2 px-4 py-2 bg-green-100 hover:bg-green-200 text-green-700 rounded-lg transition-colors"
                        >
                          <CheckCircleIcon className="w-4 h-4" />
                          <span className="text-sm font-medium">Confirmer réception</span>
                        </button>
                      )}

                      {order.status === 'PENDING' && (
                        <button
                          onClick={() => handleCancelOrder(order.id)}
                          className="flex items-center gap-2 px-4 py-2 bg-red-100 hover:bg-red-200 text-red-700 rounded-lg transition-colors ml-auto"
                        >
                          <XCircleIcon className="w-4 h-4" />
                          <span className="text-sm font-medium">Annuler</span>
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              </Card>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default MyPurchases;


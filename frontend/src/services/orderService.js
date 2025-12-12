import api from './api';

const orderService = {
    // Créer une nouvelle commande
    createOrder: async (orderData) => {
        const response = await api.post('/orders', orderData);
        return response.data;
    },

    // Récupérer une commande par ID
    getOrderById: async (orderId) => {
        const response = await api.get(`/orders/${orderId}`);
        return response.data;
    },

    // Récupérer une commande par numéro
    getOrderByNumber: async (orderNumber) => {
        const response = await api.get(`/orders/number/${orderNumber}`);
        return response.data;
    },

    // Récupérer toutes les commandes d'un acheteur
    getBuyerOrders: async (buyerId) => {
        const response = await api.get(`/orders/buyer/${buyerId}`);
        return response.data;
    },

    // Récupérer toutes les commandes d'un vendeur
    getSellerOrders: async (sellerId) => {
        const response = await api.get(`/orders/seller/${sellerId}`);
        return response.data;
    },

    // Récupérer toutes les commandes d'une personne (acheteur ou vendeur)
    getPersonOrders: async (personId) => {
        const response = await api.get(`/orders/person/${personId}`);
        return response.data;
    },

    // Mettre à jour une commande
    updateOrder: async (orderId, orderData) => {
        const response = await api.put(`/orders/${orderId}`, orderData);
        return response.data;
    },

    // Mettre à jour le statut d'une commande
    updateOrderStatus: async (orderId, status) => {
        const response = await api.patch(`/orders/${orderId}/status`, null, {
            params: { status }
        });
        return response.data;
    },

    // Annuler une commande
    cancelOrder: async (orderId) => {
        const response = await api.patch(`/orders/${orderId}/cancel`);
        return response.data;
    }
};

export default orderService;

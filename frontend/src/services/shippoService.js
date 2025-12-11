import api from './api';

const shippoService = {
    // Créer un shipment et obtenir les tarifs de livraison
    createShipmentAndGetRates: async (shipmentData) => {
        const response = await api.post('/shippo/create-shipment', shipmentData);
        return response.data;
    },

    // Acheter une étiquette de livraison
    purchaseLabel: async (deliveryId, labelData) => {
        const response = await api.post(`/shippo/deliveries/${deliveryId}/purchase-label`, labelData);
        return response.data;
    },

    // Suivre une livraison
    trackDelivery: async (deliveryId) => {
        const response = await api.get(`/shippo/deliveries/${deliveryId}/track`);
        return response.data;
    },

    // Obtenir les détails d'une livraison
    getDelivery: async (deliveryId) => {
        const response = await api.get(`/shippo/deliveries/${deliveryId}`);
        return response.data;
    },

    // Obtenir toutes les livraisons d'une commande
    getOrderDeliveries: async (orderId) => {
        const response = await api.get(`/shippo/orders/${orderId}/deliveries`);
        return response.data;
    }
};

export default shippoService;


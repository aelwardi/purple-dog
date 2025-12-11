import api from './api';

const paymentService = {
    // Créer un Payment Intent Stripe
    createPaymentIntent: async (paymentData) => {
        // Le backend attend: { amount, currency, orderId?, description?, paymentMethodId?, savePaymentMethod? }
        // Le userId est récupéré du token JWT côté backend
        const response = await api.post('/payments/create-intent', {
            amount: paymentData.amount,
            currency: paymentData.currency || 'eur', // Backend attend minuscules
            orderId: paymentData.orderId,
            description: paymentData.description,
            paymentMethodId: paymentData.paymentMethodId,
            savePaymentMethod: paymentData.savePaymentMethod || false
        });
        return response.data;
    },

    // Confirmer un paiement
    confirmPayment: async (paymentIntentId) => {
        const response = await api.post('/payments/confirm', {
            paymentIntentId
        });
        return response.data;
    },

    // Annuler un paiement
    cancelPayment: async (paymentIntentId, reason) => {
        const response = await api.post('/payments/cancel', {
            paymentIntentId,
            cancellationReason: reason
        });
        return response.data;
    },

    // Récupérer un paiement par ID
    getPaymentById: async (paymentId) => {
        const response = await api.get(`/payments/${paymentId}`);
        return response.data;
    },

    // Récupérer les paiements de l'utilisateur connecté
    getMyPayments: async (page = 0, size = 20) => {
        const response = await api.get('/payments/my-payments', {
            params: { page, size }
        });
        return response.data;
    },

    // Créer un remboursement (Admin seulement)
    createRefund: async (paymentIntentId, amount, reason) => {
        const response = await api.post('/payments/refund', {
            paymentIntentId,
            amount,
            reason
        });
        return response.data;
    }
};

export default paymentService;


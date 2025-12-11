import api from './api';

const addressService = {
    // Récupérer mes adresses (utilisateur connecté)
    getMyAddresses: async () => {
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user || !user.id) {
            throw new Error('Utilisateur non connecté');
        }
        const response = await api.get(`/addresses/person/${user.id}`);
        return response;
    },

    // Créer une nouvelle adresse pour l'utilisateur connecté
    createAddress: async (addressData) => {
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user || !user.id) {
            throw new Error('Utilisateur non connecté');
        }
        const response = await api.post(`/addresses/person/${user.id}`, addressData);
        return response;
    },

    // Récupérer toutes les adresses d'un utilisateur
    getUserAddresses: async (personId) => {
        const response = await api.get(`/addresses/person/${personId}`);
        return response.data;
    },

    // Récupérer une adresse spécifique
    getAddressById: async (addressId, personId) => {
        const response = await api.get(`/addresses/${addressId}/person/${personId}`);
        return response.data;
    },

    // Récupérer l'adresse par défaut
    getDefaultAddress: async (personId) => {
        const response = await api.get(`/addresses/person/${personId}/default`);
        return response.data;
    },

    // Mettre à jour une adresse
    updateAddress: async (addressId, personId, addressData) => {
        const response = await api.put(`/addresses/${addressId}/person/${personId}`, addressData);
        return response.data;
    },

    // Définir une adresse comme adresse par défaut
    setDefaultAddress: async (addressId, personId) => {
        const response = await api.put(`/addresses/${addressId}/person/${personId}/set-default`);
        return response.data;
    },

    // Supprimer une adresse
    deleteAddress: async (addressId, personId) => {
        await api.delete(`/addresses/${addressId}/person/${personId}`);
    }
};

export default addressService;


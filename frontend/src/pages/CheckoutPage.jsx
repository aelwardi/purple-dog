import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
    MapPinIcon,
    PlusIcon,
    CheckIcon,
    CreditCardIcon,
    TruckIcon
} from '@heroicons/react/24/outline';
import { toast } from 'react-hot-toast';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import addressService from '../services/addressService';
import orderService from '../services/orderService';
import shippoService from '../services/shippoService';

const CheckoutPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { items, subtotal } = location.state || {};

    const [addresses, setAddresses] = useState([]);
    const [selectedShippingAddress, setSelectedShippingAddress] = useState(null);
    const [selectedBillingAddress, setSelectedBillingAddress] = useState(null);
    const [useSameAddress, setUseSameAddress] = useState(true);
    const [loading, setLoading] = useState(true);
    const [creatingOrder, setCreatingOrder] = useState(false);
    const [showAddressForm, setShowAddressForm] = useState(false);
    const [shippingRates, setShippingRates] = useState([]);
    const [selectedRate, setSelectedRate] = useState(null);
    const [loadingRates, setLoadingRates] = useState(false);
    const [newAddress, setNewAddress] = useState({
        label: '',
        street: '',
        complement: '',
        city: '',
        postalCode: '',
        country: 'France',
        isDefault: false
    });

    useEffect(() => {
        if (!items || items.length === 0) {
            toast.error('Votre panier est vide');
            navigate('/panier');
            return;
        }

        loadAddresses();
    }, []);

    // Calculer les tarifs Shippo quand une adresse est sélectionnée
    useEffect(() => {
        if (selectedShippingAddress && addresses.length > 0) {
            calculateShippingRates();
        }
    }, [selectedShippingAddress]);

    const loadAddresses = async () => {
        try {
            const user = JSON.parse(localStorage.getItem('user'));
            if (!user || !user.id) {
                toast.error('Utilisateur non connecté');
                navigate('/login');
                return;
            }

            const userAddresses = await addressService.getUserAddresses(user.id);
            setAddresses(userAddresses);

            // Sélectionner l'adresse par défaut si elle existe
            const defaultAddress = userAddresses.find(addr => addr.isDefault);
            if (defaultAddress) {
                setSelectedShippingAddress(defaultAddress.id);
                setSelectedBillingAddress(defaultAddress.id);
            }
        } catch (error) {
            console.error('Error loading addresses:', error);
            toast.error('Erreur lors du chargement des adresses');
        } finally {
            setLoading(false);
        }
    };

    const handleAddAddress = async () => {
        try {
            // Validation
            if (!newAddress.label || !newAddress.street || !newAddress.city || !newAddress.postalCode) {
                toast.error('Veuillez remplir tous les champs obligatoires');
                return;
            }

            const user = JSON.parse(localStorage.getItem('user'));
            const address = await addressService.createAddress(user.id, newAddress);
            setAddresses([...addresses, address]);
            setSelectedShippingAddress(address.id);
            setSelectedBillingAddress(address.id);
            setShowAddressForm(false);
            setNewAddress({
                label: '',
                street: '',
                complement: '',
                city: '',
                postalCode: '',
                country: 'France',
                isDefault: false
            });
            toast.success('Adresse ajoutée avec succès');
        } catch (error) {
            console.error('Error adding address:', error);
            toast.error(error.response?.data?.message || 'Erreur lors de l\'ajout de l\'adresse');
        }
    };

    const calculateShippingRates = async () => {
        setLoadingRates(true);
        setShippingRates([]);
        setSelectedRate(null);

        try {
            // Calculer poids et dimensions approximatifs basés sur les produits
            const totalWeight = items.length * 1.0; // 1 kg par produit en moyenne
            const packageDimensions = {
                length: 30,
                width: 25,
                height: 10 + (items.length * 2) // Augmente avec le nombre d'items
            };

            const shipmentData = {
                orderId: null, // Pas encore créé
                // Adresse d'expédition (plateforme)
                fromName: "Purple Dog",
                fromStreet: "10 Rue du Commerce",
                fromCity: "Paris",
                fromZip: "75001",
                fromCountry: "FR",
                fromPhone: "+33123456789",
                fromEmail: "contact@purpledog.com",
                // L'adresse de destination sera récupérée par le backend
                toAddressId: selectedShippingAddress,
                // Dimensions du colis
                length: packageDimensions.length,
                width: packageDimensions.width,
                height: packageDimensions.height,
                weight: totalWeight
            };

            const response = await shippoService.createShipmentAndGetRates(shipmentData);

            if (response.rates && response.rates.length > 0) {
                setShippingRates(response.rates);

                // Sélectionner automatiquement le tarif le moins cher
                const cheapestRate = response.rates.reduce((min, rate) =>
                    parseFloat(rate.amount) < parseFloat(min.amount) ? rate : min
                , response.rates[0]);

                setSelectedRate(cheapestRate);
                toast.success('Tarifs de livraison calculés');
            } else {
                // Fallback: Tarifs par défaut si Shippo échoue
                const defaultRates = [
                    {
                        rateId: 'default_standard',
                        provider: 'Standard',
                        servicelevel: { name: 'Standard', token: 'standard' },
                        amount: '15.00',
                        currency: 'EUR',
                        estimatedDays: 5,
                        durationTerms: '5-7 jours ouvrés'
                    },
                    {
                        rateId: 'default_express',
                        provider: 'Express',
                        servicelevel: { name: 'Express', token: 'express' },
                        amount: '25.00',
                        currency: 'EUR',
                        estimatedDays: 2,
                        durationTerms: '2-3 jours ouvrés'
                    }
                ];
                setShippingRates(defaultRates);
                setSelectedRate(defaultRates[0]);
                toast.info('Tarifs de livraison estimés');
            }
        } catch (error) {
            console.error('Error calculating shipping rates:', error);

            // Fallback: Tarifs par défaut
            const defaultRates = [
                {
                    rateId: 'default_standard',
                    provider: 'Standard',
                    servicelevel: { name: 'Standard', token: 'standard' },
                    amount: '15.00',
                    currency: 'EUR',
                    estimatedDays: 5,
                    durationTerms: '5-7 jours ouvrés'
                },
                {
                    rateId: 'default_express',
                    provider: 'Express',
                    servicelevel: { name: 'Express', token: 'express' },
                    amount: '25.00',
                    currency: 'EUR',
                    estimatedDays: 2,
                    durationTerms: '2-3 jours ouvrés'
                }
            ];
            setShippingRates(defaultRates);
            setSelectedRate(defaultRates[0]);
            toast.info('Tarifs de livraison estimés (Shippo indisponible)');
        } finally {
            setLoadingRates(false);
        }
    };

    const handleCreateOrder = async () => {
        if (!selectedShippingAddress) {
            toast.error('Veuillez sélectionner une adresse de livraison');
            return;
        }

        if (!useSameAddress && !selectedBillingAddress) {
            toast.error('Veuillez sélectionner une adresse de facturation');
            return;
        }

        if (!selectedRate) {
            toast.error('Veuillez patienter pendant le calcul des frais de livraison');
            return;
        }

        setCreatingOrder(true);

        try {
            const user = JSON.parse(localStorage.getItem('user'));
            const shippingCost = parseFloat(selectedRate.amount);

            // Pour chaque produit dans le panier, créer une commande
            const orders = [];

            for (const item of items) {
                const orderData = {
                    buyerId: user.id,
                    sellerId: item.seller.id,
                    productPrice: item.price,
                    shippingCost: shippingCost / items.length, // Répartir les frais de port
                    platformFee: (item.price * 0.05), // 5% de frais de plateforme
                    shippingAddressId: selectedShippingAddress,
                    billingAddressId: useSameAddress ? selectedShippingAddress : selectedBillingAddress
                };

                const order = await orderService.createOrder(orderData);
                orders.push(order);
            }

            // Vider le panier
            localStorage.setItem('cart', JSON.stringify([]));
            window.dispatchEvent(new Event('cartUpdated'));

            toast.success('Commande créée avec succès !');

            // Calculer le total final
            const total = subtotal + shippingCost;

            // Rediriger vers la page de paiement
            navigate('/payment', {
                state: {
                    orders,
                    total
                }
            });

        } catch (error) {
            console.error('Error creating order:', error);
            toast.error(error.response?.data?.message || 'Erreur lors de la création de la commande');
        } finally {
            setCreatingOrder(false);
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'EUR'
        }).format(price || 0);
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-8">Finaliser ma commande</h1>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Formulaire */}
                    <div className="lg:col-span-2 space-y-6">
                        {/* Adresse de livraison */}
                        <Card className="p-6">
                            <div className="flex items-center justify-between mb-6">
                                <h2 className="text-xl font-semibold text-gray-900 flex items-center gap-2">
                                    <TruckIcon className="w-6 h-6 text-purple-600" />
                                    Adresse de livraison
                                </h2>
                                <Button
                                    variant="secondary"
                                    size="small"
                                    onClick={() => setShowAddressForm(!showAddressForm)}
                                    icon={<PlusIcon className="w-4 h-4" />}
                                >
                                    Nouvelle adresse
                                </Button>
                            </div>

                            {/* Formulaire nouvelle adresse */}
                            {showAddressForm && (
                                <div className="mb-6 p-4 bg-gray-50 rounded-lg space-y-4">
                                    <input
                                        type="text"
                                        placeholder="Libellé (ex: Maison, Bureau)"
                                        value={newAddress.label}
                                        onChange={(e) => setNewAddress({...newAddress, label: e.target.value})}
                                        className="w-full p-3 border rounded-lg"
                                    />
                                    <input
                                        type="text"
                                        placeholder="Rue et numéro"
                                        value={newAddress.street}
                                        onChange={(e) => setNewAddress({...newAddress, street: e.target.value})}
                                        className="w-full p-3 border rounded-lg"
                                    />
                                    <input
                                        type="text"
                                        placeholder="Complément d'adresse (optionnel)"
                                        value={newAddress.complement}
                                        onChange={(e) => setNewAddress({...newAddress, complement: e.target.value})}
                                        className="w-full p-3 border rounded-lg"
                                    />
                                    <div className="grid grid-cols-2 gap-4">
                                        <input
                                            type="text"
                                            placeholder="Ville"
                                            value={newAddress.city}
                                            onChange={(e) => setNewAddress({...newAddress, city: e.target.value})}
                                            className="w-full p-3 border rounded-lg"
                                        />
                                        <input
                                            type="text"
                                            placeholder="Code postal"
                                            value={newAddress.postalCode}
                                            onChange={(e) => setNewAddress({...newAddress, postalCode: e.target.value})}
                                            className="w-full p-3 border rounded-lg"
                                        />
                                    </div>
                                    <input
                                        type="text"
                                        placeholder="Pays"
                                        value={newAddress.country}
                                        onChange={(e) => setNewAddress({...newAddress, country: e.target.value})}
                                        className="w-full p-3 border rounded-lg"
                                    />
                                    <Button
                                        variant="primary"
                                        onClick={handleAddAddress}
                                        className="w-full"
                                    >
                                        Ajouter l'adresse
                                    </Button>
                                </div>
                            )}

                            {/* Liste des adresses */}
                            {addresses.length === 0 ? (
                                <div className="text-center py-8 text-gray-500">
                                    <MapPinIcon className="w-12 h-12 mx-auto mb-2 text-gray-300" />
                                    <p>Aucune adresse enregistrée</p>
                                    <p className="text-sm">Ajoutez une adresse pour continuer</p>
                                </div>
                            ) : (
                                <div className="space-y-3">
                                    {addresses.map((address) => (
                                        <div
                                            key={address.id}
                                            onClick={() => setSelectedShippingAddress(address.id)}
                                            className={`p-4 border-2 rounded-lg cursor-pointer transition-all ${
                                                selectedShippingAddress === address.id
                                                    ? 'border-purple-600 bg-purple-50'
                                                    : 'border-gray-200 hover:border-gray-300'
                                            }`}
                                        >
                                            <div className="flex items-start justify-between">
                                                <div className="flex-1">
                                                    <p className="font-semibold text-gray-900 mb-1">{address.label}</p>
                                                    <p className="text-sm text-gray-700">{address.street}</p>
                                                    {address.complement && (
                                                        <p className="text-sm text-gray-700">{address.complement}</p>
                                                    )}
                                                    <p className="text-sm text-gray-600">
                                                        {address.postalCode} {address.city}, {address.country}
                                                    </p>
                                                    {address.isDefault && (
                                                        <span className="inline-block mt-2 px-2 py-1 bg-purple-100 text-purple-700 text-xs rounded">
                                                            Par défaut
                                                        </span>
                                                    )}
                                                </div>
                                                {selectedShippingAddress === address.id && (
                                                    <CheckIcon className="w-6 h-6 text-purple-600" />
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </Card>

                        {/* Adresse de facturation */}
                        <Card className="p-6">
                            <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center gap-2">
                                <MapPinIcon className="w-6 h-6 text-purple-600" />
                                Adresse de facturation
                            </h2>

                            <label className="flex items-center gap-2 mb-4 cursor-pointer">
                                <input
                                    type="checkbox"
                                    checked={useSameAddress}
                                    onChange={(e) => setUseSameAddress(e.target.checked)}
                                    className="w-4 h-4 text-purple-600 rounded"
                                />
                                <span className="text-gray-700">Identique à l'adresse de livraison</span>
                            </label>

                            {!useSameAddress && (
                                <div className="space-y-3">
                                    {addresses.map((address) => (
                                        <div
                                            key={address.id}
                                            onClick={() => setSelectedBillingAddress(address.id)}
                                            className={`p-4 border-2 rounded-lg cursor-pointer transition-all ${
                                                selectedBillingAddress === address.id
                                                    ? 'border-purple-600 bg-purple-50'
                                                    : 'border-gray-200 hover:border-gray-300'
                                            }`}
                                        >
                                            <div className="flex items-start justify-between">
                                                <div className="flex-1">
                                                    <p className="font-semibold text-gray-900 mb-1">{address.label}</p>
                                                    <p className="text-sm text-gray-700">{address.street}</p>
                                                    {address.complement && (
                                                        <p className="text-sm text-gray-700">{address.complement}</p>
                                                    )}
                                                    <p className="text-sm text-gray-600">
                                                        {address.postalCode} {address.city}, {address.country}
                                                    </p>
                                                </div>
                                                {selectedBillingAddress === address.id && (
                                                    <CheckIcon className="w-6 h-6 text-purple-600" />
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </Card>

                        {/* Options de livraison Shippo */}
                        {selectedShippingAddress && (
                            <Card className="p-6">
                                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center gap-2">
                                    <TruckIcon className="w-6 h-6 text-purple-600" />
                                    Options de livraison
                                </h2>

                                {loadingRates ? (
                                    <div className="flex items-center justify-center py-8">
                                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600"></div>
                                        <span className="ml-3 text-gray-600">Calcul des tarifs de livraison...</span>
                                    </div>
                                ) : shippingRates.length > 0 ? (
                                    <div className="space-y-3">
                                        {shippingRates.map((rate) => (
                                            <label
                                                key={rate.rateId}
                                                className={`flex items-center p-4 border-2 rounded-lg cursor-pointer transition-all ${
                                                    selectedRate?.rateId === rate.rateId
                                                        ? 'border-purple-600 bg-purple-50'
                                                        : 'border-gray-200 hover:border-gray-300'
                                                }`}
                                            >
                                                <input
                                                    type="radio"
                                                    name="shippingRate"
                                                    checked={selectedRate?.rateId === rate.rateId}
                                                    onChange={() => setSelectedRate(rate)}
                                                    className="w-4 h-4 text-purple-600"
                                                />
                                                <div className="flex-1 ml-4">
                                                    <div className="flex items-center justify-between mb-1">
                                                        <p className="font-semibold text-gray-900">
                                                            {rate.provider}
                                                            {rate.servicelevel?.name && ` - ${rate.servicelevel.name}`}
                                                        </p>
                                                        <p className="text-lg font-bold text-purple-600">
                                                            {formatPrice(parseFloat(rate.amount))}
                                                        </p>
                                                    </div>
                                                    {rate.durationTerms && (
                                                        <p className="text-sm text-gray-600">{rate.durationTerms}</p>
                                                    )}
                                                    {rate.estimatedDays && (
                                                        <p className="text-xs text-gray-500 mt-1">
                                                            Livraison estimée : {rate.estimatedDays} jour{rate.estimatedDays > 1 ? 's' : ''}
                                                        </p>
                                                    )}
                                                </div>
                                            </label>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="text-center py-8 text-gray-500">
                                        <p>Sélectionnez une adresse de livraison pour voir les options disponibles</p>
                                    </div>
                                )}
                            </Card>
                        )}
                    </div>

                    {/* Récapitulatif */}
                    <div>
                        <Card className="p-6 sticky top-4">
                            <h2 className="text-xl font-semibold text-gray-900 mb-4">Récapitulatif</h2>

                            <div className="space-y-3 mb-4">
                                {items?.map((item) => (
                                    <div key={item.id} className="flex justify-between text-sm">
                                        <span className="text-gray-600 truncate pr-2">{item.title}</span>
                                        <span className="text-gray-900 font-medium">{formatPrice(item.price)}</span>
                                    </div>
                                ))}
                            </div>

                            <div className="border-t pt-3 space-y-2 mb-4">
                                <div className="flex justify-between text-gray-600">
                                    <span>Sous-total</span>
                                    <span>{formatPrice(subtotal)}</span>
                                </div>
                                <div className="flex justify-between text-gray-600">
                                    <span>Livraison</span>
                                    <span>
                                        {selectedRate ? formatPrice(parseFloat(selectedRate.amount)) : 'En cours...'}
                                    </span>
                                </div>
                                <div className="flex justify-between text-xl font-bold text-gray-900 pt-2 border-t">
                                    <span>Total</span>
                                    <span>
                                        {selectedRate ? formatPrice(subtotal + parseFloat(selectedRate.amount)) : formatPrice(subtotal)}
                                    </span>
                                </div>
                            </div>

                            <Button
                                variant="primary"
                                size="large"
                                className="w-full"
                                onClick={handleCreateOrder}
                                disabled={creatingOrder || !selectedShippingAddress || !selectedRate}
                                icon={<CreditCardIcon className="w-5 h-5" />}
                            >
                                {creatingOrder ? 'Création en cours...' : 'Procéder au paiement'}
                            </Button>

                            {!selectedRate && selectedShippingAddress && (
                                <p className="text-xs text-orange-600 text-center mt-2">
                                    ⏳ Calcul des frais de livraison en cours...
                                </p>
                            )}

                            <p className="text-xs text-gray-500 text-center mt-4">
                                En continuant, vous acceptez nos conditions générales de vente
                            </p>
                        </Card>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CheckoutPage;


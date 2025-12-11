import React, { useState, useEffect, Fragment } from 'react';
import { Dialog, Transition } from '@headlessui/react';
import {
    XMarkIcon,
    MapPinIcon,
    PlusIcon,
    CheckIcon,
    TruckIcon,
    ShoppingBagIcon,
    CreditCardIcon
} from '@heroicons/react/24/outline';
import { toast } from 'react-hot-toast';
import Button from '../common/Button';
import Card from '../common/Card';
import PaymentModal from '../payment/PaymentModal';
import addressService from '../../services/addressService';
import orderService from '../../services/orderService';
import shippoService from '../../services/shippoService';

const CheckoutModal = ({ isOpen, onClose, items, subtotal, onOrderCreated }) => {
    const [currentStep, setCurrentStep] = useState(1);
    const [addresses, setAddresses] = useState([]);
    const [selectedShippingAddress, setSelectedShippingAddress] = useState(null);
    const [loading, setLoading] = useState(false);
    const [showAddressForm, setShowAddressForm] = useState(false);
    const [shippingRates, setShippingRates] = useState([]);
    const [selectedRate, setSelectedRate] = useState(null);
    const [loadingRates, setLoadingRates] = useState(false);
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [createdOrders, setCreatedOrders] = useState([]);

    const [newAddress, setNewAddress] = useState({
        label: '',
        street: '',
        complement: '',
        city: '',
        postalCode: '',
        country: 'France',
        isDefault: false
    });

    const steps = [
        { number: 1, title: 'Adresse de livraison', icon: MapPinIcon },
        { number: 2, title: 'Mode de livraison', icon: TruckIcon },
        { number: 3, title: 'Récapitulatif', icon: ShoppingBagIcon }
    ];

    useEffect(() => {
        if (isOpen) {
            loadAddresses();
        }
    }, [isOpen]);

    useEffect(() => {
        if (selectedShippingAddress && currentStep === 2) {
            calculateShippingRates();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selectedShippingAddress, currentStep]);

    const loadAddresses = async () => {
        setLoading(true);
        try {
            const response = await addressService.getMyAddresses();
            setAddresses(response.data || []);

            // Sélectionner l'adresse par défaut
            const defaultAddress = response.data?.find(addr => addr.isDefault);
            if (defaultAddress) {
                setSelectedShippingAddress(defaultAddress.id);
            }
        } catch (error) {
            console.error('Error loading addresses:', error);
            toast.error('Erreur lors du chargement des adresses');
        } finally {
            setLoading(false);
        }
    };

    const handleAddAddress = async () => {
        if (!newAddress.label || !newAddress.street || !newAddress.city || !newAddress.postalCode) {
            toast.error('Veuillez remplir tous les champs obligatoires');
            return;
        }

        // Validation code postal (min 5 caractères)
        if (newAddress.postalCode.length < 5) {
            toast.error('Le code postal doit contenir au moins 5 caractères');
            return;
        }

        try {
            console.log('📤 Envoi de l\'adresse:', newAddress);
            const response = await addressService.createAddress(newAddress);
            console.log('✅ Réponse reçue:', response);

            const createdAddress = response.data;
            if (!createdAddress) {
                throw new Error('Pas de données dans la réponse');
            }

            setAddresses([...addresses, createdAddress]);
            setSelectedShippingAddress(createdAddress.id);
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
            console.error('❌ Erreur complète:', error);
            console.error('❌ Réponse erreur:', error.response?.data);

            const errorMessage = error.response?.data?.message ||
                                error.response?.data?.error ||
                                error.message ||
                                'Erreur lors de l\'ajout de l\'adresse';

            toast.error(errorMessage);
        }
    };

    const calculateShippingRates = async () => {
        setLoadingRates(true);
        setShippingRates([]);
        setSelectedRate(null);

        try {
            const totalWeight = items.length * 1.0;
            const packageDimensions = {
                length: 30,
                width: 25,
                height: 10 + (items.length * 2)
            };

            const shipmentData = {
                orderId: null,
                fromName: "Purple Dog",
                fromStreet: "10 Rue du Commerce",
                fromCity: "Paris",
                fromZip: "75001",
                fromCountry: "FR",
                fromPhone: "+33123456789",
                fromEmail: "contact@purpledog.com",
                toAddressId: selectedShippingAddress,
                length: packageDimensions.length,
                width: packageDimensions.width,
                height: packageDimensions.height,
                weight: totalWeight
            };

            const response = await shippoService.createShipmentAndGetRates(shipmentData);

            if (response.rates && response.rates.length > 0) {
                setShippingRates(response.rates);
                setSelectedRate(response.rates[0]);
            }
        } catch (error) {
            console.error('Error calculating shipping rates:', error);
            // Tarifs par défaut en cas d'erreur
            const defaultRates = [
                {
                    rateId: 'default_standard',
                    provider: 'Standard',
                    servicelevel: { name: 'Standard', token: 'standard' },
                    amount: '8.50',
                    currency: 'EUR',
                    estimatedDays: 5,
                    durationTerms: '5-7 jours ouvrés'
                },
                {
                    rateId: 'default_express',
                    provider: 'Express',
                    servicelevel: { name: 'Express', token: 'express' },
                    amount: '15.00',
                    currency: 'EUR',
                    estimatedDays: 2,
                    durationTerms: '2-3 jours ouvrés'
                }
            ];
            setShippingRates(defaultRates);
            setSelectedRate(defaultRates[0]);
        } finally {
            setLoadingRates(false);
        }
    };

    const handleNextStep = () => {
        if (currentStep === 1 && !selectedShippingAddress) {
            toast.error('Veuillez sélectionner une adresse de livraison');
            return;
        }
        if (currentStep === 2 && !selectedRate) {
            toast.error('Veuillez sélectionner un mode de livraison');
            return;
        }
        if (currentStep < 3) {
            setCurrentStep(currentStep + 1);
        } else {
            handleCreateOrder();
        }
    };

    const handleCreateOrder = async () => {
        setLoading(true);

        try {
            const user = JSON.parse(localStorage.getItem('user'));
            const shippingCost = parseFloat(selectedRate.amount);
            const orders = [];

            for (const item of items) {
                const orderData = {
                    buyerId: user.id,
                    sellerId: item.seller?.id || item.sellerId,
                    quickSaleId: item.id,
                    productPrice: item.price,
                    shippingCost: shippingCost / items.length,
                    platformFee: (item.price * 0.05),
                    shippingAddressId: selectedShippingAddress,
                    billingAddressId: selectedShippingAddress
                };

                const response = await orderService.createOrder(orderData);
                console.log('📦 Order créée, réponse:', response);
                console.log('📦 response.data:', response.data);

                // La réponse peut être response.data.data ou directement response.data
                const orderCreated = response.data?.data || response.data || response;
                console.log('📦 Order finale:', orderCreated);

                orders.push(orderCreated);
            }

            console.log('✅ Toutes les commandes créées:', orders);
            toast.success(`${orders.length} commande(s) créée(s) avec succès`);

            // Validation avant d'ouvrir le modal de paiement
            if (orders.length === 0) {
                toast.error('Aucune commande créée');
                return;
            }

            // Vérifier que la première commande a un ID
            if (!orders[0]?.id) {
                console.error('❌ Première commande sans ID:', orders[0]);
                toast.error('Erreur : commande sans identifiant');
                return;
            }

            const total = totalWithShipping;
            if (!total || total <= 0 || isNaN(total)) {
                toast.error('Montant total invalide');
                return;
            }

            console.log('Ouverture PaymentModal avec:', {
                ordersCount: orders.length,
                total: total,
                firstOrderId: orders[0]?.id
            });

            // Sauvegarder les commandes et ouvrir le modal de paiement
            setCreatedOrders(orders);
            setShowPaymentModal(true);

        } catch (error) {
            console.error('Error creating orders:', error);
            toast.error('Erreur lors de la création de la commande');
        } finally {
            setLoading(false);
        }
    };

    const handlePaymentSuccess = () => {
        // Vider le panier
        localStorage.setItem('cart', JSON.stringify([]));
        window.dispatchEvent(new Event('cartUpdated'));

        // Callback avec les commandes
        if (onOrderCreated) {
            onOrderCreated(createdOrders);
        }

        // Fermer le modal de paiement
        setShowPaymentModal(false);

        // Fermer le modal de checkout
        onClose();

        toast.success('Commande finalisée avec succès !');
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'EUR'
        }).format(price || 0);
    };

    const totalWithShipping = subtotal + (selectedRate ? parseFloat(selectedRate.amount) : 0);

    return (
        <Transition appear show={isOpen} as={Fragment}>
            <Dialog as="div" className="relative z-50" onClose={onClose}>
                <Transition.Child
                    as={Fragment}
                    enter="ease-out duration-300"
                    enterFrom="opacity-0"
                    enterTo="opacity-100"
                    leave="ease-in duration-200"
                    leaveFrom="opacity-100"
                    leaveTo="opacity-0"
                >
                    <div className="fixed inset-0 bg-black bg-opacity-30 backdrop-blur-sm" />
                </Transition.Child>

                <div className="fixed inset-0 overflow-y-auto">
                    <div className="flex min-h-full items-center justify-center p-4">
                        <Transition.Child
                            as={Fragment}
                            enter="ease-out duration-300"
                            enterFrom="opacity-0 scale-95"
                            enterTo="opacity-100 scale-100"
                            leave="ease-in duration-200"
                            leaveFrom="opacity-100 scale-100"
                            leaveTo="opacity-0 scale-95"
                        >
                            <Dialog.Panel className="w-full max-w-4xl transform overflow-hidden rounded-2xl bg-white shadow-2xl transition-all">
                                {/* Header */}
                                <div className="border-b border-gray-200 bg-gradient-to-r from-purple-600 to-purple-700 px-6 py-4">
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center gap-3">
                                            <div className="p-2 bg-white/20 rounded-lg">
                                                <ShoppingBagIcon className="w-6 h-6 text-white" />
                                            </div>
                                            <div>
                                                <Dialog.Title className="text-xl font-bold text-white">
                                                    Finaliser ma commande
                                                </Dialog.Title>
                                                <p className="text-purple-100 text-sm">
                                                    {items?.length || 0} article{items?.length > 1 ? 's' : ''} • {formatPrice(subtotal)}
                                                </p>
                                            </div>
                                        </div>
                                        <button
                                            onClick={onClose}
                                            className="p-2 hover:bg-white/20 rounded-lg transition-colors"
                                        >
                                            <XMarkIcon className="w-6 h-6 text-white" />
                                        </button>
                                    </div>

                                    {/* Steps */}
                                    <div className="mt-6 flex items-center justify-between">
                                        {steps.map((step, index) => (
                                            <div key={step.number} className="flex items-center flex-1">
                                                <div className="flex items-center gap-3">
                                                    <div
                                                        className={`flex items-center justify-center w-10 h-10 rounded-full border-2 transition-all ${
                                                            currentStep >= step.number
                                                                ? 'bg-white border-white text-purple-600'
                                                                : 'bg-transparent border-white/50 text-white/50'
                                                        }`}
                                                    >
                                                        {currentStep > step.number ? (
                                                            <CheckIcon className="w-5 h-5" />
                                                        ) : (
                                                            <step.icon className="w-5 h-5" />
                                                        )}
                                                    </div>
                                                    <div className="hidden sm:block">
                                                        <p
                                                            className={`text-sm font-medium ${
                                                                currentStep >= step.number ? 'text-white' : 'text-white/50'
                                                            }`}
                                                        >
                                                            {step.title}
                                                        </p>
                                                    </div>
                                                </div>
                                                {index < steps.length - 1 && (
                                                    <div
                                                        className={`flex-1 h-0.5 mx-4 transition-all ${
                                                            currentStep > step.number ? 'bg-white' : 'bg-white/30'
                                                        }`}
                                                    />
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>

                                {/* Content */}
                                <div className="p-6 max-h-[60vh] overflow-y-auto">
                                    {/* Step 1: Adresse de livraison */}
                                    {currentStep === 1 && (
                                        <div className="space-y-4">
                                            <div className="flex items-center justify-between">
                                                <h3 className="text-lg font-semibold text-gray-900">
                                                    Adresse de livraison
                                                </h3>
                                                <Button
                                                    variant="secondary"
                                                    size="small"
                                                    onClick={() => setShowAddressForm(!showAddressForm)}
                                                    icon={<PlusIcon className="w-4 h-4" />}
                                                >
                                                    Nouvelle adresse
                                                </Button>
                                            </div>

                                            {showAddressForm && (
                                                <Card className="p-4 bg-gray-50">
                                                    <div className="space-y-3">
                                                        <input
                                                            type="text"
                                                            placeholder="Libellé (ex: Maison, Bureau)"
                                                            value={newAddress.label}
                                                            onChange={(e) => setNewAddress({...newAddress, label: e.target.value})}
                                                            className="w-full p-2 border rounded-lg text-sm"
                                                        />
                                                        <input
                                                            type="text"
                                                            placeholder="Adresse"
                                                            value={newAddress.street}
                                                            onChange={(e) => setNewAddress({...newAddress, street: e.target.value})}
                                                            className="w-full p-2 border rounded-lg text-sm"
                                                        />
                                                        <input
                                                            type="text"
                                                            placeholder="Complément d'adresse (optionnel)"
                                                            value={newAddress.complement}
                                                            onChange={(e) => setNewAddress({...newAddress, complement: e.target.value})}
                                                            className="w-full p-2 border rounded-lg text-sm"
                                                        />
                                                        <div className="grid grid-cols-2 gap-3">
                                                            <input
                                                                type="text"
                                                                placeholder="Ville"
                                                                value={newAddress.city}
                                                                onChange={(e) => setNewAddress({...newAddress, city: e.target.value})}
                                                                className="w-full p-2 border rounded-lg text-sm"
                                                            />
                                                            <input
                                                                type="text"
                                                                placeholder="Code postal"
                                                                value={newAddress.postalCode}
                                                                onChange={(e) => setNewAddress({...newAddress, postalCode: e.target.value})}
                                                                className="w-full p-2 border rounded-lg text-sm"
                                                            />
                                                        </div>
                                                        <Button
                                                            variant="primary"
                                                            onClick={handleAddAddress}
                                                            size="small"
                                                            className="w-full"
                                                        >
                                                            Ajouter
                                                        </Button>
                                                    </div>
                                                </Card>
                                            )}

                                            {loading ? (
                                                <div className="text-center py-8">
                                                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600 mx-auto"></div>
                                                </div>
                                            ) : addresses.length === 0 ? (
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
                                                                    <p className="font-semibold text-gray-900">{address.label}</p>
                                                                    <p className="text-sm text-gray-600 mt-1">
                                                                        {address.street}
                                                                        {address.complement && `, ${address.complement}`}
                                                                    </p>
                                                                    <p className="text-sm text-gray-600">
                                                                        {address.postalCode} {address.city}
                                                                    </p>
                                                                    <p className="text-sm text-gray-600">{address.country}</p>
                                                                </div>
                                                                {selectedShippingAddress === address.id && (
                                                                    <div className="flex-shrink-0">
                                                                        <CheckIcon className="w-6 h-6 text-purple-600" />
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    )}

                                    {/* Step 2: Mode de livraison */}
                                    {currentStep === 2 && (
                                        <div className="space-y-4">
                                            <h3 className="text-lg font-semibold text-gray-900">
                                                Choisissez votre mode de livraison
                                            </h3>

                                            {loadingRates ? (
                                                <div className="text-center py-8">
                                                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600 mx-auto mb-3"></div>
                                                    <p className="text-gray-600">Calcul des frais de livraison...</p>
                                                </div>
                                            ) : (
                                                <div className="space-y-3">
                                                    {shippingRates.map((rate) => (
                                                        <div
                                                            key={rate.rateId}
                                                            onClick={() => setSelectedRate(rate)}
                                                            className={`p-4 border-2 rounded-lg cursor-pointer transition-all ${
                                                                selectedRate?.rateId === rate.rateId
                                                                    ? 'border-purple-600 bg-purple-50'
                                                                    : 'border-gray-200 hover:border-gray-300'
                                                            }`}
                                                        >
                                                            <div className="flex items-center justify-between">
                                                                <div className="flex items-center gap-3">
                                                                    <TruckIcon className="w-6 h-6 text-gray-400" />
                                                                    <div>
                                                                        <p className="font-semibold text-gray-900">
                                                                            {rate.provider || 'Livraison'} {rate.servicelevel?.name ? `- ${rate.servicelevel.name}` : ''}
                                                                        </p>
                                                                        <p className="text-sm text-gray-600">
                                                                            {rate.durationTerms || 'Délai non spécifié'}
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                                <div className="text-right">
                                                                    <p className="font-bold text-purple-600">
                                                                        {formatPrice(parseFloat(rate.amount))}
                                                                    </p>
                                                                    {selectedRate?.rateId === rate.rateId && (
                                                                        <CheckIcon className="w-5 h-5 text-purple-600 ml-auto mt-1" />
                                                                    )}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    )}

                                    {/* Step 3: Récapitulatif */}
                                    {currentStep === 3 && (
                                        <div className="space-y-4">
                                            <h3 className="text-lg font-semibold text-gray-900">
                                                Récapitulatif de votre commande
                                            </h3>

                                            {/* Articles */}
                                            <Card className="p-4">
                                                <h4 className="font-medium text-gray-900 mb-3">Articles</h4>
                                                <div className="space-y-2">
                                                    {items?.map((item, index) => (
                                                        <div key={index} className="flex justify-between text-sm">
                                                            <span className="text-gray-600">{item.title}</span>
                                                            <span className="font-medium">{formatPrice(item.price)}</span>
                                                        </div>
                                                    ))}
                                                </div>
                                            </Card>

                                            {/* Adresse */}
                                            <Card className="p-4">
                                                <h4 className="font-medium text-gray-900 mb-2">Livraison</h4>
                                                {addresses.find(a => a.id === selectedShippingAddress) && (
                                                    <div className="text-sm text-gray-600">
                                                        <p>{addresses.find(a => a.id === selectedShippingAddress).label}</p>
                                                        <p>{addresses.find(a => a.id === selectedShippingAddress).street}</p>
                                                        <p>
                                                            {addresses.find(a => a.id === selectedShippingAddress).postalCode}{' '}
                                                            {addresses.find(a => a.id === selectedShippingAddress).city}
                                                        </p>
                                                    </div>
                                                )}
                                                <div className="mt-2 pt-2 border-t">
                                                    <p className="text-sm font-medium">
                                                        {selectedRate?.provider || 'Livraison'} {selectedRate?.servicelevel?.name ? `- ${selectedRate.servicelevel.name}` : ''}
                                                    </p>
                                                    <p className="text-sm text-gray-600">{selectedRate?.durationTerms || 'Délai non spécifié'}</p>
                                                </div>
                                            </Card>

                                            {/* Total */}
                                            <Card className="p-4 bg-gray-50">
                                                <div className="space-y-2">
                                                    <div className="flex justify-between text-sm">
                                                        <span>Sous-total</span>
                                                        <span>{formatPrice(subtotal)}</span>
                                                    </div>
                                                    <div className="flex justify-between text-sm">
                                                        <span>Livraison</span>
                                                        <span>{formatPrice(parseFloat(selectedRate?.amount || 0))}</span>
                                                    </div>
                                                    <div className="pt-2 border-t border-gray-300">
                                                        <div className="flex justify-between text-lg font-bold">
                                                            <span>Total</span>
                                                            <span className="text-purple-600">{formatPrice(totalWithShipping)}</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </Card>
                                        </div>
                                    )}
                                </div>

                                {/* Footer */}
                                <div className="border-t border-gray-200 bg-gray-50 px-6 py-4">
                                    <div className="flex items-center justify-between gap-4">
                                        {currentStep > 1 && (
                                            <Button
                                                variant="secondary"
                                                onClick={() => setCurrentStep(currentStep - 1)}
                                            >
                                                Retour
                                            </Button>
                                        )}
                                        <div className="flex-1" />
                                        <div className="text-right">
                                            <p className="text-sm text-gray-600">Total</p>
                                            <p className="text-2xl font-bold text-purple-600">
                                                {formatPrice(totalWithShipping)}
                                            </p>
                                        </div>
                                        <Button
                                            variant="primary"
                                            onClick={handleNextStep}
                                            disabled={loading || (currentStep === 1 && !selectedShippingAddress)}
                                            icon={currentStep === 3 ? <CreditCardIcon className="w-5 h-5" /> : null}
                                        >
                                            {loading ? 'Chargement...' : currentStep === 3 ? 'Procéder au paiement' : 'Continuer'}
                                        </Button>
                                    </div>
                                </div>
                            </Dialog.Panel>
                        </Transition.Child>
                    </div>
                </div>

                {/* Payment Modal */}
                <PaymentModal
                    isOpen={showPaymentModal}
                    onClose={() => setShowPaymentModal(false)}
                    orders={createdOrders}
                    total={totalWithShipping}
                    onPaymentSuccess={handlePaymentSuccess}
                />
            </Dialog>
        </Transition>
    );
};

export default CheckoutModal;


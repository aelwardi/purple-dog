import React, { useState, useEffect, Fragment } from 'react';
import { Dialog, Transition } from '@headlessui/react';
import {
    XMarkIcon,
    CheckCircleIcon,
    XCircleIcon,
    LockClosedIcon
} from '@heroicons/react/24/outline';
import { toast } from 'react-hot-toast';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import Button from '../common/Button';
import Card from '../common/Card';
import paymentService from '../../services/paymentService';

// Charger Stripe avec la clé publique
const stripePromise = loadStripe('pk_test_51ScpWHJj3NngK59l7wCvqgVhuVTpITCXhKdrhOmyqTWCPz3MC5xPlwQHjR8JdKZD6RU1oZxfsI2s78vPx8NIfVVw00HeyjcLOj');

// Style pour CardElement
const CARD_ELEMENT_OPTIONS = {
    style: {
        base: {
            color: '#32325d',
            fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
            fontSmoothing: 'antialiased',
            fontSize: '16px',
            '::placeholder': {
                color: '#aab7c4'
            }
        },
        invalid: {
            color: '#fa755a',
            iconColor: '#fa755a'
        }
    }
};

// Composant de formulaire de paiement avec Stripe Elements
const PaymentForm = ({ paymentIntent, total, orders, onSuccess, onError }) => {
    const stripe = useStripe();
    const elements = useElements();
    const [loading, setLoading] = useState(false);
    const [cardholderName, setCardholderName] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!stripe || !elements) {
            toast.error('Stripe n\'est pas encore chargé');
            return;
        }

        if (!cardholderName.trim()) {
            toast.error('Veuillez entrer le nom du titulaire de la carte');
            return;
        }

        setLoading(true);

        try {
            const cardElement = elements.getElement(CardElement);

            console.log('💳 Confirmation du paiement avec Stripe.js...');
            console.log('Client Secret:', paymentIntent?.clientSecret);

            // Confirmer le paiement avec Stripe (gère automatiquement 3D Secure/OTP)
            const { error, paymentIntent: confirmedPaymentIntent } = await stripe.confirmCardPayment(
                paymentIntent.clientSecret,
                {
                    payment_method: {
                        card: cardElement,
                        billing_details: {
                            name: cardholderName,
                        },
                    },
                }
            );

            if (error) {
                console.error('❌ Erreur Stripe:', error);
                onError(error.message);
                return;
            }

            console.log('✅ Payment Intent confirmé:', confirmedPaymentIntent);
            console.log('📊 Statut du paiement:', confirmedPaymentIntent.status);

            // Vérifier le statut
            if (confirmedPaymentIntent.status === 'succeeded') {
                // Le paiement est réussi après authentification
                console.log('✅ Paiement validé par Stripe');

                // Notifier le backend que le paiement est réussi
                const result = await paymentService.confirmPayment(paymentIntent.paymentIntentId);

                toast.success('Paiement effectué avec succès !');
                onSuccess(result);
            } else if (confirmedPaymentIntent.status === 'requires_action' || confirmedPaymentIntent.status === 'requires_source_action') {
                // 3D Secure en cours (Stripe affiche le modal automatiquement)
                console.log('🔐 Authentification 3D Secure requise...');
                toast.info('⏳ Authentification en cours...');
                // Ne rien faire, Stripe gère l'affichage du modal OTP
            } else if (confirmedPaymentIntent.status === 'processing') {
                // Paiement en cours de traitement
                console.log('⏳ Paiement en cours de traitement...');
                toast.info('Paiement en cours de traitement...');
                onSuccess({ status: 'PROCESSING' });
            } else {
                console.error('❌ Statut inattendu:', confirmedPaymentIntent.status);
                onError(`Statut de paiement inattendu: ${confirmedPaymentIntent.status}`);
            }

        } catch (error) {
            console.error('❌ Erreur de paiement:', error);
            onError(error.message || 'Erreur lors du paiement');
        } finally {
            setLoading(false);
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'EUR'
        }).format(price || 0);
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            {/* Nom du titulaire */}
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Nom du titulaire de la carte
                </label>
                <input
                    type="text"
                    value={cardholderName}
                    onChange={(e) => setCardholderName(e.target.value.toUpperCase())}
                    placeholder="JEAN DUPONT"
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    disabled={loading}
                    required
                />
            </div>

            {/* Stripe Card Element */}
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Informations de carte
                </label>
                <div className="p-3 border border-gray-300 rounded-lg focus-within:ring-2 focus-within:ring-purple-500">
                    <CardElement options={CARD_ELEMENT_OPTIONS} />
                </div>
            </div>

            {/* Carte de test */}
            <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
                <p className="text-sm font-medium text-blue-800 mb-2">💳 Cartes de test Stripe</p>
                <div className="text-xs text-blue-600 space-y-1">
                    <p><strong>Paiement réussi :</strong> 4242 4242 4242 4242</p>
                    <p><strong>Avec 3D Secure (OTP) :</strong> 4000 0027 6000 3184</p>
                    <p><strong>Date :</strong> 12/34 • <strong>CVV :</strong> 123</p>
                </div>
            </div>

            {/* Récapitulatif */}
            <Card className="p-4 bg-gray-50">
                <div className="space-y-2">
                    <div className="flex justify-between text-sm">
                        <span>Commandes</span>
                        <span>{orders?.length || 0}</span>
                    </div>
                    <div className="pt-2 border-t border-gray-300">
                        <div className="flex justify-between text-lg font-bold">
                            <span>Total</span>
                            <span className="text-purple-600">{formatPrice(total)}</span>
                        </div>
                    </div>
                </div>
            </Card>

            {/* Boutons */}
            <div className="flex gap-3 pt-4">
                <Button
                    type="submit"
                    variant="primary"
                    disabled={!stripe || loading}
                    className="w-full"
                    icon={<LockClosedIcon className="w-5 h-5" />}
                >
                    {loading ? 'Traitement...' : `Payer ${formatPrice(total)}`}
                </Button>
            </div>
        </form>
    );
};

const PaymentModal = ({ isOpen, onClose, orders, total, onPaymentSuccess }) => {
    const [loading, setLoading] = useState(false);
    const [paymentIntent, setPaymentIntent] = useState(null);
    const [paymentSuccess, setPaymentSuccess] = useState(false);
    const [paymentError, setPaymentError] = useState(null);

    useEffect(() => {
        if (isOpen && orders && orders.length > 0) {
            createPaymentIntent();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isOpen, orders]);

    const createPaymentIntent = async () => {
        try {
            setLoading(true);
            setPaymentError(null);

            // Validation détaillée
            console.log('🔍 Validation des données:', { orders, total, ordersLength: orders?.length });

            if (!orders || !Array.isArray(orders)) {
                console.error('❌ Orders invalide:', orders);
                toast.error('Aucune commande à payer');
                setTimeout(() => onClose(), 2000);
                return;
            }

            if (orders.length === 0) {
                console.error('❌ Orders vide');
                toast.error('Aucune commande à payer');
                setTimeout(() => onClose(), 2000);
                return;
            }

            if (!orders[0] || !orders[0].id) {
                console.error('❌ Première commande invalide:', orders[0]);
                toast.error('Commande invalide - ID manquant');
                setTimeout(() => onClose(), 2000);
                return;
            }

            if (!total || total <= 0 || isNaN(total)) {
                console.error('❌ Total invalide:', total);
                toast.error('Montant invalide');
                setTimeout(() => onClose(), 2000);
                return;
            }

            const paymentData = {
                orderId: orders[0].id,
                amount: Number(total).toFixed(2),
                currency: 'eur',
                description: `Paiement pour ${orders.length} commande(s)`
            };

            console.log('📤 Création Payment Intent:', paymentData);

            const intent = await paymentService.createPaymentIntent(paymentData);

            console.log('✅ Payment Intent créé:', intent);
            setPaymentIntent(intent);
        } catch (error) {
            console.error('❌ Error creating payment intent:', error);
            console.error('❌ Response:', error.response?.data);

            const errorMessage = error.response?.data?.message ||
                                error.response?.data?.error ||
                                error.message ||
                                'Erreur lors de l\'initialisation du paiement';

            setPaymentError(errorMessage);
            toast.error(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    const handlePaymentSuccess = (result) => {
        setPaymentSuccess(true);

        setTimeout(() => {
            if (onPaymentSuccess) {
                onPaymentSuccess(result);
            }
            onClose();

            // Réinitialiser les états
            setPaymentSuccess(false);
            setPaymentIntent(null);
            setPaymentError(null);
        }, 2000);
    };

    const handlePaymentError = (errorMessage) => {
        setPaymentError(errorMessage);
        toast.error(errorMessage);
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'EUR'
        }).format(price || 0);
    };

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
                            <Dialog.Panel className="w-full max-w-2xl transform overflow-hidden rounded-2xl bg-white shadow-2xl transition-all">
                                {paymentSuccess ? (
                                    // Success State
                                    <div className="p-8 text-center">
                                        <CheckCircleIcon className="w-20 h-20 text-green-500 mx-auto mb-4" />
                                        <Dialog.Title className="text-2xl font-bold text-gray-900 mb-2">
                                            Paiement réussi !
                                        </Dialog.Title>
                                        <p className="text-gray-600 mb-4">
                                            Votre commande a été confirmée et sera traitée sous peu.
                                        </p>
                                        <p className="text-sm text-gray-500">
                                            Redirection en cours...
                                        </p>
                                    </div>
                                ) : (
                                    <>
                                        {/* Header */}
                                        <div className="border-b border-gray-200 bg-gradient-to-r from-purple-600 to-purple-700 px-6 py-4">
                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center gap-3">
                                                    <div className="p-2 bg-white/20 rounded-lg">
                                                        <LockClosedIcon className="w-6 h-6 text-white" />
                                                    </div>
                                                    <div>
                                                        <Dialog.Title className="text-xl font-bold text-white">
                                                            Paiement sécurisé Stripe
                                                        </Dialog.Title>
                                                        <p className="text-purple-100 text-sm">
                                                            Total à payer : {formatPrice(total)}
                                                        </p>
                                                    </div>
                                                </div>
                                                <button
                                                    onClick={onClose}
                                                    className="p-2 hover:bg-white/20 rounded-lg transition-colors"
                                                    disabled={loading}
                                                >
                                                    <XMarkIcon className="w-6 h-6 text-white" />
                                                </button>
                                            </div>
                                        </div>

                                        {/* Content */}
                                        <div className="p-6">
                                            {loading && !paymentIntent ? (
                                                <div className="text-center py-8">
                                                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600 mx-auto mb-4"></div>
                                                    <p className="text-gray-600">Initialisation du paiement sécurisé...</p>
                                                </div>
                                            ) : paymentError && !paymentIntent ? (
                                                <div className="text-center py-8">
                                                    <XCircleIcon className="w-16 h-16 text-red-500 mx-auto mb-4" />
                                                    <p className="text-red-600 font-medium mb-2">Erreur d'initialisation</p>
                                                    <p className="text-gray-600 text-sm mb-4">{paymentError}</p>
                                                    <Button onClick={onClose} variant="secondary">
                                                        Fermer
                                                    </Button>
                                                </div>
                                            ) : paymentIntent ? (
                                                <>
                                                    {paymentError && (
                                                        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
                                                            <XCircleIcon className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
                                                            <div>
                                                                <p className="text-sm font-medium text-red-800">Erreur de paiement</p>
                                                                <p className="text-sm text-red-600">{paymentError}</p>
                                                            </div>
                                                        </div>
                                                    )}

                                                    <div className="mb-4 p-4 bg-green-50 border border-green-200 rounded-lg">
                                                        <p className="text-sm font-medium text-green-800 mb-1">
                                                            Connexion sécurisée établie avec Stripe
                                                        </p>
                                                        <p className="text-xs text-green-600">
                                                            Le paiement sera traité via Stripe avec authentification 3D Secure si nécessaire
                                                        </p>
                                                    </div>

                                                    {/* Stripe Elements Form */}
                                                    <Elements stripe={stripePromise}>
                                                        <PaymentForm
                                                            paymentIntent={paymentIntent}
                                                            total={total}
                                                            orders={orders}
                                                            onSuccess={handlePaymentSuccess}
                                                            onError={handlePaymentError}
                                                        />
                                                    </Elements>
                                                </>
                                            ) : null}

                                            {/* Sécurité */}
                                            <div className="mt-6 pt-4 border-t border-gray-200">
                                                <div className="flex items-center justify-center gap-2 text-sm text-gray-500">
                                                    <LockClosedIcon className="w-4 h-4" />
                                                    <span>Paiement sécurisé par Stripe • Authentification 3D Secure incluse</span>
                                                </div>
                                            </div>
                                        </div>
                                    </>
                                )}
                            </Dialog.Panel>
                        </Transition.Child>
                    </div>
                </div>
            </Dialog>
        </Transition>
    );
};

export default PaymentModal;


import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
    CreditCardIcon,
    CheckCircleIcon,
    XCircleIcon,
    ArrowLeftIcon
} from '@heroicons/react/24/outline';
import { toast } from 'react-hot-toast';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import paymentService from '../services/paymentService';

const PaymentPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { orders, total } = location.state || {};

    const [loading, setLoading] = useState(false);
    const [paymentIntent, setPaymentIntent] = useState(null);
    const [paymentSuccess, setPaymentSuccess] = useState(false);
    const [paymentError, setPaymentError] = useState(null);

    // Données de carte de test
    const [cardData, setCardData] = useState({
        cardNumber: '',
        cardHolder: '',
        expiryDate: '',
        cvv: ''
    });

    useEffect(() => {
        if (!orders || orders.length === 0) {
            toast.error('Aucune commande à payer');
            navigate('/panier');
            return;
        }

        createPaymentIntent();
    }, []);

    const createPaymentIntent = async () => {
        try {
            setLoading(true);

            // Créer un payment intent pour le total
            // Le backend récupère automatiquement le userId du token JWT
            const paymentData = {
                orderId: orders[0].id, // Premier ordre pour le payment intent
                amount: total,
                currency: 'eur', // Backend attend minuscules
                description: `Paiement pour ${orders.length} commande(s)`
            };

            const intent = await paymentService.createPaymentIntent(paymentData);
            setPaymentIntent(intent);
        } catch (error) {
            console.error('Error creating payment intent:', error);
            toast.error(error.response?.data?.message || 'Erreur lors de l\'initialisation du paiement');
        } finally {
            setLoading(false);
        }
    };

    const handlePayment = async (e) => {
        e.preventDefault();

        // Validation basique
        if (!cardData.cardNumber || !cardData.cardHolder || !cardData.expiryDate || !cardData.cvv) {
            toast.error('Veuillez remplir tous les champs');
            return;
        }

        if (cardData.cardNumber.replace(/\s/g, '').length !== 16) {
            toast.error('Numéro de carte invalide');
            return;
        }

        if (cardData.cvv.length !== 3) {
            toast.error('CVV invalide');
            return;
        }

        setLoading(true);
        setPaymentError(null);

        try {
            // Confirmer le paiement avec Stripe
            // Le backend attend juste le paymentIntentId
            const result = await paymentService.confirmPayment(paymentIntent.paymentIntentId);

            // Le backend retourne un PaymentResponseDTO avec status
            if (result.status === 'COMPLETED' || result.status === 'SUCCEEDED') {
                setPaymentSuccess(true);
                toast.success('Paiement effectué avec succès !');

                // Rediriger vers la page de confirmation après 3 secondes
                setTimeout(() => {
                    navigate('/order-confirmation', {
                        state: {
                            orders,
                            payment: result
                        }
                    });
                }, 3000);
            } else {
                throw new Error('Le paiement n\'a pas pu être complété');
            }

        } catch (error) {
            console.error('Payment error:', error);
            setPaymentError(error.response?.data?.message || 'Erreur lors du paiement');
            toast.error('Échec du paiement');
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

    const formatCardNumber = (value) => {
        const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
        const matches = v.match(/\d{4,16}/g);
        const match = (matches && matches[0]) || '';
        const parts = [];

        for (let i = 0, len = match.length; i < len; i += 4) {
            parts.push(match.substring(i, i + 4));
        }

        if (parts.length) {
            return parts.join(' ');
        } else {
            return value;
        }
    };

    const handleCardNumberChange = (e) => {
        const formatted = formatCardNumber(e.target.value);
        setCardData({ ...cardData, cardNumber: formatted });
    };

    const formatExpiryDate = (value) => {
        const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
        if (v.length >= 2) {
            return v.slice(0, 2) + '/' + v.slice(2, 4);
        }
        return v;
    };

    const handleExpiryChange = (e) => {
        const formatted = formatExpiryDate(e.target.value);
        setCardData({ ...cardData, expiryDate: formatted });
    };

    if (paymentSuccess) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
                <Card className="max-w-md w-full p-8 text-center">
                    <CheckCircleIcon className="w-20 h-20 text-green-500 mx-auto mb-4" />
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Paiement réussi !</h2>
                    <p className="text-gray-600 mb-4">
                        Votre commande a été confirmée et sera traitée sous peu.
                    </p>
                    <p className="text-sm text-gray-500">
                        Redirection vers la page de confirmation...
                    </p>
                </Card>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <button
                    onClick={() => navigate(-1)}
                    className="flex items-center gap-2 text-gray-600 hover:text-purple-600 mb-6"
                >
                    <ArrowLeftIcon className="w-5 h-5" />
                    <span>Retour</span>
                </button>

                <h1 className="text-3xl font-bold text-gray-900 mb-8">Paiement sécurisé</h1>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Formulaire de paiement */}
                    <div className="lg:col-span-2">
                        <Card className="p-6">
                            <h2 className="text-xl font-semibold text-gray-900 mb-6 flex items-center gap-2">
                                <CreditCardIcon className="w-6 h-6 text-purple-600" />
                                Informations de paiement
                            </h2>

                            {paymentError && (
                                <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
                                    <XCircleIcon className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
                                    <div>
                                        <p className="text-sm font-medium text-red-800">Erreur de paiement</p>
                                        <p className="text-sm text-red-600">{paymentError}</p>
                                    </div>
                                </div>
                            )}

                            <form onSubmit={handlePayment} className="space-y-4">
                                {/* Numéro de carte */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Numéro de carte
                                    </label>
                                    <input
                                        type="text"
                                        value={cardData.cardNumber}
                                        onChange={handleCardNumberChange}
                                        placeholder="1234 5678 9012 3456"
                                        maxLength="19"
                                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                    />
                                </div>

                                {/* Titulaire */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Titulaire de la carte
                                    </label>
                                    <input
                                        type="text"
                                        value={cardData.cardHolder}
                                        onChange={(e) => setCardData({ ...cardData, cardHolder: e.target.value.toUpperCase() })}
                                        placeholder="JEAN DUPONT"
                                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                    />
                                </div>

                                {/* Date d'expiration et CVV */}
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Date d'expiration
                                        </label>
                                        <input
                                            type="text"
                                            value={cardData.expiryDate}
                                            onChange={handleExpiryChange}
                                            placeholder="MM/AA"
                                            maxLength="5"
                                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            CVV
                                        </label>
                                        <input
                                            type="text"
                                            value={cardData.cvv}
                                            onChange={(e) => setCardData({ ...cardData, cvv: e.target.value.replace(/\D/g, '') })}
                                            placeholder="123"
                                            maxLength="3"
                                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                        />
                                    </div>
                                </div>

                                {/* Cartes de test Stripe */}
                                <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                                    <p className="text-sm font-medium text-blue-900 mb-2">
                                        💳 Cartes de test Stripe
                                    </p>
                                    <p className="text-xs text-blue-700 mb-2">
                                        <strong>Succès :</strong> 4242 4242 4242 4242
                                    </p>
                                    <p className="text-xs text-blue-700 mb-2">
                                        <strong>Échec :</strong> 4000 0000 0000 0002
                                    </p>
                                    <p className="text-xs text-blue-600">
                                        Date : n'importe quelle date future • CVV : n'importe quel 3 chiffres
                                    </p>
                                </div>

                                <Button
                                    type="submit"
                                    variant="primary"
                                    size="large"
                                    className="w-full mt-6"
                                    disabled={loading}
                                >
                                    {loading ? 'Traitement en cours...' : `Payer ${formatPrice(total)}`}
                                </Button>
                            </form>
                        </Card>

                        {/* Sécurité */}
                        <div className="mt-6 flex items-center justify-center gap-4 text-sm text-gray-500">
                            <div className="flex items-center gap-1">
                                <CheckCircleIcon className="w-4 h-4 text-green-500" />
                                <span>Paiement sécurisé SSL</span>
                            </div>
                            <div className="flex items-center gap-1">
                                <CheckCircleIcon className="w-4 h-4 text-green-500" />
                                <span>Certifié Stripe</span>
                            </div>
                        </div>
                    </div>

                    {/* Récapitulatif */}
                    <div>
                        <Card className="p-6 sticky top-4">
                            <h2 className="text-lg font-semibold text-gray-900 mb-4">Récapitulatif</h2>

                            <div className="space-y-3 mb-4">
                                <div className="flex justify-between text-sm text-gray-600">
                                    <span>Commandes</span>
                                    <span>{orders?.length || 0}</span>
                                </div>
                            </div>

                            <div className="border-t pt-3">
                                <div className="flex justify-between text-xl font-bold text-gray-900">
                                    <span>Total</span>
                                    <span>{formatPrice(total)}</span>
                                </div>
                            </div>

                            <div className="mt-6 p-3 bg-gray-50 rounded-lg">
                                <p className="text-xs text-gray-600 text-center">
                                    Vos données de paiement sont protégées et cryptées par Stripe
                                </p>
                            </div>
                        </Card>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PaymentPage;


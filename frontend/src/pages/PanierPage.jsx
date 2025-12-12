import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    TrashIcon,
    ShoppingBagIcon,
    ArrowLeftIcon,
    TruckIcon,
    ShieldCheckIcon
} from '@heroicons/react/24/outline';
import { toast } from 'react-hot-toast';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import CheckoutModal from '../components/checkout/CheckoutModal';

const PanierPage = () => {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showCheckoutModal, setShowCheckoutModal] = useState(false);

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = () => {
        try {
            const cart = JSON.parse(localStorage.getItem('cart') || '[]');
            console.log('🛒 Cart loaded:', cart);
            setCartItems(cart);
        } catch (error) {
            console.error('Error loading cart:', error);
            setCartItems([]);
        } finally {
            setLoading(false);
        }
    };

    const saveCart = (items) => {
        localStorage.setItem('cart', JSON.stringify(items));
        setCartItems(items);
        window.dispatchEvent(new Event('cartUpdated'));
    };

    // Calculer les totaux (pas de quantité, chaque item est unique)
    const subtotal = cartItems.reduce((sum, item) => sum + (item.price || 0), 0);

    const handleRemoveItem = (id) => {
        const updatedItems = cartItems.filter(item => item.id !== id);
        saveCart(updatedItems);
        toast.success('Produit retiré du panier');
    };

    const handleCheckout = () => {
        if (cartItems.length === 0) {
            toast.error('Votre panier est vide');
            return;
        }

        // Vérifier si l'utilisateur est connecté
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user) {
            toast.error('Vous devez être connecté pour passer commande');
            navigate('/login', { state: { from: '/panier' } });
            return;
        }

        // Ouvrir le modal de checkout
        setShowCheckoutModal(true);
    };

    const handleOrderCreated = (orders) => {
        console.log('✅ Commandes créées avec succès:', orders);
        // Le modal de paiement reste ouvert
        // Ne pas vider le panier ici - on le videra après paiement réussi
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'EUR'
        }).format(price || 0);
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                    <div className="animate-pulse">
                        <div className="h-8 bg-gray-300 rounded w-1/4 mb-8"></div>
                        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                            <div className="lg:col-span-2 space-y-4">
                                {[1, 2].map(i => (
                                    <div key={i} className="bg-gray-300 rounded-lg h-32"></div>
                                ))}
                            </div>
                            <div className="bg-gray-300 rounded-lg h-96"></div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="flex items-center justify-between mb-8">
                    <div className="flex items-center gap-4">
                        <button
                            onClick={() => navigate(-1)}
                            className="p-2 rounded-lg hover:bg-gray-100"
                        >
                            <ArrowLeftIcon className="w-6 h-6 text-gray-600" />
                        </button>
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900">Mon panier</h1>
                            <p className="text-gray-600">
                                {cartItems.length} produit{cartItems.length > 1 ? 's' : ''}
                            </p>
                        </div>
                    </div>
                    <Button
                        variant="secondary"
                        onClick={() => navigate('/search')}
                        icon={<ShoppingBagIcon className="w-5 h-5" />}
                    >
                        Continuer mes achats
                    </Button>
                </div>

                {cartItems.length === 0 ? (
                    <div className="text-center py-16">
                        <ShoppingBagIcon className="w-24 h-24 text-gray-300 mx-auto mb-6" />
                        <h2 className="text-2xl font-bold text-gray-900 mb-4">Votre panier est vide</h2>
                        <p className="text-gray-600 mb-8">
                            Découvrez notre sélection de produits
                        </p>
                        <Button
                            variant="primary"
                            onClick={() => navigate('/')}
                        >
                            Commencer mes achats
                        </Button>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                        <div className="lg:col-span-2 space-y-6">
                            <Card className="p-6">
                                <h2 className="text-xl font-semibold text-gray-900 mb-6">Produits</h2>
                                <div className="space-y-6">
                                    {cartItems.map((item) => (
                                        <div key={item.id} className="flex gap-4 p-4 border rounded-lg hover:bg-gray-50 transition-colors">
                                            <div className="flex-shrink-0">
                                                <img
                                                    src={item.image}
                                                    alt={item.title}
                                                    className="w-24 h-24 object-cover rounded-lg cursor-pointer hover:opacity-80 transition-opacity"
                                                    onClick={() => navigate(`/product/${item.productId}`)}
                                                    onError={(e) => {
                                                        e.target.src = 'https://via.placeholder.com/96?text=Image';
                                                    }}
                                                />
                                            </div>

                                            <div className="flex-1 flex justify-between items-start">
                                                <div>
                                                    <h3
                                                        className="font-semibold text-gray-900 cursor-pointer hover:text-purple-600 transition-colors mb-1"
                                                        onClick={() => navigate(`/product/${item.productId}`)}
                                                    >
                                                        {item.title}
                                                    </h3>
                                                    {item.seller && (
                                                        <p className="text-sm text-gray-600 mb-1">
                                                            Vendu par {item.seller.firstName} {item.seller.lastName}
                                                        </p>
                                                    )}
                                                    {item.condition && (
                                                        <p className="text-xs text-gray-500">
                                                            État: {item.condition}
                                                        </p>
                                                    )}
                                                </div>

                                                <div className="flex items-center gap-4">
                                                    <p className="text-xl font-bold text-purple-600">
                                                        {formatPrice(item.price)}
                                                    </p>

                                                    <button
                                                        onClick={() => handleRemoveItem(item.id)}
                                                        className="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                                                        title="Supprimer du panier"
                                                    >
                                                        <TrashIcon className="w-5 h-5" />
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </Card>
                        </div>

                        {/* Résumé */}
                        <div className="space-y-6">
                            {/* Total */}
                            <Card className="p-6">
                                <h3 className="text-lg font-semibold text-gray-900 mb-4">Récapitulatif</h3>
                                <div className="space-y-3">
                                    <div className="flex justify-between text-gray-600">
                                        <span>Sous-total ({cartItems.length} produit{cartItems.length > 1 ? 's' : ''})</span>
                                        <span>{formatPrice(subtotal)}</span>
                                    </div>
                                    <p className="text-xs text-gray-500 italic">
                                        * Les frais de livraison seront calculés à l'étape suivante
                                    </p>
                                    <div className="border-t pt-3">
                                        <div className="flex justify-between text-xl font-bold">
                                            <span>Sous-total</span>
                                            <span>{formatPrice(subtotal)}</span>
                                        </div>
                                    </div>
                                </div>

                                <Button
                                    variant="primary"
                                    size="large"
                                    className="w-full mt-6"
                                    onClick={handleCheckout}
                                >
                                    Commander
                                </Button>

                                <div className="mt-6 space-y-2">
                                    <div className="flex items-center gap-2 text-sm text-gray-600">
                                        <ShieldCheckIcon className="w-4 h-4 text-green-500" />
                                        <span>Paiement sécurisé</span>
                                    </div>
                                    <div className="flex items-center gap-2 text-sm text-gray-600">
                                        <TruckIcon className="w-4 h-4 text-blue-500" />
                                        <span>Livraison assurée</span>
                                    </div>
                                </div>
                            </Card>
                        </div>
                    </div>
                )}
            </div>

            {/* Checkout Modal */}
            <CheckoutModal
                isOpen={showCheckoutModal}
                onClose={() => setShowCheckoutModal(false)}
                items={cartItems}
                subtotal={subtotal}
                onOrderCreated={handleOrderCreated}
            />
        </div>
    );
};

export default PanierPage;


import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    HeartIcon,
    ShareIcon,
    ChevronLeftIcon,
    ChevronRightIcon,
    MapPinIcon,
    CalendarIcon,
    EyeIcon,
    ShieldCheckIcon,
    ChatBubbleLeftRightIcon,
    ExclamationTriangleIcon,
    StarIcon,
    ShoppingCartIcon,
    HandRaisedIcon,
    ClockIcon
} from '@heroicons/react/24/outline';
import { HeartIcon as HeartSolid } from '@heroicons/react/24/solid';
import { toast } from 'react-hot-toast';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import productService from '../services/productService';

const ProductInfoPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [isFavorited, setIsFavorited] = useState(false);

    // Charger le produit
    useEffect(() => {
        const loadProduct = async () => {
            try {
                setLoading(true);
                const productData = await productService.getProduct(id);
                console.log('📦 Product loaded:', productData);
                console.log('💰 Product price field:', productData.price);
                console.log('💵 Product estimatedValue field:', productData.estimatedValue);
                console.log('📋 All product fields:', Object.keys(productData));
                console.log('🏷️ Product title:', productData.title);
                console.log('📁 Category name:', productData.category?.name);
                console.log('🆔 Category ID:', productData.categoryId);
                console.log('✅ Product status:', productData.status);
                console.log('🛍️ Product saleType:', productData.saleType);

                setProduct(productData);
                setIsFavorited(productData.isFavorited || false);
            } catch (error) {
                console.error('❌ Error loading product:', error);
                toast.error('Produit non trouvé');
                navigate('/');
            } finally {
                setLoading(false);
            }
        };
        loadProduct();
    }, [id, navigate]);

    const handleToggleFavorite = async () => {
        const userId = JSON.parse(localStorage.getItem('user'))?.id;
        if (!userId) {
            toast.error('Vous devez être connecté pour ajouter aux favoris');
            navigate('/login');
            return;
        }

        try {
            if (isFavorited) {
                await productService.removeFavorite(userId, product.id);
                toast.success('Retiré des favoris');
            } else {
                await productService.addFavorite(userId, product.id);
                toast.success('Ajouté aux favoris');
            }
            setIsFavorited(!isFavorited);
        } catch (error) {
            toast.error('Erreur lors de la mise à jour des favoris');
        }
    };

    const handleShare = async () => {
        try {
            if (navigator.share) {
                await navigator.share({
                    title: product.title,
                    text: product.description,
                    url: window.location.href
                });
            } else {
                await navigator.clipboard.writeText(window.location.href);
                toast.success('Lien copié dans le presse-papiers');
            }
        } catch (error) {
            console.error('Erreur partage:', error);
        }
    };

    const handleAddToCart = async () => {
        try {
            // Récupérer le panier actuel
            const cart = JSON.parse(localStorage.getItem('cart') || '[]');

            // Vérifier si le produit existe déjà
            const existingIndex = cart.findIndex(item => item.productId === product.id);

            if (existingIndex >= 0) {
                toast.error('Ce produit est déjà dans votre panier');
                return;
            }

            // Ajouter le produit (sans quantité, chaque item est unique)
            cart.push({
                id: Date.now(),
                productId: product.id,
                title: product.title,
                price: product.price || product.estimatedValue || 0,
                image: product.photos?.[0]?.url || 'https://via.placeholder.com/400',
                seller: product.seller,
                condition: product.productCondition,
                category: product.category?.name || 'Non catégorisé'
            });

            toast.success('Produit ajouté au panier ! 🛒');

            localStorage.setItem('cart', JSON.stringify(cart));

            // Déclencher un événement pour mettre à jour le compteur du header
            window.dispatchEvent(new Event('cartUpdated'));
        } catch (error) {
            console.error('Error adding to cart:', error);
            toast.error('Erreur lors de l\'ajout au panier');
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'EUR'
        }).format(price || 0);
    };

    const getStatusBadge = () => {
        const statusConfig = {
            AVAILABLE: { label: 'Disponible', variant: 'success' },
            PENDING: { label: 'En attente', variant: 'warning' },
            SOLD: { label: 'Vendu', variant: 'secondary' },
            RESERVED: { label: 'Réservé', variant: 'info' }
        };

        const config = statusConfig[product?.status] || statusConfig.AVAILABLE;
        return <Badge variant={config.variant}>{config.label}</Badge>;
    };

    const getProductImage = (index) => {
        if (product?.photos && product.photos.length > index) {
            return product.photos[index].url;
        }
        return 'https://via.placeholder.com/800x600?text=No+Image';
    };

    const nextImage = () => {
        if (product?.photos?.length > 1) {
            setCurrentImageIndex((prev) =>
                prev === product.photos.length - 1 ? 0 : prev + 1
            );
        }
    };

    const prevImage = () => {
        if (product?.photos?.length > 1) {
            setCurrentImageIndex((prev) =>
                prev === 0 ? product.photos.length - 1 : prev - 1
            );
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                    <div className="animate-pulse">
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                            <div className="bg-gray-300 rounded-lg h-96"></div>
                            <div className="space-y-4">
                                <div className="h-8 bg-gray-300 rounded w-3/4"></div>
                                <div className="h-6 bg-gray-300 rounded w-1/2"></div>
                                <div className="h-20 bg-gray-300 rounded"></div>
                                <div className="h-10 bg-gray-300 rounded w-1/3"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (!product) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <ExclamationTriangleIcon className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                    <h1 className="text-2xl font-bold text-gray-900 mb-2">Produit non trouvé</h1>
                    <p className="text-gray-600 mb-4">Ce produit n'existe pas ou a été supprimé</p>
                    <Button onClick={() => navigate('/')}>
                        Retour à l'accueil
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Breadcrumb */}
                <nav className="mb-8">
                    <div className="flex items-center space-x-2 text-sm text-gray-600">
                        <button onClick={() => navigate('/')} className="hover:text-purple-600">
                            Accueil
                        </button>
                        <ChevronLeftIcon className="w-4 h-4 rotate-180" />
                        {product.category && (
                            <>
                                <button
                                    onClick={() => navigate(`/category/${product.categoryId}`)}
                                    className="hover:text-purple-600"
                                >
                                    {product.category.name}
                                </button>
                                <ChevronLeftIcon className="w-4 h-4 rotate-180" />
                            </>
                        )}
                        <span className="text-gray-900">{product.title}</span>
                    </div>
                </nav>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-12">
                    {/* Carousel d'images */}
                    <div className="space-y-4">
                        <Card className="p-0 overflow-hidden bg-white">
                            <div className="relative w-full" style={{ paddingBottom: '100%' }}>
                                <div className="absolute inset-0 bg-white flex items-center justify-center p-4">
                                    <img
                                        src={getProductImage(currentImageIndex)}
                                        alt={product.title}
                                        className="max-w-full max-h-full object-contain"
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = 'https://via.placeholder.com/800x600?text=Image+Non+Disponible';
                                        }}
                                    />
                                </div>
                                {product.photos && product.photos.length > 1 && (
                                    <>
                                        <button
                                            onClick={prevImage}
                                            className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-white bg-opacity-90 hover:bg-opacity-100 text-gray-900 rounded-full p-2 shadow-lg transition-all z-10"
                                        >
                                            <ChevronLeftIcon className="w-6 h-6" />
                                        </button>
                                        <button
                                            onClick={nextImage}
                                            className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-white bg-opacity-90 hover:bg-opacity-100 text-gray-900 rounded-full p-2 shadow-lg transition-all z-10"
                                        >
                                            <ChevronRightIcon className="w-6 h-6" />
                                        </button>
                                        {/* Indicateur de position */}
                                        <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex items-center gap-2 z-10">
                                            {product.photos.map((_, index) => (
                                                <button
                                                    key={index}
                                                    onClick={() => setCurrentImageIndex(index)}
                                                    className={`w-2 h-2 rounded-full transition-all ${
                                                        currentImageIndex === index
                                                            ? 'bg-purple-600 w-8'
                                                            : 'bg-gray-400 hover:bg-gray-600'
                                                    }`}
                                                />
                                            ))}
                                        </div>
                                    </>
                                )}
                                <div className="absolute top-4 left-4 z-10">
                                    {getStatusBadge()}
                                </div>
                            </div>
                        </Card>
                    </div>

                    {/* Informations du produit */}
                    <div className="space-y-6">
                        <div>
                            <div className="flex items-start justify-between mb-2">
                                <h1 className="text-3xl font-bold text-gray-900">{product.title}</h1>
                                <div className="flex items-center gap-2">
                                    <button
                                        onClick={handleToggleFavorite}
                                        className="p-2 rounded-lg hover:bg-gray-100"
                                    >
                                        {isFavorited ? (
                                            <HeartSolid className="w-6 h-6 text-red-500" />
                                        ) : (
                                            <HeartIcon className="w-6 h-6 text-gray-400" />
                                        )}
                                    </button>
                                    <button
                                        onClick={handleShare}
                                        className="p-2 rounded-lg hover:bg-gray-100"
                                    >
                                        <ShareIcon className="w-6 h-6 text-gray-400" />
                                    </button>
                                </div>
                            </div>

                            {product.category && (
                                <Badge className="bg-purple-100 text-purple-800 mb-4">
                                    {product.category.name}
                                </Badge>
                            )}

                        {/* Prix et Bouton Panier sur la même ligne */}
                        <div className="flex items-center justify-between gap-4 mb-6">
                            <div className="text-4xl font-bold text-purple-600">
                                {formatPrice(product.price || product.estimatedValue || 0)}
                            </div>

                            {(product.status === 'AVAILABLE' || product.status === 'ACTIVE') && product.saleType !== 'AUCTION' && (
                                <Button
                                    variant="primary"
                                    size="large"
                                    onClick={handleAddToCart}
                                    icon={<ShoppingCartIcon className="w-5 h-5" />}
                                >
                                </Button>
                            )}
                        </div>

                            <div className="flex items-center gap-4 text-sm text-gray-600 mb-6">
                                <div className="flex items-center gap-1">
                                    <CalendarIcon className="w-4 h-4" />
                                    <span>
                                        {product.createdAt && new Date(product.createdAt).toLocaleDateString('fr-FR')}
                                    </span>
                                </div>
                            </div>
                        </div>


                        {/* Vendeur */}
                        {product.seller && (
                            <Card className="p-4">
                                <h3 className="font-semibold text-gray-900 mb-3">Vendeur</h3>
                                <div className="flex items-center gap-3">
                                    <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
                                        <span className="text-purple-600 font-semibold">
                                            {product.seller.firstName?.[0]}{product.seller.lastName?.[0]}
                                        </span>
                                    </div>
                                    <div>
                                        <p className="font-medium text-gray-900">
                                            {product.seller.firstName} {product.seller.lastName}
                                        </p>
                                    </div>
                                </div>
                            </Card>
                        )}

                        {/* Garanties */}
                        <Card className="p-4">
                            <div className="flex items-center gap-2 mb-2">
                                <ShieldCheckIcon className="w-5 h-5 text-green-500" />
                                <h3 className="font-semibold text-gray-900">Sécurité</h3>
                            </div>
                            <ul className="space-y-2 text-sm text-gray-600">
                                <li>✓ Paiement sécurisé</li>
                                <li>✓ Vendeur vérifié</li>
                                <li>✓ Protection acheteur</li>
                            </ul>
                        </Card>
                    </div>
                </div>

                {/* Description */}
                <Card className="p-6 mb-12">
                    <h2 className="text-2xl font-bold text-gray-900 mb-4">Description</h2>
                    <div className="prose prose-gray max-w-none">
                        <p className="text-gray-700 whitespace-pre-wrap">
                            {product.description || 'Aucune description disponible.'}
                        </p>
                    </div>
                </Card>
            </div>
        </div>
    );
};

export default ProductInfoPage;


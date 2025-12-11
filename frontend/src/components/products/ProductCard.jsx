import React from 'react';
import { useNavigate } from 'react-router-dom';
import { HeartIcon, EyeIcon, ClockIcon, ShoppingCartIcon } from '@heroicons/react/24/outline';
import { HeartIcon as HeartSolid } from '@heroicons/react/24/solid';
import { toast } from 'react-hot-toast';
import Badge from '../common/Badge';
import Card from '../common/Card';

const ProductCard = ({
  product,
  onToggleFavorite,
  viewMode = 'grid',
  showSeller = true
}) => {
  const navigate = useNavigate();

  // Logs de débogage
  console.log('🔍 ProductCard - Product:', {
    id: product.id,
    title: product.title,
    status: product.status,
    saleType: product.saleType,
    price: product.price,
    estimatedValue: product.estimatedValue
  });

  // Vérifier la condition d'affichage du bouton
  const shouldShowCartButton = (product.status === 'AVAILABLE' || product.status === 'ACTIVE') && product.saleType !== 'AUCTION';
  console.log('🛒 Should show cart button?', shouldShowCartButton, {
    statusCheck: product.status === 'AVAILABLE' || product.status === 'ACTIVE',
    saleTypeCheck: product.saleType !== 'AUCTION'
  });

  const formatPrice = (price) => {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(price || 0);
  };

  const getProductImage = () => {
    if (product.photos && product.photos.length > 0) {
      const primaryPhoto = product.photos.find(p => p.primary) || product.photos[0];
      return primaryPhoto.url;
    }
    return 'https://via.placeholder.com/400x300?text=No+Image';
  };

  const getStatusBadge = () => {
    switch (product.status) {
      case 'AVAILABLE':
        return <Badge variant="success">Disponible</Badge>;
      case 'SOLD':
        return <Badge variant="danger">Vendu</Badge>;
      case 'RESERVED':
        return <Badge variant="warning">Réservé</Badge>;
      case 'PENDING':
        return <Badge variant="secondary">En attente</Badge>;
      default:
        return null;
    }
  };

  const getSaleTypeBadge = () => {
    if (product.saleType === 'AUCTION') {
      return (
        <Badge variant="warning" className="absolute top-2 left-2 flex items-center gap-1">
          <ClockIcon className="w-3 h-3" />
          Enchères
        </Badge>
      );
    }
    return null;
  };

  const handleClick = () => {
    navigate(`/product/${product.id}`);
  };

  const handleFavoriteClick = (e) => {
    e.stopPropagation();
    if (onToggleFavorite) {
      onToggleFavorite(product.id);
    }
  };

  const handleAddToCart = (e) => {
    e.stopPropagation();

    try {
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
        image: getProductImage(),
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

  if (viewMode === 'list') {
    return (
      <Card
        className="flex flex-col md:flex-row gap-4 cursor-pointer hover:shadow-lg transition-shadow group"
        onClick={handleClick}
      >
        {/* Image */}
        <div className="relative md:w-64 md:flex-shrink-0">
          <div className="relative bg-white rounded-lg overflow-hidden" style={{ paddingBottom: '75%' }}>
            <div className="absolute inset-0 p-2 flex items-center justify-center">
              <img
                src={getProductImage()}
                alt={product.title}
                className="max-w-full max-h-full object-contain"
                onError={(e) => {
                  e.target.onerror = null;
                  e.target.src = 'https://via.placeholder.com/400x300?text=Image+Non+Disponible';
                }}
              />
            </div>
            <button
              onClick={handleFavoriteClick}
              className="absolute top-2 right-2 p-2 bg-white rounded-full shadow-md hover:shadow-lg transition-shadow z-10"
            >
              {product.isFavorited ? (
                <HeartSolid className="w-5 h-5 text-red-500" />
              ) : (
                <HeartIcon className="w-5 h-5 text-gray-600" />
              )}
            </button>
            {getSaleTypeBadge()}
          </div>
        </div>

        {/* Contenu */}
        <div className="flex-1 p-4 md:p-0 md:py-4">
          <div className="flex flex-col h-full">
            <div className="flex items-start justify-between mb-2">
              <div className="flex-1">
                <h3 className="text-xl font-semibold text-gray-900 mb-1 group-hover:text-purple-600 transition-colors">
                  {product.title}
                </h3>
                {product.description && (
                  <p className="text-sm text-gray-600 mb-3 line-clamp-2">
                    {product.description}
                  </p>
                )}
              </div>
              <div className="ml-4">
                {getStatusBadge()}
              </div>
            </div>

            <div className="flex items-center justify-between mt-auto">
              <div>
                <p className="text-3xl font-bold text-purple-600">
                  {formatPrice(product.price || product.estimatedValue || 0)}
                </p>
                {product.productCondition && (
                  <p className="text-sm text-gray-500 mt-1">
                    État: <span className="font-medium">{product.productCondition}</span>
                  </p>
                )}
              </div>

              {showSeller && product.seller && (
                <div className="text-right">
                  <p className="text-xs text-gray-500">Vendeur</p>
                  <p className="text-sm font-medium text-gray-900">
                    {product.seller.firstName} {product.seller.lastName}
                  </p>
                </div>
              )}
            </div>

            {/* Bouton Ajouter au panier */}
            {shouldShowCartButton && (
              <div className="mt-4">
                <button
                  onClick={handleAddToCart}
                  className="w-full md:w-auto bg-purple-600 hover:bg-purple-700 text-white py-2 px-6 rounded-lg flex items-center justify-center gap-2 transition-colors"
                >
                  <ShoppingCartIcon className="w-5 h-5" />
                </button>
              </div>
            )}

            {/* Métadonnées */}
            <div className="flex items-center gap-4 mt-4 text-xs text-gray-500">
              {product.views !== undefined && (
                <div className="flex items-center gap-1">
                  <EyeIcon className="w-4 h-4" />
                  <span>{product.views} vues</span>
                </div>
              )}
              {product.createdAt && (
                <span>
                  Publié le {new Date(product.createdAt).toLocaleDateString('fr-FR')}
                </span>
              )}
            </div>
          </div>
        </div>
      </Card>
    );
  }

  // Vue Grille (par défaut)
  return (
    <Card
      className="group cursor-pointer hover:shadow-lg transition-shadow"
      onClick={handleClick}
    >
      {/* Image */}
      <div className="relative bg-white" style={{ paddingBottom: '75%' }}>
        <div className="absolute inset-0 p-3 flex items-center justify-center">
          <img
            src={getProductImage()}
            alt={product.title}
            className="max-w-full max-h-full object-contain rounded-t-lg"
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = 'https://via.placeholder.com/400x300?text=Image+Non+Disponible';
            }}
          />
        </div>
        <button
          onClick={handleFavoriteClick}
          className="absolute top-2 right-2 p-2 bg-white rounded-full shadow-md hover:shadow-lg transition-shadow z-10"
        >
          {product.isFavorited ? (
            <HeartSolid className="w-5 h-5 text-red-500" />
          ) : (
            <HeartIcon className="w-5 h-5 text-gray-600" />
          )}
        </button>
        {getSaleTypeBadge()}
      </div>

      {/* Contenu */}
      <div className="p-4">
        <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2 group-hover:text-purple-600 transition-colors">
          {product.title}
        </h3>

        {product.description && (
          <p className="text-sm text-gray-600 mb-3 line-clamp-2">
            {product.description}
          </p>
        )}

        <div className="flex items-center justify-between mb-3">
          <div>
            <p className="text-2xl font-bold text-purple-600">
              {formatPrice(product.price || product.estimatedValue || 0)}
            </p>
            {product.productCondition && (
              <p className="text-xs text-gray-500 mt-1">
                État: {product.productCondition}
              </p>
            )}
          </div>
        </div>

        <div className="flex items-center justify-between mb-3">
          {showSeller && product.seller && (
            <div className="flex-1">
              <p className="text-xs text-gray-500">Vendeur</p>
              <p className="text-sm font-medium text-gray-900 truncate">
                {product.seller.firstName} {product.seller.lastName}
              </p>
            </div>
          )}

          <div>
            {getStatusBadge()}
          </div>
        </div>

        {/* Bas de la carte : Date et Bouton */}
        {shouldShowCartButton && (
          <div className="mt-3 pt-3 border-t border-gray-100 flex items-center justify-between gap-2">
            {product.createdAt && (
              <p className="text-xs text-gray-500">
                {new Date(product.createdAt).toLocaleDateString('fr-FR')}
              </p>
            )}
            <button
              onClick={handleAddToCart}
              className="bg-purple-600 hover:bg-purple-700 text-white py-1.5 px-3 rounded-lg flex items-center gap-1.5 transition-colors text-sm"
            >
              <ShoppingCartIcon className="w-4 h-4" />
              <span>Panier</span>
            </button>
          </div>
        )}
      </div>
    </Card>
  );
};

export default ProductCard;


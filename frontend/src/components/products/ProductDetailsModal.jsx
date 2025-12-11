import React, { useState, useEffect } from 'react';
import { XMarkIcon, HeartIcon, ShoppingCartIcon } from '@heroicons/react/24/outline';
import { HeartIcon as HeartSolidIcon } from '@heroicons/react/24/solid';
import productService from '../../services/productService';
import favoriteService from '../../services/favoriteService';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import Button from '../common/Button';

const ProductDetailsModal = ({ productId, onClose }) => {
  const { user } = useAuth();
  const { handleError, showSuccess } = useErrorHandler();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);
  const [selectedImage, setSelectedImage] = useState(0);

  useEffect(() => {
    loadProduct();
    checkFavorite();
  }, [productId]);

  const loadProduct = async () => {
    try {
      setLoading(true);
      const data = await productService.getProduct(productId);
      setProduct(data);
      setSelectedImage(0);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  const checkFavorite = async () => {
    if (!user) return;
    try {
      const result = await favoriteService.isFavorite(user.id, productId);
      setIsFavorite(result);
    } catch (error) {
      console.error('Error checking favorite:', error);
    }
  };

  const toggleFavorite = async () => {
    try {
      if (isFavorite) {
        await favoriteService.removeFromFavorites(user.id, productId);
        setIsFavorite(false);
        showSuccess('Retiré des favoris');
      } else {
        await favoriteService.addToFavorites(user.id, productId);
        setIsFavorite(true);
        showSuccess('Ajouté aux favoris');
      }
    } catch (error) {
      handleError(error);
    }
  };

  const getConditionLabel = (condition) => {
    const conditions = {
      EXCELLENT: 'Excellent',
      VERY_GOOD: 'Très bon',
      GOOD: 'Bon',
      FAIR: 'Correct',
      POOR: 'Mauvais',
      RESTORATION_NEEDED: 'À restaurer'
    };
    return conditions[condition] || condition;
  };

  const getStatusLabel = (status) => {
    const statuses = {
      ACTIVE: 'En vente',
      PENDING_VALIDATION: 'En attente de validation',
      SOLD: 'Vendu',
      INACTIVE: 'Inactif'
    };
    return statuses[status] || status;
  };

  if (loading) {
    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg p-8">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
        </div>
      </div>
    );
  }

  if (!product) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4 overflow-y-auto">
      <div className="bg-white rounded-lg max-w-5xl w-full my-8 relative">
        {/* Close button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 hover:bg-gray-100 rounded-full transition-colors z-10"
        >
          <XMarkIcon className="w-6 h-6 text-gray-600" />
        </button>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-6">
          {/* Images */}
          <div className="space-y-4">
            {/* Main image */}
            <div className="relative aspect-square bg-gray-200 rounded-lg overflow-hidden">
              {product.photos && product.photos.length > 0 ? (
                <img
                  src={product.photos[selectedImage]?.url}
                  alt={product.title}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="flex items-center justify-center h-full text-gray-400">
                  <span className="text-6xl">📷</span>
                </div>
              )}
            </div>

            {/* Thumbnails */}
            {product.photos && product.photos.length > 1 && (
              <div className="grid grid-cols-5 gap-2">
                {product.photos.map((photo, index) => (
                  <button
                    key={photo.id}
                    onClick={() => setSelectedImage(index)}
                    className={`aspect-square bg-gray-200 rounded-lg overflow-hidden border-2 transition-colors ${
                      selectedImage === index ? 'border-purple-600' : 'border-transparent'
                    }`}
                  >
                    <img
                      src={photo.url}
                      alt={`${product.title} ${index + 1}`}
                      className="w-full h-full object-cover"
                    />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Details */}
          <div className="space-y-6">
            {/* Title & Status */}
            <div>
              <div className="flex items-start justify-between mb-2">
                <h2 className="text-2xl font-bold text-gray-900">{product.title}</h2>
                <button
                  onClick={toggleFavorite}
                  className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                >
                  {isFavorite ? (
                    <HeartSolidIcon className="w-6 h-6 text-red-500" />
                  ) : (
                    <HeartIcon className="w-6 h-6 text-gray-400" />
                  )}
                </button>
              </div>

              <div className="flex gap-2 mb-4">
                <span className={`px-3 py-1 text-sm font-medium rounded-full ${
                  product.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                  product.status === 'SOLD' ? 'bg-blue-100 text-blue-800' :
                  'bg-yellow-100 text-yellow-800'
                }`}>
                  {getStatusLabel(product.status)}
                </span>
                <span className="px-3 py-1 text-sm font-medium rounded-full bg-purple-100 text-purple-800">
                  {product.saleType === 'AUCTION' ? 'Enchères' : 'Vente rapide'}
                </span>
              </div>
            </div>

            {/* Price */}
            <div className="border-y py-4">
              <p className="text-sm text-gray-500 mb-1">Prix estimé</p>
              <p className="text-3xl font-bold text-purple-600">
                {product.estimatedValue?.toLocaleString('fr-FR')} €
              </p>
            </div>

            {/* Description */}
            <div>
              <h3 className="font-semibold text-gray-900 mb-2">Description</h3>
              <p className="text-gray-700 whitespace-pre-line">{product.description}</p>
            </div>

            {/* Details */}
            <div className="space-y-3">
              <h3 className="font-semibold text-gray-900">Détails</h3>

              <div className="grid grid-cols-2 gap-3 text-sm">
                {product.productCondition && (
                  <div>
                    <span className="text-gray-500">État :</span>
                    <span className="ml-2 font-medium text-gray-900">
                      {getConditionLabel(product.productCondition)}
                    </span>
                  </div>
                )}

                {product.brand && (
                  <div>
                    <span className="text-gray-500">Marque :</span>
                    <span className="ml-2 font-medium text-gray-900">{product.brand}</span>
                  </div>
                )}

                {product.yearOfManufacture && (
                  <div>
                    <span className="text-gray-500">Année :</span>
                    <span className="ml-2 font-medium text-gray-900">{product.yearOfManufacture}</span>
                  </div>
                )}

                {product.origin && (
                  <div>
                    <span className="text-gray-500">Origine :</span>
                    <span className="ml-2 font-medium text-gray-900">{product.origin}</span>
                  </div>
                )}

                {product.category && (
                  <div>
                    <span className="text-gray-500">Catégorie :</span>
                    <span className="ml-2 font-medium text-gray-900">{product.category.name}</span>
                  </div>
                )}
              </div>

              {/* Dimensions */}
              {(product.widthCm || product.heightCm || product.depthCm || product.weightKg) && (
                <div>
                  <p className="text-sm text-gray-500 mb-2">Dimensions</p>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    {product.widthCm && (
                      <div>
                        <span className="text-gray-500">Largeur :</span>
                        <span className="ml-2 font-medium text-gray-900">{product.widthCm} cm</span>
                      </div>
                    )}
                    {product.heightCm && (
                      <div>
                        <span className="text-gray-500">Hauteur :</span>
                        <span className="ml-2 font-medium text-gray-900">{product.heightCm} cm</span>
                      </div>
                    )}
                    {product.depthCm && (
                      <div>
                        <span className="text-gray-500">Profondeur :</span>
                        <span className="ml-2 font-medium text-gray-900">{product.depthCm} cm</span>
                      </div>
                    )}
                    {product.weightKg && (
                      <div>
                        <span className="text-gray-500">Poids :</span>
                        <span className="ml-2 font-medium text-gray-900">{product.weightKg} kg</span>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>

            {/* Seller info */}
            {product.seller && (
              <div className="border-t pt-4">
                <p className="text-sm text-gray-500 mb-1">Vendeur</p>
                <p className="font-medium text-gray-900">
                  {product.seller.firstName} {product.seller.lastName}
                </p>
              </div>
            )}

            {/* Actions */}
            {product.status === 'ACTIVE' && user?.id !== product.seller?.id && (
              <div className="flex gap-3">
                <Button variant="primary" className="flex-1">
                  <ShoppingCartIcon className="w-5 h-5 mr-2" />
                  Acheter
                </Button>
                <Button variant="outline">
                  Contacter le vendeur
                </Button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetailsModal;


import React, { useState, useEffect } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import favoriteService from '../../services/favoriteService';
import Card from '../common/Card';
import Button from '../common/Button';
import { HeartIcon, ShoppingCartIcon, EyeIcon } from '@heroicons/react/24/solid';
import { HeartIcon as HeartOutlineIcon } from '@heroicons/react/24/outline';
import ProductDetailsModal from '../products/ProductDetailsModal';

const MyFavorites = () => {
  const { user } = useAuth();
  const { handleError, showSuccess } = useErrorHandler();
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedProductId, setSelectedProductId] = useState(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);

  useEffect(() => {
    loadFavorites();
  }, [user]);

  const loadFavorites = async () => {
    try {
      setLoading(true);
      const data = await favoriteService.getUserFavorites(user.id);
      setFavorites(data);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveFavorite = async (productId) => {
    try {
      await favoriteService.removeFromFavorites(user.id, productId);
      setFavorites(favorites.filter(fav => fav.productId !== productId));
      showSuccess('Retiré des favoris');
    } catch (error) {
      handleError(error);
    }
  };

  const handleViewProduct = (productId) => {
    setSelectedProductId(productId);
    setShowDetailsModal(true);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Info */}
      <div className="flex justify-end">
        <p className="text-gray-600">{favorites.length} objet(s) en favori</p>
      </div>

      {/* Liste des favoris */}
      {favorites.length === 0 ? (
        <Card className="p-12 text-center">
          <div className="text-gray-400 text-6xl mb-4">
            <HeartOutlineIcon className="w-20 h-20 mx-auto" />
          </div>
          <h3 className="text-xl font-semibold text-gray-700 mb-2">
            Aucun favori
          </h3>
          <p className="text-gray-500 mb-6">
            Vous n'avez pas encore ajouté d'objets à vos favoris
          </p>
          <Button variant="primary" onClick={() => window.location.hash = '#/search'}>
            Parcourir les objets
          </Button>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {favorites.map((favorite) => (
            <Card key={favorite.id} className="overflow-hidden hover:shadow-lg transition-shadow">
              {/* Image */}
              <div className="relative h-48 bg-gray-200">
                {favorite.productMainImage ? (
                  <img
                    src={favorite.productMainImage}
                    alt={favorite.productTitle}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="flex items-center justify-center h-full text-gray-400">
                    <span className="text-4xl">📷</span>
                  </div>
                )}

                {/* Bouton Favoris */}
                <button
                  onClick={() => handleRemoveFavorite(favorite.productId)}
                  className="absolute top-2 right-2 p-2 bg-white rounded-full shadow-lg hover:bg-gray-100 transition-colors"
                  title="Retirer des favoris"
                >
                  <HeartIcon className="w-5 h-5 text-red-500" />
                </button>
              </div>

              {/* Contenu */}
              <div className="p-4">
                <h3 className="font-semibold text-lg text-gray-900 mb-2 line-clamp-2">
                  {favorite.productTitle}
                </h3>

                <p className="text-sm text-gray-600 mb-3 line-clamp-2">
                  {favorite.productDescription}
                </p>

                <div className="flex justify-between items-center mb-4">
                  <div>
                    <p className="text-xs text-gray-500">Prix estimé</p>
                    <p className="text-xl font-bold text-purple-600">
                      {favorite.productPrice?.toLocaleString('fr-FR')} €
                    </p>
                  </div>
                </div>

                {/* Vendeur */}
                {favorite.sellerName && (
                  <div className="mb-4 pb-3 border-b">
                    <p className="text-xs text-gray-500">Vendeur</p>
                    <p className="text-sm font-medium text-gray-700">
                      {favorite.sellerName}
                    </p>
                  </div>
                )}

                {/* Date ajout favori */}
                <div className="mb-4">
                  <p className="text-xs text-gray-500">
                    Ajouté le {new Date(favorite.createdAt).toLocaleDateString('fr-FR')}
                  </p>
                </div>

                {/* Actions */}
                <div className="flex gap-2">
                  <button
                    onClick={() => handleViewProduct(favorite.productId)}
                    className="flex-1 flex items-center justify-center gap-2 px-3 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg transition-colors"
                    title="Voir les détails"
                  >
                    <EyeIcon className="w-4 h-4" />
                    <span className="text-sm font-medium">Voir</span>
                  </button>

                  <button
                    className="flex items-center justify-center gap-2 px-3 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
                    title="Acheter"
                  >
                    <ShoppingCartIcon className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Product Details Modal */}
      {showDetailsModal && selectedProductId && (
        <ProductDetailsModal
          productId={selectedProductId}
          onClose={() => {
            setShowDetailsModal(false);
            setSelectedProductId(null);
          }}
        />
      )}
    </div>
  );
};

export default MyFavorites;


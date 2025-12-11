import React, { useState, useEffect } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import productService from '../../services/productService';
import Card from '../common/Card';
import Button from '../common/Button';
import { EyeIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import ProductDetailsModal from '../products/ProductDetailsModal';
import ProductEditModal from '../products/ProductEditModal';

const MyProducts = () => {
  const { user } = useAuth();
  const { handleError, showSuccess } = useErrorHandler();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL'); // ALL, ACTIVE, PENDING, SOLD
  const [selectedProductId, setSelectedProductId] = useState(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);

  useEffect(() => {
    loadProducts();
  }, [filter, user]);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const status = filter === 'ALL' ? null : filter;
      const data = await productService.getProductsBySeller(user.id, status);
      setProducts(data);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      ACTIVE: { label: 'En vente', class: 'bg-green-100 text-green-800' },
      PENDING_VALIDATION: { label: 'En attente', class: 'bg-yellow-100 text-yellow-800' },
      SOLD: { label: 'Vendu', class: 'bg-blue-100 text-blue-800' },
      INACTIVE: { label: 'Inactif', class: 'bg-gray-100 text-gray-800' }
    };

    const config = statusConfig[status] || { label: status, class: 'bg-gray-100 text-gray-800' };

    return (
      <span className={`px-3 py-1 text-xs font-medium rounded-full ${config.class}`}>
        {config.label}
      </span>
    );
  };

  const getSaleTypeBadge = (saleType) => {
    return saleType === 'AUCTION' ? (
      <span className="px-3 py-1 text-xs font-medium rounded-full bg-purple-100 text-purple-800">
        Enchères
      </span>
    ) : (
      <span className="px-3 py-1 text-xs font-medium rounded-full bg-indigo-100 text-indigo-800">
        Vente rapide
      </span>
    );
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

  const handleViewProduct = (productId) => {
    setSelectedProductId(productId);
    setShowDetailsModal(true);
  };

  const handleEditProduct = (productId) => {
    setSelectedProductId(productId);
    setShowEditModal(true);
  };

  const handleEditSuccess = () => {
    loadProducts(); // Recharger la liste après modification
  };

  const handleDeleteProduct = async (productId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      return;
    }

    try {
      await productService.deleteProduct(productId);
      showSuccess('Produit supprimé avec succès');
      // Recharger la liste
      loadProducts();
    } catch (error) {
      handleError(error);
    }
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
      {/* Filtres */}
      <div className="flex justify-end">
        <div className="flex gap-2">
          <button
            onClick={() => setFilter('ALL')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'ALL'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Tous ({products.length})
          </button>
          <button
            onClick={() => setFilter('ACTIVE')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'ACTIVE'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            En vente
          </button>
          <button
            onClick={() => setFilter('PENDING_VALIDATION')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'PENDING_VALIDATION'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            En attente
          </button>
          <button
            onClick={() => setFilter('SOLD')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'SOLD'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Vendus
          </button>
        </div>
      </div>

      {/* Liste des produits */}
      {products.length === 0 ? (
        <Card className="p-12 text-center">
          <div className="text-gray-400 text-6xl mb-4">📦</div>
          <h3 className="text-xl font-semibold text-gray-700 mb-2">
            Aucun produit trouvé
          </h3>
          <p className="text-gray-500 mb-6">
            {filter === 'ALL'
              ? "Vous n'avez pas encore mis d'objets en vente"
              : `Vous n'avez pas d'objets avec ce statut`}
          </p>
          {filter === 'ALL' && (
            <Button variant="primary" onClick={() => window.location.hash = '#/sell'}>
              Vendre un objet
            </Button>
          )}
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {products.map((product) => (
            <Card key={product.id} className="overflow-hidden hover:shadow-lg transition-shadow">
              {/* Image */}
              <div className="relative h-48 bg-gray-200">
                {product.photos && product.photos.length > 0 ? (
                  <img
                    src={product.photos[0].url}
                    alt={product.title}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="flex items-center justify-center h-full text-gray-400">
                    <span className="text-4xl">📷</span>
                  </div>
                )}
                <div className="absolute top-2 left-2">
                  {getStatusBadge(product.status)}
                </div>
                <div className="absolute top-2 right-2">
                  {getSaleTypeBadge(product.saleType)}
                </div>
              </div>

              {/* Contenu */}
              <div className="p-4">
                <h3 className="font-semibold text-lg text-gray-900 mb-2 line-clamp-2">
                  {product.title}
                </h3>

                <p className="text-sm text-gray-600 mb-3 line-clamp-2">
                  {product.description}
                </p>

                <div className="flex justify-between items-center mb-4">
                  <div>
                    <p className="text-xs text-gray-500">Prix estimé</p>
                    <p className="text-xl font-bold text-purple-600">
                      {product.estimatedValue?.toLocaleString('fr-FR')} €
                    </p>
                  </div>

                  {product.productCondition && (
                    <div className="text-right">
                      <p className="text-xs text-gray-500">État</p>
                      <p className="text-sm font-medium text-gray-700">
                        {getConditionLabel(product.productCondition)}
                      </p>
                    </div>
                  )}
                </div>

                {/* Actions */}
                <div className="flex gap-2 pt-3 border-t">
                  <button
                    onClick={() => handleViewProduct(product.id)}
                    className="flex-1 flex items-center justify-center gap-2 px-3 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition-colors"
                    title="Voir"
                  >
                    <EyeIcon className="w-4 h-4" />
                    <span className="text-sm font-medium">Voir</span>
                  </button>

                  {product.status !== 'SOLD' && (
                    <>
                      <button
                        onClick={() => handleEditProduct(product.id)}
                        className="flex items-center justify-center px-3 py-2 bg-blue-100 hover:bg-blue-200 text-blue-700 rounded-lg transition-colors"
                        title="Modifier"
                      >
                        <PencilIcon className="w-4 h-4" />
                      </button>

                      <button
                        onClick={() => handleDeleteProduct(product.id)}
                        className="flex items-center justify-center px-3 py-2 bg-red-100 hover:bg-red-200 text-red-700 rounded-lg transition-colors"
                        title="Supprimer"
                      >
                        <TrashIcon className="w-4 h-4" />
                      </button>
                    </>
                  )}
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

      {/* Product Edit Modal */}
      {showEditModal && selectedProductId && (
        <ProductEditModal
          productId={selectedProductId}
          onClose={() => {
            setShowEditModal(false);
            setSelectedProductId(null);
          }}
          onSuccess={handleEditSuccess}
        />
      )}
    </div>
  );
};

export default MyProducts;


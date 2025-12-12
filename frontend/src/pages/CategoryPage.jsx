import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import {
    ChevronDownIcon,
    ChevronLeftIcon,
    Squares2X2Icon,
    ListBulletIcon,
    FunnelIcon,
    MagnifyingGlassIcon
} from '@heroicons/react/24/outline';
import { toast } from 'react-hot-toast';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import ProductCard from '../components/products/ProductCard';
import { categoryService } from '../services/categoryService';
import productService from '../services/productService';

const CategoryPage = () => {
    const { categoryId } = useParams();
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();

    const [products, setProducts] = useState([]);
    const [category, setCategory] = useState(null);
    const [loading, setLoading] = useState(true);
    const [viewMode, setViewMode] = useState('grid');
    const [showFilters, setShowFilters] = useState(false);

    // Filtres
    const [filters, setFilters] = useState({
        sortBy: searchParams.get('sort') || 'newest',
        priceMin: searchParams.get('priceMin') || '',
        priceMax: searchParams.get('priceMax') || '',
        condition: searchParams.get('condition') || '',
        saleType: searchParams.get('saleType') || ''
    });

    useEffect(() => {
        loadCategoryData();
    }, [categoryId, filters]);

    const loadCategoryData = async () => {
        try {
            setLoading(true);

            // Charger la catégorie
            const categoryResponse = await categoryService.getById(categoryId);
            setCategory(categoryResponse.data);

            // Charger les produits de cette catégorie
            const searchParams = {
                categoryId: parseInt(categoryId),
                availableOnly: true
            };

            // Ajouter les filtres de prix
            if (filters.priceMin) {
                searchParams.minPrice = parseFloat(filters.priceMin);
            }
            if (filters.priceMax) {
                searchParams.maxPrice = parseFloat(filters.priceMax);
            }

            // Ajouter le type de vente
            if (filters.saleType) {
                searchParams.saleType = filters.saleType === 'auction' ? 'AUCTION' : 'DIRECT_SALE';
            }

            const productsData = await productService.searchProducts(searchParams);
            console.log('🔍 Products loaded:', productsData);
            console.log('📊 Number of products:', productsData?.length);

            // Debug: Afficher le premier produit pour vérifier la structure
            if (productsData && productsData.length > 0) {
                console.log('📦 First product sample:', productsData[0]);
                console.log('💰 First product price:', productsData[0]?.price);
            }

            setProducts(productsData || []);

        } catch (error) {
            console.error('❌ Error loading category data:', error);
            if (error.response?.status === 404) {
                toast.error('Catégorie non trouvée');
                navigate('/');
            } else {
                toast.error('Erreur lors du chargement des produits');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleFilterChange = (key, value) => {
        const newFilters = { ...filters, [key]: value };
        setFilters(newFilters);

        // Mettre à jour les paramètres URL
        const params = new URLSearchParams();
        Object.entries(newFilters).forEach(([k, v]) => {
            if (v) params.set(k, v);
        });
        setSearchParams(params);
    };


    const handleToggleFavorite = async (productId) => {
        try {
            const userId = JSON.parse(localStorage.getItem('user'))?.id;
            if (!userId) {
                toast.error('Veuillez vous connecter pour ajouter aux favoris');
                navigate('/login');
                return;
            }

            const product = products.find(p => p.id === productId);
            if (product?.isFavorited) {
                await productService.removeFavorite(userId, productId);
                toast.success('Retiré des favoris');
            } else {
                await productService.addFavorite(userId, productId);
                toast.success('Ajouté aux favoris');
            }

            setProducts(prevProducts =>
                prevProducts.map(product =>
                    product.id === productId
                        ? { ...product, isFavorited: !product.isFavorited }
                        : product
                )
            );
        } catch (error) {
            console.error('Error toggling favorite:', error);
            toast.error('Erreur lors de la mise à jour des favoris');
        }
    };

    const getSortedProducts = () => {
        let sortedProducts = [...products];

        switch (filters.sortBy) {
            case 'price-asc':
                sortedProducts.sort((a, b) => (a.price || 0) - (b.price || 0));
                break;
            case 'price-desc':
                sortedProducts.sort((a, b) => (b.price || 0) - (a.price || 0));
                break;
            case 'newest':
                sortedProducts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                break;
            case 'oldest':
                sortedProducts.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
                break;
            default:
                break;
        }

        return sortedProducts;
    };


    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                    <div className="animate-pulse">
                        <div className="h-8 bg-gray-300 rounded w-1/4 mb-4"></div>
                        <div className="h-6 bg-gray-300 rounded w-1/3 mb-8"></div>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                            {[...Array(8)].map((_, i) => (
                                <div key={i} className="bg-gray-300 rounded-lg h-80"></div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (!category) {
        return null;
    }

    const sortedProducts = getSortedProducts();

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
                        <span className="text-gray-900">{category.name}</span>
                    </div>
                </nav>

                {/* En-tête */}
                <div className="mb-8">
                    <div className="flex items-center gap-3 mb-2">
                        {category.icon && <span className="text-4xl">{category.icon}</span>}
                        <h1 className="text-3xl font-bold text-gray-900">{category.name}</h1>
                    </div>
                    {category.description && (
                        <p className="text-gray-600 mb-2">{category.description}</p>
                    )}
                    <p className="text-sm text-gray-500">
                        {sortedProducts.length} produit{sortedProducts.length > 1 ? 's' : ''} disponible{sortedProducts.length > 1 ? 's' : ''}
                    </p>
                </div>

                {/* Contrôles et filtres */}
                <div className="flex flex-col sm:flex-row gap-4 mb-6">
                    <div className="flex-1">
                        <div className="flex flex-wrap gap-4">
                            {/* Tri */}
                            <div className="relative">
                                <select
                                    value={filters.sortBy}
                                    onChange={(e) => handleFilterChange('sortBy', e.target.value)}
                                    className="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-8 text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                >
                                    <option value="newest">Plus récents</option>
                                    <option value="oldest">Plus anciens</option>
                                    <option value="price-asc">Prix croissant</option>
                                    <option value="price-desc">Prix décroissant</option>
                                </select>
                                <ChevronDownIcon className="absolute right-2 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500 pointer-events-none" />
                            </div>

                            {/* Type de vente */}
                            <div className="relative">
                                <select
                                    value={filters.saleType}
                                    onChange={(e) => handleFilterChange('saleType', e.target.value)}
                                    className="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-8 text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                >
                                    <option value="">Tous les types</option>
                                    <option value="direct">Vente directe</option>
                                    <option value="auction">Enchères</option>
                                </select>
                                <ChevronDownIcon className="absolute right-2 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500 pointer-events-none" />
                            </div>

                            {/* Bouton filtres avancés */}
                            <Button
                                variant="outline"
                                size="small"
                                onClick={() => setShowFilters(!showFilters)}
                                icon={<FunnelIcon className="w-4 h-4" />}
                            >
                                Filtres
                            </Button>
                        </div>

                        {/* Filtres avancés */}
                        {showFilters && (
                            <Card className="mt-4 p-4">
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            Prix minimum
                                        </label>
                                        <input
                                            type="number"
                                            value={filters.priceMin}
                                            onChange={(e) => handleFilterChange('priceMin', e.target.value)}
                                            placeholder="0 €"
                                            className="w-full p-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            Prix maximum
                                        </label>
                                        <input
                                            type="number"
                                            value={filters.priceMax}
                                            onChange={(e) => handleFilterChange('priceMax', e.target.value)}
                                            placeholder="10 000 €"
                                            className="w-full p-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                        />
                                    </div>
                                    <div className="flex items-end">
                                        <Button
                                            variant="outline"
                                            size="small"
                                            onClick={() => {
                                                setFilters({
                                                    sortBy: 'newest',
                                                    priceMin: '',
                                                    priceMax: '',
                                                    condition: '',
                                                    saleType: ''
                                                });
                                                setSearchParams({});
                                            }}
                                            className="w-full"
                                        >
                                            Réinitialiser
                                        </Button>
                                    </div>
                                </div>
                            </Card>
                        )}
                    </div>

                    {/* Modes d'affichage */}
                    <div className="flex items-center gap-2 bg-white rounded-lg border border-gray-300 p-1">
                        <button
                            onClick={() => setViewMode('grid')}
                            className={`p-2 rounded transition-colors ${
                                viewMode === 'grid'
                                    ? 'bg-purple-600 text-white'
                                    : 'text-gray-600 hover:bg-gray-100'
                            }`}
                            title="Grille"
                        >
                            <Squares2X2Icon className="w-5 h-5" />
                        </button>
                        <button
                            onClick={() => setViewMode('list')}
                            className={`p-2 rounded transition-colors ${
                                viewMode === 'list'
                                    ? 'bg-purple-600 text-white'
                                    : 'text-gray-600 hover:bg-gray-100'
                            }`}
                            title="Liste"
                        >
                            <ListBulletIcon className="w-5 h-5" />
                        </button>
                    </div>
                </div>

                {/* Produits */}
                {sortedProducts.length === 0 ? (
                    <div className="text-center py-16">
                        <MagnifyingGlassIcon className="w-24 h-24 text-gray-300 mx-auto mb-6" />
                        <h2 className="text-2xl font-bold text-gray-900 mb-4">Aucun produit trouvé</h2>
                        <p className="text-gray-600 mb-8">
                            Aucun produit n'est disponible dans cette catégorie pour le moment.
                        </p>
                        <Button
                            variant="primary"
                            onClick={() => navigate('/')}
                        >
                            Retour à l'accueil
                        </Button>
                    </div>
                ) : (
                    <div className={
                        viewMode === 'grid'
                            ? 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6'
                            : 'space-y-4'
                    }>
                        {sortedProducts.map((product) => (
                            <ProductCard
                                key={product.id}
                                product={product}
                                onToggleFavorite={handleToggleFavorite}
                                viewMode={viewMode}
                                showSeller={true}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default CategoryPage;


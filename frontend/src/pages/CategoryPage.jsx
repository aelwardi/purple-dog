import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import {
    AdjustmentsHorizontalIcon,
    ChevronDownIcon,
    ViewColumnsIcon,
    Squares2X2Icon,
    ListBulletIcon,
    HeartIcon,
    EyeIcon,
    ArrowLeftIcon,
    FunnelIcon
} from '@heroicons/react/24/outline';
import { HeartIcon as HeartSolid } from '@heroicons/react/24/solid';
import { toast } from 'react-hot-toast';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import { getCategoryProducts, getCategories } from '../services/categoryService';

const CategoryPage = () => {
    const { categoryId } = useParams();
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();

    const [products, setProducts] = useState([]);
    const [category, setCategory] = useState(null);
    const [loading, setLoading] = useState(true);
    const [viewMode, setViewMode] = useState('grid'); // 'grid' ou 'list'
    const [showFilters, setShowFilters] = useState(false);

    // Filtres
    const [filters, setFilters] = useState({
        sortBy: searchParams.get('sort') || 'newest',
        priceMin: searchParams.get('priceMin') || '',
        priceMax: searchParams.get('priceMax') || '',
        condition: searchParams.get('condition') || '',
        saleType: searchParams.get('saleType') || '',
        seller: searchParams.get('seller') || ''
    });

    // Pagination
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 12;

    useEffect(() => {
        loadCategoryData();
    }, [categoryId, filters]);

    const loadCategoryData = async () => {
        try {
            setLoading(true);

            // Charger les informations de la catégorie et les produits
            const [categoryData, productsData] = await Promise.all([
                getCategories(),
                getCategoryProducts(categoryId, filters)
            ]);

            const foundCategory = categoryData.find(cat => cat.id === parseInt(categoryId));
            if (!foundCategory) {
                toast.error('Catégorie non trouvée');
                navigate('/search');
                return;
            }

            setCategory(foundCategory);
            setProducts(productsData);
            
        } catch (error) {
            toast.error('Erreur lors du chargement des produits');
            console.error(error);
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
        
        setCurrentPage(1);
    };

    const handleToggleFavorite = async (productId) => {
        try {
            // Simuler l'appel API
            setProducts(prevProducts =>
                prevProducts.map(product =>
                    product.id === productId
                        ? { ...product, isFavorited: !product.isFavorited }
                        : product
                )
            );
            toast.success('Favoris mis à jour');
        } catch (error) {
            toast.error('Erreur lors de la mise à jour des favoris');
        }
    };

    const getSortedProducts = () => {
        let sortedProducts = [...products];
        
        switch (filters.sortBy) {
            case 'price-asc':
                sortedProducts.sort((a, b) => a.price - b.price);
                break;
            case 'price-desc':
                sortedProducts.sort((a, b) => b.price - a.price);
                break;
            case 'newest':
                sortedProducts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                break;
            case 'oldest':
                sortedProducts.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
                break;
            case 'popular':
                sortedProducts.sort((a, b) => (b.views || 0) - (a.views || 0));
                break;
            default:
                break;
        }
        
        return sortedProducts;
    };

    const getPaginatedProducts = () => {
        const sorted = getSortedProducts();
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        return sorted.slice(startIndex, endIndex);
    };

    const getTotalPages = () => {
        return Math.ceil(getSortedProducts().length / itemsPerPage);
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'EUR'
        }).format(price || 0);
    };

    const getConditionBadge = (condition) => {
        const conditionColors = {
            'Excellent': 'bg-green-100 text-green-800',
            'Très bon': 'bg-blue-100 text-blue-800',
            'Bon': 'bg-yellow-100 text-yellow-800',
            'Acceptable': 'bg-orange-100 text-orange-800',
            'Restauré': 'bg-purple-100 text-purple-800'
        };
        return conditionColors[condition] || 'bg-gray-100 text-gray-800';
    };

    const getSaleTypeBadge = (saleType) => {
        return saleType === 'auction' 
            ? 'bg-orange-100 text-orange-800' 
            : 'bg-green-100 text-green-800';
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                    <div className="animate-pulse">
                        <div className="h-8 bg-gray-300 rounded w-1/4 mb-4"></div>
                        <div className="h-6 bg-gray-300 rounded w-1/3 mb-8"></div>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                            {[...Array(12)].map((_, i) => (
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

    const paginatedProducts = getPaginatedProducts();
    const totalPages = getTotalPages();

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* En-tête */}
                <div className="mb-8">
                    <div className="flex items-center gap-4 mb-4">
                        <button
                            onClick={() => navigate(-1)}
                            className="p-2 rounded-lg hover:bg-gray-100 transition-colors"
                        >
                            <ArrowLeftIcon className="w-6 h-6 text-gray-600" />
                        </button>
                        <div className="flex-1">
                            <div className="flex items-center gap-3 mb-2">
                                <span className="text-4xl">{category.icon}</span>
                                <h1 className="text-3xl font-bold text-gray-900">{category.name}</h1>
                            </div>
                            <p className="text-gray-600">
                                {products.length} produit{products.length > 1 ? 's' : ''} disponible{products.length > 1 ? 's' : ''}
                            </p>
                        </div>
                    </div>

                    {/* Image de couverture de la catégorie */}
                    {category.image && (
                        <div className="relative h-48 rounded-lg overflow-hidden mb-6">
                            <img
                                src={category.image}
                                alt={category.name}
                                className="w-full h-full object-cover"
                            />
                            <div className="absolute inset-0 bg-gradient-to-t from-black/40 to-transparent" />
                        </div>
                    )}
                </div>

                {/* Contrôles et filtres */}
                <div className="flex flex-col sm:flex-row gap-4 mb-6">
                    {/* Filtres */}
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
                                    <option value="popular">Populaires</option>
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

                            {/* État */}
                            <div className="relative">
                                <select
                                    value={filters.condition}
                                    onChange={(e) => handleFilterChange('condition', e.target.value)}
                                    className="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-8 text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                                >
                                    <option value="">Tous les états</option>
                                    <option value="Excellent">Excellent</option>
                                    <option value="Très bon">Très bon</option>
                                    <option value="Bon">Bon</option>
                                    <option value="Acceptable">Acceptable</option>
                                    <option value="Restauré">Restauré</option>
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
                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
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
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            Vendeur
                                        </label>
                                        <input
                                            type="text"
                                            value={filters.seller}
                                            onChange={(e) => handleFilterChange('seller', e.target.value)}
                                            placeholder="Nom du vendeur"
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
                                                    saleType: '',
                                                    seller: ''
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
                            <Squares2X2Icon className="w-4 h-4" />
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
                            <ListBulletIcon className="w-4 h-4" />
                        </button>
                    </div>
                </div>

                {/* Produits */}
                {paginatedProducts.length === 0 ? (
                    <div className="text-center py-16">
                        <EyeIcon className="w-24 h-24 text-gray-300 mx-auto mb-6" />
                        <h2 className="text-2xl font-bold text-gray-900 mb-4">Aucun produit trouvé</h2>
                        <p className="text-gray-600 mb-8">
                            Essayez de modifier vos critères de recherche
                        </p>
                        <Button
                            variant="primary"
                            onClick={() => {
                                setFilters({
                                    sortBy: 'newest',
                                    priceMin: '',
                                    priceMax: '',
                                    condition: '',
                                    saleType: '',
                                    seller: ''
                                });
                                setSearchParams({});
                            }}
                        >
                            Réinitialiser les filtres
                        </Button>
                    </div>
                ) : (
                    <>
                        {/* Grille de produits */}
                        {viewMode === 'grid' ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-8">
                                {paginatedProducts.map((product) => (
                                    <Card
                                        key={product.id}
                                        className="group cursor-pointer overflow-hidden"
                                        onClick={() => navigate(`/products/${product.id}`)}
                                    >
                                        <div className="relative aspect-square bg-gray-100 overflow-hidden">
                                            <img
                                                src={product.image}
                                                alt={product.title}
                                                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                            />
                                            {/* Badges */}
                                            <div className="absolute top-3 left-3 flex flex-col gap-2">
                                                <Badge className={getSaleTypeBadge(product.saleType)}>
                                                    {product.saleType === 'auction' ? 'Enchères' : 'Vente directe'}
                                                </Badge>
                                                <Badge className={getConditionBadge(product.condition)}>
                                                    {product.condition}
                                                </Badge>
                                            </div>
                                            {/* Bouton favoris */}
                                            <button
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    handleToggleFavorite(product.id);
                                                }}
                                                className="absolute top-3 right-3 p-2 bg-white bg-opacity-80 rounded-full hover:bg-opacity-100 transition-opacity"
                                            >
                                                {product.isFavorited ? (
                                                    <HeartSolid className="w-5 h-5 text-red-500" />
                                                ) : (
                                                    <HeartIcon className="w-5 h-5 text-gray-600" />
                                                )}
                                            </button>
                                        </div>
                                        <div className="p-4">
                                            <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2 group-hover:text-purple-600 transition-colors">
                                                {product.title}
                                            </h3>
                                            <p className="text-sm text-gray-600 mb-2">
                                                Par {product.seller?.name || 'Vendeur anonyme'}
                                            </p>
                                            <div className="flex items-center justify-between">
                                                <span className="text-xl font-bold text-purple-600">
                                                    {formatPrice(product.price)}
                                                </span>
                                                <div className="flex items-center gap-1 text-xs text-gray-500">
                                                    <EyeIcon className="w-3 h-3" />
                                                    <span>{product.views || 0}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </Card>
                                ))}
                            </div>
                        ) : (
                            /* Vue liste */
                            <div className="space-y-4 mb-8">
                                {paginatedProducts.map((product) => (
                                    <Card
                                        key={product.id}
                                        className="group cursor-pointer"
                                        onClick={() => navigate(`/products/${product.id}`)}
                                    >
                                        <div className="flex gap-4 p-4">
                                            <div className="flex-shrink-0">
                                                <div className="relative w-32 h-32 bg-gray-100 rounded-lg overflow-hidden">
                                                    <img
                                                        src={product.image}
                                                        alt={product.title}
                                                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                                    />
                                                </div>
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <div className="flex items-start justify-between">
                                                    <div className="flex-1">
                                                        <h3 className="text-lg font-semibold text-gray-900 mb-1 group-hover:text-purple-600 transition-colors">
                                                            {product.title}
                                                        </h3>
                                                        <p className="text-sm text-gray-600 mb-2">
                                                            Par {product.seller?.name || 'Vendeur anonyme'}
                                                        </p>
                                                        <p className="text-sm text-gray-500 line-clamp-2 mb-3">
                                                            {product.description}
                                                        </p>
                                                        <div className="flex items-center gap-2 mb-2">
                                                            <Badge className={getSaleTypeBadge(product.saleType)}>
                                                                {product.saleType === 'auction' ? 'Enchères' : 'Vente directe'}
                                                            </Badge>
                                                            <Badge className={getConditionBadge(product.condition)}>
                                                                {product.condition}
                                                            </Badge>
                                                        </div>
                                                    </div>
                                                    <div className="text-right ml-4">
                                                        <div className="text-2xl font-bold text-purple-600 mb-2">
                                                            {formatPrice(product.price)}
                                                        </div>
                                                        <button
                                                            onClick={(e) => {
                                                                e.stopPropagation();
                                                                handleToggleFavorite(product.id);
                                                            }}
                                                            className="p-2 rounded-lg hover:bg-gray-100 transition-colors"
                                                        >
                                                            {product.isFavorited ? (
                                                                <HeartSolid className="w-5 h-5 text-red-500" />
                                                            ) : (
                                                                <HeartIcon className="w-5 h-5 text-gray-600" />
                                                            )}
                                                        </button>
                                                        <div className="flex items-center gap-1 text-xs text-gray-500 mt-1">
                                                            <EyeIcon className="w-3 h-3" />
                                                            <span>{product.views || 0}</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </Card>
                                ))}
                            </div>
                        )}

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <div className="flex items-center justify-center gap-2">
                                <Button
                                    variant="outline"
                                    size="small"
                                    onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                                    disabled={currentPage === 1}
                                >
                                    Précédent
                                </Button>
                                
                                {[...Array(totalPages)].map((_, index) => (
                                    <Button
                                        key={index + 1}
                                        variant={currentPage === index + 1 ? 'primary' : 'outline'}
                                        size="small"
                                        onClick={() => setCurrentPage(index + 1)}
                                        className="w-10 h-10"
                                    >
                                        {index + 1}
                                    </Button>
                                ))}
                                
                                <Button
                                    variant="outline"
                                    size="small"
                                    onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                                    disabled={currentPage === totalPages}
                                >
                                    Suivant
                                </Button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
};

export default CategoryPage;

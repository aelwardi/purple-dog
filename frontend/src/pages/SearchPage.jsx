import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { productService } from '../services';
import { useErrorHandler } from '../hooks/useErrorHandler';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import { MagnifyingGlassIcon } from '@heroicons/react/24/outline';

const SearchPage = () => {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const { handleError } = useErrorHandler();

  useEffect(() => {
    if (!query.trim()) {
      setResults([]);
      return;
    }

    const searchProducts = async () => {
      setIsLoading(true);
      try {
        // Appel API réel au backend
        const data = await productService.search({ keyword: query });
        setResults(data);
      } catch (error) {
        handleError(error);
        setResults([]);
      } finally {
        setIsLoading(false);
      }
    };

    searchProducts();
  }, [query]);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header de recherche */}
        <div className="mb-8">
          <h1 className="text-3xl font-display font-bold text-gray-900 mb-2">
            Résultats de recherche
          </h1>
          {query && (
            <p className="text-gray-600">
              {isLoading ? (
                'Recherche en cours...'
              ) : (
                <>
                  <span className="font-medium">{results.length}</span> résultat{results.length !== 1 ? 's' : ''} pour "{query}"
                </>
              )}
            </p>
          )}
        </div>

        {/* Contenu */}
        {!query.trim() ? (
          // État initial - pas de recherche
          <div className="text-center py-16">
            <MagnifyingGlassIcon className="mx-auto h-16 w-16 text-gray-400 mb-4" />
            <h2 className="text-2xl font-semibold text-gray-900 mb-2">
              Recherchez des objets d'art et antiquités
            </h2>
            <p className="text-gray-600 mb-8">
              Utilisez la barre de recherche ci-dessus pour trouver des pièces uniques
            </p>
            
            {/* Catégories suggérées */}
            <div className="max-w-2xl mx-auto">
              <p className="text-sm text-gray-500 mb-4">Catégories populaires :</p>
              <div className="flex flex-wrap justify-center gap-2">
                {['Peinture', 'Sculpture', 'Mobilier', 'Porcelaine', 'Horlogerie', 'Argenterie'].map(category => (
                  <Link 
                    key={category}
                    to={`/search?q=${encodeURIComponent(category)}`}
                  >
                    <Badge className="cursor-pointer hover:bg-purple-100 transition-colors">
                      {category}
                    </Badge>
                  </Link>
                ))}
              </div>
            </div>
          </div>
        ) : isLoading ? (
          // État de chargement
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {[1, 2, 3, 4, 5, 6, 7, 8].map(i => (
              <div key={i} className="animate-pulse">
                <div className="bg-gray-200 h-64 rounded-lg mb-4"></div>
                <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                <div className="h-4 bg-gray-200 rounded w-1/2"></div>
              </div>
            ))}
          </div>
        ) : results.length > 0 ? (
          // Résultats trouvés
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {results.map(product => (
              <Card key={product.id} className="group hover:shadow-xl transition-shadow duration-300">
                {product.image && (
                  <div className="relative h-64 overflow-hidden rounded-t-lg">
                    <img 
                      src={product.image} 
                      alt={product.title}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                    <div className="absolute top-3 right-3">
                      <Badge className="bg-white/90 backdrop-blur-sm">
                        {product.category}
                      </Badge>
                    </div>
                  </div>
                )}
                <div className="p-4">
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2 group-hover:text-purple-600 transition-colors">
                    {product.name || product.title}
                  </h3>
                  <p className="text-sm text-gray-600 mb-3 line-clamp-2">
                    {product.description}
                  </p>
                  <div className="flex items-center justify-between">
                    {product.price && (
                      <p className="text-xl font-bold text-purple-600">
                        {product.price.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' })}
                      </p>
                    )}
                    {product.condition && (
                      <Badge variant={
                        product.condition === 'Excellent' ? 'success' : 
                        product.condition === 'Très bon' ? 'info' : 
                        'secondary'
                      }>
                        {product.condition}
                      </Badge>
                    )}
                  </div>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          // Aucun résultat
          <div className="text-center py-16">
            <div className="max-w-md mx-auto">
              <MagnifyingGlassIcon className="mx-auto h-16 w-16 text-gray-400 mb-4" />
              <h2 className="text-2xl font-semibold text-gray-900 mb-2">
                Aucun résultat trouvé
              </h2>
              <p className="text-gray-600 mb-6">
                Nous n'avons pas trouvé de produits correspondant à "{query}"
              </p>
              <div className="space-y-3 text-left bg-gray-50 p-6 rounded-lg">
                <p className="text-sm font-medium text-gray-900">Suggestions :</p>
                <ul className="text-sm text-gray-600 space-y-2 list-disc list-inside">
                  <li>Vérifiez l'orthographe de vos mots-clés</li>
                  <li>Essayez des termes plus généraux</li>
                  <li>Essayez différents mots-clés</li>
                  <li>Parcourez nos catégories populaires ci-dessous</li>
                </ul>
              </div>
              <div className="mt-6 flex flex-wrap justify-center gap-2">
                {['Peinture', 'Sculpture', 'Mobilier', 'Porcelaine'].map(category => (
                  <Link 
                    key={category}
                    to={`/search?q=${encodeURIComponent(category)}`}
                  >
                    <Badge className="cursor-pointer hover:bg-purple-100 transition-colors">
                      {category}
                    </Badge>
                  </Link>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchPage;

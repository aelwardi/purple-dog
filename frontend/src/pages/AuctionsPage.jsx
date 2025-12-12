import { useState, useEffect } from 'react';
import { Gavel, Search, Filter } from 'lucide-react';
import AuctionCard from '../components/auctions/AuctionCard';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import { auctionService } from '../services/auctionService';

/**
 * Page publique de liste des enchères
 */
const AuctionsPage = () => {
  const [auctions, setAuctions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [filter, setFilter] = useState('active'); // 'active', 'all', 'closed'

  useEffect(() => {
    loadAuctions();
  }, [filter]);

  const loadAuctions = async () => {
    setLoading(true);
    setError('');
    
    try {
      let response;
      if (filter === 'active') {
        response = await auctionService.getActive();
      } else if (filter === 'closed') {
        response = await auctionService.getClosed();
      } else {
        response = await auctionService.getAll();
      }
      setAuctions(response.data || []);
    } catch (err) {
      console.error('Erreur lors du chargement des enchères:', err);
      setError('Impossible de charger les enchères');
    } finally {
      setLoading(false);
    }
  };

  const filteredAuctions = auctions.filter((auction) => {
    if (!searchTerm) return true;
    const searchLower = searchTerm.toLowerCase();
    return (
      auction.product?.title?.toLowerCase().includes(searchLower) ||
      auction.product?.description?.toLowerCase().includes(searchLower)
    );
  });

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-2">
            <Gavel size={32} className="text-purple-600" />
            <h1 className="text-3xl font-bold text-gray-800">Enchères</h1>
          </div>
          <p className="text-gray-600">
            Participez aux enchères et trouvez les meilleures affaires
          </p>
        </div>

        {/* Filters and Search */}
        <div className="bg-white rounded-lg shadow-md p-4 mb-6">
          <div className="flex flex-col md:flex-row gap-4">
            {/* Search */}
            <div className="flex-1">
              <div className="relative">
                <Search
                  size={20}
                  className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
                />
                <Input
                  type="text"
                  placeholder="Rechercher une enchère..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            {/* Filter buttons */}
            <div className="flex gap-2">
              <Button
                variant={filter === 'active' ? 'primary' : 'outline'}
                onClick={() => setFilter('active')}
                size="sm"
              >
                En cours
              </Button>
              <Button
                variant={filter === 'all' ? 'primary' : 'outline'}
                onClick={() => setFilter('all')}
                size="sm"
              >
                Toutes
              </Button>
              <Button
                variant={filter === 'closed' ? 'primary' : 'outline'}
                onClick={() => setFilter('closed')}
                size="sm"
              >
                Terminées
              </Button>
            </div>
          </div>
        </div>

        {/* Loading */}
        {loading && (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
            <p className="text-gray-600 mt-4">Chargement des enchères...</p>
          </div>
        )}

        {/* Error */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-600">
            {error}
          </div>
        )}

        {/* No results */}
        {!loading && !error && filteredAuctions.length === 0 && (
          <div className="text-center py-12">
            <Gavel size={64} className="mx-auto text-gray-300 mb-4" />
            <h3 className="text-xl font-semibold text-gray-600 mb-2">
              Aucune enchère trouvée
            </h3>
            <p className="text-gray-500">
              {searchTerm
                ? 'Essayez avec d\'autres mots-clés'
                : 'Il n\'y a pas d\'enchères disponibles pour le moment'}
            </p>
          </div>
        )}

        {/* Auctions Grid */}
        {!loading && !error && filteredAuctions.length > 0 && (
          <div>
            <p className="text-sm text-gray-600 mb-4">
              {filteredAuctions.length} enchère{filteredAuctions.length > 1 ? 's' : ''} trouvée{filteredAuctions.length > 1 ? 's' : ''}
            </p>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {filteredAuctions.map((auction) => (
                <AuctionCard key={auction.id} auction={auction} />
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuctionsPage;

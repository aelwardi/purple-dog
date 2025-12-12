import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Gavel, User, AlertCircle, CreditCard } from 'lucide-react';
import AuctionTimer from '../components/auctions/AuctionTimer';
import BidHistory from '../components/auctions/BidHistory';
import PlaceBidForm from '../components/auctions/PlaceBidForm';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import { auctionService } from '../services/auctionService';
import { bidService } from '../services/bidService';
import { useAuth } from '../hooks/useAuth';

/**
 * Page de détail d'une enchère avec bidding en temps réel
 */
const AuctionDetailPage = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const [auction, setAuction] = useState(null);
  const [bids, setBids] = useState([]);
  const [nextBidAmount, setNextBidAmount] = useState(null);
  const [winningBid, setWinningBid] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    loadAuctionData();
    
    // Rafraîchir les données toutes les 10 secondes (en attendant WebSocket)
    const interval = setInterval(() => {
      loadBids();
    }, 10000);

    return () => clearInterval(interval);
  }, [id]);

  const loadAuctionData = async () => {
    setLoading(true);
    setError('');
    
    try {
      const [auctionResponse, bidsResponse, nextAmountResponse, winningResponse] = await Promise.all([
        auctionService.getById(id),
        bidService.getAuctionBids(id),
        bidService.getNextBidAmount(id).catch(() => ({ data: null })),
        bidService.getCurrentWinningBid(id).catch(() => ({ data: null })),
      ]);

      setAuction(auctionResponse.data);
      setBids(bidsResponse.data || []);
      setNextBidAmount(nextAmountResponse.data);
      setWinningBid(winningResponse.data);
    } catch (err) {
      console.error('Erreur lors du chargement:', err);
      setError('Impossible de charger les détails de l\'enchère');
    } finally {
      setLoading(false);
    }
  };

  const loadBids = async () => {
    try {
      const [bidsResponse, winningResponse] = await Promise.all([
        bidService.getAuctionBids(id),
        bidService.getCurrentWinningBid(id).catch(() => ({ data: null })),
      ]);
      setBids(bidsResponse.data || []);
      setWinningBid(winningResponse.data);
    } catch (err) {
      console.error('Erreur lors du rafraîchissement:', err);
    }
  };

  const handlePlaceBid = async (amount) => {
    try {
      await bidService.placeBid({
        auctionId: parseInt(id),
        amount: amount,
      });
      
      setSuccessMessage('Enchère placée avec succès !');
      setTimeout(() => setSuccessMessage(''), 5000);
      
      // Recharger les données
      await loadAuctionData();
    } catch (err) {
      throw err; // Le formulaire gérera l'erreur
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
          <p className="text-gray-600 mt-4">Chargement...</p>
        </div>
      </div>
    );
  }

  if (error || !auction) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <AlertCircle size={64} className="mx-auto text-red-500 mb-4" />
          <h2 className="text-2xl font-bold text-gray-800 mb-2">Erreur</h2>
          <p className="text-gray-600">{error || 'Enchère introuvable'}</p>
          <Link to="/auctions">
            <Button className="mt-4">Retour aux enchères</Button>
          </Link>
        </div>
      </div>
    );
  }

  const product = auction.product || {};
  const isActive = auction.status === 'ACTIVE';
  const isProfessional = user?.role === 'PROFESSIONAL';
  const canBid = isProfessional && isActive;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container mx-auto px-4">
        {/* Back button */}
        <Link
          to="/auctions"
          className="inline-flex items-center gap-2 text-purple-600 hover:text-purple-700 mb-6"
        >
          <ArrowLeft size={20} />
          Retour aux enchères
        </Link>

        {/* Success message */}
        {successMessage && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg text-green-700">
            {successMessage}
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Left: Product Info */}
          <div>
            {/* Image */}
            <div className="bg-white rounded-lg shadow-md overflow-hidden mb-6">
              <img
                src={product.photos?.[0]?.url || '/placeholder-product.jpg'}
                alt={product.title}
                className="w-full h-96 object-cover"
              />
            </div>

            {/* Product Details */}
            <div className="bg-white rounded-lg shadow-md p-6">
              <div className="flex items-start justify-between mb-4">
                <h1 className="text-2xl font-bold text-gray-800">{product.title}</h1>
                <Badge variant={isActive ? 'success' : 'secondary'}>
                  {isActive ? 'En cours' : 'Terminée'}
                </Badge>
              </div>

              <p className="text-gray-600 mb-6">{product.description}</p>

              {/* Auction Info */}
              <div className="grid grid-cols-2 gap-4 mb-6">
                <div>
                  <p className="text-sm text-gray-600">Prix de départ</p>
                  <p className="text-lg font-semibold">{auction.startingPrice?.toFixed(2)} €</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Prix actuel</p>
                  <p className="text-2xl font-bold text-purple-600">
                    {auction.currentPrice?.toFixed(2)} €
                  </p>
                </div>
                {auction.reservePrice && (
                  <div>
                    <p className="text-sm text-gray-600">Prix de réserve</p>
                    <p className="text-lg font-semibold">{auction.reservePrice.toFixed(2)} €</p>
                  </div>
                )}
                <div>
                  <p className="text-sm text-gray-600">Nombre d'enchères</p>
                  <p className="text-lg font-semibold">{auction.totalBids || 0}</p>
                </div>
              </div>

              {/* Timer */}
              {isActive && (
                <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
                  <p className="text-sm text-gray-600 mb-2">Temps restant:</p>
                  <AuctionTimer endDate={auction.endDate} className="text-2xl" />
                </div>
              )}
            </div>
          </div>

          {/* Right: Bidding */}
          <div>
            <div className="bg-white rounded-lg shadow-md p-6 sticky top-6">
              <h2 className="text-xl font-bold mb-6 flex items-center gap-2">
                <Gavel size={24} />
                Enchérir
              </h2>

              {/* Bidding Form or Messages */}
              {!user && (
                <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg flex items-start gap-3">
                  <User size={20} className="text-blue-600 flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="text-blue-800 font-semibold mb-1">Connexion requise</p>
                    <p className="text-sm text-blue-700">
                      Vous devez être connecté en tant que professionnel pour enchérir.
                    </p>
                    <Link to="/login">
                      <Button size="sm" className="mt-3">Se connecter</Button>
                    </Link>
                  </div>
                </div>
              )}

              {user && !isProfessional && (
                <div className="p-4 bg-amber-50 border border-amber-200 rounded-lg flex items-start gap-3">
                  <AlertCircle size={20} className="text-amber-600 flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="text-amber-800 font-semibold mb-1">
                      Réservé aux professionnels
                    </p>
                    <p className="text-sm text-amber-700">
                      Seuls les professionnels peuvent participer aux enchères.
                    </p>
                  </div>
                </div>
              )}

              {canBid && (
                <div>
                  <PlaceBidForm
                    auctionId={auction.id}
                    currentPrice={auction.currentPrice}
                    nextBidAmount={nextBidAmount}
                    onBidPlaced={handlePlaceBid}
                  />
                  <div className="mt-4 p-3 bg-purple-50 border border-purple-200 rounded text-sm text-purple-700">
                    <CreditCard size={16} className="inline mr-2" />
                    Un moyen de paiement est requis pour enchérir
                  </div>
                </div>
              )}

              {!isActive && (
                <div className="text-center py-8 text-gray-500">
                  <p className="font-semibold">Cette enchère est terminée</p>
                </div>
              )}

              {/* Bid History */}
              <div className="mt-8">
                <BidHistory
                  bids={bids}
                  currentWinningBidId={winningBid?.id}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuctionDetailPage;

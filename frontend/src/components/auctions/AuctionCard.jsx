import { Link } from 'react-router-dom';
import AuctionTimer from './AuctionTimer';
import Badge from '../common/Badge';
import { Clock, Gavel } from 'lucide-react';

/**
 * Carte d'enchère pour la liste
 */
const AuctionCard = ({ auction }) => {
  const product = auction.product || {};
  const imageUrl = product.photos?.[0]?.url || '/placeholder-product.jpg';

  return (
    <Link to={`/auctions/${auction.id}`} className="block">
      <div className="bg-white rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300 overflow-hidden">
        {/* Image */}
        <div className="relative h-48 bg-gray-200">
          <img
            src={imageUrl}
            alt={product.title}
            className="w-full h-full object-cover"
          />
          {/* Badge statut */}
          <div className="absolute top-2 right-2">
            <Badge variant={auction.status === 'ACTIVE' ? 'success' : 'secondary'}>
              {auction.status === 'ACTIVE' ? 'En cours' : 'Terminée'}
            </Badge>
          </div>
        </div>

        {/* Contenu */}
        <div className="p-4">
          {/* Titre */}
          <h3 className="font-semibold text-lg text-gray-800 mb-2 line-clamp-2">
            {product.title}
          </h3>

          {/* Prix actuel */}
          <div className="mb-3">
            <p className="text-sm text-gray-600">Prix actuel</p>
            <p className="text-2xl font-bold text-purple-600">
              {auction.currentPrice?.toFixed(2)} €
            </p>
          </div>

          {/* Infos */}
          <div className="flex items-center justify-between text-sm text-gray-600 mb-3">
            <div className="flex items-center gap-1">
              <Gavel size={16} />
              <span>{auction.totalBids || 0} enchère{auction.totalBids > 1 ? 's' : ''}</span>
            </div>
            {auction.startingPrice && (
              <span className="text-gray-500">
                Départ: {auction.startingPrice.toFixed(2)} €
              </span>
            )}
          </div>

          {/* Timer */}
          {auction.status === 'ACTIVE' && (
            <div className="flex items-center gap-2 p-2 bg-gray-50 rounded">
              <Clock size={16} className="text-gray-600" />
              <span className="text-sm text-gray-600">Fin dans:</span>
              <AuctionTimer endDate={auction.endDate} />
            </div>
          )}
        </div>
      </div>
    </Link>
  );
};

export default AuctionCard;

import { Trophy, TrendingUp, Gavel } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';

/**
 * Composant d'historique des enchères (anonymisé)
 */
const BidHistory = ({ bids = [], currentWinningBidId }) => {
  if (bids.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        <Gavel size={48} className="mx-auto mb-2 opacity-50" />
        <p>Aucune enchère pour le moment</p>
        <p className="text-sm">Soyez le premier à enchérir !</p>
      </div>
    );
  }

  return (
    <div className="space-y-2">
      <h3 className="font-semibold text-lg mb-3 flex items-center gap-2">
        <TrendingUp size={20} />
        Historique des enchères
      </h3>
      
      <div className="max-h-96 overflow-y-auto">
        {bids.map((bid, index) => {
          const isWinning = bid.id === currentWinningBidId;
          const bidDate = new Date(bid.bidDate);
          
          return (
            <div
              key={bid.id}
              className={`p-3 rounded-lg border ${
                isWinning
                  ? 'bg-green-50 border-green-300'
                  : 'bg-gray-50 border-gray-200'
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  {isWinning && (
                    <Trophy size={20} className="text-green-600" />
                  )}
                  <div>
                    <p className="font-semibold text-gray-800">
                      {bid.bidderDisplayName || `Enchérisseur #${index + 1}`}
                    </p>
                    <p className="text-xs text-gray-500">
                      {format(bidDate, 'dd/MM/yyyy à HH:mm', { locale: fr })}
                    </p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-lg font-bold text-purple-600">
                    {bid.amount.toFixed(2)} €
                  </p>
                  {isWinning && (
                    <p className="text-xs text-green-600 font-semibold">
                      En tête
                    </p>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default BidHistory;

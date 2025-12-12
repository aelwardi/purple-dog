// filepath: frontend/src/components/products/BidList.jsx
import React, { useContext } from 'react';
import Badge from '../common/Badge';
import { AuthContext } from '../../contexts/AuthContext';

const BidList = ({ bids = [], highestAmount = 0 }) => {
  const { user } = useContext(AuthContext) || {};

  if (!Array.isArray(bids) || bids.length === 0) {
    return <div className="text-sm text-gray-500">Aucune offre pour l'instant</div>;
  }

  // Sort descending by createdAt or by amount
  const sorted = [...bids].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

  return (
    <div className="space-y-3">
      {sorted.map((bid) => {
        const amt = Number(bid.amount ?? bid.value ?? 0);
        const isOutbid = amt < highestAmount;
        const isMine = user && bid.bidder && Number(bid.bidder.id) === Number(user.id);

        return (
          <div key={bid.id} className={`flex items-center justify-between p-3 border rounded-md ${isMine ? 'bg-purple-50' : 'bg-white'}`}>
            <div>
              <div className="flex items-center gap-2">
                <div className="font-semibold text-gray-900">{amt.toFixed(2)} €</div>
                {isOutbid && <Badge className="bg-red-100 text-red-800">Surpassé</Badge>}
                {isMine && <Badge className="bg-purple-100 text-purple-800">Votre offre</Badge>}
              </div>
              <div className="text-xs text-gray-500">par {bid.bidder?.firstName || bid.bidder?.name || 'Utilisateur'} · {new Date(bid.createdAt).toLocaleString()}</div>
            </div>

            <div className="text-sm text-gray-600">{/* possible actions */}</div>
          </div>
        );
      })}
    </div>
  );
};

export default BidList;


// filepath: frontend/src/components/products/AuctionBidsPanel.jsx
import React from 'react';
import Card from '../common/Card';
import BidForm from './BidForm';
import BidList from './BidList';
import useAuctionBids from '../../hooks/useAuctionBids';
import { toast } from 'react-hot-toast';

const AuctionBidsPanel = ({ auctionId, auction }) => {
  const { bids, highestBid, placeBid, notifyLowerBidders } = useAuctionBids(auctionId);

  const basePrice = auction?.basePrice ?? auction?.startingPrice ?? 0;

  const onBidPlaced = async (amount) => {
    try {
      const created = await placeBid(amount);
      // try to notify lower bidders (best effort)
      try {
        await notifyLowerBidders(created.id);
      } catch (e) {
        // best-effort notification; log warning so lint doesn't complain
        console.warn('notifyLowerBidders failed', e);
      }
      toast.success('Votre offre a été enregistrée');
      return created;
    } catch (err) {
      console.error('AuctionBidsPanel onBidPlaced error', err);
      toast.error(err?.response?.data?.message || err?.message || 'Erreur lors de l\'offre');
      throw err;
    }
  };

  return (
    <Card className="p-4">
      <h3 className="text-lg font-semibold text-gray-900 mb-2">Enchères</h3>

      <div className="mb-4">
        <div className="text-sm text-gray-600">Prix de départ</div>
        <div className="text-2xl font-bold text-purple-600">{(basePrice || 0).toFixed(2)} €</div>
      </div>

      <div className="mb-4">
        <div className="text-sm text-gray-600">Meilleure offre</div>
        <div className="text-xl font-semibold text-orange-600">{(highestBid || basePrice || 0).toFixed(2)} €</div>
      </div>

      <div className="mb-4">
        <BidForm auctionId={auctionId} highestBid={highestBid} basePrice={basePrice} onBidPlaced={onBidPlaced} />
      </div>

      <div>
        <h4 className="text-sm font-medium text-gray-800 mb-2">Historique des offres</h4>
        <BidList bids={bids} highestAmount={highestBid || basePrice || 0} />
      </div>
    </Card>
  );
};

export default AuctionBidsPanel;

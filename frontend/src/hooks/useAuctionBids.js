// filepath: frontend/src/hooks/useAuctionBids.js
import { useEffect, useState, useCallback } from 'react';
import auctionService from '../services/auctionService';

/**
 * Hook to manage auction bids state and actions
 * @param {number|string} auctionId
 */
export default function useAuctionBids(auctionId) {
  const [bids, setBids] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchBids = useCallback(async () => {
    if (!auctionId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await auctionService.getBids(auctionId);
      // expect data to be an array of bids
      setBids(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err);
      console.error('useAuctionBids - fetchBids error', err);
    } finally {
      setLoading(false);
    }
  }, [auctionId]);

  useEffect(() => {
    fetchBids();
    // optional: add polling or socket subscription in the future
  }, [fetchBids]);

  const highestBid = bids.reduce((max, b) => {
    const amount = Number(b.amount ?? b.value ?? 0);
    return amount > max ? amount : max;
  }, 0);

  const placeBid = useCallback(async (amount) => {
    if (!auctionId) throw new Error('Invalid auctionId');
    const created = await auctionService.placeBid(auctionId, amount);
    // refresh bids after placing
    await fetchBids();
    return created;
  }, [auctionId, fetchBids]);

  const notifyLowerBidders = useCallback(async (bidId) => {
    if (!auctionId || !bidId) return null;
    try {
      const res = await auctionService.notifyLowerBidders(auctionId, bidId);
      return res;
    } catch (err) {
      // If endpoint missing, just ignore
      console.warn('notifyLowerBidders failed', err);
      return null;
    }
  }, [auctionId]);

  return {
    bids,
    loading,
    error,
    fetchBids,
    highestBid,
    placeBid,
    notifyLowerBidders,
  };
}


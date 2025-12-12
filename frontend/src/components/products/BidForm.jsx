// filepath: frontend/src/components/products/BidForm.jsx
import React, { useState, useContext } from 'react';
import Button from '../common/Button';
import { toast } from 'react-hot-toast';
import { AuthContext } from '../../contexts/AuthContext';

const BidForm = ({ highestBid, basePrice, onBidPlaced }) => {
  const { user } = useContext(AuthContext) || {};
  const [amount, setAmount] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const parseAmount = (val) => {
    if (!val) return 0;
    // accept comma or dot
    return parseFloat(String(val).replace(',', '.')) || 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!user) {
      toast.error('Veuillez vous connecter pour enchérir');
      window.location.href = '/login';
      return;
    }

    // Ensure only professionals can bid
    if (user.role !== 'PROFESSIONAL') {
      toast.error('Seuls les professionnels peuvent proposer des offres. Connectez-vous avec un compte professionnel.');
      return;
    }

    const amt = parseAmount(amount);

    if (!amt || amt <= 0) {
      setError('Veuillez entrer un montant valide');
      return;
    }

    if (highestBid > 0 && amt <= highestBid) {
      setError('Votre offre doit être supérieure à l\'offre actuelle');
      return;
    }

    if (highestBid === 0 && basePrice && amt < basePrice) {
      setError(`Le montant doit être au moins le prix de départ: ${basePrice}`);
      return;
    }

    try {
      setSubmitting(true);
      const created = await onBidPlaced(amt);
      toast.success('Offre placée');
      setAmount('');
      return created;
    } catch (err) {
      console.error('BidForm submit error', err);
      // Normalize error into a readable string
      let msg = 'Erreur lors de la soumission';
      if (err?.response?.data) {
        if (typeof err.response.data === 'string') {
          msg = err.response.data;
        } else if (err.response.data.message) {
          msg = err.response.data.message;
        } else {
          try {
            msg = JSON.stringify(err.response.data);
          } catch (e) {
            msg = String(err.response.data);
          }
        }
      } else if (err?.message) {
        msg = err.message;
      }
      setError(msg);
      toast.error(msg.toString());
      throw err;
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-2">
      <div>
        <label className="text-sm font-medium text-gray-700">Votre offre (EUR)</label>
        <input
          type="number"
          step="0.01"
          min="0"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="mt-1 block w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-purple-500"
          placeholder={highestBid > 0 ? `Supérieur à ${highestBid}` : `Au moins ${basePrice || 0}`}
        />
        {error && <p className="text-red-600 text-sm mt-1">{error}</p>}
      </div>

      <div className="flex gap-2">
        <Button type="submit" variant="primary" disabled={submitting}>
          {submitting ? 'Envoi...' : 'Proposer une offre'}
        </Button>
        <Button type="button" variant="ghost" onClick={() => setAmount('')}>Réinitialiser</Button>
      </div>
    </form>
  );
};

export default BidForm;

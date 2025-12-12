import { useState } from 'react';
import Button from '../common/Button';
import Input from '../common/Input';
import { Gavel, AlertCircle } from 'lucide-react';

/**
 * Formulaire pour placer une enchère
 */
const PlaceBidForm = ({ 
  auctionId, 
  currentPrice, 
  nextBidAmount, 
  onBidPlaced, 
  disabled = false 
}) => {
  const [bidAmount, setBidAmount] = useState(nextBidAmount?.toFixed(2) || '');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const amount = parseFloat(bidAmount);

    // Validations
    if (isNaN(amount) || amount <= 0) {
      setError('Veuillez entrer un montant valide');
      return;
    }

    if (amount <= currentPrice) {
      setError(`Le montant doit être supérieur au prix actuel (${currentPrice.toFixed(2)} €)`);
      return;
    }

    if (nextBidAmount && amount < nextBidAmount) {
      setError(`Le montant minimum est de ${nextBidAmount.toFixed(2)} €`);
      return;
    }

    setLoading(true);
    
    try {
      await onBidPlaced(amount);
      // Réinitialiser le formulaire après succès
      setBidAmount('');
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors du placement de l\'enchère');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Votre enchère
        </label>
        <Input
          type="number"
          step="0.01"
          min={nextBidAmount || currentPrice + 0.01}
          value={bidAmount}
          onChange={(e) => setBidAmount(e.target.value)}
          placeholder={`Minimum: ${nextBidAmount?.toFixed(2) || (currentPrice + 1).toFixed(2)} €`}
          disabled={disabled || loading}
          className="text-lg font-semibold"
        />
        {nextBidAmount && (
          <p className="text-sm text-gray-500 mt-1">
            Enchère minimum suggérée: {nextBidAmount.toFixed(2)} €
          </p>
        )}
      </div>

      {error && (
        <div className="flex items-start gap-2 p-3 bg-red-50 border border-red-200 rounded-lg">
          <AlertCircle size={20} className="text-red-600 flex-shrink-0 mt-0.5" />
          <p className="text-sm text-red-600">{error}</p>
        </div>
      )}

      <Button
        type="submit"
        disabled={disabled || loading || !bidAmount}
        className="w-full"
        size="lg"
      >
        <Gavel size={20} />
        {loading ? 'Enchère en cours...' : 'Placer l\'enchère'}
      </Button>

      <p className="text-xs text-gray-500 text-center">
        En enchérissant, vous vous engagez à acheter l'article si vous remportez l'enchère.
      </p>
    </form>
  );
};

export default PlaceBidForm;

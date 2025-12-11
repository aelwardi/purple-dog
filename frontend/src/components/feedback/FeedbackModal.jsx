import React, { useState } from 'react';
import { XMarkIcon, StarIcon } from '@heroicons/react/24/outline';
import { StarIcon as StarIconSolid } from '@heroicons/react/24/solid';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import platformReviewService from '../../services/platformReviewService';
import Button from '../common/Button';

const FeedbackModal = ({ onClose }) => {
  const { user } = useAuth();
  const { handleError, showSuccess } = useErrorHandler();
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    rating: 0,
    title: '',
    comment: ''
  });

  const [hoverRating, setHoverRating] = useState(0);

  const handleRatingClick = (rating) => {
    setFormData(prev => ({ ...prev, rating }));
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.rating === 0) {
      handleError(new Error('Veuillez donner une note'));
      return;
    }

    if (!formData.title.trim()) {
      handleError(new Error('Veuillez donner un titre à votre avis'));
      return;
    }

    if (!formData.comment.trim()) {
      handleError(new Error('Veuillez écrire un commentaire'));
      return;
    }

    setSubmitting(true);

    try {
      const reviewData = {
        userId: user.id,
        rating: formData.rating,
        title: formData.title,
        comment: formData.comment
      };

      await platformReviewService.createReview(reviewData);
      showSuccess('Merci pour votre avis ! Votre retour a été envoyé avec succès.');

      setTimeout(() => {
        onClose();
      }, 1500);
    } catch (error) {
      handleError(error);
    } finally {
      setSubmitting(false);
    }
  };

  const getRatingText = (rating) => {
    if (rating === 0) return 'Cliquez sur les étoiles pour noter';
    if (rating === 1) return 'Très insatisfait';
    if (rating === 2) return 'Insatisfait';
    if (rating === 3) return 'Moyen';
    if (rating === 4) return 'Satisfait';
    if (rating === 5) return 'Très satisfait';
    return '';
  };

  const getRatingColor = (rating) => {
    if (rating === 0) return 'text-gray-400';
    if (rating <= 2) return 'text-red-600';
    if (rating === 3) return 'text-yellow-600';
    if (rating === 4) return 'text-green-600';
    return 'text-purple-600';
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] flex flex-col">
        {/* Header - Fixed at top */}
        <div className="bg-white border-b px-6 py-4 rounded-t-lg flex justify-between items-center flex-shrink-0 z-10">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Donnez votre avis</h2>
            <p className="text-sm text-gray-600 mt-1">Aidez-nous à améliorer Purple Dog</p>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-full transition-colors"
            disabled={submitting}
          >
            <XMarkIcon className="w-6 h-6 text-gray-600" />
          </button>
        </div>

        {/* Form - Scrollable */}
        <form onSubmit={handleSubmit} className="p-6 space-y-6 overflow-y-auto">
          {/* Rating Section */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-3 text-center">
              Quelle note donneriez-vous à Purple Dog ? *
            </label>

            <div className="flex justify-center gap-2 mb-4 py-2">
              {[1, 2, 3, 4, 5].map((rating) => (
                <button
                  key={rating}
                  type="button"
                  onClick={() => handleRatingClick(rating)}
                  onMouseEnter={() => setHoverRating(rating)}
                  onMouseLeave={() => setHoverRating(0)}
                  className="transition-all transform hover:scale-110 focus:outline-none"
                  disabled={submitting}
                  aria-label={`${rating} étoile${rating > 1 ? 's' : ''}`}
                >
                  {rating <= (hoverRating || formData.rating) ? (
                    <StarIconSolid className="w-12 h-12 text-yellow-400 drop-shadow-sm" />
                  ) : (
                    <StarIcon className="w-12 h-12 text-gray-300" />
                  )}
                </button>
              ))}
            </div>

            <p className={`text-center text-lg font-semibold ${getRatingColor(formData.rating)}`}>
              {getRatingText(formData.rating)}
            </p>
          </div>

          {/* Title */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Titre de votre avis *
            </label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              placeholder="Ex: Excellente plateforme pour vendre mes objets"
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              disabled={submitting}
              required
            />
          </div>

          {/* Comment */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Votre commentaire *
            </label>
            <textarea
              name="comment"
              value={formData.comment}
              onChange={handleChange}
              rows="6"
              placeholder="Partagez votre expérience : ce qui vous a plu, vos suggestions d'amélioration, etc."
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
              disabled={submitting}
              required
            />
            <p className="text-xs text-gray-500 mt-1">
              Minimum 20 caractères • {formData.comment.length} caractères
            </p>
          </div>

          {/* User Info */}
          <div className="bg-purple-50 p-4 rounded-lg border border-purple-200">
            <p className="text-sm text-gray-700">
              <span className="font-medium">📝 Envoyé par :</span> {user.firstName} {user.lastName}
            </p>
            <p className="text-sm text-gray-700 mt-1">
              <span className="font-medium">📧 Email :</span> {user.email}
            </p>
            <p className="text-xs text-gray-500 mt-2">
              ℹ️ Votre avis sera examiné par notre équipe. Il ne sera pas affiché publiquement.
            </p>
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4 border-t">
            <Button
              type="button"
              variant="outline"
              onClick={onClose}
              disabled={submitting}
              className="flex-1"
            >
              Annuler
            </Button>
            <Button
              type="submit"
              variant="primary"
              disabled={submitting || formData.rating === 0 || formData.comment.length < 20}
              className="flex-1"
            >
              {submitting ? 'Envoi en cours...' : 'Envoyer mon avis'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default FeedbackModal;


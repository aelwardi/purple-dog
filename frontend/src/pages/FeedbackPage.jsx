import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { StarIcon } from '@heroicons/react/24/solid';
import { StarIcon as StarIconOutline } from '@heroicons/react/24/outline';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import toast from 'react-hot-toast';

const FeedbackPage = () => {
  const navigate = useNavigate();
  const userEmail = localStorage.getItem('userEmail');
  const userType = localStorage.getItem('userType');

  const [formData, setFormData] = useState({
    rating: 0,
    category: '',
    comment: '',
    suggestions: ''
  });

  const [hoverRating, setHoverRating] = useState(0);

  const categories = [
    'Interface et design',
    'Facilité d\'utilisation',
    'Fonctionnalités',
    'Support client',
    'Sécurité',
    'Tarifs et commissions',
    'Autre'
  ];

  const handleRatingClick = (rating) => {
    setFormData(prev => ({ ...prev, rating }));
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (formData.rating === 0) {
      toast.error('Veuillez donner une note');
      return;
    }

    // TODO: Send to backend
    const feedbackData = {
      ...formData,
      userEmail,
      userType,
      timestamp: new Date().toISOString()
    };

    console.log('Feedback submitted:', feedbackData);
    toast.success('Merci pour votre retour ! Votre avis a été envoyé.');
    
    // Redirect back to dashboard after 2 seconds
    setTimeout(() => {
      navigate(`/dashboard?type=${userType}`);
    }, 2000);
  };

  const getRatingText = (rating) => {
    if (rating === 0) return '';
    if (rating <= 3) return 'Mécontentent';
    if (rating <= 5) return 'Peu satisfait';
    if (rating <= 7) return 'Satisfait';
    if (rating <= 9) return 'Très satisfait';
    return 'Excellent !';
  };

  const getRatingColor = (rating) => {
    if (rating === 0) return '';
    if (rating <= 3) return 'text-red-600';
    if (rating <= 5) return 'text-orange-600';
    if (rating <= 7) return 'text-yellow-600';
    if (rating <= 9) return 'text-green-600';
    return 'text-purple-600';
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl md:text-4xl font-display font-bold text-gray-900 mb-4">
            Donnez votre avis sur Purple Dog
          </h1>
          <p className="text-lg text-gray-600">
            Votre opinion compte ! Aidez-nous à améliorer la plateforme
          </p>
        </div>

        <Card className="p-8">
          <form onSubmit={handleSubmit} className="space-y-8">
            {/* Rating Section */}
            <div>
              <label className="block text-lg font-semibold text-gray-900 mb-4 text-center">
                Quelle note donneriez-vous à Purple Dog ?
              </label>
              <p className="text-sm text-gray-600 text-center mb-6">
                Notez de 1 (très mauvais) à 10 (excellent)
              </p>
              
              <div className="flex justify-center gap-2 mb-4">
                {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((rating) => (
                  <button
                    key={rating}
                    type="button"
                    onClick={() => handleRatingClick(rating)}
                    onMouseEnter={() => setHoverRating(rating)}
                    onMouseLeave={() => setHoverRating(0)}
                    className={`w-12 h-12 rounded-lg font-bold transition-all ${
                      rating <= (hoverRating || formData.rating)
                        ? 'bg-purple-600 text-white scale-110'
                        : 'bg-gray-200 text-gray-600 hover:bg-gray-300'
                    }`}
                  >
                    {rating}
                  </button>
                ))}
              </div>

              {formData.rating > 0 && (
                <p className={`text-center text-xl font-semibold ${getRatingColor(formData.rating)}`}>
                  {getRatingText(formData.rating)}
                </p>
              )}
            </div>

            {/* Category */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Quel aspect souhaitez-vous évaluer ?
              </label>
              <select
                name="category"
                value={formData.category}
                onChange={handleChange}
                className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
              >
                <option value="">Sélectionnez une catégorie (optionnel)</option>
                {categories.map(cat => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>

            {/* Comment */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Qu'avez-vous pensé de votre expérience ?
              </label>
              <textarea
                name="comment"
                value={formData.comment}
                onChange={handleChange}
                rows="5"
                placeholder="Partagez votre expérience, ce qui vous a plu ou déplu..."
                className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
              />
            </div>

            {/* Suggestions */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Avez-vous des suggestions d'amélioration ?
              </label>
              <textarea
                name="suggestions"
                value={formData.suggestions}
                onChange={handleChange}
                rows="4"
                placeholder="Comment pouvons-nous améliorer Purple Dog ?"
                className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
              />
            </div>

            {/* User Info Display */}
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">
                <span className="font-medium">Envoyé en tant que :</span> {userEmail} 
                <span className="ml-2 text-purple-600">
                  ({userType === 'individual' ? 'Particulier' : 'Professionnel'})
                </span>
              </p>
            </div>

            {/* Buttons */}
            <div className="flex gap-4 pt-6">
              <Button 
                type="button" 
                variant="outline" 
                onClick={() => navigate(`/dashboard?type=${userType}`)}
                className="flex-1"
              >
                Annuler
              </Button>
              <Button 
                type="submit" 
                variant="primary" 
                className="flex-1"
                disabled={formData.rating === 0}
              >
                Envoyer mon avis
              </Button>
            </div>
          </form>
        </Card>

        {/* Info Card */}
        <Card className="mt-8 p-6 bg-purple-50 border-purple-200">
          <h3 className="font-semibold text-purple-900 mb-2">
            🔒 Confidentialité de votre feedback
          </h3>
          <p className="text-sm text-purple-700">
            Votre avis sera transmis à l'équipe Purple Dog. Seuls les administrateurs pourront consulter vos retours. 
            Votre feedback nous aide à améliorer constamment la plateforme.
          </p>
        </Card>
      </div>
    </div>
  );
};

export default FeedbackPage;

import React, { useState } from 'react';
import { PhotoIcon, DocumentIcon, CurrencyEuroIcon, ScaleIcon, RulerIcon } from '@heroicons/react/24/outline';
import Input from '../common/Input';
import Button from '../common/Button';
import Card from '../common/Card';

const ProductListingForm = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    name: '',
    category: '',
    width: '',
    height: '',
    depth: '',
    weight: '',
    description: '',
    price: '',
    saleMode: 'auction', // 'auction' or 'quicksale'
    auctionStartPrice: '',
    photos: [],
    documents: []
  });

  const [errors, setErrors] = useState({});
  const [photoPreviews, setPhotoPreviews] = useState([]);

  const categories = [
    'Bijoux & montres',
    'Meubles anciens',
    'Objets d\'art & tableaux',
    'Objets de collection',
    'Vins & spiritueux de collection',
    'Instruments de musique',
    'Livres anciens & manuscrits',
    'Mode & accessoires de luxe',
    'Horlogerie & pendules anciennes',
    'Photographies anciennes & appareils vintage',
    'Vaisselle & argenterie & cristallerie',
    'Sculptures & objets décoratifs',
    'Véhicules de collection'
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // Clear error for this field
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handlePhotoChange = (e) => {
    const files = Array.from(e.target.files);
    const newPhotos = [...formData.photos, ...files].slice(0, 10); // Maximum 10 photos
    
    setFormData(prev => ({ ...prev, photos: newPhotos }));
    
    // Create previews
    const newPreviews = [];
    newPhotos.forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        newPreviews.push(reader.result);
        if (newPreviews.length === newPhotos.length) {
          setPhotoPreviews(newPreviews);
        }
      };
      reader.readAsDataURL(file);
    });
  };

  const removePhoto = (index) => {
    const newPhotos = formData.photos.filter((_, i) => i !== index);
    const newPreviews = photoPreviews.filter((_, i) => i !== index);
    setFormData(prev => ({ ...prev, photos: newPhotos }));
    setPhotoPreviews(newPreviews);
  };

  const handleDocumentChange = (e) => {
    const files = Array.from(e.target.files);
    setFormData(prev => ({ ...prev, documents: [...prev.documents, ...files] }));
  };

  const removeDocument = (index) => {
    const newDocs = formData.documents.filter((_, i) => i !== index);
    setFormData(prev => ({ ...prev, documents: newDocs }));
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) newErrors.name = 'Le nom de l\'objet est requis';
    if (!formData.category) newErrors.category = 'La catégorie est requise';
    if (!formData.description.trim()) newErrors.description = 'La description est requise';
    if (!formData.price || formData.price <= 0) newErrors.price = 'Le prix doit être supérieur à 0';
    if (formData.photos.length < 5) newErrors.photos = 'Minimum 5 photos requises';
    
    if (formData.saleMode === 'auction' && !formData.auctionStartPrice) {
      newErrors.auctionStartPrice = 'Le prix de départ des enchères est requis';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  return (
    <Card>
      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Informations de base */}
        <div>
          <h3 className="text-xl font-display font-semibold text-gray-900 mb-4">
            Informations de base
          </h3>
          
          <div className="space-y-4">
            <Input
              label="Nom de l'objet"
              name="name"
              type="text"
              required
              value={formData.name}
              onChange={handleChange}
              error={errors.name}
              placeholder="Ex: Vase Art Déco en cristal signé Lalique"
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Catégorie *
              </label>
              <select
                name="category"
                value={formData.category}
                onChange={handleChange}
                required
                className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
              >
                <option value="">Sélectionnez une catégorie</option>
                {categories.map(cat => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
              {errors.category && (
                <p className="mt-1 text-sm text-red-600">{errors.category}</p>
              )}
            </div>
          </div>
        </div>

        {/* Dimensions */}
        <div>
          <h3 className="text-xl font-display font-semibold text-gray-900 mb-4">
            Dimensions et poids
          </h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Input
              label="Largeur (cm)"
              name="width"
              type="number"
              step="0.1"
              value={formData.width}
              onChange={handleChange}
              placeholder="0.0"
              icon={<RulerIcon className="h-5 w-5 text-gray-400" />}
            />
            <Input
              label="Hauteur (cm)"
              name="height"
              type="number"
              step="0.1"
              value={formData.height}
              onChange={handleChange}
              placeholder="0.0"
              icon={<RulerIcon className="h-5 w-5 text-gray-400" />}
            />
            <Input
              label="Profondeur (cm)"
              name="depth"
              type="number"
              step="0.1"
              value={formData.depth}
              onChange={handleChange}
              placeholder="0.0"
              icon={<RulerIcon className="h-5 w-5 text-gray-400" />}
            />
            <Input
              label="Poids (kg)"
              name="weight"
              type="number"
              step="0.1"
              value={formData.weight}
              onChange={handleChange}
              placeholder="0.0"
              icon={<ScaleIcon className="h-5 w-5 text-gray-400" />}
            />
          </div>
        </div>

        {/* Description */}
        <div>
          <h3 className="text-xl font-display font-semibold text-gray-900 mb-4">
            Description
          </h3>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Description détaillée de l'objet *
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows="6"
              required
              className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
              placeholder="Décrivez l'objet en détail : état, provenance, histoire, caractéristiques particulières..."
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description}</p>
            )}
          </div>
        </div>

        {/* Photos */}
        <div>
          <h3 className="text-xl font-display font-semibold text-gray-900 mb-4">
            Photos de l'objet
          </h3>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Photos (minimum 5, maximum 10) *
            </label>
            <p className="text-sm text-gray-500 mb-4">
              Prenez des photos de différents angles : avant, arrière, dessus, dessous, signature, détails...
            </p>
            
            <div className="mb-4">
              <label className="flex items-center justify-center w-full px-6 py-4 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer hover:border-purple-400 transition-colors">
                <PhotoIcon className="w-8 h-8 text-gray-400 mr-3" />
                <span className="text-gray-600">Cliquez pour ajouter des photos</span>
                <input
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={handlePhotoChange}
                  className="hidden"
                />
              </label>
            </div>

            {/* Photo previews */}
            {photoPreviews.length > 0 && (
              <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
                {photoPreviews.map((preview, index) => (
                  <div key={index} className="relative group">
                    <img
                      src={preview}
                      alt={`Preview ${index + 1}`}
                      className="w-full h-32 object-cover rounded-lg"
                    />
                    <button
                      type="button"
                      onClick={() => removePhoto(index)}
                      className="absolute top-2 right-2 bg-red-500 text-white p-1 rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                    >
                      ✕
                    </button>
                    {index === 0 && (
                      <span className="absolute bottom-2 left-2 bg-purple-600 text-white text-xs px-2 py-1 rounded">
                        Photo principale
                      </span>
                    )}
                  </div>
                ))}
              </div>
            )}
            
            <p className="mt-2 text-sm text-gray-600">
              {formData.photos.length} / 10 photos ajoutées
            </p>
            {errors.photos && (
              <p className="mt-1 text-sm text-red-600">{errors.photos}</p>
            )}
          </div>
        </div>

        {/* Documents */}
        <div>
          <h3 className="text-xl font-display font-semibold text-gray-900 mb-4">
            Documents
          </h3>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Certificat d'authenticité, preuve d'achat (optionnel)
            </label>
            
            <label className="flex items-center justify-center w-full px-6 py-4 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer hover:border-purple-400 transition-colors">
              <DocumentIcon className="w-8 h-8 text-gray-400 mr-3" />
              <span className="text-gray-600">Ajouter des documents</span>
              <input
                type="file"
                accept=".pdf,.jpg,.jpeg,.png"
                multiple
                onChange={handleDocumentChange}
                className="hidden"
              />
            </label>

            {formData.documents.length > 0 && (
              <div className="mt-4 space-y-2">
                {formData.documents.map((doc, index) => (
                  <div key={index} className="flex items-center justify-between bg-gray-50 p-3 rounded-lg">
                    <span className="text-sm text-gray-700">{doc.name}</span>
                    <button
                      type="button"
                      onClick={() => removeDocument(index)}
                      className="text-red-600 hover:text-red-700 text-sm"
                    >
                      Supprimer
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Prix et mode de vente */}
        <div>
          <h3 className="text-xl font-display font-semibold text-gray-900 mb-4">
            Prix et mode de vente
          </h3>
          
          <div className="space-y-4">
            <Input
              label="Prix souhaité"
              name="price"
              type="number"
              step="0.01"
              required
              value={formData.price}
              onChange={handleChange}
              error={errors.price}
              placeholder="0.00"
              icon={<CurrencyEuroIcon className="h-5 w-5 text-gray-400" />}
            />

            {/* Mode de vente */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Mode de vente *
              </label>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <label className={`relative flex items-center p-4 border-2 rounded-lg cursor-pointer transition-all ${
                  formData.saleMode === 'auction' 
                    ? 'border-purple-600 bg-purple-50' 
                    : 'border-gray-300 hover:border-purple-300'
                }`}>
                  <input
                    type="radio"
                    name="saleMode"
                    value="auction"
                    checked={formData.saleMode === 'auction'}
                    onChange={handleChange}
                    className="mr-3"
                  />
                  <div>
                    <p className="font-semibold text-gray-900">Enchères</p>
                    <p className="text-sm text-gray-600">Les professionnels enchérissent pendant 7 jours</p>
                  </div>
                </label>

                <label className={`relative flex items-center p-4 border-2 rounded-lg cursor-pointer transition-all ${
                  formData.saleMode === 'quicksale' 
                    ? 'border-purple-600 bg-purple-50' 
                    : 'border-gray-300 hover:border-purple-300'
                }`}>
                  <input
                    type="radio"
                    name="saleMode"
                    value="quicksale"
                    checked={formData.saleMode === 'quicksale'}
                    onChange={handleChange}
                    className="mr-3"
                  />
                  <div>
                    <p className="font-semibold text-gray-900">Vente rapide</p>
                    <p className="text-sm text-gray-600">La première offre au prix remporte l'objet</p>
                  </div>
                </label>
              </div>
            </div>

            {/* Prix de départ pour les enchères */}
            {formData.saleMode === 'auction' && (
              <div className="bg-purple-50 p-4 rounded-lg">
                <Input
                  label="Prix de départ des enchères"
                  name="auctionStartPrice"
                  type="number"
                  step="0.01"
                  value={formData.auctionStartPrice}
                  onChange={handleChange}
                  error={errors.auctionStartPrice}
                  placeholder="Par défaut: -10% du prix souhaité"
                  icon={<CurrencyEuroIcon className="h-5 w-5 text-gray-400" />}
                />
                <p className="text-sm text-gray-600 mt-2">
                  Recommandé : {formData.price ? (formData.price * 0.9).toFixed(2) : '0.00'}€ 
                  (90% de votre prix souhaité)
                </p>
              </div>
            )}
          </div>
        </div>

        {/* Buttons */}
        <div className="flex gap-4 pt-6 border-t">
          {onCancel && (
            <Button type="button" variant="outline" onClick={onCancel} className="flex-1">
              Annuler
            </Button>
          )}
          <Button type="submit" variant="primary" className="flex-1">
            Publier l'objet
          </Button>
        </div>
      </form>
    </Card>
  );
};

export default ProductListingForm;

import React, { useState, useEffect } from 'react';
import { PhotoIcon, DocumentIcon, CurrencyEuroIcon, CheckCircleIcon } from '@heroicons/react/24/outline';
import Input from '../common/Input';
import Button from '../common/Button';
import Card from '../common/Card';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import productService from '../../services/productService';
import categoryService from '../../services/categoryService';
import uploadService from '../../services/uploadService';

const ProductListingForm = ({ onSubmit, onCancel }) => {
  const { user } = useAuth();
  const { showSuccess, showError, handleError } = useErrorHandler();

  const [currentStep, setCurrentStep] = useState(1);
  const totalSteps = 5;

  const [formData, setFormData] = useState({
    title: '',
    categoryId: '',
    productCondition: 'GOOD',
    brand: '',
    yearOfManufacture: '',
    origin: '',
    widthCm: '',
    heightCm: '',
    depthCm: '',
    weightKg: '',
    description: '',
    estimatedValue: '',
    saleType: 'AUCTION',
    authenticityCertificate: '',
    hasDocumentation: false,
    photos: [],
    documents: []
  });

  const [errors, setErrors] = useState({});
  const [photoPreviews, setPhotoPreviews] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);

  const conditions = [
    { value: 'NEW', label: 'Neuf' },
    { value: 'LIKE_NEW', label: 'Comme neuf' },
    { value: 'EXCELLENT', label: 'Excellent état' },
    { value: 'GOOD', label: 'Bon état' },
    { value: 'FAIR', label: 'État correct' },
    { value: 'POOR', label: 'Mauvais état' },
    { value: 'RESTORED', label: 'Restauré' }
  ];

  const steps = [
    { number: 1, title: 'Informations de base', description: 'Décrivez votre objet' },
    { number: 2, title: 'Détails techniques', description: 'Dimensions et caractéristiques' },
    { number: 3, title: 'Photos', description: 'Images de votre objet' },
    { number: 4, title: 'Documents', description: 'Certificats et preuves' },
    { number: 5, title: 'Prix et publication', description: 'Fixez votre prix' }
  ];

  useEffect(() => {
    const loadCategories = async () => {
      try {
        const data = await categoryService.getActiveCategories();
        setCategories(data);
      } catch (error) {
        console.error('Error loading categories:', error);
        showError('Erreur lors du chargement des catégories');
      }
    };
    loadCategories();
  }, [showError]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handlePhotoChange = (e) => {
    const files = Array.from(e.target.files);
    const newPhotos = [...formData.photos, ...files].slice(0, 10);

    setFormData(prev => ({ ...prev, photos: newPhotos }));

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

  const validateStep = (step) => {
    const newErrors = {};

    if (step === 1) {
      if (!formData.title.trim()) newErrors.title = 'Le titre est requis';
      if (!formData.categoryId) newErrors.categoryId = 'La catégorie est requise';
      if (!formData.description.trim()) newErrors.description = 'La description est requise';
    }

    if (step === 3) {
      if (formData.photos.length < 5) {
        newErrors.photos = 'Minimum 5 photos requises';
      }
    }

    if (step === 5) {
      if (!formData.estimatedValue || formData.estimatedValue <= 0) {
        newErrors.estimatedValue = 'Le prix doit être supérieur à 0';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNext = () => {
    if (validateStep(currentStep)) {
      setCurrentStep(prev => Math.min(prev + 1, totalSteps));
    }
  };

  const handlePrevious = () => {
    setCurrentStep(prev => Math.max(prev - 1, 1));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateStep(currentStep)) {
      return;
    }

    setLoading(true);

    try {
      // Upload photos to backend
      let photoUrls = [];

      if (formData.photos && formData.photos.length > 0) {
        showSuccess('Upload des photos en cours...');
        photoUrls = await uploadService.uploadPhotos(formData.photos);
        showSuccess(`${photoUrls.length} photos uploadées avec succès !`);
      }

      const productData = {
        sellerId: user.id,
        categoryId: parseInt(formData.categoryId),
        title: formData.title,
        description: formData.description,
        productCondition: formData.productCondition,
        saleType: formData.saleType,
        estimatedValue: parseFloat(formData.estimatedValue),
        brand: formData.brand || null,
        yearOfManufacture: formData.yearOfManufacture ? parseInt(formData.yearOfManufacture) : null,
        origin: formData.origin || null,
        authenticityCertificate: formData.authenticityCertificate || null,
        hasDocumentation: formData.hasDocumentation,
        widthCm: formData.widthCm ? parseFloat(formData.widthCm) : null,
        heightCm: formData.heightCm ? parseFloat(formData.heightCm) : null,
        depthCm: formData.depthCm ? parseFloat(formData.depthCm) : null,
        weightKg: formData.weightKg ? parseFloat(formData.weightKg) : null,
        photoUrls: photoUrls,
        documents: []
      };

      const result = await productService.createProduct(productData);

      showSuccess('Produit publié avec succès !');

      if (onSubmit) {
        onSubmit(result);
      }
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      {/* Step Indicator */}
      <div className="mb-8">
        <div className="flex items-center justify-between">
          {steps.map((step, index) => (
            <React.Fragment key={step.number}>
              <div className="flex flex-col items-center flex-1">
                <div className={`w-12 h-12 rounded-full flex items-center justify-center font-semibold transition-all ${
                  currentStep > step.number 
                    ? 'bg-green-500 text-white' 
                    : currentStep === step.number 
                    ? 'bg-purple-600 text-white' 
                    : 'bg-gray-200 text-gray-500'
                }`}>
                  {currentStep > step.number ? <CheckCircleIcon className="w-6 h-6" /> : step.number}
                </div>
                <p className={`mt-2 text-xs font-medium text-center ${
                  currentStep === step.number ? 'text-purple-600' : 'text-gray-500'
                }`}>
                  {step.title}
                </p>
              </div>
              {index < steps.length - 1 && (
                <div className={`h-1 flex-1 mx-2 rounded ${
                  currentStep > step.number ? 'bg-green-500' : 'bg-gray-200'
                }`} />
              )}
            </React.Fragment>
          ))}
        </div>
      </div>

      <Card className="p-8">
        <form onSubmit={handleSubmit}>
          {/* Step 1: Informations de base */}
          {currentStep === 1 && (
            <div className="space-y-6">
              <div>
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Informations de base</h2>
                <p className="text-gray-600">Commencez par décrire votre objet</p>
              </div>

              <Input
                label="Titre de l'objet"
                name="title"
                type="text"
                required
                value={formData.title}
                onChange={handleChange}
                error={errors.title}
                placeholder="Ex: Vase Art Déco en cristal signé Lalique"
              />

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Catégorie *
                </label>
                <select
                  name="categoryId"
                  value={formData.categoryId}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                >
                  <option value="">Sélectionnez une catégorie</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
                {errors.categoryId && (
                  <p className="mt-1 text-sm text-red-600">{errors.categoryId}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  État de l'objet *
                </label>
                <select
                  name="productCondition"
                  value={formData.productCondition}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                >
                  {conditions.map(cond => (
                    <option key={cond.value} value={cond.value}>{cond.label}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description détaillée *
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  rows="6"
                  required
                  className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Décrivez l'objet en détail : état, provenance, histoire, caractéristiques particulières..."
                />
                {errors.description && (
                  <p className="mt-1 text-sm text-red-600">{errors.description}</p>
                )}
              </div>
            </div>
          )}

          {/* Step 2: Détails techniques */}
          {currentStep === 2 && (
            <div className="space-y-6">
              <div>
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Détails techniques</h2>
                <p className="text-gray-600">Informations complémentaires sur votre objet</p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <Input
                  label="Marque"
                  name="brand"
                  type="text"
                  value={formData.brand}
                  onChange={handleChange}
                  placeholder="Ex: Lalique"
                />
                <Input
                  label="Année de fabrication"
                  name="yearOfManufacture"
                  type="number"
                  value={formData.yearOfManufacture}
                  onChange={handleChange}
                  placeholder="Ex: 1925"
                />
              </div>

              <Input
                label="Origine / Provenance"
                name="origin"
                type="text"
                value={formData.origin}
                onChange={handleChange}
                placeholder="Ex: France, Paris"
              />

              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Dimensions</h3>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <Input
                    label="Largeur (cm)"
                    name="widthCm"
                    type="number"
                    step="0.1"
                    value={formData.widthCm}
                    onChange={handleChange}
                    placeholder="0.0"
                  />
                  <Input
                    label="Hauteur (cm)"
                    name="heightCm"
                    type="number"
                    step="0.1"
                    value={formData.heightCm}
                    onChange={handleChange}
                    placeholder="0.0"
                  />
                  <Input
                    label="Profondeur (cm)"
                    name="depthCm"
                    type="number"
                    step="0.1"
                    value={formData.depthCm}
                    onChange={handleChange}
                    placeholder="0.0"
                  />
                  <Input
                    label="Poids (kg)"
                    name="weightKg"
                    type="number"
                    step="0.1"
                    value={formData.weightKg}
                    onChange={handleChange}
                    placeholder="0.0"
                  />
                </div>
              </div>
            </div>
          )}

          {/* Step 3: Photos */}
          {currentStep === 3 && (
            <div className="space-y-6">
              <div>
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Photos de l'objet</h2>
                <p className="text-gray-600">Ajoutez au moins 5 photos de qualité</p>
              </div>

              <div className="mb-4">
                <label className="flex items-center justify-center w-full px-6 py-8 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer hover:border-purple-400 transition-colors bg-gray-50">
                  <div className="text-center">
                    <PhotoIcon className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                    <span className="text-gray-600 font-medium">Cliquez pour ajouter des photos</span>
                    <p className="text-sm text-gray-500 mt-1">Maximum 10 photos (JPG, PNG)</p>
                  </div>
                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={handlePhotoChange}
                    className="hidden"
                  />
                </label>
              </div>

              {photoPreviews.length > 0 && (
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                  {photoPreviews.map((preview, index) => (
                    <div key={index} className="relative group">
                      <img
                        src={preview}
                        alt={`Preview ${index + 1}`}
                        className="w-full h-40 object-cover rounded-lg border-2 border-gray-200"
                      />
                      <button
                        type="button"
                        onClick={() => removePhoto(index)}
                        className="absolute top-2 right-2 bg-red-500 text-white p-2 rounded-full opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-600"
                      >
                        ✕
                      </button>
                      {index === 0 && (
                        <span className="absolute bottom-2 left-2 bg-purple-600 text-white text-xs px-2 py-1 rounded font-medium">
                          Photo principale
                        </span>
                      )}
                    </div>
                  ))}
                </div>
              )}

              <div className="flex items-center justify-between bg-gray-50 p-4 rounded-lg">
                <span className="text-sm font-medium text-gray-700">
                  {formData.photos.length} / 10 photos ajoutées
                </span>
                {formData.photos.length >= 5 && (
                  <span className="text-green-600 text-sm font-medium flex items-center">
                    <CheckCircleIcon className="w-4 h-4 mr-1" />
                    Minimum atteint
                  </span>
                )}
              </div>

              {errors.photos && (
                <p className="text-sm text-red-600">{errors.photos}</p>
              )}
            </div>
          )}

          {/* Step 4: Documents */}
          {currentStep === 4 && (
            <div className="space-y-6">
              <div>
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Documents et authenticité</h2>
                <p className="text-gray-600">Ajoutez vos certificats et preuves (optionnel)</p>
              </div>

              <Input
                label="Certificat d'authenticité"
                name="authenticityCertificate"
                type="text"
                value={formData.authenticityCertificate}
                onChange={handleChange}
                placeholder="Ex: Certificat Lalique n°12345"
              />

              <div className="flex items-center p-4 bg-purple-50 rounded-lg">
                <input
                  type="checkbox"
                  name="hasDocumentation"
                  checked={formData.hasDocumentation}
                  onChange={(e) => setFormData(prev => ({ ...prev, hasDocumentation: e.target.checked }))}
                  className="w-5 h-5 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
                <label className="ml-3 text-sm font-medium text-gray-900">
                  Je possède une documentation complète pour cet objet
                </label>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Documents additionnels
                </label>

                <label className="flex items-center justify-center w-full px-6 py-8 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer hover:border-purple-400 transition-colors bg-gray-50">
                  <div className="text-center">
                    <DocumentIcon className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                    <span className="text-gray-600 font-medium">Ajouter des documents</span>
                    <p className="text-sm text-gray-500 mt-1">PDF, JPG, PNG</p>
                  </div>
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
                      <div key={index} className="flex items-center justify-between bg-gray-50 p-3 rounded-lg border border-gray-200">
                        <span className="text-sm text-gray-700 font-medium">{doc.name}</span>
                        <button
                          type="button"
                          onClick={() => removeDocument(index)}
                          className="text-red-600 hover:text-red-700 text-sm font-medium"
                        >
                          Supprimer
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Step 5: Prix et publication */}
          {currentStep === 5 && (
            <div className="space-y-6">
              <div>
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Prix et publication</h2>
                <p className="text-gray-600">Dernière étape avant de publier votre objet</p>
              </div>

              <Input
                label="Valeur estimée"
                name="estimatedValue"
                type="number"
                step="0.01"
                required
                value={formData.estimatedValue}
                onChange={handleChange}
                error={errors.estimatedValue}
                placeholder="0.00"
                icon={<CurrencyEuroIcon className="h-5 w-5 text-gray-400" />}
              />

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-3">
                  Mode de vente *
                </label>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <label className={`relative flex flex-col p-6 border-2 rounded-lg cursor-pointer transition-all ${
                    formData.saleType === 'AUCTION' 
                      ? 'border-purple-600 bg-purple-50 shadow-md' 
                      : 'border-gray-300 hover:border-purple-300'
                  }`}>
                    <input
                      type="radio"
                      name="saleType"
                      value="AUCTION"
                      checked={formData.saleType === 'AUCTION'}
                      onChange={handleChange}
                      className="mb-3"
                    />
                    <p className="font-bold text-lg text-gray-900 mb-2">Enchères</p>
                    <p className="text-sm text-gray-600">Les professionnels enchérissent pendant 7 jours</p>
                  </label>

                  <label className={`relative flex flex-col p-6 border-2 rounded-lg cursor-pointer transition-all ${
                    formData.saleType === 'QUICK_SALE' 
                      ? 'border-purple-600 bg-purple-50 shadow-md' 
                      : 'border-gray-300 hover:border-purple-300'
                  }`}>
                    <input
                      type="radio"
                      name="saleType"
                      value="QUICK_SALE"
                      checked={formData.saleType === 'QUICK_SALE'}
                      onChange={handleChange}
                      className="mb-3"
                    />
                    <p className="font-bold text-lg text-gray-900 mb-2">Vente rapide</p>
                    <p className="text-sm text-gray-600">La première offre au prix remporte l'objet</p>
                  </label>
                </div>
              </div>

              {/* Récapitulatif */}
              <div className="bg-gray-50 p-6 rounded-lg border border-gray-200">
                <h3 className="font-bold text-gray-900 mb-4">Récapitulatif</h3>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600">Objet :</span>
                    <span className="font-medium text-gray-900">{formData.title || '-'}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Photos :</span>
                    <span className="font-medium text-gray-900">{formData.photos.length} ajoutées</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Valeur :</span>
                    <span className="font-medium text-gray-900">{formData.estimatedValue ? `${formData.estimatedValue}€` : '-'}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Mode :</span>
                    <span className="font-medium text-gray-900">
                      {formData.saleType === 'AUCTION' ? 'Enchères' : 'Vente rapide'}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Navigation Buttons */}
          <div className="flex gap-4 pt-8 mt-8 border-t border-gray-200">
            {currentStep > 1 ? (
              <Button
                type="button"
                variant="outline"
                onClick={handlePrevious}
                className="flex-1"
              >
                Précédent
              </Button>
            ) : (
              onCancel && (
                <Button
                  type="button"
                  variant="outline"
                  onClick={onCancel}
                  className="flex-1"
                >
                  Annuler
                </Button>
              )
            )}

            {currentStep < totalSteps ? (
              <Button
                type="button"
                variant="primary"
                onClick={handleNext}
                className="flex-1"
              >
                Suivant
              </Button>
            ) : (
              <Button
                type="submit"
                variant="primary"
                className="flex-1"
                disabled={loading}
              >
                {loading ? 'Publication en cours...' : 'Publier l\'objet'}
              </Button>
            )}
          </div>
        </form>
      </Card>
    </div>
  );
};

export default ProductListingForm;


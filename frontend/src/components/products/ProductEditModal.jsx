import React, { useState, useEffect } from 'react';
import { XMarkIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import productService from '../../services/productService';
import categoryService from '../../services/categoryService';
import uploadService from '../../services/uploadService';
import Button from '../common/Button';
import Input from '../common/Input';
import Card from '../common/Card';

const ProductEditModal = ({ productId, onClose, onSuccess }) => {
  const { user } = useAuth();
  const { handleError, showSuccess } = useErrorHandler();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [categories, setCategories] = useState([]);
  const [currentStep, setCurrentStep] = useState(1);

  const [formData, setFormData] = useState({
    title: '',
    categoryId: '',
    description: '',
    productCondition: 'GOOD',
    saleType: 'QUICK_SALE',
    estimatedValue: '',
    brand: '',
    yearOfManufacture: '',
    origin: '',
    authenticityCertificate: '',
    hasDocumentation: false,
    widthCm: '',
    heightCm: '',
    depthCm: '',
    weightKg: '',
    photos: [],
    existingPhotos: []
  });

  const [photoPreviews, setPhotoPreviews] = useState([]);

  const steps = [
    { number: 1, title: 'Informations générales' },
    { number: 2, title: 'État et provenance' },
    { number: 3, title: 'Photos' },
    { number: 4, title: 'Dimensions' }
  ];

  const conditions = [
    { value: 'EXCELLENT', label: 'Excellent' },
    { value: 'VERY_GOOD', label: 'Très bon' },
    { value: 'GOOD', label: 'Bon' },
    { value: 'FAIR', label: 'Correct' },
    { value: 'POOR', label: 'Mauvais' },
    { value: 'RESTORATION_NEEDED', label: 'À restaurer' }
  ];

  useEffect(() => {
    loadProduct();
    loadCategories();
  }, [productId]);

  const loadProduct = async () => {
    try {
      setLoading(true);
      const product = await productService.getProduct(productId);

      setFormData({
        title: product.title || '',
        categoryId: product.categoryId?.toString() || '',
        description: product.description || '',
        productCondition: product.productCondition || 'GOOD',
        saleType: product.saleType || 'QUICK_SALE',
        estimatedValue: product.estimatedValue?.toString() || '',
        brand: product.brand || '',
        yearOfManufacture: product.yearOfManufacture?.toString() || '',
        origin: product.origin || '',
        authenticityCertificate: product.authenticityCertificate || '',
        hasDocumentation: product.hasDocumentation || false,
        widthCm: product.widthCm?.toString() || '',
        heightCm: product.heightCm?.toString() || '',
        depthCm: product.depthCm?.toString() || '',
        weightKg: product.weightKg?.toString() || '',
        photos: [],
        existingPhotos: product.photos || []
      });

      setPhotoPreviews(product.photos?.map(p => p.url) || []);
    } catch (error) {
      handleError(error);
      onClose();
    } finally {
      setLoading(false);
    }
  };

  const loadCategories = async () => {
    try {
      const data = await categoryService.getAllCategories();
      setCategories(data);
    } catch (error) {
      console.error('Error loading categories:', error);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handlePhotoChange = (e) => {
    const files = Array.from(e.target.files);
    const newPhotos = [...formData.photos, ...files];

    if (newPhotos.length + formData.existingPhotos.length > 10) {
      handleError(new Error('Maximum 10 photos autorisées'));
      return;
    }

    setFormData(prev => ({ ...prev, photos: newPhotos }));

    // Generate previews
    files.forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPhotoPreviews(prev => [...prev, reader.result]);
      };
      reader.readAsDataURL(file);
    });
  };

  const removeExistingPhoto = (index) => {
    setFormData(prev => ({
      ...prev,
      existingPhotos: prev.existingPhotos.filter((_, i) => i !== index)
    }));
    setPhotoPreviews(prev => prev.filter((_, i) => i !== index));
  };

  const removeNewPhoto = (index) => {
    const existingCount = formData.existingPhotos.length;
    const newPhotoIndex = index - existingCount;

    setFormData(prev => ({
      ...prev,
      photos: prev.photos.filter((_, i) => i !== newPhotoIndex)
    }));
    setPhotoPreviews(prev => prev.filter((_, i) => i !== index));
  };

  const validateStep = (step) => {
    switch (step) {
      case 1:
        if (!formData.title || !formData.categoryId || !formData.description || !formData.estimatedValue) {
          handleError(new Error('Veuillez remplir tous les champs obligatoires'));
          return false;
        }
        return true;
      case 2:
        return true;
      case 3:
        const totalPhotos = formData.existingPhotos.length + formData.photos.length;
        if (totalPhotos < 5) {
          handleError(new Error('Minimum 5 photos requises'));
          return false;
        }
        if (totalPhotos > 10) {
          handleError(new Error('Maximum 10 photos autorisées'));
          return false;
        }
        return true;
      case 4:
        return true;
      default:
        return true;
    }
  };

  const handleNext = () => {
    if (validateStep(currentStep)) {
      setCurrentStep(prev => prev + 1);
    }
  };

  const handlePrevious = () => {
    setCurrentStep(prev => prev - 1);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateStep(currentStep)) {
      return;
    }

    setSaving(true);

    try {
      // Upload new photos
      let newPhotoUrls = [];
      if (formData.photos.length > 0) {
        showSuccess('Upload des nouvelles photos en cours...');
        newPhotoUrls = await uploadService.uploadPhotos(formData.photos);
      }

      // Combine existing and new photo URLs
      const allPhotoUrls = [
        ...formData.existingPhotos.map(p => p.url),
        ...newPhotoUrls
      ];

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
        photoUrls: allPhotoUrls,
        documents: []
      };

      await productService.updateProduct(productId, productData);
      showSuccess('Produit mis à jour avec succès !');

      if (onSuccess) {
        onSuccess();
      }
      onClose();
    } catch (error) {
      handleError(error);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg p-8">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4 overflow-y-auto">
      <div className="bg-white rounded-lg max-w-4xl w-full my-8 relative">
        {/* Header */}
        <div className="sticky top-0 bg-white border-b px-6 py-4 rounded-t-lg">
          <div className="flex justify-between items-center">
            <h2 className="text-2xl font-bold text-gray-900">Modifier le produit</h2>
            <button
              onClick={onClose}
              className="p-2 hover:bg-gray-100 rounded-full transition-colors"
            >
              <XMarkIcon className="w-6 h-6 text-gray-600" />
            </button>
          </div>

          {/* Steps */}
          <div className="flex justify-between mt-6">
            {steps.map((step) => (
              <div key={step.number} className="flex items-center flex-1">
                <div className="flex flex-col items-center flex-1">
                  <div
                    className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold ${
                      currentStep >= step.number
                        ? 'bg-purple-600 text-white'
                        : 'bg-gray-200 text-gray-600'
                    }`}
                  >
                    {step.number}
                  </div>
                  <span className="text-xs mt-2 text-center">{step.title}</span>
                </div>
                {step.number < steps.length && (
                  <div
                    className={`h-1 flex-1 ${
                      currentStep > step.number ? 'bg-purple-600' : 'bg-gray-200'
                    }`}
                  />
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6">
          {/* Step 1: Informations générales */}
          {currentStep === 1 && (
            <div className="space-y-6">
              <Input
                label="Titre *"
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="Ex: Vase ancien en porcelaine"
                required
              />

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Catégorie *
                </label>
                <select
                  name="categoryId"
                  value={formData.categoryId}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  required
                >
                  <option value="">Sélectionner une catégorie</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description *
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  rows={5}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Décrivez votre objet en détail..."
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Type de vente *
                </label>
                <div className="grid grid-cols-2 gap-4">
                  <label className={`border-2 rounded-lg p-4 cursor-pointer ${formData.saleType === 'QUICK_SALE' ? 'border-purple-600 bg-purple-50' : 'border-gray-200'}`}>
                    <input
                      type="radio"
                      name="saleType"
                      value="QUICK_SALE"
                      checked={formData.saleType === 'QUICK_SALE'}
                      onChange={handleChange}
                      className="mr-2"
                    />
                    <span className="font-medium">Vente rapide</span>
                  </label>
                  <label className={`border-2 rounded-lg p-4 cursor-pointer ${formData.saleType === 'AUCTION' ? 'border-purple-600 bg-purple-50' : 'border-gray-200'}`}>
                    <input
                      type="radio"
                      name="saleType"
                      value="AUCTION"
                      checked={formData.saleType === 'AUCTION'}
                      onChange={handleChange}
                      className="mr-2"
                    />
                    <span className="font-medium">Enchères</span>
                  </label>
                </div>
              </div>

              <Input
                label="Prix estimé (€) *"
                name="estimatedValue"
                type="number"
                value={formData.estimatedValue}
                onChange={handleChange}
                placeholder="Ex: 500"
                required
              />
            </div>
          )}

          {/* Step 2: État et provenance */}
          {currentStep === 2 && (
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  État de l'objet *
                </label>
                <select
                  name="productCondition"
                  value={formData.productCondition}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  required
                >
                  {conditions.map(cond => (
                    <option key={cond.value} value={cond.value}>{cond.label}</option>
                  ))}
                </select>
              </div>

              <Input
                label="Marque"
                name="brand"
                value={formData.brand}
                onChange={handleChange}
                placeholder="Ex: Sèvres"
              />

              <Input
                label="Année de fabrication"
                name="yearOfManufacture"
                type="number"
                value={formData.yearOfManufacture}
                onChange={handleChange}
                placeholder="Ex: 1920"
              />

              <Input
                label="Origine"
                name="origin"
                value={formData.origin}
                onChange={handleChange}
                placeholder="Ex: France"
              />

              <Input
                label="Certificat d'authenticité"
                name="authenticityCertificate"
                value={formData.authenticityCertificate}
                onChange={handleChange}
                placeholder="Numéro du certificat"
              />

              <label className="flex items-center gap-2">
                <input
                  type="checkbox"
                  name="hasDocumentation"
                  checked={formData.hasDocumentation}
                  onChange={handleChange}
                  className="w-4 h-4 text-purple-600 rounded"
                />
                <span className="text-sm text-gray-700">Documentation disponible</span>
              </label>
            </div>
          )}

          {/* Step 3: Photos */}
          {currentStep === 3 && (
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Photos (5-10 photos) *
                </label>
                <p className="text-sm text-gray-500 mb-4">
                  Total actuel: {photoPreviews.length} photo(s)
                </p>

                <div className="grid grid-cols-3 md:grid-cols-4 gap-4 mb-4">
                  {photoPreviews.map((preview, index) => (
                    <div key={index} className="relative aspect-square">
                      <img
                        src={preview}
                        alt={`Photo ${index + 1}`}
                        className="w-full h-full object-cover rounded-lg"
                      />
                      <button
                        type="button"
                        onClick={() => {
                          if (index < formData.existingPhotos.length) {
                            removeExistingPhoto(index);
                          } else {
                            removeNewPhoto(index);
                          }
                        }}
                        className="absolute top-2 right-2 p-1 bg-red-500 text-white rounded-full hover:bg-red-600"
                      >
                        <XMarkIcon className="w-4 h-4" />
                      </button>
                      {index === 0 && (
                        <span className="absolute bottom-2 left-2 px-2 py-1 bg-purple-600 text-white text-xs rounded">
                          Photo principale
                        </span>
                      )}
                    </div>
                  ))}
                </div>

                {photoPreviews.length < 10 && (
                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={handlePhotoChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                  />
                )}
              </div>
            </div>
          )}

          {/* Step 4: Dimensions */}
          {currentStep === 4 && (
            <div className="space-y-6">
              <div className="grid grid-cols-2 gap-4">
                <Input
                  label="Largeur (cm)"
                  name="widthCm"
                  type="number"
                  step="0.01"
                  value={formData.widthCm}
                  onChange={handleChange}
                  placeholder="Ex: 30"
                />

                <Input
                  label="Hauteur (cm)"
                  name="heightCm"
                  type="number"
                  step="0.01"
                  value={formData.heightCm}
                  onChange={handleChange}
                  placeholder="Ex: 45"
                />

                <Input
                  label="Profondeur (cm)"
                  name="depthCm"
                  type="number"
                  step="0.01"
                  value={formData.depthCm}
                  onChange={handleChange}
                  placeholder="Ex: 20"
                />

                <Input
                  label="Poids (kg)"
                  name="weightKg"
                  type="number"
                  step="0.01"
                  value={formData.weightKg}
                  onChange={handleChange}
                  placeholder="Ex: 2.5"
                />
              </div>
            </div>
          )}

          {/* Navigation buttons */}
          <div className="flex justify-between mt-8 pt-6 border-t">
            <div>
              {currentStep > 1 && (
                <Button
                  type="button"
                  variant="outline"
                  onClick={handlePrevious}
                  disabled={saving}
                >
                  Précédent
                </Button>
              )}
            </div>

            <div className="flex gap-3">
              <Button
                type="button"
                variant="outline"
                onClick={onClose}
                disabled={saving}
              >
                Annuler
              </Button>

              {currentStep < steps.length ? (
                <Button
                  type="button"
                  variant="primary"
                  onClick={handleNext}
                  disabled={saving}
                >
                  Suivant
                </Button>
              ) : (
                <Button
                  type="submit"
                  variant="primary"
                  disabled={saving}
                >
                  {saving ? 'Enregistrement...' : 'Enregistrer les modifications'}
                </Button>
              )}
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProductEditModal;


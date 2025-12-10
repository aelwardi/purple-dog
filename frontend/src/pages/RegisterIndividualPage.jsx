import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { UserCircleIcon, EnvelopeIcon, MapPinIcon, LockClosedIcon, CameraIcon } from '@heroicons/react/24/outline';
import { useErrorHandler } from '../hooks/useErrorHandler';
import { authService } from '../services';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';

const RegisterIndividualPage = () => {
  const navigate = useNavigate();
  const { showSuccess, handleError } = useErrorHandler();
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    address: '',
    password: '',
    confirmPassword: '',
    profilePicture: null,
    ageConfirmation: false,
    newsletter: false,
    rgpdAccepted: false
  });

  const [errors, setErrors] = useState({});
  const [previewImage, setPreviewImage] = useState(null);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setFormData(prev => ({ ...prev, profilePicture: file }));
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewImage(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.firstName.trim()) newErrors.firstName = 'Le prénom est requis';
    if (!formData.lastName.trim()) newErrors.lastName = 'Le nom est requis';
    if (!formData.email.trim()) newErrors.email = 'L\'email est requis';
    else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = 'Email invalide';
    if (!formData.address.trim()) newErrors.address = 'L\'adresse est requise';
    if (formData.password.length < 8) newErrors.password = 'Le mot de passe doit contenir au moins 8 caractères';
    if (formData.password !== formData.confirmPassword) newErrors.confirmPassword = 'Les mots de passe ne correspondent pas';
    if (!formData.ageConfirmation) newErrors.ageConfirmation = 'Vous devez confirmer avoir plus de 18 ans';
    if (!formData.rgpdAccepted) newErrors.rgpdAccepted = 'Vous devez accepter les conditions RGPD';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      // Préparer les données pour l'API
      const registrationData = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
        address: formData.address,
        newsletter: formData.newsletter,
      };

      await authService.registerIndividual(registrationData);
      
      showSuccess('Inscription réussie ! Vous pouvez maintenant vous connecter.');
      navigate('/login');
    } catch (error) {
      handleError(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl mx-auto">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-display font-bold text-gray-900 mb-2">
            Inscription Particulier
          </h1>
          <p className="text-gray-600">
            Créez votre compte pour vendre vos objets de valeur
          </p>
        </div>

        <Card>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Photo de profil */}
            <div className="flex flex-col items-center">
              <div className="relative">
                <div className="w-32 h-32 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden">
                  {previewImage ? (
                    <img src={previewImage} alt="Preview" className="w-full h-full object-cover" />
                  ) : (
                    <UserCircleIcon className="w-20 h-20 text-gray-400" />
                  )}
                </div>
                <label className="absolute bottom-0 right-0 bg-purple-600 hover:bg-purple-700 text-white p-2 rounded-full cursor-pointer transition-colors">
                  <CameraIcon className="w-5 h-5" />
                  <input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    className="hidden"
                  />
                </label>
              </div>
              <p className="text-sm text-gray-500 mt-2">Photo de profil (optionnel)</p>
            </div>

            {/* Nom et prénom */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Prénom"
                name="firstName"
                type="text"
                required
                value={formData.firstName}
                onChange={handleChange}
                error={errors.firstName}
                placeholder="Jean"
                icon={<UserCircleIcon className="h-5 w-5 text-gray-400" />}
              />
              <Input
                label="Nom"
                name="lastName"
                type="text"
                required
                value={formData.lastName}
                onChange={handleChange}
                error={errors.lastName}
                placeholder="Dupont"
                icon={<UserCircleIcon className="h-5 w-5 text-gray-400" />}
              />
            </div>

            {/* Email */}
            <Input
              label="Email"
              name="email"
              type="email"
              required
              value={formData.email}
              onChange={handleChange}
              error={errors.email}
              placeholder="jean.dupont@example.com"
              icon={<EnvelopeIcon className="h-5 w-5 text-gray-400" />}
            />

            {/* Adresse postale */}
            <Input
              label="Adresse postale"
              name="address"
              type="text"
              required
              value={formData.address}
              onChange={handleChange}
              error={errors.address}
              placeholder="123 Rue de la Paix, 75000 Paris"
              icon={<MapPinIcon className="h-5 w-5 text-gray-400" />}
            />

            {/* Mot de passe */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Mot de passe"
                name="password"
                type="password"
                required
                value={formData.password}
                onChange={handleChange}
                error={errors.password}
                placeholder="••••••••"
                icon={<LockClosedIcon className="h-5 w-5 text-gray-400" />}
              />
              <Input
                label="Confirmer le mot de passe"
                name="confirmPassword"
                type="password"
                required
                value={formData.confirmPassword}
                onChange={handleChange}
                error={errors.confirmPassword}
                placeholder="••••••••"
                icon={<LockClosedIcon className="h-5 w-5 text-gray-400" />}
              />
            </div>

            {/* Confirmations */}
            <div className="space-y-4">
              <label className="flex items-start">
                <input
                  type="checkbox"
                  name="ageConfirmation"
                  checked={formData.ageConfirmation}
                  onChange={handleChange}
                  className="mt-1 rounded border-gray-300 text-purple-600 focus:ring-purple-500"
                />
                <span className="ml-3 text-sm text-gray-700">
                  Je certifie avoir plus de 18 ans *
                </span>
              </label>
              {errors.ageConfirmation && (
                <p className="text-sm text-red-600 ml-6">{errors.ageConfirmation}</p>
              )}

              <label className="flex items-start">
                <input
                  type="checkbox"
                  name="rgpdAccepted"
                  checked={formData.rgpdAccepted}
                  onChange={handleChange}
                  className="mt-1 rounded border-gray-300 text-purple-600 focus:ring-purple-500"
                />
                <span className="ml-3 text-sm text-gray-700">
                  J'accepte les conditions RGPD et la politique de confidentialité *
                </span>
              </label>
              {errors.rgpdAccepted && (
                <p className="text-sm text-red-600 ml-6">{errors.rgpdAccepted}</p>
              )}

              <label className="flex items-start">
                <input
                  type="checkbox"
                  name="newsletter"
                  checked={formData.newsletter}
                  onChange={handleChange}
                  className="mt-1 rounded border-gray-300 text-purple-600 focus:ring-purple-500"
                />
                <span className="ml-3 text-sm text-gray-700">
                  Je souhaite recevoir la newsletter Purple Dog
                </span>
              </label>
            </div>

            {/* Submit button */}
            <Button 
              type="submit" 
              variant="primary" 
              className="w-full" 
              size="large"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Création en cours...' : 'Créer mon compte'}
            </Button>

            <p className="text-sm text-gray-600 text-center">
              Votre identité sera anonymisée. Seul votre prénom sera visible sur la plateforme.
            </p>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Vous êtes un professionnel ?{' '}
              <Link to="/register?type=professional" className="font-medium text-purple-600 hover:text-purple-700">
                S'inscrire en tant que pro
              </Link>
            </p>
            <p className="text-sm text-gray-600 mt-2">
              Déjà un compte ?{' '}
              <Link to="/login" className="font-medium text-purple-600 hover:text-purple-700">
                Se connecter
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default RegisterIndividualPage;

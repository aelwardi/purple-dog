import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { BuildingOfficeIcon, EnvelopeIcon, MapPinIcon, LockClosedIcon, DocumentTextIcon, GlobeAltIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import { useErrorHandler } from '../hooks/useErrorHandler';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';

const RegisterProfessionalPage = () => {
  const navigate = useNavigate();
  const { registerProfessional, isAuthenticated } = useAuth();
  const { showSuccess, handleError } = useErrorHandler();

  // Redirect if already authenticated
  React.useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard');
    }
  }, [isAuthenticated, navigate]);

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    companyName: '',
    siret: '',
    document: null,
    password: '',
    confirmPassword: '',
    website: '',
    specialties: '',
    bio: '',
    profilePicture: null,
    cgvAccepted: false,
    mandateAccepted: false,
    newsletter: false,
    rgpdAccepted: false
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
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
      setFormData(prev => ({ ...prev, document: file }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.firstName.trim()) newErrors.firstName = 'Le prénom est requis';
    if (!formData.lastName.trim()) newErrors.lastName = 'Le nom est requis';
    if (!formData.email.trim()) newErrors.email = 'L\'email est requis';
    else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = 'Email invalide';
    if (!formData.companyName.trim()) newErrors.companyName = 'La dénomination est requise';
    if (!formData.siret.trim()) newErrors.siret = 'Le numéro SIRET est requis';
    else if (!/^\d{14}$/.test(formData.siret.replace(/\s/g, ''))) newErrors.siret = 'SIRET invalide (14 chiffres)';
    if (!formData.phone.trim()) newErrors.phone = 'Le téléphone est requis';
    if (formData.password.length < 8) newErrors.password = 'Le mot de passe doit contenir au moins 8 caractères';
    if (formData.password !== formData.confirmPassword) newErrors.confirmPassword = 'Les mots de passe ne correspondent pas';
    if (!formData.cgvAccepted) newErrors.cgvAccepted = 'Vous devez accepter les CGV';
    if (!formData.mandateAccepted) newErrors.mandateAccepted = 'Vous devez signer le mandat d\'apport d\'affaire';
    if (!formData.rgpdAccepted) newErrors.rgpdAccepted = 'Vous devez accepter les conditions RGPD';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    setIsSubmitting(true);

    try {
      const registrationData = {
        email: formData.email,
        password: formData.password,
        firstName: formData.firstName,
        lastName: formData.lastName,
        phone: formData.phone,
        companyName: formData.companyName,
        siret: formData.siret,
        website: formData.website || null,
        specialization: formData.specialties || null,
        bio: formData.bio || null,
        profilePicture: previewImage || null,
      };

      await registerProfessional(registrationData);

      showSuccess('Inscription réussie ! Bienvenue sur Purple Dog 🎉');

      navigate('/dashboard?type=professional');
    } catch (error) {
      const errorMessage = error.response?.data?.message ||
                          error.response?.data?.errors?.[0] ||
                          'Une erreur est survenue lors de l\'inscription';
      handleError(new Error(errorMessage));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-display font-bold text-gray-900 mb-2">
            Inscription Professionnel
          </h1>
          <p className="text-gray-600">
            Rejoignez Purple Dog et accédez à des objets d'exception
          </p>
        </div>

        <Card>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Informations personnelles */}
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Informations personnelles</h3>
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
                />
              </div>
              <div className="mt-4">
                <Input
                  label="Email professionnel"
                  name="email"
                  type="email"
                  required
                  value={formData.email}
                  onChange={handleChange}
                  error={errors.email}
                  placeholder="jean.dupont@entreprise.com"
                  icon={<EnvelopeIcon className="h-5 w-5 text-gray-400" />}
                />
              </div>
            </div>

            {/* Informations entreprise */}
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Informations entreprise</h3>
              <div className="space-y-4">
                <Input
                  label="Dénomination de l'entreprise"
                  name="companyName"
                  type="text"
                  required
                  value={formData.companyName}
                  onChange={handleChange}
                  error={errors.companyName}
                  placeholder="Antiquités Dupont"
                  icon={<BuildingOfficeIcon className="h-5 w-5 text-gray-400" />}
                />
                
                <Input
                  label="Numéro SIRET"
                  name="siret"
                  type="text"
                  required
                  value={formData.siret}
                  onChange={handleChange}
                  error={errors.siret}
                  placeholder="123 456 789 00001"
                />

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Document officiel (K-Bis, avis INSEE...) *
                  </label>
                  <input
                    type="file"
                    accept=".pdf,.jpg,.jpeg,.png"
                    onChange={handleFileChange}
                    className="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-purple-50 file:text-purple-700 hover:file:bg-purple-100"
                  />
                  {errors.document && (
                    <p className="mt-1 text-sm text-red-600">{errors.document}</p>
                  )}
                  {formData.document && (
                    <p className="mt-1 text-sm text-green-600">✓ {formData.document.name}</p>
                  )}
                </div>

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
              </div>
            </div>

            {/* Informations marketing */}
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Informations marketing</h3>
              <div className="space-y-4">
                <Input
                  label="Site internet"
                  name="website"
                  type="url"
                  value={formData.website}
                  onChange={handleChange}
                  placeholder="https://www.monsite.com"
                  icon={<GlobeAltIcon className="h-5 w-5 text-gray-400" />}
                />

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Spécialités *
                  </label>
                  <textarea
                    name="specialties"
                    value={formData.specialties}
                    onChange={handleChange}
                    rows="3"
                    required
                    className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                    placeholder="Ex: Mobilier Art Déco, Peintures impressionnistes, Argenterie XIXe siècle..."
                  />
                  {errors.specialties && (
                    <p className="mt-1 text-sm text-red-600">{errors.specialties}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Objets que vous recherchez le plus *
                  </label>
                  <textarea
                    name="searchedObjects"
                    value={formData.searchedObjects}
                    onChange={handleChange}
                    rows="3"
                    required
                    className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                    placeholder="Ex: Montres de luxe, Tableaux signés, Meubles français..."
                  />
                  {errors.searchedObjects && (
                    <p className="mt-1 text-sm text-red-600">{errors.searchedObjects}</p>
                  )}
                </div>

                <Input
                  label="Réseaux sociaux (optionnel)"
                  name="socialMedia"
                  type="text"
                  value={formData.socialMedia}
                  onChange={handleChange}
                  placeholder="Instagram, Facebook, LinkedIn..."
                />
              </div>
            </div>

            {/* Mot de passe */}
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Sécurité</h3>
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
            </div>

            {/* Confirmations */}
            <div className="space-y-4 bg-gray-50 p-4 rounded-lg">
              <label className="flex items-start">
                <input
                  type="checkbox"
                  name="cgvAccepted"
                  checked={formData.cgvAccepted}
                  onChange={handleChange}
                  className="mt-1 rounded border-gray-300 text-purple-600 focus:ring-purple-500"
                />
                <span className="ml-3 text-sm text-gray-700">
                  J'accepte les conditions générales de vente (CGV) *
                </span>
              </label>
              {errors.cgvAccepted && (
                <p className="text-sm text-red-600 ml-6">{errors.cgvAccepted}</p>
              )}

              <label className="flex items-start">
                <input
                  type="checkbox"
                  name="mandateAccepted"
                  checked={formData.mandateAccepted}
                  onChange={handleChange}
                  className="mt-1 rounded border-gray-300 text-purple-600 focus:ring-purple-500"
                />
                <span className="ml-3 text-sm text-gray-700">
                  J'accepte et signe le mandat d'apport d'affaire *
                </span>
              </label>
              {errors.mandateAccepted && (
                <p className="text-sm text-red-600 ml-6">{errors.mandateAccepted}</p>
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
            <Button type="submit" variant="primary" className="w-full" size="large">
              Créer mon compte professionnel
            </Button>

            <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
              <p className="text-sm text-purple-900 font-medium mb-2">
                🎁 Offre de lancement
              </p>
              <p className="text-sm text-purple-700">
                Profitez d'<strong>1 mois gratuit</strong> puis 49€/mois pour un accès illimité à la plateforme
              </p>
            </div>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Vous êtes un particulier ?{' '}
              <Link to="/register?type=individual" className="font-medium text-purple-600 hover:text-purple-700">
                S'inscrire en tant que particulier
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

export default RegisterProfessionalPage;

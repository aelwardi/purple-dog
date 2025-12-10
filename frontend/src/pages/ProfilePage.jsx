import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  UserCircleIcon,
  KeyIcon,
  TrashIcon,
  ArrowRightOnRectangleIcon,
} from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import { useErrorHandler } from '../hooks/useErrorHandler';
import profileService from '../services/profileService';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import ConfirmModal from '../components/common/ConfirmModal';

const profileSchema = z.object({
  firstName: z.string().min(2, 'Le prénom doit contenir au moins 2 caractères'),
  lastName: z.string().min(2, 'Le nom doit contenir au moins 2 caractères'),
  email: z.string().email('Email invalide'),
  phone: z.string().min(10, 'Téléphone invalide'),
  bio: z.string().optional(),
  companyName: z.string().optional(),
  siret: z.string().optional(),
});

const passwordSchema = z.object({
  currentPassword: z.string().min(1, 'Le mot de passe actuel est requis'),
  newPassword: z.string().min(8, 'Le nouveau mot de passe doit contenir au moins 8 caractères'),
  confirmPassword: z.string().min(1, 'La confirmation est requise'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: 'Les mots de passe ne correspondent pas',
  path: ['confirmPassword'],
});

const ProfilePage = () => {
  const navigate = useNavigate();
  const { user, updateUser, logout } = useAuth();
  const { showSuccess, showError, handleError } = useErrorHandler();
  const [activeTab, setActiveTab] = useState('profile');
  const [loading, setLoading] = useState(true);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showPasswordConfirm, setShowPasswordConfirm] = useState(false);
  const [showProfileConfirm, setShowProfileConfirm] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const [mobileStep, setMobileStep] = useState(1); // 1: Info de base, 2: Contact, 3: Bio/Pro
  const [pendingProfileData, setPendingProfileData] = useState(null);
  const [pendingPasswordData, setPendingPasswordData] = useState(null);
  const userMenuRef = useRef(null);

  // Close user menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setUserMenuOpen(false);
      }
    };

    if (userMenuOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [userMenuOpen]);

  // Profile form
  const {
    register: registerProfile,
    handleSubmit: handleSubmitProfile,
    formState: { errors: profileErrors, isSubmitting: isSubmittingProfile },
    reset: resetProfile,
  } = useForm({
    resolver: zodResolver(profileSchema),
  });

  // Password form
  const {
    register: registerPassword,
    handleSubmit: handleSubmitPassword,
    formState: { errors: passwordErrors, isSubmitting: isSubmittingPassword },
    reset: resetPassword,
  } = useForm({
    resolver: zodResolver(passwordSchema),
  });

  // Load profile data
  useEffect(() => {
    const loadProfile = async () => {
      try {
        const profile = await profileService.getCurrentProfile();
        resetProfile({
          firstName: profile.firstName || '',
          lastName: profile.lastName || '',
          email: profile.email || '',
          phone: profile.phone || '',
          bio: profile.bio || '',
          companyName: profile.companyName || '',
          siret: profile.siret || '',
        });
        setLoading(false);
      } catch (error) {
        handleError(error);
        setLoading(false);
      }
    };

    loadProfile();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []); // Load only once on mount

  // Update profile - show confirmation first
  const onSubmitProfile = (data) => {
    setPendingProfileData(data);
    setShowProfileConfirm(true);
  };

  // Confirm and save profile
  const confirmProfileUpdate = async () => {
    try {
      const updatedProfile = await profileService.updateProfile(pendingProfileData);
      updateUser(updatedProfile);
      showSuccess('Profil mis à jour avec succès !');
      setShowProfileConfirm(false);
      setPendingProfileData(null);
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Erreur lors de la mise à jour';
      showError(errorMessage);
      setShowProfileConfirm(false);
    }
  };

  // Change password - show confirmation first
  const onSubmitPassword = (data) => {
    setPendingPasswordData(data);
    setShowPasswordConfirm(true);
  };

  // Confirm and change password
  const confirmPasswordChange = async () => {
    try {
      await profileService.changePassword(pendingPasswordData);
      showSuccess('Mot de passe modifié avec succès !');
      resetPassword();
      setShowPasswordConfirm(false);
      setPendingPasswordData(null);
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Erreur lors du changement de mot de passe';
      showError(errorMessage);
      setShowPasswordConfirm(false);
    }
  };

  // Delete account
  const [isDeleting, setIsDeleting] = useState(false);
  const handleDeleteAccount = async () => {
    setIsDeleting(true);
    try {
      await profileService.deleteAccount();
      showSuccess('Compte supprimé avec succès');
      logout();
      navigate('/');
    } catch (error) {
      handleError(error);
      setIsDeleting(false);
    }
  };

  // Logout
  const handleLogout = async () => {
    try {
      await logout();
      showSuccess('Déconnexion réussie');
      navigate('/');
    } catch (error) {
      handleError(error);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        {/* Top Bar même en loading */}
        <div className="bg-white border-b border-gray-200 shadow-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              <div className="flex items-center gap-4">
                <button
                  onClick={() => navigate('/dashboard')}
                  className="text-gray-600 hover:text-purple-600 transition-colors"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                  </svg>
                </button>
                <h1 className="text-xl font-semibold text-gray-900">Mon Profil</h1>
              </div>
            </div>
          </div>
        </div>

        {/* Loading spinner */}
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top Bar */}
      <div className="bg-white border-b border-gray-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-4">
              <button
                onClick={() => navigate('/dashboard')}
                className="text-gray-600 hover:text-purple-600 transition-colors"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
              </button>
              <h1 className="text-xl font-semibold text-gray-900">
                Mon Profil
              </h1>
            </div>

            {/* User Menu */}
            <div className="relative" ref={userMenuRef}>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setUserMenuOpen(!userMenuOpen);
                }}
                className="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors"
              >
                <div className="w-8 h-8 bg-purple-600 rounded-full flex items-center justify-center text-white font-semibold text-sm">
                  {user?.firstName?.[0]}{user?.lastName?.[0]}
                </div>
                <span className="text-sm font-medium text-gray-700">
                  {user?.firstName} {user?.lastName}
                </span>
                <svg
                  className={`w-4 h-4 text-gray-500 transition-transform ${userMenuOpen ? 'rotate-180' : ''}`}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              {/* Dropdown Menu */}
              {userMenuOpen && (
                <div className="absolute right-0 mt-2 w-56 bg-white rounded-lg shadow-lg border border-gray-200 py-2 z-50">
                  <button
                    onClick={() => {
                      setUserMenuOpen(false);
                      navigate('/dashboard');
                    }}
                    className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-purple-50 hover:text-purple-600 transition-colors"
                  >
                    📊 Mon Dashboard
                  </button>
                  <button
                    onClick={() => {
                      setUserMenuOpen(false);
                    }}
                    className="w-full text-left px-4 py-2 text-sm text-purple-600 bg-purple-50 transition-colors"
                  >
                    👤 Mon Profil
                  </button>
                  <hr className="my-2" />
                  <button
                    onClick={() => {
                      setUserMenuOpen(false);
                      handleLogout();
                    }}
                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2 transition-colors"
                  >
                    <ArrowRightOnRectangleIcon className="w-4 h-4" />
                    <span>Se déconnecter</span>
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Tabs Navigation */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 mb-6">
          <div className="flex border-b border-gray-200">
            <button
              onClick={() => setActiveTab('profile')}
              className={`flex items-center gap-2 px-6 py-4 font-medium transition-colors ${
                activeTab === 'profile'
                  ? 'border-b-2 border-purple-600 text-purple-600'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <UserCircleIcon className="w-5 h-5" />
              <span>Informations personnelles</span>
            </button>
            <button
              onClick={() => setActiveTab('password')}
              className={`flex items-center gap-2 px-6 py-4 font-medium transition-colors ${
                activeTab === 'password'
                  ? 'border-b-2 border-purple-600 text-purple-600'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <KeyIcon className="w-5 h-5" />
              <span>Sécurité</span>
            </button>
          </div>
        </div>

        {/* Profile Tab */}
        {activeTab === 'profile' && (
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 md:p-6">
            <div className="mb-4 md:mb-6">
              <h2 className="text-lg font-semibold text-gray-900">Informations personnelles</h2>
              <p className="text-sm text-gray-600 mt-1">
                Mettez à jour vos informations de profil
              </p>
            </div>

            {/* Mobile Steps Indicator */}
            <div className="md:hidden mb-6">
              <div className="flex items-center justify-between mb-4">
                {[1, 2, 3].map((step) => (
                  <React.Fragment key={step}>
                    <div className="flex flex-col items-center flex-1">
                      <div
                        className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold transition-colors ${
                          mobileStep >= step
                            ? 'bg-purple-600 text-white'
                            : 'bg-gray-200 text-gray-500'
                        }`}
                      >
                        {step}
                      </div>
                      <span className="text-xs mt-1 text-gray-600">
                        {step === 1 ? 'Identité' : step === 2 ? 'Contact' : 'Plus'}
                      </span>
                    </div>
                    {step < 3 && (
                      <div
                        className={`flex-1 h-0.5 mx-2 mb-5 rounded transition-colors ${
                          mobileStep > step ? 'bg-purple-600' : 'bg-gray-200'
                        }`}
                      />
                    )}
                  </React.Fragment>
                ))}
              </div>
            </div>

            <form onSubmit={handleSubmitProfile(onSubmitProfile)} className="space-y-5">
              {/* Step 1: Identité - Mobile */}
              <div className={`md:hidden ${mobileStep !== 1 ? 'hidden' : ''}`}>
                <div className="space-y-4">
                  <Input
                    label="Prénom"
                    {...registerProfile('firstName')}
                    error={profileErrors.firstName?.message}
                    disabled={isSubmittingProfile}
                  />
                  <Input
                    label="Nom"
                    {...registerProfile('lastName')}
                    error={profileErrors.lastName?.message}
                    disabled={isSubmittingProfile}
                  />
                </div>
                <div className="flex justify-end mt-6">
                  <Button
                    type="button"
                    variant="primary"
                    onClick={() => setMobileStep(2)}
                    className="px-6"
                  >
                    Suivant
                  </Button>
                </div>
              </div>

              {/* Step 2: Contact - Mobile */}
              <div className={`md:hidden ${mobileStep !== 2 ? 'hidden' : ''}`}>
                <div className="space-y-4">
                  <Input
                    label="Email"
                    type="email"
                    {...registerProfile('email')}
                    error={profileErrors.email?.message}
                    disabled={isSubmittingProfile}
                  />
                  <Input
                    label="Téléphone"
                    {...registerProfile('phone')}
                    error={profileErrors.phone?.message}
                    disabled={isSubmittingProfile}
                    placeholder="+33 6 12 34 56 78"
                  />
                </div>
                <div className="flex gap-3 mt-6">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setMobileStep(1)}
                    className="flex-1"
                  >
                    Retour
                  </Button>
                  <Button
                    type="button"
                    variant="primary"
                    onClick={() => setMobileStep(3)}
                    className="flex-1"
                  >
                    Suivant
                  </Button>
                </div>
              </div>

              {/* Step 3: Bio/Pro - Mobile */}
              <div className={`md:hidden ${mobileStep !== 3 ? 'hidden' : ''}`}>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Bio
                    </label>
                    <textarea
                      {...registerProfile('bio')}
                      rows={4}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      placeholder="Parlez-nous de vous..."
                      disabled={isSubmittingProfile}
                    />
                    {profileErrors.bio && (
                      <p className="text-sm text-red-600 mt-1">{profileErrors.bio.message}</p>
                    )}
                  </div>

                  {/* Professional fields */}
                  {user?.role === 'PROFESSIONAL' && (
                    <>
                      <Input
                        label="Nom de l'entreprise"
                        {...registerProfile('companyName')}
                        error={profileErrors.companyName?.message}
                        disabled={isSubmittingProfile}
                      />
                      <Input
                        label="SIRET"
                        {...registerProfile('siret')}
                        error={profileErrors.siret?.message}
                        disabled={isSubmittingProfile}
                        placeholder="123 456 789 01234"
                      />
                    </>
                  )}
                </div>
                <div className="flex gap-3 mt-6">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setMobileStep(2)}
                    className="flex-1"
                  >
                    Retour
                  </Button>
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={isSubmittingProfile}
                    className="flex-1"
                  >
                    {isSubmittingProfile ? 'Enregistrement...' : 'Enregistrer'}
                  </Button>
                </div>
              </div>

              {/* Desktop: All fields visible */}
              <div className="hidden md:block space-y-5">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                  <Input
                    label="Prénom"
                    {...registerProfile('firstName')}
                    error={profileErrors.firstName?.message}
                    disabled={isSubmittingProfile}
                  />
                  <Input
                    label="Nom"
                    {...registerProfile('lastName')}
                    error={profileErrors.lastName?.message}
                    disabled={isSubmittingProfile}
                  />
                </div>

                <Input
                  label="Email"
                  type="email"
                  {...registerProfile('email')}
                  error={profileErrors.email?.message}
                  disabled={isSubmittingProfile}
                />

                <Input
                  label="Téléphone"
                  {...registerProfile('phone')}
                  error={profileErrors.phone?.message}
                  disabled={isSubmittingProfile}
                  placeholder="+33 6 12 34 56 78"
                />

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Bio
                  </label>
                  <textarea
                    {...registerProfile('bio')}
                    rows={4}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                    placeholder="Parlez-nous de vous..."
                    disabled={isSubmittingProfile}
                  />
                  {profileErrors.bio && (
                    <p className="text-sm text-red-600 mt-1">{profileErrors.bio.message}</p>
                  )}
                </div>

                {/* Professional fields */}
                {user?.role === 'PROFESSIONAL' && (
                  <div className="pt-4 border-t border-gray-200">
                    <h3 className="text-md font-semibold text-gray-900 mb-4">Informations professionnelles</h3>
                    <div className="space-y-5">
                      <Input
                        label="Nom de l'entreprise"
                        {...registerProfile('companyName')}
                        error={profileErrors.companyName?.message}
                        disabled={isSubmittingProfile}
                      />
                      <Input
                        label="SIRET"
                        {...registerProfile('siret')}
                        error={profileErrors.siret?.message}
                        disabled={isSubmittingProfile}
                        placeholder="123 456 789 01234"
                      />
                    </div>
                  </div>
                )}

                <div className="flex justify-end pt-6 border-t border-gray-200">
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={isSubmittingProfile}
                    className="px-6"
                  >
                    {isSubmittingProfile ? 'Enregistrement...' : 'Enregistrer les modifications'}
                  </Button>
                </div>
              </div>
            </form>
          </div>
        )}

        {/* Password Tab */}
        {activeTab === 'password' && (
          <div className="space-y-6">
            {/* Change Password Section */}
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="mb-6">
                <h2 className="text-lg font-semibold text-gray-900">Changer le mot de passe</h2>
                <p className="text-sm text-gray-600 mt-1">
                  Assurez-vous d'utiliser un mot de passe fort
                </p>
              </div>

              <form onSubmit={handleSubmitPassword(onSubmitPassword)} className="space-y-5 max-w-2xl">
                <Input
                  label="Mot de passe actuel"
                  type="password"
                  {...registerPassword('currentPassword')}
                  error={passwordErrors.currentPassword?.message}
                  disabled={isSubmittingPassword}
                  placeholder="••••••••"
                />

                <Input
                  label="Nouveau mot de passe"
                  type="password"
                  {...registerPassword('newPassword')}
                  error={passwordErrors.newPassword?.message}
                  disabled={isSubmittingPassword}
                  placeholder="••••••••"
                />

                <Input
                  label="Confirmer le nouveau mot de passe"
                  type="password"
                  {...registerPassword('confirmPassword')}
                  error={passwordErrors.confirmPassword?.message}
                  disabled={isSubmittingPassword}
                  placeholder="••••••••"
                />

                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <p className="text-sm font-medium text-blue-900 mb-2">Exigences du mot de passe</p>
                  <ul className="text-sm text-blue-700 space-y-1">
                    <li className="flex items-center gap-2">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                      </svg>
                      Au moins 8 caractères
                    </li>
                    <li className="flex items-center gap-2">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                      </svg>
                      Une majuscule et une minuscule
                    </li>
                    <li className="flex items-center gap-2">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                      </svg>
                      Un chiffre
                    </li>
                  </ul>
                </div>

                <div className="flex justify-end pt-4">
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={isSubmittingPassword}
                    className="px-6"
                  >
                    {isSubmittingPassword ? 'Modification...' : 'Mettre à jour le mot de passe'}
                  </Button>
                </div>
              </form>
            </div>

            {/* Danger Zone */}
            <div className="bg-white rounded-lg shadow-sm border border-red-200 p-6">
              <div className="mb-6">
                <h2 className="text-lg font-semibold text-red-600 flex items-center gap-2">
                  <TrashIcon className="w-5 h-5" />
                  Zone dangereuse
                </h2>
                <p className="text-sm text-gray-600 mt-1">
                  Cette action est irréversible. Toutes vos données seront définitivement supprimées.
                </p>
              </div>

              <Button
                variant="outline"
                onClick={() => setShowDeleteConfirm(true)}
                className="border-red-600 text-red-600 hover:bg-red-50"
              >
                <TrashIcon className="w-5 h-5 mr-2" />
                Supprimer mon compte
              </Button>
            </div>
          </div>
        )}
      </div>

      {/* Confirmation Modals */}

      {/* Profile Update Confirmation */}
      <ConfirmModal
        isOpen={showProfileConfirm}
        onClose={() => {
          setShowProfileConfirm(false);
          setPendingProfileData(null);
        }}
        onConfirm={confirmProfileUpdate}
        title="Confirmer les modifications"
        message="Êtes-vous sûr de vouloir enregistrer ces modifications ? Vos informations de profil seront mises à jour."
        confirmText="Enregistrer"
        variant="info"
      />

      {/* Password Change Confirmation */}
      <ConfirmModal
        isOpen={showPasswordConfirm}
        onClose={() => {
          setShowPasswordConfirm(false);
          setPendingPasswordData(null);
        }}
        onConfirm={confirmPasswordChange}
        title="Changer le mot de passe"
        message="Confirmez-vous vouloir changer votre mot de passe ? Vous devrez utiliser le nouveau mot de passe lors de votre prochaine connexion."
        confirmText="Changer le mot de passe"
        variant="warning"
      />

      {/* Account Deletion Confirmation */}
      <ConfirmModal
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDeleteAccount}
        title="Supprimer définitivement le compte"
        message="Cette action est irréversible ! Toutes vos données, produits, messages et historique seront définitivement supprimés. Êtes-vous absolument sûr ?"
        confirmText="Oui, supprimer mon compte"
        cancelText="Non, annuler"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  );
};

export default ProfilePage;


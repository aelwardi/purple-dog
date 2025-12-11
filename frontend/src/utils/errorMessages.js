/**
 * Messages d'erreurs personnalisés en français
 * Centralise tous les messages d'erreur de l'application
 */

export const ERROR_MESSAGES = {
  // Erreurs d'authentification
  AUTH: {
    INVALID_CREDENTIALS: 'Email ou mot de passe incorrect',
    UNAUTHORIZED: 'Vous devez être connecté pour accéder à cette page',
    FORBIDDEN: 'Vous n\'avez pas les permissions nécessaires',
    SESSION_EXPIRED: 'Votre session a expiré, veuillez vous reconnecter',
    EMAIL_ALREADY_EXISTS: 'Cet email est déjà utilisé',
    WEAK_PASSWORD: 'Le mot de passe doit contenir au moins 8 caractères',
    PASSWORD_MISMATCH: 'Les mots de passe ne correspondent pas',
  },

  // Erreurs de validation
  VALIDATION: {
    REQUIRED_FIELD: 'Ce champ est requis',
    INVALID_EMAIL: 'Adresse email invalide',
    INVALID_PHONE: 'Numéro de téléphone invalide',
    INVALID_SIRET: 'Numéro SIRET invalide (14 chiffres requis)',
    INVALID_URL: 'URL invalide',
    MIN_LENGTH: (min) => `Minimum ${min} caractères requis`,
    MAX_LENGTH: (max) => `Maximum ${max} caractères autorisés`,
    INVALID_FORMAT: 'Format invalide',
    FILE_TOO_LARGE: (maxSize) => `Le fichier ne doit pas dépasser ${maxSize}MB`,
    INVALID_FILE_TYPE: 'Type de fichier non autorisé',
  },

  // Erreurs réseau
  NETWORK: {
    NO_CONNECTION: 'Pas de connexion internet. Veuillez vérifier votre connexion.',
    TIMEOUT: 'La requête a expiré. Veuillez réessayer.',
    SERVER_ERROR: 'Erreur serveur. Veuillez réessayer plus tard.',
    SERVICE_UNAVAILABLE: 'Le service est temporairement indisponible.',
  },

  // Erreurs HTTP
  HTTP: {
    400: 'Requête invalide',
    401: 'Non authentifié',
    403: 'Accès refusé',
    404: 'Ressource non trouvée',
    409: 'Conflit - Cette ressource existe déjà',
    422: 'Données invalides',
    429: 'Trop de requêtes. Veuillez patienter.',
    500: 'Erreur serveur interne',
    502: 'Passerelle invalide',
    503: 'Service indisponible',
    504: 'Délai d\'attente de la passerelle dépassé',
  },

  // Erreurs de formulaire
  FORM: {
    SUBMISSION_FAILED: 'Échec de la soumission du formulaire',
    INVALID_DATA: 'Les données saisies sont invalides',
    MISSING_REQUIRED_FIELDS: 'Veuillez remplir tous les champs obligatoires',
  },

  // Erreurs de fichier
  FILE: {
    UPLOAD_FAILED: 'Échec du téléchargement du fichier',
    DOWNLOAD_FAILED: 'Échec du téléchargement',
    INVALID_FILE: 'Fichier invalide',
    FILE_NOT_FOUND: 'Fichier non trouvé',
  },

  // Erreurs génériques
  GENERIC: {
    UNKNOWN: 'Une erreur inattendue s\'est produite',
    TRY_AGAIN: 'Veuillez réessayer',
    CONTACT_SUPPORT: 'Si le problème persiste, contactez le support',
    OPERATION_FAILED: 'L\'opération a échoué',
    NOT_FOUND: 'Élément non trouvé',
  },

  // Erreurs spécifiques métier
  BUSINESS: {
    PRODUCT_NOT_AVAILABLE: 'Ce produit n\'est plus disponible',
    INSUFFICIENT_STOCK: 'Stock insuffisant',
    PAYMENT_FAILED: 'Le paiement a échoué',
    ORDER_NOT_FOUND: 'Commande non trouvée',
    INVALID_COUPON: 'Code promo invalide',
    SUBSCRIPTION_REQUIRED: 'Un abonnement est requis pour cette fonctionnalité',
  },
};

/**
 * Obtient un message d'erreur basé sur le code d'erreur
 */
export function getErrorMessage(errorCode, defaultMessage = ERROR_MESSAGES.GENERIC.UNKNOWN) {
  // Gestion des codes HTTP
  if (typeof errorCode === 'number') {
    return ERROR_MESSAGES.HTTP[errorCode] || defaultMessage;
  }

  // Gestion des codes string
  const parts = errorCode?.split('.');
  if (parts && parts.length === 2) {
    const [category, type] = parts;
    return ERROR_MESSAGES[category]?.[type] || defaultMessage;
  }

  return defaultMessage;
}

/**
 * Extrait un message d'erreur depuis différents formats de réponse
 */
export function extractErrorMessage(error) {
  // Message direct
  if (typeof error === 'string') {
    return error;
  }

  // Erreur Axios avec réponse du serveur
  if (error?.response?.data?.message) {
    return error.response.data.message;
  }

  if (error?.response?.data?.error) {
    return error.response.data.error;
  }

  // Erreur Axios avec validation backend
  if (error?.response?.data?.errors) {
    const errors = error.response.data.errors;
    if (Array.isArray(errors)) {
      return errors.map(e => e.message || e).join(', ');
    }
    if (typeof errors === 'object') {
      return Object.values(errors).flat().join(', ');
    }
  }

  // Message d'erreur standard
  if (error?.message) {
    return error.message;
  }

  // Code de statut HTTP
  if (error?.response?.status) {
    return getErrorMessage(error.response.status);
  }

  return ERROR_MESSAGES.GENERIC.UNKNOWN;
}

export default ERROR_MESSAGES;

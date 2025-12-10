/**
 * Service centralisé de gestion d'erreurs
 * Traite et formate les erreurs pour l'ensemble de l'application
 */

import toast from 'react-hot-toast';
import logger from './logger';
import { extractErrorMessage, ERROR_MESSAGES } from './errorMessages';

/**
 * Types d'erreurs personnalisés
 */
export class AppError extends Error {
  constructor(message, code = 'UNKNOWN', statusCode = 500, data = null) {
    super(message);
    this.name = 'AppError';
    this.code = code;
    this.statusCode = statusCode;
    this.data = data;
    this.timestamp = new Date().toISOString();
  }
}

export class ValidationError extends AppError {
  constructor(message, errors = {}) {
    super(message, 'VALIDATION_ERROR', 400, errors);
    this.name = 'ValidationError';
    this.errors = errors;
  }
}

export class AuthenticationError extends AppError {
  constructor(message = ERROR_MESSAGES.AUTH.UNAUTHORIZED) {
    super(message, 'AUTH_ERROR', 401);
    this.name = 'AuthenticationError';
  }
}

export class AuthorizationError extends AppError {
  constructor(message = ERROR_MESSAGES.AUTH.FORBIDDEN) {
    super(message, 'AUTHORIZATION_ERROR', 403);
    this.name = 'AuthorizationError';
  }
}

export class NetworkError extends AppError {
  constructor(message = ERROR_MESSAGES.NETWORK.NO_CONNECTION) {
    super(message, 'NETWORK_ERROR', 0);
    this.name = 'NetworkError';
  }
}

export class NotFoundError extends AppError {
  constructor(message = ERROR_MESSAGES.GENERIC.NOT_FOUND) {
    super(message, 'NOT_FOUND', 404);
    this.name = 'NotFoundError';
  }
}

/**
 * Classe principale de gestion d'erreurs
 */
class ErrorHandler {
  /**
   * Gère une erreur et affiche le message approprié
   */
  handle(error, options = {}) {
    const {
      showToast = true,
      logError = true,
      context = {},
      redirectTo = null,
    } = options;

    // Extraction du message d'erreur
    const errorMessage = extractErrorMessage(error);
    const errorDetails = this.getErrorDetails(error);

    // Logging
    if (logError) {
      logger.error('Error handled', error, { ...context, details: errorDetails });
    }

    // Affichage du toast
    if (showToast) {
      this.showErrorToast(errorMessage, errorDetails.statusCode);
    }

    // Redirection si nécessaire
    if (redirectTo) {
      setTimeout(() => {
        window.location.href = redirectTo;
      }, 1500);
    }

    // Gestion spécifique par type d'erreur
    this.handleByType(error, errorDetails);

    return errorDetails;
  }

  /**
   * Extrait les détails d'une erreur
   */
  getErrorDetails(error) {
    const details = {
      message: extractErrorMessage(error),
      code: error?.code || error?.response?.data?.code || 'UNKNOWN',
      statusCode: error?.response?.status || error?.statusCode || 0,
      isNetworkError: !error?.response && error?.request,
      isServerError: error?.response?.status >= 500,
      isClientError: error?.response?.status >= 400 && error?.response?.status < 500,
      data: error?.response?.data || error?.data || null,
      timestamp: new Date().toISOString(),
    };

    return details;
  }

  /**
   * Gère les erreurs par type
   */
  handleByType(error, details) {
    // Erreur réseau
    if (details.isNetworkError) {
      logger.warn('Network error detected');
      return;
    }

    // Erreur 401 - Non authentifié
    if (details.statusCode === 401) {
      this.handleAuthenticationError();
      return;
    }

    // Erreur 403 - Non autorisé
    if (details.statusCode === 403) {
      logger.warn('Authorization error - Access denied');
      return;
    }

    // Erreur 404
    if (details.statusCode === 404) {
      logger.warn('Resource not found', details.data);
      return;
    }

    // Erreur 422 - Validation
    if (details.statusCode === 422) {
      logger.warn('Validation error', details.data);
      return;
    }

    // Erreur serveur
    if (details.isServerError) {
      logger.error('Server error', error, details);
      return;
    }
  }

  /**
   * Gère les erreurs d'authentification
   */
  handleAuthenticationError() {
    // Nettoyer le localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('userType');
    localStorage.removeItem('userEmail');

    // Rediriger vers la page de connexion après un délai
    setTimeout(() => {
      if (window.location.pathname !== '/login') {
        window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname)}`;
      }
    }, 2000);
  }

  /**
   * Affiche un toast d'erreur approprié
   */
  showErrorToast(message, statusCode) {
    const duration = statusCode === 401 ? 3000 : 4000;

    toast.error(message, {
      duration,
      position: 'top-right',
      icon: this.getErrorIcon(statusCode),
      style: {
        background: '#fee2e2',
        color: '#991b1b',
        border: '1px solid #fecaca',
      },
    });
  }

  /**
   * Retourne l'icône appropriée selon le code d'erreur
   */
  getErrorIcon(statusCode) {
    switch (true) {
      case statusCode === 401:
        return '🔒';
      case statusCode === 403:
        return '⛔';
      case statusCode === 404:
        return '🔍';
      case statusCode >= 500:
        return '⚠️';
      default:
        return '❌';
    }
  }

  /**
   * Gère les erreurs de validation de formulaire
   */
  handleValidationError(errors) {
    const errorMessages = Object.values(errors)
      .flat()
      .filter(Boolean)
      .slice(0, 3); // Limite à 3 messages

    if (errorMessages.length > 0) {
      toast.error(errorMessages.join('\n'), {
        duration: 5000,
        position: 'top-right',
        style: {
          whiteSpace: 'pre-line',
          background: '#fee2e2',
          color: '#991b1b',
          border: '1px solid #fecaca',
        },
      });
    }

    logger.warn('Validation errors', errors);
  }

  /**
   * Gère les erreurs async/await
   */
  async handleAsync(fn, options = {}) {
    try {
      const result = await fn();
      return [result, null];
    } catch (error) {
      const errorDetails = this.handle(error, options);
      return [null, errorDetails];
    }
  }

  /**
   * Wrapper pour les promesses avec gestion d'erreur
   */
  wrapPromise(promise, options = {}) {
    return promise
      .then(result => [result, null])
      .catch(error => {
        const errorDetails = this.handle(error, options);
        return [null, errorDetails];
      });
  }

  /**
   * Affiche un message de succès
   */
  showSuccess(message, options = {}) {
    toast.success(message, {
      duration: 3000,
      position: 'top-right',
      icon: '✅',
      ...options,
    });
  }

  /**
   * Affiche un message d'information
   */
  showInfo(message, options = {}) {
    toast(message, {
      duration: 3000,
      position: 'top-right',
      icon: 'ℹ️',
      style: {
        background: '#dbeafe',
        color: '#1e40af',
        border: '1px solid #bfdbfe',
      },
      ...options,
    });
  }

  /**
   * Affiche un avertissement
   */
  showWarning(message, options = {}) {
    toast(message, {
      duration: 4000,
      position: 'top-right',
      icon: '⚠️',
      style: {
        background: '#fef3c7',
        color: '#92400e',
        border: '1px solid #fde68a',
      },
      ...options,
    });
  }
}

// Instance singleton
const errorHandler = new ErrorHandler();

export default errorHandler;

// Export des méthodes pour un usage direct
export const {
  handle,
  handleValidationError,
  handleAsync,
  wrapPromise,
  showSuccess,
  showInfo,
  showWarning,
} = errorHandler;

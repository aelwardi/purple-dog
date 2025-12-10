/**
 * Hook personnalisé pour la gestion d'erreurs
 * Simplifie la gestion des erreurs dans les composants
 */

import { useCallback } from 'react';
import errorHandler from '../utils/errorHandler';

/**
 * Hook pour gérer les erreurs
 */
export const useErrorHandler = (options = {}) => {
  const {
    showToast = true,
    logError = true,
    context = {},
  } = options;

  /**
   * Gère une erreur
   */
  const handleError = useCallback((error, customOptions = {}) => {
    return errorHandler.handle(error, {
      showToast,
      logError,
      context,
      ...customOptions,
    });
  }, [showToast, logError, context]);

  /**
   * Gère les erreurs de validation
   */
  const handleValidationError = useCallback((errors) => {
    return errorHandler.handleValidationError(errors);
  }, []);

  /**
   * Wrapper pour les fonctions async
   */
  const handleAsync = useCallback(async (fn, customOptions = {}) => {
    return errorHandler.handleAsync(fn, {
      showToast,
      logError,
      context,
      ...customOptions,
    });
  }, [showToast, logError, context]);

  /**
   * Wrapper pour les promesses
   */
  const wrapPromise = useCallback((promise, customOptions = {}) => {
    return errorHandler.wrapPromise(promise, {
      showToast,
      logError,
      context,
      ...customOptions,
    });
  }, [showToast, logError, context]);

  /**
   * Affiche un message de succès
   */
  const showSuccess = useCallback((message, toastOptions = {}) => {
    return errorHandler.showSuccess(message, toastOptions);
  }, []);

  /**
   * Affiche un message d'information
   */
  const showInfo = useCallback((message, toastOptions = {}) => {
    return errorHandler.showInfo(message, toastOptions);
  }, []);

  /**
   * Affiche un avertissement
   */
  const showWarning = useCallback((message, toastOptions = {}) => {
    return errorHandler.showWarning(message, toastOptions);
  }, []);

  return {
    handleError,
    handleValidationError,
    handleAsync,
    wrapPromise,
    showSuccess,
    showInfo,
    showWarning,
  };
};

export default useErrorHandler;

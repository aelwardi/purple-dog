/**
 * Hook personnalisé pour gérer les erreurs API
 * Gère les états de chargement, succès et erreur pour les appels API
 */

import { useState, useCallback } from 'react';
import { useErrorHandler } from './useErrorHandler';

/**
 * Hook pour gérer les erreurs API avec états
 */
export const useApiError = (options = {}) => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);

  const { handleError, showSuccess } = useErrorHandler(options);

  /**
   * Exécute une fonction async avec gestion d'erreur et états
   */
  const execute = useCallback(async (
    asyncFunction,
    {
      successMessage = null,
      onSuccess = null,
      onError = null,
      showToast = true,
    } = {}
  ) => {
    setIsLoading(true);
    setError(null);

    try {
      const result = await asyncFunction();
      setData(result);
      
      if (successMessage && showToast) {
        showSuccess(successMessage);
      }
      
      if (onSuccess) {
        onSuccess(result);
      }
      
      return [result, null];
    } catch (err) {
      const errorDetails = handleError(err, { showToast });
      setError(errorDetails);
      
      if (onError) {
        onError(err);
      }
      
      return [null, errorDetails];
    } finally {
      setIsLoading(false);
    }
  }, [handleError, showSuccess]);

  /**
   * Réinitialise l'état
   */
  const reset = useCallback(() => {
    setIsLoading(false);
    setError(null);
    setData(null);
  }, []);

  /**
   * Définit manuellement une erreur
   */
  const setManualError = useCallback((error) => {
    setError(error);
    handleError(error);
  }, [handleError]);

  return {
    isLoading,
    error,
    data,
    execute,
    reset,
    setError: setManualError,
    setData,
  };
};

export default useApiError;

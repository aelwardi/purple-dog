/**
 * Service de logging pour l'application
 * Gère les logs en développement et production
 */

const isDevelopment = import.meta.env.MODE === 'development';

/**
 * Niveaux de log
 */
export const LOG_LEVELS = {
  ERROR: 'error',
  WARN: 'warn',
  INFO: 'info',
  DEBUG: 'debug',
};

/**
 * Couleurs pour les logs console
 */
const LOG_COLORS = {
  error: 'color: #ef4444; font-weight: bold;',
  warn: 'color: #f59e0b; font-weight: bold;',
  info: 'color: #3b82f6; font-weight: bold;',
  debug: 'color: #8b5cf6; font-weight: bold;',
  success: 'color: #10b981; font-weight: bold;',
};

class Logger {
  constructor() {
    this.enabled = true;
  }

  /**
   * Formatte un message de log
   */
  formatMessage(level, message, data) {
    const timestamp = new Date().toISOString();
    return {
      timestamp,
      level,
      message,
      data,
    };
  }

  /**
   * Log un message d'erreur
   */
  error(message, error = null, context = {}) {
    if (!this.enabled) return;

    console.error(
      `%c[ERROR] ${message}`,
      LOG_COLORS.error
    );

    if (error) {
      console.error('Error details:', error);
    }

    if (Object.keys(context).length > 0) {
      console.error('Context:', context);
    }

    // En production, on pourrait envoyer à un service de monitoring
    if (!isDevelopment) {
      this.sendToMonitoring('error', message, { error, context });
    }
  }

  /**
   * Log un avertissement
   */
  warn(message, data = null) {
    if (!this.enabled) return;

    console.warn(
      `%c[WARN] ${message}`,
      LOG_COLORS.warn
    );

    if (data) {
      console.warn('Data:', data);
    }
  }

  /**
   * Log une information
   */
  info(message, data = null) {
    if (!this.enabled || !isDevelopment) return;

    console.info(
      `%c[INFO] ${message}`,
      LOG_COLORS.info
    );

    if (data) {
      console.info('Data:', data);
    }
  }

  /**
   * Log de débogage
   */
  debug(message, data = null) {
    if (!this.enabled || !isDevelopment) return;

    console.log(
      `%c[DEBUG] ${message}`,
      LOG_COLORS.debug
    );

    if (data) {
      console.log('Data:', data);
    }
  }

  /**
   * Log un succès
   */
  success(message, data = null) {
    if (!this.enabled || !isDevelopment) return;

    console.log(
      `%c[SUCCESS] ${message}`,
      LOG_COLORS.success
    );

    if (data) {
      console.log('Data:', data);
    }
  }

  /**
   * Log une requête API
   */
  apiRequest(method, url, data = null) {
    if (!isDevelopment) return;

    console.group(`%c🚀 API REQUEST: ${method.toUpperCase()} ${url}`, 'color: #06b6d4; font-weight: bold;');
    if (data) {
      console.log('Request data:', data);
    }
    console.log('Timestamp:', new Date().toISOString());
    console.groupEnd();
  }

  /**
   * Log une réponse API
   */
  apiResponse(method, url, status, data = null, duration = null) {
    if (!isDevelopment) return;

    const statusColor = status >= 200 && status < 300 ? '#10b981' : '#ef4444';
    console.group(`%c✅ API RESPONSE: ${method.toUpperCase()} ${url} [${status}]`, `color: ${statusColor}; font-weight: bold;`);
    if (duration) {
      console.log('Duration:', `${duration}ms`);
    }
    if (data) {
      console.log('Response data:', data);
    }
    console.groupEnd();
  }

  /**
   * Log une erreur API
   */
  apiError(method, url, error) {
    console.group(`%c❌ API ERROR: ${method.toUpperCase()} ${url}`, 'color: #ef4444; font-weight: bold;');
    console.error('Error:', error);
    if (error?.response) {
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    }
    console.groupEnd();
  }

  /**
   * Log un événement utilisateur
   */
  userAction(action, details = null) {
    if (!isDevelopment) return;

    console.log(
      `%c👤 USER ACTION: ${action}`,
      'color: #f59e0b; font-weight: bold;'
    );

    if (details) {
      console.log('Details:', details);
    }
  }

  /**
   * Log le cycle de vie d'un composant
   */
  componentLifecycle(componentName, lifecycle, data = null) {
    if (!isDevelopment) return;

    console.log(
      `%c🔄 ${componentName} [${lifecycle}]`,
      'color: #8b5cf6;'
    );

    if (data) {
      console.log('Data:', data);
    }
  }

  /**
   * Envoie les logs à un service de monitoring (Sentry, LogRocket, etc.)
   */
  sendToMonitoring(level, message, data) {
    // TODO: Implémenter l'envoi vers un service de monitoring
    // Exemple avec Sentry:
    // if (window.Sentry) {
    //   window.Sentry.captureMessage(message, {
    //     level,
    //     extra: data,
    //   });
    // }
  }

  /**
   * Active/désactive les logs
   */
  setEnabled(enabled) {
    this.enabled = enabled;
  }
}

// Instance singleton
const logger = new Logger();

export default logger;

// Export des méthodes pour un usage direct
export const {
  error,
  warn,
  info,
  debug,
  success,
  apiRequest,
  apiResponse,
  apiError,
  userAction,
  componentLifecycle,
} = logger;

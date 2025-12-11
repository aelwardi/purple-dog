/**
 * Error Boundary Component
 * Attrape les erreurs React et affiche un fallback UI
 */

import React from 'react';
import logger from '../../utils/logger';
import ErrorFallback from './ErrorFallback';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
  }

  static getDerivedStateFromError(error) {
    // Met à jour l'état pour afficher le fallback UI
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    // Log l'erreur
    logger.error('Error Boundary caught an error', error, {
      componentStack: errorInfo.componentStack,
      errorBoundary: this.props.name || 'ErrorBoundary',
    });

    // Met à jour l'état avec les détails de l'erreur
    this.setState({
      error,
      errorInfo,
    });

    // Appel du callback onError si fourni
    if (this.props.onError) {
      this.props.onError(error, errorInfo);
    }
  }

  resetError = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    });

    // Appel du callback onReset si fourni
    if (this.props.onReset) {
      this.props.onReset();
    }
  };

  render() {
    if (this.state.hasError) {
      // Si un fallback personnalisé est fourni, l'utiliser
      if (this.props.fallback) {
        return typeof this.props.fallback === 'function'
          ? this.props.fallback(this.state.error, this.resetError)
          : this.props.fallback;
      }

      // Sinon, utiliser le fallback par défaut
      return (
        <ErrorFallback
          error={this.state.error}
          errorInfo={this.state.errorInfo}
          resetError={this.resetError}
        />
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;

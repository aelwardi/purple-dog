/**
 * Error Message Component
 * Affiche un message d'erreur inline dans les formulaires ou sections
 */

import React from 'react';
import clsx from 'clsx';

const ErrorMessage = ({ 
  message, 
  className = '',
  icon = true,
  variant = 'error' // 'error', 'warning', 'info'
}) => {
  if (!message) return null;

  const variants = {
    error: {
      container: 'bg-red-50 border-red-200 text-red-800',
      icon: '❌',
      iconColor: 'text-red-500',
    },
    warning: {
      container: 'bg-yellow-50 border-yellow-200 text-yellow-800',
      icon: '⚠️',
      iconColor: 'text-yellow-500',
    },
    info: {
      container: 'bg-blue-50 border-blue-200 text-blue-800',
      icon: 'ℹ️',
      iconColor: 'text-blue-500',
    },
  };

  const variantStyles = variants[variant] || variants.error;

  return (
    <div
      className={clsx(
        'flex items-start gap-2 p-3 rounded-lg border text-sm',
        variantStyles.container,
        className
      )}
      role="alert"
    >
      {icon && (
        <span className={clsx('flex-shrink-0 text-base', variantStyles.iconColor)}>
          {variantStyles.icon}
        </span>
      )}
      <p className="flex-1">{message}</p>
    </div>
  );
};

export default ErrorMessage;

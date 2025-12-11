import React from 'react';
import clsx from 'clsx';

const Input = ({ 
  label,
  error,
  helperText,
  type = 'text',
  className = '',
  required = false,
  ...props 
}) => {
  return (
    <div className={className}>
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-2">
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <input
        type={type}
        className={clsx(
          'w-full px-4 py-3 text-gray-900 bg-white border rounded-lg transition-all duration-200',
          'focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent',
          error 
            ? 'border-red-500 focus:ring-red-500' 
            : 'border-gray-300'
        )}
        {...props}
      />
      {error && (
        <p className="mt-2 text-sm text-red-600">{error}</p>
      )}
      {helperText && !error && (
        <p className="mt-2 text-sm text-gray-500">{helperText}</p>
      )}
    </div>
  );
};

export default Input;

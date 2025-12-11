import React from 'react';
import { XMarkIcon, ExclamationTriangleIcon, CheckCircleIcon, InformationCircleIcon } from '@heroicons/react/24/outline';
import Modal from './Modal';
import Button from './Button';

const ConfirmModal = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = 'Confirmer',
  cancelText = 'Annuler',
  variant = 'danger',
  isLoading = false,
}) => {
  const variants = {
    danger: {
      icon: ExclamationTriangleIcon,
      iconBg: 'bg-red-100',
      iconColor: 'text-red-600',
      buttonClass: 'bg-red-600 hover:bg-red-700 text-white',
    },
    warning: {
      icon: ExclamationTriangleIcon,
      iconBg: 'bg-yellow-100',
      iconColor: 'text-yellow-600',
      buttonClass: 'bg-yellow-600 hover:bg-yellow-700 text-white',
    },
    success: {
      icon: CheckCircleIcon,
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
      buttonClass: 'bg-green-600 hover:bg-green-700 text-white',
    },
    info: {
      icon: InformationCircleIcon,
      iconBg: 'bg-purple-100',
      iconColor: 'text-purple-600',
      buttonClass: 'bg-purple-600 hover:bg-purple-700 text-white',
    },
    primary: {
      icon: InformationCircleIcon,
      iconBg: 'bg-purple-100',
      iconColor: 'text-purple-600',
      buttonClass: 'bg-purple-600 hover:bg-purple-700 text-white',
    },
  };

  const { icon: Icon, iconBg, iconColor, buttonClass } = variants[variant] || variants.info;

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="md">
      <div className="relative p-5">
        {/* Close button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
          disabled={isLoading}
        >
          <XMarkIcon className="w-5 h-5" />
        </button>

        {/* Icon */}
        <div className="flex justify-center mb-3">
          <div className={`${iconBg} rounded-full p-2.5`}>
            <Icon className={`w-6 h-6 ${iconColor}`} />
          </div>
        </div>

        {/* Title */}
        <h3 className="text-base font-semibold text-gray-900 text-center mb-2">
          {title}
        </h3>

        {/* Message */}
        <p className="text-sm text-gray-600 text-center mb-5">
          {message}
        </p>

        {/* Actions */}
        <div className="flex gap-3">
          <Button
            variant="outline"
            onClick={onClose}
            className="flex-1"
            disabled={isLoading}
          >
            {cancelText}
          </Button>
          <Button
            onClick={onConfirm}
            className={`flex-1 ${buttonClass}`}
            disabled={isLoading}
          >
            {isLoading ? 'Chargement...' : confirmText}
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default ConfirmModal;


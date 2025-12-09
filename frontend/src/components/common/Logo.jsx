import React from 'react';

const Logo = ({ className = '', size = 'normal' }) => {
  const sizeClasses = {
    small: 'h-8',
    normal: 'h-10',
    large: 'h-16'
  };

  return (
    <div className={`flex items-center gap-2 ${className}`}>
      {/* Purple Dog Icon */}
      <div className="relative">
        <svg 
          className={`${sizeClasses[size]} w-auto text-purple-600`}
          viewBox="0 0 48 48" 
          fill="currentColor"
          xmlns="http://www.w3.org/2000/svg"
        >
          {/* Dog head shape */}
          <path d="M24 8C16 8 12 12 10 16C8 20 8 28 12 32C16 36 20 38 24 38C28 38 32 36 36 32C40 28 40 20 38 16C36 12 32 8 24 8Z" />
          {/* Left ear */}
          <path d="M14 12C12 10 10 10 8 12C6 14 6 16 8 18C10 20 12 18 12 16C12 14 13 13 14 12Z" />
          {/* Right ear */}
          <path d="M34 12C36 10 38 10 40 12C42 14 42 16 40 18C38 20 36 18 36 16C36 14 35 13 34 12Z" />
          {/* Eyes */}
          <circle cx="18" cy="22" r="2" fill="white" />
          <circle cx="30" cy="22" r="2" fill="white" />
          {/* Nose */}
          <ellipse cx="24" cy="28" rx="3" ry="2" fill="white" opacity="0.8" />
          {/* Mouth */}
          <path d="M24 30C22 32 20 32 18 30M24 30C26 32 28 32 30 30" stroke="white" strokeWidth="1.5" fill="none" opacity="0.8" />
        </svg>
      </div>
      
      {/* Text Logo */}
      <span className={`font-display font-bold text-purple-600 ${
        size === 'small' ? 'text-xl' : size === 'large' ? 'text-4xl' : 'text-2xl'
      }`}>
        Purple Dog
      </span>
    </div>
  );
};

export default Logo;

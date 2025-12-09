import React from 'react';
import logoSvg from '../../assets/logo.svg';

const Logo = ({ size = 'normal' }) => {
  const sizes = {
    small: 'h-8',
    normal: 'h-10',
    large: 'h-12'
  };

  return (
    <div className="flex items-center">
      <img 
        src={logoSvg} 
        alt="Purple Dog" 
        className={`${sizes[size]} w-auto`}
      />
    </div>
  );
};

export default Logo;

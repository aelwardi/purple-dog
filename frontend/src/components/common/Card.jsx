import React from 'react';
import clsx from 'clsx';

const Card = ({ 
  children, 
  className = '',
  hoverable = true,
  padding = 'normal',
  ...props 
}) => {
  const paddings = {
    none: '',
    small: 'p-4',
    normal: 'p-6',
    large: 'p-8',
  };

  return (
    <div
      className={clsx(
        'bg-white rounded-lg shadow-card transition-shadow duration-300',
        hoverable && 'hover:shadow-card-hover',
        paddings[padding],
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};

export default Card;

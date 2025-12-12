import { useState, useEffect } from 'react';

/**
 * Composant de compte à rebours pour une enchère
 */
const AuctionTimer = ({ endDate, className = '' }) => {
  const [timeLeft, setTimeLeft] = useState(calculateTimeLeft());

  function calculateTimeLeft() {
    const difference = new Date(endDate) - new Date();
    
    if (difference <= 0) {
      return { expired: true };
    }

    return {
      expired: false,
      days: Math.floor(difference / (1000 * 60 * 60 * 24)),
      hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
      minutes: Math.floor((difference / 1000 / 60) % 60),
      seconds: Math.floor((difference / 1000) % 60),
    };
  }

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(calculateTimeLeft());
    }, 1000);

    return () => clearInterval(timer);
  }, [endDate]);

  if (timeLeft.expired) {
    return (
      <div className={`font-semibold text-gray-500 ${className}`}>
        Terminée
      </div>
    );
  }

  const isUrgent = timeLeft.days === 0 && timeLeft.hours < 1;

  return (
    <div className={`font-mono font-semibold ${isUrgent ? 'text-red-600' : 'text-purple-600'} ${className}`}>
      {timeLeft.days > 0 && `${timeLeft.days}j `}
      {String(timeLeft.hours).padStart(2, '0')}:
      {String(timeLeft.minutes).padStart(2, '0')}:
      {String(timeLeft.seconds).padStart(2, '0')}
    </div>
  );
};

export default AuctionTimer;

import React, { useState } from 'react';
import { EnvelopeIcon } from '@heroicons/react/24/outline';
import Button from '../common/Button';

const Newsletter = () => {
  const [email, setEmail] = useState('');
  const [subscribed, setSubscribed] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Intégrer avec le backend
    console.log('Newsletter subscription:', email);
    setSubscribed(true);
    setEmail('');
    setTimeout(() => setSubscribed(false), 3000);
  };

  return (
    <section className="py-16 bg-purple-600">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-8">
          <h2 className="text-3xl lg:text-4xl font-display font-bold text-white mb-4">
            Restez informé
          </h2>
          <p className="text-xl text-purple-100 max-w-2xl mx-auto">
            Inscrivez-vous à notre newsletter pour recevoir les dernières nouveautés et offres exclusives
          </p>
        </div>

        <form onSubmit={handleSubmit} className="max-w-md mx-auto">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1 relative">
              <EnvelopeIcon className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Votre adresse email"
                required
                className="w-full pl-12 pr-4 py-3 rounded-lg border-2 border-transparent focus:border-white focus:ring-2 focus:ring-white/20 transition-all"
              />
            </div>
            <Button 
              type="submit" 
              variant="secondary"
              className="whitespace-nowrap"
            >
              S'abonner
            </Button>
          </div>
          {subscribed && (
            <p className="mt-4 text-center text-white font-medium">
              Merci ! Vous êtes maintenant inscrit à notre newsletter.
            </p>
          )}
        </form>

        <p className="text-center text-purple-200 text-sm mt-6">
          En vous inscrivant, vous acceptez notre politique de confidentialité et nos conditions d'utilisation
        </p>
      </div>
    </section>
  );
};

export default Newsletter;

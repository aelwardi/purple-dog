import React from 'react';
import { CheckIcon, XMarkIcon } from '@heroicons/react/24/outline';
import Card from '../components/common/Card';
import Button from '../components/common/Button';

const PricingPage = () => {
  const plans = [
    {
      name: 'Particulier',
      price: 'Gratuit',
      description: 'Pour vendre vos objets de valeur',
      features: [
        { text: 'Vente illimitée d\'objets', included: true },
        { text: 'Accès aux professionnels certifiés', included: true },
        { text: 'Enchères et vente rapide', included: true },
        { text: 'Photos et descriptions détaillées', included: true },
        { text: 'Messagerie sécurisée', included: true },
        { text: 'Commission vendeur: 2%', included: true },
        { text: 'Acheter des objets', included: false },
        { text: 'Recherche avancée', included: false },
      ],
      cta: 'Commencer gratuitement',
      ctaLink: '/register?type=individual',
      highlighted: false
    },
    {
      name: 'Professionnel',
      price: '49€',
      period: '/mois',
      description: 'Pour acheter et vendre des objets d\'exception',
      trial: '1 mois gratuit',
      features: [
        { text: 'Toutes les fonctionnalités Particulier', included: true },
        { text: 'Acheter des objets', included: true },
        { text: 'Vente illimitée d\'objets', included: true },
        { text: 'Recherche avancée avec filtres', included: true },
        { text: 'Favoris et alertes', included: true },
        { text: 'Historique des enchères', included: true },
        { text: 'Support prioritaire', included: true },
        { text: 'Commission acheteur: 3%', included: true },
      ],
      cta: 'Essayer gratuitement',
      ctaLink: '/register?type=professional',
      highlighted: true
    }
  ];

  const faq = [
    {
      question: 'Puis-je changer de forfait ?',
      answer: 'Les particuliers restent toujours gratuits. Les professionnels peuvent annuler leur abonnement à tout moment.'
    },
    {
      question: 'Comment fonctionnent les commissions ?',
      answer: 'Pour chaque vente, nous prélevons 2% au vendeur et 3% à l\'acheteur. Ces commissions couvrent la sécurisation des transactions et la maintenance de la plateforme.'
    },
    {
      question: 'Le mois gratuit est-il vraiment sans engagement ?',
      answer: 'Oui ! Vous pouvez annuler à tout moment pendant le premier mois et ne serez pas facturé.'
    },
    {
      question: 'Quels sont les moyens de paiement acceptés ?',
      answer: 'Nous acceptons toutes les cartes bancaires via notre partenaire Stripe (Visa, Mastercard, American Express).'
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-purple-600 to-purple-800 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 className="text-4xl md:text-5xl font-display font-bold mb-6">
            Des forfaits adaptés à vos besoins
          </h1>
          <p className="text-xl max-w-3xl mx-auto opacity-90">
            Que vous soyez particulier ou professionnel, Purple Dog vous accompagne dans vos transactions
          </p>
        </div>
      </div>

      {/* Pricing Cards */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid md:grid-cols-2 gap-8 max-w-5xl mx-auto">
          {plans.map((plan, index) => (
            <Card 
              key={index} 
              className={`relative p-8 ${plan.highlighted ? 'border-2 border-purple-600 shadow-2xl' : ''}`}
            >
              {plan.highlighted && (
                <div className="absolute top-0 right-8 transform -translate-y-1/2">
                  <span className="bg-purple-600 text-white px-4 py-1 rounded-full text-sm font-semibold">
                    Le plus populaire
                  </span>
                </div>
              )}
              
              <div className="text-center mb-8">
                <h3 className="text-2xl font-display font-bold text-gray-900 mb-2">
                  {plan.name}
                </h3>
                <p className="text-gray-600 mb-4">{plan.description}</p>
                
                <div className="mb-4">
                  <span className="text-5xl font-bold text-gray-900">{plan.price}</span>
                  {plan.period && <span className="text-gray-600">{plan.period}</span>}
                </div>
                
                {plan.trial && (
                  <div className="inline-block bg-green-100 text-green-800 px-4 py-2 rounded-full text-sm font-semibold">
                    {plan.trial}
                  </div>
                )}
              </div>

              <ul className="space-y-4 mb-8">
                {plan.features.map((feature, fIndex) => (
                  <li key={fIndex} className="flex items-start">
                    {feature.included ? (
                      <CheckIcon className="w-6 h-6 text-green-500 mr-3 flex-shrink-0" />
                    ) : (
                      <XMarkIcon className="w-6 h-6 text-gray-300 mr-3 flex-shrink-0" />
                    )}
                    <span className={feature.included ? 'text-gray-700' : 'text-gray-400'}>
                      {feature.text}
                    </span>
                  </li>
                ))}
              </ul>

              <Button 
                variant={plan.highlighted ? 'primary' : 'outline'} 
                className="w-full"
                onClick={() => window.location.href = plan.ctaLink}
              >
                {plan.cta}
              </Button>
            </Card>
          ))}
        </div>

        {/* Commission Details */}
        <div className="mt-16 max-w-4xl mx-auto">
          <Card className="p-8 bg-purple-50 border-purple-200">
            <h3 className="text-2xl font-display font-bold text-purple-900 mb-4 text-center">
              Comment fonctionnent les commissions ?
            </h3>
            <div className="grid md:grid-cols-2 gap-8">
              <div>
                <h4 className="font-semibold text-purple-900 mb-2">💰 Commission vendeur : 2%</h4>
                <p className="text-purple-700 text-sm">
                  Prélevée sur chaque vente. Si vous vendez un objet à 1000€, vous recevez 980€.
                </p>
              </div>
              <div>
                <h4 className="font-semibold text-purple-900 mb-2">💳 Commission acheteur : 3%</h4>
                <p className="text-purple-700 text-sm">
                  Ajoutée au prix d'achat. Si vous achetez un objet à 1000€, vous payez 1030€.
                </p>
              </div>
            </div>
          </Card>
        </div>

        {/* FAQ */}
        <div className="mt-16">
          <h2 className="text-3xl font-display font-bold text-gray-900 mb-8 text-center">
            Questions fréquentes
          </h2>
          <div className="max-w-3xl mx-auto space-y-4">
            {faq.map((item, index) => (
              <Card key={index} className="p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  {item.question}
                </h3>
                <p className="text-gray-600">
                  {item.answer}
                </p>
              </Card>
            ))}
          </div>
        </div>

        {/* CTA Section */}
        <div className="mt-16 text-center">
          <Card className="p-8 bg-gradient-to-r from-purple-600 to-purple-800 text-white">
            <h3 className="text-2xl font-display font-bold mb-4">
              Prêt à rejoindre Purple Dog ?
            </h3>
            <p className="text-lg mb-6 opacity-90">
              Commencez à vendre ou acheter des objets d'exception dès aujourd'hui
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button 
                variant="outline" 
                className="bg-white text-purple-600 hover:bg-gray-100 border-white"
                onClick={() => window.location.href = '/register?type=individual'}
              >
                Je suis particulier
              </Button>
              <Button 
                variant="outline" 
                className="bg-transparent text-white hover:bg-purple-700 border-white"
                onClick={() => window.location.href = '/register?type=professional'}
              >
                Je suis professionnel
              </Button>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default PricingPage;

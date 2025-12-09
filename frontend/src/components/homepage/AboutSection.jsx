import React from 'react';
import { CheckCircleIcon, ShieldCheckIcon, CurrencyEuroIcon, SparklesIcon } from '@heroicons/react/24/outline';

const AboutSection = () => {
  const features = [
    {
      icon: <ShieldCheckIcon className="h-8 w-8 text-purple-600" />,
      title: 'Sécurisé & de confiance',
      description: 'Paiements sécurisés via Stripe, vérification des professionnels et protection des données personnelles'
    },
    {
      icon: <CurrencyEuroIcon className="h-8 w-8 text-purple-600" />,
      title: 'Meilleur prix garanti',
      description: 'Système d\'enchères et vente rapide pour obtenir la meilleure valeur pour vos objets précieux'
    },
    {
      icon: <SparklesIcon className="h-8 w-8 text-purple-600" />,
      title: 'Simple & rapide',
      description: 'Publiez vos objets en quelques minutes et recevez des offres de professionnels qualifiés'
    },
    {
      icon: <CheckCircleIcon className="h-8 w-8 text-purple-600" />,
      title: 'Support dédié',
      description: 'Notre équipe vous accompagne à chaque étape de votre transaction, de la mise en vente à la livraison'
    }
  ];

  return (
    <section className="py-20 bg-gradient-to-br from-purple-50 to-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl lg:text-4xl font-display font-bold text-gray-900 mb-4">
            Pourquoi choisir Purple Dog ?
          </h2>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            LA plateforme pour vendre mieux vos objets de valeurs à des tiers de confiance
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-16">
          {features.map((feature, index) => (
            <div 
              key={index}
              className="bg-white rounded-xl p-6 shadow-card hover:shadow-card-hover transition-shadow duration-300"
            >
              <div className="mb-4">{feature.icon}</div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                {feature.title}
              </h3>
              <p className="text-gray-600">
                {feature.description}
              </p>
            </div>
          ))}
        </div>

        {/* How it works */}
        <div className="bg-white rounded-2xl p-8 lg:p-12 shadow-xl">
          <h3 className="text-2xl lg:text-3xl font-display font-bold text-center text-gray-900 mb-12">
            Comment ça marche ?
          </h3>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl font-bold text-purple-600">1</span>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">
                Inscrivez-vous
              </h4>
              <p className="text-gray-600">
                Créez votre compte en quelques minutes. Gratuit pour les particuliers vendeurs !
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl font-bold text-purple-600">2</span>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">
                Publiez votre objet
              </h4>
              <p className="text-gray-600">
                Ajoutez photos et description. Choisissez entre enchères ou vente rapide.
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl font-bold text-purple-600">3</span>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">
                Vendez au meilleur prix
              </h4>
              <p className="text-gray-600">
                Recevez des offres de professionnels et vendez votre objet au meilleur prix.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default AboutSection;

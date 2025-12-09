import React from 'react';
import { ShieldCheckIcon, CurrencyEuroIcon, SparklesIcon, UserGroupIcon } from '@heroicons/react/24/outline';
import Card from '../components/common/Card';

const AboutPage = () => {
  const values = [
    {
      icon: <ShieldCheckIcon className="w-12 h-12 text-purple-600" />,
      title: 'Sécurité & Confiance',
      description: 'Tous nos professionnels sont vérifiés et certifiés. Vos objets de valeur sont entre de bonnes mains.'
    },
    {
      icon: <CurrencyEuroIcon className="w-12 h-12 text-purple-600" />,
      title: 'Meilleur Prix',
      description: 'Le système d\'enchères garantit que vous obtenez le meilleur prix pour vos objets précieux.'
    },
    {
      icon: <SparklesIcon className="w-12 h-12 text-purple-600" />,
      title: 'Simple & Rapide',
      description: 'Déposez vos objets en quelques clics et recevez des offres de professionnels qualifiés.'
    },
    {
      icon: <UserGroupIcon className="w-12 h-12 text-purple-600" />,
      title: 'Communauté',
      description: 'Rejoignez une communauté de passionnés d\'art, d\'antiquités et d\'objets de collection.'
    }
  ];

  const steps = [
    {
      number: '01',
      title: 'Inscrivez-vous',
      description: 'Créez votre compte en quelques minutes. Particuliers comme professionnels sont les bienvenus.'
    },
    {
      number: '02',
      title: 'Déposez votre objet',
      description: 'Ajoutez photos, description et choisissez entre enchères ou vente rapide.'
    },
    {
      number: '03',
      title: 'Recevez des offres',
      description: 'Les professionnels certifiés vous font des propositions pour votre objet.'
    },
    {
      number: '04',
      title: 'Vendez en toute sécurité',
      description: 'Acceptez l\'offre qui vous convient et nous gérons la livraison et le paiement.'
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-purple-600 to-purple-800 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 className="text-4xl md:text-5xl font-display font-bold mb-6">
            À propos de Purple Dog
          </h1>
          <p className="text-xl max-w-3xl mx-auto opacity-90">
            La plateforme de confiance pour vendre et acheter des objets de valeur entre particuliers et professionnels
          </p>
        </div>
      </div>

      {/* Mission Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid md:grid-cols-2 gap-12 items-center">
          <div>
            <h2 className="text-3xl font-display font-bold text-gray-900 mb-6">
              Notre Mission
            </h2>
            <p className="text-lg text-gray-700 mb-4">
              Purple Dog est née d'un constat simple : trop de particuliers possèdent des objets de valeur 
              qu'ils ne savent pas comment vendre au juste prix, tandis que des professionnels recherchent 
              constamment de nouvelles pièces pour leur clientèle.
            </p>
            <p className="text-lg text-gray-700 mb-4">
              Notre mission est de créer un pont de confiance entre ces deux mondes, en garantissant 
              transparence, sécurité et équité pour tous.
            </p>
            <p className="text-lg text-gray-700">
              Nous croyons que chaque objet a une histoire et mérite de trouver le bon acquéreur 
              qui saura l'apprécier à sa juste valeur.
            </p>
          </div>
          <div className="relative">
            <div className="aspect-square bg-gradient-to-br from-purple-100 to-purple-200 rounded-2xl shadow-2xl flex items-center justify-center p-12 overflow-hidden">
              {/* Handshake & Trust Illustration */}
              <svg className="w-full h-full text-purple-600" viewBox="0 0 200 200" fill="none" xmlns="http://www.w3.org/2000/svg">
                {/* Background circles for decoration */}
                <circle cx="50" cy="50" r="30" fill="currentColor" opacity="0.1"/>
                <circle cx="150" cy="150" r="40" fill="currentColor" opacity="0.1"/>
                
                {/* Handshake illustration */}
                <g transform="translate(40, 70)">
                  {/* Left hand */}
                  <path d="M10,40 Q20,30 35,35 L45,45 Q50,50 45,55 L20,60 Q15,60 10,55 Z" fill="currentColor" opacity="0.8"/>
                  
                  {/* Right hand */}
                  <path d="M110,40 Q100,30 85,35 L75,45 Q70,50 75,55 L100,60 Q105,60 110,55 Z" fill="currentColor" opacity="0.8"/>
                  
                  {/* Middle connection */}
                  <rect x="40" y="42" width="40" height="15" rx="3" fill="currentColor"/>
                  
                  {/* Sparkle/Star elements around */}
                  <circle cx="25" cy="25" r="3" fill="currentColor"/>
                  <circle cx="95" cy="25" r="3" fill="currentColor"/>
                  <circle cx="60" cy="15" r="2" fill="currentColor"/>
                </g>
                
                {/* Art frame representation */}
                <rect x="60" y="120" width="35" height="45" rx="2" stroke="currentColor" strokeWidth="3" fill="none"/>
                <rect x="105" y="120" width="35" height="45" rx="2" stroke="currentColor" strokeWidth="3" fill="none"/>
                
                {/* Price tag */}
                <g transform="translate(150, 40)">
                  <path d="M0,0 L20,0 L25,5 L20,10 L0,10 Z" fill="currentColor" opacity="0.7"/>
                  <circle cx="5" cy="5" r="2" fill="white"/>
                </g>
              </svg>
            </div>
            <div className="absolute -bottom-4 -right-4 w-24 h-24 bg-purple-600 rounded-full opacity-20"></div>
            <div className="absolute -top-4 -left-4 w-16 h-16 bg-purple-400 rounded-full opacity-30"></div>
          </div>
        </div>
      </div>

      {/* Values Section */}
      <div className="bg-white py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-display font-bold text-gray-900 mb-4">
              Nos Valeurs
            </h2>
            <p className="text-lg text-gray-600 max-w-2xl mx-auto">
              Ce qui nous guide chaque jour pour vous offrir la meilleure expérience
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {values.map((value, index) => (
              <Card key={index} className="text-center p-6 hover:shadow-xl transition-shadow">
                <div className="flex justify-center mb-4">
                  {value.icon}
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-3">
                  {value.title}
                </h3>
                <p className="text-gray-600">
                  {value.description}
                </p>
              </Card>
            ))}
          </div>
        </div>
      </div>

      {/* How it works */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-display font-bold text-gray-900 mb-4">
            Comment ça marche ?
          </h2>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Vendre vos objets de valeur n'a jamais été aussi simple
          </p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
          {steps.map((step, index) => (
            <div key={index} className="relative">
              <div className="flex flex-col items-center text-center">
                <div className="w-20 h-20 bg-purple-100 rounded-full flex items-center justify-center mb-4">
                  <span className="text-3xl font-display font-bold text-purple-600">
                    {step.number}
                  </span>
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-3">
                  {step.title}
                </h3>
                <p className="text-gray-600">
                  {step.description}
                </p>
              </div>
              {index < steps.length - 1 && (
                <div className="hidden lg:block absolute top-10 left-1/2 w-full h-0.5 bg-purple-200" />
              )}
            </div>
          ))}
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-purple-600 text-white py-16">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-display font-bold mb-4">
            Prêt à commencer ?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Rejoignez Purple Dog et découvrez une nouvelle façon de vendre vos objets de valeur
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <a 
              href="/register?type=individual"
              className="bg-white text-purple-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition-colors"
            >
              Je suis particulier
            </a>
            <a 
              href="/register?type=professional"
              className="border-2 border-white text-white px-8 py-3 rounded-lg font-semibold hover:bg-purple-700 transition-colors"
            >
              Je suis professionnel
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AboutPage;

import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/24/outline';
import Button from '../common/Button';
import Badge from '../common/Badge';

const Hero = () => {
  const [currentSlide, setCurrentSlide] = useState(0);

  // Exemple d'objets pour le carousel (à remplacer par des vraies données)
  const featuredObjects = [
    {
      id: 1,
      name: 'Vase Art Déco en cristal',
      category: 'Objets d\'art & tableaux',
      image: 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=800',
      finalPrice: '2,450€',
      saleType: 'auction',
      description: 'Magnifique vase en cristal de la période Art Déco, parfait état'
    },
    {
      id: 2,
      name: 'Montre Rolex Submariner Vintage',
      category: 'Bijoux & montres',
      image: 'https://images.unsplash.com/photo-1587836374139-ddb2127fcb2d?w=800',
      finalPrice: '8,900€',
      saleType: 'quicksale',
      description: 'Rolex Submariner des années 1960, boîte et papiers d\'origine'
    },
    {
      id: 3,
      name: 'Fauteuil scandinave vintage',
      category: 'Meubles anciens',
      image: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800',
      finalPrice: '1,200€',
      saleType: 'auction',
      description: 'Fauteuil design scandinave des années 1960, tissu d\'origine'
    },
    {
      id: 4,
      name: 'Tableau impressionniste',
      category: 'Objets d\'art & tableaux',
      image: 'https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?w=800',
      finalPrice: '5,600€',
      saleType: 'quicksale',
      description: 'Huile sur toile, paysage impressionniste signé, début XXe siècle'
    },
    {
      id: 5,
      name: 'Service à thé en argent',
      category: 'Vaisselle & argenterie',
      image: 'https://images.unsplash.com/photo-1610224947028-a4c96e1fe3e8?w=800',
      finalPrice: '3,200€',
      saleType: 'auction',
      description: 'Service à thé complet en argent massif, époque XIXe siècle'
    }
  ];

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % featuredObjects.length);
    }, 5000);
    return () => clearInterval(timer);
  }, []);

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % featuredObjects.length);
  };

  const prevSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + featuredObjects.length) % featuredObjects.length);
  };

  const current = featuredObjects[currentSlide];

  return (
    <div className="relative bg-gradient-to-br from-purple-50 to-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 lg:py-20">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          {/* Left side - Image carousel */}
          <div className="relative">
            <div className="relative aspect-square rounded-2xl overflow-hidden shadow-2xl bg-white">
              <img
                src={current.image}
                alt={current.name}
                className="w-full h-full object-cover transition-opacity duration-500"
              />
              <div className="absolute top-4 right-4">
                <Badge variant={current.saleType === 'auction' ? 'auction' : 'quicksale'}>
                  {current.saleType === 'auction' ? 'ENCHÈRE' : 'VENTE RAPIDE'}
                </Badge>
              </div>
            </div>

            {/* Carousel controls */}
            <button
              onClick={prevSlide}
              className="absolute left-4 top-1/2 -translate-y-1/2 bg-white/90 hover:bg-white p-2 rounded-full shadow-lg transition-all"
            >
              <ChevronLeftIcon className="h-6 w-6 text-gray-700" />
            </button>
            <button
              onClick={nextSlide}
              className="absolute right-4 top-1/2 -translate-y-1/2 bg-white/90 hover:bg-white p-2 rounded-full shadow-lg transition-all"
            >
              <ChevronRightIcon className="h-6 w-6 text-gray-700" />
            </button>

            {/* Dots */}
            <div className="flex justify-center mt-6 gap-2">
              {featuredObjects.map((_, index) => (
                <button
                  key={index}
                  onClick={() => setCurrentSlide(index)}
                  className={`h-2 rounded-full transition-all ${
                    index === currentSlide 
                      ? 'w-8 bg-purple-600' 
                      : 'w-2 bg-gray-300 hover:bg-gray-400'
                  }`}
                />
              ))}
            </div>
          </div>

          {/* Right side - Object info */}
          <div>
            <h1 className="text-4xl lg:text-5xl font-display font-bold text-gray-900 mb-4">
              Vendez vos objets de valeur en toute{' '}
              <span className="text-gradient-purple">confiance</span>
            </h1>
            <p className="text-xl text-gray-600 mb-8">
              La plateforme qui connecte vendeurs particuliers et professionnels du marché de l'art
            </p>

            {/* Featured object card */}
            <div className="bg-white rounded-xl p-6 shadow-lg mb-8">
              <p className="text-sm text-gray-500 mb-2">{current.category}</p>
              <h3 className="text-2xl font-display font-semibold text-gray-900 mb-2">
                {current.name}
              </h3>
              <p className="text-gray-600 mb-4">{current.description}</p>
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-500">Vendu pour</p>
                  <p className="text-3xl font-bold text-purple-600">{current.finalPrice}</p>
                </div>
                <Link to={`/products/${current.id}`}>
                  <Button variant="outline">
                    Voir l'objet
                  </Button>
                </Link>
              </div>
            </div>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row gap-4">
              <Link to="/register" className="flex-1">
                <Button variant="primary" className="w-full" size="large">
                  Vendre un objet
                </Button>
              </Link>
              <Link to="/search" className="flex-1">
                <Button variant="secondary" className="w-full" size="large">
                  Acheter
                </Button>
              </Link>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-3 gap-4 mt-8 pt-8 border-t border-gray-200">
              <div className="text-center">
                <p className="text-3xl font-bold text-purple-600">15K+</p>
                <p className="text-sm text-gray-600">Objets vendus</p>
              </div>
              <div className="text-center">
                <p className="text-3xl font-bold text-purple-600">2,5K+</p>
                <p className="text-sm text-gray-600">Professionnels</p>
              </div>
              <div className="text-center">
                <p className="text-3xl font-bold text-purple-600">98%</p>
                <p className="text-sm text-gray-600">Satisfaction</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Hero;

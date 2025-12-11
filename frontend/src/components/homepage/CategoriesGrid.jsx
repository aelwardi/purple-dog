import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../common/Card';
import { categoryService } from '../../services';

const CategoriesGrid = () => {

  const [activeCategories, setActiveCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchActiveCategories = async () => {
      try {
        setLoading(true);
        const response = await categoryService.getActive();
        console.log('📂 Active Categories Response:', response);

        // Vérifier si la réponse est directement un tableau ou si les données sont dans response.data
        const categories = Array.isArray(response) ? response : response.data || [];
        console.log('📊 Categories array:', categories);

        if (categories.length > 0) {
          console.log('📦 First category sample:', categories[0]);
          console.log('🔢 First category count field:', categories[0]?.count);
          console.log('🔢 First category productCount field:', categories[0]?.productCount);
        }

        setActiveCategories(categories);
      } catch (err) {
        console.error('❌ Error fetching categories:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchActiveCategories();
  }, []);


  if (loading) {
    return (
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Chargement des catégories...</p>
          </div>
        </div>
      </section>
    );
  }

  // const categories = [
  //   {
  //     id: 1,
  //     name: 'Bijoux & montres',
  //     icon: '💎',
  //     count: 1250,
  //     image: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=400'
  //   },
  //   {
  //     id: 2,
  //     name: 'Meubles anciens',
  //     icon: '🪑',
  //     count: 850,
  //     image: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400'
  //   },
  //   {
  //     id: 3,
  //     name: 'Objets d\'art & tableaux',
  //     icon: '🎨',
  //     count: 2100,
  //     image: 'https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?w=400'
  //   },
  //   {
  //     id: 4,
  //     name: 'Objets de collection',
  //     icon: '🏺',
  //     count: 670,
  //     image: 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=400'
  //   },
  //   {
  //     id: 5,
  //     name: 'Vins & spiritueux',
  //     icon: '🍷',
  //     count: 430,
  //     image: 'https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=400'
  //   },
  //   {
  //     id: 6,
  //     name: 'Instruments de musique',
  //     icon: '🎸',
  //     count: 320,
  //     image: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400'
  //   },
  //   {
  //     id: 7,
  //     name: 'Livres anciens',
  //     icon: '📚',
  //     count: 540,
  //     image: 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=400'
  //   },
  //   {
  //     id: 8,
  //     name: 'Mode & accessoires',
  //     icon: '👜',
  //     count: 980,
  //     image: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400'
  //   },
  //   {
  //     id: 9,
  //     name: 'Horlogerie',
  //     icon: '⏰',
  //     count: 410,
  //     image: 'https://images.unsplash.com/photo-1509048191080-d2984bad6ae5?w=400'
  //   },
  //   {
  //     id: 10,
  //     name: 'Photographies',
  //     icon: '📷',
  //     count: 290,
  //     image: 'https://images.unsplash.com/photo-1452780212940-6f5c0d14d848?w=400'
  //   },
  //   {
  //     id: 11,
  //     name: 'Vaisselle & argenterie',
  //     icon: '🍽️',
  //     count: 560,
  //     image: 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400'
  //   },
  //   {
  //     id: 12,
  //     name: 'Sculptures',
  //     icon: '🗿',
  //     count: 380,
  //     image: 'https://images.unsplash.com/photo-1513519245088-0e3a94918c58?w=400'
  //   }
  // ];

  return (
    <section className="py-16 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl lg:text-4xl font-display font-bold text-gray-900 mb-4">
            Explorer par catégorie
          </h2>
          <p className="text-xl text-gray-600">
            Découvrez nos objets d'exception classés par univers
          </p>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {Array.isArray(activeCategories) && activeCategories.map((category) => (
            <Link
              key={category.id}
              to={`/category/${category.id}`}
              className="group"
            >
              <Card padding="none" className="overflow-hidden">
                <div className="relative h-40 overflow-hidden">
                  <img
                    src={category?.iconUrl || category?.image}
                    alt={category?.name}
                    className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
                </div>
                <div className="p-4">
                  <h3 className="font-semibold text-gray-900 mb-1 group-hover:text-purple-600 transition-colors">
                    {category.name}
                  </h3>
                  {(category?.count > 0 || category?.productCount > 0) && (
                    <p className="text-sm text-gray-500">
                      {category?.productCount || category?.count || 0} objet{(category?.productCount || category?.count || 0) > 1 ? 's' : ''}
                    </p>
                  )}
                </div>
              </Card>
            </Link>
          ))}
        </div>

        <div className="text-center mt-12">
          <Link to="/search">
            <button className="btn-outline">
              Voir toutes les catégories
            </button>
          </Link>
        </div>
      </div>
    </section>
  );
};

export default CategoriesGrid;

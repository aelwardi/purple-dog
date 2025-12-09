import React from 'react';
import Hero from '../components/homepage/Hero';
import CategoriesGrid from '../components/homepage/CategoriesGrid';
import AboutSection from '../components/homepage/AboutSection';
import Newsletter from '../components/homepage/Newsletter';

const HomePage = () => {
  return (
    <div>
      <Hero />
      <CategoriesGrid />
      <AboutSection />
      <Newsletter />
    </div>
  );
};

export default HomePage;

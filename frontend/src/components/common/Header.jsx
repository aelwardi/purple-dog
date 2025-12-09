import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Bars3Icon, XMarkIcon } from '@heroicons/react/24/outline';
import Logo from './Logo';
import Button from './Button';
import RegisterTypeModal from '../auth/RegisterTypeModal';

const Header = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [registerModalOpen, setRegisterModalOpen] = useState(false);

  return (
    <header className="bg-white shadow-sm sticky top-0 z-40">
      <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-20">
          {/* Logo */}
          <Link to="/" className="flex-shrink-0">
            <Logo size="normal" />
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            <Link 
              to="/search" 
              className="text-gray-700 hover:text-purple-600 font-medium transition-colors"
            >
              Explorer
            </Link>
            <Link 
              to="/auctions" 
              className="text-gray-700 hover:text-purple-600 font-medium transition-colors"
            >
              Enchères
            </Link>
            <Link 
              to="/about" 
              className="text-gray-700 hover:text-purple-600 font-medium transition-colors"
            >
              À propos
            </Link>
            <Link 
              to="/contact" 
              className="text-gray-700 hover:text-purple-600 font-medium transition-colors"
            >
              Contact
            </Link>
          </div>

          {/* Auth Buttons */}
          <div className="hidden md:flex items-center space-x-4">
            <Link to="/login">
              <Button variant="outline" size="small">
                Se connecter
              </Button>
            </Link>
            <Button 
              variant="primary" 
              size="small"
              onClick={() => setRegisterModalOpen(true)}
            >
              S'inscrire
            </Button>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="text-gray-700 hover:text-purple-600"
            >
              {mobileMenuOpen ? (
                <XMarkIcon className="h-6 w-6" />
              ) : (
                <Bars3Icon className="h-6 w-6" />
              )}
            </button>
          </div>
        </div>

        {/* Mobile menu */}
        {mobileMenuOpen && (
          <div className="md:hidden py-4 border-t border-gray-200">
            <div className="flex flex-col space-y-4">
              <Link 
                to="/search" 
                className="text-gray-700 hover:text-purple-600 font-medium"
                onClick={() => setMobileMenuOpen(false)}
              >
                Explorer
              </Link>
              <Link 
                to="/auctions" 
                className="text-gray-700 hover:text-purple-600 font-medium"
                onClick={() => setMobileMenuOpen(false)}
              >
                Enchères
              </Link>
              <Link 
                to="/about" 
                className="text-gray-700 hover:text-purple-600 font-medium"
                onClick={() => setMobileMenuOpen(false)}
              >
                À propos
              </Link>
              <Link 
                to="/contact" 
                className="text-gray-700 hover:text-purple-600 font-medium"
                onClick={() => setMobileMenuOpen(false)}
              >
                Contact
              </Link>
              <div className="flex flex-col space-y-2 pt-4 border-t border-gray-200">
                <Link to="/login" onClick={() => setMobileMenuOpen(false)}>
                  <Button variant="outline" size="small" className="w-full">
                    Se connecter
                  </Button>
                </Link>
                <Button 
                  variant="primary" 
                  size="small" 
                  className="w-full"
                  onClick={() => {
                    setRegisterModalOpen(true);
                    setMobileMenuOpen(false);
                  }}
                >
                  S'inscrire
                </Button>
              </div>
            </div>
          </div>
        )}
      </nav>
      
      {/* Register Type Modal */}
      <RegisterTypeModal 
        isOpen={registerModalOpen} 
        onClose={() => setRegisterModalOpen(false)} 
      />
    </header>
  );
};

export default Header;

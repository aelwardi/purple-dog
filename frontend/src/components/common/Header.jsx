import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Bars3Icon, XMarkIcon, UserCircleIcon, ArrowRightOnRectangleIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../../hooks/useAuth';
import { useErrorHandler } from '../../hooks/useErrorHandler';
import Logo from './Logo';
import Button from './Button';
import LoginModal from '../auth/LoginModal';
import RegisterModal from '../auth/RegisterModal';
import SearchBar from '../search/SearchBar';

const Header = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();
  const { showSuccess, handleError } = useErrorHandler();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [loginModalOpen, setLoginModalOpen] = useState(false);
  const [registerModalOpen, setRegisterModalOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const userMenuRef = useRef(null);

  // Close user menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setUserMenuOpen(false);
      }
    };

    if (userMenuOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [userMenuOpen]);

  const handleLogout = async () => {
    try {
      await logout();
      showSuccess('Déconnexion réussie');
      navigate('/');
    } catch (error) {
      handleError(error);
    }
  };

  return (
    <header className="bg-white shadow-sm sticky top-0 z-40">
      <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-20 gap-4">
          {/* Logo */}
          <Link to="/" className="flex-shrink-0">
            <Logo size="normal" />
          </Link>

          {/* Search Bar - Desktop */}
          <div className="hidden md:flex flex-1 max-w-2xl">
            <SearchBar />
          </div>

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

          {/* Auth Buttons / User Menu */}
          <div className="hidden md:flex items-center space-x-4">
            {isAuthenticated ? (
              <div className="relative" ref={userMenuRef}>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setUserMenuOpen(!userMenuOpen);
                  }}
                  className="flex items-center space-x-2 px-3 py-2 rounded-lg text-gray-700 hover:text-purple-600 hover:bg-purple-50 transition-all cursor-pointer"
                >
                  <div className="w-8 h-8 bg-gradient-to-br from-purple-500 to-purple-700 rounded-full flex items-center justify-center text-white font-semibold text-sm">
                    {user?.firstName?.[0]}{user?.lastName?.[0]}
                  </div>
                  <span className="font-medium">
                    {user?.firstName || 'Mon compte'}
                  </span>
                  <svg
                    className={`w-4 h-4 transition-transform ${userMenuOpen ? 'rotate-180' : ''}`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>

                {/* User Dropdown Menu */}
                {userMenuOpen && (
                  <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-2xl py-2 z-50 border border-gray-100 animate-fadeIn">
                    <div className="px-4 py-3 border-b border-gray-100">
                      <p className="text-sm font-semibold text-gray-900">{user?.firstName} {user?.lastName}</p>
                      <p className="text-xs text-gray-500 mt-1">{user?.email}</p>
                    </div>
                    <Link
                      to="/dashboard"
                      className="block px-4 py-2 text-gray-700 hover:bg-purple-50 hover:text-purple-600 transition-colors"
                      onClick={() => setUserMenuOpen(false)}
                    >
                      📊 Mon Dashboard
                    </Link>
                    <Link
                      to="/profile"
                      className="block px-4 py-2 text-gray-700 hover:bg-purple-50 hover:text-purple-600 transition-colors"
                      onClick={() => setUserMenuOpen(false)}
                    >
                      👤 Mon Profil
                    </Link>
                    <hr className="my-2" />
                    <button
                      onClick={() => {
                        setUserMenuOpen(false);
                        handleLogout();
                      }}
                      className="w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 flex items-center space-x-2 transition-colors"
                    >
                      <ArrowRightOnRectangleIcon className="h-5 w-5" />
                      <span>Se déconnecter</span>
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <>
                <Button
                  variant="outline"
                  size="small"
                  onClick={() => setLoginModalOpen(true)}
                >
                  Se connecter
                </Button>
                <Button
                  variant="primary"
                  size="small"
                  onClick={() => setRegisterModalOpen(true)}
                >
                  S'inscrire
                </Button>
              </>
            )}
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
            {/* Search Bar - Mobile */}
            <div className="mb-4">
              <SearchBar />
            </div>
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
                {isAuthenticated ? (
                  <>
                    <div className="flex items-center space-x-2 px-4 py-2 text-gray-700">
                      <UserCircleIcon className="h-8 w-8" />
                      <span className="font-medium">
                        {user?.firstName} {user?.lastName}
                      </span>
                    </div>
                    <Link
                      to="/dashboard"
                      onClick={() => setMobileMenuOpen(false)}
                      className="block px-4 py-2 text-gray-700 hover:bg-purple-50 hover:text-purple-600 rounded"
                    >
                      Mon Dashboard
                    </Link>
                    <Link
                      to="/profile"
                      onClick={() => setMobileMenuOpen(false)}
                      className="block px-4 py-2 text-gray-700 hover:bg-purple-50 hover:text-purple-600 rounded"
                    >
                      Mon Profil
                    </Link>
                    <button
                      onClick={() => {
                        setMobileMenuOpen(false);
                        handleLogout();
                      }}
                      className="w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 flex items-center space-x-2 rounded"
                    >
                      <ArrowRightOnRectangleIcon className="h-5 w-5" />
                      <span>Se déconnecter</span>
                    </button>
                  </>
                ) : (
                  <>
                    <Button
                      variant="outline"
                      size="small"
                      className="w-full"
                      onClick={() => {
                        setLoginModalOpen(true);
                        setMobileMenuOpen(false);
                      }}
                    >
                      Se connecter
                    </Button>
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
                  </>
                )}
              </div>
            </div>
          </div>
        )}
      </nav>
      
      {/* Login Modal */}
      <LoginModal
        isOpen={loginModalOpen}
        onClose={() => setLoginModalOpen(false)}
        onSwitchToRegister={() => {
          setLoginModalOpen(false);
          setRegisterModalOpen(true);
        }}
      />

      {/* Register Modal */}
      <RegisterModal
        isOpen={registerModalOpen}
        onClose={() => setRegisterModalOpen(false)}
        onSwitchToLogin={() => {
          setRegisterModalOpen(false);
          setLoginModalOpen(true);
        }}
      />
    </header>
  );
};

export default Header;

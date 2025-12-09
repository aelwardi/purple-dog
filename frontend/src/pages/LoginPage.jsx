import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import toast from 'react-hot-toast';

const LoginPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Simple authentication logic
    const { email, password } = formData;
    
    if (email === 'particulier@gmail.com' && password === '1234') {
      toast.success('Connexion réussie !');
      // Store user type in localStorage for dashboard access
      localStorage.setItem('userType', 'individual');
      localStorage.setItem('userEmail', email);
      navigate('/dashboard?type=individual');
    } else if (email === 'professionnel@gmail.com' && password === '1234') {
      toast.success('Connexion réussie !');
      localStorage.setItem('userType', 'professional');
      localStorage.setItem('userEmail', email);
      navigate('/dashboard?type=professional');
    } else {
      toast.error('Email ou mot de passe incorrect');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-display font-bold text-gray-900">
            Connexion
          </h2>
          <p className="mt-2 text-gray-600">
            Connectez-vous à votre compte Purple Dog
          </p>
        </div>

        <Card>
          <form onSubmit={handleSubmit} className="space-y-6">
            <Input
              label="Email"
              type="email"
              required
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              placeholder="votre@email.com"
            />

            <Input
              label="Mot de passe"
              type="password"
              required
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              placeholder="••••••••"
            />

            <div className="flex items-center justify-between">
              <label className="flex items-center">
                <input type="checkbox" className="rounded border-gray-300 text-purple-600 focus:ring-purple-500" />
                <span className="ml-2 text-sm text-gray-600">Se souvenir de moi</span>
              </label>
              <Link to="/forgot-password" className="text-sm text-purple-600 hover:text-purple-700">
                Mot de passe oublié ?
              </Link>
            </div>

            <Button type="submit" variant="primary" className="w-full">
              Se connecter
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Pas encore de compte ?{' '}
              <Link to="/register" className="font-medium text-purple-600 hover:text-purple-700">
                S'inscrire
              </Link>
            </p>
          </div>

          <div className="mt-4 p-4 bg-purple-50 rounded-lg">
            <p className="text-xs text-purple-900 font-semibold mb-2">Comptes de test :</p>
            <p className="text-xs text-purple-700">Particulier: particulier@gmail.com / 1234</p>
            <p className="text-xs text-purple-700">Professionnel: professionnel@gmail.com / 1234</p>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default LoginPage;

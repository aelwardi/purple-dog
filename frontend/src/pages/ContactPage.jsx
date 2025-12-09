import React, { useState } from 'react';
import { EnvelopeIcon, PhoneIcon, MapPinIcon, ClockIcon } from '@heroicons/react/24/outline';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import toast from 'react-hot-toast';

const ContactPage = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    subject: '',
    message: ''
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // Clear error for this field
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) newErrors.name = 'Le nom est requis';
    if (!formData.email.trim()) newErrors.email = 'L\'email est requis';
    else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = 'Email invalide';
    if (!formData.subject.trim()) newErrors.subject = 'Le sujet est requis';
    if (!formData.message.trim()) newErrors.message = 'Le message est requis';
    else if (formData.message.length < 10) newErrors.message = 'Le message doit contenir au moins 10 caractères';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      // TODO: Implement contact form submission
      toast.success('Message envoyé avec succès ! Nous vous répondrons dans les plus brefs délais.');
      setFormData({ name: '', email: '', subject: '', message: '' });
    }
  };

  const contactInfo = [
    {
      icon: <EnvelopeIcon className="w-6 h-6" />,
      title: 'Email',
      content: 'contact@purpledog.fr',
      link: 'mailto:contact@purpledog.fr'
    },
    {
      icon: <PhoneIcon className="w-6 h-6" />,
      title: 'Téléphone',
      content: '+33 1 23 45 67 89',
      link: 'tel:+33123456789'
    },
    {
      icon: <MapPinIcon className="w-6 h-6" />,
      title: 'Adresse',
      content: '123 Rue de la Paix, 75000 Paris, France',
      link: null
    },
    {
      icon: <ClockIcon className="w-6 h-6" />,
      title: 'Horaires',
      content: 'Lun - Ven: 9h00 - 18h00',
      link: null
    }
  ];

  const faqs = [
    {
      question: 'Comment puis-je vendre un objet ?',
      answer: 'Inscrivez-vous en tant que particulier ou professionnel, puis déposez votre objet avec photos et description. Vous pouvez choisir entre enchères ou vente rapide.'
    },
    {
      question: 'Comment sont vérifiés les professionnels ?',
      answer: 'Tous les professionnels doivent fournir leur KBIS, numéro SIRET et sont vérifiés avant validation de leur compte.'
    },
    {
      question: 'Quels sont les frais ?',
      answer: 'Pour les particuliers: gratuit. Pour les professionnels: 1 mois gratuit puis 49€/mois. Commission de 2% vendeur et 3% acheteur sur chaque transaction.'
    },
    {
      question: 'Comment se passe la livraison ?',
      answer: 'Nous travaillons avec des transporteurs spécialisés. Le vendeur emballe l\'objet et le transporteur se charge de la récupération et livraison.'
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-purple-600 to-purple-800 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 className="text-4xl md:text-5xl font-display font-bold mb-6">
            Contactez-nous
          </h1>
          <p className="text-xl max-w-3xl mx-auto opacity-90">
            Une question ? Un problème ? Notre équipe est là pour vous aider
          </p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid lg:grid-cols-3 gap-12">
          {/* Contact Form */}
          <div className="lg:col-span-2">
            <Card className="p-8">
              <h2 className="text-2xl font-display font-bold text-gray-900 mb-6">
                Envoyez-nous un message
              </h2>
              
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid md:grid-cols-2 gap-4">
                  <Input
                    label="Nom complet"
                    name="name"
                    type="text"
                    required
                    value={formData.name}
                    onChange={handleChange}
                    error={errors.name}
                    placeholder="Jean Dupont"
                  />
                  <Input
                    label="Email"
                    name="email"
                    type="email"
                    required
                    value={formData.email}
                    onChange={handleChange}
                    error={errors.email}
                    placeholder="jean.dupont@example.com"
                    icon={<EnvelopeIcon className="h-5 w-5 text-gray-400" />}
                  />
                </div>

                <Input
                  label="Sujet"
                  name="subject"
                  type="text"
                  required
                  value={formData.subject}
                  onChange={handleChange}
                  error={errors.subject}
                  placeholder="Comment puis-je..."
                />

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Message *
                  </label>
                  <textarea
                    name="message"
                    value={formData.message}
                    onChange={handleChange}
                    rows="6"
                    required
                    className="w-full px-4 py-3 text-gray-900 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                    placeholder="Décrivez votre demande en détail..."
                  />
                  {errors.message && (
                    <p className="mt-1 text-sm text-red-600">{errors.message}</p>
                  )}
                </div>

                <Button type="submit" variant="primary" size="large" className="w-full">
                  Envoyer le message
                </Button>
              </form>
            </Card>

            {/* FAQ Section */}
            <div className="mt-12">
              <h2 className="text-2xl font-display font-bold text-gray-900 mb-6">
                Questions fréquentes
              </h2>
              <div className="space-y-4">
                {faqs.map((faq, index) => (
                  <Card key={index} className="p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      {faq.question}
                    </h3>
                    <p className="text-gray-600">
                      {faq.answer}
                    </p>
                  </Card>
                ))}
              </div>
            </div>
          </div>

          {/* Contact Info Sidebar */}
          <div className="lg:col-span-1">
            <div className="sticky top-8 space-y-6">
              <Card className="p-6">
                <h3 className="text-xl font-display font-semibold text-gray-900 mb-6">
                  Informations de contact
                </h3>
                <div className="space-y-4">
                  {contactInfo.map((info, index) => (
                    <div key={index} className="flex items-start">
                      <div className="flex-shrink-0 w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center text-purple-600">
                        {info.icon}
                      </div>
                      <div className="ml-4">
                        <h4 className="text-sm font-medium text-gray-900">
                          {info.title}
                        </h4>
                        {info.link ? (
                          <a 
                            href={info.link} 
                            className="text-sm text-purple-600 hover:text-purple-700 transition-colors"
                          >
                            {info.content}
                          </a>
                        ) : (
                          <p className="text-sm text-gray-600">
                            {info.content}
                          </p>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </Card>

              <Card className="p-6 bg-purple-50 border-purple-200">
                <h3 className="text-lg font-semibold text-purple-900 mb-3">
                  Support prioritaire
                </h3>
                <p className="text-sm text-purple-700 mb-4">
                  Les professionnels abonnés bénéficient d'un support prioritaire avec réponse sous 2h.
                </p>
                <Button variant="primary" size="small" className="w-full">
                  Devenir professionnel
                </Button>
              </Card>

              <Card className="p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">
                  Suivez-nous
                </h3>
                <div className="flex gap-3">
                  <a 
                    href="#" 
                    className="w-10 h-10 bg-gray-100 hover:bg-purple-100 rounded-lg flex items-center justify-center text-gray-600 hover:text-purple-600 transition-colors"
                  >
                    <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                    </svg>
                  </a>
                  <a 
                    href="#" 
                    className="w-10 h-10 bg-gray-100 hover:bg-purple-100 rounded-lg flex items-center justify-center text-gray-600 hover:text-purple-600 transition-colors"
                  >
                    <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"/>
                    </svg>
                  </a>
                  <a 
                    href="#" 
                    className="w-10 h-10 bg-gray-100 hover:bg-purple-100 rounded-lg flex items-center justify-center text-gray-600 hover:text-purple-600 transition-colors"
                  >
                    <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M12 0C8.74 0 8.333.015 7.053.072 5.775.132 4.905.333 4.14.63c-.789.306-1.459.717-2.126 1.384S.935 3.35.63 4.14C.333 4.905.131 5.775.072 7.053.012 8.333 0 8.74 0 12s.015 3.667.072 4.947c.06 1.277.261 2.148.558 2.913.306.788.717 1.459 1.384 2.126.667.666 1.336 1.079 2.126 1.384.766.296 1.636.499 2.913.558C8.333 23.988 8.74 24 12 24s3.667-.015 4.947-.072c1.277-.06 2.148-.262 2.913-.558.788-.306 1.459-.718 2.126-1.384.666-.667 1.079-1.335 1.384-2.126.296-.765.499-1.636.558-2.913.06-1.28.072-1.687.072-4.947s-.015-3.667-.072-4.947c-.06-1.277-.262-2.149-.558-2.913-.306-.789-.718-1.459-1.384-2.126C21.319 1.347 20.651.935 19.86.63c-.765-.297-1.636-.499-2.913-.558C15.667.012 15.26 0 12 0zm0 2.16c3.203 0 3.585.016 4.85.071 1.17.055 1.805.249 2.227.415.562.217.96.477 1.382.896.419.42.679.819.896 1.381.164.422.36 1.057.413 2.227.057 1.266.07 1.646.07 4.85s-.015 3.585-.074 4.85c-.061 1.17-.256 1.805-.421 2.227-.224.562-.479.96-.899 1.382-.419.419-.824.679-1.38.896-.42.164-1.065.36-2.235.413-1.274.057-1.649.07-4.859.07-3.211 0-3.586-.015-4.859-.074-1.171-.061-1.816-.256-2.236-.421-.569-.224-.96-.479-1.379-.899-.421-.419-.69-.824-.9-1.38-.165-.42-.359-1.065-.42-2.235-.045-1.26-.061-1.649-.061-4.844 0-3.196.016-3.586.061-4.861.061-1.17.255-1.814.42-2.234.21-.57.479-.96.9-1.381.419-.419.81-.689 1.379-.898.42-.166 1.051-.361 2.221-.421 1.275-.045 1.65-.06 4.859-.06l.045.03zm0 3.678c-3.405 0-6.162 2.76-6.162 6.162 0 3.405 2.76 6.162 6.162 6.162 3.405 0 6.162-2.76 6.162-6.162 0-3.405-2.76-6.162-6.162-6.162zM12 16c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4zm7.846-10.405c0 .795-.646 1.44-1.44 1.44-.795 0-1.44-.646-1.44-1.44 0-.794.646-1.439 1.44-1.439.793-.001 1.44.645 1.44 1.439z"/>
                    </svg>
                  </a>
                  <a 
                    href="#" 
                    className="w-10 h-10 bg-gray-100 hover:bg-purple-100 rounded-lg flex items-center justify-center text-gray-600 hover:text-purple-600 transition-colors"
                  >
                    <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                    </svg>
                  </a>
                </div>
              </Card>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ContactPage;

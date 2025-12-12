// Données mock pour les enchères - à remplacer par des appels API plus tard

// Helper pour générer des dates futures
const addDays = (days) => {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return date.toISOString();
};

const addHours = (hours) => {
  const date = new Date();
  date.setHours(date.getHours() + hours);
  return date.toISOString();
};

const subtractDays = (days) => {
  const date = new Date();
  date.setDate(date.getDate() - days);
  return date.toISOString();
};

export const mockAuctions = [
  {
    id: 1,
    status: 'ACTIVE',
    startingPrice: 2500.00,
    currentPrice: 3200.00,
    reservePrice: 3000.00,
    totalBids: 8,
    startDate: subtractDays(2),
    endDate: addDays(2),
    product: {
      id: 1,
      title: "Vase en porcelaine de Chine, Dynastie Qing",
      description: "Magnifique vase en porcelaine bleu et blanc datant de la dynastie Qing (1644-1912). Pièce rare avec décor floral traditionnel et marques d'authenticité au fond.",
      category: "Porcelaine",
      condition: "Excellent",
      photos: [
        { url: "https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=800&h=800&fit=crop", isPrimary: true },
        { url: "https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=800&h=800&fit=crop" }
      ]
    }
  },
  {
    id: 2,
    status: 'ACTIVE',
    startingPrice: 5000.00,
    currentPrice: 7500.00,
    reservePrice: 7000.00,
    totalBids: 15,
    startDate: subtractDays(3),
    endDate: addHours(18),
    product: {
      id: 2,
      title: "Tableau Impressionniste - Paysage de Provence",
      description: "Huile sur toile représentant un paysage provençal, style impressionniste, début XXe siècle. Signature à identifier. Dimensions: 80x60cm. Cadre d'époque doré.",
      category: "Peinture",
      condition: "Très bon",
      photos: [
        { url: "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 3,
    status: 'ACTIVE',
    startingPrice: 3500.00,
    currentPrice: 4800.00,
    reservePrice: 4500.00,
    totalBids: 12,
    startDate: subtractDays(1),
    endDate: addDays(3),
    product: {
      id: 3,
      title: "Sculpture en bronze Art Déco",
      description: "Danseuse en bronze signé, époque Art Déco 1920-1930. Patine brune d'origine. Base en marbre noir. Hauteur: 45cm. État exceptionnel.",
      category: "Sculpture",
      condition: "Excellent",
      photos: [
        { url: "https://images.unsplash.com/photo-1513519245088-0e12902e35ca?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 4,
    status: 'ACTIVE',
    startingPrice: 3200.00,
    currentPrice: 3200.00,
    reservePrice: 4000.00,
    totalBids: 0,
    startDate: new Date().toISOString(),
    endDate: addDays(5),
    product: {
      id: 4,
      title: "Horloge Comtoise XVIIIe siècle",
      description: "Horloge comtoise en chêne massif, mécanisme d'origine, sonnerie au passage. Cadran émaillé avec décor floral. Balancier et poids d'origine. Hauteur: 240cm.",
      category: "Horlogerie",
      condition: "Bon",
      photos: [
        { url: "https://images.unsplash.com/photo-1563861826100-9cb868fdbe1c?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 5,
    status: 'ACTIVE',
    startingPrice: 2000.00,
    currentPrice: 2650.00,
    reservePrice: 2500.00,
    totalBids: 5,
    startDate: subtractDays(1),
    endDate: addDays(4),
    product: {
      id: 5,
      title: "Service à thé en argent massif",
      description: "Service à thé complet en argent massif poinçonné, style Napoléon III. Comprend: théière, sucrier, crémier et plateau. Poids total: 2.8kg.",
      category: "Argenterie",
      condition: "Très bon",
      photos: [
        { url: "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 6,
    status: 'ACTIVE',
    startingPrice: 2800.00,
    currentPrice: 3100.00,
    reservePrice: 3000.00,
    totalBids: 4,
    startDate: subtractDays(2),
    endDate: addHours(36),
    product: {
      id: 6,
      title: "Miroir vénitien ancien",
      description: "Grand miroir vénitien en verre de Murano avec cadre ouvragé, XIXe siècle. Décor floral gravé à l'acide. Dimensions: 120x80cm. Quelques altérations du tain.",
      category: "Mobilier",
      condition: "Bon",
      photos: [
        { url: "https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 7,
    status: 'ACTIVE',
    startingPrice: 4500.00,
    currentPrice: 5900.00,
    reservePrice: 5500.00,
    totalBids: 9,
    startDate: subtractDays(3),
    endDate: addHours(12),
    product: {
      id: 7,
      title: "Tapis Persan Tabriz",
      description: "Tapis persan noué main, région de Tabriz, laine et soie, circa 1950. Décor central médaillon avec écoinçons floraux. Dimensions: 3x2m. Excellent état.",
      category: "Textile",
      condition: "Excellent",
      photos: [
        { url: "https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 8,
    status: 'ACTIVE',
    startingPrice: 5500.00,
    currentPrice: 6800.00,
    reservePrice: 6500.00,
    totalBids: 7,
    startDate: subtractDays(2),
    endDate: addDays(1),
    product: {
      id: 8,
      title: "Lustre en cristal de Baccarat",
      description: "Lustre à pampilles en cristal de Baccarat, 8 lumières, début XXe siècle. Structure en bronze doré. Nombreuses pampilles taillées. Hauteur: 90cm.",
      category: "Luminaire",
      condition: "Excellent",
      photos: [
        { url: "https://images.unsplash.com/photo-1565373679574-2b9817ee4e53?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 9,
    status: 'CLOSED',
    startingPrice: 3000.00,
    currentPrice: 4200.00,
    reservePrice: 3500.00,
    totalBids: 11,
    startDate: subtractDays(10),
    endDate: subtractDays(3),
    product: {
      id: 9,
      title: "Statuette en ivoire, Japon Meiji",
      description: "Okimono en ivoire sculpté représentant un sage, période Meiji (1868-1912). Certificat CITES inclus. Hauteur: 18cm. Sculpture finement détaillée.",
      category: "Art Asiatique",
      condition: "Très bon",
      photos: [
        { url: "https://images.unsplash.com/photo-1582126147936-a79f4b27ed2c?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 10,
    status: 'CLOSED',
    startingPrice: 4000.00,
    currentPrice: 5800.00,
    reservePrice: 5000.00,
    totalBids: 14,
    startDate: subtractDays(15),
    endDate: subtractDays(8),
    product: {
      id: 10,
      title: "Fauteuil Louis XV",
      description: "Fauteuil à la reine, époque Louis XV, bois doré et tapisserie d'Aubusson. Restauration professionnelle récente. Assise confortable. Dimensions: 95x70x60cm.",
      category: "Mobilier",
      condition: "Restauré",
      photos: [
        { url: "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 11,
    status: 'ACTIVE',
    startingPrice: 800.00,
    currentPrice: 1150.00,
    reservePrice: 1000.00,
    totalBids: 6,
    startDate: subtractDays(1),
    endDate: addDays(6),
    product: {
      id: 11,
      title: "Coffret à bijoux en marqueterie",
      description: "Coffret Napoléon III en marqueterie avec incrustations de nacre. Intérieur capitonné en velours rouge. Serrure et clé d'origine. Dimensions: 30x20x15cm.",
      category: "Objets d'art",
      condition: "Bon",
      photos: [
        { url: "https://images.unsplash.com/photo-1611085583191-a3b181a88401?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  },
  {
    id: 12,
    status: 'ACTIVE',
    startingPrice: 2500.00,
    currentPrice: 3500.00,
    reservePrice: 3200.00,
    totalBids: 10,
    startDate: subtractDays(4),
    endDate: addHours(8),
    product: {
      id: 12,
      title: "Montre de gousset en or",
      description: "Montre de gousset en or 18 carats avec chaîne, fin XIXe siècle. Mouvement mécanique. Cadran émaillé blanc avec chiffres romains. Fonctionne parfaitement.",
      category: "Horlogerie",
      condition: "Excellent",
      photos: [
        { url: "https://images.unsplash.com/photo-1509941943102-10c232535736?w=800&h=800&fit=crop", isPrimary: true }
      ]
    }
  }
];

// Fonctions utilitaires
export const getActiveAuctions = () => {
  return mockAuctions.filter(auction => auction.status === 'ACTIVE');
};

export const getClosedAuctions = () => {
  return mockAuctions.filter(auction => auction.status === 'CLOSED');
};

export const getAuctionById = (id) => {
  return mockAuctions.find(auction => auction.id === parseInt(id));
};

export default mockAuctions;

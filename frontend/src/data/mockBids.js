// Données mock pour les enchères (bids) - à remplacer par des appels API plus tard

// Helper pour générer des dates
const subtractHours = (hours) => {
  const date = new Date();
  date.setHours(date.getHours() - hours);
  return date.toISOString();
};

const subtractMinutes = (minutes) => {
  const date = new Date();
  date.setMinutes(date.getMinutes() - minutes);
  return date.toISOString();
};

const subtractDays = (days) => {
  const date = new Date();
  date.setDate(date.getDate() - days);
  return date.toISOString();
};

// Mock bids pour chaque enchère
export const mockBids = [
  // Bids pour Auction 1 (Vase en porcelaine)
  {
    id: 1,
    auctionId: 1,
    bidderId: 101,
    bidderDisplayName: "Antiquités Martin",
    amount: 2500.00,
    bidDate: subtractDays(2),
  },
  {
    id: 2,
    auctionId: 1,
    bidderId: 102,
    bidderDisplayName: "Galerie Durand",
    amount: 2650.00,
    bidDate: subtractHours(45),
  },
  {
    id: 3,
    auctionId: 1,
    bidderId: 103,
    bidderDisplayName: "CollectionPro",
    amount: 2800.00,
    bidDate: subtractHours(42),
  },
  {
    id: 4,
    auctionId: 1,
    bidderId: 101,
    bidderDisplayName: "Antiquités Martin",
    amount: 2900.00,
    bidDate: subtractHours(38),
  },
  {
    id: 5,
    auctionId: 1,
    bidderId: 104,
    bidderDisplayName: "Art & Histoire",
    amount: 3000.00,
    bidDate: subtractHours(24),
  },
  {
    id: 6,
    auctionId: 1,
    bidderId: 102,
    bidderDisplayName: "Galerie Durand",
    amount: 3050.00,
    bidDate: subtractHours(18),
  },
  {
    id: 7,
    auctionId: 1,
    bidderId: 103,
    bidderDisplayName: "CollectionPro",
    amount: 3100.00,
    bidDate: subtractHours(12),
  },
  {
    id: 8,
    auctionId: 1,
    bidderId: 101,
    bidderDisplayName: "Antiquités Martin",
    amount: 3200.00,
    bidDate: subtractHours(6),
  },

  // Bids pour Auction 2 (Tableau Impressionniste)
  {
    id: 9,
    auctionId: 2,
    bidderId: 105,
    bidderDisplayName: "Atelier des Beaux Arts",
    amount: 5000.00,
    bidDate: subtractDays(3),
  },
  {
    id: 10,
    auctionId: 2,
    bidderId: 106,
    bidderDisplayName: "Maison Leblanc",
    amount: 5300.00,
    bidDate: subtractHours(70),
  },
  {
    id: 11,
    auctionId: 2,
    bidderId: 107,
    bidderDisplayName: "Art Premier",
    amount: 5600.00,
    bidDate: subtractHours(68),
  },
  {
    id: 12,
    auctionId: 2,
    bidderId: 105,
    bidderDisplayName: "Atelier des Beaux Arts",
    amount: 5900.00,
    bidDate: subtractHours(64),
  },
  {
    id: 13,
    auctionId: 2,
    bidderId: 108,
    bidderDisplayName: "Galerie Moderne",
    amount: 6200.00,
    bidDate: subtractHours(60),
  },
  {
    id: 14,
    auctionId: 2,
    bidderId: 106,
    bidderDisplayName: "Maison Leblanc",
    amount: 6500.00,
    bidDate: subtractHours(48),
  },
  {
    id: 15,
    auctionId: 2,
    bidderId: 107,
    bidderDisplayName: "Art Premier",
    amount: 6800.00,
    bidDate: subtractHours(36),
  },
  {
    id: 16,
    auctionId: 2,
    bidderId: 105,
    bidderDisplayName: "Atelier des Beaux Arts",
    amount: 7000.00,
    bidDate: subtractHours(24),
  },
  {
    id: 17,
    auctionId: 2,
    bidderId: 108,
    bidderDisplayName: "Galerie Moderne",
    amount: 7200.00,
    bidDate: subtractHours(18),
  },
  {
    id: 18,
    auctionId: 2,
    bidderId: 106,
    bidderDisplayName: "Maison Leblanc",
    amount: 7350.00,
    bidDate: subtractHours(12),
  },
  {
    id: 19,
    auctionId: 2,
    bidderId: 105,
    bidderDisplayName: "Atelier des Beaux Arts",
    amount: 7500.00,
    bidDate: subtractHours(6),
  },

  // Bids pour Auction 3 (Sculpture en bronze)
  {
    id: 20,
    auctionId: 3,
    bidderId: 109,
    bidderDisplayName: "Bronze & Cie",
    amount: 3500.00,
    bidDate: subtractDays(1),
  },
  {
    id: 21,
    auctionId: 3,
    bidderId: 110,
    bidderDisplayName: "Sculptures d'Art",
    amount: 3650.00,
    bidDate: subtractHours(22),
  },
  {
    id: 22,
    auctionId: 3,
    bidderId: 111,
    bidderDisplayName: "Décor Vintage",
    amount: 3800.00,
    bidDate: subtractHours(20),
  },
  {
    id: 23,
    auctionId: 3,
    bidderId: 109,
    bidderDisplayName: "Bronze & Cie",
    amount: 3950.00,
    bidDate: subtractHours(18),
  },
  {
    id: 24,
    auctionId: 3,
    bidderId: 112,
    bidderDisplayName: "Collection Privée",
    amount: 4100.00,
    bidDate: subtractHours(16),
  },
  {
    id: 25,
    auctionId: 3,
    bidderId: 110,
    bidderDisplayName: "Sculptures d'Art",
    amount: 4250.00,
    bidDate: subtractHours(14),
  },
  {
    id: 26,
    auctionId: 3,
    bidderId: 111,
    bidderDisplayName: "Décor Vintage",
    amount: 4400.00,
    bidDate: subtractHours(10),
  },
  {
    id: 27,
    auctionId: 3,
    bidderId: 109,
    bidderDisplayName: "Bronze & Cie",
    amount: 4550.00,
    bidDate: subtractHours(8),
  },
  {
    id: 28,
    auctionId: 3,
    bidderId: 112,
    bidderDisplayName: "Collection Privée",
    amount: 4650.00,
    bidDate: subtractHours(6),
  },
  {
    id: 29,
    auctionId: 3,
    bidderId: 110,
    bidderDisplayName: "Sculptures d'Art",
    amount: 4750.00,
    bidDate: subtractHours(4),
  },
  {
    id: 30,
    auctionId: 3,
    bidderId: 109,
    bidderDisplayName: "Bronze & Cie",
    amount: 4800.00,
    bidDate: subtractHours(2),
  },

  // Auction 4 (Horloge Comtoise) - Pas d'enchères (totalBids: 0)

  // Bids pour Auction 5 (Service à thé)
  {
    id: 31,
    auctionId: 5,
    bidderId: 113,
    bidderDisplayName: "Argenterie Royale",
    amount: 2000.00,
    bidDate: subtractDays(1),
  },
  {
    id: 32,
    auctionId: 5,
    bidderId: 114,
    bidderDisplayName: "Maison Précieuse",
    amount: 2150.00,
    bidDate: subtractHours(20),
  },
  {
    id: 33,
    auctionId: 5,
    bidderId: 115,
    bidderDisplayName: "Trésors d'Argent",
    amount: 2350.00,
    bidDate: subtractHours(16),
  },
  {
    id: 34,
    auctionId: 5,
    bidderId: 113,
    bidderDisplayName: "Argenterie Royale",
    amount: 2500.00,
    bidDate: subtractHours(12),
  },
  {
    id: 35,
    auctionId: 5,
    bidderId: 114,
    bidderDisplayName: "Maison Précieuse",
    amount: 2650.00,
    bidDate: subtractHours(8),
  },

  // Bids pour Auction 6 (Miroir vénitien)
  {
    id: 36,
    auctionId: 6,
    bidderId: 116,
    bidderDisplayName: "Miroirs Anciens",
    amount: 2800.00,
    bidDate: subtractDays(2),
  },
  {
    id: 37,
    auctionId: 6,
    bidderId: 117,
    bidderDisplayName: "Déco Classique",
    amount: 2900.00,
    bidDate: subtractHours(40),
  },
  {
    id: 38,
    auctionId: 6,
    bidderId: 118,
    bidderDisplayName: "Reflets d'Art",
    amount: 3000.00,
    bidDate: subtractHours(32),
  },
  {
    id: 39,
    auctionId: 6,
    bidderId: 116,
    bidderDisplayName: "Miroirs Anciens",
    amount: 3100.00,
    bidDate: subtractHours(24),
  },

  // Bids pour Auction 7 (Tapis Persan)
  {
    id: 40,
    auctionId: 7,
    bidderId: 119,
    bidderDisplayName: "Orient Express",
    amount: 4500.00,
    bidDate: subtractDays(3),
  },
  {
    id: 41,
    auctionId: 7,
    bidderId: 120,
    bidderDisplayName: "Tapis d'Orient",
    amount: 4700.00,
    bidDate: subtractHours(68),
  },
  {
    id: 42,
    auctionId: 7,
    bidderId: 121,
    bidderDisplayName: "Tissus Précieux",
    amount: 4900.00,
    bidDate: subtractHours(60),
  },
  {
    id: 43,
    auctionId: 7,
    bidderId: 119,
    bidderDisplayName: "Orient Express",
    amount: 5100.00,
    bidDate: subtractHours(52),
  },
  {
    id: 44,
    auctionId: 7,
    bidderId: 122,
    bidderDisplayName: "Collection Textile",
    amount: 5300.00,
    bidDate: subtractHours(44),
  },
  {
    id: 45,
    auctionId: 7,
    bidderId: 120,
    bidderDisplayName: "Tapis d'Orient",
    amount: 5450.00,
    bidDate: subtractHours(36),
  },
  {
    id: 46,
    auctionId: 7,
    bidderId: 121,
    bidderDisplayName: "Tissus Précieux",
    amount: 5600.00,
    bidDate: subtractHours(28),
  },
  {
    id: 47,
    auctionId: 7,
    bidderId: 119,
    bidderDisplayName: "Orient Express",
    amount: 5750.00,
    bidDate: subtractHours(20),
  },
  {
    id: 48,
    auctionId: 7,
    bidderId: 122,
    bidderDisplayName: "Collection Textile",
    amount: 5900.00,
    bidDate: subtractHours(12),
  },

  // Bids pour Auction 8 (Lustre en cristal)
  {
    id: 49,
    auctionId: 8,
    bidderId: 123,
    bidderDisplayName: "Lumières Anciennes",
    amount: 5500.00,
    bidDate: subtractDays(2),
  },
  {
    id: 50,
    auctionId: 8,
    bidderId: 124,
    bidderDisplayName: "Cristal & Lumière",
    amount: 5700.00,
    bidDate: subtractHours(42),
  },
  {
    id: 51,
    auctionId: 8,
    bidderId: 125,
    bidderDisplayName: "Éclairage d'Art",
    amount: 5900.00,
    bidDate: subtractHours(36),
  },
  {
    id: 52,
    auctionId: 8,
    bidderId: 123,
    bidderDisplayName: "Lumières Anciennes",
    amount: 6100.00,
    bidDate: subtractHours(30),
  },
  {
    id: 53,
    auctionId: 8,
    bidderId: 126,
    bidderDisplayName: "Baccarat Collector",
    amount: 6300.00,
    bidDate: subtractHours(24),
  },
  {
    id: 54,
    auctionId: 8,
    bidderId: 124,
    bidderDisplayName: "Cristal & Lumière",
    amount: 6500.00,
    bidDate: subtractHours(18),
  },
  {
    id: 55,
    auctionId: 8,
    bidderId: 125,
    bidderDisplayName: "Éclairage d'Art",
    amount: 6800.00,
    bidDate: subtractHours(12),
  },

  // Bids pour Auction 9 (Statuette en ivoire) - CLOSED
  {
    id: 56,
    auctionId: 9,
    bidderId: 127,
    bidderDisplayName: "Art Asiatique Pro",
    amount: 3000.00,
    bidDate: subtractDays(10),
  },
  {
    id: 57,
    auctionId: 9,
    bidderId: 128,
    bidderDisplayName: "Collection Japon",
    amount: 3200.00,
    bidDate: subtractDays(9),
  },
  {
    id: 58,
    auctionId: 9,
    bidderId: 129,
    bidderDisplayName: "Orient Antiques",
    amount: 3400.00,
    bidDate: subtractDays(8),
  },
  {
    id: 59,
    auctionId: 9,
    bidderId: 127,
    bidderDisplayName: "Art Asiatique Pro",
    amount: 3550.00,
    bidDate: subtractDays(7),
  },
  {
    id: 60,
    auctionId: 9,
    bidderId: 130,
    bidderDisplayName: "Ivoire Précieux",
    amount: 3700.00,
    bidDate: subtractDays(6),
  },
  {
    id: 61,
    auctionId: 9,
    bidderId: 128,
    bidderDisplayName: "Collection Japon",
    amount: 3800.00,
    bidDate: subtractDays(5),
  },
  {
    id: 62,
    auctionId: 9,
    bidderId: 129,
    bidderDisplayName: "Orient Antiques",
    amount: 3900.00,
    bidDate: subtractDays(5),
  },
  {
    id: 63,
    auctionId: 9,
    bidderId: 127,
    bidderDisplayName: "Art Asiatique Pro",
    amount: 4000.00,
    bidDate: subtractDays(4),
  },
  {
    id: 64,
    auctionId: 9,
    bidderId: 130,
    bidderDisplayName: "Ivoire Précieux",
    amount: 4100.00,
    bidDate: subtractDays(4),
  },
  {
    id: 65,
    auctionId: 9,
    bidderId: 128,
    bidderDisplayName: "Collection Japon",
    amount: 4150.00,
    bidDate: subtractDays(3),
  },
  {
    id: 66,
    auctionId: 9,
    bidderId: 127,
    bidderDisplayName: "Art Asiatique Pro",
    amount: 4200.00,
    bidDate: subtractDays(3),
  },

  // Bids pour Auction 10 (Fauteuil Louis XV) - CLOSED
  {
    id: 67,
    auctionId: 10,
    bidderId: 131,
    bidderDisplayName: "Mobilier Ancien",
    amount: 4000.00,
    bidDate: subtractDays(15),
  },
  {
    id: 68,
    auctionId: 10,
    bidderId: 132,
    bidderDisplayName: "Château Décor",
    amount: 4200.00,
    bidDate: subtractDays(14),
  },
  {
    id: 69,
    auctionId: 10,
    bidderId: 133,
    bidderDisplayName: "Louis XV Expert",
    amount: 4400.00,
    bidDate: subtractDays(13),
  },
  {
    id: 70,
    auctionId: 10,
    bidderId: 131,
    bidderDisplayName: "Mobilier Ancien",
    amount: 4600.00,
    bidDate: subtractDays(12),
  },
  {
    id: 71,
    auctionId: 10,
    bidderId: 134,
    bidderDisplayName: "Antiquités Royales",
    amount: 4800.00,
    bidDate: subtractDays(11),
  },
  {
    id: 72,
    auctionId: 10,
    bidderId: 132,
    bidderDisplayName: "Château Décor",
    amount: 5000.00,
    bidDate: subtractDays(10),
  },
  {
    id: 73,
    auctionId: 10,
    bidderId: 133,
    bidderDisplayName: "Louis XV Expert",
    amount: 5200.00,
    bidDate: subtractDays(10),
  },
  {
    id: 74,
    auctionId: 10,
    bidderId: 131,
    bidderDisplayName: "Mobilier Ancien",
    amount: 5350.00,
    bidDate: subtractDays(9),
  },
  {
    id: 75,
    auctionId: 10,
    bidderId: 134,
    bidderDisplayName: "Antiquités Royales",
    amount: 5450.00,
    bidDate: subtractDays(9),
  },
  {
    id: 76,
    auctionId: 10,
    bidderId: 132,
    bidderDisplayName: "Château Décor",
    amount: 5550.00,
    bidDate: subtractDays(8),
  },
  {
    id: 77,
    auctionId: 10,
    bidderId: 133,
    bidderDisplayName: "Louis XV Expert",
    amount: 5650.00,
    bidDate: subtractDays(8),
  },
  {
    id: 78,
    auctionId: 10,
    bidderId: 131,
    bidderDisplayName: "Mobilier Ancien",
    amount: 5700.00,
    bidDate: subtractDays(8),
  },
  {
    id: 79,
    auctionId: 10,
    bidderId: 134,
    bidderDisplayName: "Antiquités Royales",
    amount: 5750.00,
    bidDate: subtractDays(8),
  },
  {
    id: 80,
    auctionId: 10,
    bidderId: 132,
    bidderDisplayName: "Château Décor",
    amount: 5800.00,
    bidDate: subtractDays(8),
  },

  // Bids pour Auction 11 (Coffret à bijoux)
  {
    id: 81,
    auctionId: 11,
    bidderId: 135,
    bidderDisplayName: "Petits Objets",
    amount: 800.00,
    bidDate: subtractDays(1),
  },
  {
    id: 82,
    auctionId: 11,
    bidderId: 136,
    bidderDisplayName: "Bijoux & Écrin",
    amount: 900.00,
    bidDate: subtractHours(20),
  },
  {
    id: 83,
    auctionId: 11,
    bidderId: 137,
    bidderDisplayName: "Marqueterie Art",
    amount: 950.00,
    bidDate: subtractHours(18),
  },
  {
    id: 84,
    auctionId: 11,
    bidderId: 135,
    bidderDisplayName: "Petits Objets",
    amount: 1000.00,
    bidDate: subtractHours(16),
  },
  {
    id: 85,
    auctionId: 11,
    bidderId: 136,
    bidderDisplayName: "Bijoux & Écrin",
    amount: 1100.00,
    bidDate: subtractHours(12),
  },
  {
    id: 86,
    auctionId: 11,
    bidderId: 137,
    bidderDisplayName: "Marqueterie Art",
    amount: 1150.00,
    bidDate: subtractHours(8),
  },

  // Bids pour Auction 12 (Montre de gousset)
  {
    id: 87,
    auctionId: 12,
    bidderId: 138,
    bidderDisplayName: "Horlogerie Ancienne",
    amount: 2500.00,
    bidDate: subtractDays(4),
  },
  {
    id: 88,
    auctionId: 12,
    bidderId: 139,
    bidderDisplayName: "Montres d'Or",
    amount: 2650.00,
    bidDate: subtractHours(90),
  },
  {
    id: 89,
    auctionId: 12,
    bidderId: 140,
    bidderDisplayName: "Temps Précieux",
    amount: 2800.00,
    bidDate: subtractHours(84),
  },
  {
    id: 90,
    auctionId: 12,
    bidderId: 138,
    bidderDisplayName: "Horlogerie Ancienne",
    amount: 2900.00,
    bidDate: subtractHours(72),
  },
  {
    id: 91,
    auctionId: 12,
    bidderId: 141,
    bidderDisplayName: "Collection Horlogère",
    amount: 3000.00,
    bidDate: subtractHours(60),
  },
  {
    id: 92,
    auctionId: 12,
    bidderId: 139,
    bidderDisplayName: "Montres d'Or",
    amount: 3100.00,
    bidDate: subtractHours(48),
  },
  {
    id: 93,
    auctionId: 12,
    bidderId: 140,
    bidderDisplayName: "Temps Précieux",
    amount: 3200.00,
    bidDate: subtractHours(36),
  },
  {
    id: 94,
    auctionId: 12,
    bidderId: 138,
    bidderDisplayName: "Horlogerie Ancienne",
    amount: 3300.00,
    bidDate: subtractHours(24),
  },
  {
    id: 95,
    auctionId: 12,
    bidderId: 141,
    bidderDisplayName: "Collection Horlogère",
    amount: 3400.00,
    bidDate: subtractHours(18),
  },
  {
    id: 96,
    auctionId: 12,
    bidderId: 139,
    bidderDisplayName: "Montres d'Or",
    amount: 3500.00,
    bidDate: subtractHours(12),
  },
];

// Fonctions utilitaires
export const getBidsByAuctionId = (auctionId) => {
  return mockBids
    .filter(bid => bid.auctionId === parseInt(auctionId))
    .sort((a, b) => new Date(b.bidDate) - new Date(a.bidDate)); // Plus récentes en premier
};

export const getCurrentWinningBid = (auctionId) => {
  const bids = getBidsByAuctionId(auctionId);
  return bids.length > 0 ? bids[0] : null; // La plus récente = la plus haute
};

export const getNextBidAmount = (auctionId, currentPrice) => {
  // Incrément suggéré basé sur le prix actuel
  const price = parseFloat(currentPrice);
  if (price < 1000) return price + 50;
  if (price < 5000) return price + 100;
  if (price < 10000) return price + 200;
  return price + 500;
};

export const getBidsByBidderId = (bidderId) => {
  return mockBids
    .filter(bid => bid.bidderId === parseInt(bidderId))
    .sort((a, b) => new Date(b.bidDate) - new Date(a.bidDate));
};

export default mockBids;

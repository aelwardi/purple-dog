// Données mock pour les produits - à remplacer par des appels API plus tard
export const mockProducts = [
  {
    id: 1,
    title: "Vase en porcelaine de Chine, Dynastie Qing",
    description: "Magnifique vase en porcelaine bleu et blanc datant de la dynastie Qing (1644-1912)",
    category: "Porcelaine",
    price: 3500,
    condition: "Excellent",
    image: "https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=400&h=400&fit=crop"
  },
  {
    id: 2,
    title: "Tableau Impressionniste - Paysage de Provence",
    description: "Huile sur toile représentant un paysage provençal, style impressionniste, début XXe siècle",
    category: "Peinture",
    price: 8900,
    condition: "Très bon",
    image: "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?w=400&h=400&fit=crop"
  },
  {
    id: 3,
    title: "Sculpture en bronze Art Déco",
    description: "Danseuse en bronze signé, époque Art Déco 1920-1930",
    category: "Sculpture",
    price: 5200,
    condition: "Excellent",
    image: "https://images.unsplash.com/photo-1513519245088-0e12902e35ca?w=400&h=400&fit=crop"
  },
  {
    id: 4,
    title: "Horloge Comtoise XVIIIe siècle",
    description: "Horloge comtoise en chêne massif, mécanisme d'origine, sonnerie au passage",
    category: "Horlogerie",
    price: 4800,
    condition: "Bon",
    image: "https://images.unsplash.com/photo-1563861826100-9cb868fdbe1c?w=400&h=400&fit=crop"
  },
  {
    id: 5,
    title: "Service à thé en argent massif",
    description: "Service à thé complet en argent massif poinçonné, style Napoléon III",
    category: "Argenterie",
    price: 2800,
    condition: "Très bon",
    image: "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=400&h=400&fit=crop"
  },
  {
    id: 6,
    title: "Miroir vénitien ancien",
    description: "Grand miroir vénitien en verre de Murano avec cadre ouvragé, XIXe siècle",
    category: "Mobilier",
    price: 3200,
    condition: "Bon",
    image: "https://images.unsplash.com/photo-1618220179428-22790b461013?w=400&h=400&fit=crop"
  },
  {
    id: 7,
    title: "Tapis Persan Tabriz",
    description: "Tapis persan noué main, région de Tabriz, laine et soie, circa 1950",
    category: "Textile",
    price: 6500,
    condition: "Excellent",
    image: "https://images.unsplash.com/photo-1600166898405-da9535204843?w=400&h=400&fit=crop"
  },
  {
    id: 8,
    title: "Lustre en cristal de Baccarat",
    description: "Lustre à pampilles en cristal de Baccarat, 8 lumières, début XXe siècle",
    category: "Luminaire",
    price: 7200,
    condition: "Excellent",
    image: "https://images.unsplash.com/photo-1565373679574-2b9817ee4e53?w=400&h=400&fit=crop"
  },
  {
    id: 9,
    title: "Statuette en ivoire, Japon Meiji",
    description: "Okimono en ivoire sculpté représentant un sage, période Meiji (1868-1912)",
    category: "Art Asiatique",
    price: 4200,
    condition: "Très bon",
    image: "https://images.unsplash.com/photo-1582126147936-a79f4b27ed2c?w=400&h=400&fit=crop"
  },
  {
    id: 10,
    title: "Fauteuil Louis XV",
    description: "Fauteuil à la reine, époque Louis XV, bois doré et tapisserie d'Aubusson",
    category: "Mobilier",
    price: 5800,
    condition: "Restauré",
    image: "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400&h=400&fit=crop"
  },
  {
    id: 11,
    title: "Coffret à bijoux en marqueterie",
    description: "Coffret Napoléon III en marqueterie avec incrustations de nacre",
    category: "Objets d'art",
    price: 1200,
    condition: "Bon",
    image: "https://images.unsplash.com/photo-1611085583191-a3b181a88401?w=400&h=400&fit=crop"
  },
  {
    id: 12,
    title: "Montre de gousset en or",
    description: "Montre de gousset en or 18 carats avec chaîne, fin XIXe siècle",
    category: "Horlogerie",
    price: 3800,
    condition: "Excellent",
    image: "https://images.unsplash.com/photo-1509941943102-10c232535736?w=400&h=400&fit=crop"
  },
  {
    id: 13,
    title: "Lithographie originale - Picasso",
    description: "Lithographie originale signée dans la planche, édition limitée",
    category: "Estampe",
    price: 12000,
    condition: "Excellent",
    image: "https://images.unsplash.com/photo-1541961017774-22349e4a1262?w=400&h=400&fit=crop"
  },
  {
    id: 14,
    title: "Paire de vases Médicis en fonte",
    description: "Paire de vases monumentaux en fonte patinée, décor néoclassique",
    category: "Jardin",
    price: 4500,
    condition: "Bon",
    image: "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=400&h=400&fit=crop"
  },
  {
    id: 15,
    title: "Épée de collection XVIIIe siècle",
    description: "Épée d'officier avec fourreau en cuir, lame gravée, garde en laiton",
    category: "Militaria",
    price: 2400,
    condition: "Bon",
    image: "https://images.unsplash.com/photo-1596838132731-3301c3fd4317?w=400&h=400&fit=crop"
  },
  {
    id: 16,
    title: "Lampe Tiffany",
    description: "Lampe de table style Tiffany, vitraux multicolores, début XXe siècle",
    category: "Luminaire",
    price: 3900,
    condition: "Très bon",
    image: "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=400&h=400&fit=crop"
  },
  {
    id: 17,
    title: "Céramique de Vallauris",
    description: "Plat décoratif en céramique émaillée, Vallauris, années 1950",
    category: "Céramique",
    price: 850,
    condition: "Excellent",
    image: "https://images.unsplash.com/photo-1578500494198-246f612d3b3d?w=400&h=400&fit=crop"
  },
  {
    id: 18,
    title: "Globe terrestre ancien",
    description: "Globe terrestre sur pied en acajou, cartographie XIXe siècle",
    category: "Cartographie",
    price: 1800,
    condition: "Bon",
    image: "https://images.unsplash.com/photo-1569163139394-de4798aa62b6?w=400&h=400&fit=crop"
  },
  {
    id: 19,
    title: "Icône russe ancienne",
    description: "Icône religieuse sur bois, école russe, XIXe siècle, tempera sur fond d'or",
    category: "Art Religieux",
    price: 5600,
    condition: "Restauré",
    image: "https://images.unsplash.com/photo-1577720643272-265f08d0f59d?w=400&h=400&fit=crop"
  },
  {
    id: 20,
    title: "Flacon de parfum Lalique",
    description: "Flacon de parfum en cristal moulé-pressé, signé R. Lalique, années 1920",
    category: "Verrerie",
    price: 2200,
    condition: "Excellent",
    image: "https://images.unsplash.com/photo-1541643600914-78b084683601?w=400&h=400&fit=crop"
  }
];

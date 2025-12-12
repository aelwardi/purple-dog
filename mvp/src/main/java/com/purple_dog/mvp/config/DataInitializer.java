package com.purple_dog.mvp.config;

import com.purple_dog.mvp.dao.*;
import com.purple_dog.mvp.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(
            PersonRepository personRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            // === UTILISATEURS ===
            Individual individual = null;
            Professional professional = null;
            
            if (personRepository.findByEmail("particulier@gmail.com").isEmpty()) {
                individual = new Individual();
                individual.setEmail("particulier@gmail.com");
                individual.setPassword(passwordEncoder.encode("password123"));
                individual.setFirstName("Jean");
                individual.setLastName("Dupont");
                individual.setPhone("0612345678");
                individual.setRole(UserRole.INDIVIDUAL);
                individual.setAccountStatus(AccountStatus.ACTIVE);
                individual.setEmailVerified(true);
                individual = personRepository.save(individual);
                logger.info("✅ Particulier créé");
            } else {
                individual = (Individual) personRepository.findByEmail("particulier@gmail.com").get();
            }

            if (personRepository.findByEmail("professionnel@gmail.com").isEmpty()) {
                professional = new Professional();
                professional.setEmail("professionnel@gmail.com");
                professional.setPassword(passwordEncoder.encode("password123"));
                professional.setFirstName("Marie");
                professional.setLastName("Martin");
                professional.setPhone("0687654321");
                professional.setRole(UserRole.PROFESSIONAL);
                professional.setAccountStatus(AccountStatus.ACTIVE);
                professional.setEmailVerified(true);
                professional.setCompanyName("Martin SARL");
                professional.setSiret("12345678901234");
                professional = personRepository.save(professional);
                logger.info("✅ Professionnel créé");
            } else {
                professional = (Professional) personRepository.findByEmail("professionnel@gmail.com").get();
            }

            if (personRepository.findByEmail("admin@purpledog.com").isEmpty()) {
                Admin admin = new Admin();
                admin.setEmail("admin@purpledog.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFirstName("Admin");
                admin.setLastName("Purple Dog");
                admin.setPhone("0600000000");
                admin.setRole(UserRole.ADMIN);
                admin.setAccountStatus(AccountStatus.ACTIVE);
                admin.setEmailVerified(true);
                personRepository.save(admin);
                logger.info("✅ Admin créé");
            }

            // === CATÉGORIES ===
            Category bijoux = createCategoryIfNotExists(categoryRepository, "Bijoux & Montres", 
                "Bijoux précieux, montres de luxe, pierres précieuses");
            Category art = createCategoryIfNotExists(categoryRepository, "Art & Antiquités", 
                "Tableaux, sculptures, objets d'art anciens");
            Category electronique = createCategoryIfNotExists(categoryRepository, "Électronique", 
                "Smartphones, ordinateurs, appareils électroniques de valeur");
            Category mode = createCategoryIfNotExists(categoryRepository, "Mode & Luxe", 
                "Vêtements de créateurs, sacs de luxe, accessoires");
            Category collection = createCategoryIfNotExists(categoryRepository, "Collections",
                "Timbres, monnaies, cartes rares, objets de collection");
            Category ameublement = createCategoryIfNotExists(categoryRepository, "Mobilier Design", 
                "Meubles design, mobilier ancien, pièces uniques");

            logger.info("✅ {} catégories", categoryRepository.count());

            // === PRODUITS (QUICK_SALE uniquement - PAS d'enchères) ===
            // Créer uniquement si le produit n'existe pas déjà
            createProductIfNotExists(productRepository, "Rolex Submariner Date 116610LN",
                "Montre Rolex Submariner Date en acier inoxydable, cadran noir, bracelet Oyster.",
                new BigDecimal("8500.00"), ProductCondition.EXCELLENT, bijoux, individual);

            createProductIfNotExists(productRepository, "Tableau Huile sur Toile - Paysage Maritime",
                "Magnifique tableau huile sur toile représentant un paysage maritime. Dimensions: 80x60cm.",
                new BigDecimal("1200.00"), ProductCondition.GOOD, art, professional);

            createProductIfNotExists(productRepository, "iPhone 15 Pro Max 256GB",
                "iPhone 15 Pro Max neuf, 256GB, couleur titane naturel. Garantie Apple 1 an.",
                new BigDecimal("1300.00"), ProductCondition.NEW, electronique, individual);

            createProductIfNotExists(productRepository, "Sac Hermès Birkin 30",
                "Sac iconique Hermès Birkin 30 en cuir Togo noir. Authentique avec certificat.",
                new BigDecimal("9500.00"), ProductCondition.LIKE_NEW, mode, professional);

            createProductIfNotExists(productRepository, "Chaise Eames DSW Vintage",
                "Chaise Eames DSW authentique des années 70. Pièce collector en très bon état.",
                new BigDecimal("450.00"), ProductCondition.LIKE_NEW, ameublement, individual);

            createProductIfNotExists(productRepository, "Pièces Or Napoléon 20 Francs",
                "Lot de 5 pièces Napoléon 20 Francs Or. Années 1850-1900. Certificat inclus.",
                new BigDecimal("1850.00"), ProductCondition.GOOD, collection, professional);

            logger.info("🎯 Initialisation terminée!");
            logger.info("📊 Total: {} utilisateurs, {} catégories, {} produits",
                    personRepository.count(), categoryRepository.count(), productRepository.count());
        };
    }

    private Category createCategoryIfNotExists(CategoryRepository repo, String name, String description) {
        return repo.findByName(name).orElseGet(() -> {
            Category cat = new Category();
            cat.setName(name);
            cat.setDescription(description);
            cat.setActive(true);
            return repo.save(cat);
        });
    }

    private void createProductIfNotExists(ProductRepository repo, String title, String description,
            BigDecimal estimatedValue, ProductCondition condition, Category category, Person seller) {
        if (repo.findByTitle(title).isEmpty()) {
            Product product = new Product();
            product.setTitle(title);
            product.setDescription(description);
            product.setEstimatedValue(estimatedValue);
            product.setProductCondition(condition);
            product.setCategory(category);
            product.setSeller(seller);
            product.setSaleType(SaleType.QUICK_SALE);
            product.setStatus(ProductStatus.PENDING_VALIDATION);
            repo.save(product);
            logger.info("✅ Produit créé: {}", title);
        }
    }
}

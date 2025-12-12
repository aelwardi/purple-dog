package com.purple_dog.mvp.config;

import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.entities.Individual;
import com.purple_dog.mvp.entities.Professional;
import com.purple_dog.mvp.entities.Admin;
import com.purple_dog.mvp.entities.UserRole;
import com.purple_dog.mvp.entities.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Créer un utilisateur particulier de test si pas déjà existant
            if (personRepository.findByEmail("particulier@gmail.com").isEmpty()) {
                Individual individual = new Individual();
                individual.setEmail("particulier@gmail.com");
                individual.setPassword(passwordEncoder.encode("password123"));
                individual.setFirstName("Jean");
                individual.setLastName("Dupont");
                individual.setPhone("0612345678");
                individual.setRole(UserRole.INDIVIDUAL);
                individual.setAccountStatus(AccountStatus.ACTIVE);
                individual.setEmailVerified(true);
                personRepository.save(individual);
                logger.info("✅ Utilisateur particulier de test créé: particulier@gmail.com / password123");
            }

            // Créer un utilisateur professionnel de test si pas déjà existant
            if (personRepository.findByEmail("professionnel@gmail.com").isEmpty()) {
                Professional professional = new Professional();
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
                personRepository.save(professional);
                logger.info("✅ Utilisateur professionnel de test créé: professionnel@gmail.com / password123");
            }

            // Créer un admin de test si pas déjà existant
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
                logger.info("✅ Utilisateur admin de test créé: admin@purpledog.com / admin123");
            }

            logger.info("🎯 Initialisation des données de test terminée");
        };
    }
}

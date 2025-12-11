package com.purple_dog.mvp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration globale pour le Web MVC
 * Gère notamment la configuration CORS
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://localhost:5173",     // Frontend Vite dev server
                    "http://localhost:5174",     // Vite port 2
                    "http://localhost:5175",     // Vite port 3
                    "http://localhost:5176",     // Vite port 4
                    "http://localhost:3000",     // Alternative frontend port
                    "http://localhost:4173"      // Vite preview
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

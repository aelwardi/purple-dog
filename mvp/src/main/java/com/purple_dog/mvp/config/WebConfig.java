package com.purple_dog.mvp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration globale pour le Web MVC
 * CORS est géré dans SecurityConfig pour éviter les conflits
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS est géré dans SecurityConfig.java
    // Ne pas dupliquer la config ici pour éviter les conflits
    
    /* @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://localhost:5173",
                    "http://localhost:3000",
                    "http://localhost:4173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    */
}

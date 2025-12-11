package com.purple_dog.mvp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration pour l'envoi d'emails asynchrones
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // Les paramètres sont dans application.properties
}
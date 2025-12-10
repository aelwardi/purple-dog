package com.purple_dog.mvp.config;

import com.shippo.Shippo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ShippoConfig {

    @Value("${shippo.api-key}")
    private String apiKey;

    @Value("${shippo.test-mode:true}")
    private boolean testMode;

    @PostConstruct
    public void init() {
        Shippo.setApiKey(apiKey);
        log.info("Shippo API initialized successfully (Test mode: {})", testMode);
        log.info("Using Shippo API key: {}...", apiKey.substring(0, 20));
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean isTestMode() {
        return testMode;
    }
}


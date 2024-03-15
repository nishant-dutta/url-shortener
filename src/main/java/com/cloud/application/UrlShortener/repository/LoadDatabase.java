package com.cloud.application.UrlShortener.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Loads some dummy data which can be used for liveliness probes
// when working with a real database, configure it to push values only if it isn't present
@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(ShortenedUrlProxy repository){
        return args -> {
            log.info("Preloading " + repository.save(new ShortenedUrl("http://abc.com","a", 1)));
            log.info("Preloading " + repository.save(new ShortenedUrl("http://pqr.com","p", 1)));
        };
    }
}
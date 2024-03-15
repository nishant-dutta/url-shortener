package com.cloud.application.UrlShortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortenedUrlProxy extends JpaRepository<ShortenedUrl, Integer> {
    public ShortenedUrl findByShortUrl(String shortUrl);
}

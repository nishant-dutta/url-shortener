package com.cloud.application.UrlShortener.repository;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class ShortenedUrl {

    @Id
    @GeneratedValue
    private int shortUrlId;

    @Column(unique = true)
    private String shortUrl;

    private String originalUrl;
    private LocalDateTime createdOn;
    private LocalDateTime lastModified;
    private int userId;
    private boolean isEnabled;

    public ShortenedUrl() {
        this.createdOn = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public ShortenedUrl(String originalUrl, String shortUrl, int userId) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.userId = userId;
        this.createdOn = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public int getShortUrlId() {
        return shortUrlId;
    }

    public void setShortUrlId(int shortUrlId) {
        this.shortUrlId = shortUrlId;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public String toString() {
        return "ShortenedUrl{" +
                "shortUrlId=" + shortUrlId +
                ", shortUrl='" + shortUrl + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", createdOn=" + createdOn +
                ", lastModified=" + lastModified +
                ", userId=" + userId +
                ", isEnabled=" + isEnabled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShortenedUrl that)) return false;
        return shortUrlId == that.shortUrlId && Objects.equals(shortUrl, that.shortUrl) && Objects.equals(originalUrl, that.originalUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortUrlId, shortUrl, originalUrl);
    }
}
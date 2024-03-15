package com.cloud.application.UrlShortener.urlDao;

import com.cloud.application.UrlShortener.repository.ShortenedUrl;
import com.cloud.application.UrlShortener.repository.ShortenedUrlProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ShortenedUrlDao {
    private static final Logger logger = LoggerFactory.getLogger(ShortenedUrlDao.class);
    @Autowired
    ShortenedUrlProxy repository;

    public String mapShortUrlToLong(String shortUrl){
        ShortenedUrl shortenedUrl  = repository.findByShortUrl(shortUrl);

        if(shortenedUrl == null){
            logger.info("No entries found for shortUrl:" + shortUrl);
            return null;
        }else {
            logger.info(shortenedUrl.toString());
        }
        return shortenedUrl.getOriginalUrl();
    }

    public ShortenedUrl getShortUrlDetails(String shortUrl){
        return repository.findByShortUrl(shortUrl);
    }

    public ShortenedUrl getUrlById(int id){
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Couldn't find shortURL with Id:" + id));
    }

    public ShortenedUrl save(ShortenedUrl shortenedUrl){
        logger.info("Creating a new entry for shortUrl:" + shortenedUrl.getShortUrl());
        return repository.save(shortenedUrl);
    }

    public ShortenedUrl updateShortUrlDetails(ShortenedUrl updatedUrlObject){
        ShortenedUrl shortenedUrl = repository.findByShortUrl(updatedUrlObject.getShortUrl());

        if(shortenedUrl == null){
            logger.warn("shortUrl:" + updatedUrlObject.getShortUrl() + " doesn't exist!");
        }else{
            shortenedUrl.setOriginalUrl(updatedUrlObject.getOriginalUrl());
            shortenedUrl.setEnabled(updatedUrlObject.isEnabled());
            shortenedUrl.setLastModified(LocalDateTime.now());
            logger.info("Updated To: " + shortenedUrl);
        }
        return repository.save(shortenedUrl);
    }

    public void deleteShortUrl(String shortUrl){
        ShortenedUrl shortenedUrl = getShortUrlDetails(shortUrl);
        repository.deleteById(shortenedUrl.getShortUrlId());
    }

}

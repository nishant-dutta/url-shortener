package com.cloud.application.UrlShortener.controller;

import com.cloud.application.UrlShortener.repository.ShortenedUrl;
import com.cloud.application.UrlShortener.urlDao.ShortenedUrlDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@RestController
public class ShortenedUrlController {

    private static final Logger logger = LoggerFactory.getLogger(ShortenedUrlController.class);

    @GetMapping("/")
    public String getHomePage(){
        return "HomePage";
    }


    @Autowired
    ShortenedUrlDao shortenedUrlDao;

    @GetMapping("/exists/{shortUrl}")
    public boolean shortUrlExists(@PathVariable String shortUrl){
        return shortenedUrlDao.getShortUrlDetails(shortUrl) != null ? true : false;
    }

    @GetMapping("/getUrlById/{id}")
    public ShortenedUrl getUrlById(@PathVariable int id){
        return shortenedUrlDao.getUrlById(id);
    }

    @PostMapping("/create/{shortUrl}")
    public ShortenedUrl addShortUrl(HttpServletResponse response, @PathVariable String shortUrl, @RequestBody ShortenedUrl shortenedUrl){
        response.setStatus(HttpStatus.CREATED.value());
        return shortenedUrlDao.save(shortenedUrl);
    }

    @GetMapping("/get/{shortUrl}")
    public ShortenedUrl getShortUrlDetails(@PathVariable String shortUrl){
        return shortenedUrlDao.getShortUrlDetails(shortUrl);
    }

    @PatchMapping("/update/{shortUrl}")
    public ShortenedUrl updateShortUrlDetails(@RequestBody ShortenedUrl shortenedUrl){
        return shortenedUrlDao.updateShortUrlDetails(shortenedUrl);
    }

    @DeleteMapping("/delete/{shortUrl}")
    public void deleteShortUrl(@PathVariable String shortUrl, HttpServletResponse response){
        response.setStatus(HttpStatus.ACCEPTED.value());
        logger.info("Deleting shortURL:" + shortUrl);
        shortenedUrlDao.deleteShortUrl(shortUrl);
    }

    @RequestMapping("/test-id")
    public ModelAndView redirectTo(ModelMap model, HttpServletRequest request) {
        String url = "https://jsonplaceholder.typicode.com/posts";
        logger.info("Redirected shortUrl:/test-id to:" +  url);

        // Can be used for permanent redirects
        // request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);

        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:" + url, model);
    }

    @RequestMapping("/{shortUrl}")
//    @ResponseBody
    public ModelAndView redirectToURI(HttpServletRequest request, @PathVariable String shortUrl) {

        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);

        String longUrl = shortenedUrlDao.mapShortUrlToLong(shortUrl);

        if(longUrl == null) {
            String defaultURI = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            logger.error("shortUrl:" + shortUrl + " NOT FOUND! Redirecting to defaultURI:" + defaultURI);
            return new ModelAndView("redirect:" + defaultURI);
        }else {
            logger.info("Redirecting shortUrI:" + shortUrl + " to URI:" + longUrl);
            return new ModelAndView("redirect:" + longUrl);
        }
    }

}

package com.lessons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application
 **/
@SpringBootApplication
public class Application
{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);


    /**
     * Web Application Starts Here
     **/
    public static void main( String[] args )
    {
        logger.debug("main() started.");

        // Start up Spring Boot but disable the banner
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

        logger.debug("WebApp is Up.");
    }
}
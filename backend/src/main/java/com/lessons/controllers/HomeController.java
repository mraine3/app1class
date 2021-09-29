package com.lessons.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * This page endpoint is needed to ensure that all page routes to the Angular Frontend
     * @return a string which causes Spring Web to redirect the user to index.html
     *
     * NOTE:  If the user is going to /app1/page/view/reports, then
     *         1. Spring will redirect the user to the /index.html
     *         2. Angular routes will redirect the user to the route for view/reports
     */
    @RequestMapping(value = {"/", "/page/**"}, method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('READER', 'ADMIN')")
    public String home() {

        // This method handles two cases:
        // Case 1: The user goes to http://localhost:8080/app1  --> Take users to the index.html
        // Case 2: The user goes to http://localhost:8080/app1/page/addReport and presses refresh --> Take users to the index.html
        return "forward:/index.html";
    }


    /**
     * GET /api/time
     * @return a plain-old string with the system time
     */
    @RequestMapping(value = "/api/time", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAnyRole('READER', 'ADMIN')")
    public ResponseEntity<?> getDateTime() {
        logger.debug("getDateTime() started.");

        // Get the date/time
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);

        // Return the date/time string as plain-text
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(dateTime);
        }

}

package com.lessons.controllers;

import com.lessons.models.GetPreferenceDTO;
import com.lessons.services.PreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Controller("com.lessons.controllers.PreferenceController")
public class PreferenceController {
    private static final Logger logger = LoggerFactory.getLogger(PreferenceController.class);

    @Resource
    private PreferenceService preferenceService;


    /**
     * GET /api/preferences/banner REST call
     *
     * Returns a PreferenceDTO object that holds information about this preference
     */
    @RequestMapping(value = "/api/preferences/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getPreferences() {

        logger.debug("getPreferences() started.");

        int loggedInUserId = 25;

        // Get the preference information
        GetPreferenceDTO preferenceDTO = this.preferenceService.getPreferences(loggedInUserId);

        // Return the GetPreferenceDTO back to the front-end and a 200 status code
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(preferenceDTO);
    }

    /**
     * POST /api/preferences/banner/set/{bannerValue} REST call
     *
     * This REST call sets the banner preference only
     * Returns 200 status code if it works
     */
    @RequestMapping(value = "/api/preferences/banner/set/{bannerValue}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> setBannerPreference(@PathVariable(value="bannerValue") Boolean aBannerValue) {

        logger.debug("setBannerPreference() started.  aBannerValue={}", aBannerValue);

        int loggedInUserId = 25;

        // Set the banner preference
        this.preferenceService.setBanner(loggedInUserId, aBannerValue);

        // Return a 200 status code and a null object
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }



}

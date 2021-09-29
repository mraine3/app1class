package com.lessons.controllers;

import com.lessons.models.GetDashboardDataDTO;
import com.lessons.services.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Controller("com.lessons.controllers.DashboardController")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Resource
    private DashboardService dashboardService;


    /**
     * REST endpoint /api/dashboard/chart/data
     *
     * @return ResponseEntity object that a GetDashboardDataDTO object and 200 status code
     */
    @RequestMapping(value = "/api/dashboard/chart/data", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getChartData() {

        logger.debug("getChartData() started.");

        // Use the DashboardService to get the information from the back-end
        GetDashboardDataDTO getDashboardDataDTO = this.dashboardService.getAllChartData();

        // Return the DTO object back to the front-end  (Jackson will convert the java object to JSON)
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getDashboardDataDTO);
    }
}

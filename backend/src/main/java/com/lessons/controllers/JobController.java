package com.lessons.controllers;

import com.lessons.models.JobStatusDTO;
import com.lessons.services.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Controller("com.lessons.controllers.JobController")
public class JobController {
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    @Resource
    private JobService jobService;

    /**
     * GET /api/jobs/status/{jobId} REST call
     *
     * Returns a JobStatusDTO object that holds information about this one job
     */
    @RequestMapping(value = "/api/jobs/status/{jobId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getJobStatus(@PathVariable(value="jobId") Integer aJobId) {

        logger.debug("getJobStatus() started.  aJobId={}", aJobId);

        if (! this.jobService.doesJobExist(aJobId)) {
            // The job id is not found in the database
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Your job id does not exist in the database: " + aJobId);
        }

        // Get the status information for this job
        JobStatusDTO jobStatusDTO = jobService.getJobStatus(aJobId);

        logger.debug("getJobStatus() returns.  aJobId={}  state={}", aJobId, jobStatusDTO.getState());

        // Return the JobStatusDTO back to the front-end and a 200 status code
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jobStatusDTO);
    }

}



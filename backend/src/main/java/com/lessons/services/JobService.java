package com.lessons.services;

import com.lessons.models.JobStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Service("com.lessons.services.JobService")
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private DatabaseService databaseService;

    public static final Integer JOB_STATE_PENDING = 1;
    public static final Integer JOB_STATE_WORK_IN_PROGRESS = 2;
    public static final Integer JOB_STATE_FINISHED_SUCCESS = 3;
    public static final Integer JOB_STATE_FINISHED_ERROR = 4;

    /**
     *  Update the job record in the database
     */
    public void updateJobRecord(Integer aJobId, Integer aJobState, Integer aProgressAsPercent) {
        logger.debug("updateJobRecord started:  aJobId={}", aJobId);

        if (aJobId == null) {
            throw new RuntimeException("Error in updateJobRecord():  The passed-in aJobId is null.");
        }
        else if (aJobState == null) {
            throw new RuntimeException("Error in updateJobRecord():  The passed-in aJobStatus is null.");
        }

        // Construct the SQL to update this job record
        String sql = "update jobs set state=?, progress_as_percent=? where id=?";

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        int rowsUpdated = jt.update(sql, aJobState, aProgressAsPercent,
                aJobId);

        if (rowsUpdated != 1) {
            throw new RuntimeException("Error in updateJobRecord():  I expected to update one record, but instead I updated " + rowsUpdated + " rows.  This should never happen.");
        }

        logger.debug("updateJobRecord finished:  aJobId={}  aJobState={}", aJobId, aJobState);
    }


    /**
     *  Update the job record as failed
     */
    public void updateJobRecordAsFailed(Integer aJobId, String aJobMessage) {
        logger.debug("updateJobRecordAsFailed started:  aJobId={}", aJobId);

        if (aJobId == null) {
            throw new RuntimeException("Error in updateJobRecordAsFailed():  The passed-in aJobId is null.");
        }

        // Construct the SQL to update this job record
        String sql = "update jobs set state=?, user_message=? where id=?";

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        int rowsUpdated = jt.update(sql, JOB_STATE_FINISHED_ERROR, aJobMessage,
                aJobId);

        if (rowsUpdated != 1) {
            throw new RuntimeException("Error in updateJobRecordAsFailed():  I expected to update one record, but instead I updated " + rowsUpdated + " rows.  This should never happen.");
        }

        logger.debug("updateJobRecordAsFailed finished:  aJobId={}", aJobId);
    }


    /**
     *  Update the job record as completed successfully
     */
    public void updateJobRecordAsSucceeded(Integer aJobId) {
        logger.debug("updateJobRecordAsSucceeded started:  aJobId={}", aJobId);

        if (aJobId == null) {
            throw new RuntimeException("Error in updateJobRecordAsSucceeded():  The passed-in aJobId is null.");
        }

        // Construct the SQL to update this job record
        String sql = "update jobs set state=?, progress_as_percent=100 where id=?";

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        int rowsUpdated = jt.update(sql, JOB_STATE_FINISHED_SUCCESS, aJobId);

        if (rowsUpdated != 1) {
            throw new RuntimeException("Error in updateJobRecordAsSucceeded():  I expected to update one record, but instead I updated " + rowsUpdated + " rows.  This should never happen.");
        }

        logger.debug("updateJobRecordAsSucceeded finished:  aJobId={}", aJobId);
    }


    /**
     * Create a job record and return the number of the newly-created record
     */
    public Integer addJobRecord(String aJobSubmitterName, String aUploadedFilename) {
        logger.debug("addJobRecord() started.");

        // Get the next unique id from the database
        Integer newJobId = this.databaseService.getNextId();

        // Create a parameter map to hold all of the bind variables
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", newJobId);
        paramMap.put("state", JOB_STATE_WORK_IN_PROGRESS);
        paramMap.put("submitter_username", aJobSubmitterName);
        paramMap.put("original_filename", aUploadedFilename);

        // Construct the SQL to insert the jobs record
        String sql = "insert into jobs(id, state, submitter_username, original_filename) " +
                "values(:id, :state, :submitter_username, :original_filename)";

        // Execute the SQL to insert the jobs record
        NamedParameterJdbcTemplate nt = new NamedParameterJdbcTemplate(this.dataSource);
        nt.update(sql, paramMap);

        // Return the new job id
        return newJobId;
    }

    /**
     * Get Single JobStatusDTO objects back from the database
     */
    public JobStatusDTO getJobStatus(Integer aJobId) {

        // Execute the SQL to get information about this job
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "select state, user_message, progress_as_percent from jobs where id=?";
        SqlRowSet rs = jt.queryForRowSet(sql, aJobId);

        if (! rs.next() ) {
            // I did not find the record
            throw new RuntimeException("Error in getJobStatus() with aJobId=" + aJobId + "  The job record was not found in the database.");
        }

        // Get the values out of the SqlRowSet
        int    state             = rs.getInt("state");
        int    progressAsPercent = rs.getInt("progress_as_percent");
        String userMessage       = rs.getString("user_message");

        // Put the information into a single object
        JobStatusDTO jobStatusDTO = new JobStatusDTO(aJobId, state, progressAsPercent, userMessage);

        return jobStatusDTO;
    }


    /**
     * @param aJobId ID that uniquely identifies this job record
     * @return TRUE if the passed-in aJobId is found in the JOBS database table.  FALSE if not found
     */
    public boolean doesJobExist(int aJobId) {
        // Execute the SQL to get information about this job
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "select id from jobs where id=?";
        SqlRowSet rs = jt.queryForRowSet(sql, aJobId);

        return rs.next();
    }

}


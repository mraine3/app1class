package com.lessons.services;

import com.lessons.models.AddReportDTO;
import com.lessons.models.GetReportDTO;
import com.lessons.models.GetUpdateReportDTO;
import com.lessons.models.SetUpdateReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("com.lessons.services.ReportService")
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private DatabaseService databaseService;


    /**
     * Attempt to add a report record to the database
     *
     * @param addReportDTO  Pass-in model object that holds all of the report fields
     */
    public void addReport(AddReportDTO addReportDTO) {
        logger.debug("addReport() started.");

        String sql = "insert into reports(id, version, name, priority, start_date, end_date) " +
                "values(:id, :version, :name, :priority, :start_date, :end_date)";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", databaseService.getNextId() );
        paramMap.put("version", databaseService.getStartingVersionValue());
        paramMap.put("name", addReportDTO.getName());
        paramMap.put("priority", addReportDTO.getPriority());
        paramMap.put("start_date",addReportDTO.getStartDate());
        paramMap.put("end_date", addReportDTO.getEndDate());

        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(this.dataSource);

        // Execute the SQL
        int rowsCreated = np.update(sql, paramMap);

        if (rowsCreated != 1) {
            throw new RuntimeException("Critical error in addReport():  I expected to create one database record, but did not.");
        }

        logger.debug("addReport() finished.");
    }

    /**
     * @return a List of all Reports (as a list of GetReportDTO objects)
     */
    public List<GetReportDTO> getAllReports() {
        // Construct the SQL to get all reports
        // NOTE:  We do a left join to get all records from reports
        //        If a report record has null  for priority, then priority is null
        //        If a report record has a priority it, then get the name for that priority
        String sql = "select r.id, r.name, l.name as priority, \n" +
                     "       to_char(start_date, 'mm/dd/yyyy') as start_date, to_char(end_date, 'mm/dd/yyyy') as end_date \n" +
                     "from reports r \n" +
                     "LEFT JOIN lookup l on (r.priority = l.id) \n" +
                     "order by id";

        // Use the rowMapper to convert the results into a list of GetReportDTO objects
        BeanPropertyRowMapper<GetReportDTO> rowMapper = new BeanPropertyRowMapper<>(GetReportDTO.class);

        // Execute the SQL and Convert the results into a list of GetReportDTO objects
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        List<GetReportDTO> listOfReports = jt.query(sql, rowMapper);

        // Return the list of GetReportDTO objects
        return listOfReports;
    }

    /**
     * @param aReportId holds the ID that uniquely identifies thie report in the database
     * @return TRUE if the ID is found in the reports table.  False otherwise.
     */
    public boolean doesReportIdExist(Integer aReportId) {
        if (aReportId == null) {
            return false;
        }

        String sql = "select id from reports where id=?";
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        SqlRowSet rs = jt.queryForRowSet(sql, aReportId);
        if (rs.next() ) {
            // I found the record in the database.  So, return true.
            return true;
        }
        else {
            // I did not find this ID in the database.  So, return false.
            return false;
        }
    }


    /**
     * Update the Report record in the database
     *
     * @param aUpdateReportDTO holds the information from the front-end with new report info
     */
    public void updateReport(SetUpdateReportDTO aUpdateReportDTO) {
        // Construct the SQL to update this record
        String sql = "UPDATE reports set name=:name, priority=:priority WHERE id=:id";

        // Create a parameter map
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name",     aUpdateReportDTO.getReportName() );
        paramMap.put("priority", aUpdateReportDTO.getPriority() );
        paramMap.put("id",       aUpdateReportDTO.getId() );

        // Execute the SQL and get the number of rows affected
        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(this.dataSource);
        int rowsUpdated = np.update(sql, paramMap);

        if (rowsUpdated != 1) {
            throw new RuntimeException("I expected to update one report record.  Instead, I updated " + rowsUpdated + " records.  This should never happen.");
        }
    }

    /**
     * @param aReportId holds the ID that uniquely identifies a report in the database
     * @return GetUpdateReportDTO object that holds information to show in the "Edit Report" page
     */
    public GetUpdateReportDTO getInfoForUpdateReport(Integer aReportId) {
        // Construct the SQL to get information about this record
        String sql = "select name, priority from reports where id=?";

        // Execute the SQL, generating a read-only SqlRowSet
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        SqlRowSet rs = jt.queryForRowSet(sql, aReportId);

        if (! rs.next() ) {
            throw new RuntimeException("Error in getInfoForUpdateReport():  I did not find any records in the database for this reportId " + aReportId);
        }

        // Get the values out of the SqlRowSet object
        String reportName = rs.getString("name");
        Integer priority = (Integer) rs.getObject("priority");

        // Build and return the DTO object
        GetUpdateReportDTO dto = new GetUpdateReportDTO(aReportId, priority, reportName);
        return dto;
    }


}

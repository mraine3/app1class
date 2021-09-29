package com.lessons.services;

import com.lessons.models.GetPreferenceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Service("com.lessons.services.PreferenceService")
public class PreferenceService {
    private static final Logger logger = LoggerFactory.getLogger(PreferenceService.class);

    @Resource
    private DataSource dataSource;


    /**
     * Get all preferences for this userid
     * @param aUserid holds the unique number that identifies this user
     * @return GetPreferenceDTO object that holds all preferences
     */
    public GetPreferenceDTO getPreferences(int aUserid) {
        GetPreferenceDTO dto;

        // Construct the SQL to get all preferences for this userid
        String sql = "Select show_banner from preferences where userid=?";

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        SqlRowSet rs = jt.queryForRowSet(sql, aUserid);
        if (! rs.next() ) {
            // No records were found in the database for this user.  So, return default values
            dto = new GetPreferenceDTO(true);
        }
        else {
            // one record was found in the database
            boolean showBanner = rs.getBoolean("show_banner");

            dto = new GetPreferenceDTO(showBanner);
        }

        // Return the GetPreferenceDTO object
        return dto;
    }


    /**
     * Set the banner preference in the database
     * @param aUserid holds the unique number that identifies this user
     * @param aBannerValue holds the new banner value to store in the database
     */
    public void setBanner(int aUserid, Boolean aBannerValue) {
        // Construct the SQL to update this record in the database
        String sql = "update preferences set show_banner=? where userid=?";

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        // Execute the sql to update this record
        int rowsUpdated = jt.update(sql, aBannerValue, aUserid);

        if (rowsUpdated == 0) {
            // The record was not found, so insert a record
            sql = "insert into preferences(id, show_banner, userid) " +
                  "values( nextval('seq_table_ids'), ?, ?);";

            // Insert a new record in the database
            int rowsInserted = jt.update(sql, aBannerValue, aUserid);

            if (rowsInserted != 1) {
                // I should have inserted 1 record, but did not
                throw new RuntimeException("Error in setBanner():  I expected to insert 1 record.  Instead, I inserted " + rowsInserted + " records.");
            }
        }
        else if (rowsUpdated > 1) {
            // I updated multiple records -- this should never happen
            throw new RuntimeException("Error in setBanner():  I expected to update 1 record.  Instead, I updated " + rowsUpdated + " records.");
        }
    }
}


package com.lessons.services;

import com.lessons.models.LookupDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Service("com.lessons.services.LookupService")
public class LookupService {
    private static final Logger logger = LoggerFactory.getLogger(LookupService.class);

    @Resource
    private DataSource dataSource;


    /**
     * @param aLookupType holds the name of the lookup type
     * @return true if found in the lookup_type table.  False otherwise
     */
    public boolean doesLookupTypeExist(String aLookupType) {
        // Construct the sql to see if this lookup type is found in the lookup_type table
        String sql = "Select id from lookup_type where name=?";

        // Execute the SQL
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        SqlRowSet rs = jt.queryForRowSet(sql, aLookupType);

        // Returns true if the lookup name is found, false otherwise
        return rs.next();
    }

    /**
     * @param aLookupType holds a string with a lookup type name
     * @return list of LookupDTO objects that hold information about all lookups with this lookup type name
     */
    public List<LookupDTO> getLookupsWithType(String aLookupType, String aOrderBy) {
        String sql = "select l.id, l.name\n" +
                "from lookup l\n" +
                "join lookup_type lt on (lt.id=l.lookup_type)\n" +
                "where lt.name=? ";

        if (StringUtils.isNotEmpty(aOrderBy)) {
            // Append the order by string
            sql = sql + "order by " + aOrderBy;
        }


        BeanPropertyRowMapper<LookupDTO> rowMapper = new BeanPropertyRowMapper<>(LookupDTO.class);
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        // Execute the SQL, generating a list of LookupDTO objects
        // NOTE:  If no lookup records are found, then returns an empty list
        List<LookupDTO> lookups = jt.query(sql, rowMapper, aLookupType);

        // Return the list of LookupDTO objects (or empty list)
        return lookups;
    }
}


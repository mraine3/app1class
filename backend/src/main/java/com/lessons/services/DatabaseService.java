package com.lessons.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.sql.DataSource;

@Service("com.lessons.services.DatabaseService")
public class DatabaseService {

    @Resource
    private DataSource dataSource;

    public Integer getNextId() {
        String sql = "select nextval('seq_table_ids')";
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        Integer nextId = jt.queryForObject(sql, Integer.class);
        return nextId;
    }


    public Integer getStartingVersionValue() {
        return 1;
    }
}

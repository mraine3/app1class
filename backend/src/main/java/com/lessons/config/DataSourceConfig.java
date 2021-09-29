package com.lessons.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${app.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${app.datasource.url}")
    private String url;

    @Value("${app.datasource.username}")
    private String username;

    @Value("${app.datasource.password}")
    private String password;

    @Value("${app.datasource.maxPoolSize:20}")
    private int maxPoolSize;

    @Value("${app.datasource.schema}")
    private String schemaName;

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(this.driverClassName);
        hikariConfig.setJdbcUrl(this.url);
        hikariConfig.setUsername(this.username);
        hikariConfig.setPassword(this.password);
        hikariConfig.setMaximumPoolSize(this.maxPoolSize);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("app1_jdbc_connection_pool");

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        // Initialize the flyway object by setting the data source and schema name
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .load();

        // Use the flyway object to do a migrate on webapp startup
        flyway.migrate();


        return dataSource;
    }

}

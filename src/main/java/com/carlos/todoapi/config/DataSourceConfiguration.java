package com.carlos.todoapi.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Configuration
public class DataSourceConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfiguration.class);


    private final DataSource dataSource;

    @Autowired
    public DataSourceConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            log.info("Database connection successful: {}", connection.getMetaData().getURL());
        } catch (SQLException e) {
            log.error("Database connection failed: {}", e.getMessage(), e);
        }

    }
}
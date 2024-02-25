package com.example.cntrctmgmt.config;

import com.example.cntrctmgmt.constant.db.DBCategoryConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ApplicationConfig implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ApplicationConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        createDatabaseTableIfNotExist();
    }

    private void createDatabaseTableIfNotExist() {
        // create category table;
        this.jdbcTemplate.execute(DBCategoryConst._DB_CREATE_CATEGORY_TABLE_IF_NOT_EXISTS);
    }
}

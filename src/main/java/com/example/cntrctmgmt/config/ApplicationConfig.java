package com.example.cntrctmgmt.config;

import com.example.cntrctmgmt.constant.db.DBCategoryConst;
import com.example.cntrctmgmt.constant.db.DBContractConst;
import com.example.cntrctmgmt.constant.db.DBSubCategoryConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

//@Configuration
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
//        this.jdbcTemplate.execute("PRAGMA foreign_keys=ON;");
//        this.jdbcTemplate.execute("PRAGMA recursive_triggers = OFF;");
        // create category table;
        // category table
        this.jdbcTemplate.execute(DBCategoryConst._DB_CREATE_CATEGORY_TABLE_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBCategoryConst._DB_CREATE_CATEGORY_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBCategoryConst._DB_CREATE_CATEGORY_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);

        // subcategory table
        this.jdbcTemplate.execute(DBSubCategoryConst._DB_CREATE_SUBCATEGORY_TABLE_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBSubCategoryConst._DB_CREATE_SUBCATEGORY_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBSubCategoryConst._DB_CREATE_SUBCATEGORY_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);

        // contract table
        this.jdbcTemplate.execute(DBContractConst._DB_CREATE_CONTRACT_TABLE_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBContractConst._DB_CREATE_CONTRACT_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBContractConst._DB_CREATE_CONTRACT_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);

    }
}

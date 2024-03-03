package com.example.cntrctmgmt.config;

import com.example.cntrctmgmt.constant.db.*;
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

        //sub contract table
        this.jdbcTemplate.execute(DBSubContractConst._DB_CREATE_SUBCONTRACT_TABLE_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBSubContractConst._DB_CREATE_SUBCONTRACT_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBSubContractConst._DB_CREATE_SUBCONTRACT_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);

        // join table for contract and subContract
        this.jdbcTemplate.execute(DBCategorySubCategoryConst._DB_CREATE_CATEGORYSUBCATEGORYJOINER_TABLE_IF_NOT_EXISTS);


        // transaction type table
        this.jdbcTemplate.execute(DBTransactionTypeConst._DB_CREATE_TRANSACTIONTYPE_TABLE_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBTransactionTypeConst._DB_CREATE_TRANSACTIONTYPE_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);
        this.jdbcTemplate.execute(DBTransactionTypeConst._DB_CREATE_TRANSACTIONTYPE_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS);

        // insert default value to the transaction type table
        this.jdbcTemplate.execute(DBTransactionTypeConst._DB_INSERT_INTO_TRANSACTIONTYPE_TABLE_DEFAULT_VALUES);
    }
}

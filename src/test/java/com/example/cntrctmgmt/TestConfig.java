package com.example.cntrctmgmt;

import com.example.cntrctmgmt.constant.db.DBTransactionTypeConst;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestConfig {
    public static void configTransactionTypeTable(JdbcTemplate jdbcTemplate) {
        // transaction type table
        // insert default value
        String sql = "INSERT INTO "+ DBTransactionTypeConst.DB_TABLE_TRANSACTIONTYPE+
                " ("+DBTransactionTypeConst.DB_TABLE_COLUMN_TRANSACTIONTYPETITLE+"" +
                ", "+DBTransactionTypeConst.DB_TABLE_COLUMN_TRANSACTIONMULTIPLIER+")\n" +
                "VALUES ('Credit', -1), ('Debit', 1)";

        jdbcTemplate.execute(sql);
    }
}

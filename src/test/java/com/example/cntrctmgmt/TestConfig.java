package com.example.cntrctmgmt;

import com.example.cntrctmgmt.constant.db.DBCategoryConst;
import com.example.cntrctmgmt.constant.db.DBSubCategoryConst;
import com.example.cntrctmgmt.constant.db.DBTableJoinerCategorySubCategoryConst;
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

    public static void configCategorySubCategoryTable(JdbcTemplate jdbcTemplate) {
        String sqlCategory =  "INSERT INTO "+ DBCategoryConst.DB_TABLE_CATEGORY+
                " ("+DBCategoryConst.DB_TABLE_COLUMN_CATEGORYTITLE+"" +
                ", "+DBCategoryConst.DB_TABLE_COLUMN_ISSOFTCOST+")\n" +
                "VALUES ('Technology', true)";
        jdbcTemplate.execute(sqlCategory);

        String sqlSubCategory =  "INSERT INTO "+ DBSubCategoryConst.DB_TABLE_SUBCATEGORY+
                " ("+DBCategoryConst.DB_TABLE_COLUMN_CATEGORYTITLE+")\n" +
                "VALUES ('Infrastructure improvement')";
        jdbcTemplate.execute(sqlSubCategory);

        String sqlJoinerCategorySubCategory =  "INSERT INTO "+ DBTableJoinerCategorySubCategoryConst.DB_TABLE_JOINER_CATEGORY_SUBCATEGORY+
                " ("+DBTableJoinerCategorySubCategoryConst.DB_TABLE_COLUMN_CATEGORY_FK+"" +
                ", "+DBTableJoinerCategorySubCategoryConst.DB_TABLE_COLUMN_SUBCATEGORY_FK+")\n" +
                "VALUES (1, 1)";

        jdbcTemplate.execute(sqlJoinerCategorySubCategory);

    }
}

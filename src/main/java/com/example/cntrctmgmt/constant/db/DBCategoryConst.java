package com.example.cntrctmgmt.constant.db;

public interface DBCategoryConst {
    String DB_TABLE_CATEGORY = "cmCategory";
    String DB_TABLE_COLUMN_CATEGORY_PK = "pkcmCategory";
    String DB_TABLE_COLUMN_CATEGORYTITLE = "title";
    String DB_TABLE_COLUMN_ISSOFTCOST = "isSoftCost";


    String _DB_CREATE_CATEGORY_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS "+DB_TABLE_CATEGORY+" (\n" +
                    "    "+DB_TABLE_COLUMN_CATEGORY_PK+" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    "+DB_TABLE_COLUMN_CATEGORYTITLE+" VARCHAR(255) UNIQUE,\n" +
                    "    "+DB_TABLE_COLUMN_ISSOFTCOST+" BOOLEAN DEFAULT 0,\n" +
                    "    "+DBCommonTableColumnConstant.DB_TABLE_COLUMN_DATECREATED_SQL+",\n" +
                    "    "+DBCommonTableColumnConstant.DB_TABLE_COLUMN_DATEMODIFIED_SQL+"\n" +
                    ");";
}

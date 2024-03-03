package com.example.cntrctmgmt.constant.db;

public interface DBCategorySubCategoryConst {
    String DB_TABLE_JOINER_CATEGORY_SUBCATEGORY = "cmCategorySubCategoryJoiner";
    String DB_TABLE_COLUMN_CATEGORY_FK = "fk"+DBCategoryConst.DB_TABLE_CATEGORY;
    String DB_TABLE_COLUMN_SUBCATEGORY_FK = "fk"+DBSubCategoryConst.DB_TABLE_SUBCATEGORY;



    String _DB_CREATE_CATEGORYSUBCATEGORYJOINER_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS "+DB_TABLE_JOINER_CATEGORY_SUBCATEGORY+" (\n" +
                    "    "+DB_TABLE_COLUMN_CATEGORY_FK+" INTEGER NOT NULL,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCATEGORY_FK+" INTEGER NOT NULL\n" +
                    ");";


}

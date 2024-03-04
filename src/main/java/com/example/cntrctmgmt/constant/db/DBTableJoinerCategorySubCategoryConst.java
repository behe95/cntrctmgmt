package com.example.cntrctmgmt.constant.db;

public interface DBTableJoinerCategorySubCategoryConst {
    /**
     * Table name
     */
    String DB_TABLE_JOINER_CATEGORY_SUBCATEGORY = "cmCategorySubCategoryJoiner";

    /**
     * Table columns
     */
    String DB_TABLE_COLUMN_CATEGORY_FK = "fk" + DBCategoryConst.DB_TABLE_CATEGORY;
    String DB_TABLE_COLUMN_SUBCATEGORY_FK = "fk" + DBSubCategoryConst.DB_TABLE_SUBCATEGORY;

    /**
     * Table column constraint definitions
     */
    String _DB_TABLE_FK_CONSTRAINT_CATEGORYSUBCATEGORYJOINER_TO_CATEGORY =
            "CONSTRAINT fkConstraint_" +
                    DBTableJoinerCategorySubCategoryConst.DB_TABLE_JOINER_CATEGORY_SUBCATEGORY + "_" +
                    DBCategoryConst.DB_TABLE_CATEGORY + "\n" +
                    "FOREIGN KEY (" + DBTableJoinerCategorySubCategoryConst.DB_TABLE_COLUMN_CATEGORY_FK + ")" +
                    " REFERENCES " + DBCategoryConst.DB_TABLE_CATEGORY + "(" + DBCategoryConst.DB_TABLE_COLUMN_CATEGORY_PK + ")";

    String _DB_TABLE_FK_CONSTRAINT_CATEGORYSUBCATEGORYJOINER_TO_SUBCATEGORY =
            "CONSTRAINT fkConstraint_" +
                    DBTableJoinerCategorySubCategoryConst.DB_TABLE_JOINER_CATEGORY_SUBCATEGORY + "_" +
                    DBSubCategoryConst.DB_TABLE_SUBCATEGORY + "\n" +
                    "FOREIGN KEY (" + DBTableJoinerCategorySubCategoryConst.DB_TABLE_COLUMN_SUBCATEGORY_FK + ")" +
                    " REFERENCES " + DBSubCategoryConst.DB_TABLE_SUBCATEGORY + "(" + DBSubCategoryConst.DB_TABLE_COLUMN_SUBCATEGORY_PK + ")";


    /**
     * SQL script to create table if not exists
     */
    String _DB_CREATE_CATEGORYSUBCATEGORYJOINER_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_JOINER_CATEGORY_SUBCATEGORY + " (\n" +
                    "    " + DB_TABLE_COLUMN_CATEGORY_FK + " INTEGER NOT NULL,\n" +
                    "    " + DB_TABLE_COLUMN_SUBCATEGORY_FK + " INTEGER NOT NULL,\n" +
                    "    " + _DB_TABLE_FK_CONSTRAINT_CATEGORYSUBCATEGORYJOINER_TO_CATEGORY + ",\n" +
                    "    " + _DB_TABLE_FK_CONSTRAINT_CATEGORYSUBCATEGORYJOINER_TO_SUBCATEGORY + "\n" +
                    ");";


}

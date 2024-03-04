package com.example.cntrctmgmt.constant.db;

public interface DBCategoryConst {

    /**
     * table name
     */
    String DB_TABLE_CATEGORY = "cmCategory";

    /**
     * table columns
     */
    String DB_TABLE_COLUMN_CATEGORY_PK = "pkcmCategory";
    String DB_TABLE_COLUMN_CATEGORYTITLE = "title";
    String DB_TABLE_COLUMN_ISSOFTCOST = "isSoftCost";


    String DB_TABLE_COLUMN_DATECREATED = "created";
    String DB_TABLE_COLUMN_DATEMODIFIED = "modified";

    /**
     * Trigger name
     */

    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION = "cmCategory_update_date_after_insertion";
    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE = "cmCategory_update_date_after_update";


    /**
     * SQL script to create database table if not exists
     */
    String _DB_CREATE_CATEGORY_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_CATEGORY + " (\n" +
                    "    " + DB_TABLE_COLUMN_CATEGORY_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    " + DB_TABLE_COLUMN_CATEGORYTITLE + " VARCHAR(255) UNIQUE,\n" +
                    "    " + DB_TABLE_COLUMN_ISSOFTCOST + " BOOLEAN DEFAULT 0,\n" +
                    "    " + DB_TABLE_COLUMN_DATECREATED + " TEXT,\n" +
                    "    " + DB_TABLE_COLUMN_DATEMODIFIED + " TEXT\n" +
                    ");";


    /**
     * SQL script to create trigger if not exists
     */
    String _DB_CREATE_CATEGORY_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS " + DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION + "\n" +
                    "AFTER INSERT ON " + DB_TABLE_CATEGORY + "\n" +
                    "FOR EACH ROW\n" +
                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE " + DB_TABLE_CATEGORY + "\n" +
                    "    SET " + DB_TABLE_COLUMN_DATECREATED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "        ," + DB_TABLE_COLUMN_DATEMODIFIED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE " + DB_TABLE_CATEGORY + "." + DB_TABLE_COLUMN_CATEGORY_PK + " = NEW." + DB_TABLE_COLUMN_CATEGORY_PK + ";\n" +
                    "END;\n";
    String _DB_CREATE_CATEGORY_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS " + DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE + "\n" +
                    "AFTER UPDATE OF " + DB_TABLE_COLUMN_CATEGORYTITLE + "," + DB_TABLE_COLUMN_ISSOFTCOST + " ON " + DB_TABLE_CATEGORY + "\n" +
                    "FOR EACH ROW\n" +
//                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE " + DB_TABLE_CATEGORY + "\n" +
                    "    SET " + DB_TABLE_COLUMN_DATEMODIFIED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE " + DB_TABLE_CATEGORY + "." + DB_TABLE_COLUMN_CATEGORY_PK + " = NEW." + DB_TABLE_COLUMN_CATEGORY_PK + ";\n" +
                    "END;\n";
}

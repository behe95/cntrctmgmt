package com.example.cntrctmgmt.constant.db;

public interface DBSubCategoryConst {
    String DB_TABLE_SUBCATEGORY = "cmSubCategory";
    String DB_TABLE_COLUMN_SUBCATEGORY_PK = "pkcmSubCategory";
    String DB_TABLE_COLUMN_SUBCATEGORYTITLE = "title";


    String DB_TABLE_COLUMN_DATECREATED = "created";
    String DB_TABLE_COLUMN_DATEMODIFIED = "modified";

    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION = "cmSubCategory_update_date_after_insertion";
    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE = "cmSubCategory_update_date_after_update";


    String _DB_CREATE_SUBCATEGORY_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_SUBCATEGORY + " (\n" +
                    "    " + DB_TABLE_COLUMN_SUBCATEGORY_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    " + DB_TABLE_COLUMN_SUBCATEGORYTITLE + " VARCHAR(255) UNIQUE,\n" +
                    "    " + DB_TABLE_COLUMN_DATECREATED + " TEXT,\n" +
                    "    " + DB_TABLE_COLUMN_DATEMODIFIED + " TEXT\n" +
                    ");";


    String _DB_CREATE_SUBCATEGORY_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS " + DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION + "\n" +
                    "AFTER INSERT ON " + DB_TABLE_SUBCATEGORY + "\n" +
                    "FOR EACH ROW\n" +
                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE " + DB_TABLE_SUBCATEGORY + "\n" +
                    "    SET " + DB_TABLE_COLUMN_DATECREATED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "        ," + DB_TABLE_COLUMN_DATEMODIFIED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE " + DB_TABLE_SUBCATEGORY + "." + DB_TABLE_COLUMN_SUBCATEGORY_PK + " = NEW." + DB_TABLE_COLUMN_SUBCATEGORY_PK + ";\n" +
                    "END;\n";
    String _DB_CREATE_SUBCATEGORY_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS " + DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE + "\n" +
                    "AFTER UPDATE OF " + DB_TABLE_COLUMN_SUBCATEGORYTITLE + " ON " + DB_TABLE_SUBCATEGORY + "\n" +
                    "FOR EACH ROW\n" +
//                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE " + DB_TABLE_SUBCATEGORY + "\n" +
                    "    SET " + DB_TABLE_COLUMN_DATEMODIFIED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE " + DB_TABLE_SUBCATEGORY + "." + DB_TABLE_COLUMN_SUBCATEGORY_PK + " = NEW." + DB_TABLE_COLUMN_SUBCATEGORY_PK + ";\n" +
                    "END;\n";
}

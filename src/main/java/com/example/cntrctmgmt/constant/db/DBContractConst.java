package com.example.cntrctmgmt.constant.db;

public interface DBContractConst {
    /**
     * table name
     */
    String DB_TABLE_CONTRACT = "cmContract";

    /**
     * table columns
     */
    String DB_TABLE_COLUMN_CONTRACT_PK = "pkcmContract";
    String DB_TABLE_COLUMN_CONTRACTTITLE = "title";


    String DB_TABLE_COLUMN_DATECREATED = "created";
    String DB_TABLE_COLUMN_DATEMODIFIED = "modified";

    /**
     * Trigger name
     */
    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION = "cmContract_update_date_after_insertion";
    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE = "cmContract_update_date_after_update";


    /**
     * SQL script to create database table if not exists
     */
    String _DB_CREATE_CONTRACT_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_CONTRACT + " (\n" +
                    "    " + DB_TABLE_COLUMN_CONTRACT_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    " + DB_TABLE_COLUMN_CONTRACTTITLE + " VARCHAR(255) UNIQUE,\n" +
                    "    " + DB_TABLE_COLUMN_DATECREATED + " TEXT,\n" +
                    "    " + DB_TABLE_COLUMN_DATEMODIFIED + " TEXT\n" +
                    ");";


    /**
     * SQL script to create trigger if not exists
     */
    String _DB_CREATE_CONTRACT_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS " + DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION + "\n" +
                    "AFTER INSERT ON " + DB_TABLE_CONTRACT + "\n" +
                    "FOR EACH ROW\n" +
                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE " + DB_TABLE_CONTRACT + "\n" +
                    "    SET " + DB_TABLE_COLUMN_DATECREATED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "        ," + DB_TABLE_COLUMN_DATEMODIFIED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE " + DB_TABLE_CONTRACT + "." + DB_TABLE_COLUMN_CONTRACT_PK + " = NEW." + DB_TABLE_COLUMN_CONTRACT_PK + ";\n" +
                    "END;\n";
    String _DB_CREATE_CONTRACT_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS " + DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE + "\n" +
                    "AFTER UPDATE OF " + DB_TABLE_COLUMN_CONTRACTTITLE + " ON " + DB_TABLE_CONTRACT + "\n" +
                    "FOR EACH ROW\n" +
//                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE " + DB_TABLE_CONTRACT + "\n" +
                    "    SET " + DB_TABLE_COLUMN_DATEMODIFIED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE " + DB_TABLE_CONTRACT + "." + DB_TABLE_COLUMN_CONTRACT_PK + " = NEW." + DB_TABLE_COLUMN_CONTRACT_PK + ";\n" +
                    "END;\n";
}



package com.example.cntrctmgmt.constant.db;

public interface DBTransactionTypeConst {
    String DB_TABLE_TRANSACTIONTYPE = "cmTransactionType";
    String DB_TABLE_COLUMN_TRANSACTIONTYPE_PK = "pkcmTransactionType";
    String DB_TABLE_COLUMN_TRANSACTIONTYPETITLE = "title";
    String DB_TABLE_COLUMN_TRANSACTIONMULTIPLIER = "transactionMultiplier";


    String DB_TABLE_COLUMN_DATECREATED = "created";
    String DB_TABLE_COLUMN_DATEMODIFIED = "modified";

    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION = "cmTransactionType_update_date_after_insertion";
    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE = "cmTransactionType_update_date_after_update";


    String _DB_CREATE_TRANSACTIONTYPE_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_TRANSACTIONTYPE + " (\n" +
                    "    " + DB_TABLE_COLUMN_TRANSACTIONTYPE_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    " + DB_TABLE_COLUMN_TRANSACTIONTYPETITLE + " VARCHAR(255) NOT NULL UNIQUE,\n" +
                    "    " + DB_TABLE_COLUMN_TRANSACTIONMULTIPLIER + " INTEGER NOT NULL,\n" +
                    "    " + DB_TABLE_COLUMN_DATECREATED + " TEXT,\n" +
                    "    " + DB_TABLE_COLUMN_DATEMODIFIED + " TEXT\n" +
                    ");";


    String _DB_CREATE_TRANSACTIONTYPE_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS " + DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION + "\n" +
                    "AFTER INSERT ON " + DB_TABLE_TRANSACTIONTYPE + "\n" +
                    "FOR EACH ROW\n" +
                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE " + DB_TABLE_TRANSACTIONTYPE + "\n" +
                    "    SET " + DB_TABLE_COLUMN_DATECREATED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "        ," + DB_TABLE_COLUMN_DATEMODIFIED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE " + DB_TABLE_TRANSACTIONTYPE + "." + DB_TABLE_COLUMN_TRANSACTIONTYPE_PK + " = NEW." + DB_TABLE_COLUMN_TRANSACTIONTYPE_PK + ";\n" +
                    "END;\n";
    String _DB_CREATE_TRANSACTIONTYPE_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS " + DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE + "\n" +
                    "AFTER UPDATE OF "
                    + DB_TABLE_COLUMN_TRANSACTIONTYPETITLE + ", "
                    + DB_TABLE_COLUMN_TRANSACTIONMULTIPLIER
                    + " ON " + DB_TABLE_TRANSACTIONTYPE + "\n" +
                    "FOR EACH ROW\n" +
//                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE " + DB_TABLE_TRANSACTIONTYPE + "\n" +
                    "    SET " + DB_TABLE_COLUMN_DATEMODIFIED + " = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE " + DB_TABLE_TRANSACTIONTYPE + "." + DB_TABLE_COLUMN_TRANSACTIONTYPE_PK + " = NEW." + DB_TABLE_COLUMN_TRANSACTIONTYPE_PK + ";\n" +
                    "END;\n";


    String _DB_INSERT_INTO_TRANSACTIONTYPE_TABLE_DEFAULT_VALUES =
            "INSERT OR IGNORE INTO " + DB_TABLE_TRANSACTIONTYPE + " ("
                    + DB_TABLE_COLUMN_TRANSACTIONTYPETITLE + ", "
                    + DB_TABLE_COLUMN_TRANSACTIONMULTIPLIER + ")\n" +
                    "VALUES " +
                    "('Credit', -1), " +
                    "('Debit', 1)";
}

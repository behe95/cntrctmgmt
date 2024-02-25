package com.example.cntrctmgmt.constant.db;

public interface DBCommonTableColumnConstant {
    String DB_TABLE_COLUMN_DATECREATED = "created";
    String DB_TABLE_COLUMN_DATEMODIFIED= "modified";
    String DB_TABLE_COLUMN_DATECREATED_SQL = DB_TABLE_COLUMN_DATECREATED + " TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime'))";
    String DB_TABLE_COLUMN_DATEMODIFIED_SQL = DB_TABLE_COLUMN_DATEMODIFIED + " TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime'))";
}

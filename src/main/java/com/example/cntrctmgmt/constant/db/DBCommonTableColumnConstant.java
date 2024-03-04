package com.example.cntrctmgmt.constant.db;

public interface DBCommonTableColumnConstant {

    /**
     * JPA mapping issue.
     * JPA map entities created and modified properties
     * which is null unless value initialized
     * Instead use a trigger on the table
     */
    @Deprecated
    String DB_TABLE_COLUMN_DATECREATED = "created";

    @Deprecated
    String DB_TABLE_COLUMN_DATEMODIFIED = "modified";

    @Deprecated
    String DB_TABLE_COLUMN_DATECREATED_SQL = DB_TABLE_COLUMN_DATECREATED + " TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime'))";

    @Deprecated
    String DB_TABLE_COLUMN_DATEMODIFIED_SQL = DB_TABLE_COLUMN_DATEMODIFIED + " TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime'))";
}

package com.example.cntrctmgmt.constant.db;

public interface DBSubContractConst {
    String DB_TABLE_SUBCONTRACT = "cmSubContract";
    String DB_TABLE_COLUMN_SUBCONTRACT_PK = "pkcmSubContract";
    String DB_TABLE_COLUMN_SUBCONTRACTTITLE = "title";
    String DB_TABLE_COLUMN_SUBCONTRACT_ORDERNUMBER = "orderNumber";
    String DB_TABLE_COLUMN_SUBCONTRACT_SUBCONTRACTNUMBER = "subContractNumber";
    String DB_TABLE_COLUMN_SUBCONTRACT_TRANSACTIONTYPE = "transactionType";
    String DB_TABLE_COLUMN_SUBCONTRACT_DESCRIPTION = "description";
    String DB_TABLE_COLUMN_SUBCONTRACT_AMOUNT = "amount";
    String DB_TABLE_COLUMN_SUBCONTRACT_STARTDATE = "startDate";
    String DB_TABLE_COLUMN_SUBCONTRACT_ENDDATE = "endDate";


    String DB_TABLE_COLUMN_DATECREATED = "created";
    String DB_TABLE_COLUMN_DATEMODIFIED= "modified";

    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION = "cmSubContract_update_date_after_insertion";
    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE = "cmSubContract_update_date_after_update";



    String _DB_CREATE_SUBCONTRACT_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS "+DB_TABLE_SUBCONTRACT+" (\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_PK+" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACTTITLE+" VARCHAR(255) UNIQUE,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_ORDERNUMBER+" INTEGER,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_SUBCONTRACTNUMBER+" INTEGER,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_TRANSACTIONTYPE+" INTEGER,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_DESCRIPTION+" VARCHAR(255),\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_AMOUNT+" DECIMAL,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_STARTDATE +" TEXT,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_ENDDATE +" TEXT,\n" +
                    "    "+DB_TABLE_COLUMN_DATECREATED +" TEXT,\n" +
                    "    "+DB_TABLE_COLUMN_DATEMODIFIED+" TEXT\n" +
                    ");";


    String _DB_CREATE_SUBCONTRACT_TABLE_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS "+ DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION +"\n" +
                    "AFTER INSERT ON " + DB_TABLE_SUBCONTRACT + "\n" +
                    "FOR EACH ROW\n" +
                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE "+DB_TABLE_SUBCONTRACT+"\n" +
                    "    SET "+DB_TABLE_COLUMN_DATECREATED+" = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "        ,"+DB_TABLE_COLUMN_DATEMODIFIED+" = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE "+DB_TABLE_SUBCONTRACT+"."+DB_TABLE_COLUMN_SUBCONTRACT_PK+" = NEW."+DB_TABLE_COLUMN_SUBCONTRACT_PK+";\n" +
                    "END;\n";
    String _DB_CREATE_SUBCONTRACT_TABLE_MODIFIED_DATE_UPDATE_TRIGGER_IF_NOT_EXISTS =
            "CREATE TRIGGER IF NOT EXISTS "+ DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE +"\n" +
                    "AFTER UPDATE OF "+ DB_TABLE_COLUMN_SUBCONTRACTTITLE
                    +"," + DB_TABLE_COLUMN_SUBCONTRACT_ORDERNUMBER
                    +"," + DB_TABLE_COLUMN_SUBCONTRACT_SUBCONTRACTNUMBER
                    +"," + DB_TABLE_COLUMN_SUBCONTRACT_TRANSACTIONTYPE
                    +"," + DB_TABLE_COLUMN_SUBCONTRACT_DESCRIPTION
                    +"," + DB_TABLE_COLUMN_SUBCONTRACT_AMOUNT
                    +"," + DB_TABLE_COLUMN_SUBCONTRACT_STARTDATE
                    +"," + DB_TABLE_COLUMN_SUBCONTRACT_ENDDATE
                    +" ON " + DB_TABLE_SUBCONTRACT + "\n" +
                    "FOR EACH ROW\n" +
//                    "WHEN NEW.created IS NULL\n" +
                    "BEGIN\n" +
                    "    UPDATE "+DB_TABLE_SUBCONTRACT+"\n" +
                    "    SET "+DB_TABLE_COLUMN_DATEMODIFIED+" = strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')\n" +
                    "    WHERE "+DB_TABLE_SUBCONTRACT+"."+DB_TABLE_COLUMN_SUBCONTRACT_PK+" = NEW."+DB_TABLE_COLUMN_SUBCONTRACT_PK+";\n" +
                    "END;\n";
}

package com.example.cntrctmgmt.constant.db;

public interface DBSubContractConst {
    String DB_TABLE_SUBCONTRACT = "cmSubContract";
    String DB_TABLE_COLUMN_SUBCONTRACT_PK = "pkcmSubContract";
    String DB_TABLE_COLUMN_SUBCONTRACTTITLE = "title";
    String DB_TABLE_COLUMN_SUBCONTRACT_ORDERNUMBER = "orderNumber";
    String DB_TABLE_COLUMN_SUBCONTRACT_SUBCONTRACTNUMBER = "subContractNumber";
    String DB_TABLE_COLUMN_SUBCONTRACT_TRANSACTIONTYPE_FK = "fk"+DBTransactionTypeConst.DB_TABLE_TRANSACTIONTYPE;
    String DB_TABLE_COLUMN_SUBCONTRACT_CONTRACT_FK = "fk"+DBContractConst.DB_TABLE_CONTRACT;
    String DB_TABLE_COLUMN_SUBCONTRACT_CATEGORY_FK = "fk"+DBCategoryConst.DB_TABLE_CATEGORY;
    String DB_TABLE_COLUMN_SUBCONTRACT_SUBCATEGORY_FK = "fk"+DBSubCategoryConst.DB_TABLE_SUBCATEGORY;
    String DB_TABLE_COLUMN_SUBCONTRACT_DESCRIPTION = "description";
    String DB_TABLE_COLUMN_SUBCONTRACT_AMOUNT = "amount";
    String DB_TABLE_COLUMN_SUBCONTRACT_STARTDATE = "startDate";
    String DB_TABLE_COLUMN_SUBCONTRACT_ENDDATE = "endDate";


    String DB_TABLE_COLUMN_DATECREATED = "created";
    String DB_TABLE_COLUMN_DATEMODIFIED= "modified";

    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_INSERTION = "cmSubContract_update_date_after_insertion";
    String DB_TABLE_TRIGGER_UPDATE_DATE_AFTER_RECORD_UPDATE = "cmSubContract_update_date_after_update";

    String _DB_TABLE_FK_CONSTRAINT_SUBCONTRACT_TO_CONTRACT =
            "CONSTRAINT fkConstraint_"+
            DBSubContractConst.DB_TABLE_SUBCONTRACT+"_"+
            DBContractConst.DB_TABLE_CONTRACT+"\n" +
            "FOREIGN KEY ("+DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_CONTRACT_FK+")" +
            " REFERENCES "+DBContractConst.DB_TABLE_CONTRACT+"("+DBContractConst.DB_TABLE_COLUMN_CONTRACT_PK+")";

    String _DB_TABLE_FK_CONSTRAINT_SUBCONTRACT_TO_TRANSACTIONTYPE =
            "CONSTRAINT fkConstraint_"+
                    DBSubContractConst.DB_TABLE_SUBCONTRACT+"_"+
                    DBTransactionTypeConst.DB_TABLE_TRANSACTIONTYPE+"\n" +
                    "FOREIGN KEY ("+DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_TRANSACTIONTYPE_FK+")" +
                    " REFERENCES "+DBTransactionTypeConst.DB_TABLE_TRANSACTIONTYPE+"("+DBTransactionTypeConst.DB_TABLE_COLUMN_TRANSACTIONTYPE_PK+")";

    String _DB_TABLE_FK_CONSTRAINT_SUBCONTRACT_TO_CATEGORY =
            "CONSTRAINT fkConstraint_"+
                    DBSubContractConst.DB_TABLE_SUBCONTRACT+"_"+
                    DBCategoryConst.DB_TABLE_CATEGORY+"\n" +
                    "FOREIGN KEY ("+DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_CATEGORY_FK+")" +
                    " REFERENCES "+DBCategoryConst.DB_TABLE_CATEGORY+"("+DBCategoryConst.DB_TABLE_COLUMN_CATEGORY_PK+")";

    String _DB_TABLE_FK_CONSTRAINT_SUBCONTRACT_TO_SUBCATEGORY =
            "CONSTRAINT fkConstraint_"+
                    DBSubContractConst.DB_TABLE_SUBCONTRACT+"_"+
                    DBSubCategoryConst.DB_TABLE_SUBCATEGORY+"\n" +
                    "FOREIGN KEY ("+DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_SUBCATEGORY_FK+")" +
                    " REFERENCES "+DBSubCategoryConst.DB_TABLE_SUBCATEGORY+"("+DBSubCategoryConst.DB_TABLE_COLUMN_SUBCATEGORY_PK+")";


    String _DB_CREATE_SUBCONTRACT_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS "+DB_TABLE_SUBCONTRACT+" (\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_PK+" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACTTITLE+" VARCHAR(255) UNIQUE,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_ORDERNUMBER+" INTEGER,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_SUBCONTRACTNUMBER+" VARCHAR(255),\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_TRANSACTIONTYPE_FK+" INTEGER NOT NULL,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_CONTRACT_FK+" INTEGER NOT NULL,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_CATEGORY_FK+" INTEGER NOT NULL,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_SUBCATEGORY_FK+" INTEGER NOT NULL,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_DESCRIPTION+" VARCHAR(255),\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_AMOUNT+" DECIMAL,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_STARTDATE +" TEXT,\n" +
                    "    "+DB_TABLE_COLUMN_SUBCONTRACT_ENDDATE +" TEXT,\n" +
                    "    "+DB_TABLE_COLUMN_DATECREATED +" TEXT,\n" +
                    "    "+DB_TABLE_COLUMN_DATEMODIFIED+" TEXT,\n" +
                    "    "+_DB_TABLE_FK_CONSTRAINT_SUBCONTRACT_TO_CONTRACT +",\n" +
                    "    "+_DB_TABLE_FK_CONSTRAINT_SUBCONTRACT_TO_TRANSACTIONTYPE +",\n" +
                    "    "+_DB_TABLE_FK_CONSTRAINT_SUBCONTRACT_TO_CATEGORY +",\n" +
                    "    "+_DB_TABLE_FK_CONSTRAINT_SUBCONTRACT_TO_SUBCATEGORY +"\n" +
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
                    +"," + DB_TABLE_COLUMN_SUBCONTRACT_TRANSACTIONTYPE_FK
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

package com.example.cntrctmgmt.entities;

import com.example.cntrctmgmt.constant.db.DBTransactionTypeConst;
import jakarta.persistence.*;

@Entity
@Table(name = DBTransactionTypeConst.DB_TABLE_TRANSACTIONTYPE)
public class TransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = DBTransactionTypeConst.DB_TABLE_COLUMN_TRANSACTIONTYPE_PK)
    private int id;

    @Column(name = DBTransactionTypeConst.DB_TABLE_COLUMN_TRANSACTIONTYPETITLE)
    private String title;

    @Column(name = DBTransactionTypeConst.DB_TABLE_COLUMN_TRANSACTIONMULTIPLIER)
    private int multiplier;

    public TransactionType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}

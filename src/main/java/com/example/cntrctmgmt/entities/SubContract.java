package com.example.cntrctmgmt.entities;

import com.example.cntrctmgmt.constant.db.DBContractConst;
import com.example.cntrctmgmt.constant.db.DBSubContractConst;
import com.example.cntrctmgmt.constant.db.DBTransactionTypeConst;
import jakarta.persistence.*;
import javafx.beans.property.*;

import java.time.LocalDateTime;

@Entity
@Table(name = DBSubContractConst.DB_TABLE_SUBCONTRACT)
public class SubContract {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private IntegerProperty orderNumber = new SimpleIntegerProperty();
    private StringProperty subContractNumber = new SimpleStringProperty();

    private ObjectProperty<TransactionType> transactionType = new SimpleObjectProperty<>();

    private StringProperty description = new SimpleStringProperty();
    private DoubleProperty amount = new SimpleDoubleProperty();
    private ObjectProperty<LocalDateTime> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> endDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> created = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> modified = new SimpleObjectProperty<>();

    @Transient
    private ObjectProperty<Contract> contract = new SimpleObjectProperty<>();

    public SubContract() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_PK)
    public int getId() {
        return id.get();
    }

    @Transient
    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACTTITLE)
    public String getTitle() {
        return title.get();
    }

    @Transient
    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_ORDERNUMBER)
    public int getOrderNumber() {
        return orderNumber.get();
    }

    @Transient
    public IntegerProperty orderNumberProperty() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber.set(orderNumber);
    }

    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_SUBCONTRACTNUMBER)
    public String getSubContractNumber() {
        return subContractNumber.get();
    }

    @Transient
    public StringProperty subContractNumberProperty() {
        return subContractNumber;
    }

    public void setSubContractNumber(String subContractNumber) {
        this.subContractNumber.set(subContractNumber);
    }


    @OneToOne
    @JoinColumn(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_TRANSACTIONTYPE_FK)
    public TransactionType getTransactionType() {
        return transactionType.get();
    }

    @Transient
    public ObjectProperty<TransactionType> transactionTypeProperty() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType.set(transactionType);
    }

    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_DESCRIPTION)
    public String getDescription() {
        return description.get();
    }

    @Transient
    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_AMOUNT)
    public double getAmount() {
        return amount.get();
    }

    @Transient
    public DoubleProperty amountProperty() {
        return amount;
    }


    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_STARTDATE)
    public LocalDateTime getStartDate() {
        return startDate.get();
    }

    @Transient
    public ObjectProperty<LocalDateTime> startDateProperty() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate.set(startDate);
    }

    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_ENDDATE)
    public LocalDateTime getEndDate() {
        return endDate.get();
    }

    @Transient
    public ObjectProperty<LocalDateTime> endDateProperty() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate.set(endDate);
    }

    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_DATECREATED)
    public LocalDateTime getCreated() {
        return created.get();
    }

    @Transient
    public ObjectProperty<LocalDateTime> createdProperty() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created.set(created);
    }


    @Column(name = DBSubContractConst.DB_TABLE_COLUMN_DATEMODIFIED)
    public LocalDateTime getModified() {
        return modified.get();
    }

    @Transient
    public ObjectProperty<LocalDateTime> modifiedProperty() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified.set(modified);
    }

    @ManyToOne
    @JoinColumn(name = DBSubContractConst.DB_TABLE_COLUMN_SUBCONTRACT_CONTRACT_FK)
    public Contract getContract() {
        return contract.get();
    }

    @Transient
    public ObjectProperty<Contract> contractProperty() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract.set(contract);
    }
}

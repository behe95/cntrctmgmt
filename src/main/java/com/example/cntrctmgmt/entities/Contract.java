package com.example.cntrctmgmt.entities;

import com.example.cntrctmgmt.constant.db.DBCategoryConst;
import com.example.cntrctmgmt.constant.db.DBContractConst;
import com.example.cntrctmgmt.constant.db.DBSubCategoryConst;
import jakarta.persistence.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DBContractConst.DB_TABLE_CONTRACT)
public class Contract {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private ObjectProperty<LocalDateTime> created = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> modified = new SimpleObjectProperty<>();

    @Transient  // ignore , use getter instead
    private ObservableList<SubContract> subContracts = FXCollections.observableList(new ArrayList<>());

    public Contract() {
    }

    public Contract(String title) {
        this.setTitle(title);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = DBContractConst.DB_TABLE_COLUMN_CONTRACT_PK)
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

    @Column(name = DBCategoryConst.DB_TABLE_COLUMN_CATEGORYTITLE)
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

    @Column(name = DBSubCategoryConst.DB_TABLE_COLUMN_DATECREATED)
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

    @Column(name = DBContractConst.DB_TABLE_COLUMN_DATEMODIFIED)
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


    @OneToMany(mappedBy = "contract", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    public List<SubContract> getSubContracts() {
        return subContracts;
    }

    public void setSubContracts(List<SubContract> subContracts) {
        this.subContracts = FXCollections.observableList(subContracts);
    }

    public void addSubContract(SubContract subContract) {
        this.subContracts.add(subContract);
    }
}

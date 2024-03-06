package com.example.cntrctmgmt.entities;

import com.example.cntrctmgmt.constant.db.DBCategoryConst;
import com.example.cntrctmgmt.constant.db.DBTableJoinerCategorySubCategoryConst;
import jakarta.persistence.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DBCategoryConst.DB_TABLE_CATEGORY)
public class Category {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private BooleanProperty softCost = new SimpleBooleanProperty();


    private ObjectProperty<LocalDateTime> created = new SimpleObjectProperty<>();

    private ObjectProperty<LocalDateTime> modified = new SimpleObjectProperty<>();

    @Transient
    private ObservableList<SubCategory> subCategoryList = FXCollections.observableList(new ArrayList<>());


    /**
     * Default constructor for JPA
     */
    public Category() {
    }

    public Category(String title, boolean isSoftCost) {
        this.setTitle(title);
        this.setSoftCost(isSoftCost);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = DBCategoryConst.DB_TABLE_COLUMN_CATEGORY_PK)
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

    @Column(name = DBCategoryConst.DB_TABLE_COLUMN_ISSOFTCOST)
    public boolean getSoftCost() {
        return softCost.get();
    }

    @Transient
    public BooleanProperty softCostProperty() {
        return softCost;
    }

    public void setSoftCost(boolean isSoftCost) {
        this.softCost.set(isSoftCost);
    }

    @Column(name = DBCategoryConst.DB_TABLE_COLUMN_DATECREATED)
//    @Convert(converter = DatabaseDateTimeConverter.class)
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

    @Column(name = DBCategoryConst.DB_TABLE_COLUMN_DATEMODIFIED)
//    @Convert(converter = DatabaseDateTimeConverter.class)
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


    @ManyToMany
    @JoinTable(
            name = DBTableJoinerCategorySubCategoryConst.DB_TABLE_JOINER_CATEGORY_SUBCATEGORY
            , joinColumns = @JoinColumn(name = DBTableJoinerCategorySubCategoryConst.DB_TABLE_COLUMN_CATEGORY_FK)
            , inverseJoinColumns = @JoinColumn(name = DBTableJoinerCategorySubCategoryConst.DB_TABLE_COLUMN_SUBCATEGORY_FK)
    )
    @Fetch(FetchMode.JOIN)
    public ObservableList<SubCategory> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(List<SubCategory> subCategoryList) {
        this.subCategoryList = FXCollections.observableList(subCategoryList);
    }

    public void addToSubCategoryList(SubCategory subCategory) {
        this.subCategoryList.add(subCategory);
    }

}

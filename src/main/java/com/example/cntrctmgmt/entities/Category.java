package com.example.cntrctmgmt.entities;

import com.example.cntrctmgmt.constant.db.DBCategoryConst;
import com.example.cntrctmgmt.constant.db.DBCommonTableColumnConstant;
import com.example.cntrctmgmt.util.DatabaseDateTimeConverter;
import jakarta.persistence.*;
import javafx.beans.property.*;

import java.time.LocalDateTime;

@Entity
@Table(name = DBCategoryConst.DB_TABLE_CATEGORY)
public class Category {
    private IntegerProperty pkcmCategory = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private BooleanProperty softCost = new SimpleBooleanProperty();



    private ObjectProperty<LocalDateTime> created = new SimpleObjectProperty<>();

    private ObjectProperty<LocalDateTime> modified = new SimpleObjectProperty<>();


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
    public int getPkcmCategory() {
        return pkcmCategory.get();
    }

    @Transient
    public IntegerProperty pkcmCategoryProperty() {
        return pkcmCategory;
    }

    public void setPkcmCategory(int pkcmCategory) {
        this.pkcmCategory.set(pkcmCategory);
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

    @Override
    public String toString() {
        return "Category{" +
                "pkcmCategory=" + pkcmCategory +
                ", title=" + title +
                ", softCost=" + softCost +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}

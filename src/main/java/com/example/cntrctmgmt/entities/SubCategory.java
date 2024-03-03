package com.example.cntrctmgmt.entities;

import com.example.cntrctmgmt.constant.db.DBSubCategoryConst;
import jakarta.persistence.*;
import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DBSubCategoryConst.DB_TABLE_SUBCATEGORY)
public class SubCategory {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private ObjectProperty<LocalDateTime> created = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> modified = new SimpleObjectProperty<>();

    private List<Category> categoryList = new ArrayList<>();

    /**
     * Default constructor for JPA
     */
    public SubCategory() {}

    public SubCategory(String title) {
        this.setTitle(title);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = DBSubCategoryConst.DB_TABLE_COLUMN_SUBCATEGORY_PK)
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

    @Column(name = DBSubCategoryConst.DB_TABLE_COLUMN_SUBCATEGORYTITLE)
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

    @Column(name = DBSubCategoryConst.DB_TABLE_COLUMN_DATEMODIFIED)
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

    @ManyToMany(mappedBy = "subCategoryList")
    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}

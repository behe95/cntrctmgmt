package com.example.cntrctmgmt.controllers;

import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.InvalidInputException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Controller
public abstract class SettingsCategorySubCategoryTemplate<P, C> {

    // service class to interact with service repository
    protected final CategoryService categoryService;
    // service class to interact with child repository
    protected final SubCategoryService subCategoryService;

    // contains all the children
    // this is a reference list which is not tied to any rendered nodes
    protected final ObservableList<P> parentObservableList;

    // contains all the children
    protected final ObservableList<C> childObservableList;

    // currently selected parent by the end user
    protected final ObjectProperty<P> currentSelected = new SimpleObjectProperty<>();

    // contains available children for each parent
    protected final HashMap<P, ObservableList<C>> availableToBeAssigned;

    // List view to display all the parents
    @FXML
    protected ListView<P> listViewParent;

    // List view to display all the available children
    // that can be assigned to any selected parent
    @FXML
    protected ListView<C> listViewAvailableChildren;

    // List view to display all the assigned children to
    // the currently selected parents
    @FXML
    protected ListView<C> listViewAssignedChildren;
    // button icon to add a new child
    @FXML
    protected Button btnAddParent;

    // button icon to delete one or multiple selected children
    @FXML
    protected Button btnDeleteParent;

    // button icon to save or update single or multiple children
    @FXML
    protected Button btnSaveParent;


    @FXML
    protected void initialize() {
        // populate ListView for all the parents
        listViewParent.setItems(parentObservableList);
        // select the first item for the first time by default
        listViewParent.getSelectionModel().selectFirst();
        currentSelected.set(listViewParent.getSelectionModel().getSelectedItem());

        // populate list views
        setupCellFactoryListViewParent();
        setupCellFactoryListViewAvailableChildren();
        setupCellFactoryListViewAssignedChildren();
    }

    public SettingsCategorySubCategoryTemplate(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;

        // data containers
        this.parentObservableList = FXCollections.observableArrayList(new ArrayList<>());
        this.childObservableList = FXCollections.observableArrayList(new ArrayList<>());
        this.availableToBeAssigned = new HashMap<>();
    }



    /**
     * This method assign a child to a parent
     *
     * @param child Child to assign
     */
    protected void assign(C child) {
        P parent = currentSelected.get();
        // remove the child from the available children list
        availableToBeAssigned.get(parent).remove(child);
        if (parent instanceof Category category && child instanceof SubCategory subCategory) {
            // assign the child to the selected parent
            category.getSubCategoryList().add(subCategory);
            // assign the selected parent to the child that has been assigned
            // to make association in the persistence context
            subCategory.getCategoryList().add(category);
        } else if (parent instanceof SubCategory subCategory && child instanceof Category category) {
            // assign the parent to the selected child
            subCategory.getCategoryList().add(category);
            // assign the selected child to the parent that has been assigned
            // to make association in the persistence context
            category.getSubCategoryList().add(subCategory);
        }
    }

    /**
     * This method un-assign a child from a parent
     *
     * @param child Child to un-assign
     */
    protected void unassign(C child) {
        P parent = currentSelected.get();
        // add the child to the list of available children
        availableToBeAssigned.get(parent).add(child);
        if (parent instanceof Category category && child instanceof SubCategory subCategory) {
            // remove the child from the assigned parent list
            category.getSubCategoryList().remove(subCategory);
            // remove the selected parent from the child that has been un-assigned
            // to remove the association in the persistence context
            subCategory.getCategoryList().remove(category);
        } else if (parent instanceof SubCategory subCategory && child instanceof Category category) {
            // remove the category from the assigned category list
            subCategory.getCategoryList().remove(category);
            // remove the selected child from the parent that has been un-assigned
            // to remove the association in the persistence context
            category.getSubCategoryList().remove(subCategory);
        }
    }

    protected void addParent(P parent) {
        listViewParent.itemsProperty().get().add(parent);
        // clear any previous selection
        listViewParent.getSelectionModel().clearSelection();
        // select new item
        listViewParent.getSelectionModel().select(parent);
        currentSelected.set(parent);


        int newAddedSubCategoryIdx = listViewParent.itemsProperty().get().size() - 1;
        listViewParent.layout();
        listViewParent.scrollTo(newAddedSubCategoryIdx);
        listViewParent.getFocusModel().focus(newAddedSubCategoryIdx);


        listViewParent.edit(newAddedSubCategoryIdx);
    }

    protected boolean saveOrUpdateParent() {

        boolean isSaved = true;
        try {

            onSaveOrUpdateParentValidate();

            onSaveOrUpdateParent();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(onParentSaveOrUpdateSuccessShow());
            alert.showAndWait();
        } catch (DuplicateEntityException e) {
            isSaved = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(onParentSaveOrUpdateFailureShow() + " " + e.getMessage());
            alert.showAndWait();
        } catch (InvalidInputException e) {
            isSaved = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return isSaved;
    }

    protected abstract void onSaveOrUpdateParent() throws DuplicateEntityException;

    protected abstract void onSaveOrUpdateParentValidate() throws InvalidInputException;

    protected abstract String onParentSaveOrUpdateSuccessShow();

    protected abstract String onParentSaveOrUpdateFailureShow();


    protected void deleteParent(ActionEvent actionEvent) {
        try {
            onDeleteParent();
            // get the index of the deleted parent
            int selectedIdx = listViewParent.getSelectionModel().getSelectedIndices().stream().min(Integer::compareTo).orElse(-1);


            // clean up the available children
            for (P key : listViewParent.getSelectionModel().getSelectedItems()) {
                // remove the assigned sub-categories
                availableToBeAssigned.get(key).clear();
                // remove the category
                availableToBeAssigned.remove(key);
            }

            // remove it from the parent ListView
            listViewParent.itemsProperty().get().removeAll(listViewParent.getSelectionModel().getSelectedItems());
            // clear selection
            listViewParent.getSelectionModel().clearSelection();

            // if there are still parents left, change the selection
            // and focus to either next or previous parent relative to the
            // index that were removed
            if (listViewParent.itemsProperty().get().size() > 0) {
                if (selectedIdx >= listViewParent.itemsProperty().get().size()) {
                    selectedIdx = selectedIdx - 1;
                }
                listViewParent.getSelectionModel().select(selectedIdx);
                listViewParent.getFocusModel().focus(selectedIdx);
                // set the current selected category
                currentSelected.set(listViewParent.getSelectionModel().getSelectedItem());
            } else {
                // set current category
                currentSelected.set(null);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(onParentDeleteSuccessShow());
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(onParentDeleteFailureShow());
            alert.showAndWait();
        }
    }

    public abstract void onDeleteParent();

    protected abstract String onParentDeleteSuccessShow();

    protected abstract String onParentDeleteFailureShow();


    /**
     * Creates a Context menu for user interaction with a selected ListCell
     *
     * @param textFieldListCell ListCell that will be interacted
     * @return Context Menu
     */
    protected ContextMenu getCustomContextMenu(TextFieldListCell<P> textFieldListCell) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem saveMenutItem = new MenuItem("Save");
        MenuItem addMenutItem = new MenuItem("Add New");
        MenuItem editMenuItem = new MenuItem("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem cancelMenuItem = new MenuItem("Cancel");

        // disable the following for any other cells that don't contain data
        if (textFieldListCell.isEmpty() || Objects.isNull(textFieldListCell.getItem())) {
            saveMenutItem.setDisable(true);
            editMenuItem.setDisable(true);
            deleteMenuItem.setDisable(true);
        }

        // save or update category
        // throws exception if duplicate parent
        // is being saved
        saveMenutItem.setOnAction(actionEvent -> {
            boolean isSavedOrUpdated = saveOrUpdateParent();
            if (!isSavedOrUpdated) {
                textFieldListCell.startEdit();
            }
        });

        // delete paren
        deleteMenuItem.setOnAction(actionEvent -> {
            textFieldListCell.listViewProperty().get().getSelectionModel().getSelectedItem();
            deleteParent(actionEvent);
        });

        // add a new parent item at the end of the ListView
        // automatically focus and start editing once added
        addMenutItem.setOnAction(actionEvent -> addParent(onAddNewParentCreateNewParent()));

        // edit selected list-cell
        editMenuItem.setOnAction(actionEvent -> textFieldListCell.startEdit());

        // add menu items to the context menu
        contextMenu.getItems().addAll(saveMenutItem, addMenutItem, editMenuItem, deleteMenuItem, cancelMenuItem);

        return contextMenu;

    }

    protected abstract P onAddNewParentCreateNewParent();

    /**
     * All the available parents will be presented.
     * Method populates the ListView with all the available parents
     * from queried from the database.
     * The ListView is editable.
     * Each ListCell is represented with TextFieldListCell to provide editing
     * option to the end user.
     * On selection of any category from the list, the ListView for the
     * assigned children and available children to assign to this parent will be populated.
     * User can right-click on any of the ListCell that will pop-up a context menu for
     * further interaction.
     */
    protected void setupCellFactoryListViewParent() {
        // make ListView editable
        listViewParent.setEditable(true);
        // multiple cell selection from the ListView
        listViewParent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        // Show parent's title in listviewParent
        listViewParent.setCellFactory(new Callback<ListView<P>, ListCell<P>>() {
            @Override
            public ListCell<P> call(ListView<P> categoryListView) {

                TextFieldListCell<P> textFieldListCell = new TextFieldListCell<P>() {

                    // Contains text value inside the listcell when in editing state
                    private StringProperty tempTextProperty = new SimpleStringProperty("");

                    // check if the list-cell editing state is turned off by pressing
                    // ESCAPE Key or not
                    boolean isCancelledEditingByEscapeKey = false;

                    // check if the list-cell editing state is turned off by
                    // pressing Enter key when committed
                    boolean isCommitedEditingByEnterKey = false;

                    // focused lost
                    boolean isFocusLost = false;

                    // change listener when focus lost
                    ChangeListener<? super Boolean> focusLostListener = new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasFocused, Boolean isFocused) {
                            if (!isFocused) {
                                cancelEdit();
                            }
                        }
                    };

                    // Temp event handler that handles the key press of list-cell's textfield when editing
                    EventHandler<KeyEvent> escapeKeyEventHandler = keyEvent -> {
                        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                            isCancelledEditingByEscapeKey = true;
                        }
                        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                            isCommitedEditingByEnterKey = true;
                        }
                    };

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        // contains the temp text-field inside the list-cell
                        if (Objects.nonNull(getGraphic())) {
                            TextField textField = (TextField) getGraphic();
                            // binding to capture the edited values
                            tempTextProperty.bind(textField.textProperty());
                            textField.addEventHandler(KeyEvent.KEY_PRESSED, escapeKeyEventHandler);
                            textField.focusedProperty().addListener(focusLostListener);
                        }


                    }

                    @Override
                    public void cancelEdit() {

                        // cleanup
                        if (Objects.nonNull(getGraphic()) && getGraphic() instanceof TextField textField) {
                            textField.focusedProperty().removeListener(focusLostListener);
                            textField.removeEventHandler(KeyEvent.KEY_PRESSED, escapeKeyEventHandler);
                        }


                        if (!isCancelledEditingByEscapeKey && !isCommitedEditingByEnterKey) {
                            // commit changes before cancelling
                            String editedValues = tempTextProperty.get();
                            setText(editedValues);
                            P parent = getItem();
                            if (parent instanceof Category category) {
                                category.setTitle(editedValues);
                            } else if (parent instanceof SubCategory subCategory) {
                                subCategory.setTitle(editedValues);
                            }
                            updateItem(parent, false);
                            commitEdit(parent);
                            listViewParent.getSelectionModel().clearSelection();
                            currentSelected.set(null);
                        }


                        if (isCancelledEditingByEscapeKey) {
                            isCancelledEditingByEscapeKey = false;
                        }

                        if (isCommitedEditingByEnterKey) {
                            isCommitedEditingByEnterKey = false;
                        }

                        /**
                         * TODO:    if user left a cell blank remove it
                         */
//                        if (!isEmpty()
//                                && Objects.isNull(getItem().getTitle())
//                                && getItem().getSubCategoryList().size() == 0) {
//                            availableToBeAssigned.remove(getItem());
//                            parentObservableList.remove(getItem());
//                        }
                        super.cancelEdit();

                    }

                    @Override
                    public void commitEdit(P parent) {
                        if (!isEmpty()) {
                            super.commitEdit(parent);
                            if (parent instanceof Category category) {
                                setText(category.getTitle());
                            } else if (parent instanceof SubCategory subCategory) {
                                setText(subCategory.getTitle());
                            }
                        }
                    }

                    // convert object to make it compatible for the TextFieldListCell
                    @Override
                    public void updateItem(P parent, boolean empty) {
                        super.updateItem(parent, empty);


                        setConverter(new StringConverter<P>() {
                            @Override
                            public String toString(P parent) {
                                if (Objects.nonNull(parent) && parent instanceof Category category) {
                                    return category.getTitle();
                                } else if (Objects.nonNull(parent) && parent instanceof SubCategory subCategory) {
                                    return subCategory.getTitle();
                                }
                                return "";
                            }

                            @Override
                            public P fromString(String s) {
                                if (getItem() instanceof Category category) {
                                    category.setTitle(s);
                                } else if (getItem() instanceof SubCategory subCategory) {
                                    subCategory.setTitle(s);
                                }
                                return getItem();
                            }
                        });


                        // show available children and assigned parents
                        this.setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                if (Objects.nonNull(parent) && !empty) {
                                    // set currentSelected
                                    currentSelected.set(getItem());
                                }
                            }
                        });
                    }


                };


                textFieldListCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    // show context menu for interacting with the selected list-cell
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        textFieldListCell.setContextMenu(getCustomContextMenu(textFieldListCell));
                    }
                });


                return textFieldListCell;
            }
        });


    }


    /**
     * All the available children will be presented that can be assigned to a selected parent.
     * Method populates the ListView with all the available children
     * except for the assigned children.
     * End user can double-click on any of the children which will move
     * the child to the assigned children.
     */
    protected void setupCellFactoryListViewAvailableChildren() {
        // Show child's title in listViewAvailableChildren
        listViewAvailableChildren.setCellFactory(new Callback<ListView<C>, ListCell<C>>() {
            @Override
            public ListCell<C> call(ListView<C> subCategoryListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(C children, boolean empty) {
                        super.updateItem(children, empty);
                        if (Objects.nonNull(children) && !empty) {
                            if (children instanceof SubCategory subCategory) {
                                setText(subCategory.getTitle());
                            } else if (children instanceof Category category) {
                                setText(category.getTitle());
                            }
                        } else {
                            setText(null);
                        }

                        // double click to assign the child
                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (mouseEvent.getClickCount() == 2 && Objects.nonNull(children)) {
                                    assign(children);
                                }
                            }
                        });
                    }
                };
            }
        });
    }


    /**
     * All the assigned children will be presented.
     * Method populates the ListView with all the assigned children
     * except for the un-assigned children.
     * End user can double-click on any of the children which will move
     * the child to the ListView that contains un-assigned or available children.
     */
    protected void setupCellFactoryListViewAssignedChildren() {
        // Show parent title in listviewAssignedChildren
        listViewAssignedChildren.setCellFactory(new Callback<ListView<C>, ListCell<C>>() {
            @Override
            public ListCell<C> call(ListView<C> subCategoryListView) {
                return new ListCell<C>() {
                    @Override
                    protected void updateItem(C children, boolean empty) {
                        super.updateItem(children, empty);
                        if (Objects.nonNull(children) && !empty) {
                            if (children instanceof SubCategory subCategory) {
                                setText(subCategory.getTitle());
                            } else if (children instanceof Category category) {
                                setText(category.getTitle());
                            }
                        } else {
                            setText(null);
                        }

                        // double click to un-assign the child
                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (mouseEvent.getClickCount() == 2 && Objects.nonNull(children)) {
                                    unassign(children);
                                }
                            }
                        });
                    }
                };
            }
        });
    }



}

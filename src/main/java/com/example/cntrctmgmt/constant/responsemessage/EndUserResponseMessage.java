package com.example.cntrctmgmt.constant.responsemessage;

public enum EndUserResponseMessage {
    CATEGORY_SAVED("Category saved successfully!"),
    CATEGORY_SAVED_ERROR("Something went wrong! Unable to save category!"),
    CATEGORY_UPDATED("Category updated successfully!"),
    CATEGORY_UPDATED_ERROR("Something went wrong! Unable to update category!"),
    CATEGORY_DELETED("Category deleted successfully!"),
    CATEGORY_DELETED_ERROR("Something went wrong! Unable to delete category!"),

    SUBCATEGORY_SAVED("Sub-category saved successfully!"),
    SUBCATEGORY_SAVED_ERROR("Something went wrong! Unable to save sub-category!"),
    SUBCATEGORY_UPDATED("Sub-category updated successfully!"),
    SUBCATEGORY_UPDATED_ERROR("Something went wrong! Unable to update sub-category!"),
    SUBCATEGORY_DELETED("Sub-category deleted successfully!"),
    SUBCATEGORY_DELETED_ERROR("Something went wrong! Unable to delete sub-category!"),

    CONTRACT_SAVED("Contract saved successfully!"),
    CONTRACT_SAVED_ERROR("Something went wrong! Unable to save contract!"),
    CONTRACT_UPDATED("Contract updated successfully!"),
    CONTRACT_UPDATED_ERROR("Something went wrong! Unable to update contract!"),
    CONTRACT_DELETED("Contract deleted successfully!"),
    CONTRACT_DELETED_ERROR("Something went wrong! Unable to delete contract!"),

    SUBCONTRACT_SAVED("Sub-contract saved successfully!"),
    SUBCONTRACT_SAVED_ERROR("Something went wrong! Unable to save sub-contract!"),
    SUBCONTRACT_UPDATED("Sub-contract updated successfully!"),
    SUBCONTRACT_UPDATED_ERROR("Something went wrong! Unable to update sub-contract!"),
    SUBCONTRACT_DELETED("Sub-contract deleted successfully!"),
    SUBCONTRACT_DELETED_ERROR("Something went wrong! Unable to delete sub-contract!");

    private final String message;
    EndUserResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

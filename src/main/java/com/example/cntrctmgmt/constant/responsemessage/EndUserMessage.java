package com.example.cntrctmgmt.constant.responsemessage;

public enum EndUserMessage {
    CATEGORY_SAVED("Category saved successfully!"),
    CATEGORY_UPDATED("Category updated successfully!"),
    CATEGORY_DELETED("Category deleted successfully!"),

    SUBCATEGORY_SAVED("Sub-category saved successfully!"),
    SUBCATEGORY_UPDATED("Sub-category updated successfully!"),
    SUBCATEGORY_DELETED("Sub-category deleted successfully!"),

    CONTRACT_SAVED("Contract saved successfully!"),
    CONTRACT_UPDATED("Contract updated successfully!"),
    CONTRACT_DELETED("Contract deleted successfully!"),

    SUBCONTRACT_SAVED("Sub-contract saved successfully!"),
    SUBCONTRACT_UPDATED("Sub-contract updated successfully!"),
    SUBCONTRACT_DELETED("Sub-contract deleted successfully!");

    private final String message;
    EndUserMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

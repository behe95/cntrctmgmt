package com.example.cntrctmgmt.constant.views;

public enum SCREEN_NAMES {
    SETTINGS_CATEGORY_SCREEN("Category Management", "SettingsCategoryView.fxml"),
    SETTINGS_SUBCATEGORY_SCREEN("Sub-category Management", "SettingsSubCategoryView.fxml");

    private final String title;
    private final String fileLocation;
    SCREEN_NAMES(String title, String fileLocation) {
        this.title = title;
        this.fileLocation = fileLocation;
    }

    public String getTitle() {
        return title;
    }

    public String getFileLocation() {
        return fileLocation;
    }
}

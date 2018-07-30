package com.example.hamzah.modulediary.Deadlines;


public class DeadlinesBean {

    private String date;
    private String type;

    public String getDateObject() {
        return dateObject;
    }

    public void setDateObject(String dateObject) {
        this.dateObject = dateObject;
    }

    private String dateObject;
    private String title;
    private String description;
    private String moduleTitle;
    private String moduleId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}

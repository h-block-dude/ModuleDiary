package com.example.hamzah.modulediary.SetUp.ModulePicker;

public class ModulesSetUpBean {

    private String title;
    private String id;
    private int credits;
    private boolean isChecked = false;
    private Long year;
    private String semester;

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private ModulesSetUpBean() {

    }

    public ModulesSetUpBean(String title, int credits) {
        this.title = title;
        this.id = id;
        this.credits = credits;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

}

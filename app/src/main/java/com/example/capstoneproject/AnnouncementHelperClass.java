package com.example.capstoneproject;

public class AnnouncementHelperClass {
    String title, description, dateTime;

    public AnnouncementHelperClass(String title, String description, String dateTime) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getDateTime() {
        return dateTime;
    }
}

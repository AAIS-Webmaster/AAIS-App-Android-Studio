package com.example.capstoneproject;

// Announcement class represents an announcement with a title, description, and date/time
public class Announcement {
    private String title;       // Title of the announcement
    private String description; // Description of the announcement
    private String dateTime;    // Date and time of the announcement

    // Constructor to initialize an Announcement object with title, description, and dateTime
    public Announcement(String title, String description, String dateTime) {
        this.title = title;       // Set the title
        this.description = description; // Set the description
        this.dateTime = dateTime; // Set the date and time
    }

    // Getter method to retrieve the title of the announcement
    public String getTitle() {
        return title;
    }

    // Setter method to update the title of the announcement
    public void setTitle(String name) {
        this.title = title; // Update the title
    }

    // Getter method to retrieve the date and time of the announcement
    public String getDateTime() {
        return dateTime;
    }

    // Setter method to update the date and time of the announcement
    public void setDate(String dateTime) {
        this.dateTime = dateTime; // Update the date and time
    }

    // Getter method to retrieve the description of the announcement
    public String getDescription() {
        return description;
    }

    // Setter method to update the description of the announcement
    public void setDescription(String description) {
        this.description = description; // Update the description
    }
}
package com.example.capstoneproject;

// Helper class to represent an announcement with its details
public class AnnouncementHelperClass {
    // Member variables to store announcement details
    String title; // The title of the announcement
    String description; // The description of the announcement
    String dateTime; // The date and time of the announcement

    // Constructor to initialize an AnnouncementHelperClass object
    public AnnouncementHelperClass(String title, String description, String dateTime) {
        this.title = title; // Set the title
        this.description = description; // Set the description
        this.dateTime = dateTime; // Set the date and time
    }

    // Getter method for the title
    public String getTitle() {
        return title; // Return the title
    }

    // Getter method for the description
    public String getDescription() {
        return description; // Return the description
    }

    // Getter method for the date and time
    public String getDateTime() {
        return dateTime; // Return the date and time
    }
}

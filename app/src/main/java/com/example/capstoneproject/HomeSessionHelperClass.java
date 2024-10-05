package com.example.capstoneproject;

public class HomeSessionHelperClass {
    // Member variables to hold session details
    String subject; // Subject of the session
    String date;    // Date of the session
    String time;    // Time of the session
    String track;   // Track of the session

    // Constructor to initialize session details
    public HomeSessionHelperClass(String subject, String date, String time, String track) {
        this.subject = subject; // Assign subject
        this.date = date;       // Assign date
        this.time = time;       // Assign time
        this.track = track;     // Assign track
    }

    // Getter method for subject
    public String getSubject() {
        return subject; // Return the subject
    }

    // Getter method for date
    public String getDate() {
        return date; // Return the date
    }

    // Getter method for time
    public String getTime() {
        return time; // Return the time
    }

    // Getter method for track
    public String getTrack() {
        return track; // Return the track
    }
}

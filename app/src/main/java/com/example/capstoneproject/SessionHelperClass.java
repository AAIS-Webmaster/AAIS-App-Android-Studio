package com.example.capstoneproject;

public class SessionHelperClass {
    // Class variables to hold session details
    String track; // The track/category of the session
    String session_name; // The name/title of the session
    String time; // The scheduled time of the session

    // Constructor to initialize a SessionHelperClass object with track, session name, and time
    public SessionHelperClass(String track, String session_name, String time) {
        this.track = track; // Set the track
        this.session_name = session_name; // Set the session name
        this.time = time; // Set the session time
    }

    // Getter method for the track
    public String getTrack() {
        return track; // Return the track value
    }

    // Getter method for the session name
    public String getSession_name() {
        return session_name; // Return the session name value
    }

    // Getter method for the session time
    public String getTime() {
        return time; // Return the time value
    }
}

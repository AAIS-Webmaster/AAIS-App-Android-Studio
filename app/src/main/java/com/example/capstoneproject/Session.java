package com.example.capstoneproject;

import java.time.LocalDate; // Import LocalDate for managing date
import java.time.LocalTime; // Import LocalTime for managing time

public class Session {
    // Private fields representing the attributes of a session
    private String track; // Track category of the session
    private String name; // Name or title of the session
    private String location; // Location where the session will take place
    private String address; // Address of the session location
    private String chair; // Name of the chairperson for the session
    // Names and URLs for up to four papers associated with the session
    private String paper1_name, paper1_url;
    private String paper2_name, paper2_url;
    private String paper3_name, paper3_url;
    private String paper4_name, paper4_url;
    private LocalDate date; // Date of the session
    private LocalTime start_time, end_time; // Start and end times for the session

    // Constructor to initialize a Session object with specified attributes
    public Session(String track, String name, LocalDate date, LocalTime start_time, LocalTime end_time, String location,
                   String address, String chair, String paper1_name,
                   String paper1_url, String paper2_name, String paper2_url,
                   String paper3_name, String paper3_url, String paper4_name, String paper4_url) {
        this.track = track; // Assign track to the session
        this.name = name; // Assign name to the session
        this.date = date; // Assign date to the session
        this.start_time = start_time; // Assign start time to the session
        this.end_time = end_time; // Assign end time to the session
        this.location = location; // Assign location to the session
        this.address = address; // Assign address to the session
        this.chair = chair; // Assign chairperson to the session
        this.paper1_name = paper1_name; // Assign first paper name
        this.paper1_url = paper1_url; // Assign first paper URL
        this.paper2_name = paper2_name; // Assign second paper name
        this.paper2_url = paper2_url; // Assign second paper URL
        this.paper3_name = paper3_name; // Assign third paper name
        this.paper3_url = paper3_url; // Assign third paper URL
        this.paper4_name = paper4_name; // Assign fourth paper name
        this.paper4_url = paper4_url; // Assign fourth paper URL
    }

    // Getter for track
    public String getTrack() {
        return track;
    }

    // Setter for track
    public void setTrack(String track) {
        this.track = track;
    }

    // Getter for session name
    public String getName() {
        return name;
    }

    // Setter for session name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for session date
    public LocalDate getDate() {
        return date;
    }

    // Setter for session date
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Getter for start time
    public LocalTime getStart_time() {
        return start_time;
    }

    // Setter for start time
    public void setStart_time(LocalTime start_time) {
        this.start_time = start_time;
    }

    // Getter for end time
    public LocalTime getEnd_time() {
        return end_time;
    }

    // Setter for end time
    public void setEnd_time(LocalTime end_time) {
        this.end_time = end_time;
    }

    // Getter for location
    public String getLocation() {
        return location;
    }

    // Setter for location
    public void setLocation(String location) {
        this.location = location;
    }

    // Getter for address
    public String getAddress() {
        return address;
    }

    // Setter for address
    public void setAddress(String address) {
        this.address = address;
    }

    // Getter for chairperson's name
    public String getChair() {
        return chair;
    }

    // Setter for chairperson's name
    public void setChair(String chair) {
        this.chair = chair;
    }

    // Getter for first paper name
    public String getPaper1_name() {
        return paper1_name;
    }

    // Setter for first paper name
    public void setPaper1_name(String paper1_name) {
        this.paper1_name = paper1_name;
    }

    // Getter for first paper URL
    public String getPaper1_url() {
        return paper1_url;
    }

    // Setter for first paper URL
    public void setPaper1_url(String paper1_url) {
        this.paper1_url = paper1_url;
    }

    // Getter for second paper name
    public String getPaper2_name() {
        return paper2_name;
    }

    // Setter for second paper name
    public void setPaper2_name(String paper2_name) {
        this.paper2_name = paper2_name;
    }

    // Getter for second paper URL
    public String getPaper2_url() {
        return paper2_url;
    }

    // Setter for second paper URL
    public void setPaper2_url(String paper2_url) {
        this.paper2_url = paper2_url;
    }

    // Getter for third paper name
    public String getPaper3_name() {
        return paper3_name;
    }

    // Setter for third paper name
    public void setPaper3_name(String paper3_name) {
        this.paper3_name = paper3_name;
    }

    // Getter for third paper URL
    public String getPaper3_url() {
        return paper3_url;
    }

    // Setter for third paper URL
    public void setPaper3_url(String paper3_url) {
        this.paper3_url = paper3_url;
    }

    // Getter for fourth paper name
    public String getPaper4_name() {
        return paper4_name;
    }

    // Setter for fourth paper name
    public void setPaper4_name(String paper4_name) {
        this.paper4_name = paper4_name;
    }

    // Getter for fourth paper URL
    public String getPaper4_url() {
        return paper4_url;
    }

    // Setter for fourth paper URL
    public void setPaper4_url(String paper4_url) {
        this.paper4_url = paper4_url;
    }
}

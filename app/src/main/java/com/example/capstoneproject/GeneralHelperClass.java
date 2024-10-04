package com.example.capstoneproject;

public class GeneralHelperClass {
    String track, event_name, time;

    public GeneralHelperClass(String track, String event_name, String time) {
        this.track = track;
        this.event_name = event_name;
        this.time = time;
    }

    public String getTrack() {
        return track;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getTime() {
        return time;
    }
}

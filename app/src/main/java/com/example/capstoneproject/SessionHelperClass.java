package com.example.capstoneproject;

public class SessionHelperClass {
    String track, event_name, time;

    public SessionHelperClass(String track, String event_name, String time) {
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

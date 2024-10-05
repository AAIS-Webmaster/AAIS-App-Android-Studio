package com.example.capstoneproject;

public class SessionHelperClass {
    String track, session_name, time;

    public SessionHelperClass(String track, String session_name, String time) {
        this.track = track;
        this.session_name = session_name;
        this.time = time;
    }

    public String getTrack() {
        return track;
    }

    public String getSession_name() {
        return session_name;
    }

    public String getTime() {
        return time;
    }
}

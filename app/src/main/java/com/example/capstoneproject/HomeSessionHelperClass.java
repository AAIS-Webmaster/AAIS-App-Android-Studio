package com.example.capstoneproject;

public class HomeSessionHelperClass {
    String subject, date, time, track;

    public HomeSessionHelperClass(String subject, String date, String time, String track) {
        this.subject = subject;
        this.date = date;
        this.time = time;
        this.track = track;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTrack() {
        return track;
    }
}

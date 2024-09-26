package com.example.capstoneproject;

public class HomeAnnouncementHelperClass {
    String subject, date, time;

    public HomeAnnouncementHelperClass(String subject, String date, String time) {
        this.subject = subject;
        this.date = date;
        this.time = time;
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
}

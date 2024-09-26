package com.example.capstoneproject;

import android.graphics.Bitmap;

public class GeneralHelperClass {
    String event_name, time;

    public GeneralHelperClass(String event_name, String time) {
        this.event_name = event_name;
        this.time = time;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getTime() {
        return time;
    }
}

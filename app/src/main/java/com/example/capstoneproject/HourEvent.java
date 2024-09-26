package com.example.capstoneproject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HourEvent
{
    private LocalDate date;
    LocalTime time;
    ArrayList<Event> events;

    public HourEvent(LocalDate date, LocalTime time, ArrayList<Event> events) {
        this.date = date;
        this.time = time;
        this.events = events;
    }

    public LocalDate getDate() { return date; }

    public LocalTime getTime()
    {
        return time;
    }

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

    public ArrayList<Event> getEvents()
    {
        return events;
    }

    public void setEvents(ArrayList<Event> events)
    {
        this.events = events;
    }
}
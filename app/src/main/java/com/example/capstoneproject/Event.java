package com.example.capstoneproject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event
{
    public static ArrayList<Event> eventsList = new ArrayList<>();

//    public static ArrayList<Event> eventsForDate(LocalDate date)
//    {
//        ArrayList<Event> events = new ArrayList<>();
//
//        for(Event event : eventsList)
//        {
//            if(event.getDate().equals(date))
//                events.add(event);
//        }
//        return events;
//    }
//
//    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time)
//    {
//        ArrayList<Event> events = new ArrayList<>();
//
//        for(Event event : eventsList)
//        {
//            int eventHour = event.start_time.getHour();
//            int cellHour = time.getHour();
//            if(event.getDate().equals(date) && eventHour == cellHour)
//                events.add(event);
//        }
//
//        return events;
//    }

    private String track, name, location, address, chair, paper1_name, paper1_url, paper2_name, paper2_url,
            paper3_name, paper3_url, paper4_name, paper4_url;
    private LocalDate date;
    private LocalTime start_time, end_time;

    public Event(String track, String name, LocalDate date, LocalTime start_time, LocalTime end_time, String location,
                 String address, String chair, String paper1_name,
                 String paper1_url, String paper2_name, String paper2_url,
                 String paper3_name, String paper3_url, String paper4_name, String paper4_url)
    {
        this.track = track;
        this.name = name;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.location = location;
        this.address = address;
        this.chair = chair;
        this.paper1_name = paper1_name;
        this.paper1_url = paper1_url;
        this.paper2_name = paper2_name;
        this.paper2_url = paper2_url;
        this.paper3_name = paper3_name;
        this.paper3_url = paper3_url;
        this.paper4_name = paper4_name;
        this.paper4_url = paper4_url;
    }

    public String getTrack()
    {
        return track;
    }

    public void setTrack(String track)
    {
        this.track = track;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public LocalTime getStart_time()
    {
        return start_time;
    }

    public void setStart_time(LocalTime start_time)
    {
        this.start_time = start_time;
    }

    public LocalTime getEnd_time()
    {
        return end_time;
    }

    public void setEnd_time(LocalTime end_time)
    {
        this.end_time = end_time;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getChair()
    {
        return chair;
    }

    public void setChair(String chair)
    {
        this.chair = chair;
    }

    public String getPaper1_name()
    {
        return paper1_name;
    }

    public void setPaper1_name(String paper1_name)
    {
        this.paper1_name = paper1_name;
    }

    public String getPaper1_url()
    {
        return paper1_url;
    }

    public void setPaper1_url(String paper1_url)
    {
        this.paper1_url = paper1_url;
    }

    public String getPaper2_name()
    {
        return paper2_name;
    }

    public void setPaper2_name(String paper2_name)
    {
        this.paper2_name = paper2_name;
    }

    public String getPaper2_url()
    {
        return paper2_url;
    }

    public void setPaper2_url(String paper2_url)
    {
        this.paper2_url = paper2_url;
    }

    public String getPaper3_name()
    {
        return paper3_name;
    }

    public void setPaper3_name(String paper3_name)
    {
        this.paper3_name = paper3_name;
    }

    public String getPaper3_url()
    {
        return paper3_url;
    }

    public void setPaper3_url(String paper3_url)
    {
        this.paper3_url = paper3_url;
    }

    public String getPaper4_name()
    {
        return paper4_name;
    }

    public void setPaper4_name(String paper4_name)
    {
        this.paper4_name = paper4_name;
    }

    public String getPaper4_url()
    {
        return paper4_url;
    }

    public void setPaper4_url(String paper4_url)
    {
        this.paper4_url = paper4_url;
    }
}
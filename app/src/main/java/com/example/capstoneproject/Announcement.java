package com.example.capstoneproject;

public class Announcement
{
    private String title, description, dateTime;

    public Announcement(String title, String description, String dateTime)
    {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String name)
    {
        this.title = title;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDate(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
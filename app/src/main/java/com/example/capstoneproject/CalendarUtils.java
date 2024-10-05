package com.example.capstoneproject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Utility class for calendar-related functions
public class CalendarUtils {
    public static LocalDate selectedDate; // Holds the currently selected date

    // Method to format a LocalDate into a string representing the month and year
    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy"); // Define the date format
        return date.format(formatter); // Return the formatted date string
    }

    // Method to generate an ArrayList of LocalDate objects representing the days of the week
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>(); // Initialize the list to hold the days
        LocalDate current = sundayForDate(selectedDate); // Get the Sunday of the current week
        LocalDate endDate = current.plusWeeks(1); // Set the end date to one week later

        // Loop through the week, adding each day to the list
        while (current.isBefore(endDate)) {
            days.add(current); // Add the current date to the list
            current = current.plusDays(1); // Move to the next day
        }
        return days; // Return the list of days
    }

    // Helper method to find the Sunday that corresponds to the given date
    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1); // Calculate one week before the current date

        // Loop backwards from the current date to find the previous Sunday
        while (current.isAfter(oneWeekAgo)) {
            if (current.getDayOfWeek() == DayOfWeek.SUNDAY) // Check if the current date is a Sunday
                return current; // Return the found Sunday

            current = current.minusDays(1); // Move to the previous day
        }
        return null; // Return null if no Sunday is found (should not happen)
    }
}
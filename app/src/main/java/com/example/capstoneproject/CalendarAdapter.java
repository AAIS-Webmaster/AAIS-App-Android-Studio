package com.example.capstoneproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

// Adapter class for managing the calendar view in a RecyclerView
class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<LocalDate> days; // List of LocalDate objects representing the days
    private final OnItemListener onItemListener; // Listener for item click events

    // Constructor to initialize the adapter with days and item listener
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days; // Assign the provided days list to the member variable
        this.onItemListener = onItemListener; // Assign the provided listener to the member variable
    }

    // Creates new view holders to display calendar cells
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext()); // Get LayoutInflater from parent context
        View view = inflater.inflate(R.layout.calendar_cell, parent, false); // Inflate the calendar cell layout
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams(); // Get the layout parameters for the view

        // Set the height of the calendar cell based on the number of days
        if (days.size() > 15) // Check if displaying month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666); // Set height for month view
        else // Otherwise, it’s a week view
            layoutParams.height = (int) parent.getHeight(); // Set height for week view

        return new CalendarViewHolder(view, onItemListener, days); // Return a new ViewHolder instance
    }

    // Binds data to the view holder for a specific position
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position); // Get the LocalDate for the current position

        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth())); // Set the day of the month in the view

        // Highlight the selected date
        if (date.equals(CalendarUtils.selectedDate)) { // Check if this date is the selected date
            holder.parentView.setBackgroundColor(holder.parentView.getResources().getColor(R.color.accent)); // Set background color
            holder.dayOfMonth.setTextColor(holder.dayOfMonth.getResources().getColor(R.color.white)); // Set text color
        }
    }

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return days.size(); // Return the size of the days list
    }

    // Interface for handling item click events
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date); // Method to handle item click with position and date
    }
}
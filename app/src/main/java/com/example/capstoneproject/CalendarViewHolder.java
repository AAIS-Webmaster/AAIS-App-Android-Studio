package com.example.capstoneproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ArrayList<LocalDate> days; // List of LocalDate objects representing the days in the calendar
    public final View parentView; // The parent view for this ViewHolder
    public final TextView dayOfMonth; // TextView to display the day of the month
    private final CalendarAdapter.OnItemListener onItemListener; // Listener for item click events

    // Constructor for the ViewHolder
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {
        super(itemView); // Call the superclass constructor
        parentView = itemView.findViewById(R.id.parentView); // Initialize the parent view
        dayOfMonth = itemView.findViewById(R.id.cellDayText); // Initialize the TextView for the day of the month
        this.onItemListener = onItemListener; // Set the item listener
        itemView.setOnClickListener(this); // Set the click listener for the item view
        this.days = days; // Store the list of days
    }

    // Method to handle click events
    @Override
    public void onClick(View view) {
        // Notify the listener of the item clicked, passing the position and corresponding date
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
    }
}
package com.example.capstoneproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeSessionAdapter extends RecyclerView.Adapter<HomeSessionAdapter.HomeSessionViewHolder> {

    ArrayList<HomeSessionHelperClass> homeAnnouncementLocations; // List of session data
    SessionAdapter.RecyclerViewClickListener listener; // Click listener for RecyclerView items

    // Constructor to initialize data and listener
    public HomeSessionAdapter(ArrayList<HomeSessionHelperClass> homeAnnouncementLocations, SessionAdapter.RecyclerViewClickListener listener) {
        this.homeAnnouncementLocations = homeAnnouncementLocations; // Assign data list
        this.listener = listener; // Assign click listener
    }

    @NonNull
    @Override
    public HomeSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each session item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_session_card_design, parent, false);
        // Create a new ViewHolder instance with the inflated view
        HomeSessionViewHolder homeSessionViewHolder = new HomeSessionViewHolder(view, listener);
        return homeSessionViewHolder; // Return the ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSessionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the session data for the current position
        HomeSessionHelperClass homeSessionHelperClass = homeAnnouncementLocations.get(position);

        // Bind data to the corresponding TextViews in the ViewHolder
        holder.subject.setText(homeSessionHelperClass.getSubject());
        holder.date.setText(homeSessionHelperClass.getDate());
        holder.time.setText(homeSessionHelperClass.getTime());
        holder.track.setText(homeSessionHelperClass.getTrack());

        // Set an onClickListener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext(); // Get the context from the view
                Intent intent = new Intent(context, Session_Details_Page.class); // Create an intent to start the session details page
                intent.putExtra("session_name", homeAnnouncementLocations.get(position).subject); // Pass session name as extra
                context.startActivity(intent); // Start the session details activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeAnnouncementLocations.size(); // Return the total number of items
    }

    // ViewHolder class to hold session item views
    public static class HomeSessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subject, date, time, track; // TextViews for session details
        SessionAdapter.RecyclerViewClickListener listener; // Click listener reference

        // Constructor to initialize ViewHolder and set the click listener
        public HomeSessionViewHolder(@NonNull View itemView, SessionAdapter.RecyclerViewClickListener listener) {
            super(itemView); // Call the parent constructor

            // Hooks for the TextViews
            subject = itemView.findViewById(R.id.subject); // Find the subject TextView
            date = itemView.findViewById(R.id.date); // Find the date TextView
            time = itemView.findViewById(R.id.time); // Find the time TextView
            track = itemView.findViewById(R.id.track); // Find the track TextView

            this.listener = listener; // Assign the click listener
            itemView.setOnClickListener(this); // Set the item view's click listener
        }

        @Override
        public void onClick(View v) {
            listener.onClick(itemView, getAdapterPosition()); // Call the click listener when the item is clicked
        }
    }

    // Interface for handling RecyclerView item clicks
    public interface RecyclerViewClickListener {
        void onClick(View v, int position); // Method to handle click events
    }
}
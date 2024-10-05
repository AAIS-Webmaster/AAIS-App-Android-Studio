package com.example.capstoneproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    ArrayList<SessionHelperClass> sessions; // List of session items to be displayed
    RecyclerViewClickListener listener; // Listener for RecyclerView item clicks

    // Constructor for the adapter to initialize session list and click listener
    public SessionAdapter(ArrayList<SessionHelperClass> sessions, RecyclerViewClickListener listener) {
        this.sessions = sessions; // Initialize sessions
        this.listener = listener; // Initialize listener
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each session card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_card_design, parent, false);
        return new SessionViewHolder(view, listener); // Return a new ViewHolder instance
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the session item at the current position
        SessionHelperClass sessionHelperClass = sessions.get(position);

        // Set session details to the corresponding TextViews
        holder.session_name.setText(sessionHelperClass.getSession_name());
        holder.track.setText(sessionHelperClass.getTrack());
        holder.time.setText(sessionHelperClass.getTime());

        // Set click listener for the item view to open Session_Details_Page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext(); // Get the context from the clicked view
                Intent intent = new Intent(context, Session_Details_Page.class); // Create intent for Session_Details_Page
                intent.putExtra("session_name", sessions.get(position).session_name); // Pass session name to the new activity
                context.startActivity(intent); // Start the new activity
            }
        });

        // Set click listener for the info button to open Session_Details_Page
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext(); // Get the context from the clicked view
                Intent intent = new Intent(context, Session_Details_Page.class); // Create intent for Session_Details_Page
                intent.putExtra("session_name", sessions.get(position).session_name); // Pass session name to the new activity
                context.startActivity(intent); // Start the new activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size(); // Return the total number of sessions
    }

    // ViewHolder class to hold the session view components
    public static class SessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView track, session_name, time; // TextViews for displaying session details
        ImageButton info; // Button for more session info
        RecyclerViewClickListener listener; // Click listener for the ViewHolder

        // Constructor for the ViewHolder to initialize view components
        public SessionViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            // Hooks for view components
            track = itemView.findViewById(R.id.track); // Initialize track TextView
            session_name = itemView.findViewById(R.id.session_name); // Initialize session name TextView
            time = itemView.findViewById(R.id.time); // Initialize time TextView
            info = itemView.findViewById(R.id.session_info); // Initialize info ImageButton

            this.listener = listener; // Set the listener
            info.setOnClickListener(this); // Set click listener for info button
        }

        @Override
        public void onClick(View v) {
            listener.onClick(itemView, getAdapterPosition()); // Notify listener about the click event
        }
    }

    // Interface for click events on RecyclerView items
    public interface RecyclerViewClickListener {
        void onClick(View v, int position); // Method to handle click events
    }
}

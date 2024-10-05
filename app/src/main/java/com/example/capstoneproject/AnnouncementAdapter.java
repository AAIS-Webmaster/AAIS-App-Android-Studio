package com.example.capstoneproject;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter class for displaying announcements in a RecyclerView
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private final List<AnnouncementHelperClass> announcements; // List of announcement data
    private final String currentUserEmail; // Store the current user's email to check permissions

    // Constructor for initializing adapter with announcements and user email
    public AnnouncementAdapter(List<AnnouncementHelperClass> announcements, String currentUserEmail) {
        this.announcements = announcements; // Set the list of announcements
        this.currentUserEmail = currentUserEmail; // Set the user's email for role checks
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the announcement card layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_card_design, parent, false);
        return new AnnouncementViewHolder(view); // Return a new ViewHolder instance
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        // Get the announcement for the current position
        AnnouncementHelperClass announcement = announcements.get(position);

        // Bind the announcement data to the views
        holder.titleTextView.setText(announcement.getTitle());
        holder.descriptionTextView.setText(announcement.getDescription());
        holder.dateTimeTextView.setText(announcement.getDateTime());

        // Show or hide the delete button based on whether the user is an admin
        if (currentUserEmail.equals("guptasdhuruv4@gmail.com")) {
            holder.deleteImageView.setVisibility(View.VISIBLE); // Show delete button for admin
        } else {
            holder.deleteImageView.setVisibility(View.GONE); // Hide delete button for non-admins
        }

        // Set click listener for the delete button to show confirmation dialog
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(holder.itemView.getContext(), announcement); // Show delete confirmation dialog
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size(); // Return the total number of announcements
    }

    // Method to show a confirmation dialog before deleting an announcement
    private void showDeleteConfirmationDialog(Context context, AnnouncementHelperClass announcement) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);

        // Initialize the custom views from the dialog layout
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        Button confirmButton = dialogView.findViewById(R.id.btn_confirm);

        // Set dialog message with announcement details
        messageTextView.setText("Are you sure you want to delete this announcement?" +
                "\n\nTitle: " + announcement.getTitle() +
                "\nDescription: " + announcement.getDescription() +
                "\nDateTime: " + announcement.getDateTime());

        // Create and configure the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create(); // Create the dialog

        // Set click listener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close dialog if canceled
            }
        });

        // Set click listener for the confirm button to delete the announcement
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(); // Initialize database helper
                // Call method to delete the announcement from the database
                dbHelper.deleteAnnouncement(announcement.getTitle(), announcement.getDescription(), announcement.getDateTime());
                dialog.dismiss(); // Close dialog after confirming
            }
        });

        dialog.show(); // Show the dialog
    }

    // ViewHolder class for holding announcement item views
    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView; // TextView for announcement title
        TextView descriptionTextView; // TextView for announcement description
        TextView dateTimeTextView; // TextView for announcement date and time
        ImageView deleteImageView; // ImageView for delete action

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views from the item layout
            titleTextView = itemView.findViewById(R.id.announcement_title);
            descriptionTextView = itemView.findViewById(R.id.announcement_description);
            dateTimeTextView = itemView.findViewById(R.id.announcement_date_time);
            deleteImageView = itemView.findViewById(R.id.delete_announcement);
        }
    }
}
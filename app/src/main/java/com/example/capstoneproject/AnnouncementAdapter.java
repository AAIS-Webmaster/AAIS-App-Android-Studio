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

// ...

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private final List<AnnouncementHelperClass> announcements;
    private final String currentUserEmail; // Add this to check user role

    public AnnouncementAdapter(List<AnnouncementHelperClass> announcements, String currentUserEmail) {
        this.announcements = announcements;
        this.currentUserEmail = currentUserEmail; // Pass user email or role
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_card_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnnouncementHelperClass announcement = announcements.get(position);

        holder.titleTextView.setText(announcement.getTitle());
        holder.descriptionTextView.setText(announcement.getDescription());
        holder.dateTimeTextView.setText(announcement.getDateTime());

        // Show or hide the delete button based on the current user email
        if (currentUserEmail.equals("guptasdhuruv4@gmail.com")) {
            holder.deleteImageView.setVisibility(View.VISIBLE);
        } else {
            holder.deleteImageView.setVisibility(View.GONE);
        }

        // Set click listener for the delete button
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(holder.itemView.getContext(), announcement);
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    private void showDeleteConfirmationDialog(Context context, AnnouncementHelperClass announcement) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);

        // Initialize the custom views
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        Button confirmButton = dialogView.findViewById(R.id.btn_confirm);

        // Set dialog message
        messageTextView.setText("Are you sure you want to delete this announcement?" +
                "\n\nTitle: " + announcement.getTitle() +
                "\nDescription: " + announcement.getDescription() +
                "\nDateTime: " + announcement.getDateTime());

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close dialog if canceled
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper dbHelper = new MyDatabaseHelper();
                dbHelper.deleteAnnouncement(announcement.getTitle(), announcement.getDescription(), announcement.getDateTime());
                dialog.dismiss(); // Close dialog after confirming
            }
        });

        dialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTimeTextView;
        ImageView deleteImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.announcement_title);
            descriptionTextView = itemView.findViewById(R.id.announcement_description);
            dateTimeTextView = itemView.findViewById(R.id.announcement_date_time);
            deleteImageView = itemView.findViewById(R.id.delete_announcement);
        }
    }
}


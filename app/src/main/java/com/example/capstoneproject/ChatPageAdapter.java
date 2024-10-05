package com.example.capstoneproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0; // View type for date header
    private static final int VIEW_TYPE_MESSAGE = 1; // View type for messages
    ArrayList<ChatPageHelperClass> firstLocations; // List to hold chat messages and headers
    private MyDatabaseHelper dbHelper; // Database helper instance

    // Constructor to initialize the adapter with a list of ChatPageHelperClass
    public ChatPageAdapter(ArrayList<ChatPageHelperClass> firstLocations) {
        this.firstLocations = firstLocations; // Set the list of locations
    }

    // Method to determine the view type for each item
    @Override
    public int getItemViewType(int position) {
        if (firstLocations.get(position).isHeader()) {
            return VIEW_TYPE_HEADER; // Return header view type if item is a header
        } else {
            return VIEW_TYPE_MESSAGE; // Return message view type otherwise
        }
    }

    // Method to create the ViewHolder based on the view type
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_header, parent, false);
            return new DateHeaderViewHolder(view); // Create and return DateHeaderViewHolder
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card_design, parent, false);
            return new MessageViewHolder(view); // Create and return MessageViewHolder
        }
    }

    // Method to bind data to the ViewHolder based on the position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatPageHelperClass item = firstLocations.get(position); // Get the item at the current position
        dbHelper = new MyDatabaseHelper(); // Initialize the database helper

        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            // If the holder is for the header, set the date header text
            ((DateHeaderViewHolder) holder).dateHeader.setText(item.getFormattedDate());
        } else {
            // If the holder is for a message, cast the holder and set the message data
            MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
            messageViewHolder.name.setText(item.getName() != null ? item.getName() : "Unknown"); // Set name or default to "Unknown"
            messageViewHolder.conversation_text.setText(item.getConversation_text() != null ? item.getConversation_text() : ""); // Set conversation text
            messageViewHolder.localDateTime.setText(item.getFormattedTime()); // Set formatted time

            String nameInitial = item.getName() != null ? item.getName().split(" ")[0].substring(0, 1).toUpperCase() : ""; // Get the first initial of the name

            // Call the method to get user image URL
            if (item.getName() != null) {
                try {
                    dbHelper.getUserImageUrl(item.getEmail(), new MyDatabaseHelper.ImageUrlCallback() {
                        @Override
                        public void onImageUrlRetrieved(String imageUrl) {
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                // Load image using Picasso
                                Picasso.get().load(imageUrl).into(messageViewHolder.imageView);
                                messageViewHolder.imageView.setVisibility(View.VISIBLE); // Show image view
                                messageViewHolder.card_view.setVisibility(View.GONE); // Hide card view text
                            } else {
                                // Set default image or the name initial if no URL is found
                                messageViewHolder.card_view.setText(nameInitial); // Show the initial
                                messageViewHolder.card_view.setVisibility(View.VISIBLE); // Show card view text
                                messageViewHolder.imageView.setVisibility(View.GONE); // Hide image view
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            messageViewHolder.card_view.setText(nameInitial); // Show the initial on error
                            messageViewHolder.imageView.setVisibility(View.GONE); // Hide image view on error
                        }
                    });
                } catch (Exception e) {
                    messageViewHolder.card_view.setText(nameInitial); // Show the initial if an error occurs
                    messageViewHolder.imageView.setVisibility(View.GONE); // Hide image view on exception
                }
            } else {
                // Show the initial if name is not provided
                messageViewHolder.imageView.setImageDrawable(null); // Clear previous image
                messageViewHolder.card_view.setText(nameInitial); // Show the initial
            }
        }
    }

    // Method to return the total number of items in the list
    @Override
    public int getItemCount() {
        return firstLocations.size(); // Return the size of the list
    }

    // ViewHolder class for message items
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView name, conversation_text, localDateTime, card_view; // TextViews for message details
        ImageView imageView; // ImageView for user images

        // Constructor for MessageViewHolder
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name); // Initialize name TextView
            conversation_text = itemView.findViewById(R.id.conversation_text); // Initialize conversation TextView
            localDateTime = itemView.findViewById(R.id.dateTime); // Initialize localDateTime TextView
            card_view = itemView.findViewById(R.id.card_view_text); // Initialize card_view TextView
            imageView = itemView.findViewById(R.id.card_view_images); // Initialize ImageView for user image
        }
    }

    // ViewHolder class for date header items
    public static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeader; // TextView for the date header

        // Constructor for DateHeaderViewHolder
        public DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.dateHeader); // Initialize dateHeader TextView
        }
    }

    // Method to set new data and notify the adapter of changes
    public void setData(ArrayList<ChatPageHelperClass> newFirstLocations) {
        this.firstLocations = newFirstLocations; // Update the list of items
        notifyDataSetChanged(); // Notify the adapter of data changes
    }
}
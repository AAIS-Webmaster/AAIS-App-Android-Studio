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

public class FirstAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;

    ArrayList<FirstHelperClass> firstLocations;
    private MyDatabaseHelper dbHelper;

    public FirstAdapter(ArrayList<FirstHelperClass> firstLocations) {
        this.firstLocations = firstLocations;
    }

    @Override
    public int getItemViewType(int position) {
        if (firstLocations.get(position).isHeader()) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_header, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.first_card_design, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FirstHelperClass item = firstLocations.get(position);
        dbHelper = new MyDatabaseHelper();

        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            ((DateHeaderViewHolder) holder).dateHeader.setText(item.getFormattedDate());
        } else {
            MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
            messageViewHolder.name.setText(item.getName() != null ? item.getName() : "Unknown");
            messageViewHolder.conversation_text.setText(item.getConversation_text() != null ? item.getConversation_text() : "");
            messageViewHolder.localDateTime.setText(item.getFormattedTime());

            String nameInitial = item.getName() != null ? item.getName().split(" ")[0].substring(0, 1).toUpperCase() : "";

            // Call the method to get user image URL
            if (item.getName() != null) {
                try {
                    dbHelper.getUserImageUrl(item.getEmail(), new MyDatabaseHelper.ImageUrlCallback() {
                        @Override
                        public void onImageUrlRetrieved(String imageUrl) {
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                // Load image using Glide or any image loading library
                                Picasso.get().load(imageUrl).into(messageViewHolder.imageView);
                                messageViewHolder.imageView.setVisibility(View.VISIBLE);
                                messageViewHolder.card_view.setVisibility(View.GONE);
                            } else {
                                // Set default image or the name initial if no URL is found
    //                            messageViewHolder.imageView.setImageDrawable(null); // Clear previous image
                                messageViewHolder.card_view.setText(nameInitial); // Show the initial
                                messageViewHolder.card_view.setVisibility(View.VISIBLE);
                                messageViewHolder.imageView.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            messageViewHolder.card_view.setText(nameInitial); // Show the initial
                            messageViewHolder.imageView.setVisibility(View.GONE);
                        }
                    });
                }
                catch (Exception e){
                    messageViewHolder.card_view.setText(nameInitial); // Show the initial
                    messageViewHolder.imageView.setVisibility(View.GONE);
                }
            } else {
                // Show the initial if name is not provided
                messageViewHolder.imageView.setImageDrawable(null); // Clear previous image
                messageViewHolder.card_view.setText(nameInitial);
            }
        }
    }

    @Override
    public int getItemCount() {
        return firstLocations.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView name, conversation_text, localDateTime, card_view;
        ImageView imageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            conversation_text = itemView.findViewById(R.id.conversation_text);
            localDateTime = itemView.findViewById(R.id.dateTime);
            card_view = itemView.findViewById(R.id.card_view_text);
            imageView = itemView.findViewById(R.id.card_view_images); // Add your ImageView
        }
    }

    public static class DateHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView dateHeader;

        public DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.dateHeader);
        }
    }

    public void setData(ArrayList<FirstHelperClass> newFirstLocations) {
        this.firstLocations = newFirstLocations;
        notifyDataSetChanged(); // Notify the adapter of data changes
    }
}

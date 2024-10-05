package com.example.capstoneproject;

import androidx.recyclerview.widget.DiffUtil; // Importing DiffUtil for efficient updates in RecyclerView
import java.util.List; // Importing List for handling collections of items

public class MessageDiffCallback extends DiffUtil.Callback {
    // Member variables to hold old and new lists of messages
    private final List<ChatPageHelperClass> oldList; // The old list of messages
    private final List<ChatPageHelperClass> newList; // The new list of messages

    // Constructor to initialize the old and new lists
    public MessageDiffCallback(List<ChatPageHelperClass> oldList, List<ChatPageHelperClass> newList) {
        this.oldList = oldList; // Assigning old list
        this.newList = newList; // Assigning new list
    }

    // Returns the size of the old list
    @Override
    public int getOldListSize() {
        return oldList.size(); // Return the number of items in the old list
    }

    // Returns the size of the new list
    @Override
    public int getNewListSize() {
        return newList.size(); // Return the number of items in the new list
    }

    // Checks if the items at the given positions are the same
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ChatPageHelperClass oldItem = oldList.get(oldItemPosition); // Get item from the old list
        ChatPageHelperClass newItem = newList.get(newItemPosition); // Get item from the new list

        // Check if both items are non-null and have the same ID
        return oldItem != null && newItem != null && oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
    }

    // Checks if the contents of the items at the given positions are the same
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ChatPageHelperClass oldItem = oldList.get(oldItemPosition); // Get item from the old list
        ChatPageHelperClass newItem = newList.get(newItemPosition); // Get item from the new list

        // Return false if either item is null
        if (oldItem == null || newItem == null) {
            return false;
        }
        // Check if the contents of both items are equal
        return oldItem.equals(newItem);
    }
}
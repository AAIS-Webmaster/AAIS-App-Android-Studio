package com.example.capstoneproject;

import androidx.recyclerview.widget.DiffUtil;
import java.util.List;

public class MessageDiffCallback extends DiffUtil.Callback {
    private final List<ChatPageHelperClass> oldList;
    private final List<ChatPageHelperClass> newList;

    public MessageDiffCallback(List<ChatPageHelperClass> oldList, List<ChatPageHelperClass> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ChatPageHelperClass oldItem = oldList.get(oldItemPosition);
        ChatPageHelperClass newItem = newList.get(newItemPosition);
        return oldItem != null && newItem != null && oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ChatPageHelperClass oldItem = oldList.get(oldItemPosition);
        ChatPageHelperClass newItem = newList.get(newItemPosition);
        if (oldItem == null || newItem == null) {
            return false;
        }
        return oldItem.equals(newItem);
    }
}

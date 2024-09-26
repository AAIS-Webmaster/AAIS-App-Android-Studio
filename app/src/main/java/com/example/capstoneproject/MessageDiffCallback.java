package com.example.capstoneproject;

import androidx.recyclerview.widget.DiffUtil;
import java.util.List;

public class MessageDiffCallback extends DiffUtil.Callback {
    private final List<FirstHelperClass> oldList;
    private final List<FirstHelperClass> newList;

    public MessageDiffCallback(List<FirstHelperClass> oldList, List<FirstHelperClass> newList) {
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
        FirstHelperClass oldItem = oldList.get(oldItemPosition);
        FirstHelperClass newItem = newList.get(newItemPosition);
        return oldItem != null && newItem != null && oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        FirstHelperClass oldItem = oldList.get(oldItemPosition);
        FirstHelperClass newItem = newList.get(newItemPosition);
        if (oldItem == null || newItem == null) {
            return false;
        }
        return oldItem.equals(newItem);
    }
}

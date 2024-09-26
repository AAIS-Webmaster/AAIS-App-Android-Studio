package com.example.capstoneproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeAnnouncementAdapter extends RecyclerView.Adapter<HomeAnnouncementAdapter.HomeAnnouncementViewHolder> {

    ArrayList<HomeAnnouncementHelperClass> homeAnnouncementLocations;
    GeneralAdapter.RecyclerViewClickListener listener;

    public HomeAnnouncementAdapter(ArrayList<HomeAnnouncementHelperClass> homeAnnouncementLocations, GeneralAdapter.RecyclerViewClickListener listener) {
        this.homeAnnouncementLocations = homeAnnouncementLocations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeAnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_announcement_card_design,parent,false);
        HomeAnnouncementViewHolder homeAnnouncementViewHolder = new HomeAnnouncementViewHolder(view, listener);
        return homeAnnouncementViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAnnouncementViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HomeAnnouncementHelperClass homeAnnouncementHelperClass = homeAnnouncementLocations.get(position);

        holder.subject.setText(homeAnnouncementHelperClass.getSubject());
        holder.date.setText(homeAnnouncementHelperClass.getDate());
        holder.time.setText(homeAnnouncementHelperClass.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Event_Page.class);
                intent.putExtra("event_name", homeAnnouncementLocations.get(position).subject);
//                intent.putExtra("time", time);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeAnnouncementLocations.size();
    }

    public static class HomeAnnouncementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subject, date, time;
        GeneralAdapter.RecyclerViewClickListener listener;

        public HomeAnnouncementViewHolder(@NonNull View itemView, GeneralAdapter.RecyclerViewClickListener listener) {
            super(itemView);

            //Hooks
            subject = itemView.findViewById(R.id.subject);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);

            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(itemView, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }
}

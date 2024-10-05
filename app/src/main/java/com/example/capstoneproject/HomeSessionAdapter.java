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

    ArrayList<HomeSessionHelperClass> homeAnnouncementLocations;
    SessionAdapter.RecyclerViewClickListener listener;

    public HomeSessionAdapter(ArrayList<HomeSessionHelperClass> homeAnnouncementLocations, SessionAdapter.RecyclerViewClickListener listener) {
        this.homeAnnouncementLocations = homeAnnouncementLocations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_session_card_design,parent,false);
        HomeSessionViewHolder homeSessionViewHolder = new HomeSessionViewHolder(view, listener);
        return homeSessionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSessionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HomeSessionHelperClass homeSessionHelperClass = homeAnnouncementLocations.get(position);

        holder.subject.setText(homeSessionHelperClass.getSubject());
        holder.date.setText(homeSessionHelperClass.getDate());
        holder.time.setText(homeSessionHelperClass.getTime());
        holder.track.setText(homeSessionHelperClass.getTrack());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Session_Details_Page.class);
                intent.putExtra("session_name", homeAnnouncementLocations.get(position).subject);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeAnnouncementLocations.size();
    }

    public static class HomeSessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subject, date, time, track;
        SessionAdapter.RecyclerViewClickListener listener;

        public HomeSessionViewHolder(@NonNull View itemView, SessionAdapter.RecyclerViewClickListener listener) {
            super(itemView);

            //Hooks
            subject = itemView.findViewById(R.id.subject);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            track = itemView.findViewById(R.id.track);

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

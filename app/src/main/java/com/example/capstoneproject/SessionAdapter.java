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

    ArrayList<SessionHelperClass> sessions;
    RecyclerViewClickListener listener;

    public SessionAdapter(ArrayList<SessionHelperClass> sessions, RecyclerViewClickListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_card_design, parent, false);
        return new SessionViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SessionHelperClass sessionHelperClass = sessions.get(position);

        holder.event_name.setText(sessionHelperClass.getEvent_name());
        holder.track.setText(sessionHelperClass.getTrack());
        holder.time.setText(sessionHelperClass.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Session_Details_Page.class);
                intent.putExtra("event_name", sessions.get(position).event_name);
                context.startActivity(intent);
            }
        });

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Session_Details_Page.class);
                intent.putExtra("event_name", sessions.get(position).event_name);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView track, event_name, time;
        ImageButton info;
        RecyclerViewClickListener listener;

        public SessionViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            // Hooks
            track = itemView.findViewById(R.id.track);
            event_name = itemView.findViewById(R.id.event_name);
            time = itemView.findViewById(R.id.time);
            info = itemView.findViewById(R.id.event_info);

            this.listener = listener;
            info.setOnClickListener(this);
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

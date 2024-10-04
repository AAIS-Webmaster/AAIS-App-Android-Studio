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

public class GeneralAdapter extends RecyclerView.Adapter<GeneralAdapter.GeneralViewHolder> {

    ArrayList<GeneralHelperClass> generalLocations;
    RecyclerViewClickListener listener;

    public GeneralAdapter(ArrayList<GeneralHelperClass> generalLocations, RecyclerViewClickListener listener) {
        this.generalLocations = generalLocations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GeneralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.general_card_design, parent, false);
        GeneralViewHolder generalViewHolder = new GeneralViewHolder(view, listener);
        return generalViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GeneralHelperClass generalHelperClass = generalLocations.get(position);

        holder.event_name.setText(generalHelperClass.getEvent_name());
        holder.track.setText(generalHelperClass.getTrack());
        holder.time.setText(generalHelperClass.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Event_Page.class);
                intent.putExtra("event_name", generalLocations.get(position).event_name);
//                intent.putExtra("time", time);
                context.startActivity(intent);
            }
        });

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Event_Page.class);
                intent.putExtra("event_name", generalLocations.get(position).event_name);
//                intent.putExtra("time", time);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return generalLocations.size();
    }

    public static class GeneralViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView track, event_name, time;
        ImageButton info;
        RecyclerViewClickListener listener;

        public GeneralViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            //Hooks
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

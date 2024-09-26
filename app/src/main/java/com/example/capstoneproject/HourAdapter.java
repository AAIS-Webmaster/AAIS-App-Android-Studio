package com.example.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent> {

    private MyDatabaseHelper dbHelper;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private String personEmail;
    private List<HourEvent> hourEvents; // Keep track of the hour events

    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents) {
        super(context, 0, hourEvents);
        this.hourEvents = hourEvents; // Initialize hourEvents
        dbHelper = new MyDatabaseHelper(); // Initialize dbHelper here
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(), gso);

        // Get the currently signed-in user
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        if (acct != null) {
            personEmail = acct.getEmail();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View finalConvertView;
        HourEvent hourEvent = getItem(position);

        if (convertView == null) {
            finalConvertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);
        } else {
            finalConvertView = convertView;
        }

        if (hourEvent != null) {
            // Fetch events from the database
            dbHelper.getEvents(new MyDatabaseHelper.EventsRetrievalCallback() {
                @Override
                public void onEventsRetrieved(List<Event> events) {
                    if (events != null) {
                        // Filter events to match the time of the current HourEvent
                        ArrayList<Event> filteredEvents = new ArrayList<>();
                        for (Event event : events) {
//                            if (event.getDate().equals(hourEvent.getDate()) &&
//                                    event.getTime().equals(hourEvent.getTime())) {
//                                filteredEvents.add(event);
//                            }
                        }

                        // Set the hour and events for the current HourEvent
                        setHour(finalConvertView, hourEvent.getTime());
                        setEvents(finalConvertView, filteredEvents);
                    } else {
                        Toast.makeText(getContext(), "No events found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Set OnClickListener for the entire row (LinearLayout)
            finalConvertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EventEditActivity.class);
                intent.putExtra("eventTime", hourEvent.getTime().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            });
        }

        return finalConvertView;
    }

    public void updateData(List<HourEvent> newHourEvents) {
        this.hourEvents.clear();
//        this.hourEvents.addAll(newHourEvents);
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }

    private void setHour(View convertView, LocalTime time) {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedShortTime(time));
    }

    private void setEvents(View convertView, ArrayList<Event> events) {
        TextView event1 = convertView.findViewById(R.id.event1);
        TextView event2 = convertView.findViewById(R.id.event2);
        TextView event3 = convertView.findViewById(R.id.event3);

        if (events.size() == 0) {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
        } else if (events.size() == 1) {
            setEvent(event1, events.get(0));
            hideEvent(event2);
            hideEvent(event3);
        } else if (events.size() == 2) {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            hideEvent(event3);
        } else if (events.size() == 3) {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setEvent(event3, events.get(2));
        } else {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(events.size() - 2) + " More Events";
            event3.setText(eventsNotShown);
        }
    }

    private void setEvent(TextView textView, Event event) {
        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Event_Page.class);
            intent.putExtra("eventName", event.getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        });
    }

    private void hideEvent(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }
}


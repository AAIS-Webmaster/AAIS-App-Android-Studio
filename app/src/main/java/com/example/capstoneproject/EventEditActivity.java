package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;
    String personName, personEmail;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private LocalTime time;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        dbHelper = new MyDatabaseHelper();
        initWidgets();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
        }

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            time = LocalTime.parse(extra.getString("eventTime"));
            eventTimeTV.setText("Time: " + time.toString());
        }
//        time = LocalTime.now();

        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
//        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
    }

    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
    }

    public void saveEventAction(View view)
    {
        String eventName = eventNameET.getText().toString();
//        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time);
//        Event.eventsList.add(newEvent);
        // Create a list of events to send
        List<Event> events = new ArrayList<>();
//        events.add(new Event(eventNameET.getText().toString(), LocalDate.parse(eventDateTV.getText()), LocalTime.parse(eventTimeTV.getText())));
//        events.add(new Event(eventName, CalendarUtils.selectedDate, time));

//        events.add(new Event("Event 2", LocalDate.of(2024, 8, 30), LocalTime.of(11, 0)));

        // Send the events to the database
        dbHelper.sendEvents(events);

        finish();
    }
}
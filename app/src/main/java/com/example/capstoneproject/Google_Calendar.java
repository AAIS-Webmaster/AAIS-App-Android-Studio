package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class Google_Calendar extends AppCompatActivity {

    EditText title, location, description;
    Button addEvent, goCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_calendar);

        title = findViewById(R.id.etTitle);
        location = findViewById(R.id.etLocation);
        description = findViewById(R.id.etDescription);
        addEvent = findViewById(R.id.btnAdd);
        goCalendar = findViewById(R.id.btnGO);

        goCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.microsoft.office.outlook");

                if (intent != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(Google_Calendar.this, "Calendar app not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title.getText().toString().isEmpty() && !location.getText().toString().isEmpty()
                        && !description.getText().toString().isEmpty()){

                    // Set the start and end time for the event
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(2024, Calendar.SEPTEMBER, 15, 9, 0); // Example: 15th September 2024, 9:00 AM
                    long startMillis = beginTime.getTimeInMillis();

                    Calendar endTime = Calendar.getInstance();
                    endTime.set(2024, Calendar.SEPTEMBER, 15, 11, 0); // Example: 15th September 2024, 11:00 AM
                    long endMillis = endTime.getTimeInMillis();

                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE, title.getText().toString());
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location.getText().toString());
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, description.getText().toString());
//                    intent.putExtra(CalendarContract.Events.ALL_DAY, "true");
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);
                    intent.putExtra(CalendarContract.Events.HAS_ATTENDEE_DATA, "endMillis@gmail.com");
//                    intent.putExtra(Intent.EXTRA_EMAIL, "test@gmail.com, test2@gmial.com");

                    if(intent.resolveActivity(getPackageManager()) != null){
                        Toast.makeText(Google_Calendar.this, "in", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(Google_Calendar.this, "There is no app that can support this action", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Google_Calendar.this, "out", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
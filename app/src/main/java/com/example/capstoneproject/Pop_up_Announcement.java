package com.example.capstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Pop_up_Announcement extends AppCompatActivity {
    EditText title, description;
    TextView error;
    Button post, cancel;
    LocalDate localDate;
    LocalTime localTime;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement_pop_up_window);
        dbHelper = new MyDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hooks
        title = findViewById(R.id.enter_subject);
        description = findViewById(R.id.enter_description);
        error = findViewById(R.id.error);
        post = findViewById(R.id.post);
        cancel = findViewById(R.id.cancel_action);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!title.getText().toString().equals("") && !description.getText().toString().equals("")){
                    localDate = LocalDate.now();
                    localTime = LocalTime.now();

                    // Create a DateTimeFormatter instance with the desired format
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                    // Format the time
                    String formattedTime = localTime.format(timeFormatter);

                    List<Announcement> announcements = new ArrayList<>();
                    Announcement announcement = new Announcement(title.getText().toString(), description.getText().toString(),
                            localDate.toString() + " " + formattedTime);

                    announcements.add(announcement);
                    dbHelper.insertData("Announcement", announcements);
                    dbHelper.removeAllSeenAnnouncements();
//                    Intent intent = new Intent(Pop_up.this, Announcement_Page.class);
//                    intent.putExtra("key1", title.getText().toString());
//                    intent.putExtra("key2", description.getText().toString());
                    startActivity(new Intent(Pop_up_Announcement.this, Announcement_Page.class));
                }
                else if (title.getText().toString().equals("") && description.getText().toString().equals("")) {
                    error.setText("Input Required: The Subject and Description Tags are empty.");
                }
                else if (!title.getText().toString().equals("") && description.getText().toString().equals("")) {
                    error.setText("Input Required: The Description Tag is empty.");
                }
                else if (title.getText().toString().equals("") && !description.getText().toString().equals("")) {
                    error.setText("Input Required: The Subject Tag is empty.");
                }
                error.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Pop_up_Announcement.this, Announcement_Page.class));
            }
        });
    }
}

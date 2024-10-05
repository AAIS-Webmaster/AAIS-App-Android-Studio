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

public class Add_Announcement_Page extends AppCompatActivity {
    // UI components
    EditText title, description;
    TextView error;
    Button post, cancel;

    // For storing the current date and time
    LocalDate localDate;
    LocalTime localTime;

    // Helper class for interacting with the database
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement_pop_up_window);

        // Initialize the database helper
        dbHelper = new MyDatabaseHelper();

        // Hide the default ActionBar for a clean look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hooks - initialize UI components
        title = findViewById(R.id.enter_subject);  // Input field for the announcement title
        description = findViewById(R.id.enter_description);  // Input field for the announcement description
        error = findViewById(R.id.error);  // TextView for displaying input validation error messages
        post = findViewById(R.id.post);  // Button to post the announcement
        cancel = findViewById(R.id.cancel_action);  // Button to cancel and go back to the announcements page

        // Set a click listener for the "Post" button
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if both title and description fields are not empty
                if (!title.getText().toString().equals("") && !description.getText().toString().equals("")) {
                    // Get the current date and time
                    localDate = LocalDate.now();
                    localTime = LocalTime.now();

                    // Create a formatter to format the time to "HH:mm" format
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    String formattedTime = localTime.format(timeFormatter);  // Format the time

                    // Create a new announcement object with the input data and the current date/time
                    List<Announcement> announcements = new ArrayList<>();
                    Announcement announcement = new Announcement(
                            title.getText().toString(),
                            description.getText().toString(),
                            localDate.toString() + " " + formattedTime  // Store the date and time as a single string
                    );

                    // Add the new announcement to the list
                    announcements.add(announcement);

                    // Insert the announcement into the database
                    dbHelper.addAnnouncement("Announcement", announcements);

                    // Remove all seen announcements from users (reset their seen status)
                    dbHelper.removeAllSeenAnnouncements();

                    // Navigate back to the Announcement_Page after posting
                    startActivity(new Intent(Add_Announcement_Page.this, Announcement_Page.class));
                }
                // Input validation: Show appropriate error messages if fields are empty
                else if (title.getText().toString().equals("") && description.getText().toString().equals("")) {
                    error.setText("Input Required: The Subject and Description Tags are empty.");
                }
                else if (!title.getText().toString().equals("") && description.getText().toString().equals("")) {
                    error.setText("Input Required: The Description Tag is empty.");
                }
                else if (title.getText().toString().equals("") && !description.getText().toString().equals("")) {
                    error.setText("Input Required: The Subject Tag is empty.");
                }

                // Make the error message visible
                error.setVisibility(View.VISIBLE);
            }
        });

        // Set a click listener for the "Cancel" button to go back to the announcements page without posting
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the Announcement_Page when the cancel button is pressed
                startActivity(new Intent(Add_Announcement_Page.this, Announcement_Page.class));
            }
        });
    }
}
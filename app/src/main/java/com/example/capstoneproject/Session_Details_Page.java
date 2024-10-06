package com.example.capstoneproject;

import static com.example.capstoneproject.Home_Page.END_SCALE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Session_Details_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Request code for starting an activity for a result
    private static final int REQUEST_CODE = 1001;

    // Variables to hold information about a person and the session
    String personName; // Name of the person
    String personEmail; // Email of the person
    String title; // Title of the session
    String session_address; // Address of the session
    String selected_track; // Track selected for the session
    String session_location; // Location where the session will occur
    String text_chair; // Chairperson's name for the session
    String paper1_name, paper2_name, paper3_name, paper4_name = ""; // Names of up to four papers associated with the session
    LocalDate session_date; // Date of the session
    boolean sessionExists; // Flag indicating if the session exists

    // Flags indicating if papers are present for the session
    boolean paper2_isPresent, paper3_isPresent, paper4_isPresent = false;

    // Variables to hold the start and end times of the session
    long start_time, end_time;

    // UI elements for the navigation drawer and its components
    DrawerLayout drawerLayout; // Layout for the navigation drawer
    NavigationView navigationView; // Navigation view for drawer items
    ImageView menuIcon, favorite, delete, notification; // Icons for menu, favorites, delete action, and notifications
    Button unseen; // Button for unseen actions

    // ImageButtons for downloading papers
    ImageButton paper1_download, paper2_download, paper3_download, paper4_download;

    // Content view layout for the session details
    LinearLayout contentView; // Linear layout for the content display

    // Google Sign-In options and client
    GoogleSignInOptions gso; // Google Sign-In options configuration
    GoogleSignInClient gsc; // Client for Google Sign-In

    // LinkedIn Sign-In Data
    UserProfile userProfile; // LinkedIn sign-in user profile

    // TextViews for displaying session details
    TextView session_name, track, date, time, location, address, chair, paper1, paper2, paper3, paper4; // Text views for various session attributes

    // URIs for accessing paper documents
    Uri paper1_uri, paper2_uri, paper3_uri, paper4_uri; // URIs for each paper

    // Layouts for displaying additional content for each paper
    LinearLayout layout2, layout3, layout4; // Layouts for paper details

    // Database helper for managing session data
    private MyDatabaseHelper dbHelper; // Database helper for session operations

    // Handler and runnable for managing background tasks on the main thread
    private Handler handler = new Handler(Looper.getMainLooper()); // Handler to post tasks to the main thread
    private Runnable checkSessionRunnable; // Runnable for checking session existence

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details_page);
        dbHelper = new MyDatabaseHelper(); // Initialize database helper

        // Runnable to periodically check if the session exists in the user's calendar
        checkSessionRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if the session is in the calendar based on its title and timings
                sessionExists = isSessionInCalendar(title, start_time, end_time);

                // If the session exists, change the favorite icon to indicate it
                if (sessionExists) {
                    favorite.setImageResource(R.drawable.baseline_favorite_24);
                }

                // Re-run the check every 1 second (1000 milliseconds)
                handler.postDelayed(this, 1000);
            }
        };

        // Hide the action bar if it is present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize UI elements by finding them by their IDs
        session_name = findViewById(R.id.session_name); // TextView for session name
        track = findViewById(R.id.track); // TextView for the track of the session
        date = findViewById(R.id.date); // TextView for the session date
        time = findViewById(R.id.time); // TextView for the session time
        location = findViewById(R.id.location); // TextView for session location
        address = findViewById(R.id.address); // TextView for session address
        chair = findViewById(R.id.person); // TextView for the chairperson's name
        paper1 = findViewById(R.id.paper1); // TextView for the first paper's name
        paper2 = findViewById(R.id.paper2); // TextView for the second paper's name
        paper3 = findViewById(R.id.paper3); // TextView for the third paper's name
        paper4 = findViewById(R.id.paper4); // TextView for the fourth paper's name
        drawerLayout = findViewById(R.id.drawer_layout); // DrawerLayout for navigation drawer
        navigationView = findViewById(R.id.navigation_view); // NavigationView for drawer items
        menuIcon = findViewById(R.id.menu_icon); // ImageView for the menu icon
        contentView = findViewById(R.id.content); // LinearLayout for content display
        favorite = findViewById(R.id.favorite); // ImageView for favorite action
        delete = findViewById(R.id.delete); // ImageView for delete action
        notification = findViewById(R.id.notification); // ImageView for notifications
        unseen = findViewById(R.id.unseen); // Button for unseen announcements

        // Initialize ImageButtons for downloading papers
        paper1_download = findViewById(R.id.paper1_download); // ImageButton for downloading the first paper
        paper2_download = findViewById(R.id.paper2_download); // ImageButton for downloading the second paper
        paper3_download = findViewById(R.id.paper3_download); // ImageButton for downloading the third paper
        paper4_download = findViewById(R.id.paper4_download); // ImageButton for downloading the fourth paper

        // Initialize layouts for displaying additional content for papers
        layout2 = findViewById(R.id.layout2); // Layout for the second paper
        layout3 = findViewById(R.id.layout3); // Layout for the third paper
        layout4 = findViewById(R.id.layout4); // Layout for the fourth paper

        // Get the last signed-in account
        userProfile = UserProfile.getInstance();

        if (userProfile.getName() != null && userProfile.getEmail() != null){
            // Retrieve the display name and email of the signed-in user
            personName = userProfile.getName();
            personEmail = userProfile.getEmail();

            // If the user's email matches a specific address, show the delete button
            if (personEmail.equals("u3238031@uni.canberra.edu.au")) {
                delete.setVisibility(View.VISIBLE); // Show delete session button for admin
            }
        }

        else {
            // Set up Google Sign-In options
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso); // Get GoogleSignInClient

            // Get the last signed-in account
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                // Retrieve the display name and email of the signed-in user
                personName = acct.getDisplayName();
                personEmail = acct.getEmail();

                // If the user's email matches a specific address, show the delete button
                if (personEmail.equals("guptasdhuruv4@gmail.com")) {
                    delete.setVisibility(View.VISIBLE); // Show delete session button for admin
                }
            }
        }

        // Retrieve seen announcement status from the database
        dbHelper.getSeenAnnouncement(personEmail, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    // Check if the data snapshot exists
                    if (dataSnapshot.exists()) {
                        String seenStatus = dataSnapshot.getValue(String.class);
                        if (seenStatus != null) {
                            unseen.setVisibility(View.GONE); // Hide unseen button if status is present
                        }
                    } else {
                        unseen.setVisibility(View.VISIBLE); // Show unseen button if no status
                    }
                } catch (Exception e) {
                    // Handle any errors that occur during the data processing
                    System.out.println("An error occurred while processing SeenAnnouncement status: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors when retrieving the announcement status
                System.out.println("Failed to retrieve SeenAnnouncement status: " + databaseError.getMessage());
            }
        });

        // Set up delete button click listener
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog for deletion
                showDeleteConfirmationDialog();
            }
        });

        // Set up favorite button click listener
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if calendar permissions are granted
                if (ContextCompat.checkSelfPermission(Session_Details_Page.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Session_Details_Page.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // If not granted, request calendar permissions
                    ActivityCompat.requestPermissions(Session_Details_Page.this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
                } else {
                    // If the session does not exist, create a new calendar event
                    if (!sessionExists) {
                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setData(CalendarContract.Events.CONTENT_URI); // Set URI for calendar events
                        intent.putExtra(CalendarContract.Events.TITLE, title); // Set event title
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, session_location); // Set event location

                        // Build event description based on which papers are present
                        if (paper2_isPresent && paper3_isPresent && paper4_isPresent) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Track: " + selected_track + "\n"
                                    + "Session Chair: " + text_chair + "\n"
                                    + "Address: " + session_address + "\n" + "Location: " + session_location + "\n"
                                    + "Paper Details:\n 1. " + paper1_name + "\n 2. " + paper2_name + "\n 3. "
                                    + paper3_name + "\n 4. " + paper4_name);
                        } else if (paper2_isPresent && paper3_isPresent) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Track: " + selected_track + "\n"
                                    + "Session Chair: " + text_chair + "\n"
                                    + "Address: " + session_address + "\n" + "Location: " + session_location + "\n"
                                    + "Paper Details:\n 1. " + paper1_name + "\n 2. "
                                    + paper2_name + "\n 3. " + paper3_name);
                        } else if (paper2_isPresent) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Track: " + selected_track + "\n"
                                    + "Session Chair: " + text_chair + "\n"
                                    + "Address: " + session_address + "\n" + "Location: " + session_location + "\n"
                                    + "Paper Details:\n 1. " + paper1_name + "\n 2. " + paper2_name);
                        } else {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Track: " + selected_track + "\n"
                                    + "Session Chair: " + text_chair + "\n"
                                    + "Address: " + session_address + "\n" + "Location: " + session_location + "\n"
                                    + "Paper Details:\n 1. " + paper1_name);
                        }

                        // Set the event start and end time
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start_time);
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end_time);

                        // Check if there is an app to handle the intent
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent); // Start the intent to create the event
                            // Check if the session now exists in the calendar after insertion
                            sessionExists = isSessionInCalendar(title, start_time, end_time);
                            if (sessionExists) {
                                favorite.setImageResource(R.drawable.baseline_favorite_24); // Update favorite icon
                                Toast.makeText(Session_Details_Page.this, "Favourite Session!", Toast.LENGTH_SHORT).show(); // Show success message
                            }
                        } else {
                            Toast.makeText(Session_Details_Page.this, "There is no app that can support this action", Toast.LENGTH_SHORT).show(); // Show error message if no app can handle the action
                        }
                    } else {
                        // If the session already exists, remove it from the calendar
                        removeSessionFromCalendar();
                    }
                }
            }
        });

        // Set up notification button click listener
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start NotificationActivity to handle notifications
                Intent intent = new Intent(Session_Details_Page.this, Announcement_Page.class);
                startActivity(intent); // Start the notification activity
            }
        });

        // Set up unseen button click listener
        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Session_Details_Page.this, Announcement_Page.class));
            }
        });

        Bundle extra = getIntent().getExtras();

        if (extra != null){
            String finalName = extra.getString("session_name");
            dbHelper.getSessions(new MyDatabaseHelper.SessionsRetrievalCallback() {
                @Override
                public void onEventsRetrieved(List<Session> sessions) {
                    if (sessions != null) {
                        // Filter sessions to match the time of the current HourEvent
                        for (Session session : sessions) {
                            String sessionName = session.getName();

                            if (sessionName.equals(finalName)){
                                title = finalName;
                                selected_track = session.getTrack();
                                session_address = session.getAddress();
                                session_location = session.getLocation();
                                session_date = session.getDate();
                                text_chair = session.getChair();

                                if (!session.getPaper1_name().equals("")){
                                    paper1_name = session.getPaper1_name();
                                    paper1_uri = Uri.parse(session.getPaper1_url());
                                }
                                if (!session.getPaper2_name().equals("")){
                                    paper2_isPresent = true;
                                    paper2_name = session.getPaper2_name();
                                    paper2_uri = Uri.parse(session.getPaper2_url());
                                    layout2.setVisibility(View.VISIBLE);
                                }
                                if (!session.getPaper3_name().equals("")){
                                    paper3_isPresent = true;
                                    paper3_name = session.getPaper3_name();
                                    paper3_uri = Uri.parse(session.getPaper3_url());
                                    layout3.setVisibility(View.VISIBLE);
                                }
                                if (!session.getPaper4_name().equals("")){
                                    paper4_isPresent = true;
                                    paper4_name = session.getPaper4_name();
                                    paper4_uri = Uri.parse(session.getPaper4_url());
                                    layout4.setVisibility(View.VISIBLE);
                                }

                                LocalTime startTimeLocal = session.getStart_time();
                                LocalTime endTimeLocal = session.getEnd_time();

                                // Define the custom format
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                                // Format the date
                                String formattedDate = session_date.format(formatter).toUpperCase();

                                // Convert LocalTime and LocalDate to milliseconds since epoch
                                LocalDateTime startDateTime = LocalDateTime.of(session_date, startTimeLocal);
                                LocalDateTime endDateTime = LocalDateTime.of(session_date, endTimeLocal);

                                int start_day = startDateTime.getDayOfMonth();
                                int start_year = startDateTime.getYear();
                                int start_hour = startDateTime.getHour();
                                int start_min = startDateTime.getMinute();

                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(start_year, Calendar.DECEMBER, start_day, start_hour, start_min); // Example: 15th September 2024, 9:00 AM
                                long startMillis = beginTime.getTimeInMillis();

                                int end_day = endDateTime.getDayOfMonth();
                                int end_year = endDateTime.getYear();
                                int end_hour = endDateTime.getHour();
                                int end_min = endDateTime.getMinute();

                                Calendar endTime = Calendar.getInstance();
                                endTime.set(end_year, Calendar.DECEMBER, end_day, end_hour, end_min); // Example: 15th September 2024, 9:00 AM
                                long endMillis = endTime.getTimeInMillis();

                                start_time = startMillis;
                                end_time = endMillis;
                                session_name.setText(title);
                                track.setText(selected_track);
                                date.setText(formattedDate);
                                time.setText(start_hour + ":" + start_min + "0 - " + end_hour + ":" + end_min + "0");
                                location.setText(session_location);
                                address.setText(session_address);
                                chair.setText(text_chair);
                                paper1.setText(paper1_name);
                                paper2.setText(paper2_name);
                                paper3.setText(paper3_name);
                                paper4.setText(paper4_name);

                                // Check if the READ_CALENDAR and WRITE_CALENDAR permissions are granted
                                if (ContextCompat.checkSelfPermission(Session_Details_Page.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(Session_Details_Page.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                    // If not, request the permissions
                                    ActivityCompat.requestPermissions(Session_Details_Page.this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
                                } else {
                                    // Permissions are already granted, check session existence
                                    // Start the first check
                                    handler.post(checkSessionRunnable);
                                }

                                if (ContextCompat.checkSelfPermission(Session_Details_Page.this, Manifest.permission.WRITE_CALENDAR)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Request the permission
                                    ActivityCompat.requestPermissions(Session_Details_Page.this,
                                            new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
                                }

                            }
                        }

                    } else {
                        Toast.makeText(Session_Details_Page.this, "No sessions found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Session_Details_Page.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set an onClickListener for the paper1 download button
        paper1_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create an intent to view the URL of paper1
                    Intent intent = new Intent(Intent.ACTION_VIEW, paper1_uri);
                    startActivity(intent); // Start the activity to open the URL
                } catch (Exception e){
                    // Show a toast message if unable to load the URL
                    Toast.makeText(Session_Details_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

// Set an onClickListener for the paper2 download button
        paper2_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create an intent to view the URL of paper2
                    Intent intent = new Intent(Intent.ACTION_VIEW, paper2_uri);
                    startActivity(intent); // Start the activity to open the URL
                } catch (Exception e){
                    // Show a toast message if unable to load the URL
                    Toast.makeText(Session_Details_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

// Set an onClickListener for the paper3 download button
        paper3_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create an intent to view the URL of paper3
                    Intent intent = new Intent(Intent.ACTION_VIEW, paper3_uri);
                    startActivity(intent); // Start the activity to open the URL
                } catch (Exception e){
                    // Show a toast message if unable to load the URL
                    Toast.makeText(Session_Details_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

// Set an onClickListener for the paper4 download button
        paper4_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create an intent to view the URL of paper4
                    Intent intent = new Intent(Intent.ACTION_VIEW, paper4_uri);
                    startActivity(intent); // Start the activity to open the URL
                } catch (Exception e){
                    // Show a toast message if unable to load the URL
                    Toast.makeText(Session_Details_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

// Check if the READ_CALENDAR and WRITE_CALENDAR permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // If permissions are not granted, request them
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
        } else {
            // Permissions are already granted, check if the session exists in the calendar
            sessionExists = isSessionInCalendar(title, start_time, end_time);
            if (sessionExists) {
                // If session exists, set favorite icon and show a toast message
                favorite.setImageResource(R.drawable.baseline_favorite_24);
                Toast.makeText(this, "Favourite Session!", Toast.LENGTH_SHORT).show();
            }
        }

// Call the method to set up the navigation drawer
        navigationDrawer();
    }

    // Override onDestroy to clean up resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks from the handler
        handler.removeCallbacks(checkSessionRunnable);
    }

    // Show a confirmation dialog before deleting a session
    private void showDeleteConfirmationDialog() {
        // Create Date objects for start and end times
        Date startTime = new Date(start_time);
        Date endTime = new Date(end_time);

        // Format the time to "HH:mm" using SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // Format the start and end times to strings
        String start = sdf.format(startTime);
        String end = sdf.format(endTime);

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(Session_Details_Page.this);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);

        // Initialize the custom views in the dialog layout
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        Button confirmButton = dialogView.findViewById(R.id.btn_confirm);

        // Set the message in the dialog based on available papers
        if (paper1_name != null && paper2_name != null &&
                paper3_name != null && paper4_name != null){
            messageTextView.setText("Are you sure you want to delete this Conference?" +
                    "\n\nSession Name: " + title +
                    "\nLocation: " + session_address + " " + session_location +
                    "\nDate: " + session_date +
                    "\nTime: " + start + " - " + end +
                    "\nSession Chair: " + text_chair +
                    "\nPapers:\n\t\t1. " + paper1_name + "\n\t\t2. " + paper2_name +
                    "\n\t\t3. " + paper3_name + "\n\t\t4. " + paper4_name);
        }
        else if (paper1_name != null && paper2_name != null &&
                paper3_name != null){
            messageTextView.setText("Are you sure you want to delete this Conference?" +
                    "\n\nSession Name: " + title +
                    "\nLocation: " + session_address + " " + session_location +
                    "\nDate: " + session_date +
                    "\nTime: " + start + " - " + end +
                    "\nSession Chair: " + text_chair +
                    "\nPapers:\n\t\t1. " + paper1_name + "\n\t\t2. " + paper2_name + "\n\t\t3. " + paper3_name);
        }
        else if (paper1_name != null && paper2_name != null){
            messageTextView.setText("Are you sure you want to delete this Conference?" +
                    "\n\nSession Name: " + title +
                    "\nLocation: " + session_address + " " + session_location +
                    "\nDate: " + session_date +
                    "\nTime: " + start + " - " + end +
                    "\nSession Chair: " + text_chair +
                    "\nPapers:\n\t\t1. " + paper1_name + "\n\t\t2. " + paper2_name);
        }
        else if (paper1_name != null){
            messageTextView.setText("Are you sure you want to delete this Conference?" +
                    "\n\nSession Name: " + title +
                    "\nLocation: " + session_address + " " + session_location +
                    "\nDate: " + session_date +
                    "\nTime: " + start + " - " + end +
                    "\nSession Chair: " + text_chair +
                    "\nPapers:\n\t\t1. " + paper1_name);
        }

        // Create and show the dialog using AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Session_Details_Page.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set click listeners for the cancel and confirm buttons
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close dialog if canceled
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the session from the database
                dbHelper.deleteSession(title, String.valueOf(session_date), start, end, text_chair);
                dialog.dismiss(); // Close dialog after confirming
                // Redirect to the Session_Page activity
                startActivity(new Intent(Session_Details_Page.this, Session_Page.class));
            }
        });

        // Show the dialog
        dialog.show();
    }

    // Check if a session exists in the calendar
    public boolean isSessionInCalendar(String sessionTitle, long startTime, long endTime) {
        // Define the projection for the query
        String[] proj =
                new String[]{
                        Instances._ID,
                        Instances.BEGIN,
                        Instances.END,
                        Instances.EVENT_ID};
        boolean sessionExists;
        try (Cursor cursor = Instances.query(getContentResolver(), proj, startTime, endTime, sessionTitle)) {
            sessionExists = false; // Initialize sessionExists to false
            if (cursor.getCount() > 0) {
                sessionExists = true; // If records found, set sessionExists to true
            }
        }

        return sessionExists; // Return the result
    }

    // Remove a session from the calendar
    @SuppressLint("Range")
    private void removeSessionFromCalendar() {
        // Retrieve the event ID for the session
        String[] proj = new String[]{
                Instances.EVENT_ID
        };
        long eventId = -1; // Initialize eventId to an invalid value
        try (Cursor cursor = Instances.query(getContentResolver(), proj, start_time, end_time, title)) {
            if (cursor.moveToFirst()) {
                eventId = cursor.getLong(cursor.getColumnIndex(Instances.EVENT_ID)); // Get the event ID
            }
        }

        // If a valid event ID was found, attempt to delete it
        if (eventId != -1) {
            Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
            int rowsDeleted = getContentResolver().delete(deleteUri, null, null);
            if (rowsDeleted > 0) {
                // Show success message if session is removed
                Toast.makeText(this, "Session removed from calendar", Toast.LENGTH_SHORT).show();
                favorite.setImageResource(R.drawable.baseline_favorite_border_24); // Update favorite icon
                sessionExists = false; // Update session existence status
            } else {
                // Show failure message if unable to remove session
                Toast.makeText(this, "Failed to remove session", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show message if session not found
            Toast.makeText(this, "Session not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Check if the session exists in the calendar after permission is granted
                boolean sessionExists = isSessionInCalendar(title, start_time, end_time);

                if (sessionExists) {
                    favorite.setImageResource(R.drawable.baseline_favorite_24); // Set favorite icon
                    Toast.makeText(this, "Favourite Session!", Toast.LENGTH_SHORT).show(); // Show toast message
                }

            } else {
                // Show message if permission is denied
                Toast.makeText(this, "Permission Denied to Read Calendar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle back button press for navigation drawer
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer if it's open
        }
        else super.onBackPressed(); // Default back press behavior
    }

    // Method to initialize and handle the navigation drawer
    private void navigationDrawer() {
        // Bring the navigation view to the front and set its item selected listener
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        // Handle menu icon click to open/close the drawer
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);  // Close drawer if visible
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);  // Open drawer if hidden
                }
            }
        });

        // Add animation to the navigation drawer
        animateNavigationDrawer();
    }

    // Method to animate the navigation drawer opening and closing
    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Apply scaling and translation effect on the content view as the drawer slides
                final float diffScaleOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaleOffset;
                contentView.setScaleX(offsetScale);  // Scale X-axis
                contentView.setScaleY(offsetScale);  // Scale Y-axis

                // Translate content view to the right as the drawer opens
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaleOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    // Handle item selection in the navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Navigate to the appropriate activity based on the selected item
        if(item.toString().equals("Home")){
            startActivity(new Intent(Session_Details_Page.this, Home_Page.class));
        }
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(Session_Details_Page.this, Session_Page.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Session_Details_Page.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Session_Details_Page.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Committee")){
            startActivity(new Intent(Session_Details_Page.this, Organising_Committee_Page.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Session_Details_Page.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Session_Details_Page.this, About_Page.class));
        }
        if(item.toString().equals("Sign Out")){
            try {
                // Clear stored access token
                SharedPreferences sharedPreferences = getSharedPreferences("YourAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(userProfile.getToken());
                editor.apply();

                // Clear any other user data
                UserProfile.getInstance().clearUserProfile(); // Implement this method to clear user profile data

                // Redirect user to the login screen or homepage
                Intent intent = new Intent(Session_Details_Page.this, Sign_In_Page.class); // Change to your login activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception ignored){}

            try {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(Session_Details_Page.this, Sign_In_Page.class));
                    }
                });
            } catch (Exception ignored){}
        }
        return true;
    }
}
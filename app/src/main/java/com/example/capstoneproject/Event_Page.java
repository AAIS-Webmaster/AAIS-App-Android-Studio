package com.example.capstoneproject;

import static com.example.capstoneproject.Home.END_SCALE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
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
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Event_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int REQUEST_CODE = 1001;
    String personName, personEmail, title, event_address, event_location,
            text_chair, paper1_name, paper2_name, paper3_name, paper4_name = "";
    LocalDate event_date;
    boolean eventExists;
    boolean paper2_isPresent, paper3_isPresent, paper4_isPresent = false;
    long start_time, end_time;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon, favorite, delete, notification;
    Button unseen;
    ImageButton paper1_download, paper2_download, paper3_download, paper4_download;
    LinearLayout contentView;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView event_name, date, time, location, address, chair, paper1, paper2, paper3, paper4;
    Uri paper1_uri, paper2_uri, paper3_uri, paper4_uri;
    LinearLayout layout2, layout3, layout4;
    private MyDatabaseHelper dbHelper;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable checkEventRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        dbHelper = new MyDatabaseHelper();

        checkEventRunnable = new Runnable() {
            @Override
            public void run() {
                eventExists = isEventInCalendar(title, start_time, end_time);
                if (eventExists) {
                    favorite.setImageResource(R.drawable.baseline_favorite_24);
                }
                // Re-run the check every 1 second (1000 milliseconds)
                handler.postDelayed(this, 1000);
            }
        };

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String name = "User";

        event_name = findViewById(R.id.event_name);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);
        address = findViewById(R.id.address);
        chair = findViewById(R.id.person);
        paper1 = findViewById(R.id.paper1);
        paper2 = findViewById(R.id.paper2);
        paper3 = findViewById(R.id.paper3);
        paper4 = findViewById(R.id.paper4);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
//        userIcon = findViewById(R.id.notification);
        contentView = findViewById(R.id.content);
        favorite = findViewById(R.id.favorite);
        delete = findViewById(R.id.delete);
        notification = findViewById(R.id.notification);
        unseen = findViewById(R.id.unseen);

        paper1_download = findViewById(R.id.paper1_download);
        paper2_download = findViewById(R.id.paper2_download);
        paper3_download = findViewById(R.id.paper3_download);
        paper4_download = findViewById(R.id.paper4_download);

        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();

            if (personEmail.equals("guptasdhuruv4@gmail.com")) {
                delete.setVisibility(View.VISIBLE);
            }
        }

        dbHelper.getSeenAnnouncement(personEmail, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        String seenStatus = dataSnapshot.getValue(String.class);
                        if (seenStatus != null) {
                            unseen.setVisibility(View.GONE);
                        }
                    } else {
                        unseen.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred while processing SeenAnnouncement status: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Failed to retrieve SeenAnnouncement status: " + databaseError.getMessage());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the READ_CALENDAR and WRITE_CALENDAR permissions are granted
                if (ContextCompat.checkSelfPermission(Event_Page.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Event_Page.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // If not, request the permissions
                    ActivityCompat.requestPermissions(Event_Page.this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
                } else {
                    if (!eventExists) {
                        Intent intent = new Intent(Intent.ACTION_INSERT);
//                        intent.setPackage("com.google.android.calendar");
                        intent.setData(CalendarContract.Events.CONTENT_URI);
                        intent.putExtra(CalendarContract.Events.TITLE, title);
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event_location);
                        if (paper2_isPresent && paper3_isPresent && paper4_isPresent) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Session Chair: " + text_chair + "\n"
                                    + "Address: " + event_address + "\n" + "Location: " + event_location + "\n"
                                    + "Paper Details:\n 1. " + paper1_name + "\n 2. " + paper2_name + "\n 3. "
                                    + paper3_name + "\n 4. " + paper4_name);
                        } else if (paper2_isPresent && paper3_isPresent) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Session Chair: " + text_chair + "\n"
                                    + "Address: " + event_address + "\n" + "Location: " + event_location + "\n"
                                    + "Paper Details:\n 1. " + paper1_name + "\n 2. "
                                    + paper2_name + "\n 3. " + paper3_name);
                        } else if (paper2_isPresent) {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Session Chair: " + text_chair + "\n"
                                    + "Address: " + event_address + "\n" + "Location: " + event_location + "\n"
                                    + "Paper Details:\n 1. " + paper1_name + "\n 2. " + paper2_name);
                        } else {
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Session Chair: " + text_chair + "\n"
                                    + "Address: " + event_address + "\n" + "Location: " + event_location + "\n"
                                    + "Paper Details:\n 1. " + paper1_name);
                        }

//                    intent.putExtra(CalendarContract.Events.ALL_DAY, "true");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start_time);
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end_time);
//                    intent.putExtra(Intent.EXTRA_EMAIL, "test@gmail.com, test2@gmial.com");

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                            eventExists = isEventInCalendar(title, start_time, end_time);
                            if (eventExists) {
                                favorite.setImageResource(R.drawable.baseline_favorite_24);
                                Toast.makeText(Event_Page.this, "Favourite Event!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Event_Page.this, "There is no app that can support this action", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        removeEventFromCalendar();
                    }
                }
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Event_Page.this, Announcement_Page.class));
            }
        });

        Bundle extra = getIntent().getExtras();

        if (extra != null){
            name = extra.getString("event_name");
            String finalName = name;
            dbHelper.getEvents(new MyDatabaseHelper.EventsRetrievalCallback() {
                @Override
                public void onEventsRetrieved(List<Event> events) {
                    if (events != null) {
                        // Filter events to match the time of the current HourEvent
                        for (Event event : events) {
                            String eventName = event.getName();
//                        Toast.makeText(HomePage.this, current_date.toString() + " " + date.toString(), Toast.LENGTH_SHORT).show();

                            if (eventName.equals(finalName)){
                                title = finalName;
                                event_address = event.getAddress();
                                event_location = event.getLocation();
                                event_date = event.getDate();
                                text_chair = event.getChair();

                                if (!event.getPaper1_name().equals("")){
                                    paper1_name = event.getPaper1_name();
                                    paper1_uri = Uri.parse(event.getPaper1_url());
                                }
                                if (!event.getPaper2_name().equals("")){
                                    paper2_isPresent = true;
                                    paper2_name = event.getPaper2_name();
                                    paper2_uri = Uri.parse(event.getPaper2_url());
                                    layout2.setVisibility(View.VISIBLE);
                                }
                                if (!event.getPaper3_name().equals("")){
                                    paper3_isPresent = true;
                                    paper3_name = event.getPaper3_name();
                                    paper3_uri = Uri.parse(event.getPaper3_url());
                                    layout3.setVisibility(View.VISIBLE);
                                }
                                if (!event.getPaper4_name().equals("")){
                                    paper4_isPresent = true;
                                    paper4_name = event.getPaper4_name();
                                    paper4_uri = Uri.parse(event.getPaper4_url());
                                    layout4.setVisibility(View.VISIBLE);
                                }

                                LocalTime startTimeLocal = event.getStart_time();
                                LocalTime endTimeLocal = event.getEnd_time();

                                // Define the custom format
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                                // Format the date
                                String formattedDate = event_date.format(formatter).toUpperCase();

                                // Convert LocalTime and LocalDate to milliseconds since epoch
                                LocalDateTime startDateTime = LocalDateTime.of(event_date, startTimeLocal);
                                LocalDateTime endDateTime = LocalDateTime.of(event_date, endTimeLocal);

                                int start_day = startDateTime.getDayOfMonth();
                                Month start_month = startDateTime.getMonth();
                                int start_year = startDateTime.getYear();
                                int start_hour = startDateTime.getHour();
                                int start_min = startDateTime.getMinute();
//                                long startMillis = startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
//                                long endMillis = endDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(start_year, Calendar.DECEMBER, start_day, start_hour, start_min); // Example: 15th September 2024, 9:00 AM
                                long startMillis = beginTime.getTimeInMillis();

                                int end_day = endDateTime.getDayOfMonth();
                                Month end_month = endDateTime.getMonth();
                                int end_year = endDateTime.getYear();
                                int end_hour = endDateTime.getHour();
                                int end_min = endDateTime.getMinute();

                                Calendar endTime = Calendar.getInstance();
                                endTime.set(end_year, Calendar.DECEMBER, end_day, end_hour, end_min); // Example: 15th September 2024, 9:00 AM
                                long endMillis = endTime.getTimeInMillis();

                                start_time = startMillis;
                                end_time = endMillis;
                                event_name.setText(title);
                                date.setText(formattedDate);
                                time.setText(start_hour + ":" + start_min + "0 - " + end_hour + ":" + end_min + "0");
                                location.setText(event_location);
                                address.setText(event_address);
                                chair.setText(text_chair);
                                paper1.setText(paper1_name);
                                paper2.setText(paper2_name);
                                paper3.setText(paper3_name);
                                paper4.setText(paper4_name);

                                // Check if the READ_CALENDAR and WRITE_CALENDAR permissions are granted
                                if (ContextCompat.checkSelfPermission(Event_Page.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(Event_Page.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                    // If not, request the permissions
                                    ActivityCompat.requestPermissions(Event_Page.this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
                                } else {
                                    // Permissions are already granted, check event existence
                                    // Start the first check
                                    handler.post(checkEventRunnable);
                                }

                                if (ContextCompat.checkSelfPermission(Event_Page.this, Manifest.permission.WRITE_CALENDAR)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Request the permission
                                    ActivityCompat.requestPermissions(Event_Page.this,
                                            new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
                                }

                            }
                        }

                    } else {
                        Toast.makeText(Event_Page.this, "No events found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Event_Page.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        paper1_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, paper1_uri);
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(Event_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        paper2_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, paper2_uri);
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(Event_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        paper3_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, paper3_uri);
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(Event_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        paper4_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, paper4_uri);
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(Event_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check if the READ_CALENDAR and WRITE_CALENDAR permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // If not, request the permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
        } else {
            // Permissions are already granted, check event existence
            eventExists = isEventInCalendar(title, start_time, end_time);
            if (eventExists) {
                favorite.setImageResource(R.drawable.baseline_favorite_24);
                Toast.makeText(this, "Favourite Event!", Toast.LENGTH_SHORT).show();
            }
        }

        navigationDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(checkEventRunnable);
    }

    private void showDeleteConfirmationDialog() {
//        Context context = new Event_Page();

        // Create a Date object from the timestamp
        Date startTime = new Date(start_time);
        Date endTime = new Date(end_time);

        // Use SimpleDateFormat to format the date object to "HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // Return the formatted time string
        String start = sdf.format(startTime);
        String end = sdf.format(endTime);

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(Event_Page.this);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);

        // Initialize the custom views
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        Button confirmButton = dialogView.findViewById(R.id.btn_confirm);

        if (paper1_name != null && paper2_name != null &&
                paper3_name != null && paper4_name != null){
            // Set dialog message
            messageTextView.setText("Are you sure you want to delete this Conference?" +
                    "\n\nEvent Name: " + title +
                    "\nLocation: " + event_address + " " + event_location +
                    "\nDate: " + event_date +
                    "\nTime: " + start + " - " + end +
                    "\nSession Chair: " + text_chair +
                    "\nPapers:\n\t\t1. " + paper1_name + "\n\t\t2. " + paper2_name +
                    "\n\t\t3. " + paper3_name + "\n\t\t4. " + paper4_name);
        }
        else if (paper1_name != null && paper2_name != null &&
                paper3_name != null){
            // Set dialog message
            messageTextView.setText("Are you sure you want to delete this Conference?" +
                    "\n\nEvent Name: " + title +
                    "\nLocation: " + event_address + " " + event_location +
                    "\nDate: " + event_date +
                    "\nTime: " + start + " - " + end +
                    "\nSession Chair: " + text_chair +
                    "\nPapers:\n\t\t1. " + paper1_name + "\n\t\t2. " + paper2_name + "\n\t\t3. " + paper3_name);
        }
        else if (paper1_name != null && paper2_name != null){
            // Set dialog message
            messageTextView.setText("Are you sure you want to delete this Conference?" +
                    "\n\nEvent Name: " + title +
                    "\nLocation: " + event_address + " " + event_location +
                    "\nDate: " + event_date +
                    "\nTime: " + start + " - " + end +
                    "\nSession Chair: " + text_chair +
                    "\nPapers:\n\t\t1. " + paper1_name + "\n\t\t2. " + paper2_name);
        }
        else if (paper1_name != null){
            // Set dialog message
            messageTextView.setText("Are you sure you want to delete this Conference?" +
                    "\n\nEvent Name: " + title +
                    "\nLocation: " + event_address + " " + event_location +
                    "\nDate: " + event_date +
                    "\nTime: " + start + " - " + end +
                    "\nSession Chair: " + text_chair +
                    "\nPapers:\n\t\t1. " + paper1_name);
        }

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(Event_Page.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close dialog if canceled
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteEvent(title, String.valueOf(event_date), start, end, text_chair);
                dialog.dismiss(); // Close dialog after confirming
                startActivity(new Intent(Event_Page.this, HomePage.class));
            }
        });

        dialog.show();
    }

    public boolean isEventInCalendar(String eventTitle, long startTime, long endTime) {
        String[] proj =
                new String[]{
                        Instances._ID,
                        Instances.BEGIN,
                        Instances.END,
                        Instances.EVENT_ID};
        boolean eventExists;
        try (Cursor cursor = Instances.query(getContentResolver(), proj, startTime, endTime, eventTitle)) {
            eventExists = false;
            if (cursor.getCount() > 0) {
                eventExists = true;
            }
        }

        return eventExists;
    }

    @SuppressLint("Range")
    private void removeEventFromCalendar() {
        // Retrieve the event ID
        String[] proj = new String[]{
                Instances.EVENT_ID
        };
        long eventId = -1;
        try (Cursor cursor = Instances.query(getContentResolver(), proj, start_time, end_time, title)) {
            if (cursor.moveToFirst()) {
                eventId = cursor.getLong(cursor.getColumnIndex(Instances.EVENT_ID));
            }
        }

        if (eventId != -1) {
            Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
            int rowsDeleted = getContentResolver().delete(deleteUri, null, null);
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Event removed from calendar", Toast.LENGTH_SHORT).show();
                favorite.setImageResource(R.drawable.baseline_favorite_border_24);
                eventExists = false;
            } else {
                Toast.makeText(this, "Failed to remove event", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean eventExists = isEventInCalendar(title, start_time, end_time);

                if (eventExists) {
                    favorite.setImageResource(R.drawable.baseline_favorite_24);
                    Toast.makeText(this, "Favourite Event!", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Permission denied, show a message or handle appropriately
                Toast.makeText(this, "Permission Denied to Read Calendar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Navigation Drawer Functions
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else super.onBackPressed();
    }

    private void navigationDrawer() {
        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else{drawerLayout.openDrawer(GravityCompat.START);}
            }
        });
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaleOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaleOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaleOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.toString().equals("Home")){
            startActivity(new Intent(Event_Page.this, Home.class));
        }
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(Event_Page.this, HomePage.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Event_Page.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Event_Page.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Organising Committee")){
            startActivity(new Intent(Event_Page.this, Organising_Committee.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Event_Page.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Event_Page.this, About_Page.class));
        }
        if(item.toString().equals("Sign Out")){
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(Event_Page.this, MainActivity.class));
                }
            });
        }
        return true;
    }
}
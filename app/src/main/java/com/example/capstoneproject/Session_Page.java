package com.example.capstoneproject;

import static com.example.capstoneproject.CalendarUtils.daysInWeekArray;
import static com.example.capstoneproject.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Session_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CalendarAdapter.OnItemListener {
    static final float END_SCALE = 0.7f; // Scale factor for the drawer animation
    GoogleSignInOptions gso; // Google Sign-In options
    GoogleSignInClient gsc; // Google Sign-In client
    UserProfile userProfile; // LinkedIn sign-in user profile
    String personName, personEmail; // Variables to hold user's name and email
    DrawerLayout drawerLayout; // Drawer layout for navigation
    NavigationView navigationView; // Navigation view for the drawer
    ImageView menuIcon, notification; // UI elements for menu and notification
    Button unseen; // Button for unseen announcements
    LinearLayout contentView, error; // Layouts for content and error messages
    RecyclerView generalRecycler; // RecyclerView for displaying sessions
    RecyclerView.Adapter adapter; // Adapter for the RecyclerView
    private MyDatabaseHelper dbHelper; // Database helper for database operations
    private SessionAdapter.RecyclerViewClickListener listener; // Listener for RecyclerView item clicks
    ArrayList<String> pos; // ArrayList for positions (not used in the provided code)
    FloatingActionButton AddSession; // Floating action button for adding sessions
    private TextView monthYearText; // TextView for displaying month and year
    private RecyclerView calendarRecyclerView; // RecyclerView for the calendar view
    Boolean session_present = false; // Flag to check if sessions are present

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_page); // Set the layout for the activity
        dbHelper = new MyDatabaseHelper(); // Initialize database helper

        // Hide the default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hooks for UI components
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        notification = findViewById(R.id.notification);
        contentView = findViewById(R.id.content);
        error = findViewById(R.id.error);
        generalRecycler = findViewById(R.id.general_recycle);
        AddSession = findViewById(R.id.addSession);
        unseen = findViewById(R.id.unseen);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

        // Get the last signed-in account
        userProfile = UserProfile.getInstance();

        // Retrieve the admin emails from values/strings.xml
        String[] adminEmailsArray = getResources().getStringArray(R.array.admin_email_list);
        List<String> adminEmailsList = Arrays.asList(adminEmailsArray);

        if (userProfile.getName() != null && userProfile.getEmail() != null){
            personName = userProfile.getName(); // Get user's name
            personEmail = userProfile.getEmail(); // Get user's email
        }

        else {
            // Set up Google Sign-In options and client
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso);

            // Get the last signed-in account
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                personName = acct.getDisplayName(); // Get user's name
                personEmail = acct.getEmail(); // Get user's email
            }
        }

        // Show the AddSession button only for a specific email
        if (adminEmailsList.contains(personEmail)) {
            AddSession.setVisibility(View.VISIBLE); // Show add session button for admin
        }

        // Check if the user has seen announcements
        dbHelper.getSeenAnnouncement(personEmail, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        String seenStatus = dataSnapshot.getValue(String.class); // Get seen status
                        if (seenStatus != null) {
                            unseen.setVisibility(View.GONE); // Hide unseen button if seen
                        }
                    } else {
                        unseen.setVisibility(View.VISIBLE); // Show unseen button if not seen
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

        // Set up notification button click listener
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Session_Page.this, Announcement_Page.class)); // Start Announcement_Page
            }
        });

        // Set up unseen button click listener
        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Session_Page.this, Announcement_Page.class)); // Start Announcement_Page
            }
        });

        // Set up AddSession button click listener
        AddSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Session_Page.this, Add_Session_Page.class); // Create intent for Add_Session_Page
                intent.putExtra("date", Integer.valueOf(CalendarUtils.selectedDate.getDayOfMonth())); // Pass selected date
                intent.putExtra("month", Integer.valueOf(CalendarUtils.selectedDate.getMonthValue())); // Pass selected month
                intent.putExtra("year", Integer.valueOf(CalendarUtils.selectedDate.getYear())); // Pass selected year
                startActivity(intent); // Start Add_Session_Page
            }
        });

        navigationDrawer(); // Set up the navigation drawer
        setWeekView(); // Initialize the week view
    }

    // Handle back button pressed event
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer if it is open
        } else {
            super.onBackPressed(); // Otherwise, execute default back press action
        }
    }

    private void navigationDrawer() {
        // Set up navigation drawer
        navigationView.bringToFront(); // Bring navigation view to the front
        navigationView.setNavigationItemSelectedListener(this); // Set listener for navigation items
        navigationView.setCheckedItem(R.id.nav_session); // Check the current item

        // Set up menu icon click listener to open/close the drawer
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START); // Close drawer if it is open
                } else {
                    drawerLayout.openDrawer(GravityCompat.START); // Open drawer if it is closed
                }
            }
        });

        animateNavigationDrawer(); // Animate drawer on opening/closing
    }

    private void animateNavigationDrawer() {
        // Add a listener to handle drawer slide animations
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaleOffset = slideOffset * (1 - END_SCALE); // Calculate the scale offset
                final float offsetScale = 1 - diffScaleOffset; // Calculate the new scale for content
                contentView.setScaleX(offsetScale); // Scale content view horizontally
                contentView.setScaleY(offsetScale); // Scale content view vertically

                final float xOffset = drawerView.getWidth() * slideOffset; // Calculate x offset
                final float xOffsetDiff = contentView.getWidth() * diffScaleOffset / 2; // Calculate difference
                final float xTranslation = xOffset - xOffsetDiff; // Calculate translation for the content view
                contentView.setTranslationX(xTranslation); // Apply translation to content view
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation item selections
        if(item.toString().equals("Home")){
            startActivity(new Intent(Session_Page.this, Home_Page.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Session_Page.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Session_Page.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Committee")){
            startActivity(new Intent(Session_Page.this, Organising_Committee_Page.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Session_Page.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Session_Page.this, About_Page.class));
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
                Intent intent = new Intent(Session_Page.this, Sign_In_Page.class); // Change to your login activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception ignored){}

            try {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(Session_Page.this, Sign_In_Page.class));
                    }
                });
            } catch (Exception ignored){}
        }
        return true;
    }

    private void setWeekView() {
        // Set up the calendar view for the current week
        if (CalendarUtils.selectedDate == null) {
            LocalDate firstDayOfDecember = LocalDate.of(LocalDate.now().getYear(), 12, 1);
            CalendarUtils.selectedDate = firstDayOfDecember.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

            // Adjust to the Monday of the week
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        showSessions(CalendarUtils.selectedDate);

        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        showSessions(date);
        setWeekView();
    }

    private void showSessions(LocalDate current_date) {
        generalRecycler.setHasFixedSize(true);
        generalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayList<SessionHelperClass> generalLocations = new ArrayList<>();
        session_present = false;

        dbHelper.getSessions(new MyDatabaseHelper.SessionsRetrievalCallback() {
            @Override
            public void onEventsRetrieved(List<Session> sessions) {
                ArrayList<SessionHelperClass> generalLocations = new ArrayList<>();
                session_present = false;
                if (sessions != null && !sessions.isEmpty()) {
                    // Sort and display sessionS
                    Collections.sort(sessions, new Comparator<Session>() {
                        @Override
                        public int compare(Session session1, Session session2) {
                            return session1.getStart_time().compareTo(session2.getStart_time());
                        }
                    });

                    for (Session session : sessions) {
                        LocalDate date = session.getDate();
                        String time = session.getStart_time().toString() + " - " + session.getEnd_time().toString();

                        if (date.equals(current_date)) {
                            session_present = true;
                            generalLocations.add(new SessionHelperClass("Track: " + session.getTrack(), session.getName(), time));
                        }
                    }

                    if (session_present) {
                        error.setVisibility(View.GONE);
                    } else {
                        error.setVisibility(View.VISIBLE);
                    }

                    adapter = new SessionAdapter(generalLocations, listener);
                    generalRecycler.setAdapter(adapter);

                } else {
                    Toast.makeText(Session_Page.this, "No sessionS found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(Session_Page.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
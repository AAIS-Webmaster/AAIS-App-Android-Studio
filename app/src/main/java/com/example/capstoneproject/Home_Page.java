package com.example.capstoneproject;

import android.content.Intent;
import android.net.Uri;
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
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Home_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // UI elements
    TextView user, user_icon_text; // User welcome message and icon text
    Button unseen; // Button for unseen announcements
    static final float END_SCALE = 0.7f; // Constant for drawer animation scaling
    GoogleSignInOptions gso; // Google sign-in options
    GoogleSignInClient gsc; // Google sign-in client
    String personName, personEmail; // User's name and email
    DrawerLayout drawerLayout; // Navigation drawer layout
    CardView track, check_in, site_map; // Card views for different functionalities
    NavigationView navigationView; // Navigation view for the drawer
    ImageView menuIcon, notification, userIcon; // Icons for menu and notifications
    LinearLayout contentView; // Main content view
    RecyclerView sessionRecycler; // Recycler view for displaying sessions
    RecyclerView.Adapter adapter; // Adapter for the Recycler view
    private MyDatabaseHelper dbHelper; // Database helper instance
    private SessionAdapter.RecyclerViewClickListener listener; // Listener for Recycler view clicks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call to the parent class's onCreate method
        setContentView(R.layout.activity_home); // Set the layout for the activity
        dbHelper = new MyDatabaseHelper(); // Initialize the database helper

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize menu hooks (UI elements)
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        userIcon = findViewById(R.id.user_icon);
        user_icon_text = findViewById(R.id.user_icon_text);
        notification = findViewById(R.id.notification);
        contentView = findViewById(R.id.content);
        sessionRecycler = findViewById(R.id.announcement_recycle);
        user = findViewById(R.id.welcome);
        unseen = findViewById(R.id.unseen);

        // Initialize card views
        track = findViewById(R.id.track);
        check_in = findViewById(R.id.qr_code_check_in);
        site_map = findViewById(R.id.site_map);

        // Configure Google Sign-In options
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso); // Initialize GoogleSignInClient

        // Get the last signed-in account
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) { // Check if an account is available
            personName = acct.getDisplayName(); // Get user display name
            personEmail = acct.getEmail(); // Get user email
            String profileImageUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : ""; // Get profile image URL

            // Load the user's profile image using Picasso
            if (!profileImageUrl.isEmpty()) {
                Picasso.get().load(profileImageUrl).into(userIcon);
                dbHelper.saveUserDataWithImageUrl(personEmail, personName, profileImageUrl); // Save user data to the database
            } else {
                // Set the initial of the user's name if no image is available
                user_icon_text.setText(personName.substring(0, 1).toUpperCase());
                user_icon_text.setVisibility(View.VISIBLE); // Show initial
                userIcon.setVisibility(View.GONE); // Hide profile image
            }

            // Set welcome message for the user
            user.setText("Hello, " + personName.toString().split(" ")[0].substring(0, 1).toUpperCase() +
                    personName.toString().split(" ")[0].substring(1).toLowerCase() + " !");

            // Show admin sign-in message if the user is an admin
            if (personEmail.equals("guptasdhuruv4@gmail.com")) {
                Toast.makeText(this, "Admin Signed In", Toast.LENGTH_SHORT).show();
            }
        }

        // Set click listener for the "track" card view
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Open the track URL in a web browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://acis.aaisnet.org/acis2024/tracks/"));
                    startActivity(intent);
                } catch (Exception e) {
                    // Show a toast message if unable to load the URL
                    Toast.makeText(Home_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for the "site map" card view
        site_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Page.this, Site_Map_Page.class)); // Navigate to the site map page
            }
        });

        // Retrieve the seen announcement status from the database
        dbHelper.getSeenAnnouncement(personEmail, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    // Check if there is a seen status in the data snapshot
                    if (dataSnapshot.exists()) {
                        String seenStatus = dataSnapshot.getValue(String.class);
                        if (seenStatus != null) {
                            unseen.setVisibility(View.GONE); // Hide unseen button if already seen
                        }
                    } else {
                        unseen.setVisibility(View.VISIBLE); // Show unseen button if not seen
                    }
                } catch (Exception e) {
                    // Print error message if an exception occurs
                    System.out.println("An error occurred while processing SeenAnnouncement status: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Print error message if the database query fails
                System.out.println("Failed to retrieve SeenAnnouncement status: " + databaseError.getMessage());
            }
        });

        // Set click listener for the "check-in" card view
        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Page.this, QR_check_in.class)); // Navigate to the QR check-in page
            }
        });

        // Set click listener for the notification icon
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Page.this, Announcement_Page.class)); // Navigate to the announcement page
            }
        });

        // Set click listener for the unseen announcements button
        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Page.this, Announcement_Page.class)); // Navigate to the announcement page
            }
        });

        // Initialize the session RecyclerView
        sessionRecycler();

        // Initialize the navigation drawer
        navigationDrawer();

        // Change color of specific items
//        Menu menu = navigationView.getMenu();

//        MenuItem socialsItem = menu.findItem(R.id.socials);
//
//        SpannableString s = new SpannableString(socialsItem.getTitle());
//        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.highlighted_item_color)), 0, s.length(), 0);
//        socialsItem.setTitle(s);
    }

    private void sessionRecycler() {
        sessionRecycler.setHasFixedSize(true); // Optimize RecyclerView for fixed-size items
        sessionRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Set layout manager to horizontal

        // Retrieve sessions from the database
        dbHelper.getSessions(new MyDatabaseHelper.SessionsRetrievalCallback() {
            @Override
            public void onEventsRetrieved(List<Session> sessions) {
                if (sessions != null && !sessions.isEmpty()) {
                    ArrayList<HomeSessionHelperClass> announcementLocations = new ArrayList<>();

                    // Sort sessions by date, then by start time, and finally by end time
                    Collections.sort(sessions, new Comparator<Session>() {
                        @Override
                        public int compare(Session session1, Session session2) {
                            // First compare by date
                            int dateComparison = session1.getDate().compareTo(session2.getDate());
                            if (dateComparison != 0) {
                                return dateComparison;
                            }

                            // If dates are equal, compare by start time
                            int startTimeComparison = session1.getStart_time().compareTo(session2.getStart_time());
                            if (startTimeComparison != 0) {
                                return startTimeComparison;
                            }

                            // If start times are equal, compare by end time
                            return session1.getEnd_time().compareTo(session2.getEnd_time());
                        }
                    });

                    // Get current date and time for comparison
                    LocalDate currentDate = LocalDate.now();
                    LocalTime currentTime = LocalTime.now();

                    // Limit the number of sessions to 4
                    int sessionCount = 0;

                    // Iterate through the sorted list of sessions
                    for (Session session : sessions) {
                        if (sessionCount >= 4) break; // Stop adding after 4 sessions

                        LocalDate sessionDate = session.getDate();
                        LocalTime sessionEndTime = session.getEnd_time();

                        // Skip sessions that are in the past
                        if (sessionDate.isBefore(currentDate)) {
                            continue; // Skip if the session date is before today
                        }

                        // If the session is today, check if it has already ended
                        if (sessionDate.isEqual(currentDate) && sessionEndTime.isBefore(currentTime)) {
                            continue; // Skip if the session has already ended today
                        }

                        // Now it's a valid future or ongoing session, so add it
                        String sessionName = session.getName();
                        String track = session.getTrack();
                        String sessionTime = session.getStart_time().toString() + " - " + session.getEnd_time().toString();

                        announcementLocations.add(new HomeSessionHelperClass(
                                sessionName,
                                "Date: " + session.getDate().toString(),
                                "Duration: " + sessionTime,
                                "Track: " + track
                        ));
                        sessionCount++;
                    }

                    // Update RecyclerView with the adapter
                    adapter = new HomeSessionAdapter(announcementLocations, listener);
                    sessionRecycler.setAdapter(adapter);

                } else {
                    Toast.makeText(Home_Page.this, "No sessions found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(Home_Page.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        navigationView.setCheckedItem(R.id.nav_home);  // Highlight the current page in the drawer

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
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(Home_Page.this, Session_Page.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Home_Page.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Home_Page.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Committee")){
            startActivity(new Intent(Home_Page.this, Organising_Committee_Page.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Home_Page.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Home_Page.this, About_Page.class));
        }
        if(item.toString().equals("Sign Out")){
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(Home_Page.this, Google_Sign_In_Page.class));
                }
            });
        }
        return true; // Indicate that the item selection has been handled
    }
}
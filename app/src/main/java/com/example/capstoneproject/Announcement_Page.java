package com.example.capstoneproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Activity for displaying announcements and managing user interactions
public class Announcement_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f; // Scale factor for navigation drawer
    GoogleSignInOptions gso; // Google Sign-In options
    GoogleSignInClient gsc; // Google Sign-In client
    UserProfile userProfile; // LinkedIn sign-in user profile
    String personName, personEmail; // User's name and email
    DrawerLayout drawerLayout; // Navigation drawer layout
    NavigationView navigationView; // Navigation view for drawer items
    LinearLayout contentView; // Main content view
    ImageView notification, menuIcon; // UI elements for notifications and menu icon
    RecyclerView announcementRecycler; // RecyclerView for displaying announcements
    RecyclerView.Adapter adapter; // Adapter for RecyclerView
    FloatingActionButton addAnnouncement; // Button for adding new announcements
    private MyDatabaseHelper dbHelper; // Database helper for managing data
    ArrayList<AnnouncementHelperClass> announcementLocations; // List to hold announcement data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_page); // Set the content view
        dbHelper = new MyDatabaseHelper(); // Initialize database helper

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        // Hooks for UI elements
        drawerLayout = findViewById(R.id.announcement_drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        announcementRecycler = findViewById(R.id.announcement_recycle);
        notification = findViewById(R.id.notification);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        addAnnouncement = findViewById(R.id.add_announcement);

        // Get the last signed-in LinkedIn account
        userProfile = UserProfile.getInstance();

        if (userProfile.getName() != null && userProfile.getEmail() != null){
            personName = userProfile.getName(); // Get user's display name
            personEmail = userProfile.getEmail(); // Get user's email

            // Show admin sign-in message if the user is an admin
            if (personEmail.equals("u3238031@uni.canberra.edu.au")) {
                Toast.makeText(this, "Admin Signed In", Toast.LENGTH_SHORT).show(); // Notify admin sign-in
                addAnnouncement.setVisibility(View.VISIBLE); // Show add announcement button for admin
            }
        }

        else {
            // Configure Google Sign-In options
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso); // Get Google Sign-In client

            // Get the last signed-in Google account
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this); // Get last signed-in account
            if (acct != null) {
                personName = acct.getDisplayName(); // Get user's display name
                personEmail = acct.getEmail(); // Get user's email

                // Check if the signed-in user is an admin
                assert personEmail != null;
                if (personEmail.equals("guptasdhuruv4@gmail.com")) {
                    Toast.makeText(this, "Admin Signed In", Toast.LENGTH_SHORT).show(); // Notify admin sign-in
                    addAnnouncement.setVisibility(View.VISIBLE); // Show add announcement button for admin
                }
            }
        }

        dbHelper.sendSeenAnnouncement(personEmail, true); // Mark announcement as seen
        navigationDrawer(); // Initialize navigation drawer
        AnnouncementRecycler(); // Setup RecyclerView for announcements

        // Set onClickListener for addAnnouncement button
        addAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Announcement_Page.this, Add_Announcement_Page.class)); // Start Add Announcement page
            }
        });
    }

    // Method to setup the RecyclerView for announcements
    private void AnnouncementRecycler() {
        announcementRecycler.setHasFixedSize(true); // Optimize performance
        announcementRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)); // Set layout manager

        announcementLocations = new ArrayList<>(); // Initialize the announcement list

        if (dbHelper != null) {
            // Retrieve announcement data from the database
            dbHelper.getAnnouncements("Announcement", new MyDatabaseHelper.DataRetrievalCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataRetrieved(List<Announcement> announcements) {
                    if (announcements != null) {
                        List<AnnouncementHelperClass> helperList = new ArrayList<>(); // Helper list for announcements
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Date formatter

                        // Populate helper list with announcement data
                        for (Announcement announcement : announcements) {
                            helperList.add(new AnnouncementHelperClass(
                                    announcement.getTitle(),
                                    announcement.getDescription(),
                                    announcement.getDateTime()
                            ));
                        }

                        // Sort the list based on dateTime in descending order
                        helperList.sort((a1, a2) -> {
                            try {
                                LocalDateTime dateTime1 = LocalDateTime.parse(a1.getDateTime(), formatter);
                                LocalDateTime dateTime2 = LocalDateTime.parse(a2.getDateTime(), formatter);
                                return dateTime2.compareTo(dateTime1); // Sort in descending order
                            } catch (Exception e) {
                                e.printStackTrace(); // Handle parsing exceptions
                                return 0; // No change in order if an error occurs
                            }
                        });

                        announcementLocations.clear(); // Clear existing list
                        announcementLocations.addAll(helperList); // Add sorted announcements to the list

                        // Notify adapter of data changes
                        if (adapter == null) {
                            adapter = new AnnouncementAdapter(announcementLocations, personEmail); // Create new adapter
                            announcementRecycler.setAdapter(adapter); // Set adapter to RecyclerView
                        } else {
                            adapter.notifyDataSetChanged(); // Notify existing adapter of data change
                        }
                    } else {
                        System.out.println("No data found for this category."); // Log message if no data found
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace(); // Print stack trace for debugging
                    Toast.makeText(Announcement_Page.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Show error message
                }
            });
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

    // Method to setup navigation drawer
    private void navigationDrawer() {
        navigationView.bringToFront(); // Bring navigation view to front
        navigationView.setNavigationItemSelectedListener(this); // Set item selection listener

        // Set onClickListener for menu icon to toggle drawer visibility
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START); // Close drawer if open
                }
                else {
                    drawerLayout.openDrawer(GravityCompat.START); // Open drawer if closed
                }
            }
        });

        animateNavigationDrawer(); // Animate drawer
    }

    // Method to animate navigation drawer
    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaleOffset = slideOffset * (1 - END_SCALE); // Calculate scale offset
                final float offsetScale = 1 - diffScaleOffset; // Calculate new scale
                contentView.setScaleX(offsetScale); // Apply scale on X-axis
                contentView.setScaleY(offsetScale); // Apply scale on Y-axis

                final float xOffset = drawerView.getWidth() * slideOffset; // Calculate X offset for translation
                final float xOffsetDiff = contentView.getWidth() * diffScaleOffset / 2; // Calculate difference for translation
                final float xTranslation = xOffset - xOffsetDiff; // Calculate final translation
                contentView.setTranslationX(xTranslation); // Apply translation
            }
        });
    }

    // Handle navigation item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Switch case for handling navigation item clicks
        if(item.toString().equals("Home")){
            startActivity(new Intent(Announcement_Page.this, Home_Page.class)); // Go to Home page
        }
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(Announcement_Page.this, Session_Page.class)); // Go to Sessions page
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Announcement_Page.this, QR_check_in.class)); // Go to QR Check-In page
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Announcement_Page.this, Site_Map_Page.class)); // Go to Site Map page
        }
        if(item.toString().equals("Committee")){
            startActivity(new Intent(Announcement_Page.this, Organising_Committee_Page.class)); // Go to Committee page
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Announcement_Page.this, Group_Chat_Page.class)); // Go to Chat page
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Announcement_Page.this, About_Page.class)); // Go to About page
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
                Intent intent = new Intent(Announcement_Page.this, Sign_In_Page.class); // Change to your login activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception ignored){}

            try {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(Announcement_Page.this, Sign_In_Page.class));
                    }
                });
            } catch (Exception ignored){}
        }
        return true; // Indicate item selection handled
    }
}
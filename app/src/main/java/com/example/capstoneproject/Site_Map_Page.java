package com.example.capstoneproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;

public class Site_Map_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f; // Scale factor for drawer animation
    String personName, personEmail; // Variables to store user's name and email
    GoogleSignInOptions gso; // Google sign-in options
    GoogleSignInClient gsc; // Google sign-in client
    UserProfile userProfile; // LinkedIn sign-in user profile
    DrawerLayout drawerLayout; // Layout for the navigation drawer
    NavigationView navigationView; // Navigation view for the drawer
    LinearLayout contentView; // Main content view
    private MyDatabaseHelper dbHelper; // Database helper for Firebase operations
    ImageView map, menuIcon, notification; // Image views for map and icons
    Button unseen; // Button to show unseen announcements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the superclass method
        setContentView(R.layout.activity_site_map_page); // Set the content view to the activity layout
        dbHelper = new MyDatabaseHelper(); // Initialize the database helper

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hooks: Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.content);
        menuIcon = findViewById(R.id.menu_icon);
        notification = findViewById(R.id.notification);
        map = findViewById(R.id.site_map);
        unseen = findViewById(R.id.unseen);

        // Get the currently signed-in account
        userProfile = UserProfile.getInstance();

        if (userProfile.getName() != null && userProfile.getEmail() != null){
            personName = userProfile.getName(); // Store user's display name
            personEmail = userProfile.getEmail(); // Store user's email
        }

        else {
            // Configure Google Sign-In options
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso); // Get the Google Sign-In client

            // Get the currently signed-in account
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                personName = acct.getDisplayName(); // Store user's display name
                personEmail = acct.getEmail(); // Store user's email
            }
        }

        // Check for seen announcements in the database
        dbHelper.getSeenAnnouncement(personEmail, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) { // If data exists
                        String seenStatus = dataSnapshot.getValue(String.class);
                        if (seenStatus != null) { // If seen status is retrieved
                            unseen.setVisibility(View.GONE); // Hide unseen button
                        }
                    } else {
                        unseen.setVisibility(View.VISIBLE); // Show unseen button
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

        // Load the site map image using Picasso
        String imageUrl = "https://www.canberra.edu.au/__data/assets/image/0009/1613709/UCEM0093_UCMapsUpdate2020_Main_201207.png";
        Picasso.get()
                .load(imageUrl) // Load the image from the URL
                .error(R.drawable.baseline_error_24) // Error image if loading fails
                .into(map); // Set the loaded image into the ImageView

        // Set an OnClickListener for the map image
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Open the site map URL in a web browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.canberra.edu.au/maps"));
                    startActivity(intent);
                } catch (Exception e) {
                    // Show a toast message if unable to load the URL
                    Toast.makeText(Site_Map_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnClickListener for the notification icon
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Site_Map_Page.this, Announcement_Page.class)); // Navigate to Announcement Page
            }
        });

        // Set OnClickListener for the unseen button
        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Site_Map_Page.this, Announcement_Page.class)); // Navigate to Announcement Page
            }
        });

        navigationDrawer(); // Initialize the navigation drawer
    }

    // Handle back press to close the drawer if it's open
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer if it's visible
        } else {
            super.onBackPressed(); // Otherwise, call the superclass method
        }
    }

    private void navigationDrawer() {
        // Initialize the navigation drawer
        navigationView.bringToFront(); // Bring the navigation view to the front
        navigationView.setNavigationItemSelectedListener(this); // Set the navigation item selected listener
        navigationView.setCheckedItem(R.id.nav_site_map); // Set the current item in the drawer

        // Set OnClickListener for the menu icon to toggle the drawer
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer if it's visible
                } else {
                    drawerLayout.openDrawer(GravityCompat.START); // Open the drawer if it's not visible
                }
            }
        });

        animateNavigationDrawer(); // Start the drawer animation
    }

    private void animateNavigationDrawer() {
        // Add a drawer listener to animate the drawer
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaleOffset = slideOffset * (1 - END_SCALE); // Calculate scaling
                final float offsetScale = 1 - diffScaleOffset; // Calculate offset scale
                contentView.setScaleX(offsetScale); // Set scale for the content view
                contentView.setScaleY(offsetScale); // Set scale for the content view

                final float xOffset = drawerView.getWidth() * slideOffset; // Calculate x offset
                final float xOffsetDiff = contentView.getWidth() * diffScaleOffset / 2; // Calculate x offset difference
                final float xTranslation = xOffset - xOffsetDiff; // Calculate translation
                contentView.setTranslationX(xTranslation); // Set translation for the content view
            }
        });
    }

    // Handle navigation item selections
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.toString().equals("Home")) {
            startActivity(new Intent(Site_Map_Page.this, Home_Page.class)); // Navigate to Home Page
        }
        if (item.toString().equals("Sessions")) {
            startActivity(new Intent(Site_Map_Page.this, Session_Page.class)); // Navigate to Session Page
        }
        if (item.toString().equals("QR Check-In")) {
            startActivity(new Intent(Site_Map_Page.this, QR_check_in.class)); // Navigate to QR Check-In Page
        }
        if (item.toString().equals("Committee")) {
            startActivity(new Intent(Site_Map_Page.this, Organising_Committee_Page.class)); // Navigate to Organising Committee Page
        }
        if (item.toString().equals("Chat")) {
            startActivity(new Intent(Site_Map_Page.this, Group_Chat_Page.class)); // Navigate to Group Chat Page
        }
        if (item.toString().equals("About")) {
            startActivity(new Intent(Site_Map_Page.this, About_Page.class)); // Navigate to About Page
        }
        if (item.toString().equals("Sign Out")) {
            try {
                // Clear stored access token
                SharedPreferences sharedPreferences = getSharedPreferences("YourAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(userProfile.getToken());
                editor.apply();

                // Clear any other user data
                UserProfile.getInstance().clearUserProfile(); // Implement this method to clear user profile data

                // Redirect user to the login screen or homepage
                Intent intent = new Intent(Site_Map_Page.this, Sign_In_Page.class); // Change to your login activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception ignored){}

            try {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(Site_Map_Page.this, Sign_In_Page.class));
                    }
                });
            } catch (Exception ignored){}
        }
        return true; // Return true to indicate the event was handled
    }
}
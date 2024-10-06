package com.example.capstoneproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.BuildConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Group_Chat_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static final float END_SCALE = 0.7f; // Scale factor for drawer animation
    private EditText message; // EditText for entering messages
    private ImageButton send; // Button to send messages
    private RecyclerView firstRecycler; // RecyclerView to display messages
    private ChatPageAdapter adapter; // Adapter for the RecyclerView
    private GoogleSignInOptions gso; // Google Sign-In options
    private GoogleSignInClient gsc; // Google Sign-In client
    UserProfile userProfile; // LinkedIn sign-in user profile
    private String personName, personEmail; // Variables to store user details
    DrawerLayout drawerLayout; // Layout for the navigation drawer
    NavigationView navigationView; // Navigation view for menu items
    LinearLayout contentView; // Content view layout
    ImageView menuIcon, notification; // Menu icon and notification button
    Button unseen; // Button to indicate unseen announcements
    private MyDatabaseHelper dbHelper; // Database helper for Firebase operations
    private ArrayList<ChatPageHelperClass> groupedMessages; // List to hold chat messages
    private String lastInsertedDate = ""; // Track the last date a header was inserted

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_page); // Set the content view to the group chat layout

        // Initialize Firebase database helper
        dbHelper = new MyDatabaseHelper();

        // Hide the action bar for a cleaner interface
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hook up UI components to their respective IDs
        firstRecycler = findViewById(R.id.first_recycle);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        unseen = findViewById(R.id.unseen);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.content);
        menuIcon = findViewById(R.id.menu_icon);
        notification = findViewById(R.id.notification);

        // Get the last signed-in account
        userProfile = UserProfile.getInstance();

        if (userProfile.getName() != null && userProfile.getEmail() != null){
            personName = userProfile.getName();  // Retrieve the display name
            personEmail = userProfile.getEmail(); // Retrieve the email address
        }

        else {
            // Configure Google Sign-In options to request the user's email
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail() // Request email address
                    .build(); // Build the options
            gsc = GoogleSignIn.getClient(this, gso); // Get the GoogleSignInClient with the specified options

            // Get the last signed-in account
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                personName = acct.getDisplayName(); // Retrieve the display name
                personEmail = acct.getEmail(); // Retrieve the email address
            }
        }

        // Set up the RecyclerView for displaying chat messages
        firstRecycler.setHasFixedSize(true); // Optimize RecyclerView size
        firstRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)); // Set layout manager
        groupedMessages = new ArrayList<>(); // Initialize message list
        adapter = new ChatPageAdapter(groupedMessages); // Create adapter with message list
        firstRecycler.setAdapter(adapter); // Set the adapter to the RecyclerView

        // Check if the user has seen announcements
        dbHelper.getSeenAnnouncement(personEmail, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) { // Check if the data exists
                        String seenStatus = dataSnapshot.getValue(String.class);
                        if (seenStatus != null) {
                            unseen.setVisibility(View.GONE); // Hide unseen button if announcements have been seen
                        }
                    } else {
                        unseen.setVisibility(View.VISIBLE); // Show unseen button if no data exists
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

        // Start listening for incoming messages
        listenForMessages();

        // Set onClickListener for notification icon to navigate to announcement page
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Group_Chat_Page.this, Announcement_Page.class)); // Start announcement activity
            }
        });

        // Set onClickListener for unseen button to navigate to announcement page
        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Group_Chat_Page.this, Announcement_Page.class)); // Start announcement activity
            }
        });

        // Set onClickListener for send button to handle message sending
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = message.getText().toString().trim(); // Get message text
                if (!messageText.isEmpty()) { // Proceed if message is not empty
                    LocalDate localDate = LocalDate.now(); // Get current date
                    LocalTime localTime = LocalTime.now(); // Get current time
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // Format time
                    String formattedDate = localDate.toString(); // Format date
                    String formattedTime = localTime.format(timeFormatter); // Format time
                    String formattedDateTime = formattedDate + " " + formattedTime; // Combine date and time

                    // Insert a new header if the date has changed
                    if (!formattedDate.equals(lastInsertedDate)) {
                        dbHelper.addHeader(formattedDate); // Insert header for new date
                        lastInsertedDate = formattedDate; // Update last inserted date
                    }

                    // Send the message to the database
                    dbHelper.addMessage(personName, personEmail, messageText, formattedDateTime);

                    // Clear the input field after sending
                    message.setText("");
                }
            }
        });

        // Enable strict mode for debugging during development
        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll() // Detect all thread-related issues
                    .penaltyLog() // Log penalties for violations
                    .build();
            StrictMode.setThreadPolicy(policy); // Set thread policy

            StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                    .detectAll() // Detect all VM-related issues
                    .penaltyLog() // Log penalties for violations
                    .build();
            StrictMode.setVmPolicy(vmPolicy); // Set VM policy
        }

        navigationDrawer();
    }

    private void listenForMessages() {
        dbHelper.getConversations(new MyDatabaseHelper.FirstHelperClassRetrievalCallback() {
            @Override
            public void onDataRetrieved(List<ChatPageHelperClass> conversations) {
                // Ensure data is properly formatted
                updateRecyclerView(conversations);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateRecyclerView(List<ChatPageHelperClass> newConversations) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MessageDiffCallback(groupedMessages, newConversations));
        // Update the adapter with new messages
        groupedMessages.clear(); // Clear the current message list
        groupedMessages.addAll(newConversations); // Add new messages
        diffResult.dispatchUpdatesTo(adapter);
        Log.d("Group_Chat_Page", "RecyclerView updated with " + newConversations.size() + " items");
        // Scroll to the bottom
        firstRecycler.scrollToPosition(newConversations.size() - 1);
    }

    @Override
    public void onBackPressed() {
        // Override back button press behavior to handle drawer state
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer if open
        } else {
            super.onBackPressed(); // Otherwise, perform default back press behavior
        }
    }

    // Method to initialize the navigation drawer
    private void navigationDrawer() {

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_chat);

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
            startActivity(new Intent(Group_Chat_Page.this, Home_Page.class));
        }
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(Group_Chat_Page.this, Session_Page.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Group_Chat_Page.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Group_Chat_Page.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Committee")){
            startActivity(new Intent(Group_Chat_Page.this, Organising_Committee_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Group_Chat_Page.this, About_Page.class));
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
                Intent intent = new Intent(Group_Chat_Page.this, Sign_In_Page.class); // Change to your login activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception ignored){}

            try {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(Group_Chat_Page.this, Sign_In_Page.class));
                    }
                });
            } catch (Exception ignored){}
        }
        return true;
    }
}

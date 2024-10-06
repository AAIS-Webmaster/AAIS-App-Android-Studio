package com.example.capstoneproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
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
import com.google.zxing.Result;

public class QR_check_in extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Constants
    private static final int CAMERA_PERMISSION_CODE = 100; // Code for camera permission request
    static final float END_SCALE = 0.7f; // Scale factor for navigation drawer animation

    // Instance variables
    private CodeScanner mCodeScanner; // CodeScanner object for scanning QR codes
    CodeScannerView scannerView; // View for the code scanner
    TextView scanner_text; // TextView to display scanned result
    String personName, personEmail; // Variables to hold user's name and email
    DrawerLayout drawerLayout; // Layout for the navigation drawer
    NavigationView navigationView; // Navigation view for menu items
    LinearLayout contentView; // Main content layout
    ImageView menuIcon, notification; // Menu and notification icons
    GoogleSignInOptions gso; // Google Sign-In options
    GoogleSignInClient gsc; // Google Sign-In client
    UserProfile userProfile; // LinkedIn sign-in user profile
    Button open, unseen; // Buttons for opening URL and displaying unseen announcements
    private MyDatabaseHelper dbHelper; // Database helper for Firebase interactions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_check_in);
        dbHelper = new MyDatabaseHelper(); // Initialize database helper

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hooks to link layout components
        scannerView = findViewById(R.id.scanner_view);
        scanner_text = findViewById(R.id.scanner_text);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.content);
        menuIcon = findViewById(R.id.menu_icon);
        notification = findViewById(R.id.notification);
        open = findViewById(R.id.open_url);
        unseen = findViewById(R.id.unseen);

        // Get the last signed-in user's account information
        userProfile = UserProfile.getInstance();

        if (userProfile.getName() != null && userProfile.getEmail() != null){
            personName = userProfile.getName(); // Get user's display name
            personEmail = userProfile.getEmail(); // Get user's email
        }

        else {
            // Configure Google Sign-In options
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso);

            // Get the last signed-in user's account information
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                personName = acct.getDisplayName(); // Get user's display name
                personEmail = acct.getEmail(); // Get user's email
            }
        }

        // Retrieve seen announcement status from the database
        dbHelper.getSeenAnnouncement(personEmail, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        String seenStatus = dataSnapshot.getValue(String.class);
                        if (seenStatus != null) {
                            unseen.setVisibility(View.GONE); // Hide unseen button if announcement has been seen
                        }
                    } else {
                        unseen.setVisibility(View.VISIBLE); // Show unseen button if no data is found
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

        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            // If permission is granted, initialize the scanner
            initializeScanner();
        }

        // Set click listener for notification button
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QR_check_in.this, Announcement_Page.class)); // Open announcement page
            }
        });

        // Set click listener for unseen button
        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QR_check_in.this, Announcement_Page.class)); // Open announcement page
            }
        });

        // Set click listener for open URL button
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri open_url = Uri.parse(scanner_text.getText().toString()); // Parse URL from scanned text
                    Intent intent = new Intent(Intent.ACTION_VIEW, open_url); // Create an intent to view the URL
                    startActivity(intent); // Start the intent
                } catch (Exception e) {
                    Toast.makeText(QR_check_in.this, "Unable to load URL", Toast.LENGTH_SHORT).show(); // Show error message if URL fails to load
                }
            }
        });

        // Initialize the navigation drawer
        navigationDrawer();
    }

    // Handle back button press to close the drawer if it's open
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer
        } else {
            super.onBackPressed(); // Otherwise, handle default back action
        }
    }

    // Initialize the QR code scanner
    private void initializeScanner() {
        mCodeScanner = new CodeScanner(this, scannerView); // Create CodeScanner instance
        mCodeScanner.setDecodeCallback(new DecodeCallback() { // Set decode callback to handle scanned results
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() { // Ensure UI updates happen on the main thread
                    @Override
                    public void run() {
                        scanner_text.setText(result.getText()); // Set the scanned text to the TextView
                        mCodeScanner.startPreview(); // Restart the scanner preview
                    }
                });
            }
        });
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission was granted, initialize the scanner
                initializeScanner();
            } else {
                // If permission was denied, show a message
                Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Set up the navigation drawer
    private void navigationDrawer() {
        navigationView.bringToFront(); // Bring the navigation view to the front
        navigationView.setNavigationItemSelectedListener(this); // Set item selection listener
        navigationView.setCheckedItem(R.id.nav_qr_sign_in); // Check the current menu item

        // Set click listener for the menu icon
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

        animateNavigationDrawer(); // Start the navigation drawer animation
    }

    // Animate the navigation drawer
    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaleOffset = slideOffset * (1 - END_SCALE); // Calculate the difference in scale
                final float offsetScale = 1 - diffScaleOffset; // Calculate the new scale
                contentView.setScaleX(offsetScale); // Set the scale for the content view
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset; // Calculate X offset for translation
                final float xOffsetDiff = contentView.getWidth() * diffScaleOffset / 2; // Calculate the difference in offset
                final float xTranslation = xOffset - xOffsetDiff; // Calculate final X translation
                contentView.setTranslationX(xTranslation); // Apply translation to the content view
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCodeScanner != null) {
            mCodeScanner.startPreview(); // Start the scanner preview when resuming the activity
        }
    }

    @Override
    protected void onPause() {
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources(); // Release scanner resources when pausing the activity
        }
        super.onPause();
    }

    // Handle navigation item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.toString().equals("Home")){
            startActivity(new Intent(QR_check_in.this, Home_Page.class));
        }
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(QR_check_in.this, Session_Page.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(QR_check_in.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Committee")){
            startActivity(new Intent(QR_check_in.this, Organising_Committee_Page.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(QR_check_in.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(QR_check_in.this, About_Page.class));
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
                Intent intent = new Intent(QR_check_in.this, Sign_In_Page.class); // Change to your login activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception ignored){}

            try {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(QR_check_in.this, Sign_In_Page.class));
                    }
                });
            } catch (Exception ignored){}
        }
        return true;
    }
}

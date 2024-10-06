package com.example.capstoneproject;

import static com.example.capstoneproject.Home_Page.END_SCALE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Organising_Committee_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Variables to hold user information
    String personName, personEmail;
    ImageView[] imageViews; // Array of ImageViews to display committee member images
    Button unseen; // Button to handle unseen announcements
    Map<ImageView, String> imageUrlMap; // Map to hold ImageView and corresponding image URLs
    Map<ImageView, String> linkMap; // Map to hold ImageView and corresponding links
    DrawerLayout drawerLayout; // Navigation drawer layout
    NavigationView navigationView; // Navigation view for menu items
    ImageView menuIcon, notification; // Icons for menu and notifications
    LinearLayout contentView; // Content view of the layout
    GoogleSignInOptions gso; // Google Sign-In options
    GoogleSignInClient gsc; // Google Sign-In client
    UserProfile userProfile; // LinkedIn sign-in user profile
    private MyDatabaseHelper dbHelper; // Database helper for managing announcements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organising_committee);
        dbHelper = new MyDatabaseHelper(); // Initialize database helper

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get the signed-in account details
        userProfile = UserProfile.getInstance();

        if (userProfile.getName() != null && userProfile.getEmail() != null){
            personName = userProfile.getName(); // Retrieve display name
            personEmail = userProfile.getEmail(); // Retrieve email
        }

        else {
            // Configure Google Sign-In options
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso);

            // Get the signed-in account details
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                personName = acct.getDisplayName(); // Retrieve display name
                personEmail = acct.getEmail(); // Retrieve email
            }
        }

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        notification = findViewById(R.id.notification);
        contentView = findViewById(R.id.content);
        unseen = findViewById(R.id.unseen);

        // Retrieve announcement seen status from the database
        dbHelper.getSeenAnnouncement(personEmail, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        String seenStatus = dataSnapshot.getValue(String.class);
                        if (seenStatus != null) {
                            unseen.setVisibility(View.GONE); // Hide unseen button if status exists
                        }
                    } else {
                        unseen.setVisibility(View.VISIBLE); // Show unseen button if no status
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

        // Initialize ImageViews for committee member images
        imageViews = new ImageView[]{
                findViewById(R.id.image1),
                findViewById(R.id.image2),
                findViewById(R.id.image3),
                findViewById(R.id.image4),
                findViewById(R.id.image5),
                findViewById(R.id.image6),
                findViewById(R.id.image7),
                findViewById(R.id.image8),
                findViewById(R.id.image9),
                findViewById(R.id.image10),
                findViewById(R.id.image11),
                findViewById(R.id.image12),
                findViewById(R.id.image13),
                findViewById(R.id.image14),
                findViewById(R.id.image15),
                findViewById(R.id.image16),
                findViewById(R.id.image17),
                findViewById(R.id.image18),
                findViewById(R.id.image19),
                findViewById(R.id.image20),
                findViewById(R.id.image21),
                findViewById(R.id.image22),
                findViewById(R.id.image23),
                findViewById(R.id.image24),
                findViewById(R.id.image25),
                findViewById(R.id.image26),
                findViewById(R.id.image27)
        };

        // Initialize maps to store image URLs and links
        imageUrlMap = new HashMap<>();
        linkMap = new HashMap<>();

        // Fetch images from the committee page
        new FetchImagesTask().execute("https://acis.aaisnet.org/acis2024/committee/");
        navigationDrawer(); // Setup navigation drawer

        // Set click listeners for notification and unseen buttons
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Organising_Committee_Page.this, Announcement_Page.class)); // Navigate to Announcement Page
            }
        });

        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Organising_Committee_Page.this, Announcement_Page.class)); // Navigate to Announcement Page
            }
        });
    }

    // AsyncTask to fetch images and links from the provided URL
    private class FetchImagesTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            try {
                // Connect to the website and retrieve the document
                Document doc = Jsoup.connect(url).get();

                // Select the specific <div> containing committee member information
                Element targetDiv = doc.select("div.entry-content").first(); // Adjust the selector to match your target <div>

                if (targetDiv != null) {
                    // Extract <img> src attributes within the target <div>
                    Elements imgElements = targetDiv.select("img[src]");
                    int i = 0; // Counter for ImageViews
                    for (Element img : imgElements) {
                        if (i >= imageViews.length) break; // Prevent overflow if more images than ImageViews

                        String imgUrl = img.attr("src"); // Get image URL
                        System.out.println("Image src: " + imgUrl);
                        // Store imgUrl and corresponding ImageView
                        imageUrlMap.put(imageViews[i], imgUrl);

                        i++; // Increment counter
                    }

                    // Extract <a> href attributes within the target <div>
                    Elements linkElements = targetDiv.select("a[href]");
                    i = 0; // Reset counter for links
                    for (Element link : linkElements) {
                        if (i >= imageViews.length) break; // Prevent overflow if more links than ImageViews

                        String linkHref = link.attr("href"); // Get link URL
                        System.out.println("Link href: " + linkHref);
                        // Store linkHref and corresponding ImageView
                        linkMap.put(imageViews[i], linkHref);

                        i++; // Increment counter
                    }
                } else {
                    System.out.println("Target div not found."); // Log if target div is not found
                }

            } catch (IOException e) {
                e.printStackTrace(); // Handle any IO exceptions
            }

            return null; // Return null for Void type
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Set images in ImageViews and attach click listeners
            for (Map.Entry<ImageView, String> entry : imageUrlMap.entrySet()) {
                ImageView imageView = entry.getKey();
                String imgUrl = entry.getValue();
                Picasso.get().load(imgUrl).into(imageView); // Load image into ImageView

                // Set OnClickListener to open the corresponding link
                String linkHref = linkMap.get(imageView);
                if (linkHref != null) {
                    imageView.setOnClickListener(v -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkHref)); // Create intent to open link
                            startActivity(intent); // Start activity
                        } catch (Exception e) {
                            Toast.makeText(Organising_Committee_Page.this, "Unable to Open the URL", Toast.LENGTH_SHORT).show(); // Show error message if URL can't be opened
                        }
                    });
                }
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
        navigationView.setCheckedItem(R.id.nav_org);  // Highlight the current page in the drawer

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
            startActivity(new Intent(Organising_Committee_Page.this, Home_Page.class));
        }
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(Organising_Committee_Page.this, Session_Page.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Organising_Committee_Page.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Organising_Committee_Page.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Organising_Committee_Page.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Organising_Committee_Page.this, About_Page.class));
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
                Intent intent = new Intent(Organising_Committee_Page.this, Sign_In_Page.class); // Change to your login activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception ignored){}

            try {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(Organising_Committee_Page.this, Sign_In_Page.class));
                    }
                });
            } catch (Exception ignored){}
        }
        return true;
    }
}

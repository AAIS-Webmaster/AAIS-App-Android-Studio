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

    TextView user, user_icon_text;
    Button unseen;
    static final float END_SCALE = 0.7f;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String personName, personEmail;
    DrawerLayout drawerLayout;
    CardView track, check_in, site_map;
    NavigationView navigationView;
    ImageView menuIcon, notification, userIcon;
    LinearLayout contentView;
    RecyclerView sessionRecycler;
    RecyclerView.Adapter adapter;
    private MyDatabaseHelper dbHelper;
    private SessionAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = new MyDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Menu Hooks
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

        track = findViewById(R.id.track);
        check_in = findViewById(R.id.qr_code_check_in);
        site_map = findViewById(R.id.site_map);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct != null){
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            String profileImageUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "";
            if (!profileImageUrl.isEmpty()) {
                Picasso.get().load(profileImageUrl).into(userIcon); // Use Picasso to load the image
                dbHelper.saveUserDataWithImageUrl(personEmail, personName, profileImageUrl);
            }
            else {
                user_icon_text.setText(personName.substring(0, 1).toUpperCase());
                user_icon_text.setVisibility(View.VISIBLE);
                userIcon.setVisibility(View.GONE);
            }
            user.setText("Hello, " + personName.toString().split(" ")[0].substring(0, 1).toUpperCase() +
                    personName.toString().split(" ")[0].substring(1).toLowerCase() +
                    " !");

            if (personEmail.equals("guptasdhuruv4@gmail.com")) {
                Toast.makeText(this, "Admin Signed In", Toast.LENGTH_SHORT).show();
            }
        }
        
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://acis.aaisnet.org/acis2024/tracks/"));
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(Home_Page.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        site_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Page.this, Site_Map_Page.class));
            }
        });

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

        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Page.this, QR_check_in.class));
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Page.this, Announcement_Page.class));
            }
        });

        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Page.this, Announcement_Page.class));
            }
        });

        sessionRecycler();

        // Change color of specific items
//        Menu menu = navigationView.getMenu();

//        MenuItem socialsItem = menu.findItem(R.id.socials);
//
//        SpannableString s = new SpannableString(socialsItem.getTitle());
//        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.highlighted_item_color)), 0, s.length(), 0);
//        socialsItem.setTitle(s);
        navigationDrawer();
    }

    private void sessionRecycler() {
        sessionRecycler.setHasFixedSize(true);
        sessionRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
        navigationView.setCheckedItem(R.id.nav_home);

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
        return true;
    }
}
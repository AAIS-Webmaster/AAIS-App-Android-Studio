package com.example.capstoneproject;

import android.content.Intent;
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

public class Announcement_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String personName, personEmail;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    ImageView notification, menuIcon, delete_announcement;
    RecyclerView announcementRecycler;
    RecyclerView.Adapter adapter;
    FloatingActionButton addAnnouncement;
    private MyDatabaseHelper dbHelper;
    ArrayList<AnnouncementHelperClass> announcementLocations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_page);
        dbHelper = new MyDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Menu Hooks
        drawerLayout = findViewById(R.id.announcement_drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        announcementRecycler = findViewById(R.id.announcement_recycle);
        notification = findViewById(R.id.notification);
        menuIcon = findViewById(R.id.menu_icon);
//        delete_announcement = findViewById(R.id.delete_announcement);
        contentView = findViewById(R.id.content);
        addAnnouncement = findViewById(R.id.add_announcement);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();

            if(personEmail.equals("guptasdhuruv4@gmail.com")){
                Toast.makeText(this, "Admin Signed In", Toast.LENGTH_SHORT).show();
//                userIcon.setImageResource(R.drawable.admin_icon);
                addAnnouncement.setVisibility(View.VISIBLE);
            }
        }

        dbHelper.sendSeenAnnouncement(personEmail, true);
        navigationDrawer();
        AnnouncementRecycler();

        addAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Announcement_Page.this, Pop_up.class));
            }
        });
    }

    private void AnnouncementRecycler() {
        announcementRecycler.setHasFixedSize(true);
        announcementRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        announcementLocations = new ArrayList<>();

        if (dbHelper != null) {
            dbHelper.getData("Announcement", new MyDatabaseHelper.DataRetrievalCallback() {
                @Override
                public void onDataRetrieved(List<Announcement> announcements) {
                    if (announcements != null) {
                        List<AnnouncementHelperClass> helperList = new ArrayList<>();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
                                return dateTime2.compareTo(dateTime1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return 0;
                            }
                        });

                        announcementLocations.clear();
                        announcementLocations.addAll(helperList);

                        // Notify adapter of data changes
                        if (adapter == null) {
                            adapter = new AnnouncementAdapter(announcementLocations, personEmail);
                            announcementRecycler.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        System.out.println("No data found for this category.");
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Announcement_Page.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
            startActivity(new Intent(Announcement_Page.this, Home.class));
        }
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(Announcement_Page.this, HomePage.class));
        }
        if(item.toString().equals("QR Sign-In")){
            startActivity(new Intent(Announcement_Page.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Announcement_Page.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Organising Committee")){
            startActivity(new Intent(Announcement_Page.this, Organising_Committee.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Announcement_Page.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Announcement_Page.this, About_Page.class));
        }
        if(item.toString().equals("Sign Out")){
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(Announcement_Page.this, MainActivity.class));
                }
            });
        }
        return true;
    }
}
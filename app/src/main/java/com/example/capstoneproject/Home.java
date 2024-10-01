package com.example.capstoneproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView user, user_icon_text;
    Button unseen;
    static final float END_SCALE = 0.7f;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String personName, personEmail, extractedText1, extractedText2, extractedText3, extractedText4;
    // Drawer Menu
    DrawerLayout drawerLayout;
    CardView track, check_in, site_map;
    NavigationView navigationView;
    ImageView menuIcon, notification, userIcon;
    LinearLayout contentView;
    RecyclerView generalRecycler, announcementRecycler;
    RecyclerView.Adapter adapter, adapter2, adapter3;
    private Bitmap imageBitmap;
    private MyDatabaseHelper dbHelper;
    private GeneralAdapter.RecyclerViewClickListener listener;
    ArrayList<String> pos;

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
//        generalRecycler = findViewById(R.id.general_recycle);
        announcementRecycler = findViewById(R.id.announcement_recycle);
//        firstRecycler = findViewById(R.id.first_recycle);
        user = findViewById(R.id.welcome);
        unseen = findViewById(R.id.unseen);

        track = findViewById(R.id.track);
        check_in = findViewById(R.id.qr_code_check_in);
        site_map = findViewById(R.id.site_map);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
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

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://myaccount.google.com/personal-info?gar=WzJd&hl=en_GB&utm_source=OGB&utm_medium=act"));
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(Home.this, "Unable to open the Google Account", Toast.LENGTH_SHORT).show();
                }
            }
        });

        user_icon_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://myaccount.google.com/personal-info?gar=WzJd&hl=en_GB&utm_source=OGB&utm_medium=act"));
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(Home.this, "Unable to open the Google Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://acis.aaisnet.org/acis2024/tracks/"));
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(Home.this, "Unable to load Paper URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        site_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Site_Map_Page.class));
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
                startActivity(new Intent(Home.this, QR_check_in.class));
            }
        });

//        announcementRecycler.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setOnClickListener();
//            }
//        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Announcement_Page.class));
            }
        });

//        generalRecycler();
//        firstRecycler();
        announcementRecycler();

        // Change color of specific items
//        Menu menu = navigationView.getMenu();

//        MenuItem socialsItem = menu.findItem(R.id.socials);
//
//        SpannableString s = new SpannableString(socialsItem.getTitle());
//        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.highlighted_item_color)), 0, s.length(), 0);
//        socialsItem.setTitle(s);
        navigationDrawer();

    }

    private void announcementRecycler() {
//        setOnClickListener();
        announcementRecycler.setHasFixedSize(true);
        announcementRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


//        pos = new ArrayList<>();

        dbHelper.getEvents(new MyDatabaseHelper.EventsRetrievalCallback() {
            @Override
            public void onEventsRetrieved(List<Event> events) {
                if (events != null && !events.isEmpty()) {
                    ArrayList<HomeAnnouncementHelperClass> announcementLocations = new ArrayList<>();
                    // Sort events by date, then by start time, and finally by end time
                    Collections.sort(events, new Comparator<Event>() {
                        @Override
                        public int compare(Event event1, Event event2) {
                            // First compare by date
                            int dateComparison = event1.getDate().compareTo(event2.getDate());
                            if (dateComparison != 0) {
                                return dateComparison;
                            }

                            // If dates are equal, compare by start time
                            int startTimeComparison = event1.getStart_time().compareTo(event2.getStart_time());
                            if (startTimeComparison != 0) {
                                return startTimeComparison;
                            }

                            // If start times are equal, compare by end time
                            return event1.getEnd_time().compareTo(event2.getEnd_time());
                        }
                    });

                    // Get current date and time for comparison
                    LocalDate currentDate = LocalDate.now();
                    LocalTime currentTime = LocalTime.now();

                    // Limit the number of events to 4
                    int eventCount = 0;

                    // Iterate through the sorted list of events
                    for (Event event : events) {
                        if (eventCount >= 4) break; // Stop adding after 4 events

                        LocalDate eventDate = event.getDate();
                        LocalTime eventEndTime = event.getEnd_time();

                        // Skip events that are in the past
                        if (eventDate.isBefore(currentDate)) {
                            continue; // Skip if the event date is before today
                        }

                        // If the event is today, check if it has already ended
                        if (eventDate.isEqual(currentDate) && eventEndTime.isBefore(currentTime)) {
                            continue; // Skip if the event has already ended today
                        }

                        // Now it's a valid future or ongoing event, so add it
                        String eventName = event.getName();
                        String eventTime = event.getStart_time().toString() + " - " + event.getEnd_time().toString();

                        announcementLocations.add(new HomeAnnouncementHelperClass(
                                eventName,
                                "Date: " + event.getDate().toString(),
                                "Duration: " + eventTime
                        ));

                        eventCount++;
                    }

                    // Update RecyclerView with the adapter
                    adapter3 = new HomeAnnouncementAdapter(announcementLocations, listener);
                    announcementRecycler.setAdapter(adapter3);

                } else {
                    Toast.makeText(Home.this, "No events found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(Home.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void setOnClickListener() {
//        listener = new GeneralAdapter.RecyclerViewClickListener() {
//            @Override
//            public void onClick(View v, int position) {
////                listener.onClick(v, position);
//
//                Intent intent = new Intent(Home.this, Event_Page.class);
//                intent.putExtra("event_name", pos.get(position));
////                intent.putExtra("time", time);
//                startActivity(intent);
//            }
//        };
//    }

    private void generalRecycler() {
        generalRecycler.setHasFixedSize(true);
        generalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<GeneralHelperClass> generalLocations = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("Scrapper", "Connecting to website...");
                    Document doc = Jsoup.connect("https://acis.aaisnet.org/acis2024/committee/").timeout(6000).get();

                    // Extracting the desired text from the HTML
                    Element paragraph1 = doc.select("figcaption:contains(Shirley Gregor)").first();
                    Element paragraph2 = doc.select("figcaption:contains(Craig McDonald)").first();
                    Element paragraph3 = doc.select("figcaption:contains(Ahmed Imran)").first();
                    Element paragraph4 = doc.select("figcaption:contains(John James)").first();

                    extractedText1 = paragraph1.text();
                    extractedText2 = paragraph2.text();
                    extractedText3 = paragraph3.text();
                    extractedText4 = paragraph4.text();

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Define the target
                            Target target1 = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    imageBitmap = bitmap;
//                                    generalLocations.add(new GeneralHelperClass(imageBitmap, extractedText1));
//                                    adapter = new GeneralAdapter(generalLocations);
//                                    generalRecycler.setAdapter(adapter);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    // Handle failure
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    // Handle placeholder
                                }
                            };

                            Target target2 = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    imageBitmap = bitmap;
//                                    generalLocations.add(new GeneralHelperClass(imageBitmap, extractedText2));
//                                    adapter = new GeneralAdapter(generalLocations);
//                                    generalRecycler.setAdapter(adapter);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    // Handle failure
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    // Handle placeholder
                                }
                            };

                            Target target3 = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    imageBitmap = bitmap;
//                                    generalLocations.add(new GeneralHelperClass(imageBitmap, extractedText3));
//                                    adapter = new GeneralAdapter(generalLocations);
//                                    generalRecycler.setAdapter(adapter);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    // Handle failure
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    // Handle placeholder
                                }
                            };

                            Target target4 = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    imageBitmap = bitmap;
//                                    generalLocations.add(new GeneralHelperClass(imageBitmap, extractedText4));
//                                    adapter = new GeneralAdapter(generalLocations);
//                                    generalRecycler.setAdapter(adapter);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    // Handle failure
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    // Handle placeholder
                                }
                            };

                            // Load the image using Picasso into the target
                            Picasso.get().load("https://acis.aaisnet.org/acis2024/wp-content/uploads/2024/03/shirley-gregor.png").into(target1);
                            Picasso.get().load("https://acis.aaisnet.org/acis2024/wp-content/uploads/2024/03/craig-mcdonald.png").into(target2);
                            Picasso.get().load("https://acis.aaisnet.org/acis2024/wp-content/uploads/2024/03/ahmed-imran.png").into(target3);
                            Picasso.get().load("https://acis.aaisnet.org/acis2024/wp-content/uploads/2024/06/john-james.jpg").into(target4);



//
////                            Picasso.get().load("https://acis.aaisnet.org/acis2024/wp-content/uploads/2024/07/detmar-w-straub.png").into(image);
//                            generalLocations.add(new GeneralHelperClass(R.drawable.google_logo, extractedText));
                        }
                    });
                } catch (IOException e) {
                    Log.e("Scrapper", "Error fetching data", e);
                }
            }
        }).start();


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
            startActivity(new Intent(Home.this, HomePage.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Home.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Home.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Organising Committee")){
            startActivity(new Intent(Home.this, Organising_Committee.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Home.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Home.this, About_Page.class));
        }
        if(item.toString().equals("Sign Out")){
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(Home.this, MainActivity.class));
                }
            });
        }
        return true;
    }
}
package com.example.capstoneproject;

import static com.example.capstoneproject.CalendarUtils.daysInWeekArray;
import static com.example.capstoneproject.CalendarUtils.monthYearFromDate;

import android.content.Intent;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CalendarAdapter.OnItemListener {

    TextView user;
    static final float END_SCALE = 0.7f;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String personName, personEmail, event_name, time;
    // Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon, notification;
    Button unseen;
    LinearLayout contentView, error;
    RecyclerView generalRecycler;
    RecyclerView.Adapter adapter;
    private MyDatabaseHelper dbHelper;
    private GeneralAdapter.RecyclerViewClickListener listener;
    ArrayList<String> pos;
    FloatingActionButton AddEvent;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    Boolean event_present = false;
    Boolean isArrowUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        dbHelper = new MyDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        notification = findViewById(R.id.notification);
        contentView = findViewById(R.id.content);
        error = findViewById(R.id.error);
        generalRecycler = findViewById(R.id.general_recycle);

        AddEvent = findViewById(R.id.addEvent);
        unseen = findViewById(R.id.unseen);

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();

            if (personEmail.equals("guptasdhuruv4@gmail.com")){
                AddEvent.setVisibility(View.VISIBLE);
            }
        }

//        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_arrow);

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

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, Announcement_Page.class));
            }
        });

//        OptionMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start the rotation animation
//                OptionMenu.startAnimation(rotateAnimation);
//
//                // Toggle the arrow state
//                if (isArrowUp) {
//                    OptionMenu.setImageResource(R.drawable.menu_downward); // Set the downward arrow drawable
//                    AddEvent.setVisibility(View.VISIBLE);
//                    DeleteEvent.setVisibility(View.VISIBLE);
//                } else {
//                    OptionMenu.setImageResource(R.drawable.floating_menu); // Set the upward arrow drawable
//                    AddEvent.setVisibility(View.GONE);
//                    DeleteEvent.setVisibility(View.GONE);
//                }
//                isArrowUp = !isArrowUp;
//            }
//        });

        AddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, Pop_up_add_event.class));
            }
        });

//        generalRecycler.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setOnClickListener();
//            }
//        });
//        generalRecycler();

        // Change color of specific items
//        Menu menu = navigationView.getMenu();

//        MenuItem socialsItem = menu.findItem(R.id.socials);
//
//        SpannableString s = new SpannableString(socialsItem.getTitle());
//        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.highlighted_item_color)), 0, s.length(), 0);
//        socialsItem.setTitle(s);
        navigationDrawer();
        setWeekView();
    }

    private void generalRecycler() {
//        setOnClickListener();
        generalRecycler.setHasFixedSize(true);
        generalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayList<GeneralHelperClass> generalLocations = new ArrayList<>();

        pos = new ArrayList<>();
//        ArrayList<Event> filteredEvents = new ArrayList<>();

        dbHelper.getEvents(new MyDatabaseHelper.EventsRetrievalCallback() {
            @Override
            public void onEventsRetrieved(List<Event> events) {
                if (events != null) {
                    // Filter events to match the time of the current HourEvent
                    for (Event event : events) {
                        String name = event.getName();
                        String time = event.getStart_time().toString()
                                + " - " + event.getEnd_time().toString();
                        generalLocations.add(new GeneralHelperClass(name, time));
                        adapter = new GeneralAdapter(generalLocations, listener);
                        generalRecycler.setAdapter(adapter);
                    }

                } else {
                    Toast.makeText(HomePage.this, "No events found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(HomePage.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        pos.add("Event1");
        pos.add("Event2");
        pos.add("Event3");
        pos.add("Event4");

//        Toast.makeText(this, String.valueOf(filteredEvents.size()), Toast.LENGTH_SHORT).show();
//        for (int n = 0; n < filteredEvents.size(); n++){
//            Toast.makeText(this, "in", Toast.LENGTH_SHORT).show();
//            String name = filteredEvents.get(n).getName();
//            String time = filteredEvents.get(n).getStart_time().toString()
//                    + " - " + filteredEvents.get(n).getEnd_time().toString();
//            generalLocations.add(new GeneralHelperClass(name, time));
//            adapter = new GeneralAdapter(generalLocations, listener);
//            generalRecycler.setAdapter(adapter);
//        }

        generalLocations.add(new GeneralHelperClass("Information Technology Conference", "4:00 - 6:00"));
        generalLocations.add(new GeneralHelperClass("Health Conference", "5:00 - 7:00"));
        generalLocations.add(new GeneralHelperClass("Research Conference", "6:00 - 8:00"));
        adapter = new GeneralAdapter(generalLocations, listener);
        generalRecycler.setAdapter(adapter);
    }

//    private void setOnClickListener() {
//        listener = new GeneralAdapter.RecyclerViewClickListener() {
//            @Override
//            public void onClick(View v, int position) {
////                listener.onClick(v, position);
//
//                Intent intent = new Intent(HomePage.this, Event_Page.class);
//                intent.putExtra("event_name", pos.get(position));
////                intent.putExtra("time", time);
//                startActivity(intent);
//            }
//        };
//    }

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
        navigationView.setCheckedItem(R.id.nav_session);

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
            startActivity(new Intent(HomePage.this, Home.class));
        }
        if(item.toString().equals("QR Sign-In")){
            startActivity(new Intent(HomePage.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(HomePage.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Organising Committee")){
            startActivity(new Intent(HomePage.this, Organising_Committee.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(HomePage.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(HomePage.this, About_Page.class));
        }
        if(item.toString().equals("Sign Out")){
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(HomePage.this, MainActivity.class));
                }
            });
        }
        return true;
    }

    private void setWeekView() {
        if (CalendarUtils.selectedDate == null) {
            LocalDate firstDayOfDecember = LocalDate.of(LocalDate.now().getYear(), 12, 1);
            CalendarUtils.selectedDate = firstDayOfDecember.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

            // Adjust to the Monday of the week
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        showEvents(CalendarUtils.selectedDate);

        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    private void setEventAdapter()
    {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
//         eventListView.setAdapter(eventAdapter); // Uncomment if you have an event list view
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        showEvents(date);
        setWeekView();
    }

    private void showEvents(LocalDate current_date) {
        generalRecycler.setHasFixedSize(true);
        generalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayList<GeneralHelperClass> generalLocations = new ArrayList<>();
        event_present = false;

        dbHelper.getEvents(new MyDatabaseHelper.EventsRetrievalCallback() {
            @Override
            public void onEventsRetrieved(List<Event> events) {
                if (events != null && !events.isEmpty()) {
                    // Sort and display events
                    Collections.sort(events, new Comparator<Event>() {
                        @Override
                        public int compare(Event event1, Event event2) {
                            return event1.getStart_time().compareTo(event2.getStart_time());
                        }
                    });

                    for (Event event : events) {
                        LocalDate date = event.getDate();
                        String time = event.getStart_time().toString() + " - " + event.getEnd_time().toString();

                        if (date.equals(current_date)) {
                            event_present = true;
                            generalLocations.add(new GeneralHelperClass(event.getName(), time));
                        }
                    }

                    if (event_present) {
                        adapter = new GeneralAdapter(generalLocations, listener);
                        generalRecycler.setAdapter(adapter);
                        error.setVisibility(View.GONE);
                    } else {
                        error.setVisibility(View.VISIBLE);
                        adapter = new GeneralAdapter(generalLocations, listener);
                        generalRecycler.setAdapter(adapter);
                    }

                } else {
                    Toast.makeText(HomePage.this, "No events found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(HomePage.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


//        if (generalLocations.isEmpty()){
//            error.setVisibility(View.VISIBLE);
//        }
//        else {
//            error.setVisibility(View.GONE);
//        }

//        generalLocations.add(new GeneralHelperClass("Research Conference", "6:00 - 8:00"));
//        adapter = new GeneralAdapter(generalLocations, listener);
//        generalRecycler.setAdapter(adapter);
    }
}
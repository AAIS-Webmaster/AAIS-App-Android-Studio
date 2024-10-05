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

public class Session_Page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CalendarAdapter.OnItemListener {
    static final float END_SCALE = 0.7f;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String personName, personEmail;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon, notification;
    Button unseen;
    LinearLayout contentView, error;
    RecyclerView generalRecycler;
    RecyclerView.Adapter adapter;
    private MyDatabaseHelper dbHelper;
    private SessionAdapter.RecyclerViewClickListener listener;
    ArrayList<String> pos;
    FloatingActionButton AddSession;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    Boolean session_present = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_page);
        dbHelper = new MyDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        notification = findViewById(R.id.notification);
        contentView = findViewById(R.id.content);
        error = findViewById(R.id.error);
        generalRecycler = findViewById(R.id.general_recycle);

        AddSession = findViewById(R.id.addSession);
        unseen = findViewById(R.id.unseen);

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null){
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();

            if (personEmail.equals("guptasdhuruv4@gmail.com")){
                AddSession.setVisibility(View.VISIBLE);
            }
        }

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
                startActivity(new Intent(Session_Page.this, Announcement_Page.class));
            }
        });

        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Session_Page.this, Announcement_Page.class));
            }
        });

        AddSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Session_Page.this, Add_Session_Page.class);
                intent.putExtra("date", Integer.valueOf(CalendarUtils.selectedDate.getDayOfMonth()));
                intent.putExtra("month", Integer.valueOf(CalendarUtils.selectedDate.getMonthValue()));
                intent.putExtra("year", Integer.valueOf(CalendarUtils.selectedDate.getYear()));
                startActivity(intent);
            }
        });
        navigationDrawer();
        setWeekView();
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
            startActivity(new Intent(Session_Page.this, Home_Page.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Session_Page.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Session_Page.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Committee")){
            startActivity(new Intent(Session_Page.this, Organising_Committee_Page.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Session_Page.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Session_Page.this, About_Page.class));
        }
        if(item.toString().equals("Sign Out")){
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(Session_Page.this, Google_Sign_In_Page.class));
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
        showSessions(CalendarUtils.selectedDate);

        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        showSessions(date);
        setWeekView();
    }

    private void showSessions(LocalDate current_date) {
        generalRecycler.setHasFixedSize(true);
        generalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayList<SessionHelperClass> generalLocations = new ArrayList<>();
        session_present = false;

        dbHelper.getSessions(new MyDatabaseHelper.SessionsRetrievalCallback() {
            @Override
            public void onEventsRetrieved(List<Session> sessions) {
                ArrayList<SessionHelperClass> generalLocations = new ArrayList<>();
                session_present = false;
                if (sessions != null && !sessions.isEmpty()) {
                    // Sort and display sessionS
                    Collections.sort(sessions, new Comparator<Session>() {
                        @Override
                        public int compare(Session session1, Session session2) {
                            return session1.getStart_time().compareTo(session2.getStart_time());
                        }
                    });

                    for (Session session : sessions) {
                        LocalDate date = session.getDate();
                        String time = session.getStart_time().toString() + " - " + session.getEnd_time().toString();

                        if (date.equals(current_date)) {
                            session_present = true;
                            generalLocations.add(new SessionHelperClass("Track: " + session.getTrack(), session.getName(), time));
                        }
                    }

                    if (session_present) {
                        error.setVisibility(View.GONE);
                    } else {
                        error.setVisibility(View.VISIBLE);
                    }

                    adapter = new SessionAdapter(generalLocations, listener);
                    generalRecycler.setAdapter(adapter);

                } else {
                    Toast.makeText(Session_Page.this, "No sessionS found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(Session_Page.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
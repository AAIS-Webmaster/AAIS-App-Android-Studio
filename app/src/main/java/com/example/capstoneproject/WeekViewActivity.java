package com.example.capstoneproject;

import static com.example.capstoneproject.CalendarUtils.daysInWeekArray;
import static com.example.capstoneproject.CalendarUtils.monthYearFromDate;
import static com.example.capstoneproject.CalendarUtils.selectedDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText, monthDayText, dayOfWeekTV;
    private RecyclerView calendarRecyclerView;
    private ListView hourListView;
    private MyDatabaseHelper dbHelper;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String personName, personEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);

        dbHelper = new MyDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
        }

        initWidgets();
        setWeekView();
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        hourListView = findViewById(R.id.hourListView);
    }

    private void setWeekView() {
        if (CalendarUtils.selectedDate == null) {
            CalendarUtils.selectedDate = LocalDate.now(); // Ensure it's initialized
        }
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
//        setHourAdapter();
    }

    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView(); // Refresh week view and adapters
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView(); // Refresh week view and adapters
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
//        setDayView(); // Refresh day view and adapters
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setEventAdapter(); // Refresh event adapter
//        setDayView(); // Refresh day view and adapters
    }

    private void setEventAdapter()
    {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
//         eventListView.setAdapter(eventAdapter); // Uncomment if you have an event list view
    }

    public void newEventAction(View view)
    {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    public void dailyAction(View view) {
        startActivity(new Intent(this, DailyCalendarActivity.class));
    }

//    private void setDayView()
//    {
//        // Uncomment if you have UI elements for day view
////         monthDayText.setText(CalendarUtils.monthDayFromDate(CalendarUtils.selectedDate));
////         String dayOfWeek = CalendarUtils.selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
////         dayOfWeekTV.setText(dayOfWeek);
//
//        setHourAdapter(); // Ensure HourAdapter is updated for the current day
//    }

//    private void setHourAdapter()
//    {
//        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEventList());
//        hourListView.setAdapter(hourAdapter);
//    }

    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(CalendarUtils.selectedDate, time);
            HourEvent hourEvent = new HourEvent(CalendarUtils.selectedDate, time, events);
            list.add(hourEvent);
        }

        return list;
    }

    public void previousDayAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
//        setDayView(); // Refresh day view and adapters
    }

    public void nextDayAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
//        setDayView(); // Refresh day view and adapters
    }
}

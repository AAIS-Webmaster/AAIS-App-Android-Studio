package com.example.capstoneproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Add_Event_Page extends AppCompatActivity {

    TextView addStartTime, addEndTime, addDate, error, paper1_text, paper2_text, paper3_text, paper4_text;
    CardView show_location, paper21, paper22, paper31, paper32, paper41, paper42;
    EditText track, address, location, chair, paper_name1, paper_url1, paper_name2, paper_url2, paper_name3, paper_url3, paper_name4, paper_url4;
    Button add, cancel;
    NumberPicker numberPicker;
//    ImageButton more_papers;
    int hour, minute, paper_number;
    String numPaper, track_selected;
    Boolean visible = false;
    private DatePickerDialog datePickerDialog;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_add_event);
        dbHelper = new MyDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the Spinner by ID
        Spinner spinner = findViewById(R.id.spinner);
        Spinner track_spinner = findViewById(R.id.tracksSpinner);
        addStartTime = findViewById(R.id.add_start_time);
        addEndTime = findViewById(R.id.add_end_time);
        addDate = findViewById(R.id.add_date);
        track = findViewById(R.id.enter_track);
        show_location = findViewById(R.id.show_location);
        location = findViewById(R.id.enter_location);
        address = findViewById(R.id.add_address);
        chair = findViewById(R.id.add_session_chair);

        error = findViewById(R.id.error);

        paper21 = findViewById(R.id.paper21);
        paper22 = findViewById(R.id.paper22);
        paper31 = findViewById(R.id.paper31);
        paper32 = findViewById(R.id.paper32);
        paper41 = findViewById(R.id.paper41);
        paper42 = findViewById(R.id.paper42);

        paper_name1 = findViewById(R.id.paper1_name);
        paper_url1 = findViewById(R.id.paper1_url);
        paper_name2 = findViewById(R.id.paper2_name);
        paper_url2 = findViewById(R.id.paper2_url);
        paper_name3 = findViewById(R.id.paper3_name);
        paper_url3 = findViewById(R.id.paper3_url);
        paper_name4 = findViewById(R.id.paper4_name);
        paper_url4 = findViewById(R.id.paper4_url);

        paper1_text = findViewById(R.id.paper1_text);
        paper2_text = findViewById(R.id.paper2_text);
        paper3_text = findViewById(R.id.paper3_text);
        paper4_text = findViewById(R.id.paper4_text);

        add = findViewById(R.id.post);
        cancel = findViewById(R.id.cancel_action);
//        numberPicker = findViewById(R.id.number_picker);
//        more_papers = findViewById(R.id.more_papers);

//        paper_number = 1;
//        numberPicker.setTextColor(getResources().getColor(R.color.text));
//        numberPicker.setMaxValue(4);
//        numberPicker.setMinValue(1);
//        numberPicker.setValue(1);
//        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                paper_number = newVal;
//
//                if (paper_number == 1){
//                    paper2_text.setVisibility(View.GONE);
//                    paper21.setVisibility(View.GONE);
//                    paper22.setVisibility(View.GONE);
//
//                    paper3_text.setVisibility(View.GONE);
//                    paper31.setVisibility(View.GONE);
//                    paper32.setVisibility(View.GONE);
//
//                    paper4_text.setVisibility(View.GONE);
//                    paper41.setVisibility(View.GONE);
//                    paper42.setVisibility(View.GONE);
//                }
//
//                else if (paper_number == 2){
//                    paper2_text.setVisibility(View.VISIBLE);
//                    paper21.setVisibility(View.VISIBLE);
//                    paper22.setVisibility(View.VISIBLE);
//
//                    paper3_text.setVisibility(View.GONE);
//                    paper31.setVisibility(View.GONE);
//                    paper32.setVisibility(View.GONE);
//
//                    paper4_text.setVisibility(View.GONE);
//                    paper41.setVisibility(View.GONE);
//                    paper42.setVisibility(View.GONE);
//                }
//
//                else if (paper_number == 3){
//                    paper2_text.setVisibility(View.VISIBLE);
//                    paper21.setVisibility(View.VISIBLE);
//                    paper22.setVisibility(View.VISIBLE);
//
//                    paper3_text.setVisibility(View.VISIBLE);
//                    paper31.setVisibility(View.VISIBLE);
//                    paper32.setVisibility(View.VISIBLE);
//
//                    paper4_text.setVisibility(View.GONE);
//                    paper41.setVisibility(View.GONE);
//                    paper42.setVisibility(View.GONE);
//                }
//                else if (paper_number == 4){
//                    paper2_text.setVisibility(View.VISIBLE);
//                    paper21.setVisibility(View.VISIBLE);
//                    paper22.setVisibility(View.VISIBLE);
//
//                    paper3_text.setVisibility(View.VISIBLE);
//                    paper31.setVisibility(View.VISIBLE);
//                    paper32.setVisibility(View.VISIBLE);
//
//                    paper4_text.setVisibility(View.VISIBLE);
//                    paper41.setVisibility(View.VISIBLE);
//                    paper42.setVisibility(View.VISIBLE);
//                }
//            }
//        });

//        more_papers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (paper_number == 0){
//                    if (!paper_name1.getText().toString().equals("") &&
//                            !paper_url1.getText().toString().equals("")){
//                        paper2_text.setVisibility(View.VISIBLE);
//                        paper21.setVisibility(View.VISIBLE);
//                        paper22.setVisibility(View.VISIBLE);
//                        paper_number = 1;
//                    }
//                    if (paper_name1.getText().toString().equals("")) {
//                        paper_name1.setError("Paper 1 name cannot be empty");
//                        paper_name1.requestFocus();
//                    }
//                    else if (paper_url1.getText().toString().equals("")) {
//                        paper_url1.setError("Paper 1 URL cannot be empty");
//                        paper_url1.requestFocus();
//                    }
//                }
//                else if (paper_number == 1){
//                    if (!paper_name1.getText().toString().equals("") &&
//                            !paper_url1.getText().toString().equals("") &&
//                            !paper_name2.getText().toString().equals("") &&
//                            !paper_url2.getText().toString().equals("")){
//                        paper3_text.setVisibility(View.VISIBLE);
//                        paper31.setVisibility(View.VISIBLE);
//                        paper32.setVisibility(View.VISIBLE);
//                        paper_number = 2;
////                        error.setVisibility(View.GONE);
//                    }
//                    if (paper_name2.getText().toString().equals("")) {
//                        paper_name2.setError("Paper 2 name cannot be empty");
//                        paper_name2.requestFocus();
//                    }
//                    else if (paper_url2.getText().toString().equals("")) {
//                        paper_url2.setError("Paper 2 URL cannot be empty");
//                        paper_url2.requestFocus();
//                    }
//                }
//                else if (paper_number == 2){
//                    if (!paper_name1.getText().toString().equals("") &&
//                            !paper_url1.getText().toString().equals("") &&
//                            !paper_name2.getText().toString().equals("") &&
//                            !paper_url2.getText().toString().equals("") &&
//                            !paper_name3.getText().toString().equals("") &&
//                            !paper_url3.getText().toString().equals("")){
//                        paper4_text.setVisibility(View.VISIBLE);
//                        paper41.setVisibility(View.VISIBLE);
//                        paper42.setVisibility(View.VISIBLE);
//                        paper_number = 3;
//                    }
//                    if (paper_name3.getText().toString().equals("")) {
//                        paper_name3.setError("Paper 3 name cannot be empty");
//                        paper_name3.requestFocus();
//                    }
//                    else if (paper_url3.getText().toString().equals("")) {
//                        paper_url3.setError("Paper 3 URL cannot be empty");
//                        paper_url3.requestFocus();
//                    }
//                }
//            }
//        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Add_Event_Page.this, Session_Page.class));
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (visible) {
//                    location_info = location.getText().toString();
//                }

                String name = track.getText().toString();
                String track_location = location.getText().toString();
                String track_address = address.getText().toString();
                String track_date = addDate.getText().toString();
                String track_start_time = addStartTime.getText().toString();
                String track_end_time = addEndTime.getText().toString();
                String track_chair = chair.getText().toString();
                String paper_name_1 = paper_name1.getText().toString();
                String paper_url_1 = paper_url1.getText().toString();
                String paper_name_2 = paper_name2.getText().toString();
                String paper_url_2 = paper_url2.getText().toString();
                String paper_name_3 = paper_name3.getText().toString();
                String paper_url_3 = paper_url3.getText().toString();
                String paper_name_4 = paper_name4.getText().toString();
                String paper_url_4 = paper_url4.getText().toString();

//                Toast.makeText(Pop_up_add_event.this, String.valueOf(location_info), Toast.LENGTH_SHORT).show();
                // Validate if any field is empty
                if (name.isEmpty()) {
                    track.setError("Track name cannot be empty");
                    track.requestFocus();
                } else if (track_location.isEmpty()) {
                    location.setError("Location cannot be empty");
                    location.requestFocus();
                } else if (track_address.isEmpty()) {
                    address.setError("Track address can not be empty");
                    address.requestFocus();
                } else if (track_date.isEmpty()) {
                    addDate.setError("Track date cannot be empty");
                    addDate.requestFocus();
                } else if (track_start_time.equals("Select Start Time")) {
                    addStartTime.setError("Start time cannot be empty");
                    addStartTime.requestFocus();
                } else if (track_end_time.equals("Select End Time") || track_end_time.equals(track_start_time)) {
                    addEndTime.setError("Select a Valid End time");
                    addEndTime.requestFocus();
                } else if (track_chair.isEmpty()) {
                    chair.setError("Track chair cannot be empty");
                    chair.requestFocus();
                } else if (paper_name_1.isEmpty()) {
                    paper_name1.setError("Paper 1 name cannot be empty");
                    paper_name1.requestFocus();
                } else if (paper_url_1.isEmpty()) {
                    paper_url1.setError("Paper 1 URL cannot be empty");
                    paper_url1.requestFocus();
                } else if (paper_name_2.isEmpty() && Integer.parseInt(numPaper) >= 2) {
                    paper_name2.setError("Paper 2 name cannot be empty");
                    paper_name2.requestFocus();
                } else if (paper_url_2.isEmpty() && Integer.parseInt(numPaper) >= 2) {
                    paper_url2.setError("Paper 2 URL cannot be empty");
                    paper_url2.requestFocus();
                } else if (paper_name_3.isEmpty() && Integer.parseInt(numPaper) >= 3) {
                    paper_name3.setError("Paper 3 name cannot be empty");
                    paper_name3.requestFocus();
                } else if (paper_url_3.isEmpty() && Integer.parseInt(numPaper) >= 3) {
                    paper_url3.setError("Paper 3 URL cannot be empty");
                    paper_url3.requestFocus();
                } else if (paper_name_4.isEmpty() && Integer.parseInt(numPaper) == 4) {
                    paper_name4.setError("Paper 4 name cannot be empty");
                    paper_name4.requestFocus();
                } else if (paper_url_4.isEmpty() && Integer.parseInt(numPaper) == 4) {
                    paper_url4.setError("Paper 4 URL cannot be empty");
                    paper_url4.requestFocus();
                } else {
                    error.setVisibility(View.GONE);
                    // Proceed only if all fields are filled
                    DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
                    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    try {
                        LocalTime startTime = LocalTime.parse(track_start_time, formatterTime);
                        LocalTime endTime = LocalTime.parse(track_end_time, formatterTime);
                        LocalDate localDate = LocalDate.parse(track_date, formatterDate);

                        List<Event> events = new ArrayList<>();
                        events.add(new Event(track_selected, name, localDate, startTime, endTime,
                                location.getText().toString(), track_address, track_chair, paper_name_1, paper_url_1,
                                paper_name_2, paper_url_2, paper_name_3, paper_url_3,
                                paper_name_4, paper_url_4));

                        // Send the events to the database
                        dbHelper.sendEvents(events);
                        startActivity(new Intent(Add_Event_Page.this, Session_Page.class));

                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid time format: " + e.getMessage());
                    }
                }
            }
        });

        addStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        hour = selectedHour;
                        minute = selectedMinute;
                        addStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
                        addStartTime.setTextColor(getResources().getColor(R.color.text));
                        addStartTime.setError(null);
                    }
                };

                 int style = AlertDialog.THEME_HOLO_LIGHT;

                TimePickerDialog timePickerDialog = new TimePickerDialog(Add_Event_Page.this, style, onTimeSetListener, hour, minute, true);
                timePickerDialog.show();
            }
        });

        addEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        hour = selectedHour;
                        minute = selectedMinute;
                        addEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
                        addEndTime.setTextColor(getResources().getColor(R.color.text));
                        addEndTime.setError(null);
                    }
                };

                int style = AlertDialog.THEME_HOLO_LIGHT;

                TimePickerDialog timePickerDialog = new TimePickerDialog(Add_Event_Page.this, style, onTimeSetListener, hour, minute, true);
                timePickerDialog.show();
            }
        });

        initDatePicker();

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            int selected_day = extra.getInt("date");
            int selected_month = extra.getInt("month");
            int selected_year = extra.getInt("year");

            // Ensure day and month are formatted with two digits
            String date = String.format("%d-%02d-%02d", selected_year, selected_month, selected_day);

            // Parse the date
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatterDate);

            // Set the date to the TextView
            addDate.setText(localDate.toString());
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, R.layout.spinner_design);
        adapter.setDropDownViewResource(R.layout.spinner_design);
        spinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> track_adapter = ArrayAdapter.createFromResource(this,
                R.array.tracks_spinner, R.layout.spinner_design);
        adapter.setDropDownViewResource(R.layout.spinner_design);
        track_spinner.setAdapter(track_adapter);

        // Set a listener for the Number of Paper Spinner
        track_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                track_selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set a listener for the Number of Paper Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                numPaper = selectedOption;

                if (Integer.parseInt(numPaper) == 1){
                    paper2_text.setVisibility(View.GONE);
                    paper21.setVisibility(View.GONE);
                    paper22.setVisibility(View.GONE);
                    paper_name2.setText("");
                    paper_url2.setText("");

                    paper3_text.setVisibility(View.GONE);
                    paper31.setVisibility(View.GONE);
                    paper32.setVisibility(View.GONE);
                    paper_name3.setText("");
                    paper_url3.setText("");

                    paper4_text.setVisibility(View.GONE);
                    paper41.setVisibility(View.GONE);
                    paper42.setVisibility(View.GONE);
                    paper_name4.setText("");
                    paper_url4.setText("");
                }

                else if (Integer.parseInt(numPaper) == 2){
                    paper2_text.setVisibility(View.VISIBLE);
                    paper21.setVisibility(View.VISIBLE);
                    paper22.setVisibility(View.VISIBLE);

                    paper3_text.setVisibility(View.GONE);
                    paper31.setVisibility(View.GONE);
                    paper32.setVisibility(View.GONE);
                    paper_name3.setText("");
                    paper_url3.setText("");

                    paper4_text.setVisibility(View.GONE);
                    paper41.setVisibility(View.GONE);
                    paper42.setVisibility(View.GONE);
                    paper_name4.setText("");
                    paper_url4.setText("");
                }

                else if (Integer.parseInt(numPaper) == 3){
                    paper2_text.setVisibility(View.VISIBLE);
                    paper21.setVisibility(View.VISIBLE);
                    paper22.setVisibility(View.VISIBLE);

                    paper3_text.setVisibility(View.VISIBLE);
                    paper31.setVisibility(View.VISIBLE);
                    paper32.setVisibility(View.VISIBLE);

                    paper4_text.setVisibility(View.GONE);
                    paper41.setVisibility(View.GONE);
                    paper42.setVisibility(View.GONE);
                    paper_name4.setText("");
                    paper_url4.setText("");
                }

                else if (Integer.parseInt(numPaper) == 4){
                    paper2_text.setVisibility(View.VISIBLE);
                    paper21.setVisibility(View.VISIBLE);
                    paper22.setVisibility(View.VISIBLE);

                    paper3_text.setVisibility(View.VISIBLE);
                    paper31.setVisibility(View.VISIBLE);
                    paper32.setVisibility(View.VISIBLE);

                    paper4_text.setVisibility(View.VISIBLE);
                    paper41.setVisibility(View.VISIBLE);
                    paper42.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {// Handle the case where nothing is selected
            }
        });
    }

//    private String date() {
//        Calendar cal = Calendar.getInstance();
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH);
//        month = month + 1;
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//        return makeDateString(day, month, year);
//    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                // Create LocalDate object
                LocalDate date = LocalDate.of(year, month, day);
                // Format the date
                String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                // Set the formatted date in the TextView
                addDate.setText(formattedDate);
//                addDate.setTextColor(getResources().getColor(R.color.text));
            }
        };

//        Calendar cal = Calendar.getInstance();

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            int day = extra.getInt("date");
            int month = extra.getInt("month");
            int year = extra.getInt("year");

            int style = AlertDialog.THEME_HOLO_LIGHT;

            datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month - 1, day);
        }

//        else {
//            int year = cal.get(Calendar.YEAR);
//            int month = cal.get(Calendar.MONTH);
//            int day = cal.get(Calendar.DAY_OF_MONTH);
//
//            int style = AlertDialog.THEME_HOLO_LIGHT;
//
//            datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
//        }
    }

    private String makeDateString(int day, int month, int year)
    {
        return year + "-" + month + "-" + day;
//        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
}
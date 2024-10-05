package com.example.capstoneproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper {
    private final DatabaseReference dbRef;

    public MyDatabaseHelper() {
        // Initialize the Firebase Database reference
        dbRef = FirebaseDatabase.getInstance("https://capstone2024uc-e385c-default-rtdb.firebaseio.com/")
                .getReference();
    }

    // Callback interface for sessions retrieval
    public interface SessionsRetrievalCallback {
        void onEventsRetrieved(List<Session> sessions);
        void onError(Exception e);
    }

    // Callback interface for data retrieval
    public interface DataRetrievalCallback {
        void onDataRetrieved(List<Announcement> announcements);
        void onError(Exception e);
    }

    public interface FirstHelperClassRetrievalCallback {
        void onDataRetrieved(List<ChatPageHelperClass> chatPageHelperClasses);
        void onError(Exception e);
    }

    // Insert data method with category, key, and value under the email
    public void insertData(String category, List<Announcement> announcements) {
        // Reference to the "Announcement" node in Firebase
        DatabaseReference announcementsRef = dbRef.child(category);

        for (Announcement announcement : announcements) {
            DatabaseReference announcementRef = announcementsRef.push(); // Create a new entry

            // Set announcement details
            announcementRef.child("title").setValue(announcement.getTitle());
            announcementRef.child("description").setValue(announcement.getDescription());
            announcementRef.child("dateTime").setValue(announcement.getDateTime());
        }
    }

    // Retrieve all keys and values under a specific email and category with a callback
    public void getData(String category, DataRetrievalCallback callback) {
        // Reference to the "Announcement" node in Firebase
        DatabaseReference announcementsRef = dbRef.child(category);

        announcementsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Announcement> announcementList = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve values from the snapshot
                        String title = childSnapshot.child("title").getValue(String.class);
                        String description = childSnapshot.child("description").getValue(String.class);
                        String dateTimeString = childSnapshot.child("dateTime").getValue(String.class);

                        // Create an Announcement object
                        Announcement announcement = new Announcement(title, description, dateTimeString);

                        // Add the Announcement object to the list
                        announcementList.add(announcement);
                    }
                    callback.onDataRetrieved(announcementList);
                } else {
                    callback.onDataRetrieved(new ArrayList<>()); // Return an empty list if no data
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void deleteAnnouncement(String title, String description, String dateTime) {
        // Reference to the "Announcement" node in Firebase
        DatabaseReference announcementsRef = dbRef.child("Announcement");

        // Query to find the announcement with matching title, description, and dateTime
        announcementsRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean announcementDeleted = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve fields from the snapshot
                    String dbDescription = childSnapshot.child("description").getValue(String.class);
                    String dbDateTime = childSnapshot.child("dateTime").getValue(String.class);

                    // Match all fields
                    if (dbDescription != null && dbDateTime != null && dbDescription.equals(description) && dbDateTime.equals(dateTime)) {
                        // Delete the matching announcement
                        childSnapshot.getRef().removeValue();
                        announcementDeleted = true;
                        System.out.println("Announcement deleted: " + childSnapshot.getKey());
                        break;  // Exit loop after deleting the first matching announcement
                    }
                }

                if (!announcementDeleted) {
                    System.out.println("No matching announcement found with the provided details.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Failed to delete announcement: " + databaseError.getMessage());
            }
        });
    }

    public void sendSession(List<Session> sessions) {
        // Reference to the "Session" node under the specified email
        DatabaseReference emailRef = dbRef.child("Session");

        for (Session session : sessions) {
            DatabaseReference sessionRef = emailRef.push(); // Create a new entry

            // Set session details
            sessionRef.child("track").setValue(session.getTrack());
            sessionRef.child("name").setValue(session.getName());
            sessionRef.child("date").setValue(session.getDate().toString());
            sessionRef.child("startTime").setValue(session.getStart_time().toString());
            sessionRef.child("endTime").setValue(session.getEnd_time().toString());
            sessionRef.child("location").setValue(session.getLocation());
            sessionRef.child("address").setValue(session.getAddress());
            sessionRef.child("sessionChair").setValue(session.getChair());
            sessionRef.child("paper1_name").setValue(session.getPaper1_name());
            sessionRef.child("paper1_url").setValue(session.getPaper1_url());
            sessionRef.child("paper2_name").setValue(session.getPaper2_name());
            sessionRef.child("paper2_url").setValue(session.getPaper2_url());
            sessionRef.child("paper3_name").setValue(session.getPaper3_name());
            sessionRef.child("paper3_url").setValue(session.getPaper3_url());
            sessionRef.child("paper4_name").setValue(session.getPaper4_name());
            sessionRef.child("paper4_url").setValue(session.getPaper4_url());

        }
    }

    // Fetch sessions method
    public void getSessions(SessionsRetrievalCallback callback) {
        // Reference to the "Session" node
        DatabaseReference emailRef = dbRef.child("Session");

        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Session> sessions = new ArrayList<>();
                    for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                        // Fetch session details from snapshot
                        String track = sessionSnapshot.child("track").getValue(String.class);
                        String name = sessionSnapshot.child("name").getValue(String.class);
                        String dateStr = sessionSnapshot.child("date").getValue(String.class);
                        String startTime = sessionSnapshot.child("startTime").getValue(String.class);
                        String endTime = sessionSnapshot.child("endTime").getValue(String.class);
                        String location = sessionSnapshot.child("location").getValue(String.class);
                        String address = sessionSnapshot.child("address").getValue(String.class);
                        String description = sessionSnapshot.child("sessionChair").getValue(String.class);
                        String paper1_name = sessionSnapshot.child("paper1_name").getValue(String.class);
                        String paper1_url = sessionSnapshot.child("paper1_url").getValue(String.class);
                        String paper2_name = sessionSnapshot.child("paper2_name").getValue(String.class);
                        String paper2_url = sessionSnapshot.child("paper2_url").getValue(String.class);
                        String paper3_name = sessionSnapshot.child("paper3_name").getValue(String.class);
                        String paper3_url = sessionSnapshot.child("paper3_url").getValue(String.class);
                        String paper4_name = sessionSnapshot.child("paper4_name").getValue(String.class);
                        String paper4_url = sessionSnapshot.child("paper4_url").getValue(String.class);

                        // Initialize LocalDate and LocalTime variables
                        LocalDate date = null;
                        LocalTime start_time = null;
                        LocalTime end_time = null;

                        // Convert dateStr to LocalDate, but handle null or invalid format
                        if (dateStr != null && !dateStr.isEmpty()) {
                            try {
                                date = LocalDate.parse(dateStr);
                            } catch (DateTimeParseException e) {
                                // Handle parsing error, log, or skip the session
                                e.printStackTrace();
                                continue; // Skip this session if the date format is invalid
                            }
                        }

                        // Convert startTime to LocalTime, but handle null or invalid format
                        if (startTime != null && !startTime.isEmpty()) {
                            try {
                                start_time = LocalTime.parse(startTime);
                            } catch (DateTimeParseException e) {
                                // Handle parsing error, log, or skip the session
                                e.printStackTrace();
                                continue; // Skip this session if the start time format is invalid
                            }
                        }

                        // Convert endTime to LocalTime, but handle null or invalid format
                        if (endTime != null && !endTime.isEmpty()) {
                            try {
                                end_time = LocalTime.parse(endTime);
                            } catch (DateTimeParseException e) {
                                // Handle parsing error, log, or skip the session
                                e.printStackTrace();
                                continue; // Skip this session if the end time format is invalid
                            }
                        }

                        // Ensure all required fields are present before creating an Session object
                        if (date != null && start_time != null && end_time != null) {
                            // Create Session object and add to list
                            Session session = new Session(track, name, date, start_time, end_time, location, address, description,
                                    paper1_name, paper1_url, paper2_name, paper2_url, paper3_name, paper3_url, paper4_name, paper4_url);
                            sessions.add(session);
                        }
                    }
                    callback.onEventsRetrieved(sessions); // Pass sessions to callback
                } else {
                    callback.onEventsRetrieved(new ArrayList<>()); // No data found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void deleteSession(String sessionName, String sessionDate, String startTime, String endTime, String sessionChair) {
        // Reference to the "Session" node
        DatabaseReference sessionsRef = dbRef.child("Session");

        // Query to find the session with matching details
        sessionsRef.orderByChild("name").equalTo(sessionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean sessionDeleted = false;

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve fields from the session to match with the input
                    String dbDate = sessionSnapshot.child("date").getValue(String.class);
                    String dbStartTime = sessionSnapshot.child("startTime").getValue(String.class);
                    String dbEndTime = sessionSnapshot.child("endTime").getValue(String.class);
                    String dbChair = sessionSnapshot.child("sessionChair").getValue(String.class);  // "description" as session chair

                    // Match all fields (name, date, startTime, endTime, chair)
                    if (dbDate.equals(sessionDate) && dbStartTime.equals(startTime) && dbEndTime.equals(endTime) && dbChair.equals(sessionChair)) {
                        // Delete the matching session
                        sessionSnapshot.getRef().removeValue();
                        sessionDeleted = true;
                        System.out.println("Session deleted: " + sessionSnapshot.getKey());
                        break;  // Exit loop after deleting the first matching session
                    }
                }

                if (!sessionDeleted) {
                    System.out.println("No matching session found with the provided details.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Failed to delete session: " + databaseError.getMessage());
            }
        });
    }

    public void sendSeenAnnouncement(String email, boolean seen) {
        // Replace '.' in email with ',' since Firebase keys can't contain '.'
        String sanitizedEmail = email.replace(".", ",");

        // Reference to the "AnnouncementSeen" node
        DatabaseReference announcementSeenRef = dbRef.child("AnnouncementSeen").child(sanitizedEmail);

        // Set the seen value
        announcementSeenRef.setValue(seen ? "true" : "false")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("SeenAnnouncement status updated successfully.");
                    } else {
                        System.out.println("Failed to update SeenAnnouncement status: " + task.getException().getMessage());
                    }
                });
    }

    public void getSeenAnnouncement(String email, ValueEventListener listener) {
        try {
            // Replace '.' in email with ',' since Firebase keys can't contain '.'
            String sanitizedEmail = email.replace(".", ",");

            // Reference to the "AnnouncementSeen" node
            DatabaseReference announcementSeenRef = dbRef.child("AnnouncementSeen").child(sanitizedEmail);

            // Listen for real-time updates to the "seen" status
            announcementSeenRef.addValueEventListener(listener);
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving SeenAnnouncement status: " + e.getMessage());
        }
    }

    public void removeAllSeenAnnouncements() {
        try {
            // Reference to the "AnnouncementSeen" node
            DatabaseReference announcementSeenRef = dbRef.child("AnnouncementSeen");

            // Remove all data under "AnnouncementSeen"
            announcementSeenRef.removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("All SeenAnnouncements data removed successfully.");
                        } else {
                            System.out.println("Failed to remove SeenAnnouncements data: " + task.getException().getMessage());
                        }
                    });
        } catch (Exception e) {
            System.out.println("An error occurred while removing SeenAnnouncements data: " + e.getMessage());
        }
    }

    public void insertMessage(String name, String email, String conversationText, String dateTime) {
        DatabaseReference conversationsRef = dbRef.child("General").push(); // Create a new key under "General"

        String id = conversationsRef.getKey(); // Generate a unique ID for the message
        conversationsRef.child("id").setValue(id);
        conversationsRef.child("name").setValue(name);
        conversationsRef.child("email").setValue(email);
        conversationsRef.child("conversation_text").setValue(conversationText);
        conversationsRef.child("dateTime").setValue(dateTime);
        conversationsRef.child("isHeader").setValue(false); // Explicitly mark as not a header
    }

    public void insertHeader(String date) {
        DatabaseReference headerRef = dbRef.child("General").push();
        String id = headerRef.getKey();
        headerRef.child("id").setValue(id);
        headerRef.child("dateHeader").setValue(date);
        headerRef.child("isHeader").setValue(true);
        Log.d("DatabaseHelper", "Header inserted: " + date);
    }

    public void getConversations(FirstHelperClassRetrievalCallback callback) {
        DatabaseReference conversationsRef = dbRef.child("General");

        conversationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatPageHelperClass> conversationsList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    String lastDateHeader = null;

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        boolean isHeader = childSnapshot.child("isHeader").getValue(Boolean.class) != null
                                && childSnapshot.child("isHeader").getValue(Boolean.class);

                        if (isHeader) {
                            String dateHeader = childSnapshot.child("dateHeader").getValue(String.class);
                            if (!dateHeader.equals(lastDateHeader)) {
                                // Only add header if it's different from the last one
                                ChatPageHelperClass header = new ChatPageHelperClass(null, dateHeader, true);
                                conversationsList.add(header);
                                lastDateHeader = dateHeader;
                            }
                        } else {
                            String id = childSnapshot.child("id").getValue(String.class);
                            String name = childSnapshot.child("name").getValue(String.class);
                            String conversationText = childSnapshot.child("conversation_text").getValue(String.class);
                            String dateTime = childSnapshot.child("dateTime").getValue(String.class);
                            String email = childSnapshot.child("email").getValue(String.class); // Extract email

                            ChatPageHelperClass conversation = new ChatPageHelperClass(id, name, conversationText, dateTime, email, false);
                            conversationsList.add(conversation);
                        }
                    }
                    callback.onDataRetrieved(conversationsList);
                } else {
                    callback.onDataRetrieved(new ArrayList<>()); // Return empty list if no data found
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
                Log.e("Group_Chat_Page", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    public void saveUserDataWithImageUrl(String personEmail, String personName, String imageUrl) {
        String sanitizedEmail = personEmail.replace(".", ",");
        // Reference to the Firebase Realtime Database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(sanitizedEmail);

        // Save the user data under the unique key
        dbRef.child("personEmail").setValue(personEmail);
        dbRef.child("personName").setValue(personName);
        dbRef.child("url").setValue(imageUrl); // The image URL already available
    }

    public void getUserImageUrl(String email, ImageUrlCallback callback) {
        // Replace '.' with '_' in the email to match the Firebase key format
        String sanitizedEmail = email.replace(".", ",");

        DatabaseReference userRef = dbRef.child("Users").child(sanitizedEmail);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.child("url").getValue(String.class);
                    callback.onImageUrlRetrieved(imageUrl);
                } else {
                    // If the user data does not exist in the database
                    callback.onImageUrlRetrieved(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface ImageUrlCallback {
        void onImageUrlRetrieved(String imageUrl);
        void onError(Exception e);
    }
}

package com.example.capstoneproject;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper {
    private final DatabaseReference dbRef;

    public MyDatabaseHelper() {
        // Initialize the Firebase Database reference
        dbRef = FirebaseDatabase.getInstance("https://capstone-project-432506-default-rtdb.firebaseio.com/")
                .getReference();
    }

    // Callback interface for events retrieval
    public interface EventsRetrievalCallback {
        void onEventsRetrieved(List<Event> events);
        void onError(Exception e);
    }

    // Callback interface for data retrieval
    public interface DataRetrievalCallback {
        void onDataRetrieved(List<Announcement> announcements);
        void onError(Exception e);
    }

    public interface FirstHelperClassRetrievalCallback {
        void onDataRetrieved(List<FirstHelperClass> firstHelperClasses);
        void onError(Exception e);
    }

//    public void addConversationsListener(ValueEventListener listener) {
//        DatabaseReference conversationsRef = dbRef.child("General");
//        conversationsRef.addValueEventListener(listener);
//    }

    // Insert data method with category, key, and value under the email
    public void insertData(String category, List<Announcement> announcements) {
        // Reference to the "Announcements" node in Firebase
//        String sanitizedEmail = email.replace(".", ",");
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
        // Reference to the "Announcements" node in Firebase
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
        // Reference to the "Announcements" node in Firebase
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

//    public void insertData(String email, String category, String key, String value, LocalDateTime dateTime) {
//        // Replace '.' in email with ',' since Firebase keys can't contain '.'
//        String sanitizedEmail = email.replace(".", ",");
//
//        // Reference to the category under the email
//        DatabaseReference categoryRef = dbRef.child(sanitizedEmail).child(category).child(key);
//
//        // Add the key-value pair under the category
//        categoryRef.setValue(value)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        System.out.println("Data inserted successfully.");
//                    } else {
//                        System.out.println("Failed to insert data: " + task.getException().getMessage());
//                    }
//                });
//        categoryRef.setValue(dateTime);
//    }

    public void sendEvents(List<Event> events) {
        // Replace '.' in email with ',' since Firebase keys can't contain '.'
//        String sanitizedEmail = email.replace(".", ",");

        // Reference to the "events" node under the specified email
        DatabaseReference emailRef = dbRef.child("Events");

        for (Event event : events) {
            DatabaseReference eventRef = emailRef.push(); // Create a new entry

            // Set event details
            eventRef.child("name").setValue(event.getName());
            eventRef.child("date").setValue(event.getDate().toString());
            eventRef.child("startTime").setValue(event.getStart_time().toString());
            eventRef.child("endTime").setValue(event.getEnd_time().toString());
            eventRef.child("location").setValue(event.getLocation());
            eventRef.child("address").setValue(event.getAddress());
            eventRef.child("description").setValue(event.getChair());
            eventRef.child("paper1_name").setValue(event.getPaper1_name());
            eventRef.child("paper1_url").setValue(event.getPaper1_url());
            eventRef.child("paper2_name").setValue(event.getPaper2_name());
            eventRef.child("paper2_url").setValue(event.getPaper2_url());
            eventRef.child("paper3_name").setValue(event.getPaper3_name());
            eventRef.child("paper3_url").setValue(event.getPaper3_url());
            eventRef.child("paper4_name").setValue(event.getPaper4_name());
            eventRef.child("paper4_url").setValue(event.getPaper4_url());

        }
    }

    // Fetch events method
    public void getEvents(EventsRetrievalCallback callback) {
        // Reference to the "Events" node
        DatabaseReference emailRef = dbRef.child("Events");

        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Event> events = new ArrayList<>();
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        // Fetch event details from snapshot
                        String name = eventSnapshot.child("name").getValue(String.class);
                        String dateStr = eventSnapshot.child("date").getValue(String.class);
                        String startTime = eventSnapshot.child("startTime").getValue(String.class);
                        String endTime = eventSnapshot.child("endTime").getValue(String.class);
                        String location = eventSnapshot.child("location").getValue(String.class);
                        String address = eventSnapshot.child("address").getValue(String.class);
                        String description = eventSnapshot.child("description").getValue(String.class);
                        String paper1_name = eventSnapshot.child("paper1_name").getValue(String.class);
                        String paper1_url = eventSnapshot.child("paper1_url").getValue(String.class);
                        String paper2_name = eventSnapshot.child("paper2_name").getValue(String.class);
                        String paper2_url = eventSnapshot.child("paper2_url").getValue(String.class);
                        String paper3_name = eventSnapshot.child("paper3_name").getValue(String.class);
                        String paper3_url = eventSnapshot.child("paper3_url").getValue(String.class);
                        String paper4_name = eventSnapshot.child("paper4_name").getValue(String.class);
                        String paper4_url = eventSnapshot.child("paper4_url").getValue(String.class);

                        // Initialize LocalDate and LocalTime variables
                        LocalDate date = null;
                        LocalTime start_time = null;
                        LocalTime end_time = null;

                        // Convert dateStr to LocalDate, but handle null or invalid format
                        if (dateStr != null && !dateStr.isEmpty()) {
                            try {
                                date = LocalDate.parse(dateStr);
                            } catch (DateTimeParseException e) {
                                // Handle parsing error, log, or skip the event
                                e.printStackTrace();
                                continue; // Skip this event if the date format is invalid
                            }
                        }

                        // Convert startTime to LocalTime, but handle null or invalid format
                        if (startTime != null && !startTime.isEmpty()) {
                            try {
                                start_time = LocalTime.parse(startTime);
                            } catch (DateTimeParseException e) {
                                // Handle parsing error, log, or skip the event
                                e.printStackTrace();
                                continue; // Skip this event if the start time format is invalid
                            }
                        }

                        // Convert endTime to LocalTime, but handle null or invalid format
                        if (endTime != null && !endTime.isEmpty()) {
                            try {
                                end_time = LocalTime.parse(endTime);
                            } catch (DateTimeParseException e) {
                                // Handle parsing error, log, or skip the event
                                e.printStackTrace();
                                continue; // Skip this event if the end time format is invalid
                            }
                        }

                        // Ensure all required fields are present before creating an Event object
                        if (date != null && start_time != null && end_time != null) {
                            // Create Event object and add to list
                            Event event = new Event(name, date, start_time, end_time, location, address, description,
                                    paper1_name, paper1_url, paper2_name, paper2_url, paper3_name, paper3_url, paper4_name, paper4_url);
                            events.add(event);
                        }
                    }
                    callback.onEventsRetrieved(events); // Pass events to callback
                } else {
                    callback.onEventsRetrieved(new ArrayList<>()); // No data found
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void deleteEvent(String eventName, String eventDate, String startTime, String endTime, String sessionChair) {
        // Reference to the "Events" node
        DatabaseReference eventsRef = dbRef.child("Events");

        // Query to find the event with matching details
        eventsRef.orderByChild("name").equalTo(eventName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean eventDeleted = false;

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve fields from the event to match with the input
                    String dbDate = eventSnapshot.child("date").getValue(String.class);
                    String dbStartTime = eventSnapshot.child("startTime").getValue(String.class);
                    String dbEndTime = eventSnapshot.child("endTime").getValue(String.class);
                    String dbChair = eventSnapshot.child("description").getValue(String.class);  // "description" as session chair

                    // Match all fields (name, date, startTime, endTime, chair)
                    if (dbDate.equals(eventDate) && dbStartTime.equals(startTime) && dbEndTime.equals(endTime) && dbChair.equals(sessionChair)) {
                        // Delete the matching event
                        eventSnapshot.getRef().removeValue();
                        eventDeleted = true;
                        System.out.println("Event deleted: " + eventSnapshot.getKey());
                        break;  // Exit loop after deleting the first matching event
                    }
                }

                if (!eventDeleted) {
                    System.out.println("No matching event found with the provided details.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Failed to delete event: " + databaseError.getMessage());
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
                List<FirstHelperClass> conversationsList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    String lastDateHeader = null;

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        boolean isHeader = childSnapshot.child("isHeader").getValue(Boolean.class) != null
                                && childSnapshot.child("isHeader").getValue(Boolean.class);

                        if (isHeader) {
                            String dateHeader = childSnapshot.child("dateHeader").getValue(String.class);
                            if (!dateHeader.equals(lastDateHeader)) {
                                // Only add header if it's different from the last one
                                FirstHelperClass header = new FirstHelperClass(null, dateHeader, true);
                                conversationsList.add(header);
                                lastDateHeader = dateHeader;
                            }
                        } else {
                            String id = childSnapshot.child("id").getValue(String.class);
                            String name = childSnapshot.child("name").getValue(String.class);
                            String conversationText = childSnapshot.child("conversation_text").getValue(String.class);
                            String dateTime = childSnapshot.child("dateTime").getValue(String.class);
                            String email = childSnapshot.child("email").getValue(String.class); // Extract email

                            FirstHelperClass conversation = new FirstHelperClass(id, name, conversationText, dateTime, email, false);
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


    private void uploadImageToFirebase(Uri imageUri, String personEmail, String personName) {
        // Reference to Firebase storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference for the image file using the email as the identifier
        StorageReference imageRef = storageRef.child("profile_images/" + personEmail + ".jpg");

        // Upload image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Retrieve the image URL after upload
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        saveUserDataWithImageUrl(personEmail, personName, imageUrl);
//                        // Now save the data including the image URL in Realtime Database
//                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
//
//                        // Push a new user entry under "Users"
//                        DatabaseReference userRef = dbRef.push(); // Generate a new key for this user
//
//                        // Save the user data under the unique key
//                        userRef.child("personEmail").setValue(personEmail);
//                        userRef.child("personName").setValue(personName);
////                        userRef.child("category").setValue(category); // e.g., "Student" or "Presenter"
//                        userRef.child("url").setValue(imageUrl); // The image URL saved to Firebase Storage
                    }).addOnFailureListener(e -> {
                        // Handle the failure of getting the image URL
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the failure of image upload
                });
    }

    public void saveUserDataWithImageUrl(String personEmail, String personName, String imageUrl) {
        String sanitizedEmail = personEmail.replace(".", ",");
        // Reference to the Firebase Realtime Database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(sanitizedEmail);

        // Push a new user entry under "Users"
//        DatabaseReference userRef = dbRef.push(); // Generate a new key for this user

        // Save the user data under the unique key
        dbRef.child("personEmail").setValue(personEmail);
        dbRef.child("personName").setValue(personName);
//        userRef.child("category").setValue(category); // e.g., "Student" or "Presenter"
        dbRef.child("url").setValue(imageUrl); // The image URL already available

        // Optionally notify user of success
//        Toast.makeText(getApplicationContext(), "User data and image URL saved successfully!", Toast.LENGTH_SHORT).show();
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

package com.example.capstoneproject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class ChatPageHelperClass {
    String id; // Unique identifier for each chat message or header
    String name; // Name of the user sending the message
    String conversation_text; // Text of the conversation/message
    String localDateTime; // Timestamp of when the message was sent
    String email; // Email of the user sending the message
    boolean isHeader; // Flag to indicate if this is a header or a message

    // Constructor for messages with user details
    public ChatPageHelperClass(String id, String name, String conversation_text, String localDateTime, String email, boolean isHeader) {
        this.id = id; // Set the unique identifier
        this.name = name; // Set the user's name
        this.conversation_text = conversation_text; // Set the conversation text
        this.localDateTime = localDateTime; // Set the timestamp
        this.email = email; // Set the user's email
        this.isHeader = isHeader; // Set the header flag
    }

    // Constructor for headers (no user details)
    public ChatPageHelperClass(String id, String localDateTime, boolean isHeader) {
        this(id, null, null, localDateTime, null, isHeader); // Call the main constructor with null values for user details
    }

    // Getters and setters for the member variables
    public String getId() {
        return id; // Return the unique identifier
    }

    public String getName() {
        return name; // Return the user's name
    }

    public String getConversation_text() {
        return conversation_text; // Return the conversation text
    }

    public String getLocalDateTime() {
        return localDateTime; // Return the timestamp
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime; // Set the timestamp
    }

    public boolean isHeader() {
        return isHeader; // Return whether this is a header
    }

    public String getEmail() {
        return email; // Return the user's email
    }

    public void setEmail(String email) {
        this.email = email; // Set the user's email
    }

    // Method to format and return the date from the localDateTime string
    public String getFormattedDate() {
        if (localDateTime == null || localDateTime.isEmpty()) {
            return ""; // Return an empty string or a placeholder if localDateTime is missing
        }

        try {
            // Parse the date from the localDateTime string
            LocalDate date = LocalDate.parse(localDateTime.split(" ")[0]);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH); // Define the date format
            return date.format(dateFormatter); // Format and return the date as a string
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date"; // Return a default value in case of parsing error
        }
    }

    // Method to format and return the time from the localDateTime string
    public String getFormattedTime() {
        if (localDateTime == null || localDateTime.isEmpty()) {
            return ""; // Return an empty string or a placeholder if localDateTime is missing
        }

        try {
            // Parse the time from the localDateTime string
            LocalTime time = LocalTime.parse(localDateTime.split(" ")[1]);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH); // Define the time format
            return time.format(timeFormatter); // Format and return the time as a string
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Time"; // Return a default value in case of parsing error
        }
    }

    // Override equals method to compare two ChatPageHelperClass objects
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check if both references are the same
        if (obj == null || getClass() != obj.getClass()) return false; // Check if the object is null or of a different class

        ChatPageHelperClass that = (ChatPageHelperClass) obj; // Cast the object to ChatPageHelperClass

        // Compare member variables for equality
        if (isHeader != that.isHeader) return false; // Compare header flags
        if (!Objects.equals(name, that.name)) return false; // Compare names
        if (!Objects.equals(conversation_text, that.conversation_text)) return false; // Compare conversation texts
        if (!Objects.equals(localDateTime, that.localDateTime)) return false; // Compare timestamps
        return Objects.equals(email, that.email); // Compare emails
    }

    // Override hashCode method for proper hashing in collections
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0; // Generate hash code for name
        result = 31 * result + (conversation_text != null ? conversation_text.hashCode() : 0); // Include conversation text
        result = 31 * result + (localDateTime != null ? localDateTime.hashCode() : 0); // Include timestamp
        result = 31 * result + (email != null ? email.hashCode() : 0); // Include email
        result = 31 * result + (isHeader ? 1 : 0); // Include header flag
        return result; // Return the computed hash code
    }
}

package com.example.capstoneproject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class FirstHelperClass {
    String id, name, conversation_text, localDateTime, email;
    boolean isHeader;

    // Constructor for messages
    public FirstHelperClass(String id, String name, String conversation_text, String localDateTime, String email, boolean isHeader) {
        this.id = id;
        this.name = name;
        this.conversation_text = conversation_text;
        this.localDateTime = localDateTime;
        this.email = email;
        this.isHeader = isHeader;
    }

    // Constructor for headers
    public FirstHelperClass(String id, String localDateTime, boolean isHeader) {
        this(id, null, null, localDateTime, null, isHeader);
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getConversation_text() {
        return conversation_text;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormattedDate() {
        if (localDateTime == null || localDateTime.isEmpty()) {
            return ""; // Return an empty string or a placeholder if localDateTime is missing
        }

        try {
            LocalDate date = LocalDate.parse(localDateTime.split(" ")[0]);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
            return date.format(dateFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date"; // Return a default value in case of parsing error
        }
    }

    public String getFormattedTime() {
        if (localDateTime == null || localDateTime.isEmpty()) {
            return ""; // Return an empty string or a placeholder if localDateTime is missing
        }

        try {
            LocalTime time = LocalTime.parse(localDateTime.split(" ")[1]);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
            return time.format(timeFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Time"; // Return a default value in case of parsing error
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        FirstHelperClass that = (FirstHelperClass) obj;

        if (isHeader != that.isHeader) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(conversation_text, that.conversation_text)) return false;
        if (!Objects.equals(localDateTime, that.localDateTime)) return false;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (conversation_text != null ? conversation_text.hashCode() : 0);
        result = 31 * result + (localDateTime != null ? localDateTime.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (isHeader ? 1 : 0);
        return result;
    }
}

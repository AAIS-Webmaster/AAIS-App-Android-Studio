package com.example.capstoneproject;

public class UserProfile {

    // Singleton instance of UserProfile
    private static UserProfile instance;

    // LinkedIn User ID (sub claim in the token)
    public String sub;

    // Full name of the user
    public String name;

    // URL for the LinkedIn profile picture
    public String picture;

    // User email address
    public String email;

    // Token for authenticated requests
    private String token;

    // Private constructor to enforce Singleton pattern
    private UserProfile() { }

    // Get the singleton instance of UserProfile
    public static UserProfile getInstance() {
        if (instance == null) {
            instance = new UserProfile();
        }
        return instance;
    }

    /**
     * Set the user profile with the provided details.
     *
     * @param name - The full name of the user
     * @param email - The user's email address
     * @param picture - URL to the user's profile picture
     * @param token - The token for authenticated requests
     */
    public void setUserProfile(String name, String email, String picture, String token) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.token = token;
    }

    // Getter method for the user's full name
    public String getName() { return name; }

    // Getter method for the user's email
    public String getEmail() { return email; }

    // Getter method for the user's profile picture URL
    public String getPicture() { return picture; }

    // Getter method for the authentication token
    public String getToken() { return token; }

    /**
     * Clear the user profile information. Resets all fields to null.
     */
    public void clearUserProfile() {
        this.name = null;
        this.email = null;
        this.picture = null;
        this.token = null;
    }
}

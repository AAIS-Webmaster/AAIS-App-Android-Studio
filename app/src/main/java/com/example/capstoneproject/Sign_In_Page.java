package com.example.capstoneproject;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sign_In_Page extends AppCompatActivity {
    GoogleSignInOptions gso; // Options for configuring Google Sign-In
    GoogleSignInClient gsc; // Client for Google Sign-In
    Button googleBtn; // Button to initiate Google Sign-In
    Button linkedinLoginButton; // Button to initiate LinkedIn Sign-In
    CardView google_card,linkedin_card;
    private String linkedinAuthURLFull;
    private Dialog linkedInDialog;
    private String linkedinCode;
    private boolean isTokenRequestInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page); // Set the content view to the sign-in layout

        // Hide the action bar for a cleaner look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        google_card = findViewById(R.id.google_card);
        linkedin_card = findViewById(R.id.linkedin_card);

        // Initialize the Google Sign-In button
        googleBtn = findViewById(R.id.google_id);

        // Configure Google Sign-In options to request the user's email
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // Request email address
                .build(); // Build the options

        // Get the GoogleSignInClient with the specified options
        gsc = GoogleSignIn.getClient(this, gso);

        // Configure the LinkedIn Authorization URL
        String state = "linkedin" + System.currentTimeMillis();
        linkedinAuthURLFull = LinkedInConstraints.AUTH_URL + "?response_type=code&client_id=" + LinkedInConstraints.CLIENT_ID +
                "&scope=" + LinkedInConstraints.SCOPE + "&state=" + state + "&redirect_uri=" + LinkedInConstraints.REDIRECT_URI;

        // Initialize the LinkedIn Sign-In button
        linkedinLoginButton = findViewById(R.id.linkedIn_id);

        // Set an OnClickListener for the LinkedIn Sign-In button
        linkedinLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLinkedInWebViewDialog(linkedinAuthURLFull);
            }
        });

        // Set an OnClickListener for the LinkedIn Logo
        linkedin_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLinkedInWebViewDialog(linkedinAuthURLFull);
            }
        });

        // Set an OnClickListener for the Google Sign-In button
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(); // Call the signIn method when the button is clicked
            }
        });

        // Set an OnClickListener for the Google Logo
        google_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(); // Call the signIn method when the button is clicked
            }
        });
    }

    // Method to start the Google Sign-In intent
    void signIn() {
        Intent signInIntent = gsc.getSignInIntent(); // Get the sign-in intent
        startActivityForResult(signInIntent, 1000); // Start the sign-in activity with a request code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Call the superclass method

        // Check if the result is from the sign-in request
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data); // Get the sign-in account from the intent

            try {
                // Attempt to get the result of the sign-in
                task.getResult(ApiException.class);
                navigateToSecondActivity(); // If successful, navigate to the next activity
            } catch (ApiException e) {
                // Show a toast message if sign-in failed
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to navigate to the Home_Page activity
    void navigateToSecondActivity() {
        finish(); // Finish the current activity
        Intent intent = new Intent(Sign_In_Page.this, Home_Page.class); // Create an intent for the Home_Page
        startActivity(intent); // Start the Home_Page activity
    }

    private void setupLinkedInWebViewDialog(String url) {
        // Initialize a dialog to display LinkedIn login page
        linkedInDialog = new Dialog(this);

        // Create a WebView to load LinkedIn OAuth login page
        WebView webView = new WebView(this);
        webView.setVerticalScrollBarEnabled(false); // Disable vertical scroll bar
        webView.setHorizontalScrollBarEnabled(false); // Disable horizontal scroll bar

        // Set a custom WebViewClient to handle LinkedIn OAuth callback
        webView.setWebViewClient(new Sign_In_Page.LinkedInWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript for proper page rendering

        // Load the LinkedIn login URL
        webView.loadUrl(url);

        // Set the WebView as the content of the dialog and display it
        linkedInDialog.setContentView(webView);
        linkedInDialog.show();
    }

    private class LinkedInWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // Check if the URL is the LinkedIn redirect URI
            if (request.getUrl().toString().startsWith(LinkedInConstraints.REDIRECT_URI)) {
                // Handle the OAuth callback URL
                handleUrl(request.getUrl().toString());

                // Dismiss the dialog if the URL contains the authorization code
                if (request.getUrl().toString().contains("?code=")) {
                    linkedInDialog.dismiss();
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Check if the URL is the LinkedIn redirect URI (for backward compatibility)
            if (url.startsWith(LinkedInConstraints.REDIRECT_URI)) {
                handleUrl(url);
                if (url.contains("?code=")) {
                    linkedInDialog.dismiss(); // Dismiss dialog on successful authorization
                }
                return true;
            }
            return false;
        }

        // Handle the OAuth callback URL and extract authorization code or error
        private void handleUrl(String url) {
            Uri uri = Uri.parse(url);

            // If the URL contains the authorization code, store it
            if (url.contains("code")) {
                linkedinCode = uri.getQueryParameter("code");
                linkedInRequestForAccessToken(); // Request access token using the code
            } else if (url.contains("error")) {
                String error = uri.getQueryParameter("error");
                Log.e("Error: ", error); // Log the error if authorization fails
            }
        }
    }

    private void linkedInRequestForAccessToken() {
        // Prevent multiple token requests from being initiated
        if (isTokenRequestInProgress) return;
        isTokenRequestInProgress = true;

        // Create a background thread to request the access token
        new Thread(() -> {
            String grantType = "authorization_code";
            // Create the POST parameters required for the access token request
            String postParams = "grant_type=" + grantType + "&code=" + linkedinCode +
                    "&redirect_uri=" + LinkedInConstraints.REDIRECT_URI +
                    "&client_id=" + LinkedInConstraints.CLIENT_ID +
                    "&client_secret=" + LinkedInConstraints.CLIENT_SECRET;

            OkHttpClient client = new OkHttpClient();

            // Create the request body for the token request
            RequestBody body = RequestBody.create(postParams, MediaType.parse("application/x-www-form-urlencoded"));

            // Build the token request
            Request request = new Request.Builder()
                    .url(LinkedInConstraints.TOKEN_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                // Parse the JSON response and extract the access token
                String jsonResponse = response.body().string();
                Sign_In_Page.LinkedInTokenResponse tokenResponse = new Gson().fromJson(jsonResponse, Sign_In_Page.LinkedInTokenResponse.class);
                String accessToken = tokenResponse.access_token;
                Log.d("Access Token: ", accessToken);

                // Fetch the LinkedIn user's profile using the access token
                fetchLinkedInUserProfile(accessToken);
            } catch (IOException e) {
                e.printStackTrace(); // Handle IO exceptions
            } finally {
                isTokenRequestInProgress = false; // Reset the token request flag
            }
        }).start(); // Start the token request thread
    }

    private void fetchLinkedInUserProfile(String token) {
        // LinkedIn API endpoint to retrieve user profile information
        String userInfoURL = "https://api.linkedin.com/v2/userinfo";

        // Build the request with the access token in the header
        Request request = new Request.Builder()
                .url(userInfoURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        OkHttpClient client = new OkHttpClient();

        // Make the API call to retrieve user information
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the JSON response to extract user profile data
                    String res = response.body().string();
                    Log.d("User Info Response", res);  // Log the response for debugging

                    // Convert the JSON response into a UserProfile object
                    UserProfile profileModel = new Gson().fromJson(res, UserProfile.class);

                    // Check if the profile model is not null and log the user data
                    if (profileModel != null) {
                        Log.d("LinkedIn User ID: ", profileModel.sub != null ? profileModel.sub : "N/A");
                        Log.d("LinkedIn Full Name: ", profileModel.name != null ? profileModel.name : "N/A");
                        Log.d("LinkedIn Profile Picture URL: ", profileModel.getPicture() != null ? profileModel.getPicture() : "N/A");
                        Log.d("LinkedIn Email: ", profileModel.email != null ? profileModel.email : "N/A");

                        // Set the retrieved user data in the UserProfile singleton
                        UserProfile.getInstance().setUserProfile(profileModel.name, profileModel.email, profileModel.picture, token);

                        // Navigate to the Home_Page activity after successful login
                        startActivity(new Intent(Sign_In_Page.this, Home_Page.class));
                    } else {
                        Log.e("Profile Model", "Profile model is null");
                    }
                } else {
                    // Log the error response if fetching the profile fails
                    String errorBody = response.body().string();
                    Log.e("Profile Error", "Failed to fetch profile: " + response.code() + " " + response.message() + ", Body: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network failure and log the error
                e.printStackTrace();
                Log.e("Profile Error", "Failed to fetch profile: " + e.getMessage());
            }
        });
    }

    // Class to model the LinkedIn token response
    public static class LinkedInTokenResponse {
        public String access_token; // Access token field
    }
}
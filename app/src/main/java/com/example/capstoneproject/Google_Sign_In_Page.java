package com.example.capstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class Google_Sign_In_Page extends AppCompatActivity {
    GoogleSignInOptions gso; // Options for configuring Google Sign-In
    GoogleSignInClient gsc; // Client for Google Sign-In
    Button googleBtn; // Button to initiate Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_sign_in_page); // Set the content view to the sign-in layout

        // Hide the action bar for a cleaner look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize the Google Sign-In button
        googleBtn = findViewById(R.id.google_id);

        // Configure Google Sign-In options to request the user's email
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // Request email address
                .build(); // Build the options

        // Get the GoogleSignInClient with the specified options
        gsc = GoogleSignIn.getClient(this, gso);

        // Set an OnClickListener for the Google Sign-In button
        googleBtn.setOnClickListener(new View.OnClickListener() {
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
        Intent intent = new Intent(Google_Sign_In_Page.this, Home_Page.class); // Create an intent for the Home_Page
        startActivity(intent); // Start the Home_Page activity
    }
}
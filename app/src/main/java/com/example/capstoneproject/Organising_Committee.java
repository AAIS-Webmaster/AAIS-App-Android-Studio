package com.example.capstoneproject;

import static com.example.capstoneproject.Home.END_SCALE;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Organising_Committee extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String personName, personEmail;
    ImageView[] imageViews;
    Button unseen;
    Map<ImageView, String> imageUrlMap;
    Map<ImageView, String> linkMap;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon, notification;
    LinearLayout contentView;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organising_committee);
        dbHelper = new MyDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct != null){
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        notification = findViewById(R.id.notification);
        contentView = findViewById(R.id.content);
        unseen = findViewById(R.id.unseen);

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

        // Initialize ImageViews
        imageViews = new ImageView[]{
                findViewById(R.id.image1),
                findViewById(R.id.image2),
                findViewById(R.id.image3),
                findViewById(R.id.image4),
                findViewById(R.id.image5),
                findViewById(R.id.image6),
                findViewById(R.id.image7),
                findViewById(R.id.image8),
                findViewById(R.id.image9),
                findViewById(R.id.image10),
                findViewById(R.id.image11),
                findViewById(R.id.image12),
                findViewById(R.id.image13),
                findViewById(R.id.image14),
                findViewById(R.id.image15),
                findViewById(R.id.image16),
                findViewById(R.id.image17),
                findViewById(R.id.image18),
                findViewById(R.id.image19),
                findViewById(R.id.image20),
                findViewById(R.id.image21),
                findViewById(R.id.image22),
                findViewById(R.id.image23),
                findViewById(R.id.image24),
                findViewById(R.id.image25),
                findViewById(R.id.image26),
                findViewById(R.id.image27)
        };

        imageUrlMap = new HashMap<>();
        linkMap = new HashMap<>();

        new FetchImagesTask().execute("https://acis.aaisnet.org/acis2024/committee/");
        navigationDrawer();

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Organising_Committee.this, Announcement_Page.class));
            }
        });

        unseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Organising_Committee.this, Announcement_Page.class));
            }
        });
    }

    private class FetchImagesTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            try {
                Document doc = Jsoup.connect(url).get();

                // Select the specific <div> you are interested in
                Element targetDiv = doc.select("div.entry-content").first(); // Adjust the selector to match your target <div>

                if (targetDiv != null) {
                    // Extract <img> src attributes within the target <div>
                    Elements imgElements = targetDiv.select("img[src]");
                    int i = 0; // Counter for ImageViews
                    for (Element img : imgElements) {
                        if (i >= imageViews.length) break; // Prevent overflow if more images than ImageViews

                        String imgUrl = img.attr("src");
                        System.out.println("Image src: " + imgUrl);
                        // Store imgUrl and corresponding ImageView
                        imageUrlMap.put(imageViews[i], imgUrl);

                        i++;
                    }

                    // Extract <a> href attributes within the target <div>
                    Elements linkElements = targetDiv.select("a[href]");
                    i = 0; // Reset counter for links
                    for (Element link : linkElements) {
                        if (i >= imageViews.length) break; // Prevent overflow if more links than ImageViews

                        String linkHref = link.attr("href");
                        System.out.println("Link href: " + linkHref);
                        // Store linkHref and corresponding ImageView
                        linkMap.put(imageViews[i], linkHref);

                        i++;
                    }
                } else {
                    System.out.println("Target div not found.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Set images and click listeners
            for (Map.Entry<ImageView, String> entry : imageUrlMap.entrySet()) {
                ImageView imageView = entry.getKey();
                String imgUrl = entry.getValue();
                Picasso.get().load(imgUrl).into(imageView);

                // Set OnClickListener
                String linkHref = linkMap.get(imageView);
                if (linkHref != null) {
                    imageView.setOnClickListener(v -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkHref));
                            startActivity(intent);
                        } catch (Exception e){
                            Toast.makeText(Organising_Committee.this, "Unable to Open the URL", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
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
        navigationView.setCheckedItem(R.id.nav_org);

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
            startActivity(new Intent(Organising_Committee.this, Home.class));
        }
        if(item.toString().equals("Sessions")){
            startActivity(new Intent(Organising_Committee.this, HomePage.class));
        }
        if(item.toString().equals("QR Check-In")){
            startActivity(new Intent(Organising_Committee.this, QR_check_in.class));
        }
        if(item.toString().equals("Site Map")){
            startActivity(new Intent(Organising_Committee.this, Site_Map_Page.class));
        }
        if(item.toString().equals("Chat")){
            startActivity(new Intent(Organising_Committee.this, Group_Chat_Page.class));
        }
        if(item.toString().equals("About")){
            startActivity(new Intent(Organising_Committee.this, About_Page.class));
        }
        if(item.toString().equals("Sign Out")){
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(Organising_Committee.this, Google_Sign_In_Page.class));
                }
            });
        }
        return true;
    }
}

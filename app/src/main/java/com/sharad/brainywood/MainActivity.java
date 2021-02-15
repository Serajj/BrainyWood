package com.sharad.brainywood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sharad.brainywood.Utils.FirebaseEmailVerify;
import com.sharad.brainywood.Utils.FirebaseUserModel;
import com.sharad.brainywood.Utils.SessionManager;
import com.sharad.brainywood.Utils.WelcomeAdapter;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    androidx.appcompat.widget.Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;


    String name, user_id, email;
    SessionManager sessionManager;


    LinearLayout homeLayout, courseLayout, liveLayout, qaLayout, downloadLayout;
    TextView homeText, courseText, liveText, qaText, downloadText;

    ViewPager viewPager;


    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference referenceEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        referenceEmail = rootNode.getReference("verify");


        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();


        setUpToolbar();
        setUpNavigation();
        setUpBottomButtons();


        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        WelcomeAdapter welcomeAdapter = new WelcomeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(welcomeAdapter);


        HashMap<String, String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);
        email = user.get(sessionManager.EMAIL);
        user_id = user.get(sessionManager.ID);


        // checking firebase unique device logic
        CheckDeviceLogin();

   //     CheckEmailVerification();

    }

    private void CheckEmailVerification() {

        Query checkUser = referenceEmail.orderByChild("user_id").equalTo(user_id);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String user_verify = dataSnapshot.child(user_id).child("user_verify").getValue(String.class);


                if (user_verify == null || user_verify.equals("null") ){

                    //setting firebase user values //
                    FirebaseEmailVerify emailVerify = new FirebaseEmailVerify(user_id, "none");
                    referenceEmail.child(user_id).setValue(emailVerify);
                    Toast.makeText(MainActivity.this, "None added", Toast.LENGTH_SHORT).show();

                    //----------//
                }
                else if (user_verify.equals("none")){
                    Toast.makeText(MainActivity.this, "Not verified", Toast.LENGTH_SHORT).show();
                }
                else if (user_verify.equals("verified")){
                    Toast.makeText(MainActivity.this, "Verified", Toast.LENGTH_SHORT).show();

                }

                // Toast.makeText(MainActivity.this, username+"\n"+device_id, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void CheckDeviceLogin() {

        Query checkUser = reference.orderByChild("user_id").equalTo(user_id);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String device_id = dataSnapshot.child(user_id).child("user_device_id").getValue(String.class);
                String username = dataSnapshot.child(user_id).child("user_name").getValue(String.class);
                @SuppressLint("HardwareIds")
                String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                if (device_id.equals(android_id)) {
                    getLatestVersion();
                } else {

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                            .setTitle("")
                            .setMessage("This account is logged in into another device, please login again to use brainywood app, thank you.")
                            .setCancelable(false)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //  Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                    //  homeIntent.addCategory(Intent.CATEGORY_HOME);
                                    //  homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    //  startActivity(homeIntent);
                                    sessionManager.logout();
                                    //   finish();
                                    //  startActivity(new Intent(MainActivity.this, MainActivity.class));
                                    //   System.exit(0);
                                }
                            });
                    dialog.show();

                   // Toast.makeText(MainActivity.this, "Not Same", Toast.LENGTH_SHORT).show();
                }

                // Toast.makeText(MainActivity.this, username+"\n"+device_id, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getLatestVersion() {
        new GetLatestVersion().execute();
    }


    private void setUpBottomButtons() {
        homeLayout = findViewById(R.id.homeLayout);
        courseLayout = findViewById(R.id.courseLayout);
        liveLayout = findViewById(R.id.liveLayout);
        qaLayout = findViewById(R.id.qaLayout);
        downloadLayout = findViewById(R.id.downloadLayout);

        homeText = findViewById(R.id.homeText);
        courseText = findViewById(R.id.courseText);
        liveText = findViewById(R.id.liveText);
        qaText = findViewById(R.id.qaText);
        downloadText = findViewById(R.id.downloadText);
    }


    private void setUpToolbar() {

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private void setUpNavigation() {

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_profile:
                        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                        // i.putExtra("studentInfo", studentList);
                        startActivity(i);
                        break;

                    case R.id.nav_quiz:
                        Intent ii = new Intent(MainActivity.this, QuizListActivity.class);
                        // i.putExtra("studentInfo", studentList);
                        startActivity(ii);
                        break;

                    case R.id.nav_leader:
                        Toast.makeText(MainActivity.this, "LeaderShip Board", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_rate:
                        Rating();
                        break;

                    case R.id.nav_share:

                        ShareApp();
                        break;

                    case R.id.nav_contact:

                        Uri uriUrl = Uri.parse("https://brainywoodindia.com/contacts/");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                        break;


                    case R.id.nav_about:
                        Intent in = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(in);
                        break;

                    case R.id.nav_logout:
                        sessionManager.logout();
                        break;


                    case R.id.nav_exit:

                        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                .setTitle("")
                                .setMessage("Do you want to close the app?")
                                .setCancelable(false)
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setNeutralButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(homeIntent);
                                        finish();
                                        System.exit(0);
                                    }
                                });
                        dialog.show();


                        break;

                    case R.id.nav_terms:

                        Uri uriUrl1 = Uri.parse("https://www.brainywoodindia.com/terms-conditions");
                        Intent launchBrowser1 = new Intent(Intent.ACTION_VIEW, uriUrl1);
                        startActivity(launchBrowser1);
                        break;

                    case R.id.nav_privacy:

                        Uri uriUrl2 = Uri.parse("https://www.brainywoodindia.com/privacy-policy-2");
                        Intent launchBrowser2 = new Intent(Intent.ACTION_VIEW, uriUrl2);
                        startActivity(launchBrowser2);
                        break;

                    case R.id.nav_refund:

                        Uri uriUrl3 = Uri.parse("https://www.brainywoodindia.com/refund-policy");
                        Intent launchBrowser3 = new Intent(Intent.ACTION_VIEW, uriUrl3);
                        startActivity(launchBrowser3);
                        break;
                }

                return false;
            }
        });

    }

    private void Rating() {

        final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {

            Uri uriUrl = Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
    }

    public void HomeOpen(View view) {

        viewPager.setCurrentItem(0);


        courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        homeLayout.setBackgroundColor(Color.parseColor("#80BAFA"));
        liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        courseText.setTextColor(Color.parseColor("#232323"));
        homeText.setTextColor(Color.parseColor("#ffffff"));
        liveText.setTextColor(Color.parseColor("#232323"));
        qaText.setTextColor(Color.parseColor("#232323"));
        downloadText.setTextColor(Color.parseColor("#232323"));


    }

    public void CourseOpen(View view) {

        viewPager.setCurrentItem(1);

        courseLayout.setBackgroundColor(Color.parseColor("#80BAFA"));
        homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        courseText.setTextColor(Color.parseColor("#ffffff"));
        homeText.setTextColor(Color.parseColor("#232323"));
        liveText.setTextColor(Color.parseColor("#232323"));
        qaText.setTextColor(Color.parseColor("#232323"));
        downloadText.setTextColor(Color.parseColor("#232323"));

    }

    public void LiveOpen(View view) {
        viewPager.setCurrentItem(2);


        courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        liveLayout.setBackgroundColor(Color.parseColor("#80BAFA"));
        qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        courseText.setTextColor(Color.parseColor("#232323"));
        homeText.setTextColor(Color.parseColor("#232323"));
        liveText.setTextColor(Color.parseColor("#ffffff"));
        qaText.setTextColor(Color.parseColor("#232323"));
        downloadText.setTextColor(Color.parseColor("#232323"));
    }

    public void QAOpen(View view) {

        viewPager.setCurrentItem(3);


        courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        qaLayout.setBackgroundColor(Color.parseColor("#80BAFA"));
        downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        courseText.setTextColor(Color.parseColor("#232323"));
        homeText.setTextColor(Color.parseColor("#232323"));
        liveText.setTextColor(Color.parseColor("#232323"));
        qaText.setTextColor(Color.parseColor("#ffffff"));
        downloadText.setTextColor(Color.parseColor("#232323"));
    }

    public void DownloadOpen(View view) {
        startActivity(new Intent(this, DownloadedVideosActivity.class));

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                .setTitle("")
                .setMessage("Do you want to close the app?")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNeutralButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        finish();
                        System.exit(0);
                    }
                });
        dialog.show();

    }

    public void OpenCourse(View view) {
        startActivity(new Intent(MainActivity.this, CourseActivity.class));
    }

    public void OpenProfile(View view) {

        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        // i.putExtra("studentInfo", studentList);
        startActivity(i);

    }

    public void ShareClick(View view) {


        ShareApp();

    }



    private void ShareApp() {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BrainyWood");
            String shareMessage = "\nInstall BrainyWood App:\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }


    protected class GetLatestVersion extends AsyncTask<String, Void, String > {
        String latestVersion;
        @Override
        protected String doInBackground(String... strings) {
            try {
                latestVersion = Jsoup
                        .connect("https://play.google.com/store/app/details?id="
                                +getPackageName())
                        .timeout(50000)
                        .get()
                        .select("div.hAyfc:nth-child(4)>"+
                                "span:nth-child(2) > div:nth-child(1)"+
                                "> span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return latestVersion;
        }

        @Override
        protected void onPostExecute(String s) {

            String currentVersion = BuildConfig.VERSION_NAME;
            if ( latestVersion != null){
                float lVersion = Float.parseFloat(latestVersion);
                float cVersion = Float.parseFloat(currentVersion);
                if (lVersion > cVersion){
                    updateDialog();
                }
            }

        }
    }

    private void updateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage("Update available, please update from play store for better experience.");
        builder.setCancelable(false);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="+getPackageName())
                        ));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
}
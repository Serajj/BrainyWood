package com.sharad.brainywood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity2 extends AppCompatActivity {


    DrawerLayout drawerLayout;
    androidx.appcompat.widget.Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    private WebView wv1;
    LinearLayout progress;

    LinearLayout homeLayout, courseLayout, liveLayout, qaLayout, downloadLayout;
    TextView homeText, courseText, liveText, qaText, downloadText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);
        setUpBottomButtons();

        setUpToolbar();
        setUpNavigation();


        progress = findViewById(R.id.progressLayout);

        wv1 = (WebView) findViewById(R.id.MainWebView);
        wv1.setWebViewClient(new MyBrowser());

        wv1.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                // Toast.makeText(MainActivity2.this, url+"\n"+userAgent+"\n"+contentDisposition+"\n"+mimetype+"\n"+String.valueOf(contentLength), Toast.LENGTH_LONG).show();
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype); //returns a string of the name of the file THE IMPORTANT PART


                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setTitle(fileName);
                request.setDescription("Download Video Lectures from BrainyWood");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setDestinationInExternalFilesDir(MainActivity2.this, "/BrainyWood/", fileName + "");

                DownloadManager manager = (DownloadManager) MainActivity2.this.getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                Toast.makeText(MainActivity2.this, "Downloading...", Toast.LENGTH_SHORT).show();

            }


        });

        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.loadUrl("https://brainywoodindia.com/app-dashboard/");

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

    public void CourseOpen(View view) {
        progress.setVisibility(View.VISIBLE);

        courseLayout.setBackgroundColor(Color.parseColor("#2c98f0"));
        homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        courseText.setTextColor(Color.parseColor("#ffffff"));
        homeText.setTextColor(Color.parseColor("#232323"));
        liveText.setTextColor(Color.parseColor("#232323"));
        qaText.setTextColor(Color.parseColor("#232323"));
        downloadText.setTextColor(Color.parseColor("#232323"));


        wv1.loadUrl("https://brainywoodindia.com/app-courses/");

    }

    public void HomeOpen(View view) {


        progress.setVisibility(View.VISIBLE);

        courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        homeLayout.setBackgroundColor(Color.parseColor("#2c98f0"));
        liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        courseText.setTextColor(Color.parseColor("#232323"));
        homeText.setTextColor(Color.parseColor("#ffffff"));
        liveText.setTextColor(Color.parseColor("#232323"));
        qaText.setTextColor(Color.parseColor("#232323"));
        downloadText.setTextColor(Color.parseColor("#232323"));


        wv1.loadUrl("https://brainywoodindia.com/app-dashboard/");

    }

    public void LiveOpen(View view) {


        progress.setVisibility(View.VISIBLE);

        courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        liveLayout.setBackgroundColor(Color.parseColor("#2c98f0"));
        qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        courseText.setTextColor(Color.parseColor("#232323"));
        homeText.setTextColor(Color.parseColor("#232323"));
        liveText.setTextColor(Color.parseColor("#ffffff"));
        qaText.setTextColor(Color.parseColor("#232323"));
        downloadText.setTextColor(Color.parseColor("#232323"));


        wv1.loadUrl("https://brainywoodindia.com/wp-content/uploads/2020/08/sample-mp4-file.mp4");

    }

    public void QAOpen(View view) {


        progress.setVisibility(View.VISIBLE);

        courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        qaLayout.setBackgroundColor(Color.parseColor("#2c98f0"));
        downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        courseText.setTextColor(Color.parseColor("#232323"));
        homeText.setTextColor(Color.parseColor("#232323"));
        liveText.setTextColor(Color.parseColor("#232323"));
        qaText.setTextColor(Color.parseColor("#ffffff"));
        downloadText.setTextColor(Color.parseColor("#232323"));


        wv1.loadUrl("https://brainywoodindia.com/app-questions/");

    }

    public void DownloadOpen(View view) {

        /*

        progress.setVisibility(View.VISIBLE);

        courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        downloadLayout.setBackgroundColor(Color.parseColor("#2c98f0"));

        courseText.setTextColor(Color.parseColor("#232323"));
        homeText.setTextColor(Color.parseColor("#232323"));
        liveText.setTextColor(Color.parseColor("#232323"));
        qaText.setTextColor(Color.parseColor("#232323"));
        downloadText.setTextColor(Color.parseColor("#ffffff"));


        wv1.loadUrl("https://vimeo.com/");

        */
        startActivity(new Intent(this, DownloadedVideosActivity.class));

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress.setVisibility(View.GONE);

                    if (!checkPermissionForReadExtertalStorage()) {

                        Dexter.withContext(MainActivity2.this)
                                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                                }).check();
                    }
                }
            }, 2000);


        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
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
                        //   Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                        // i.putExtra("studentInfo", studentList);
                        //  startActivity(i);
                        break;

                    case R.id.nav_contact:

                        Uri uriUrl = Uri.parse("http://google.com/");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                        break;

                    case R.id.nav_logout:

                        //    sessionManager.logout();

                        break;


                    case R.id.nav_exit:

                        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity2.this, R.style.DialogTheme)
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
                                        //  Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                        //  homeIntent.addCategory(Intent.CATEGORY_HOME);
                                        //  homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        //  startActivity(homeIntent);
                                        finish();
                                        //   System.exit(0);
                                    }
                                });
                        dialog.show();


                        break;
                }

                return false;
            }
        });

    }

}
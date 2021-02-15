package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sharad.brainywood.Utils.SessionManager;
import com.sharad.brainywood.Utils.SliderAdapter;

public class SliderActivity extends AppCompatActivity {


    ViewPager viewPager;

    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slider);

        String check = "not";


        try {

            sessionManager = new SessionManager(this);

            Intent intent = getIntent();
            check = intent.getStringExtra("check");


            if (!sessionManager.isLogin()) {

                viewPager = findViewById(R.id.viewPager);
                SliderAdapter adapter = new SliderAdapter(getSupportFragmentManager());
                viewPager.setAdapter(adapter);
            } else {
                startActivity(new Intent(SliderActivity.this, MainActivity.class));
                finish();
            }


        } catch (NullPointerException e) {
            if (!sessionManager.isLogin()) {

                viewPager = findViewById(R.id.viewPager);
                SliderAdapter adapter = new SliderAdapter(getSupportFragmentManager());
                viewPager.setAdapter(adapter);
            } else {
                startActivity(new Intent(SliderActivity.this, MainActivity.class));
                finish();
            }
        }

    }


}
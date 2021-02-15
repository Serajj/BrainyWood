package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sharad.brainywood.Utils.SessionManager;

public class SplashActivity extends AppCompatActivity {


    ProgressBar bar;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splash);
        bar = findViewById(R.id.splash_progress);

        sessionManager = new SessionManager(this);


        final RelativeLayout logo = findViewById(R.id.logoLayout);
        final ImageView background_image = findViewById(R.id.background_image);
        final ImageView effectImage = findViewById(R.id.effectImage);
        logo.setVisibility(View.GONE);


        Animation trans = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left);
        Animation Fadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        effectImage.startAnimation(trans);
        background_image.startAnimation(Fadeout);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                logo.setVisibility(View.VISIBLE);
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                logo.startAnimation(aniFade);

            }
        }, 1000);


        if (!checkPermissionForReadExtertalStorage()) {
            Dexter.withContext(SplashActivity.this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (sessionManager.isLogin()) {
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        startActivity(new Intent(SplashActivity.this, SliderActivity.class));
                                        finish();
                                    }


                                }
                            }, 2000);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {

                            finish();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                    }).check();
        } else {

            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (sessionManager.isLogin()) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, SliderActivity.class));
                        finish();
                    }

                }
            }, 2500);
        }


    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
}
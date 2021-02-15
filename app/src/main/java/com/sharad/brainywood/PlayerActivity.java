package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.sharad.brainywood.offline.EncryptDecryptUtils;
import com.sharad.brainywood.offline.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {
    VideoView myvideo;
    MediaController mediaController;
    String songName;
    RelativeLayout preloaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_player);



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        myvideo = findViewById(R.id.videoView);
        preloaderView=findViewById(R.id.previewLoader);
        mediaController = new MediaController(this);

        Intent intent = getIntent();
        songName = intent.getStringExtra("name");



        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute();
        Log.d("Seraj","called");


    }



    private static File getFile(byte[] byteArray) throws IOException {
        File tempFile = File.createTempFile("seraj", ".mp4");
        tempFile.deleteOnExit();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fos.write(byteArray);

        return tempFile;
    }

    private byte[] decryption() {

        try {
            byte[] fileData = FileUtils.readFile(Environment.getExternalStorageDirectory() + "/android/data/com.sharad.brainywood/files/.nomedia/" + songName + ".mp4");
            byte[] decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(this).getSecretKey(), fileData);
            Log.d("seraj","File Decrypted ");

            return decryptedBytes;
        } catch (Exception e) {
            Log.d("seraj","File Decryption failed.\nException: " + e.getMessage());
        }
        return null;
    }


    public class MyAsyncTasks extends AsyncTask<String, String, Uri> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            Log.d("Seraj","Executing");

        }

        @Override
        protected Uri doInBackground(String... params) {
            Log.d("seraj","hiiii"+songName);
            File tempFile = null;
            // implement API in background and store the response in current variable
            try {
                byte[] fileData = FileUtils.readFile(Environment.getExternalStorageDirectory() + "/android/data/com.sharad.brainywood/files/.nomedia/" + songName + ".mp4");
                byte[] decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(getApplicationContext()).getSecretKey(), fileData);
                Log.d("seraj","File Decrypted ");


                 tempFile = File.createTempFile("seraj", ".mp4");
                tempFile.deleteOnExit();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(tempFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                fos.write(decryptedBytes);
            } catch (Exception e) {
                Log.d("seraj","File Decryption failed.\nException: " + e.getMessage());
            }

            return Uri.fromFile(tempFile);
        }

        @Override
        protected void onPostExecute(Uri s) {
            Log.d("Seraj","post Executing");
            myvideo.setVideoURI(s);
            myvideo.setMediaController(mediaController);
            mediaController.setAnchorView(myvideo);
            myvideo.setDrawingCacheEnabled(true);
            myvideo.setZOrderOnTop(true);
            preloaderView.setVisibility(View.GONE);
            myvideo.setVisibility(View.VISIBLE);
            myvideo.start();

            }

    }
}
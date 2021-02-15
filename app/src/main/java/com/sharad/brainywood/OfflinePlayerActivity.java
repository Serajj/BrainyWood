package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
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
import com.sharad.brainywood.offline.PrefUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class OfflinePlayerActivity extends AppCompatActivity {

    VideoView myvideo;
    MediaController mediaController;
    String songName;
    RelativeLayout preloaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_offline_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        myvideo = findViewById(R.id.videoView);
        preloaderView=findViewById(R.id.previewLoader);
        mediaController = new MediaController(this);

        Intent intent = getIntent();
        songName = intent.getStringExtra("name");



        String videoPath = Environment.getExternalStorageDirectory() + "/android/data/com.sharad.brainywood/files/.nomedia/" + songName + ".mp4";

        //     Toast.makeText(DownloadedVideosActivity.this, ""+videoPath, Toast.LENGTH_LONG).show();
        Uri fileUri = Uri.fromFile(new File("/android/data/com.sharad.brainywood/files/.nomedia/" + songName + ".mp4"));


        Uri path = null;
        try {
            path = Uri.fromFile(getFile(decryption()));
            Log.d("seraj","File Playing ");
            preloaderView.setVisibility(View.GONE);
            myvideo.setVisibility(View.VISIBLE);




        } catch (IOException e) {
            e.printStackTrace();
        }

       // Uri uri = Uri.parse(videoPath);
        myvideo.setVideoURI(path);
        myvideo.setMediaController(mediaController);
        mediaController.setAnchorView(myvideo);
        myvideo.setDrawingCacheEnabled(true);
        myvideo.setZOrderOnTop(true);
        myvideo.start();

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



}
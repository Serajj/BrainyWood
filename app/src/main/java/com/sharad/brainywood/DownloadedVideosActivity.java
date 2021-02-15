package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class DownloadedVideosActivity extends AppCompatActivity {

    SwipeMenuListView listViewSongs;
    ArrayAdapter<String> myAdapter;
    ArrayList<String> items = null;

   // VideoView myvideo;
    MediaController mediaController;

    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_downloaded_videos);




    //    myvideo = findViewById(R.id.videoView);
        mediaController = new MediaController(this);

        if (!checkPermissionForReadExtertalStorage()) {

            Dexter.withContext(DownloadedVideosActivity.this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            display();


                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        }
                    }).check();
        } else {
            display();
        }


    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    void display() {
        listViewSongs = null;
        items = null;
        myAdapter = null;
        listViewSongs = (SwipeMenuListView) findViewById(R.id.song_list_view);


        try {

            File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.sharad.brainywood/files/.nomedia");

            final ArrayList<File> mysongs = findSong(file);

            items =new ArrayList<String>();

            for (int i = 0; i < mysongs.size(); i++) {

               // items[i] = mysongs.get(i).getName().toString().replace(".mp4", "").replace(".avi", "");
                items.add(mysongs.get(i).getName().toString().replace(".mp4", "").replace(".avi", ""));

            }

            myAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_whitetext_, items);

            listViewSongs.setAdapter(myAdapter);

            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {
                    // create "open" item
                    SwipeMenuItem openItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                            0xCE)));
                    // set item width
                    openItem.setWidth(170);
                    // set item title
                    openItem.setTitle("Open");
                    // set item title fontsize
                    openItem.setTitleSize(18);
                    // set item title font color
                    openItem.setTitleColor(Color.WHITE);
                    // add to menu
                    // menu.addMenuItem(openItem);

                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    // set item width
                    deleteItem.setWidth(170);
                    // set a icon
                    deleteItem.setIcon(R.drawable.ic_delete_white);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                }
            };

            listViewSongs.setMenuCreator(creator);

            listViewSongs.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            //-----
                            final String songName = listViewSongs.getItemAtPosition(position).toString();

                            final AlertDialog.Builder dialog = new AlertDialog.Builder(DownloadedVideosActivity.this, R.style.DialogTheme)
                                    .setTitle("")
                                    .setMessage("Delete this video?\n("+songName+")   ")
                                    .setCancelable(false)
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setNeutralButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            File deleteFile = new File(Environment.getExternalStorageDirectory() + "/android/data/com.sharad.brainywood/files/.nomedia/" + songName + ".mp4");
                                            boolean deleted = deleteFile.delete();
                                            if (deleted){
                                                items.remove(position);
                                                myAdapter.notifyDataSetChanged();
                                                Toast.makeText(DownloadedVideosActivity.this, songName+" deleted", Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });
                            dialog.show();

                            //-----

                            break;
                        case 1:
                            Toast.makeText(DownloadedVideosActivity.this, "1 clicked", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    // false : close the menu; true : not close the menu
                    return false;
                }
            });

            listViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                    String songName = listViewSongs.getItemAtPosition(i).toString();
                    Intent intent = new Intent(DownloadedVideosActivity.this, PlayerActivity.class);
                    intent.putExtra("name", songName);
                    startActivity(intent);

                    /*

                    startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                            .putExtra("songs", mysongs)
                            .putExtra("songname", songName)
                            .putExtra("pos", i));

                    */

                    /*

                    String videoPath = Environment.getExternalStorageDirectory() + "/android/data/com.sharad.brainywood/files/BrainyWood/" + songName + ".mp4";

                    //     Toast.makeText(DownloadedVideosActivity.this, ""+videoPath, Toast.LENGTH_LONG).show();
                    Uri fileUri = Uri.fromFile(new File("/android/data/com.sharad.brainywood/files/BrainyWood/" + songName + ".mp4"));
                    Uri path = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/android/data/com.sharad.brainywood/files/BrainyWood/" + songName + ".mp4"));

                    Uri uri = Uri.parse(videoPath);
                    myvideo.setVideoURI(path);
                    myvideo.setMediaController(mediaController);
                    mediaController.setAnchorView(myvideo);
                    myvideo.start();


                     */

                    // Uri u = Uri.parse(mysongs.get(i).toString());


                }
            });

        } catch (Exception e) {
            //Toast.makeText(this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    public ArrayList<File> findSong(File file) {

        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = null;
        files = file.listFiles();

        for (File singleFile : files) {

            if (singleFile.isDirectory() && !singleFile.isHidden()) {

                arrayList.addAll(findSong(singleFile));

            } else {
                if (singleFile.getName().endsWith(".mp4") ||
                        singleFile.getName().endsWith(".avi")) {

                    arrayList.add(singleFile);

                }
            }

        }

        return arrayList;
    }


    public void Finish(View view) {
        finish();
    }

    public void FullScreen(View view) {

        ImageView btFullScreen = findViewById(R.id.bt_fullscreen);

        if (flag) {
            btFullScreen.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_fullscreen));

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            flag = false;
        } else {
            btFullScreen.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_fullscreen_exit));

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            flag = true;
        }

    }
}
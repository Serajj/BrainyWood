package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sharad.brainywood.Models.LessonsModel.LessonsList;
import com.sharad.brainywood.Models.QuizQuestions.QuizQuestionsList;
import com.sharad.brainywood.Utils.GlobalUrlApi;
import com.sharad.brainywood.offline.EncryptDecryptUtils;
import com.sharad.brainywood.offline.FileUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class LessonActivity extends AppCompatActivity {

    LessonsList selectedLeson;
    String QuizTitle;

    String extractedMp4Url;

    TextView lesson_title, course_title, lesson_description;

    ImageView thumbnail;

    LinearLayout videoLayout;
    String name = null, exe = null;

    ArrayList<LessonsList> lessonsList;
    int GetItemPosition;
    String courseTitle;
    String filename;

    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_lesson);

        videoLayout = findViewById(R.id.videoLayout);

        Intent i = getIntent();


        lessonsList = (ArrayList<LessonsList>) i.getSerializableExtra("lesssonsList");
        GetItemPosition = i.getIntExtra("currentLessonPosition", 0);
        selectedLeson = (LessonsList) i.getSerializableExtra("selectedLeson");
        courseTitle = i.getStringExtra("courseTitle");

        CheckQuiz();

        LoadUI(courseTitle);

        if (selectedLeson.getLesson_video_url().equals("") || selectedLeson.getLesson_video_url() == null){
            videoLayout.setVisibility(View.GONE);

        }

        getUrl(selectedLeson.getLesson_video_url()+"/config"); // brainy video
      //  Toast.makeText(this, ""+(selectedLeson.getLesson_video_url()), Toast.LENGTH_LONG).show();

    }

    private void CheckQuiz() {
        if (selectedLeson.getQuiz_id() != null && !selectedLeson.getQuiz_id().equals("null")){

            FetchQuizTitle quizTitle = new FetchQuizTitle();
            quizTitle.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    private class FetchQuizTitle extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                // URL url = new URL(new GlobalUrlApi().getBaseUrl() + "wp-json/app/v1/get_quiz_question/" + (selectedQuiz.getQuiz_id()));
                URL url = new URL(new GlobalUrlApi().getBaseUrl() + "wp-json/ldlms/v1/sfwd-quiz/" + selectedLeson.getQuiz_id());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);

                }

                mainFile = stringBuffer.toString();


                //   JSONObject child = new getJSONObject(0);
                JSONObject child = new JSONObject(mainFile);
                Log.d("QUIZ_RESPONSE", mainFile);

                //getting course id
                String quiz_id = child.getString("id");

                //getting course title
                JSONObject quiz_title = child.getJSONObject("title");
                String quiz_name = quiz_title.getString("rendered");
                QuizTitle = quiz_name.replace("&#8211;", "-");

                //getting course desc
                JSONObject content = child.getJSONObject("content");
                String quiz_desc = content.getString("rendered");

                //S

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            CardView cardQuiz = findViewById(R.id.cardQuiz);
            cardQuiz.setVisibility(View.VISIBLE);
        }
    }


    public void getUrl(String url) {

        StringRequest str = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(CheckActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject request = object.getJSONObject("request");
                    JSONObject files = request.getJSONObject("files");
                    JSONArray progressive = files.getJSONArray("progressive");

                    JSONObject array1 = progressive.getJSONObject(2);

                    extractedMp4Url = array1.getString("url");


                    JSONObject video = object.getJSONObject("video");
                    JSONObject thumbs = video.getJSONObject("thumbs");

                    String thumb = thumbs.getString("640");

                 //   Toast.makeText(LessonActivity.this, ""+thumb, Toast.LENGTH_SHORT).show();
                    Picasso.get()
                            .load(thumb)
                            .into(thumbnail);

                //    Toast.makeText(LessonActivity.this, ""+extractedMp4Url, Toast.LENGTH_SHORT).show();




                    //Setting thumbnail from url
                    /*
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    //give YourVideoUrl below
                    retriever.setDataSource(url1, new HashMap<String, String>());
                    // this gets frame at 2nd second
                    Bitmap image = retriever.getFrameAtTime(1500000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    //use this bitmap image
                    ImageView imageView = view.findViewById(R.id.thumbnail);
                 //   imageView.setImageBitmap(image);

                     */



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LessonActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                    //  result.setText(e.toString());

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(LessonActivity.this);
        requestQueue.add(str);
    }


    private void LoadUI(String courseTitle) {

        lesson_title = findViewById(R.id.lesson_title);
        course_title = findViewById(R.id.course_title);
        lesson_description = findViewById(R.id.lesson_description);
        thumbnail = findViewById(R.id.thumbnail);


        lesson_title.setText(selectedLeson.getLesson_title().replace("&#8211;", "-"));
        course_title.setText(courseTitle.replace("&#8211;", "-"));
        lesson_description.setText(selectedLeson.getLesson_descrption());

    }


    public void Close(View view) {

        finish();
    }

    public void PlayVideo(View view) {

        if (extractedMp4Url != null && !extractedMp4Url.equals("")){
        Intent intent = new Intent(LessonActivity.this, OnlinePlayerActivity.class);
        intent.putExtra("url", extractedMp4Url);
        startActivity(intent);}
        else {
            Toast.makeText(this, "This video is private from vimeo servers...", Toast.LENGTH_LONG).show();
        }
    }

    public void DownloadVideo(View view) {



        try {
            URL url = new URL(extractedMp4Url);
            name = FilenameUtils.getBaseName(url.getPath());
            exe = FilenameUtils.getExtension(url.getPath());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("seraj","Permission is granted");
                    //File write logic here

                }else{
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }


            String appPath = getApplicationContext().getFilesDir().getAbsolutePath()+"/";
           String AppPath2= Environment
                    .getExternalStorageDirectory().toString();

            Log.d("Seraj",appPath);
            Log.d("Seraj",AppPath2);

            try {
                new DownloadFileFromURL().execute(String.valueOf(url));
                Log.d("Seraj","Downloading");
            }catch (Exception e){
                  Log.d("seraj",e.getLocalizedMessage());
            }
            //    Toast.makeText(getActivity(), name + "\n" + exe, Toast.LENGTH_SHORT).show();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(extractedMp4Url));
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
//        request.setTitle((selectedLeson.getLesson_title().replace("&#8211;", "-"))+"."+exe);
//        request.setDescription("Download Video Lectures from BrainyWood");
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//        request.setDestinationInExternalFilesDir(LessonActivity.this, "/.nomedia/", (selectedLeson.getLesson_title().replace("&#8211;", "-"))+"."+exe + "");
//
//        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//        manager.enqueue(request);

        Toast.makeText(LessonActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();

    }

    public void FinishLesson(View view) {
        finish();
    }

    public void NextLesson(View view) {


        try {

           // int tempPosition = Integer.parseInt(GetItemPosition);
          //  tempPosition++;
           // GetItemPosition = String.valueOf(tempPosition);

            GetItemPosition++;

            try {


                LessonsList selectedLeson = lessonsList.get(GetItemPosition);

                Intent i = new Intent(
                        LessonActivity.this, LessonActivity.class);
                i.putExtra("lesssonsList", (Serializable) lessonsList);
                i.putExtra("currentLessonPosition", GetItemPosition);
                i.putExtra("selectedLeson", selectedLeson);
                i.putExtra("courseTitle", courseTitle);
                startActivity(i);
                finish();

            }
            catch (IndexOutOfBoundsException i){
                finish();
                i.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void OpenQuizFromLesson(View view) {

        Intent i = new Intent(LessonActivity.this, QuizActivity.class);
       // i.putExtra("selectedQuiz", selectedQuiz);
        i.putExtra("selectedQuizID", selectedLeson.getQuiz_id());
        i.putExtra("quizTitle", QuizTitle);
        i.putExtra("CourseTitle",courseTitle.replace("&#8211;", "-"));
        startActivity(i);
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                Log.d("Seraj","inside do in back");
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                Log.d("Seraj","outputstream"+lenghtOfFile);

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());

                Log.d("seraj",Environment
                        .getExternalStorageDirectory().toString()+"/Android/data/com.sharad.brainywood/files/.nomedia/"+(selectedLeson.getLesson_title().replace(" ", "-"))+"."+exe + "");
                File directory = new File(Environment
                        .getExternalStorageDirectory().toString()+"/Android/data/com.sharad.brainywood/files/.nomedia");
                if (! directory.exists()){
                    directory.mkdir();
                    Log.d("seraj","Directory created");
                }else{
                    Log.d("seraj","Directory found");
                }

                // Output stream
                Log.d("Seraj","outputstream");
                filename=(selectedLeson.getLesson_title().replace(" ", "-"))+"."+exe + "";
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()+"/Android/data/com.sharad.brainywood/files/.nomedia/"+(selectedLeson.getLesson_title().replace(" ", "-"))+"."+exe + "");



                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file

                    output.write(data, 0, count);

                }

                byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(getApplicationContext(),filename));
                byte[] encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(getApplicationContext()).getSecretKey(), fileData);
                FileUtils.saveFile(encodedBytes, FileUtils.getFilePath(getApplicationContext(),filename));

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

        }

    }

}
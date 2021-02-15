package com.sharad.brainywood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sharad.brainywood.Models.CourseModel.CourseHorizAdapter;
import com.sharad.brainywood.Models.CourseModel.CourseList;
import com.sharad.brainywood.Models.LessonsModel.LessonAdapter;
import com.sharad.brainywood.Models.LessonsModel.LessonsList;
import com.sharad.brainywood.Utils.GlobalUrlApi;
import com.sharad.brainywood.Utils.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CourseSubsActivity extends AppCompatActivity {

    CourseList selectedCourse;

    ArrayList<String> lessons_ids;
    int lessons_ids_loop = 0;

    ArrayList<LessonsList> lessonsList;
    RecyclerView lessonsRecycler;
    ProgressBar lessonsRecyclerProgress;

    View ChildView;
    int GetItemPosition;

    FetchLessonIds fetchLessonIds;
    FetchLessonContents contents;

    TextView course_title, course_description;
    ImageView selected_course_image;
    ProgressBar selected_course_image_progress;

    GlobalUrlApi globalUrlApi;
    SessionManager sessionManager;
    String user_id, email, name;

    boolean exit = false;

    boolean subs = false;

    CardView subscribeCard;

    String role = "";



    ProgressDialog pd;// = new ProgressDialog(yourActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_course_subs);

        pd = new ProgressDialog(CourseSubsActivity.this);
        pd.setMessage("loading");
        pd.setCancelable(false);


        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);
        email = user.get(sessionManager.EMAIL);
        user_id = user.get(sessionManager.ID);

        initializeURLs();


        selected_course_image = findViewById(R.id.selected_course_image);
        selected_course_image_progress = findViewById(R.id.selected_course_image_progress);
        course_title = findViewById(R.id.course_title);
        course_description = findViewById(R.id.course_description);
        lessonsRecyclerProgress = findViewById(R.id.lessonProgress);
        subscribeCard = findViewById(R.id.subscribeCard);


        Intent i = getIntent();
        selectedCourse = (CourseList) i.getSerializableExtra("selectedCourse");
        if (Objects.requireNonNull(i.getStringExtra("courseEnrolled")).equals("yes")){
           // Toast.makeText(getApplicationContext(), "Course Already Subscribed", Toast.LENGTH_SHORT).show();
            subscribeCard.setVisibility(View.GONE);
            TextView currentStatus = findViewById(R.id.currentStatus);
            String status = "Current Status:"+"\n"+"ALREADY ENROLLED";

            currentStatus.setText(status);
        }
        else {

            //Toast.makeText(getApplicationContext(), "Course Not Subscribed", Toast.LENGTH_SHORT).show();

        }
        setupCourseUI();

        fetchLessons();

        GetUserDetails(user_id);


    }

    private void initializeURLs() {
        globalUrlApi = new GlobalUrlApi();
        //  Categories_URL = globalUrlApi.getGlobalVariable() + "categories_json.php";
        // Cat_pref_count_URL = globalUrlApi.getGlobalVariable() + "get_pref_count_cat.php?user_id=" + user_id;


    }

    private void fetchLessons() {

        lessonsRecycler = findViewById(R.id.lessonRecycler);
        lessonsRecycler.setLayoutManager(new LinearLayoutManager(this));
        lessonsList = new ArrayList<LessonsList>();

     //   fetchLessonIds = new FetchLessonIds();
     //   fetchLessonIds.execute();
        lessons_ids = selectedCourse.getLessons_ids();
        if (lessons_ids_loop < lessons_ids.size() && !exit) {

            //  Toast.makeText(CheckActivity.this, "Course fetching started", Toast.LENGTH_SHORT).show();
            fetchLessonContent();

        }

        lessonsRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(CourseSubsActivity.this,
                    new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onSingleTapUp(MotionEvent motionEvent) {
                            return true;
                        }
                    });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                ChildView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                    GetItemPosition = recyclerView.getChildAdapterPosition(ChildView);

                    LessonsList selectedLeson = lessonsList.get(GetItemPosition);

                 //   Intent i = new Intent(CourseSubsActivity.this, LessonActivity.class);
                 //   i.putExtra("selectedLeson", selectedLeson);
                 //   i.putExtra("courseTitle", selectedCourse.getCourse_title());
                 //   startActivity(i);
                    Toast.makeText(CourseSubsActivity.this, "Please enroll yourself to access\nCourse Contents, Thank you", Toast.LENGTH_LONG).show();

                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }
        });

    }

    public void Subscribe(View view) {
        if (selectedCourse.getCourse_price().equals("free")){
            pd.show();

            // Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show();
            EnrollFirst();
        }
        else if (selectedCourse.getCourse_price().equals("closed")){

            if (role.equals("subscriber")){
                EnrollFirst();
            }
            else {

                AlertDialog.Builder dialog = new AlertDialog.Builder(CourseSubsActivity.this, R.style.DialogTheme)
                        .setTitle("Subscribe Now")
                        .setMessage("Only for Membership, please subscribe on our website")
                        .setCancelable(false)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String url = "https://brainywoodindia.com/product/little-birbal/";
                                try {
                                    Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                                    Intent ii = new Intent(Intent.ACTION_VIEW, uri);
                                    ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(ii);
                                } catch (ActivityNotFoundException e) {
                                    // Chrome is probably not installed
                                }

                            }
                        });
                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                dialog.show();

            }

        }
    }

    private void GetUserDetails(final String user_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, new GlobalUrlApi().getBaseUrl() + "wp-json/app/v1/get_user_detail/" + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            //JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (status.equals("true")) {
                                Log.d("STATUS_USER: ", "Successful");
                                // Toast.makeText(ProfileActivity.this, ""+(jsonObject.getJSONObject("data").toString()), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("STATUS_USER: ", "Error");

                            }
                            JSONObject data = jsonObject.getJSONObject("data");

                            role = data.getString("role");




                        } catch (JSONException e) {

                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server not responding", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Server not responding", Toast.LENGTH_SHORT).show();
                        //    login_text.setVisibility(View.VISIBLE);
                        //   login_text.setText("Error from php");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("username", email);
                //   params.put("password", password);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0; //retry turn off
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }



    private void EnrollFirst() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new GlobalUrlApi().getBaseUrl()+"wp-json/ldlms/v1/users/"+user_id+"/courses",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(CourseSubsActivity.this, "First Done", Toast.LENGTH_SHORT).show();
                       // EnrollSecond();


                        // Toast.makeText(CourseSubsActivity.this, "Second Done", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        pd.cancel();
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(CourseSubsActivity.this, R.style.DialogTheme)
                                .setTitle("")
                                .setMessage("Enrolled into this course")
                                .setCancelable(false)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //  Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                        //  homeIntent.addCategory(Intent.CATEGORY_HOME);
                                        //  homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        //  startActivity(homeIntent);
                                        finish();
                                        startActivity(new Intent(CourseSubsActivity.this, MainActivity.class));
                                        //   System.exit(0);
                                    }
                                });
                        dialog.show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(CourseSubsActivity.this, "VolleyError error", Toast.LENGTH_SHORT).show();


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_ids", selectedCourse.getCourse_id());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0; //retry turn off
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void EnrollSecond() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, new GlobalUrlApi().getBaseUrl()+"wp-json/ldlms/v1/sfwd-courses/"+selectedCourse.getCourse_id()+"/users",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       // Toast.makeText(CourseSubsActivity.this, "Second Done", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        pd.cancel();
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(CourseSubsActivity.this, R.style.DialogTheme)
                                .setTitle("")
                                .setMessage("Enrolled into this course")
                                .setCancelable(false)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //  Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                        //  homeIntent.addCategory(Intent.CATEGORY_HOME);
                                        //  homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        //  startActivity(homeIntent);
                                        finish();
                                        startActivity(new Intent(CourseSubsActivity.this, MainActivity.class));
                                        //   System.exit(0);
                                    }
                                });
                        dialog.show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(CourseSubsActivity.this, "VolleyError error", Toast.LENGTH_SHORT).show();


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_ids", user_id);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0; //retry turn off
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }

    private class FetchLessonIds extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(globalUrlApi.getBaseUrl() + "/wp-json/ldlms/v1/sfwd-courses/"+(selectedCourse.getCourse_id())+"/steps?type=t");
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



                JSONObject parent = new JSONObject(mainFile);

              //  JSONObject title = parent.getJSONObject("t");

                JSONArray sfwdlessons = parent.getJSONArray("sfwd-lessons");


                lessons_ids = new ArrayList<String>();

                int i = 0;
             //   course_id = "";
                while (i < sfwdlessons.length()) {


                    lessons_ids.add(sfwdlessons.getString(i));
                  //  lessons_ids.add(String.valueOf(parent.length()));
                    //    course_id = parent.getString(0);
                    //   course_name = parent.getString(0);

                    i++;
                }





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

            String lesson_id = "";
            for (int i = 0; i < lessons_ids.size(); i++) {

                lesson_id = lesson_id + lessons_ids.get(i) + "\n";
              //  course_name = lessons_ids.get(i) + "\n";

            }

           // Toast.makeText(CourseSubsActivity.this, ""+lesson_id, Toast.LENGTH_LONG).show();

            if (lessons_ids_loop < lessons_ids.size() && !exit) {

                //  Toast.makeText(CheckActivity.this, "Course fetching started", Toast.LENGTH_SHORT).show();
                fetchLessonContent();

            }

        }
    }

    private void fetchLessonContent() {

      //  Toast.makeText(this, "" + ("https://brainywoodindia.com/wp-json/ldlms/v1/sfwd-lessons/" + lessons_ids.get(lessons_ids_loop)), Toast.LENGTH_SHORT).show();
        contents = new FetchLessonContents();
        contents.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private class FetchLessonContents extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(("https://brainywoodindia.com/wp-json/ldlms/v1/sfwd-lessons/" + lessons_ids.get(lessons_ids_loop)));
                //  URL url = new URL("https://www.brainywoodindia.com/wp-json/ldlms/v1/sfwd-courses/2601");
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


                //getting course id
                String lesson_id = child.getString("id");

                //getting course title
                JSONObject title = child.getJSONObject("title");
                String lesson_name = title.getString("rendered");

                //getting course desc
                JSONObject content = child.getJSONObject("content");
                String lesson_desc = content.getString("rendered");



                lessonsList.add(new com.sharad.brainywood.Models.LessonsModel.LessonsList(lesson_id, lesson_name, lesson_desc));
                //  }


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


            lessons_ids_loop++;

            if (lessons_ids_loop < lessons_ids.size() && !exit) {

              //  Toast.makeText(CourseSubsActivity.this, "Adapter list size: " + lessonsList.size(), Toast.LENGTH_SHORT).show();

                lessonsRecycler = null;
                lessonsRecycler = findViewById(R.id.lessonRecycler);
                lessonsRecycler.setLayoutManager(new LinearLayoutManager(CourseSubsActivity.this));

                ArrayList<LessonsList> lessonsListTemp;
                lessonsListTemp = lessonsList;
                LessonAdapter lessonAdapter = new LessonAdapter(lessonsListTemp, CourseSubsActivity.this);
                lessonsRecycler.setAdapter(lessonAdapter);
                lessonsRecycler.setVisibility(View.VISIBLE);
                // CourseList selectedCourse = courseList.get(course_subs_loop);
                // Toast.makeText(CheckActivity.this, ""+(selectedCourse.getCourse_title()), Toast.LENGTH_SHORT).show();

                fetchLessonContent();

            } else {

              //  CourseHorizAdapter noticeAdapter = new CourseHorizAdapter(lessonsList, CheckActivity.this);
               // courseRecycler.setAdapter(noticeAdapter);
              //  Toast.makeText(CheckActivity.this, "Else started", Toast.LENGTH_SHORT).show();
                LessonAdapter lessonAdapter = new LessonAdapter(lessonsList, CourseSubsActivity.this);
                lessonsRecycler.setAdapter(lessonAdapter);
                lessonsRecycler.setVisibility(View.VISIBLE);
                lessonsRecyclerProgress.setVisibility(View.GONE);
               // Toast.makeText(CourseSubsActivity.this, "Done Fetching \n"+"Adapter list size: " + lessonsList.size(), Toast.LENGTH_SHORT).show();
            }


        }
    }



    private void setupCourseUI() {


        course_title.setText((selectedCourse.getCourse_title()).replace("&#8211;", "-"));
        course_description.setText(
                (
                        (selectedCourse.getCourse_content()
                        ).replace("<p>", "")
                ).replace("</p>", "")
        );
        //   Toast.makeText(this, ""+ (selectedCourse != null ? selectedCourse.getCourse_image_url() : null), Toast.LENGTH_SHORT).show();

        Picasso.get()
                .load(selectedCourse.getCourse_image_url())
                .into(selected_course_image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (selected_course_image_progress != null) {
                            selected_course_image_progress.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                });

    }


    public void Close(View view) {
        exit = true;
        if (fetchLessonIds != null){
            fetchLessonIds.cancel(true);
        }
        if (contents != null){
            contents.cancel(true);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit = true;
        if (fetchLessonIds != null){
            fetchLessonIds.cancel(true);
        }
        if (contents != null){
            contents.cancel(true);
        }
    }

    public void OpenLecture(View view) {
        startActivity(new Intent(CourseSubsActivity.this, LectureActivity.class));

    }

    public void OpenQuiz(View view) {
        startActivity(new Intent(CourseSubsActivity.this, QuizActivity.class));

    }

}
package com.sharad.brainywood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import com.sharad.brainywood.Models.CourseModel.CourseList;
import com.sharad.brainywood.Models.LessonsModel.LessonAdapter;
import com.sharad.brainywood.Models.LessonsModel.LessonsList;
import com.sharad.brainywood.Models.QuizModel.QuizAdapterVertical;
import com.sharad.brainywood.Models.QuizModel.QuizList;
import com.sharad.brainywood.Utils.GlobalUrlApi;
import com.sharad.brainywood.Utils.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    CourseList selectedCourse;

    TextView course_title, course_description;
    ImageView selected_course_image;
    ProgressBar selected_course_image_progress;


    ArrayList<String> lessons_ids;
    int lessons_ids_loop = 0;

    ArrayList<LessonsList> lessonsList;
    RecyclerView lessonsRecycler;
    ProgressBar lessonsRecyclerProgress;
    View ChildView;
    int GetItemPosition;

    ArrayList<String> quiz_ids;
    int quiz_ids_loop = 0;
    ArrayList<QuizList> quizList;
    RecyclerView quizRecycler;
    ProgressBar quizRecyclerProgress;

    FetchLessonIds fetchLessonIds;
    FetchLessonContents contentsLessons;
    FetchQuizContents contentsQuiz;

    GlobalUrlApi globalUrlApi;
    SessionManager sessionManager;
    String user_id, email, name;

    boolean exitLesson = false;
    boolean exitQuiz = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_course);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);


        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);
        email = user.get(sessionManager.EMAIL);
        user_id = user.get(sessionManager.ID);

        initializeURLs();


        Intent i = getIntent();
        selectedCourse = (CourseList) i.getSerializableExtra("selectedCourse");

        try {
            if (selectedCourse.getQuiz_ids().size() < 1) {

                CardView quizCardLayout = findViewById(R.id.quizCardLayout);
                quizCardLayout.setVisibility(View.GONE);


            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            CardView quizCardLayout = findViewById(R.id.quizCardLayout);
            quizCardLayout.setVisibility(View.GONE);
        }


        setupCourseUI();


        fetchLessons();

        //fetchQuizzes();

    }

    private void fetchQuizzes() {
        quizRecycler = findViewById(R.id.quizRecycler);
        quizRecycler.setLayoutManager(new LinearLayoutManager(this));
        quizList = new ArrayList<QuizList>();

        // fetchLessonIds = new FetchLessonIds();
        // fetchLessonIds.execute();

        quiz_ids = selectedCourse.getQuiz_ids();
        // Log.d("QUIZ_ID_LIST", quiz_ids.get(0));

        if (quiz_ids_loop < quiz_ids.size() && !exitQuiz) {

            //  Toast.makeText(CheckActivity.this, "Course fetching started", Toast.LENGTH_SHORT).show();
            //   fetchLessonContent();
            fetchQuizContent();

        }

        quizRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(CourseActivity.this,
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

                    QuizList selectedQuiz = quizList.get(GetItemPosition);


                    Intent i = new Intent(CourseActivity.this, QuizActivity.class);
                    i.putExtra("selectedQuiz", selectedQuiz);
                    i.putExtra("quizTitle", selectedQuiz.getQuiz_title());
                    i.putExtra("CourseTitle", (selectedCourse.getCourse_title()).replace("&#8211;", "-"));
                    startActivity(i);
                    //   Toast.makeText(CourseActivity.this, selectedLeson.getLesson_title()+"\n\n"+selectedLeson.getLesson_descrption()+"\n\n"+selectedLeson.getLesson_video_url(), Toast.LENGTH_LONG).show();

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


    private void fetchLessons() {
        lessonsRecycler = findViewById(R.id.lessonRecycler);
        lessonsRecycler.setLayoutManager(new LinearLayoutManager(this));
        lessonsList = new ArrayList<LessonsList>();

        // fetchLessonIds = new FetchLessonIds();
        // fetchLessonIds.execute();

        lessons_ids = selectedCourse.getLessons_ids();
       // Toast.makeText(this, selectedCourse.getLessonQuizIdlist().size() + "", Toast.LENGTH_SHORT).show();

        if (lessons_ids_loop < lessons_ids.size() && !exitLesson) {

            //  Toast.makeText(CheckActivity.this, "Course fetching started", Toast.LENGTH_SHORT).show();
            fetchLessonContent();

        }

        lessonsRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(CourseActivity.this,
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

                   // Toast.makeText(CourseActivity.this, "" + selectedLeson.getQuiz_id(), Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(CourseActivity.this, LessonActivity.class);
                    i.putExtra("lesssonsList", (Serializable) lessonsList);
                    i.putExtra("currentLessonPosition", GetItemPosition);
                    i.putExtra("selectedLeson", selectedLeson);
                    i.putExtra("courseTitle", selectedCourse.getCourse_title());
                    startActivity(i);
                    //   Toast.makeText(CourseActivity.this, selectedLeson.getLesson_title()+"\n\n"+selectedLeson.getLesson_descrption()+"\n\n"+selectedLeson.getLesson_video_url(), Toast.LENGTH_LONG).show();

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

    private class FetchLessonIds extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(globalUrlApi.getBaseUrl() + "/wp-json/ldlms/v1/sfwd-courses/" + (selectedCourse.getCourse_id()) + "/steps?type=t");
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

            //    Toast.makeText(CourseActivity.this, ""+lesson_id, Toast.LENGTH_LONG).show();
            lessons_ids = selectedCourse.getLessons_ids();

            if (lessons_ids_loop < lessons_ids.size() && !exitLesson) {

                //  Toast.makeText(CheckActivity.this, "Course fetching started", Toast.LENGTH_SHORT).show();
                fetchLessonContent();

            }

        }
    }


    private void fetchQuizContent() {

        //  Toast.makeText(this, "" + ("https://brainywoodindia.com/wp-json/ldlms/v1/sfwd-lessons/" + lessons_ids.get(lessons_ids_loop)), Toast.LENGTH_SHORT).show();
        // contentsLessons = new FetchLessonContents();
        //  contentsLessons.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        contentsQuiz = new FetchQuizContents();
        contentsQuiz.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void fetchLessonContent() {

        //  Toast.makeText(this, "" + ("https://brainywoodindia.com/wp-json/ldlms/v1/sfwd-lessons/" + lessons_ids.get(lessons_ids_loop)), Toast.LENGTH_SHORT).show();
        contentsLessons = new FetchLessonContents();
        contentsLessons.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class FetchQuizContents extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(("https://brainywoodindia.com/wp-json/ldlms/v1/sfwd-quiz/" + quiz_ids.get(quiz_ids_loop)));
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
                Log.d("QUIZ_RESPONSE", mainFile);

                //getting course id
                String quiz_id = child.getString("id");

                //getting course title
                JSONObject quiz_title = child.getJSONObject("title");
                String quiz_name = quiz_title.getString("rendered");

                //getting course desc
                JSONObject content = child.getJSONObject("content");
                String quiz_desc = content.getString("rendered");

                //Start

                /*
                Document document = Jsoup.parse(quiz_desc);
                String lesson_description = document.body().text(); // "An example link"

                Elements elements = document.select("body *");
                List<String> tagNames = new ArrayList<String>();
                List<String> values = new ArrayList<String>();

                for (Element element : elements) {
                    String tagName = element.tagName();
                    tagNames.add(tagName);
                    values.add(element.attr("src"));

                }

                String lesson_video_url = values.get(1);
                if (!lesson_video_url.equals("") && lesson_video_url != null) {
                    lesson_video_url = lesson_video_url.substring(0, lesson_video_url.indexOf("?"));
                }
                else{
                    lesson_video_url = "";
                }


                 */

                //for checking
                /*
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Your UI code here
                        Toast.makeText(CourseActivity.this, "" + lesson_video_url, Toast.LENGTH_SHORT).show();
                    }
                });

                 */


                quizList.add(new QuizList(quiz_id, quiz_name, quiz_desc));
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


            quiz_ids_loop++;

            if (quiz_ids_loop < quiz_ids.size() && !exitQuiz) {

                //  Toast.makeText(CourseActivity.this, "Adapter list size: " + lessonsList.size(), Toast.LENGTH_SHORT).show();


                quizRecycler = null;
                quizRecycler = findViewById(R.id.quizRecycler);
                quizRecycler.setLayoutManager(new LinearLayoutManager(CourseActivity.this));

                ArrayList<LessonsList> lessonsListTemp;
                lessonsListTemp = lessonsList;
                QuizAdapterVertical quizAdapterVertical = new QuizAdapterVertical(quizList, CourseActivity.this);
                quizRecycler.setAdapter(quizAdapterVertical);
                quizRecycler.setVisibility(View.VISIBLE);


                // CourseList selectedCourse = courseList.get(course_subs_loop);
                // Toast.makeText(CheckActivity.this, ""+(selectedCourse.getCourse_title()), Toast.LENGTH_SHORT).show();

                fetchQuizContent();

            } else {

                //  CourseHorizAdapter noticeAdapter = new CourseHorizAdapter(lessonsList, CheckActivity.this);
                // courseRecycler.setAdapter(noticeAdapter);
                //  Toast.makeText(CheckActivity.this, "Else started", Toast.LENGTH_SHORT).show();
                quizRecycler = null;
                quizRecycler = findViewById(R.id.quizRecycler);
                quizRecycler.setLayoutManager(new LinearLayoutManager(CourseActivity.this));
                QuizAdapterVertical quizAdapterVertical = new QuizAdapterVertical(quizList, CourseActivity.this);
                quizRecycler.setAdapter(quizAdapterVertical);
                quizRecycler.setVisibility(View.VISIBLE);
                quizRecyclerProgress.setVisibility(View.GONE);
                //    Toast.makeText(CourseActivity.this, "Done Fetching \n"+"Adapter list size: " + lessonsList.size(), Toast.LENGTH_SHORT).show();
            }


        }
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

                //Start

                Document document = Jsoup.parse(lesson_desc);
                String lesson_description = document.body().text(); // "An example link"

                Elements elements = document.select("body *");
                List<String> tagNames = new ArrayList<String>();
                List<String> values = new ArrayList<String>();

                for (Element element : elements) {
                    String tagName = element.tagName();
                    tagNames.add(tagName);
                    values.add(element.attr("src"));

                }

                String lesson_video_url = "";

                try {
                    lesson_video_url = values.get(1);

                }
                catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
                if (!lesson_video_url.equals("") && lesson_video_url != null) {
                    lesson_video_url = lesson_video_url.substring(0, lesson_video_url.indexOf("?"));
                } else {
                    lesson_video_url = "";
                }


                //for checking
                /*
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Your UI code here
                        Toast.makeText(CourseActivity.this, "" + lesson_video_url, Toast.LENGTH_SHORT).show();
                    }
                });

                 */


                lessonsList.add(new LessonsList(lesson_id, lesson_name, lesson_desc, lesson_video_url, lesson_description, selectedCourse.getLessonQuizIdlist().get(lessons_ids_loop).getQuiz_id()));
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

            if (lessons_ids_loop < lessons_ids.size() && !exitLesson) {

                //  Toast.makeText(CourseActivity.this, "Adapter list size: " + lessonsList.size(), Toast.LENGTH_SHORT).show();


                lessonsRecycler = null;
                lessonsRecycler = findViewById(R.id.lessonRecycler);
                lessonsRecycler.setLayoutManager(new LinearLayoutManager(CourseActivity.this));

                ArrayList<LessonsList> lessonsListTemp;
                lessonsListTemp = lessonsList;
                LessonAdapter lessonAdaptert = new LessonAdapter(lessonsList, CourseActivity.this);
                lessonsRecycler.setAdapter(lessonAdaptert);
                lessonsRecycler.setVisibility(View.VISIBLE);


                // CourseList selectedCourse = courseList.get(course_subs_loop);
                // Toast.makeText(CheckActivity.this, ""+(selectedCourse.getCourse_title()), Toast.LENGTH_SHORT).show();

                fetchLessonContent();

            } else {

                //  CourseHorizAdapter noticeAdapter = new CourseHorizAdapter(lessonsList, CheckActivity.this);
                // courseRecycler.setAdapter(noticeAdapter);
                //  Toast.makeText(CheckActivity.this, "Else started", Toast.LENGTH_SHORT).show();
                lessonsRecycler = null;
                lessonsRecycler = findViewById(R.id.lessonRecycler);
                lessonsRecycler.setLayoutManager(new LinearLayoutManager(CourseActivity.this));
                LessonAdapter lessonAdapter = new LessonAdapter(lessonsList, CourseActivity.this);
                lessonsRecycler.setAdapter(lessonAdapter);
                lessonsRecycler.setVisibility(View.VISIBLE);
                lessonsRecyclerProgress.setVisibility(View.GONE);
                //    Toast.makeText(CourseActivity.this, "Done Fetching \n"+"Adapter list size: " + lessonsList.size(), Toast.LENGTH_SHORT).show();
            }


        }
    }


    private void initializeURLs() {
        globalUrlApi = new GlobalUrlApi();
    }

    private void setupCourseUI() {

        lessonsRecyclerProgress = findViewById(R.id.lessonProgress);
        quizRecyclerProgress = findViewById(R.id.quizProgress);


        selected_course_image = findViewById(R.id.selected_course_image);
        selected_course_image_progress = findViewById(R.id.selected_course_image_progress);
        course_title = findViewById(R.id.course_title);
        course_description = findViewById(R.id.course_description);

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
        exitLesson = true;
        if (fetchLessonIds != null) {
            fetchLessonIds.cancel(true);
        }
        if (contentsLessons != null) {
            contentsLessons.cancel(true);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitLesson = true;
        if (fetchLessonIds != null) {
            fetchLessonIds.cancel(true);
        }
        if (contentsLessons != null) {
            contentsLessons.cancel(true);
        }
    }

    public void OpenLecture(View view) {
        startActivity(new Intent(CourseActivity.this, LectureActivity.class));

    }


}
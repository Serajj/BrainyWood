package com.sharad.brainywood;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sharad.brainywood.Models.CourseModel.CourseAdapter;
import com.sharad.brainywood.Models.CourseModel.CourseHorizAdapter;
import com.sharad.brainywood.Models.CourseModel.CourseList;
import com.sharad.brainywood.Utils.FirebaseEmailVerify;
import com.sharad.brainywood.Utils.GlobalUrlApi;
import com.sharad.brainywood.Utils.SessionManager;

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
import java.util.Collections;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ViewPager viewPager;
    ViewFlipper viewFlipper;


    GlobalUrlApi globalUrlApi;
    SessionManager sessionManager;
    String user_id, email, name;

    TextView userName, userEmail, enrolled_courses_text;

    private ProgressBar enrolProgress, allcoursesProgress;
    CardView cardLoading;
    //  LinearLayout loadingLayouot;


    View view;

    // All courses
    ArrayList<CourseList> courseList;
    RecyclerView courseRecycler;
    View ChildView;
    int GetItemPosition;


    //Enrolled Courses components
    ArrayList<CourseList> courseEnrolList;
    RecyclerView courseEnrolRecycler;
    ArrayList<String> course_ids;
    int course_subs_loop = 0;



    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference referenceEmail;

    CardView verifyEmailCard;

    public HomeFragment() {
        // Required empty public constructorcategoryPrefRecycler
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = getActivity().findViewById(R.id.viewPager);
        sessionManager = new SessionManager(getActivity());
        //   loadingLayouot = view.findViewById(R.id.loadingLayout);
        //   loadingLayouot.setVisibility(View.VISIBLE);

        enrolProgress = view.findViewById(R.id.enrolProgress);
        allcoursesProgress = view.findViewById(R.id.allcoursesProgress);
        cardLoading = view.findViewById(R.id.cardLoading);


        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        enrolled_courses_text = view.findViewById(R.id.enrolled_courses_text);

        verifyEmailCard = view.findViewById(R.id.verifyEmailCard);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        referenceEmail = rootNode.getReference("verify");


//        progressBar.setVisibility(View.VISIBLE);


        HashMap<String, String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);
        email = user.get(sessionManager.EMAIL);
        user_id = user.get(sessionManager.ID);

        userName.setText(name);
        userEmail.setText(email);

        initializeURLs();

        // setRetainInstance(true);

        CheckEmailVerification();

        EnrolledCoursesLoad();

        loadCourses();


        return view;
    }

    private void CheckEmailVerification() {

        verifyEmailCard.setVisibility(View.GONE);
        Query checkUser = referenceEmail.orderByChild("user_id").equalTo(user_id);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String user_verify = dataSnapshot.child(user_id).child("user_verify").getValue(String.class);


                if (user_verify == null || user_verify.equals("null") ){

                    //setting firebase user values //
                    FirebaseEmailVerify emailVerify = new FirebaseEmailVerify(user_id, "none");
                    referenceEmail.child(user_id).setValue(emailVerify);
                    //Toast.makeText(getActivity(), "None added", Toast.LENGTH_SHORT).show();
                    verifyEmailCard.setVisibility(View.VISIBLE);

                    //----------//
                }
                else if (user_verify.equals("none")){
                    //Toast.makeText(getActivity(), "Not verified", Toast.LENGTH_SHORT).show();
                    verifyEmailCard.setVisibility(View.VISIBLE);
                }
                else if (user_verify.equals("verified")){
                    //Toast.makeText(getActivity(), "Verified", Toast.LENGTH_SHORT).show();

                }

                // Toast.makeText(MainActivity.this, username+"\n"+device_id, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
      //  Toast.makeText(getActivity(), "REsumed", Toast.LENGTH_SHORT).show();
        CheckEmailVerification();

    }

    private void EnrolledCoursesLoad() {

        courseEnrolRecycler = view.findViewById(R.id.courseEnrolRecycler);
        // noticeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        courseEnrolRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        courseEnrolList = new ArrayList<CourseList>();


        FetchUserCourse userCourse = new FetchUserCourse();
       // userCourse.execute();
        userCourse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        courseEnrolRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getActivity(),
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

                    CourseList selectedCourse = courseEnrolList.get(GetItemPosition);


                    try {
                        if (selectedCourse.getLessons_ids().get(0) != null && !selectedCourse.getLessons_ids().get(0).equals("")) {
                            Intent i = new Intent(getActivity(), CourseActivity.class);
                            i.putExtra("selectedCourse", selectedCourse);
                            startActivity(i);

                        } else {
                            Toast.makeText(getActivity(), "Fetching Course Contents Please Wait...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(getActivity(), "Fetching Course Contents Please Wait...", Toast.LENGTH_SHORT).show();
                    }

                    // Toast.makeText(getActivity(), "" + selectedCourse.getLessons_ids().get(0), Toast.LENGTH_SHORT).show();

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

    private void loadCourses() {

        courseRecycler = view.findViewById(R.id.courseRecycler);
        courseRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        courseList = new ArrayList<CourseList>();

        FetchCourseAll courseAll = new FetchCourseAll();
      //  courseAll.execute();
        courseAll.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        courseRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getActivity(),
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
                    //  Toast.makeText(NavigationActivity.this, songName.get(GetItemPosition)+"\n"+songId.get(GetItemPosition), Toast.LENGTH_LONG).show();
                    //  openNewActivity(OfferId.get(GetItemPosition), OfferImage.get(GetItemPosition),
                    //        OfferName.get(GetItemPosition),
                    //        OfferRupee.get(GetItemPosition), OfferSale.get(GetItemPosition));

                    CourseList selectedCourse = courseList.get(GetItemPosition);


                    try {
                        if (selectedCourse.getLessons_ids().get(0) != null && !selectedCourse.getLessons_ids().get(0).equals("")) {
                            Intent courseSubIntent = new Intent(getActivity(), CourseSubsActivity.class);
                            courseSubIntent.putExtra("selectedCourse", selectedCourse);

                            boolean subs = false;

                            for (int j = 0; j < course_ids.size(); j++) {

                                if (selectedCourse.getCourse_id().equals(course_ids.get(j))) {
                                    subs = true;
                                }

                            }

                            if (subs){
                              //  courseSubIntent.putExtra("courseEnrolled", "yes");
                                Intent ii = new Intent(getActivity(), CourseActivity.class);
                                ii.putExtra("selectedCourse", selectedCourse);
                                startActivity(ii);
                            }
                            else {
                                courseSubIntent.putExtra("courseEnrolled", "no");
                                startActivity(courseSubIntent);

                            }

                        } else {
                            Toast.makeText(getActivity(), "Fetching Course Contents Please Wait...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(getActivity(), "Fetching Course Contents Please Wait...", Toast.LENGTH_SHORT).show();
                    }





                    // Toast.makeText(getActivity(), ""+selectedCourse.getCourse_image_url().toString(), Toast.LENGTH_SHORT).show();
                    //  startActivity(new Intent(getActivity(), CourseSubsActivity.class));

                    //  Toast.makeText(MainActivity.this, ""+(selectedNotice.getNotice_desc()), Toast.LENGTH_SHORT).show();
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

    private class FetchUserCourse extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(globalUrlApi.getBaseUrl() + "wp-json/ldlms/v1/users/" + user_id + "/courses");
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

                //mainFile = "[2601,2425,2453,2487,2489]";

                JSONArray parent = new JSONArray(mainFile);

                course_ids = new ArrayList<String>();

                int i = 0;
                while (i < parent.length()) {

                    course_ids.add(parent.getString(i));
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


            if (course_subs_loop < course_ids.size()) {

                //  Toast.makeText(CheckActivity.this, "Course fetching started", Toast.LENGTH_SHORT).show();
                fetchCourses();

            } else {

                //enrolled_courses_text.setText("Currently you are not enrolled in any course. \nPlease check our");
                enrolled_courses_text.setText("");
                enrolProgress.setVisibility(View.GONE);
                cardLoading.setVisibility(View.GONE);
                allcoursesProgress.setVisibility(View.GONE);
                courseRecycler.setVisibility(View.VISIBLE);
            }


        }
    }

    private void fetchCourses() {

        // Toast.makeText(getActivity(), ""+("https://www.brainywoodindia.com/wp-json/ldlms/v1/sfwd-courses/" + course_ids.get(course_subs_loop)), Toast.LENGTH_SHORT).show();
        FetchCourseEnrol courseAll = new FetchCourseEnrol();
       // courseAll.execute();
        courseAll.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



    }


    private class FetchCourseEnrol extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL((globalUrlApi.getBaseUrl() + "wp-json/ldlms/v1/sfwd-courses/" + course_ids.get(course_subs_loop)));
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
                String course_id = child.getString("id");

                //getting course title
                JSONObject title = child.getJSONObject("title");
                String course_name = title.getString("rendered");

                //getting course desc
                JSONObject content = child.getJSONObject("content");
                String course_desc = content.getString("rendered");

                //getting course price
                String course_price = child.getString("course_price_type");

                //getting course link
                JSONObject _links = child.getJSONObject("_links");
                String self = _links.getString("self");
                JSONArray selfArray = new JSONArray(self);
                JSONObject self_child = selfArray.getJSONObject(0);
                String course_link = self_child.getString("href");

                //getting course steps
                String steps = _links.getString("steps");
                JSONArray stepsArray = new JSONArray(steps);
                JSONObject steps_child = stepsArray.getJSONObject(0);
                String course_steps = steps_child.getString("href");

                //getting course media
                String featuredmedia = _links.getString("wp:featuredmedia");
                JSONArray mediaArray = new JSONArray(featuredmedia);
                JSONObject media_child = mediaArray.getJSONObject(0);
                String course_media = media_child.getString("href");


                courseEnrolList.add(new CourseList(course_id, course_name, course_desc, course_price, course_link, course_steps, course_media));
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


            course_subs_loop++;

            if (course_subs_loop < course_ids.size()) {

                //  Toast.makeText(getActivity(), "Adapter list size: "+courseEnrolList.size(), Toast.LENGTH_SHORT).show();
                // CourseList selectedCourse = courseList.get(course_subs_loop);
                // Toast.makeText(CheckActivity.this, ""+(selectedCourse.getCourse_title()), Toast.LENGTH_SHORT).show();

                fetchCourses();

            } else {


                CourseHorizAdapter noticeAdapter = new CourseHorizAdapter(courseEnrolList, getActivity());
                courseEnrolRecycler.setAdapter(noticeAdapter);
                enrolProgress.setVisibility(View.GONE);
                cardLoading.setVisibility(View.GONE);
                courseEnrolRecycler.setVisibility(View.VISIBLE);

                allcoursesProgress.setVisibility(View.GONE);
                courseRecycler.setVisibility(View.VISIBLE);

                //  Toast.makeText(getActivity(), "Else started", Toast.LENGTH_SHORT).show();
            }


        }
    }


    private class FetchCourseAll extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(globalUrlApi.getBaseUrl() + "wp-json/ldlms/v1/sfwd-courses");
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

                JSONArray parent = new JSONArray(mainFile);

                int i = 0;

                while (i < parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    //getting course id
                    String course_id = child.getString("id");

                    //getting course title
                    JSONObject title = child.getJSONObject("title");
                    String course_name = title.getString("rendered");

                    //getting course desc
                    JSONObject content = child.getJSONObject("content");
                    String course_desc = content.getString("rendered");

                    //getting course price
                    String course_price = child.getString("course_price_type");

                    //getting course link
                    JSONObject _links = child.getJSONObject("_links");
                    String self = _links.getString("self");
                    JSONArray selfArray = new JSONArray(self);
                    JSONObject self_child = selfArray.getJSONObject(0);
                    String course_link = self_child.getString("href");

                    //getting course steps
                    String steps = _links.getString("steps");
                    JSONArray stepsArray = new JSONArray(steps);
                    JSONObject steps_child = stepsArray.getJSONObject(0);
                    String course_steps = steps_child.getString("href");

                    //getting course media
                    String featuredmedia = _links.getString("wp:featuredmedia");
                    JSONArray mediaArray = new JSONArray(featuredmedia);
                    JSONObject media_child = mediaArray.getJSONObject(0);
                    String course_media = media_child.getString("href");


                    courseList.add(new CourseList(course_id, course_name, course_desc, course_price, course_link, course_steps, course_media));
                    //  }

                    //  OfferId.add(offerId);

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


            // Collections.reverse(courseList);


            CourseAdapter noticeAdapter = new CourseAdapter(courseList, getActivity());
            courseRecycler.setAdapter(noticeAdapter);

            CourseFragment.getInstance().loadCourses(courseList, course_ids);

            // CourseFragment.getInstance().loadCourses(courseList, course_ids);



        }
    }

    private void initializeURLs() {
        globalUrlApi = new GlobalUrlApi();

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            ((MainActivity) getActivity()).courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).homeLayout.setBackgroundColor(Color.parseColor("#80BAFA"));
            ((MainActivity) getActivity()).liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

            ((MainActivity) getActivity()).courseText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).homeText.setTextColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).liveText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).qaText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).downloadText.setTextColor(Color.parseColor("#232323"));

        } catch (NullPointerException e) {
            // Toast.makeText(getActivity(), ""+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

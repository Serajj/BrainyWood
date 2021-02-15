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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.sharad.brainywood.Models.CourseModel.CourseAdapter;
import com.sharad.brainywood.Models.CourseModel.CourseFragmentAdapter;
import com.sharad.brainywood.Models.CourseModel.CourseList;
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
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends Fragment {

    ViewPager viewPager;
    ViewFlipper viewFlipper;


    GlobalUrlApi globalUrlApi;
    SessionManager sessionManager;
    String user_id;

    private ProgressBar progressBar;
    //  LinearLayout loadingLayouot;


    View view;

    ArrayList<String> course_ids;


    ArrayList<CourseList> courseList;
    RecyclerView courseRecycler;
    View ChildView;
    int GetItemPosition;

    TextView courseTitle;

    private static CourseFragment instance = null;


    public CourseFragment() {
        // Required empty public constructorcategoryPrefRecycler
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_course, container, false);
        viewPager = getActivity().findViewById(R.id.viewPager);
        sessionManager = new SessionManager(getActivity());
        //   loadingLayouot = view.findViewById(R.id.loadingLayout);
        //   loadingLayouot.setVisibility(View.VISIBLE);
        progressBar = view.findViewById(R.id.progress_bar);
        courseTitle = view.findViewById(R.id.courseTitle);

        instance = this;

//        progressBar.setVisibility(View.VISIBLE);


        HashMap<String, String> user = sessionManager.getUserDetail();
        //name = user.get(sessionManager.NAME);
        String email = user.get(sessionManager.EMAIL);
        user_id = user.get(sessionManager.ID);

        initializeURLs();

        setRetainInstance(true);

        //loadCourses();


        return view;

    }

    public static CourseFragment getInstance() {
        return instance;
    }

    public void LoadCourses(String name) {
        if (name != null) {
            courseTitle.setText(name);
        } else {
            Toast.makeText(getActivity(), "Null", Toast.LENGTH_SHORT).show();
        }
    }


    public void loadCourses(ArrayList<CourseList> courseListt, ArrayList<String> course_idss) {

        courseRecycler = view.findViewById(R.id.courseRecycler);
        courseRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        course_ids = course_idss;

        //courseList = new ArrayList<CourseList>();
        courseList = courseListt;

        FetchCourseAll courseAll = new FetchCourseAll();
        //  courseAll.execute();

        ProgressBar allcoursesProgress = view.findViewById(R.id.allcoursesProgress);
        allcoursesProgress.setVisibility(View.GONE);
        courseRecycler.setVisibility(View.VISIBLE);

        CourseFragmentAdapter adapter = new CourseFragmentAdapter(courseList, getActivity());
        courseRecycler.setAdapter(adapter);

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
                            Intent i = new Intent(getActivity(), CourseSubsActivity.class);
                            i.putExtra("selectedCourse", selectedCourse);

                            boolean subs = false;

                            for (int j = 0; j < course_ids.size(); j++) {

                                if (selectedCourse.getCourse_id().equals(course_ids.get(j))) {
                                    subs = true;
                                }

                            }

                            if (subs) {
                                //  courseSubIntent.putExtra("courseEnrolled", "yes");
                                Intent ii = new Intent(getActivity(), CourseActivity.class);
                                ii.putExtra("selectedCourse", selectedCourse);
                                startActivity(ii);

                            } else {
                                i.putExtra("courseEnrolled", "no");
                                startActivity(i);

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

            ProgressBar allcoursesProgress = view.findViewById(R.id.allcoursesProgress);
            allcoursesProgress.setVisibility(View.GONE);
            courseRecycler.setVisibility(View.VISIBLE);

            CourseFragmentAdapter adapter = new CourseFragmentAdapter(courseList, getActivity());
            courseRecycler.setAdapter(adapter);


        }
    }


    private void initializeURLs() {
        globalUrlApi = new GlobalUrlApi();
        //  Categories_URL = globalUrlApi.getGlobalVariable() + "categories_json.php";
        // Cat_pref_count_URL = globalUrlApi.getGlobalVariable() + "get_pref_count_cat.php?user_id=" + user_id;


    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            ((MainActivity) getActivity()).courseLayout.setBackgroundColor(Color.parseColor("#80BAFA"));
            ((MainActivity) getActivity()).homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).liveLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

            ((MainActivity) getActivity()).courseText.setTextColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).homeText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).liveText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).qaText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).downloadText.setTextColor(Color.parseColor("#232323"));

        } catch (NullPointerException e) {
            // Toast.makeText(getActivity(), ""+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

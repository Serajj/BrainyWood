package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.commons.io.FilenameUtils;

import android.view.View;
import android.widget.ImageView;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckActivity extends AppCompatActivity {

    TextView result;

    String url1;


    String course_id, course_name, course_desc, course_price, course_link, course_steps, course_media;

    ArrayList<String> course_ids;
    int course_subs_loop = 0;

    String coure_image;


    ArrayList<CourseList> courseList;
    RecyclerView courseRecycler;
    View ChildView;
    int GetItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        result = findViewById(R.id.result);


        //  getUrl("https://player.vimeo.com/video/27170494/config"); //not brainy video
        //  getUrl("https://player.vimeo.com/video/462061589/config");  //brainy video

        // getUrll("https://brainywoodindia.com/wp-json/ldlms/v1/sfwd-courses"); //not brainy video

        FetchWish wish = new FetchWish();
        //  wish.execute();

        Fetchmedia fetchmedia = new Fetchmedia();
        // fetchmedia.execute();

        //   CourseLoad();


        // String html = "<p>text1 &nbsp;</p><p><img src=\"http://theSite.com/apple.png\" alt=\"apple-touch-icon-144x144-precomposed\" /></p><p><img src=\"http://theSite.com/sony.gif\" alt=\"cool\" /></p><p style=\"text-align: center;\">Second Text&nbsp;</p><p><img src=\"http://theSite.com/img.jpg\" alt=\"2\" /></p><p>&nbsp;</p><p style=\"text-align: left;\">TextAgain&nbsp;</p>";
        String html = "<p><iframe title=\"Fourth Dimension of Education\" src=\"https://player.vimeo.com/video/462061589?dnt=1&amp;app_id=122963\" width=\"1170\" height=\"658\" frameborder=\"0\" allow=\"autoplay; fullscreen\" allowfullscreen></iframe></p> <p>Do you know What are Four Dimensions of Education? Do you know the importance of Trainer in Life?</p> <p>Have you ever wondered why we able to memorise Movies just after a single watch but things go completely opposite while studying?</p> <p>We don&#8217;t watch movies to remember, but we still able to Memorise the exact scene, dialogues of it on the other hand we want to memorise the long answer, our study content still we not able to.               Ever wondered why that happen? In this very first episode of Brain Science you will get all your unsolved answers.</p> ";
        Document document = Jsoup.parse(html);
        String text = document.body().text(); // "An example link"

        Elements elements = document.select("body *");
        List<String> tagNames = new ArrayList<String>();
        List<String> values = new ArrayList<String>();

        int i = 0;
        for (Element element : elements) {
            String tagName = element.tagName();
            tagNames.add(tagName);
            values.add(element.attr("src"));

            i++;
        }

        String url = values.get(1);
        url = url.substring(0, url.indexOf("?"));

        System.out.println(text);
        System.out.println(url);

        EnrollFirst();


    }

    private void EnrollFirst() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://brainywoodindia.com/wp-json/ldlms/v1/users/990490/courses",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(CheckActivity.this, "First Done", Toast.LENGTH_SHORT).show();
                        EnrollSecond();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(CheckActivity.this, "VolleyError error", Toast.LENGTH_SHORT).show();


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_ids", "2601");
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


            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://brainywoodindia.com/wp-json/ldlms/v1/sfwd-courses/2601/users",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Toast.makeText(CheckActivity.this, "Second Done", Toast.LENGTH_SHORT).show();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(CheckActivity.this, "VolleyError error", Toast.LENGTH_SHORT).show();


                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_ids", "990490");
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

    private void CourseLoad() {


        courseRecycler = findViewById(R.id.courseRecycler);
        // noticeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        courseRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        courseList = new ArrayList<CourseList>();

        FetchUserCourse userCourse = new FetchUserCourse();
        userCourse.execute();

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

                    JSONObject array1 = progressive.getJSONObject(0);

                    url1 = array1.getString("url");

                    // Toast.makeText(CheckActivity.this, ""+progressive.toString(), Toast.LENGTH_SHORT).show();
                    try {
                        URL url = new URL(url1);
                        String name = FilenameUtils.getBaseName(url.getPath());
                        String exe = FilenameUtils.getExtension(url.getPath());

                        Toast.makeText(CheckActivity.this, name + "\n" + exe, Toast.LENGTH_SHORT).show();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }


                    result.setText(url1.toString());

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    //give YourVideoUrl below
                    retriever.setDataSource(url1, new HashMap<String, String>());
                    // this gets frame at 2nd second
                    Bitmap image = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    //use this bitmap image
                    ImageView imageView = findViewById(R.id.thumbnail);
                    imageView.setImageBitmap(image);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CheckActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                    result.setText(e.toString());

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(str);
    }

    public void getUrll(String url) {

        StringRequest str = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(CheckActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                try {

                    JSONArray parent = new JSONArray(response);
                    JSONObject child = parent.getJSONObject(0);
                    JSONObject title = child.getJSONObject("title");
                    String name = title.getString("rendered");
                    Toast.makeText(CheckActivity.this, "" + name, Toast.LENGTH_SHORT).show();
                    //  JSONObject rendered = child.getJSONObject("rendered");

                    /*

                    JSONObject object = new JSONObject(response);
                    JSONObject request = object.getJSONObject("request");
                    JSONObject files = request.getJSONObject("files");
                    JSONArray progressive = files.getJSONArray("progressive");

                    JSONObject array1 = progressive.getJSONObject(0);

                    url1 = array1.getString("url");

                    // Toast.makeText(CheckActivity.this, ""+progressive.toString(), Toast.LENGTH_SHORT).show();
                    try {
                        URL url = new URL(url1);
                        String name = FilenameUtils.getBaseName(url.getPath());
                        String exe = FilenameUtils.getExtension(url.getPath());

                        Toast.makeText(CheckActivity.this, name + "\n" + exe, Toast.LENGTH_SHORT).show();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }


                    result.setText(url1.toString());

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    //give YourVideoUrl below
                    retriever.setDataSource(url1, new HashMap<String, String>());
                    // this gets frame at 2nd second
                    Bitmap image = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    //use this bitmap image
                    ImageView imageView = findViewById(R.id.thumbnail);
                    imageView.setImageBitmap(image);



                     */


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CheckActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                    result.setText(e.toString());

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        str.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(str);
    }

    private class FetchWish extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("https://www.brainywoodindia.com/wp-json/ldlms/v1/sfwd-courses/2601");
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

                JSONObject child = parent.getJSONObject(0);

                //getting course id
                course_id = child.getString("id");

                //getting course title
                JSONObject title = child.getJSONObject("title");
                course_name = title.getString("rendered");

                //getting course desc
                JSONObject content = child.getJSONObject("content");
                course_desc = content.getString("rendered");

                //getting course price
                course_price = child.getString("course_price_type");

                //getting course link
                JSONObject _links = child.getJSONObject("_links");
                String self = _links.getString("self");
                JSONArray selfArray = new JSONArray(self);
                JSONObject self_child = selfArray.getJSONObject(0);
                course_link = self_child.getString("href");

                //getting course steps
                String steps = _links.getString("steps");
                JSONArray stepsArray = new JSONArray(steps);
                JSONObject steps_child = stepsArray.getJSONObject(0);
                course_steps = steps_child.getString("href");

                //getting course media
                String featuredmedia = _links.getString("wp:featuredmedia");
                JSONArray mediaArray = new JSONArray(featuredmedia);
                JSONObject media_child = mediaArray.getJSONObject(0);
                course_media = media_child.getString("href");


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

            Toast.makeText(CheckActivity.this, course_id + "\n" + course_name + "\n" + course_desc + "\n" + course_price + "\n" + course_link + "\n" + course_steps + "\n" + course_media, Toast.LENGTH_LONG).show();
            String st = (course_id + "\n" + course_name + "\n" + course_desc + "\n" + course_price + "\n" + course_link + "\n" + course_steps + "\n" + course_media);

            result.setText(st);


        }
    }

    private class Fetchmedia extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("https://brainywoodindia.com/wp-json/wp/v2/media/3118");
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


                // JSONArray parent = new JSONArray(mainFile);
                JSONObject parent = new JSONObject(mainFile);

                //JSONObject child = parent.getJSONObject("");

                //getting course id
                course_id = parent.getString("id");
                JSONObject title = parent.getJSONObject("guid");
                //  course_name = title.getString("rendered");


                JSONObject media_details = parent.getJSONObject("media_details");
                JSONObject sizes = media_details.getJSONObject("sizes");
                JSONObject medium = sizes.getJSONObject("medium");
                course_name = medium.getString("source_url");


                //getting course title
                //    JSONObject title = child.getJSONObject("title");
                //   course_name = title.getString("rendered");


                //getting course link
                /*
                JSONObject _links = child.getJSONObject("_links");
                String self = _links.getString("self");
                JSONArray selfArray = new JSONArray(self);
                JSONObject self_child = selfArray.getJSONObject(0);
                course_link = self_child.getString("href");
                 */


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

            Toast.makeText(CheckActivity.this, course_id + "\n" + course_name, Toast.LENGTH_LONG).show();
            String st = (course_id + "\n" + course_name);

            result.setText(st);


        }
    }

    private class FetchUserCourse extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("https://brainywoodindia.com/wp-json/ldlms/v1/users/990469/courses");
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

                mainFile = "[2601,2425,2453,2487,2489]";

                JSONArray parent = new JSONArray(mainFile);

                course_ids = new ArrayList<String>();

                int i = 0;
                course_id = "";
                while (i < parent.length()) {


                    course_ids.add(parent.getString(i));
                    //    course_id = parent.getString(0);
                    //   course_name = parent.getString(0);

                    i++;
                }
                //  JSONObject parent = new JSONObject(mainFile);

                //JSONObject child = parent.getJSONObject("");

                //getting course id


                //getting course title
                //    JSONObject title = child.getJSONObject("title");
                //   course_name = title.getString("rendered");


                //getting course link
                /*
                JSONObject _links = child.getJSONObject("_links");
                String self = _links.getString("self");
                JSONArray selfArray = new JSONArray(self);
                JSONObject self_child = selfArray.getJSONObject(0);
                course_link = self_child.getString("href");
                 */


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

            for (int i = 0; i < course_ids.size(); i++) {

                course_id = course_id + course_ids.get(i) + "\n";
                course_name = course_ids.get(i) + "\n";

            }

            //     Toast.makeText(CheckActivity.this, course_id + "\n" +course_name, Toast.LENGTH_LONG).show();
            String st = (course_id);

            result.setText(st);

            if (course_subs_loop < course_ids.size()) {

                //  Toast.makeText(CheckActivity.this, "Course fetching started", Toast.LENGTH_SHORT).show();
                fetchCourses();

            }


        }
    }

    private void fetchCourses() {


        //    Toast.makeText(this, ""+course_ids.get(course_subs_loop)+"\n\n"+course_subs_loop+"\n"+course_ids.size(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "" + ("https://www.brainywoodindia.com/wp-json/ldlms/v1/sfwd-courses/" + course_ids.get(course_subs_loop)), Toast.LENGTH_SHORT).show();
        FetchCourseAll courseAll = new FetchCourseAll();
        courseAll.execute();
    }

    private class FetchCourseAll extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(("https://www.brainywoodindia.com/wp-json/ldlms/v1/sfwd-courses/" + course_ids.get(course_subs_loop)));
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
                course_id = child.getString("id");

                //getting course title
                JSONObject title = child.getJSONObject("title");
                course_name = title.getString("rendered");

                //getting course desc
                JSONObject content = child.getJSONObject("content");
                course_desc = content.getString("rendered");

                //getting course price
                course_price = child.getString("course_price_type");

                //getting course link
                JSONObject _links = child.getJSONObject("_links");
                String self = _links.getString("self");
                JSONArray selfArray = new JSONArray(self);
                JSONObject self_child = selfArray.getJSONObject(0);
                course_link = self_child.getString("href");

                //getting course steps
                String steps = _links.getString("steps");
                JSONArray stepsArray = new JSONArray(steps);
                JSONObject steps_child = stepsArray.getJSONObject(0);
                course_steps = steps_child.getString("href");

                //getting course media
                String featuredmedia = _links.getString("wp:featuredmedia");
                JSONArray mediaArray = new JSONArray(featuredmedia);
                JSONObject media_child = mediaArray.getJSONObject(0);
                course_media = media_child.getString("href");


                courseList.add(new CourseList(course_id, course_name, course_desc, course_price, course_link, course_steps, course_media));
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

                Toast.makeText(CheckActivity.this, "Adapter list size: " + courseList.size(), Toast.LENGTH_SHORT).show();
                // CourseList selectedCourse = courseList.get(course_subs_loop);
                // Toast.makeText(CheckActivity.this, ""+(selectedCourse.getCourse_title()), Toast.LENGTH_SHORT).show();

                fetchCourses();

            } else {

                CourseHorizAdapter noticeAdapter = new CourseHorizAdapter(courseList, CheckActivity.this);
                courseRecycler.setAdapter(noticeAdapter);
                Toast.makeText(CheckActivity.this, "Else started", Toast.LENGTH_SHORT).show();
            }


        }
    }


}
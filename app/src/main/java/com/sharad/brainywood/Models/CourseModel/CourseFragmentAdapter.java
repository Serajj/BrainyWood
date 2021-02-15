package com.sharad.brainywood.Models.CourseModel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharad.brainywood.Models.LessonsModel.LessonQuizIDsList;
import com.sharad.brainywood.R;
import com.sharad.brainywood.Utils.GlobalUrlApi;
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
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CourseFragmentAdapter extends RecyclerView.Adapter<CourseFragmentAdapter.CourseHolder> {

    ArrayList<String> lessons_ids;
    ArrayList<String> quiz_ids;
    ArrayList<LessonQuizIDsList> lessonQuizIdlist;




    ArrayList<CourseList> list;
    Context context;
    View view;

    public CourseFragmentAdapter() {
    }

    public CourseFragmentAdapter(ArrayList<CourseList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.course_fragment_row, parent, false);


        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int position) {

        final CourseList currentData = list.get(position);

        String course_title = currentData.getCourse_title();
        String course_desc = currentData.getCourse_content();

        course_title = course_title.replace("&#8211;", "-");
        course_desc = course_desc.replace("<p>", "");
        course_desc = course_desc.replace("</p>", "");

        List<String> colors;

        colors=new ArrayList<String>();

        colors.add("#f2453d");
        colors.add("#fd9727");
        colors.add("#fdc02f");
        colors.add("#50ae54");
        colors.add("#2c98f0");
        colors.add("#4054b2");
        colors.add("#9a30ae");
        colors.add("#e72564");

        Random r = new Random();
        int i1 = r.nextInt(7- 0) + 0;

        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.RECTANGLE);
        draw.setColor(Color.parseColor(colors.get(i1)));

        holder.Course_Background.setBackground(draw); //textview



        //font colors transparent

        List<String> colors2;

        colors2=new ArrayList<String>();

        colors2.add("#40FF7D5E");
        colors2.add("#40FDC02F");
        colors2.add("#4050AE54");
        colors2.add("#402C98F0");
        colors2.add("#409A30AE");
        colors2.add("#40E72564");
        colors2.add("#40FD9727");
        colors2.add("#40CDDB49");

        Random r2 = new Random();
        int randomColor = r2.nextInt(7- 0) + 0;

        GradientDrawable draw2 = new GradientDrawable();
        draw2.setShape(GradientDrawable.RECTANGLE);
        draw2.setColor(Color.parseColor(colors2.get(randomColor)));

        holder.Course_front.setBackground(draw2); //textview


        //end of front color transparent


        holder.Course_Title.setText(course_title);
        holder.Course_Desc.setText(course_desc);

        FetchImage fetchImage = new FetchImage(currentData.getCourse_media(), holder, currentData);
        fetchImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
     //   fetchImage.execute();

        FetchLessonIds fetchLessonIds = new FetchLessonIds(holder, currentData);
        fetchLessonIds.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private class FetchLessonIds extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;

        CourseHolder holder;
        CourseList currentData;

        public FetchLessonIds(CourseHolder holderr, CourseList currentDataa) {
            holder = holderr;
            currentData = currentDataa;
        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(new GlobalUrlApi().getBaseUrl() + "/wp-json/app/v1/get_course_steps/" + (currentData.getCourse_id()) + "");
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


                lessons_ids = new ArrayList<String>();
                quiz_ids = new ArrayList<String>();
                lessonQuizIdlist = new ArrayList<LessonQuizIDsList>();
                //  JSONObject title = parent.getJSONObject("t");

                JSONArray data = parent.getJSONArray("data");
                for (int i = 0; i < data.length(); i++){

                    JSONObject lesson_quiz_ids = data.getJSONObject(i);

                    Log.d("Quizz_check", lesson_quiz_ids.getString("quiz_id"));
                    Log.d("Lesson_check", lesson_quiz_ids.getString("lesson_id"));
                    if (lesson_quiz_ids.getString("quiz_id") != null &&
                            !lesson_quiz_ids.getString("quiz_id").equals("null")

                    ){
                        lessons_ids.add(lesson_quiz_ids.getString("lesson_id"));
                        quiz_ids.add(lesson_quiz_ids.getString("quiz_id"));
                        lessonQuizIdlist.add(new LessonQuizIDsList(lesson_quiz_ids.getString("lesson_id"), lesson_quiz_ids.getString("quiz_id")));

                    }
                    else {
                        lessons_ids.add(lesson_quiz_ids.getString("lesson_id"));
                        lessonQuizIdlist.add(new LessonQuizIDsList(lesson_quiz_ids.getString("lesson_id"), "null"));
                    }

                }


                /*
                JSONArray sfwdlessons = parent.getJSONArray("sfwd-lessons");
                JSONArray sfwdquiz = parent.getJSONArray("sfwd-quiz");
              //  Log.d("QUIZZES_FETCHED", "" + sfwdquiz.length());


                for (int i = 0; i < sfwdlessons.length(); i++){
                    lessons_ids.add(sfwdlessons.getString(i));
                    Log.d("LESSONS_FETCHED_TWO", sfwdlessons.getString(i));
                }

                for (int i = 0; i < sfwdquiz.length(); i++){
                    quiz_ids.add(sfwdquiz.getString(i));
                    Log.d("QUIZZES_FETCHED", sfwdquiz.getString(i));
                }


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

            Collections.reverse(lessons_ids);
            Collections.reverse(quiz_ids);
            Collections.reverse(lessonQuizIdlist);


            currentData.setLessons_ids(lessons_ids);
            currentData.setQuiz_ids(quiz_ids);
            currentData.setLessonQuizIdlist(lessonQuizIdlist);
        }
    }


    private class FetchImage extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;
        String media_url;
        CourseHolder holderr;
        String ImageUrl;
        CourseList currentDataa;

        public FetchImage(String course_media, CourseHolder holder, CourseList currentData) {

            media_url = course_media;
            holderr = holder;
            currentDataa = currentData;

        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(media_url);
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

                JSONObject media_details = parent.getJSONObject("media_details");
                JSONObject sizes = media_details.getJSONObject("sizes");
                JSONObject medium = sizes.getJSONObject("medium");
                ImageUrl = medium.getString("source_url");

                //high resolution image
              //  JSONObject title = parent.getJSONObject("guid");
              //  ImageUrl = title.getString("rendered");


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


         //   holderr.Course_Title.setText(ImageUrl);
           // Toast.makeText(context, ""+ ImageUrl, Toast.LENGTH_SHORT).show();

            currentDataa.setCourse_image_url(ImageUrl);
            Picasso.get()
                    .load(ImageUrl)
                    .placeholder(context.getResources().getDrawable(R.drawable.default_image))

                    .into(holderr.Course_Image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (holderr.Course_Progress != null) {
                                holderr.Course_Progress.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }

                    });
                  //  .into(holderr.Course_Image);





        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CourseHolder extends RecyclerView.ViewHolder {

        TextView Course_Title, Course_Desc;
        ImageView Course_Image;
        LinearLayout Course_Background;
        ProgressBar Course_Progress;

        View Course_front;


        public CourseHolder(@NonNull View itemView) {
            super(itemView);

            Course_front = itemView.findViewById(R.id.course_front);

            Course_Title = itemView.findViewById(R.id.course_name);
            Course_Desc = itemView.findViewById(R.id.course_desc);
            Course_Image = itemView.findViewById(R.id.course_image);
            Course_Background = itemView.findViewById(R.id.courseLayoutBackground);
            Course_Progress = itemView.findViewById(R.id.course_progress);





        }
    }
}

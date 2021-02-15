package com.sharad.brainywood.Models.QuizModel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharad.brainywood.Models.CourseModel.CourseList;
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
import java.util.List;
import java.util.Random;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizHolder> {

    ArrayList<String> lessons_ids;

    ArrayList<QuizList> list;
    Context context;
    View view;

    public QuizAdapter() {
    }

    public QuizAdapter(ArrayList<QuizList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.course_fragment_row, parent, false);


        return new QuizHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizHolder holder, int position) {

        final QuizList currentData = list.get(position);

        String course_title = currentData.getQuiz_title();
        String course_desc = currentData.getQuiz_content();

        course_title = course_title.replace("&#8211;", "-");


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


        holder.Course_Title.setText(course_title);
        holder.Course_Desc.setText(course_desc);

        FetchImage fetchImage = new FetchImage(currentData.getQuiz_media(), holder, currentData);
        fetchImage.execute();

      //  FetchLessonIds fetchLessonIds = new FetchLessonIds(holder, currentData);
      //  fetchLessonIds.execute();

    }

    private class FetchLessonIds extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;

        QuizHolder holder;
        CourseList currentData;

        public FetchLessonIds(QuizHolder holderr, CourseList currentDataa) {
            holder = holderr;
            currentData = currentDataa;
        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(new GlobalUrlApi().getBaseUrl() + "/wp-json/ldlms/v1/sfwd-courses/"+(currentData.getCourse_id())+"/steps?type=t");
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

            currentData.setLessons_ids(lessons_ids);

        }
    }


    private class FetchImage extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;
        String media_url;
        QuizHolder holderr;
        String ImageUrl;
        QuizList currentDataa;

        public FetchImage(String course_media, QuizHolder holder, QuizList currentData) {

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

            currentDataa.setQuiz_image(ImageUrl);
            Picasso.get()
                    .load(ImageUrl)
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

    public class QuizHolder extends RecyclerView.ViewHolder {

        TextView Course_Title, Course_Desc;
        ImageView Course_Image;
        LinearLayout Course_Background;
        ProgressBar Course_Progress;


        public QuizHolder(@NonNull View itemView) {
            super(itemView);

            Course_Title = itemView.findViewById(R.id.course_name);
            Course_Desc = itemView.findViewById(R.id.course_desc);
            Course_Image = itemView.findViewById(R.id.course_image);
            Course_Background = itemView.findViewById(R.id.courseLayoutBackground);
            Course_Progress = itemView.findViewById(R.id.course_progress);





        }
    }
}

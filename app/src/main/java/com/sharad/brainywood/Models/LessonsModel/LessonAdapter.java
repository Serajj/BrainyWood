package com.sharad.brainywood.Models.LessonsModel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharad.brainywood.Models.CourseModel.CourseList;
import com.sharad.brainywood.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonHolder> {

    ArrayList<LessonsList> list;
    Context context;
    View view;

    public LessonAdapter() {
    }

    public LessonAdapter(ArrayList<LessonsList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public LessonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.lesson_row, parent, false);


        return new LessonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonHolder holder, int position) {

        final LessonsList currentData = list.get(position);

        String lesson_title = currentData.getLesson_title();
        String quizId = currentData.getQuiz_id();

        lesson_title = lesson_title.replace("&#8211;", "-");


        holder.Lesson_Title.setText(lesson_title);
        if(quizId != null && !quizId.equals("null")){
            holder.Quiz_view.setVisibility(View.VISIBLE);
        }


    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LessonHolder extends RecyclerView.ViewHolder {

        TextView Lesson_Title;
        TextView Quiz_view;


        public LessonHolder(@NonNull View itemView) {
            super(itemView);

            Lesson_Title = itemView.findViewById(R.id.lesson_name);
            Quiz_view = itemView.findViewById(R.id.quiz_name);





        }
    }
}

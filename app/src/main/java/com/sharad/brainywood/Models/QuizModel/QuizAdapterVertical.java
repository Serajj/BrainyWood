package com.sharad.brainywood.Models.QuizModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharad.brainywood.Models.LessonsModel.LessonsList;
import com.sharad.brainywood.R;

import java.util.ArrayList;

public class QuizAdapterVertical extends RecyclerView.Adapter<QuizAdapterVertical.LessonHolder> {

    ArrayList<QuizList> list;
    Context context;
    View view;

    public QuizAdapterVertical() {
    }

    public QuizAdapterVertical(ArrayList<QuizList> list, Context context) {
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

        final QuizList currentData = list.get(position);

        String lesson_title = currentData.getQuiz_title();

        lesson_title = lesson_title.replace("&#8211;", "-");


        holder.Lesson_Title.setText(lesson_title);


    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LessonHolder extends RecyclerView.ViewHolder {

        TextView Lesson_Title;


        public LessonHolder(@NonNull View itemView) {
            super(itemView);

            Lesson_Title = itemView.findViewById(R.id.lesson_name);





        }
    }
}

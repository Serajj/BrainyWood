package com.sharad.brainywood.Models.AnswersModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharad.brainywood.Models.CourseModel.CourseAdapter;
import com.sharad.brainywood.Models.CourseModel.CourseList;
import com.sharad.brainywood.R;

import java.util.ArrayList;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerHolder> {


    ArrayList<AnswersList> list;
    Context context;
    View view;

    public AnswerAdapter() {
    }

    public AnswerAdapter(ArrayList<AnswersList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AnswerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.answer_row, parent, false);

        return new AnswerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerHolder holder, int position) {

        final AnswersList currentData = list.get(position);

        String yourAnswer = currentData.getSelectedAnswer();
        String correctAnswer = currentData.getCorrectAnswer();
        boolean isCorrect = currentData.isAnswerCheck();


        if (isCorrect) {
            holder.answerLayoutColor.setBackgroundResource(R.drawable.green_stock_bg);
        }
        else {
            holder.answerLayoutColor.setBackgroundResource(R.drawable.red_stock_bg);

        }


        holder.yourAnswerView.setText(yourAnswer);
        holder.correctAnswerView.setText(correctAnswer);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AnswerHolder extends RecyclerView.ViewHolder {

        TextView yourAnswerView, correctAnswerView;
        LinearLayout answerLayoutColor;

        public AnswerHolder(@NonNull View itemView) {
            super(itemView);

            answerLayoutColor = itemView.findViewById(R.id.answerLayoutColor);
            yourAnswerView = itemView.findViewById(R.id.yourAnswerView);
            correctAnswerView = itemView.findViewById(R.id.correctAnswerView);

        }
    }

}

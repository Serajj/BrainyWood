package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.sharad.brainywood.Models.AnswersModel.AnswerAdapter;
import com.sharad.brainywood.Models.AnswersModel.AnswersList;
import com.sharad.brainywood.Models.LessonsModel.LessonAdapter;

import java.util.ArrayList;

public class QuizResultActivity extends AppCompatActivity {

    ArrayList<AnswersList> answersLists;
    RecyclerView answersRecycler;

    int correctAnswers = 0;
    int wrongAnswers = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_quiz_result);

        Intent intent = getIntent();

        answersLists = (ArrayList<AnswersList>) intent.getSerializableExtra("answersList");

        for (int i= 0 ; i< answersLists.size();i++){

            AnswersList currentAnswer = answersLists.get(i);
            if (currentAnswer.isAnswerCheck()){
                correctAnswers++;
            }
            else {
                wrongAnswers++;
            }

        }

        TextView wrongAnswersView, correctAnswersView, totalAnswersView;

        totalAnswersView = findViewById(R.id.totalAnswersView);
        correctAnswersView = findViewById(R.id.correctAnswersView);
        wrongAnswersView = findViewById(R.id.wrongAnswersView);

        totalAnswersView.setText(""+answersLists.size());
        correctAnswersView.setText(""+correctAnswers);
        wrongAnswersView.setText(""+wrongAnswers);


      //  Toast.makeText(this, ""+answersLists.size(), Toast.LENGTH_SHORT).show();

        answersRecycler = findViewById(R.id.answersRecycler);
        answersRecycler.setLayoutManager(new LinearLayoutManager(this));
        AnswerAdapter adapter = new AnswerAdapter(answersLists, QuizResultActivity.this);
        answersRecycler.setAdapter(adapter);


        final LottieAnimationView view = findViewById(R.id.animation_view);

        view.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {



            }

            @Override
            public void onAnimationEnd(Animator animator) {


            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void Finish(View view) {
        finish();
    }

    public void ViewAnswers(View view) {
        LinearLayout answersLayout = findViewById(R.id.answersLayout);
        answersLayout.setVisibility(View.VISIBLE);
    }
}
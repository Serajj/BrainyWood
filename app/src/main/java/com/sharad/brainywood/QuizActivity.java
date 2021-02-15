package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sharad.brainywood.Models.AnswersModel.AnswersList;
import com.sharad.brainywood.Models.CourseModel.CourseList;
import com.sharad.brainywood.Models.QuizModel.QuizList;
import com.sharad.brainywood.Models.QuizQuestions.QuizQuestionsList;
import com.sharad.brainywood.Utils.GlobalUrlApi;

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

public class QuizActivity extends AppCompatActivity {

    QuizList selectedQuiz;
    String QuizTitle, CourseTitle;
    String QuizID;

    ArrayList<QuizQuestionsList> questionsList;

    ProgressDialog pd;

    TextView courseNameView, quizNameView, QuizQuestionTitleView, QuestionDescView, AnswerOneView, AnswerTwoView, AnswerThreeView, AnswerFourView;
    ImageView ImageAnswerOne, ImageAnswerTwo, ImageAnswerThree, ImageAnswerFour;
    int currentQuestionPosition = 0;

    ArrayList<AnswersList> selectedAnswers;
    String currentSelectedAnswer = "";

    CardView cardLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_quiz);

        selectedAnswers = new ArrayList<>();
        pd = new ProgressDialog(QuizActivity.this);
        pd.setMessage("Loading Quiz...");
        pd.setCancelable(false);
        //pd.show();

        Intent intent = getIntent();
        try {


            selectedQuiz = (QuizList) intent.getSerializableExtra("selectedQuiz");

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        QuizTitle = (intent.getStringExtra("quizTitle").replace("&#8211;", "-"));
        CourseTitle = intent.getStringExtra("CourseTitle");
        QuizID = intent.getStringExtra("selectedQuizID");

        questionsList = new ArrayList<QuizQuestionsList>();
        FetchQuizQuestions questions = new FetchQuizQuestions();
        questions.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        SetupUI();
    }

    private void SetupUI() {


        cardLoading = findViewById(R.id.cardLoading);


        courseNameView = findViewById(R.id.courseNameView);
        quizNameView = findViewById(R.id.quizNameView);

        QuizQuestionTitleView = findViewById(R.id.QuizQuestionTitleView);
        QuestionDescView = findViewById(R.id.QuestionDescView);
        AnswerOneView = findViewById(R.id.AnswerOneView);
        AnswerTwoView = findViewById(R.id.AnswerTwoView);
        AnswerThreeView = findViewById(R.id.AnswerThreeView);
        AnswerFourView = findViewById(R.id.AnswerFourView);

        ImageAnswerOne = findViewById(R.id.ImageAnswerOne);
        ImageAnswerTwo = findViewById(R.id.ImageAnswerTwo);
        ImageAnswerThree = findViewById(R.id.ImageAnswerThree);
        ImageAnswerFour = findViewById(R.id.ImageAnswerFour);

        courseNameView.setText(CourseTitle);
        quizNameView.setText(QuizTitle);

    }

    public void NextQuestion(View view) {

        if (currentSelectedAnswer.equals("")){

            final AlertDialog.Builder dialog = new AlertDialog.Builder(QuizActivity.this, R.style.DialogTheme)
                    .setTitle("Info")
                    .setMessage("Please Select Any Answer                                        ")
                    .setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            dialog.show();

        }
        else {

            QuizQuestionsList currentQuestion = questionsList.get(currentQuestionPosition);

            boolean answreCheck = false;
            if (currentSelectedAnswer.equals(currentQuestion.getCorrect_answer())){
                answreCheck = true;
            }

            selectedAnswers.add(new AnswersList(currentSelectedAnswer, currentQuestion.getCorrect_answer(), answreCheck));
            currentSelectedAnswer = "";

            currentQuestionPosition++;

            final ProgressDialog nextQuestionPd;
            nextQuestionPd = new ProgressDialog(QuizActivity.this);
            nextQuestionPd.setMessage("Wait...");
            nextQuestionPd.setCancelable(false);
            nextQuestionPd.show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextQuestionPd.dismiss();
                    LoadQuizQuestionsAndAnswersUI();

                }
            }, 2000);
        }


    }

    public void FirstAnswerClicked(View view) {

        currentSelectedAnswer = AnswerOneView.getText().toString();

        ImageAnswerOne.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_blue));
        ImageAnswerTwo.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerThree.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerFour.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));


    }

    public void SecondAnswerClicked(View view) {

        currentSelectedAnswer = AnswerTwoView.getText().toString();


        ImageAnswerOne.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerTwo.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_blue));
        ImageAnswerThree.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerFour.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));

    }

    public void ThirdAnswerClicked(View view) {

        currentSelectedAnswer = AnswerThreeView.getText().toString();



        ImageAnswerOne.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerTwo.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerThree.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_blue));
        ImageAnswerFour.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));

    }

    public void FourAnswerClicked(View view) {

        currentSelectedAnswer = AnswerFourView.getText().toString();


        ImageAnswerOne.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerTwo.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerThree.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
        ImageAnswerFour.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_blue));

    }

    private class FetchQuizQuestions extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;

        String mainFile;


        @Override
        protected String doInBackground(String... strings) {

            try {
               // URL url = new URL(new GlobalUrlApi().getBaseUrl() + "wp-json/app/v1/get_quiz_question/" + (selectedQuiz.getQuiz_id()));
                URL url = new URL(new GlobalUrlApi().getBaseUrl() + "wp-json/app/v1/get_quiz_question/" + QuizID);

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

                JSONArray data = parent.getJSONArray("data");


                for (int i = 0; i < data.length(); i++) {

                    JSONObject questionObject = data.getJSONObject(i);


                    //String quiz_id = selectedQuiz.getQuiz_id();
                    String quiz_id = QuizID;

                    String question_small_id = questionObject.getString("_id");
                    String question_id_big = questionObject.getString("_id");
                    String question_title = questionObject.getString("_title");
                    String question_question = questionObject.getString("_question");

                    question_question = question_question.replace("<p>", "");
                    question_question = question_question.replace("</p>", "");

                    JSONArray answersArray = questionObject.getJSONArray("_answerData");
                    JSONObject answerObject1 = answersArray.getJSONObject(0);
                    JSONObject answerObject2 = answersArray.getJSONObject(1);
                    JSONObject answerObject3 = answersArray.getJSONObject(2);
                    JSONObject answerObject4 = answersArray.getJSONObject(3);

                    String answer_one = answerObject1.getString("_answer");
                    String answer_two = answerObject2.getString("_answer");
                    String answer_three = answerObject3.getString("_answer");
                    String answer_four = answerObject4.getString("_answer");

                    String ans_one_correct = answerObject1.getString("_correct");
                    String ans_two_correct = answerObject2.getString("_correct");
                    String ans_three_correct = answerObject3.getString("_correct");
                    String ans_four_correct = answerObject4.getString("_correct");

                    String correct_answer = "";
                    if (answerObject1.getString("_correct").equals("true")) {
                        correct_answer = answerObject1.getString("_answer");
                    } else if (answerObject2.getString("_correct").equals("true")) {
                        correct_answer = answerObject2.getString("_answer");
                    } else if (answerObject3.getString("_correct").equals("true")) {
                        correct_answer = answerObject3.getString("_answer");
                    } else if (answerObject4.getString("_correct").equals("true")) {
                        correct_answer = answerObject4.getString("_answer");
                    }

                    Log.d("Questions_Quiz", quiz_id + " " + question_small_id + " " + question_id_big + " " + question_title + " " + question_question + " " + answer_one + " " + answer_two + " " + answer_three + " " + answer_four + " " + correct_answer);
                    //  Log.d("Questions_Quiz", answerObject2.getString("_correct"));

                    questionsList.add(new QuizQuestionsList(QuizID, question_small_id, question_id_big, question_title, question_question, answer_one, answer_two, answer_three, answer_four, correct_answer));

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

            //pd.dismiss();
            cardLoading.setVisibility(View.GONE);

            Log.d("Questions_Size", "" + questionsList.size());
            LoadQuizQuestionsAndAnswersUI();
        }
    }

    private void LoadQuizQuestionsAndAnswersUI() {

        try {

            try {


                ImageAnswerOne.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
                ImageAnswerTwo.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
                ImageAnswerThree.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));
                ImageAnswerFour.setImageDrawable(ContextCompat.getDrawable(QuizActivity.this, R.drawable.ic_quiz_done_gray));

                QuizQuestionsList currentQuestion = questionsList.get(currentQuestionPosition);

                QuizQuestionTitleView.setText(currentQuestion.getQuestion_title());
                QuestionDescView.setText(currentQuestion.getQuestion_question());
                AnswerOneView.setText(currentQuestion.getAnswer_one());
                AnswerTwoView.setText(currentQuestion.getAnswer_two());
                AnswerThreeView.setText(currentQuestion.getAnswer_three());
                AnswerFourView.setText(currentQuestion.getAnswer_four());


            } catch (IndexOutOfBoundsException i) {
                i.printStackTrace();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(QuizActivity.this, R.style.DialogTheme)
                        .setTitle("Thank You")
                        .setMessage(selectedAnswers.size()+" Answered & Quiz Completed                                        ")
                        .setCancelable(false)
                        .setPositiveButton("View Result", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
                                intent.putExtra("answersList", selectedAnswers);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                dialog.show();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();

            final AlertDialog.Builder dialog = new AlertDialog.Builder(QuizActivity.this, R.style.DialogTheme)
                    .setTitle("Thank You")
                    .setMessage("Quiz Completed                                        ")
                    .setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            dialog.show();

        }


    }


    public void Close(View view) {
        finish();
    }

    public void StartButton(View view) {
        // LinearLayout quizLayout = findViewById(R.id.quizLayout);
        LinearLayout startLayout = findViewById(R.id.startLayout);
        startLayout.setVisibility(View.GONE);
        //    quizLayout.setVisibility(View.VISIBLE);
    }

    public void QuizFinish(View view) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(QuizActivity.this, R.style.DialogTheme)
                .setTitle("Thank You")
                .setMessage("Quiz Submitted..                                        ")
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        dialog.show();
    }
}
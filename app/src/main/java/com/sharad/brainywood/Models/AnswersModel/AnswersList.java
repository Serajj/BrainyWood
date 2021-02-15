package com.sharad.brainywood.Models.AnswersModel;

import java.io.Serializable;

public class AnswersList implements Serializable {

    String SelectedAnswer, CorrectAnswer;
    boolean AnswerCheck;

    public String getSelectedAnswer() {
        return SelectedAnswer;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public boolean isAnswerCheck() {
        return AnswerCheck;
    }

    public AnswersList() {
    }

    public AnswersList(String selectedAnswer, String correctAnswer, boolean answerCheck) {
        SelectedAnswer = selectedAnswer;
        CorrectAnswer = correctAnswer;
        AnswerCheck = answerCheck;
    }
}

package com.sharad.brainywood.Models.QuizQuestions;

import java.io.Serializable;

public class QuizQuestionsList implements Serializable {

    String quiz_id, question_id_small, question_id_big, question_title,
            question_question, answer_one, answer_two, answer_three, answer_four, correct_answer;

    public String getQuiz_id() {
        return quiz_id;
    }

    public String getQuestion_id_small() {
        return question_id_small;
    }

    public String getQuestion_id_big() {
        return question_id_big;
    }

    public String getQuestion_title() {
        return question_title;
    }

    public String getQuestion_question() {
        return question_question;
    }

    public String getAnswer_one() {
        return answer_one;
    }

    public String getAnswer_two() {
        return answer_two;
    }

    public String getAnswer_three() {
        return answer_three;
    }

    public String getAnswer_four() {
        return answer_four;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public QuizQuestionsList() {
    }

    public QuizQuestionsList(String quiz_id, String question_id_small, String question_id_big,
                             String question_title, String question_question,
                             String answer_one, String answer_two, String answer_three, String answer_four, String correct_answer) {
        this.quiz_id = quiz_id;
        this.question_id_small = question_id_small;
        this.question_id_big = question_id_big;
        this.question_title = question_title;
        this.question_question = question_question;
        this.answer_one = answer_one;
        this.answer_two = answer_two;
        this.answer_three = answer_three;
        this.answer_four = answer_four;
        this.correct_answer = correct_answer;
    }
}

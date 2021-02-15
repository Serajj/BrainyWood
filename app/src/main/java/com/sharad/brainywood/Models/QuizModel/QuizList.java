package com.sharad.brainywood.Models.QuizModel;

import java.io.Serializable;

public class QuizList implements Serializable {

    String quiz_id, quiz_title, quiz_content, quiz_media, quiz_image;

    public String getQuiz_id() {
        return quiz_id;
    }

    public String getQuiz_title() {
        return quiz_title;
    }

    public String getQuiz_content() {
        return quiz_content;
    }

    public String getQuiz_media() {
        return quiz_media;
    }

    public String getQuiz_image() {
        return quiz_image;
    }

    public void setQuiz_image(String quiz_image) {
        this.quiz_image = quiz_image;
    }

    public QuizList() {
    }

    public QuizList(String quiz_id, String quiz_title, String quiz_content, String quiz_media) {
        this.quiz_id = quiz_id;
        this.quiz_title = quiz_title;
        this.quiz_content = quiz_content;
        this.quiz_media = quiz_media;
    }

    public QuizList(String quiz_id, String quiz_title, String quiz_content) {
        this.quiz_id = quiz_id;
        this.quiz_title = quiz_title;
        this.quiz_content = quiz_content;
    }
}

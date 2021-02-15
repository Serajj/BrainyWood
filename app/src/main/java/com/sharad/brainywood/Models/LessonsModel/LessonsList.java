package com.sharad.brainywood.Models.LessonsModel;

import java.io.Serializable;

public class LessonsList implements Serializable {

    String lesson_id, lesson_title, lesson_content, lesson_video_url, lesson_descrption, quiz_id;

    public String getLesson_id() {
        return lesson_id;
    }

    public String getLesson_title() {
        return lesson_title;
    }

    public String getLesson_content() {
        return lesson_content;
    }

    public String getLesson_video_url() {
        return lesson_video_url;
    }

    public String getLesson_descrption() {
        return lesson_descrption;
    }

    public void setLesson_video_url(String lesson_video_url) {
        this.lesson_video_url = lesson_video_url;
    }

    public void setLesson_descrption(String lesson_descrption) {
        this.lesson_descrption = lesson_descrption;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public LessonsList() {
    }

    public LessonsList(String lesson_id, String lesson_title, String lesson_content) {
        this.lesson_id = lesson_id;
        this.lesson_title = lesson_title;
        this.lesson_content = lesson_content;
    }

    public LessonsList(String lesson_id, String lesson_title, String lesson_content, String lesson_video_url, String lesson_descrption) {
        this.lesson_id = lesson_id;
        this.lesson_title = lesson_title;
        this.lesson_content = lesson_content;
        this.lesson_video_url = lesson_video_url;
        this.lesson_descrption = lesson_descrption;
    }

    public LessonsList(String lesson_id, String lesson_title, String lesson_content, String lesson_video_url, String lesson_descrption, String quiz_id) {
        this.lesson_id = lesson_id;
        this.lesson_title = lesson_title;
        this.lesson_content = lesson_content;
        this.lesson_video_url = lesson_video_url;
        this.lesson_descrption = lesson_descrption;
        this.quiz_id = quiz_id;
    }
}

package com.sharad.brainywood.Models.LessonsModel;

import java.io.Serializable;

public class LessonQuizIDsList implements Serializable {

    String lesson_id, quiz_id;

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public LessonQuizIDsList() {
    }

    public LessonQuizIDsList(String lesson_id, String quiz_id) {
        this.lesson_id = lesson_id;
        this.quiz_id = quiz_id;
    }
}

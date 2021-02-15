package com.sharad.brainywood.Models.CourseModel;

import com.sharad.brainywood.Models.LessonsModel.LessonQuizIDsList;

import java.io.Serializable;
import java.util.ArrayList;

public class CourseList implements Serializable {

    String course_id, course_title, course_content, course_price, course_link, course_steps, course_media, course_image_url;

    ArrayList<String> lessons_ids;
    ArrayList<String> quiz_ids;

    ArrayList<LessonQuizIDsList> lessonQuizIdlist;

    public ArrayList<LessonQuizIDsList> getLessonQuizIdlist() {
        return lessonQuizIdlist;
    }

    public void setLessonQuizIdlist(ArrayList<LessonQuizIDsList> lessonQuizIdlist) {
        this.lessonQuizIdlist = lessonQuizIdlist;
    }

    public ArrayList<String> getLessons_ids() {
        return lessons_ids;
    }

    public ArrayList<String> getQuiz_ids() {
        return quiz_ids;
    }

    public String getCourse_id() {
        return course_id;
    }

    public String getCourse_title() {
        return course_title;
    }

    public String getCourse_content() {
        return course_content;
    }

    public String getCourse_price() {
        return course_price;
    }

    public String getCourse_link() {
        return course_link;
    }

    public String getCourse_steps() {
        return course_steps;
    }

    public String getCourse_media() {
        return course_media;
    }

    public String getCourse_image_url() {
        return course_image_url;
    }

    public void setCourse_image_url(String course_image_url) {
        this.course_image_url = course_image_url;
    }

    public void setLessons_ids(ArrayList<String> lessons_ids) {
        this.lessons_ids = lessons_ids;
    }

    public void setQuiz_ids(ArrayList<String> quiz_ids) {
        this.quiz_ids = quiz_ids;
    }

    public CourseList() {
    }

    public CourseList(String course_id, String course_title, String course_content, String course_price, String course_link, String course_steps, String course_media) {
        this.course_id = course_id;
        this.course_title = course_title;
        this.course_content = course_content;
        this.course_price = course_price;
        this.course_link = course_link;
        this.course_steps = course_steps;
        this.course_media = course_media;
    }


}

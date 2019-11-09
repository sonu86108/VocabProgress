package com.sonu.vocabprogress.models;

public class Quiz {
    private String quizName, date;
    private int quizId;

    public Quiz(int quizId, String quizName, String date) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.date = date;
    }
    public Quiz(){

    }

    public Quiz(String quizName, String date) {
        this.quizName = quizName;
        this.date = date;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
}

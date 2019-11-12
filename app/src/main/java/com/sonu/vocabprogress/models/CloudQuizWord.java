package com.sonu.vocabprogress.models;

import java.util.List;

public class CloudQuizWord {
    private String quizId;
    private List<Word> quizWordList;

    public CloudQuizWord(String quizId, List<Word> quizWordList) {
        this.quizId = quizId;
        this.quizWordList = quizWordList;
    }

    public CloudQuizWord(){

    }
    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public List<Word> getQuizWordList() {
        return quizWordList;
    }

    public void setQuizWordList(List<Word> quizWordList) {
        this.quizWordList = quizWordList;
    }


}

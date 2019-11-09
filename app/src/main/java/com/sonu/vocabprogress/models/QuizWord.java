package com.sonu.vocabprogress.models;

public class QuizWord extends Word {

    private String quizId;

    public QuizWord(String quizId, Word word) {
        super(word.getWordName(), word.getWordMeaning(), word.getWordDesc());
        this.quizId = quizId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public void setQuizWord(Word word){
        super.setWordName(word.getWordName());
        super.setWordMeaning(word.getWordMeaning());
        super.setWordDesc(word.getWordDesc());
    }

    public Word getQuizWord(){
        return new Word(super.getWordName(),super.getWordMeaning(),super.getWordDesc());
    }

}

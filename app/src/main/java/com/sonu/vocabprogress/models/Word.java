package com.sonu.vocabprogress.models;

import java.io.Serializable;

public class Word implements Serializable {
    private String wordName, wordMeaning, wordDesc,wordID;
    public Word(String wordName, String wordMeaning, String wordDesc, String wordID) {
        this.wordName = wordName;
        this.wordMeaning = wordMeaning;
        this.wordDesc = wordDesc;
        this.wordID = wordID;
    }
   public Word(String word){
     this.wordName=word;
   }
   public Word(){

   }


    public Word(String wordName, String wordMeaning, String wordDesc) {
        this.wordName = wordName;
        this.wordMeaning = wordMeaning;
        this.wordDesc = wordDesc;
    }
    public String getWordID(){return wordID;}

    public void setWordID(String wordID){this.wordID=wordID;}


    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getWordMeaning() {
        return wordMeaning;
    }

    public void setWordMeaning(String wordMeaning) {
        this.wordMeaning = wordMeaning;
    }

    public String getWordDesc() {
        return wordDesc;
    }

    public void setWordDesc(String wordDesc) {
        this.wordDesc = wordDesc;
    }


}

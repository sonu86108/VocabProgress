package com.sonu.vocabprogress.utilities.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CloudDatabaseHelper {
    public static final String NODE_QUIZES="quizes";
    public static final String NODE_APP_MAIN="VocabProgress/UserData/";
    public static final String NODE_QUIZ_WORD="quiz_words";

    FirebaseAuth mAuth;
    DatabaseReference mDbRef;
    public DatabaseReference mDbQuizRef,mDbQuizWordRef;
    boolean result;
    String quizKey;
    private static CloudDatabaseHelper instance=null;

    private CloudDatabaseHelper(){
        mAuth=FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDbRef=firebaseDatabase.getReference(NODE_APP_MAIN+FirebaseAuth.getInstance().
                getCurrentUser().getUid()+"/");
        mDbQuizRef=mDbRef.child(NODE_QUIZES);
        mDbQuizWordRef=mDbRef.child(NODE_QUIZ_WORD);
    }

    public static CloudDatabaseHelper getInstance(){

        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            if(instance==null){
                instance=new CloudDatabaseHelper();
            }
            return instance;
        }else {
            return null;
        }
    }



}

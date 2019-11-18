package com.sonu.vocabprogress.utilities.datahelpers;


import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sonu.vocabprogress.models.Quiz;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.OnGetDataListener;

import java.util.ArrayList;
import java.util.List;


public class CloudDatabaseHelper {
    private static CloudDatabaseHelper instance=null;
    private static final String NODE_QUIZES="quizes";
    private static final String NODE_APP_MAIN="VocabProgress/UserData/";
    private final String NODE_QUIZ_WORD="quiz_words";
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference dBQuizRef,dBQuizWordRef;
    private String userDisplayName,userID;
    private CloudDatabaseHelper(){
        firebaseAuth=CloudDatabaseHelper.getFirebaseAuth();
        userDisplayName = firebaseAuth.getCurrentUser().getDisplayName();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference=firebaseDatabase.getReference(NODE_APP_MAIN+userID+"/");
        dBQuizRef=databaseReference.child(NODE_QUIZES);
        dBQuizWordRef=databaseReference.child(NODE_QUIZ_WORD);
    }

    public static CloudDatabaseHelper getInstance(){

        if(CloudDatabaseHelper.isSignedIn()){
            if(instance==null){
                instance=new CloudDatabaseHelper();
            }
            return instance;
        }else {
            return null;
        }
    }
    public void readQuizFromFirebase(final OnGetDataListener onGetDataListener){
        onGetDataListener.onStart();
        dBQuizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Quiz> quizList=new ArrayList<>();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Quiz quiz=ds.getValue(Quiz.class);
                    quizList.add(new Quiz(ds.getKey(),quiz.getQuizName(),quiz.getDate()));
                }
                onGetDataListener.onSuccess(quizList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              onGetDataListener.onFailure(databaseError.getMessage());
            }
        });
    }

    public void readQuizWordsFromFirebase(String quizKey,final OnGetDataListener onGetDataListener){
        onGetDataListener.onStart();
        dBQuizWordRef.child(quizKey).addListenerForSingleValueEvent(new ValueEventListener() {
            List<Word> words=new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                        words.add(ds.getValue(Word.class));
                }onGetDataListener.onSuccess(words);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               onGetDataListener.onFailure(databaseError.getMessage());
            }
        });
    }

    public static boolean isSignedIn(){
     if(FirebaseAuth.getInstance().getCurrentUser()!=null) return true;
     else return false;
    }
    public static FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }
    public static String getUserDisplayName(){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null && FirebaseAuth.getInstance().getCurrentUser().getDisplayName() !=null){
            return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }
        return null;
    }
    public DatabaseReference getdBQuizRef(){
        return dBQuizRef;
    }
    public DatabaseReference getdBQuizWordRef(){
        return dBQuizWordRef;
    }



}

package com.sonu.vocabprogress.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Quiz;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.ui.adapters.WordListAdapter;
import com.sonu.vocabprogress.utilities.helpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.helpers.QuizWordHelper;
import com.sonu.vocabprogress.utilities.helpers.RecyclerViewTouchEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizWordsActivity extends AppCompatActivity implements RecyclerViewTouchEventListener {

    RecyclerView quizWordsRecyclerView;
    WordListAdapter wordListAdapter;
    List<Word> wordList;
    QuizWordHelper quizWordHelper;
    CloudDatabaseHelper cloudDatabaseHelper;
    String quizId;
    ProgressBar pbQuizWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizwords);
        init();
        initRecyclerView();
        if (getIntent() != null) {
            quizId = getIntent().getExtras().getString("quizId");
        }
    }

    public void init() {
        wordList = new ArrayList<>();
        quizWordHelper = QuizWordHelper.getInstance(this);
        pbQuizWords=findViewById(R.id.id_pb_quizWords);
        cloudDatabaseHelper=CloudDatabaseHelper.getInstance();
    }

    private void initRecyclerView() {
        quizWordsRecyclerView = findViewById(R.id.id_quizwords_recyclerView);
        wordListAdapter = new WordListAdapter(wordList, this);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        quizWordsRecyclerView.setLayoutManager(lm);
        quizWordsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        quizWordsRecyclerView.setAdapter(wordListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWordList();
    }

    @Override
    public void onRecyclerViewItemClick(View v, int p) {

    }

    @Override
    public void onRecyclerViewItemLongClick(View v, int p) {

    }

    public void updateWordList() {
        wordList.clear();
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            readQuizFromFirebase(quizId);
        }else {
            readQuizFromLocalDb();
        }

    }

    private void readQuizFromLocalDb(){
        Cursor cursor = quizWordHelper.retrieveData(quizId);
        if (cursor.moveToFirst()) {
            do {
                wordList.add(new Word(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2)));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Words not found", Toast.LENGTH_LONG).
                    show();
        }
        wordListAdapter.notifyDataSetChanged();
    }

    private void readQuizFromFirebase(String quizId){
        pbQuizWords.setVisibility(View.VISIBLE);
        cloudDatabaseHelper.mDbQuizWordRef.child(quizId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    wordList.add(ds.getValue(Word.class));
                }
                pbQuizWords.setVisibility(View.GONE);
                wordListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(databaseError.getMessage());
            }
        });
    }
private void toast(String msg){
        Toast.makeText(QuizWordsActivity.this,msg,Toast.LENGTH_SHORT).show();
}

}

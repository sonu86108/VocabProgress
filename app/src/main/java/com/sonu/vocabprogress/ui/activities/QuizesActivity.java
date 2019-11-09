package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.ClientLibraryUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Quiz;
import com.sonu.vocabprogress.ui.adapters.QuizesAdapter;
import com.sonu.vocabprogress.utilities.helpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.helpers.QuizHelper;
import com.sonu.vocabprogress.utilities.helpers.RecyclerViewTouchEventListener;

import java.util.ArrayList;
import java.util.List;


public class QuizesActivity extends AppCompatActivity implements
        RecyclerViewTouchEventListener {

    RecyclerView quizesRecyclerView;
    QuizesAdapter quizesAdapter;
    TextView tvMsg;
    ProgressBar pbQuiz;
    boolean isItPlayMode;
    List<Quiz> quizList;
    QuizHelper quizHelper;
    CloudDatabaseHelper cloudDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizes);
        init();
        ifPlayMode();
        initRecyclerView();
    }

    private void init() {
        quizList = new ArrayList<>();
        tvMsg=findViewById(R.id.id_tv_msg);
        pbQuiz=findViewById(R.id.id_pb_quiz);
        quizHelper = QuizHelper.getInstance(this);
        cloudDatabaseHelper= CloudDatabaseHelper.getInstance();
    }

    private void initRecyclerView() {
        quizesRecyclerView = findViewById(R.id.id_quizes_recyclerView);
        quizesAdapter = new QuizesAdapter(this, quizList, isItPlayMode);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        quizesRecyclerView.setLayoutManager(lm);
        quizesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        quizesRecyclerView.setAdapter(quizesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateQuizList();

    }

    private void updateQuizList() {
        quizList.clear();
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
          readQuizFromFirebase();
        }else {
            readQuizFromLocalDb();
        }
    }

    @Override
    public void onRecyclerViewItemClick(View v, int p) {

        switch (v.getId()) {
            case R.id.id_cardView_quizRow:
                Intent intent = new Intent(QuizesActivity.this, QuizWordsActivity.class);
                intent.putExtra("quizId", quizList.get(p).getQuizId());
                startActivity(intent);
                break;
            case R.id.id_playQuiz:
                Intent playQuizIntent = new Intent(this, PlayQuizActivity.class);
                playQuizIntent.putExtra("quizId", quizList.get(p).getQuizId());
                startActivity(playQuizIntent);
        }

    }

    @Override
    public void onRecyclerViewItemLongClick(View v, int p) {

    }

    public void ifPlayMode() {
        if (getIntent().getExtras() != null && getIntent().getExtras().
                getBoolean("play_mode")) {
            getSupportActionBar().setTitle("Play Quiz");
            isItPlayMode = true;
        }
    }

    private void readQuizFromLocalDb(){
        Cursor cursor = quizHelper.retrieveData();
        if (cursor.moveToFirst()) {
            do {
                quizList.add(new Quiz(String.valueOf(cursor.getInt(0)), cursor
                        .getString(1), cursor.getString(2)));

            } while (cursor.moveToNext());
        }
        quizesAdapter.notifyDataSetChanged();
        onDataNotFoundMsg();
    }

    private void readQuizFromFirebase(){
        pbQuiz.setVisibility(View.VISIBLE);
        cloudDatabaseHelper.mDbQuizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for(DataSnapshot ds: dataSnapshot.getChildren()){
                       quizList.add(new Quiz(ds.getKey(),ds.getValue(Quiz.class).getQuizName(),ds.getValue(Quiz.class).getDate()));
                   }
                   pbQuiz.setVisibility(View.GONE);
                   quizesAdapter.notifyDataSetChanged();
                   onDataNotFoundMsg();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    toast(databaseError.getMessage());
            }
        });
    }

    private void toast(String msg){
        Toast.makeText(QuizesActivity.this,msg,Toast.LENGTH_LONG).show();
    }

    private void onDataNotFoundMsg(){
        if(quizList.isEmpty()){
            quizesRecyclerView.setVisibility(View.GONE);
            tvMsg.setVisibility(View.VISIBLE);
        }
    }
}

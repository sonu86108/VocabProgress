package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Quiz;
import com.sonu.vocabprogress.ui.adapters.QuizesAdapter;
import com.sonu.vocabprogress.utilities.datahelpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.OnGetDataListener;
import com.sonu.vocabprogress.utilities.datahelpers.QuizHelper;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.RecyclerViewTouchEventListener;

import java.util.ArrayList;
import java.util.List;


public class QuizesActivity extends AppCompatActivity implements
        RecyclerViewTouchEventListener {

    RecyclerView quizesRecyclerView;
    QuizesAdapter quizesAdapter;
    TextView tvMsg;
    ProgressBar pbQuiz;
    boolean isItPlayMode;
    ArrayList<Quiz> quizList;
    QuizHelper quizHelper;
    CloudDatabaseHelper cloudDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizes);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        ifPlayMode();
        initRecyclerView();
    }

    private void init() {
        quizList=new ArrayList<>();
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
        if(CloudDatabaseHelper.isSignedIn()){
        readQuizDataFromFirebase();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
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
    public void onRecyclerViewItemLongClick(View v, ImageView menu, int p) {

    }

    public void ifPlayMode() {
        if (getIntent().getExtras() != null && getIntent().getExtras().
                getBoolean("play_mode")) {
            getSupportActionBar().setTitle("Play Quiz");
            isItPlayMode = true;
        }
    }

    private void readQuizDataFromFirebase() {
        cloudDatabaseHelper.readQuizFromFirebase(new OnGetDataListener() {
            @Override
            public void onStart() {
                pbQuiz.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(List<?> quizlist) {
                for (Quiz quiz : (ArrayList<Quiz>) quizlist) {
                    quizList.add(quiz);
                }
                quizesAdapter.notifyDataSetChanged();
                pbQuiz.setVisibility(View.GONE);
                onDataNotFoundMsg();
            }

            @Override
            public void onFailure(String errorMessage) {
                pbQuiz.setVisibility(View.GONE);
                toast(errorMessage);
                onDataNotFoundMsg();
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

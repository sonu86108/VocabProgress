package com.sonu.vocabprogress.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.ui.adapters.WordListAdapter;
import com.sonu.vocabprogress.utilities.helpers.QuizWordHelper;
import com.sonu.vocabprogress.utilities.helpers.RecyclerViewTouchEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizWordsActivity extends AppCompatActivity implements RecyclerViewTouchEventListener {

    RecyclerView quizWordsRecyclerView;
    WordListAdapter wordListAdapter;
    List<Word> wordList;
    QuizWordHelper quizWordHelper;
    int quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizwords);
        init();
        initRecyclerView();
        if (getIntent() != null) {
            quizId = getIntent().getExtras().getInt("quizId");
            Toast.makeText(this, "id: " + quizId, Toast.LENGTH_LONG).show();
        }
        updateWordList();
    }

    public void init() {
        wordList = new ArrayList<>();
        quizWordHelper = QuizWordHelper.getInstance(this);
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
        //updateWordList();
    }

    @Override
    public void onRecyclerViewItemClick(View v, int p) {

    }

    @Override
    public void onRecyclerViewItemLongClick(View v, int p) {

    }

    public void updateWordList() {
        wordList.clear();
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
}

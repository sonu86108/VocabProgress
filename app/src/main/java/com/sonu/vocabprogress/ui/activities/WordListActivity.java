package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.ui.adapters.WordListAdapter;
import com.sonu.vocabprogress.utilities.SelectionMode;
import com.sonu.vocabprogress.utilities.helpers.RecyclerViewTouchEventListener;
import com.sonu.vocabprogress.utilities.helpers.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;


public class WordListActivity extends AppCompatActivity
        implements View.OnClickListener, RecyclerViewTouchEventListener {

    SelectionMode selectionMode;
    Toolbar toolbar;
    RecyclerView wordListRecyclerView;
    List<Word> wordList;
    WordListAdapter wordListAdapter;
    FloatingActionButton fabAddWord;
    SQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);
        toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        setListners();
    }

    public void init() {
        wordListRecyclerView = findViewById(R.id.id_recyclerview_wordlist);
        selectionMode = new SelectionMode(this);
        fabAddWord = findViewById(R.id.id_fab);
        db = SQLiteHelper.getSQLiteHelper(this);
        wordList = new ArrayList<Word>();
        initRecyView();
    }

    public void initRecyView() {
        wordListAdapter = new WordListAdapter(wordList, this);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        wordListRecyclerView.setLayoutManager(lm);
        wordListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        wordListRecyclerView.setAdapter(wordListAdapter);
    }

    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.id_fab:
                startActivityForResult(new Intent(this,
                        NotificationDialogActivity.class), 24);
                break;
        }
    }

    @Override
    public void onRecyclerViewItemClick(View v, int position) {
        if (selectionMode.isInSelectionMode()) {
            selectionMode.onClick(v, position);
        } else {
            Word word = wordList.get(position);
            String s = word.getWordName() + "\n" +
                    word.getWordMeaning() + "\n" +
                    word.getWordDesc() + "\n" + position;
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecyclerViewItemLongClick(View v, int p) {
        selectionMode.enterInSelectionMode(fabAddWord, toolbar, wordList);
        selectionMode.onLongClick(v, p);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (selectionMode.isInSelectionMode()) {
            selectionMode.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 24) {
            if (resultCode == RESULT_OK) {
                updateWordList();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWordList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (selectionMode.isInSelectionMode()) selectionMode.exitSelectionMode();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (selectionMode.isInSelectionMode()) {
            selectionMode.exitSelectionMode();
        } else {
            onBackPressed();
        }
        return true;
    }

    private void setListners() {
        fabAddWord.setOnClickListener(this);
    }

    public void updateWordList() {
        Cursor curso = db.retrieveData();
        wordList.clear();
        if (curso.moveToFirst()) {
            do {
                wordList.add(new Word(curso.getString(1), curso.getString(2), curso.getString(3)));
            } while (curso.moveToNext());
        }
        wordListAdapter.notifyDataSetChanged();
    }

    public void showInSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.id_layout_activity_wordlist),
                msg, Snackbar.LENGTH_LONG).show();
    }

    public void showInToast(String msg) {
        Toast.makeText(WordListActivity.this, "", Toast.LENGTH_LONG).show();
    }
}

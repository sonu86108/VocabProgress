package com.sonu.vocabprogress.ui.activities.words;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.ui.activities.words.WordsActivity;
import com.sonu.vocabprogress.ui.dialogs.MyDialogs;
import com.sonu.vocabprogress.utilities.datahelpers.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;


public class SelectionMode {
    FloatingActionButton fab;
    Drawable background;
    Toolbar toolbar;
    private boolean isInSelectionMode;
    private WordsActivity wordListActivity;
    private int counter;
    private List<Word> wordList;
    private List<Integer> selectedWordList;
    private List<View> selectedWordView;
    private ImageView moreOptions;

    public SelectionMode(Context context) {
        this.wordListActivity = (WordsActivity) context;
        selectedWordList = new ArrayList<Integer>();
        selectedWordView = new ArrayList<View>();
    }

    public boolean isInSelectionMode() {
        return this.isInSelectionMode;
    }

    public void setSelectionMode(boolean b) {
        this.isInSelectionMode = b;
    }

    public void updateCounter() {
        if (selectedWordView.size() == selectedWordList.size()) {
            counter = selectedWordView.size();
        }
        toolbar.setTitle(counter + " items selected");
    }

    public void selectItem(View view, int p) {
        selectedWordView.add(view);
        selectedWordList.add(p);
        background = view.getBackground();
        view.setLongClickable(false);
        view.setBackgroundColor(R.color.colorPrimary);
        updateCounter();
    }

    public void unselectItem(View view, int p) {
        selectedWordView.remove(view);
        selectedWordList.remove((Integer) p);
        view.setBackground(background);
        view.setLongClickable(true);
        updateCounter();

    }

    public void enterInSelectionMode(FloatingActionButton fab,
                                     Toolbar toolbar, List<Word> words) {
        isInSelectionMode = true;
        this.fab = fab;
        this.toolbar = toolbar;
        this.wordList = words;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_selection_mode);
        fab.setVisibility(View.GONE);
        toolbar.setTitle(counter + " items selected");
    }

    public void exitSelectionMode() {
        isInSelectionMode = false;
        wordListActivity.wordListAdapter.notifyDataSetChanged();
        for (View v : selectedWordView) {
            v.setBackground(background);
            v.setEnabled(true);
        }
        this.toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_word_list);
        this.fab.setVisibility(View.VISIBLE);
        this.toolbar.setTitle("Word List");
        selectedWordList.clear();
        selectedWordView.clear();
        counter = 0;
    }

    public void onClick(View v, int p) {
        if (selectedWordList.contains((Integer) p)) {
            unselectItem(v, p);
        } else if (v.getId() == R.id.id_cardView) {
            selectItem(v, p);
        }
    }

    public void onLongClick(View v, int p) {
        switch (v.getId()) {
            case R.id.id_cardView:
                selectItem(v, p);
        }
    }

    public void onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_item_delete:
                deleteItems();
                break;
            case R.id.id_it_makeQuiz:
                makeQuiz();
        }
    }

    public void deleteItems() {
        if (counter == 0) {
            wordListActivity.showInSnackBar("Select an item first");
        } else {
            confirmDelete();
        }

    }

    public void itemDelete() {
        int i = 0;
        do {
            Word word = wordList.get(selectedWordList.get(i));
            SQLiteHelper.getSQLiteHelper(wordListActivity).deleteData(word.getWordName());
            i++;
        } while (i < selectedWordList.size());
        wordListActivity.updateWordList();
        wordListActivity.showInSnackBar(counter + " items deleted");
    }

    public void makeQuiz() {
        if (counter < 10) {
            wordListActivity.showInSnackBar("Select at least 10 words");
        } else if (counter > 10) {
            wordListActivity.showInSnackBar("Select upto 10 words only");
        } else {
            makeQuiz1();
        }
    }

    public void makeQuiz1() {
        MyDialogs quizDialog = new MyDialogs(wordListActivity, this, wordList, selectedWordList);
        quizDialog.showDialog();
    }

    public void confirmDelete() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(wordListActivity);
        dialogBuilder.setMessage("Are you sure you want to delete selected items");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                itemDelete();
                exitSelectionMode();
            }
        });
        dialogBuilder.show();
    }

}

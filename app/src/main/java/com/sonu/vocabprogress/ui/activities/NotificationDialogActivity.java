package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.utilities.datahelpers.SQLiteHelper;

public class NotificationDialogActivity extends AppCompatActivity
        implements View.OnClickListener {
    EditText edtMeaning, edtDesc, edtWord;
    Button btnSave;
    String wordName, meaning, desc;
    SQLiteHelper db;
    Intent returnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }
        //Finish activity on touch outside of activity
        setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_notification_dialog);
        //window decoration
        windowDecor();
        //views initializations
        init();


        //Inteded entry point
        if (getIntent().getStringExtra("word") != null || getIntent().getStringExtra("edit") != null) {
            startForUpdate();
        } else {
            startForNewEntry();
        }

        //Satting listener on button
        btnSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View p1) {
        if (p1.getId() == R.id.id_btn_save) {
            onSaveBtnClick();
        }
    }


    //window configurations to make activity transparent
    private void windowDecor() {

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        window.setDimAmount(0.0f);
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        getSupportActionBar().hide();

    }

    //initialization
    private void init() {
        edtWord = findViewById(R.id.id_edt_word);
        edtMeaning = findViewById(R.id.id_edt_meaning);
        edtDesc = findViewById(R.id.id_edt_desc);
        btnSave = findViewById(R.id.id_btn_save);
        db = SQLiteHelper.getSQLiteHelper(this);
        returnIntent = new Intent();
    }

    //On save button click
    private void onSaveBtnClick() {
        meaning = edtMeaning.getText().toString().trim();
        desc = edtDesc.getText().toString().trim().isEmpty() ? "n/a" :
                edtDesc.getText().toString().trim();

        if ((wordName != null && wordName.isEmpty()) || (meaning != null && meaning.isEmpty())) {
            edtWord.setError("Provide a word");
            edtMeaning.setError("Provide word's meaning");
        } else if (getIntent().getStringExtra("word") != null || getIntent().getStringExtra("edit") != null) {
            if (db.updateData(new Word(wordName, meaning, desc))) {
                Toast.makeText(this, "Word added successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        } else {
            wordName = edtWord.getText().toString().trim();

            try {
                if (db.insertData(new Word(wordName, meaning, desc))) {
                    Toast.makeText(this, "Word added successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLiteConstraintException e) {
                edtWord.setError("Duplicate value");
            }
        }
    }

    //Activity start from notification action
    private void startForUpdate() {
        edtWord.setFocusable(false);
        if (getIntent().getStringExtra("word") != null) {
            wordName = getIntent().getStringExtra("word");
        } else {
            wordName = getIntent().getStringExtra("edit");
        }
        edtWord.setText(wordName);
        edtWord.setFocusable(false);

    }

    //activity start for adding new word
    private void startForNewEntry() {
        edtWord.setText("");
        edtWord.setHint("Enter word");
    }
}

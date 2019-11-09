package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.utilities.helpers.QuizWordHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayQuizActivity extends AppCompatActivity implements
        View.OnClickListener {
    int quizId, quizNumber = 0, totalQuiz = 10;
    List<Word> quizWords;
    List<String> randomWords;
    List<RadioButton> radioButtons;
    QuizWordHelper quizWordHelper;
    TextView tvQuestion, tvqQNumber;
    String text = "Select the appropriate option for the word: ";
    RadioGroup rgOptions;
    RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    Button btnNextQuiz, btnResult;
    int checkedId, rightOption, score;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);
        init();
        quizId = getQuizId();
        quizWords = getQuizWords();
        setQuiz();
        btnNextQuiz.setOnClickListener(this);
        btnResult.setOnClickListener(this);


    }

    public void init() {
        tvQuestion = findViewById(R.id.id_tv_question);
        rgOptions = findViewById(R.id.id_rg_o);
        rbOption1 = findViewById(R.id.id_rb_o1);
        rbOption2 = findViewById(R.id.id_rb_o2);
        rbOption3 = findViewById(R.id.id_rb_o3);
        rbOption4 = findViewById(R.id.id_rb_o4);
        radioButtons = new ArrayList<>();
        radioButtons.add(rbOption1);
        radioButtons.add(rbOption2);
        radioButtons.add(rbOption3);
        radioButtons.add(rbOption4);
        tvqQNumber = findViewById(R.id.id_tv_qNumber);
        btnNextQuiz = findViewById(R.id.id_btn_next);
        btnResult = findViewById(R.id.id_btn_result);
        quizWordHelper = QuizWordHelper.getInstance(this);
        randomWords = new ArrayList<>();
        loadWordsFromAssets();

    }

    @Override
    public void onClick(View p1) {
        if (p1.getId() == R.id.id_btn_next) {
            onClickNextButton(p1);
        } else if (p1.getId() == R.id.id_btn_result) {
            showResult();
        }
    }

    public void onClickNextButton(View v) {
        if (rgOptions.getCheckedRadioButtonId() == -1) {
            btnNextQuiz.setError("Must select an item");
        } else if (quizNumber == 10 || quizWords.size() == quizNumber) {
            btnNextQuiz.setError(null);
            saveSelection();
            btnNextQuiz.setEnabled(false);
            btnResult.setVisibility(View.VISIBLE);
        } else {
            btnNextQuiz.setError(null);
            saveSelection();
            setQuiz();
        }

    }

    public void saveSelection() {
        if (radioButtons.get(rightOption).getId() == rgOptions.getCheckedRadioButtonId()) {
            score = score + 1;
        }
    }

    public void showResult() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    public void setQuiz() {
        rgOptions.clearCheck();
        int a = 0;
        Word word = quizWords.get(quizNumber);
        tvqQNumber.setText(String.valueOf(1 + quizNumber) + "/" + String.valueOf(totalQuiz));
        tvQuestion.setText(text + word.getWordName());
        Random random = new Random();
        rightOption = (random.nextInt(4));
        radioButtons.get(rightOption).
                setText(word.getWordMeaning());
        a = random.nextInt(randomWords.size());
        for (int n = 0; n <= (radioButtons.size() - 1); n++) {
            if (n == rightOption) {
                continue;
            } else if (a + 1 >= randomWords.size()) {
                a = 0;
                radioButtons.get(n).setText(
                        randomWords.get(a));
            } else {
                a = a + 1;
                radioButtons.get(n).setText(
                        randomWords.get(a));
            }
        }
        quizNumber++;

    }

    public int getQuizId() {
        return getIntent().getExtras().getInt("quizId");
    }


    public void loadWordsFromAssets() {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(
                    getAssets().open("random_words.txt"));
            BufferedReader br = new BufferedReader(isr);
            String word = new String();
            while ((word = br.readLine()) != null) {
                randomWords.add(word);
            }
            isr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Word> getQuizWords() {
        ArrayList<Word> quizWords = new ArrayList<>();
        Cursor cursor = quizWordHelper.retrieveData(quizId);
        quizWords.clear();
        if (cursor.moveToFirst()) {
            do {
                quizWords.add(new Word(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        return quizWords;
    }


}


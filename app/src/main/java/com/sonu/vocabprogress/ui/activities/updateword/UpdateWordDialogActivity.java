package com.sonu.vocabprogress.ui.activities.updateword;

import android.content.ContentValues;
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
import com.sonu.vocabprogress.utilities.constants.Consts;
import com.sonu.vocabprogress.utilities.datahelpers.SQLiteHelper;

import java.io.Serializable;

public class UpdateWordDialogActivity extends AppCompatActivity
        implements View.OnClickListener ,UpdateWordContract.View{
    EditText edtMeaning, edtDesc, edtWord;
    Button btnSave;
    String wordName, meaning, desc;
    SQLiteHelper db;
    Intent returnIntent;
    Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }
        setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_notification_dialog);
        windowDecor();

        //init views
        edtWord = findViewById(R.id.id_edt_word);
        edtMeaning = findViewById(R.id.id_edt_meaning);
        edtDesc = findViewById(R.id.id_edt_desc);
        btnSave = findViewById(R.id.id_btn_save);


        //init
        db = SQLiteHelper.getSQLiteHelper(this);
        returnIntent = new Intent();
        if(getIntent().getAction()!=null&&getIntent().getAction().equals(Consts.Action.UPDATE_LOCAL.toString()) && getIntent()
                .getSerializableExtra(Consts.extras.WORD_SERIALIZABEL.toString())!=null){
            word=(Word)getIntent().getSerializableExtra(Consts.extras.WORD_SERIALIZABEL.toString());
            startForUpdate();
        }else {
            startForNewEntry();
        }

        btnSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View p1) {
        if (p1.getId() == R.id.id_btn_save) {
        if(edtWord.getText().toString().isEmpty()){
            edtWord.setError(getResources().getString(R.string.error_enter_word));
        }else if (edtMeaning.getText().toString().isEmpty()){
            edtMeaning.setError(getResources().getString(R.string.error_enter_meaning));
        }else{
            Word word1=new Word(edtWord.getText().toString(),edtMeaning.getText().toString(),
                    edtDesc.getText().toString());
            updateData(word1);
        }
        }
    }


    private void windowDecor() {
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        window.setDimAmount(0.0f);
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        getSupportActionBar().hide();
    }



    public void updateData(Word word){
        if(getIntent().getAction()!=null&&getIntent().getAction().equals(Consts.Action.UPDATE_LOCAL.toString())){
            db.updateData(word);
            finish();
        }else if (getIntent().getAction()!=null&&getIntent().getAction().equals(Consts.Action.UPDATE_CLOUD.toString())){

        }else {
            try {
                db.insertData(word);
            } catch (SQLiteConstraintException e) {
                edtWord.setError(getResources().getString(R.string.error_already_exists));
            }
        }
    }




    //Activity start from notification action
    private void startForUpdate() {
        edtWord.setFocusable(false);
        edtWord.setText(word.getWordName());
        edtMeaning.setText(word.getWordMeaning());
        edtDesc.setText(word.getWordDesc());
    }

    //activity start for adding new word
    private void startForNewEntry() {
        edtWord.setText("");
        edtWord.setHint("Enter word");
    }
    public void resultToParent(){
        Intent intent=new Intent(UpdateWordDialogActivity.this,getParent().getClass());
        intent.putExtra(Consts.extras.WORD_SERIALIZABEL.toString(),new Word(edtWord.getText().toString(),edtMeaning.getText().toString(),
                edtDesc.getText().toString()));
        setResult(RESULT_OK,intent);

    }
}

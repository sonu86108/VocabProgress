package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.ui.activities.updateword.UpdateWordDialogActivity;
import com.sonu.vocabprogress.ui.adapters.WordListAdapter;
import com.sonu.vocabprogress.utilities.constants.Consts;
import com.sonu.vocabprogress.utilities.datahelpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.OnGetDataListener;
import com.sonu.vocabprogress.utilities.datahelpers.QuizWordHelper;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.RecyclerViewTouchEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizWordsActivity extends AppCompatActivity implements RecyclerViewTouchEventListener {
    public static int REQUEST_CODE=123;
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
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    protected void onPostResume() {
        super.onPostResume();
        updateWordList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==REQUEST_CODE && data!=null){
            
        }
    }

    @Override
    public void onRecyclerViewItemClick(View v, final int p) {
          if(v.getId()==R.id.id_more_menu_word){
              PopupMenu menu=new PopupMenu(this,v);
              menu.getMenu().add(R.string.menu_item_edit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                  @Override
                  public boolean onMenuItemClick(MenuItem item) {
                      Intent intent=new Intent(QuizWordsActivity.this, UpdateWordDialogActivity.class);
                      intent.putExtra(Consts.extras.WORD_SERIALIZABEL.toString(),wordList.get(p));
                      intent.setAction(Consts.Action.UPDATE_CLOUD.toString());
                      startActivityForResult(intent,REQUEST_CODE);
                      return true;
                  }
              });
              menu.show();
          }
    }

    @Override
    public void onRecyclerViewItemLongClick(View v, ImageView menu, int p) {

    }

    public void updateWordList() {
        if(CloudDatabaseHelper.isSignedIn()){
            readData(quizId);
        }
    }

    private void readData(String quizId){
     cloudDatabaseHelper.readQuizWordsFromFirebase(quizId, new OnGetDataListener() {
         @Override
         public void onStart() {
             pbQuizWords.setVisibility(View.VISIBLE);
         }

         @Override
         public void onSuccess(List<?> list) {
             wordList.clear();
             wordList.addAll((ArrayList<Word>)list);
             pbQuizWords.setVisibility(View.GONE);
             wordListAdapter.notifyDataSetChanged();
         }

         @Override
         public void onFailure(String errorMessage) {
             pbQuizWords.setVisibility(View.GONE);
             toast(errorMessage);
         }
     });
    }
private void toast(String msg){
        Toast.makeText(QuizWordsActivity.this,msg,Toast.LENGTH_SHORT).show();
}

}

package com.sonu.vocabprogress.ui.activities.words;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.ui.activities.updateword.UpdateWordDialogActivity;
import com.sonu.vocabprogress.ui.activities.SettingsActivity;
import com.sonu.vocabprogress.ui.adapters.WordListAdapter;
import com.sonu.vocabprogress.utilities.AppUtils;
import com.sonu.vocabprogress.utilities.constants.Consts;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.RecyclerViewTouchEventListener;
import com.sonu.vocabprogress.utilities.datahelpers.SQLiteHelper;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.DataFetcher;
import java.util.ArrayList;
import java.util.List;


public class WordsActivity extends AppCompatActivity
        implements View.OnClickListener, RecyclerViewTouchEventListener {

    SelectionMode selectionMode;
    Toolbar toolbar;
    RecyclerView wordListRecyclerView;
    TextView tvMsg;
    List<Word> wordList;
    public WordListAdapter wordListAdapter;
    FloatingActionButton fabAddWord;
    SQLiteHelper db;
    ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);
        toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        setListeners();
    }

    public void init() {
        wordListRecyclerView = findViewById(R.id.id_recyclerview_wordlist);
        selectionMode = new SelectionMode(this);
        fabAddWord = findViewById(R.id.id_fab);
        tvMsg=findViewById(R.id.id_tv_msg);
        db = SQLiteHelper.getSQLiteHelper(this);
        viewModel=new ViewModel(this);
        wordList = new ArrayList<>();
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
                Intent intent=new Intent(this,UpdateWordDialogActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRecyclerViewItemClick(View v, int position) {
        if (selectionMode.isInSelectionMode()) {
            selectionMode.onClick(v, position);
        } else  if(v.getId()==R.id.id_more_menu_word){
            showPopUpMenu(v,position);
        }
    }

    @Override
    public void onRecyclerViewItemLongClick(View v, ImageView menu, int p) {
        selectionMode.enterInSelectionMode(fabAddWord,toolbar, wordList);
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
        }else if(item.getItemId()==R.id.id_menu_word_activity_settings){
            startActivity(new Intent(WordsActivity.this, SettingsActivity.class));
        }else if(item.getItemId()==R.id.id_menu_word_activity_help){

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
    protected void onPostResume() {
        super.onPostResume();
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

    private void setListeners() {
        fabAddWord.setOnClickListener(this);
    }



    public void updateWordList(){
        viewModel.fetchWordData(new DataFetcher() {
            @Override
            public void onStart() {
                wordListRecyclerView.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.GONE);
                wordList.clear();
            }

            @Override
            public void onSuccess(List<?> list) {
              wordList.addAll((ArrayList<Word>)list);
              wordListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {
               onDataNotFound();
            }
        });
    }

    private void  showPopUpMenu(View view , final int position){
        PopupMenu menuMore=new PopupMenu(this,view);
        menuMore.getMenuInflater().inflate(R.menu.menu_more_activity_word,menuMore.getMenu());
        menuMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.id_popup_menu_item_edit){
                    Intent intent=new Intent(WordsActivity.this, UpdateWordDialogActivity.class);
                    intent.putExtra(Consts.extras.WORD_SERIALIZABEL.toString(),wordList.get(position));
                    intent.setAction(Consts.Action.UPDATE_LOCAL.toString());
                    startActivity(intent);
                }else if (item.getItemId()==R.id.id_popup_menu_item_delete){
                    db.deleteData(wordList.get(position).getWordName());
                    updateWordList();
                }
                return true;
            }
        });
        menuMore.show();
    }


    public void showInSnackBar(String msg) {
        AppUtils.snackBar(findViewById(R.id.id_layout_activity_wordlist),msg);
    }

    public void showInToast(String msg) {
        Toast.makeText(WordsActivity.this, "", Toast.LENGTH_LONG).show();
    }
    private void onDataNotFound(){
        if(wordList.isEmpty()){
            wordListRecyclerView.setVisibility(View.GONE);
            tvMsg.setVisibility(View.VISIBLE);
        }
    }
}

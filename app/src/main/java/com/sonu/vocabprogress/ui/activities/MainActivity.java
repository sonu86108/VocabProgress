package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.services.ClipBoardListenerService;
import com.sonu.vocabprogress.ui.activities.words.WordListActivity;
import com.sonu.vocabprogress.utilities.AppUtils;
import com.sonu.vocabprogress.utilities.datahelpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.datahelpers.SQLiteHelper;
import com.sonu.vocabprogress.utilities.sharedprefs.AppPrefs;
import com.sonu.vocabprogress.utilities.tmp.AndroidDatabaseManager;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        Switch.OnCheckedChangeListener {
    ActionBar actionBar;
    CardView cardViewSettings, cardViewWordList, cardViewQuizes,
            cardViewHelp, cardViewPlayQuiz, cardViewProgress;
    SQLiteHelper db;
    Switch mainSwitch;
    AppPrefs appPrefs;
    TextView tvDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!CloudDatabaseHelper.isSignedIn()) finish();
        init();
        actionBar();
        setDisplayName();
        setOnclickListeners();
    }

    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.id_cardView_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;

            case R.id.id_cardView_WordList:
                Intent wordListIntent = new Intent(MainActivity.this, WordListActivity.class);
                startActivity(wordListIntent);
                break;
            case R.id.id_cardView_Quizes:
                Intent quizListIntent = new Intent(MainActivity.this,
                        QuizesActivity.class);
                startActivity(quizListIntent);
                break;
            case R.id.id_cardView_help:
                startActivity(new Intent(this, AndroidDatabaseManager.class));
                break;
            case R.id.id_cardView_playQuiz:
                Intent playQuizIntent = new Intent(MainActivity.this,
                        QuizesActivity.class);
                playQuizIntent.putExtra("play_mode", true);
                startActivity(playQuizIntent);
                break;
            case R.id.id_cardView_progress:
                AppUtils.snackBar(findViewById(R.id.id_layout_mainLayout),"Not Available!");
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton p1, boolean p2) {
        Intent serviceIntent=new Intent(this,ClipBoardListenerService.class);
        if (p1.isChecked()) {
            ContextCompat.startForegroundService(this,serviceIntent);
        } else {
            stopService(serviceIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.id_menu_item_settings){
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
        }
       return true;
    }

    private void init() {
        appPrefs=AppPrefs.getInstance(this);
        cardViewSettings = findViewById(R.id.id_cardView_settings);
        cardViewWordList = findViewById(R.id.id_cardView_WordList);
        cardViewQuizes = findViewById(R.id.id_cardView_Quizes);
        cardViewHelp = findViewById(R.id.id_cardView_help);
        cardViewPlayQuiz = findViewById(R.id.id_cardView_playQuiz);
        cardViewProgress = findViewById(R.id.id_cardView_progress);
        tvDisplayName = findViewById(R.id.id_tv_displayName);
        db = SQLiteHelper.getSQLiteHelper(this);
    }

    private void setOnclickListeners() {
        cardViewSettings.setOnClickListener(this);
        cardViewWordList.setOnClickListener(this);
        cardViewQuizes.setOnClickListener(this);
        cardViewHelp.setOnClickListener(this);
        cardViewPlayQuiz.setOnClickListener(this);
        cardViewProgress.setOnClickListener(this);
        mainSwitch.setOnCheckedChangeListener(this);
    }

    private void setDisplayName() {
        if(CloudDatabaseHelper.getUserDisplayName() !=null){
            tvDisplayName.setText(CloudDatabaseHelper.getUserDisplayName());
        }
    }


    private void actionBar() {
        if (getSupportActionBar() != null) {
            actionBar = getSupportActionBar();
            actionBar.setElevation(0);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.app_bar_custom_view_switch);
            mainSwitch = actionBar.getCustomView().findViewById(R.id.app_bar_custom_view_switchSwitch);
            loadUserPrefs();
        }
    }

    private void loadUserPrefs() {
        if(ClipBoardListenerService.isRunning){
            mainSwitch.setChecked(true);
        }else {
            mainSwitch.setChecked(false);
        }
    }


}

package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.services.ClipBoardListenerService;
import com.sonu.vocabprogress.utilities.helpers.SQLiteHelper;
import com.sonu.vocabprogress.utilities.sharedprefs.Prefs;
import com.sonu.vocabprogress.utilities.tmp.AndroidDatabaseManager;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        Switch.OnCheckedChangeListener {
    ActionBar actionBar;
    CardView cardViewSettings, cardViewWordList, cardViewQuizes,
            cardViewHelp, cardViewPlayQuiz, cardViewProgress;
    Intent serviceIntent;
    SQLiteHelper db;
    Word word;
    Switch mainSwitch;
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedPrefEditor;
    TextView tvDisplayName;
    boolean isOnlineMode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        actionBar();
        if(checkAppMode()){
            isOnlineMode=true;
            setDislplayName();
        }
        setOnclickListners();
    }

    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.id_cardView_settings:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
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
                showInSnackbar("Sorry, Not Available");
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton p1, boolean p2) {
        if (p1.isChecked()) {
            startService(serviceIntent);
            sharedPrefEditor.putBoolean(Prefs.AppSettings.MAIN_SWITCH_STATUS.toString(), true).apply();
        } else {
            stopService(serviceIntent);
            sharedPrefEditor.putBoolean(Prefs.AppSettings.MAIN_SWITCH_STATUS.toString(), false).apply();
            Toast.makeText(MainActivity.this, "ClipBoardListenerService stopped", Toast.LENGTH_LONG).
                    show();
        }
    }

    private void init() {
        sharedPref = this.getSharedPreferences(Prefs.SharedPrefs.APP_SETTINGS.toString(), MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        cardViewSettings = findViewById(R.id.id_cardView_settings);
        cardViewWordList = findViewById(R.id.id_cardView_WordList);
        cardViewQuizes = findViewById(R.id.id_cardView_Quizes);
        cardViewHelp = findViewById(R.id.id_cardView_help);
        cardViewPlayQuiz = findViewById(R.id.id_cardView_playQuiz);
        cardViewProgress = findViewById(R.id.id_cardView_progress);
        tvDisplayName = findViewById(R.id.id_tv_displayName);
        db = SQLiteHelper.getSQLiteHelper(this);
        serviceIntent = new Intent(MainActivity.this, ClipBoardListenerService.class);
    }

    private void setOnclickListners() {
        cardViewSettings.setOnClickListener(this);
        cardViewWordList.setOnClickListener(this);
        cardViewQuizes.setOnClickListener(this);
        cardViewHelp.setOnClickListener(this);
        cardViewPlayQuiz.setOnClickListener(this);
        cardViewProgress.setOnClickListener(this);
        mainSwitch.setOnCheckedChangeListener(this);
    }

    private void setDislplayName() {
        if(FirebaseAuth.getInstance().getCurrentUser() !=null && FirebaseAuth.getInstance().getCurrentUser().getDisplayName() !=null){
            tvDisplayName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void showInSnackbar(String msg) {
        Snackbar.make(findViewById(R.id.id_layout_mainLayout), msg, Snackbar.
                LENGTH_SHORT).show();
    }
    private boolean checkAppMode(){
        if( sharedPref.getString(Prefs.AppSettings.APP_MODE.toString(),null) !=null &&
                sharedPref.getString(Prefs.AppSettings.APP_MODE.toString(),"n/a").equals(Prefs.AppSettings.ONLINE.toString())){
            return true;
        }else {
            return false;
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
        if (sharedPref.getBoolean(Prefs.AppSettings.MAIN_SWITCH_STATUS.toString(), false)) {
            mainSwitch.setChecked(true);
        } else {
            mainSwitch.setChecked(false);
        }
    }


}

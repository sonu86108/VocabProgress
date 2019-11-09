package com.sonu.vocabprogress.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import  com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Quiz;
import com.sonu.vocabprogress.models.QuizWord;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.utilities.helpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.helpers.QuizHelper;
import com.sonu.vocabprogress.utilities.helpers.QuizWordHelper;
import com.sonu.vocabprogress.utilities.sharedprefs.Prefs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    CardView cardViewSignOut;
    TextView tvSignInText;
    QuizHelper quizHelper;
    QuizWordHelper quizWordHelper;
    CloudDatabaseHelper cloudDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        AppMode();
        setOnClickListeners();
    }

    private  void init(){
        quizHelper=QuizHelper.getInstance(this);
        cloudDatabaseHelper=CloudDatabaseHelper.getInstance();
        quizWordHelper=QuizWordHelper.getInstance(this);
        cardViewSignOut=findViewById(R.id.id_signOut);
        tvSignInText=findViewById(R.id.id_tv_signInText);
    }

    private void setOnClickListeners(){
        cardViewSignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_signOut:
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    signIn();
                }else {
                    signOut();
                }
                break;
        }
    }

    private void AppMode(){
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            tvSignInText.setText("Sign out");
        }
    }


    private void signIn(){
    startLoginActivity();
    }

    private  void signOut(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
            GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(this,gso);
            googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                    startLoginActivity();
                    }
                }
            });
        }
    }
    private void startLoginActivity(){
        unsetSharedPrefs();
        Intent intent=new Intent(SettingsActivity.this,LoginActivity.class);
        intent.putExtra("signIn",true);
        startActivity(intent);
        finish();
    }

    private void unsetSharedPrefs(){
        SharedPreferences.Editor editor=getSharedPreferences(Prefs.SharedPrefs.APP_SETTINGS.toString(),MODE_PRIVATE).edit();
        editor.putString(Prefs.AppSettings.APP_MODE.toString(),null).apply();
    }

    private void toast(String msg){
        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void snackBar(String msg){
        Snackbar.make(findViewById(R.id.id_layout_activity_settings),msg,Snackbar.LENGTH_LONG).show();
    }
}

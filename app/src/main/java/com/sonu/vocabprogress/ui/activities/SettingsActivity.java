package com.sonu.vocabprogress.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import  com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.utilities.helpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.helpers.QuizHelper;
import com.sonu.vocabprogress.utilities.helpers.QuizWordHelper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                if(CloudDatabaseHelper.isSignedIn()){
                    signIn();
                }else {
                    signOut();
                }
                break;
        }
    }

    private void AppMode(){
        if(CloudDatabaseHelper.isSignedIn()){
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
        Intent intent=new Intent(SettingsActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}

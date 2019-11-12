package com.sonu.vocabprogress.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.utilities.helpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.sharedprefs.Prefs;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {
    private final int RC_SIGN_IN = 123;
    private ProgressBar pbLogin;
    private SignInButton btnSignIn;
    private TextView tvSkip;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        sharedPrefs = getSharedPreferences(Prefs.SharedPrefs.APP_SETTINGS.toString(),MODE_PRIVATE);
        mAuth = CloudDatabaseHelper.getFirebaseAuth();
        tvSkip=findViewById(R.id.id_tv_skip);
        pbLogin = findViewById(R.id.id_pb_login);
        btnSignIn = findViewById(R.id.id_btn_signIn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnSignIn.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
    }

    private void init(){
        CloudDatabaseHelper cloudDatabaseHelper=CloudDatabaseHelper.getInstance();
    }
    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.id_btn_signIn:
                signIn();
                break;
            case R.id.id_tv_skip:
                finish();
                //startHomeActivity(Prefs.AppSettings.OFFLINE.toString());
                break;
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        pbLogin.setVisibility(View.VISIBLE);
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        pbLogin.setVisibility(View.GONE);
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        pbLogin.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pbLogin.setVisibility(View.GONE);
                                startHomeActivity(Prefs.AppSettings.ONLINE.toString());
                        } else {
                            showInToast(task.getException().getMessage());
                            pbLogin.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void showInToast(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
    }


    private void startHomeActivity(String mode) {
        sharedPrefs.edit().putString(Prefs.AppSettings.APP_MODE.toString(),mode).apply();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

    }

    private void startHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(sharedPrefs.getString(Prefs.AppSettings.APP_MODE.toString(),null) != null){
            if (mAuth.getCurrentUser() != null && sharedPrefs.getString(Prefs.AppSettings.APP_MODE.toString(),null)
                    .contentEquals(Prefs.AppSettings.ONLINE.toString())) {
                startHomeActivity();
            }else if(sharedPrefs.getString(Prefs.AppSettings.APP_MODE.toString(),null).contentEquals(Prefs.AppSettings.OFFLINE.toString())){
            }
        }



    }


}

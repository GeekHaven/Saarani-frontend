package com.example.calendarapp.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.calendarapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

public class LoginActivity extends AppCompatActivity {
    Button studentSignIn;
    ConstraintLayout constraintLayout;
    CircularProgressBar circularProgressBar;
    private static final int RC_SIGN_IN = 234;
    private static final String TAG = "simplifiedcoding";
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        circularProgressBar=findViewById(R.id.loader);
        studentSignIn=findViewById(R.id.studentSignIn);
        constraintLayout=findViewById(R.id.layout);
        mAuth = FirebaseAuth.getInstance();
//        progressDialog=new ProgressDialog(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("271594298370-jmsnpsmnhm1ahm6viiag2gi2dnpqn0lg.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        studentSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraintLayout.setVisibility(View.INVISIBLE);
                Auth.GoogleSignInApi.signOut(mGoogleSignInClient.asGoogleApiClient());
                signIn();
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
//        if (mAuth.getCurrentUser() != null) {
//            finish();
//            Intent intent=new Intent(this, MainActivity2.class);
//            intent.putExtra("action","db");
//            startActivity(intent);
//        }
    }

    @Override
    public void onBackPressed() {
        if(mAuth.getCurrentUser()==null){
            Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
            homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
//            progressDialog.dismiss();
            ((CircularProgressDrawable)circularProgressBar.getIndeterminateDrawable()).stop();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account.getEmail().contains("iiita.ac.in") || account.getEmail().equals("varunbhardwaj.064@gmail.com"))
                    firebaseAuthWithGoogle(account);
                else
                    Toast.makeText(LoginActivity.this,"Please Sign in using your College ID",Toast.LENGTH_LONG).show();
            } catch (ApiException e) {
                constraintLayout.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "Sign In Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        ((CircularProgressDrawable)circularProgressBar.getIndeterminateDrawable()).start();
//        progressDialog.setTitle("Loading...");
//        progressDialog.setMessage("Signing In Using Google");
//        progressDialog.show();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(LoginActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplication(), SubscribeActivity.class);
                            intent.putExtra("val",1);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                        else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            ((CircularProgressDrawable)circularProgressBar.getIndeterminateDrawable()).stop();
                            constraintLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
//                        progressDialog.dismiss();
                    }
                });
    }
    private void signIn() {
        circularProgressBar.setVisibility(View.VISIBLE);
        ((CircularProgressDrawable)circularProgressBar.getIndeterminateDrawable()).start();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}

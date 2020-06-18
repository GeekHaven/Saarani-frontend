package com.example.calendarapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView username;
    String idTocken;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("271594298370-jmsnpsmnhm1ahm6viiag2gi2dnpqn0lg.apps.googleusercontent.com")
                .requestEmail()
                .build();
        username=findViewById(R.id.username);
        username.setText(idTocken);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        final FirebaseUser user= mAuth.getCurrentUser();
        Log.d("XXX","HELLO FRANDS");
        if(user!=null) {
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                username.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getToken()));
                                Log.d("VARUN BHARDWAJ IDTOKEN",Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getToken()));
                            } else
                                idTocken = "333";
                        }
                    });
        }
        assert user != null;
        if(user.getEmail()!=null&&user.getEmail().length()!=22) {
            LogoutWrong();
        }
        else if(user.getEmail()!=null&&user.getEmail().length()==22){
            if(!user.getEmail().toLowerCase().contains("iiita.ac.in"))
                LogoutWrong();
        }


        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("User Activity");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        FirebaseMessaging.getInstance().subscribeToTopic("Event")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("VARUN", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
        homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
        homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreenIntent);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.log_out) {
            Logout();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            username.setText("");
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
    private void Logout(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,"LogOut Successful",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }
                });
    }
    private void LogoutWrong(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,"Please Login with College ID",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }
                });
    }
}

package com.example.calendarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;

import com.example.calendarapp.AdaptorActivity.ViewHolder;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.zip.Inflater;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    TextView tvStudentName, tvStudentEmailId;
    NavController navController;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        drawerLayout=findViewById(R.id.drawerMainActivity);
        navigationView=findViewById(R.id.navigation_main);

        tvStudentName=navigationView.getHeaderView(0).findViewById(R.id.tvNavHeaderStudentName);
        tvStudentEmailId=navigationView.getHeaderView(0).findViewById(R.id.tvNavHeaderStudentId);
        imgUser=navigationView.getHeaderView(0).findViewById(R.id.imgNavHeaderUser);

        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            Picasso
                    .get()
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .resize(92,92)
                    .transform(new CropCircleTransformation())
                    .into(imgUser);
        }

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        tvStudentName.setText(user.getDisplayName());
        tvStudentEmailId.setText(user.getEmail());

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAppBarConfiguration=new AppBarConfiguration.
                Builder(R.id.navigation_nav_main, R.id.navigation_nav_profile, R.id.navigation_nav_subscription, R.id.navigation_nav_society)
                .setDrawerLayout(drawerLayout).build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_log_out: {
                        signOut();
                        break;
                    }
                    case R.id.nav_home:{
                        navController.navigate(R.id.navigation_nav_main);
                        break;
                    }
                    case R.id.nav_subscription:{
                        navController.navigate(R.id.navigation_nav_main);
                        navController.navigate(R.id.navigation_nav_subscription);
                        break;
                    }
                    case R.id.nav_profile:{
                        navController.navigate(R.id.navigation_nav_main);
                        navController.navigate(R.id.navigation_nav_profile);
                        break;
                    }
                    case R.id.nav_societies:{
                        navController.navigate(R.id.navigation_nav_main);
                        navController.navigate(R.id.navigation_nav_society);
                        break;
                    }
                }
                drawerLayout.close();
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }
}
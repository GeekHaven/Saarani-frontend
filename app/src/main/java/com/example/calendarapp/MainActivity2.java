package com.example.calendarapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.calendarapp.AdaptorActivity.ViewHolder;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    int x=0;
    String frag="";
    Boolean visibility=false;
    TextView tvStudentName, tvStudentEmailId;
    Toolbar toolbar;
    NavController navController;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView imgUser;
    MenuItem addEvent,swap;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private CardView cardView;
    private RecyclerView.Adapter adapter;
    private List<ListItems> listItems;
    private static String URL_DATA="https://socupdate.herokuapp.com/events";
    private static String url ="https://socupdate.herokuapp.com/societies/check";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("toolbar-visibility"));
        mAuth=FirebaseAuth.getInstance();
        Intent intent =getIntent();
//        if(Objects.requireNonNull(intent.getExtras()).containsKey("Fragment")){
//            frag="profile";
//        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("271594298370-jmsnpsmnhm1ahm6viiag2gi2dnpqn0lg.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        subscribeToTopic();
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

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Integer.parseInt("232323"));



        mAppBarConfiguration=new AppBarConfiguration.
                Builder(R.id.navigation_nav_main, R.id.navigation_nav_profile, R.id.navigation_nav_list,R.id.navigation_nav_soc_profile, R.id.navigation_nav_society)
                .setDrawerLayout(drawerLayout).build();
        
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Bundle bundle =new Bundle();
        if(intent.getExtras()!=null && intent.getExtras().containsKey("action")) {
            if(intent.getExtras().getString("action").equals("db")) {
                bundle.putString("loadFrom", "db");
                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                if(prefs.getString("society", "false").equals("true")) {
                    visibility=true;
                }
                else
                    visibility=false;
            }
            else {
                bundle.putString("loadFrom", "server");
                sendIdToken();
            }
        }
        else if(intent.getExtras()==null){
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            if(prefs.getString("society", "false").equals("true")) {
                visibility=true;
            }
            else
                visibility=false;
        }
//        toolbar.setVisibility(View.INVISIBLE);
        navController.setGraph(R.navigation.mobile_navigation,bundle);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(intent.getExtras()!=null&&intent.getExtras().containsKey("Fragment")){
            if(intent.getExtras().getString("Fragment").equals("profile")) {
                toolbar.setBackgroundColor(Color.rgb(59, 59, 59));
                onCreateOptionsMenu(toolbar.getMenu());
                navController.navigate(R.id.navigation_nav_profile);
                addEvent.setVisible(false);
                swap.setVisible(false);

            }
            else if(intent.getExtras().getString("Fragment").equals("list")){
                navController.navigate(R.id.navigation_nav_list);
                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                if(prefs.getString("society", "false").equals("true")) {
                    visibility=true;
                }
                else
                    visibility=false;
            }
        }
        if(intent.getExtras()!=null&&intent.getExtras().containsKey("back")){
            navController.navigate(R.id.navigation_nav_soc_profile);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_log_out: {
                            signOut();
                            break;
                        }
                        case R.id.nav_home: {
                            toolbar.setBackgroundColor(Integer.parseInt("232323"));
                            swap.setVisible(true);
                            SharedPreferences preferences =getSharedPreferences("user",MODE_PRIVATE);
                            if(preferences.getString("society", "false").equals("true"))
                                addEvent.setVisible(true);
                            if(navController.getCurrentDestination().getId()!=R.id.navigation_nav_main&&navController.getCurrentDestination().getId()!=R.id.navigation_nav_list)
                                navController.navigate(R.id.navigation_nav_main);
                            break;
                        }
                        case R.id.nav_profile: {
                            toolbar.setBackgroundColor(Color.rgb(59,59,59));
                            swap.setVisible(false);
                            addEvent.setVisible(false);
                            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                            if(prefs.getString("society", "false").equals("false")) {
                                if (navController.getCurrentDestination().getId() != R.id.navigation_nav_profile)
                                    navController.navigate(R.id.navigation_nav_profile);
                            }
                            else{
                                if (navController.getCurrentDestination().getId() != R.id.navigation_nav_soc_profile)
                                    navController.navigate(R.id.navigation_nav_soc_profile);
                            }
                            break;
                        }
                        case R.id.nav_societies: {
                            toolbar.setBackgroundColor(Integer.parseInt("232323"));
                            swap.setVisible(false);
                            addEvent.setVisible(false);
                            if(navController.getCurrentDestination().getId()!=R.id.navigation_nav_society)
                                navController.navigate(R.id.navigation_nav_society);
                            break;
                        }
                    }
                drawerLayout.close();
                return false;
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if(message.equals("true")){
                toolbar.setVisibility(View.VISIBLE);
            }
            else{
                toolbar.setVisibility(View.INVISIBLE);
            }
        }
    };

    public void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("Event")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to Event";
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("VARUN", msg);
                    }
                });
        FirebaseMessaging.getInstance().subscribeToTopic("iit2019091")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to Event";
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("VARUN", msg);
                    }
                });
    }
    public void sendIdToken(){
//        final TextView textView = findViewById(R.id.check);
        final String[] token = new String[1];
        mAuth=FirebaseAuth.getInstance();
        final FirebaseUser user= mAuth.getCurrentUser();
        if(user!=null) {
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("VARUN BHARDWAJ IDTOKEN", Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getToken()));
                                token[0] =task.getResult().getToken();
//                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                                ClipData clip = ClipData.newPlainText("idtoken", token[0]);
//                                clipboard.setPrimaryClip(clip);
                                HashMap<String,String> map=new HashMap<String, String>();
                                map.put("token",token[0]);
                                final ProgressDialog progressDialog = new ProgressDialog(MainActivity2.this);
                                progressDialog.setMessage("Checking user...");
                                progressDialog.show();
                                RequestQueue requstQueue = Volley.newRequestQueue(MainActivity2.this);
                                progressDialog.dismiss();

                                JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(map),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
//                                                    textView.setText(response.getString("society"));
                                                    if(response.getString("society").equals("true")){
                                                        addEvent.setVisible(true);
                                                        visibility=true;
                                                        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                                        editor.putString("society", "true");
                                                        editor.apply();
                                                    }
                                                    else{
                                                        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                                        editor.putString("society", "false");
                                                        editor.apply();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(MainActivity2.this,"Society check failed!",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                                requstQueue.add(jsonobj);

                            } else
                                token[0] = "0";
                        }
                    });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        addEvent=menu.findItem(R.id.add_event);
        swap=menu.findItem(R.id.calendar);
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("Fragment")){
            if(getIntent().getExtras().getString("Fragment").equals("profile")){
                addEvent.setVisible(false);
                visibility=false;
                swap.setVisible(false);
            }
            else {
                swap.setIcon(R.drawable.ic_calendar_home);
            }
        }
        addEvent.setVisible(visibility);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
        if(item.getItemId()==R.id.add_event){
            Intent intent =new Intent(MainActivity2.this,AddEventActivity.class);
            intent.putExtra("type","add");
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.calendar){
            if(navController.getCurrentDestination().getId()!=R.id.navigation_nav_list) {
                navController.navigate(R.id.navigation_nav_list);
                swap.setIcon(R.drawable.ic_calendar_home);
            }
            else {
                Bundle bundle=new Bundle();
                bundle.putString("action","db");
                navController.navigate(R.id.navigation_nav_main,bundle);
                swap.setIcon(R.drawable.ic_list_view);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void signOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity2.this,"LogOut Successful",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity2.this, LoginActivity.class));

                    }
                });
    }
}
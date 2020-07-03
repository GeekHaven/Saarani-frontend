package com.example.calendarapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
//import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    MenuItem addEvent;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private CardView cardView;
    private RecyclerView.Adapter adapter;
    private List<ListItems> listItems;
    private static String URL_DATA="https://socupdate.herokuapp.com/events";
    private static String url ="https://socupdate.herokuapp.com/societies/check";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AndroidThreeTen.init(this);
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("271594298370-jmsnpsmnhm1ahm6viiag2gi2dnpqn0lg.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        subscribeToTopic();
        sendIdToken();
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("User Activity");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//        recyclerView=findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems=new ArrayList<>();
//        loadRecyclerViewData();

    }
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
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("idtoken", token[0]);
                                clipboard.setPrimaryClip(clip);
                                HashMap<String,String> map=new HashMap<String, String>();
                                map.put("token",token[0]);
                                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,R.style.Theme_AppCompat);
                                progressDialog.setMessage("Loading data....");
                                progressDialog.show();
                                RequestQueue requstQueue = Volley.newRequestQueue(MainActivity.this);

                                JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(map),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
//                                                    textView.setText(response.getString("society"));
                                                    if(response.getString("society").equals("true")){
                                                        addEvent.setVisible(true);
                                                    }
                                                    progressDialog.dismiss();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

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
    public void loadRecyclerViewData(){
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.Theme_AppCompat);
        progressDialog.setMessage("Loading data....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    Iterator<String> iter = jsonObject.keys();
                    while (iter.hasNext()){
                        String key =iter.next();
                        JSONObject event=jsonObject.getJSONObject(key);
                        ListItems items=new ListItems(
                            event.getString("name"),
                            event.getString("byName"),
                            event.getString("desc"),
                            event.getString("venue"),
                            event.getString("time"),
                            event.getString("date")
                        );
                        listItems.add(items);
                    }
                    adapter = new AdaptorActivity(listItems, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        addEvent=menu.findItem(R.id.add_event);
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

        if(item.getItemId()==R.id.add_event){
            startActivity(new Intent(MainActivity.this,AddEventActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
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
}

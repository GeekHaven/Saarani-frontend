package com.example.calendarapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static com.example.calendarapp.R.drawable.star_img;
import static com.example.calendarapp.R.drawable.star_yellow;
import static com.example.calendarapp.R.drawable.tick;
import static com.example.calendarapp.R.drawable.tick_yellow;

public class EventActivity extends AppCompatActivity {
    TextView eventName,desc,date,venue,byName;
    String eventId;
    ImageView download_url,button_back;
    String screen;
    ConstraintLayout constraintLayout;
    String marker="";
    LinearLayout layout_attachment;
    ArrayList<String> arrayList=new ArrayList<>();
    ArrayList<String> nameList =new ArrayList<>();
    ImageView star,tick_mark;
    TextView interested,going,attachment_text;
    private ProgressDialog pDialog;
    DatabaseHandler databaseHandler;
    HashMap<String,String> mapUrl= new HashMap<>();
    private int i=0;
    public static final int progress_bar_type = 0;
    final SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
    final SimpleDateFormat format = new SimpleDateFormat("MMMM", Locale.getDefault());
    final SimpleDateFormat d = new SimpleDateFormat("dd", Locale.getDefault());
    final SimpleDateFormat y = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy",Locale.getDefault());
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        databaseHandler=new DatabaseHandler(this);
        constraintLayout=findViewById(R.id.layout);
        eventName=findViewById(R.id.name);
        desc=findViewById(R.id.desc);
        date=findViewById(R.id.date);
        venue=findViewById(R.id.venue);
        byName=findViewById(R.id.society_name);
        star=findViewById(R.id.star);
        tick_mark=findViewById(R.id.tick);
        interested=findViewById(R.id.text_interested);
        going=findViewById(R.id.text_going);
        button_back=findViewById(R.id.button);
        layout_attachment=findViewById(R.id.attachmentLayout);
        attachment_text=findViewById(R.id.attachment_text);
        attachment_text.setVisibility(View.GONE);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent =getIntent();
        eventId=intent.getExtras().getString("eventId");
        screen=intent.getExtras().getString("screen");
        if(intent.getExtras().getString("type").equals("notif")){
              setItems();
        }
        else {
            marker=intent.getExtras().getString("marker");
//            Toast.makeText(EventActivity.this,marker,Toast.LENGTH_SHORT).show();
            arrayList=intent.getStringArrayListExtra("attachments");
            nameList=intent.getStringArrayListExtra("attachments_name");
            for(int i=0;i<nameList.size();i++){
                StringBuilder convertString = new StringBuilder(nameList.get(i));
                for(int y=0;y<nameList.get(i).length();y++){
                    if(nameList.get(i).charAt(y)=='-'){
                        convertString.setCharAt(y, '.');
                    }
                }
                nameList.set(i,convertString.toString());
            }
            setIcon();
            String venue_event = intent.getExtras().getString("venue");
            String time_event = intent.getExtras().getString("time");
            String dateEvent = intent.getExtras().getString("date");
            Date date_event = null;
            try {
                date_event = dateFormat.parse(dateEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            eventName.setText(intent.getExtras().getString("name"));
            byName.setText(intent.getExtras().getString("byName"));
            desc.setText(intent.getExtras().getString("desc"));
            Linkify.addLinks(desc , Linkify.WEB_URLS);
            venue.setText(venue_event.substring(7));
            date.setText(sdf.format(date_event).substring(0, 3) + ", " + d.format(date_event) + " " + format.format(date_event) + " " + y.format(date_event) + " "+time_event.substring(6));
        }
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        if(prefs.getString("society", "false").equals("false")) {
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = star.getTag();
                    if (tag != null && (Integer) tag == star_yellow) {
                        Snackbar.make(constraintLayout, "Unmarked", Snackbar.LENGTH_LONG).show();
                        star.setTag(R.drawable.star_img);
                        star.setImageResource(R.drawable.star_img);
                        try {
                            databaseHandler.updateCount(databaseHandler.getEvent(eventId),"-","interested");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            databaseHandler.updateMarker(databaseHandler.getEvent(eventId),"none");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        deleteRequest();
                    } else {
                        Snackbar.make(constraintLayout, "Marked as interested.", Snackbar.LENGTH_LONG).show();
                        star.setTag(star_yellow);
                        star.setImageResource(star_yellow);
                        String x="-";
                        if ((Integer) tick_mark.getTag() == tick_yellow) {
                            tick_mark.setTag(tick);
                            tick_mark.setImageResource(R.drawable.going_man);
                            x="going";
                        }
                        try {
                            databaseHandler.updateCount(databaseHandler.getEvent(eventId),"interested",x);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            databaseHandler.updateMarker(databaseHandler.getEvent(eventId),"interested");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addMarker("interested");
                    }
                }
            });
            interested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = star.getTag();
                    if (tag != null && (Integer) tag == star_yellow) {
                        Snackbar.make(constraintLayout, "Unmarked", Snackbar.LENGTH_LONG).show();
                        star.setTag(R.drawable.star_img);
                        star.setImageResource(R.drawable.star_img);
                        try {
                            databaseHandler.updateCount(databaseHandler.getEvent(eventId),"-","interested");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            databaseHandler.updateMarker(databaseHandler.getEvent(eventId),"none");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        deleteRequest();
                    } else {
                        Snackbar.make(constraintLayout, "Marked as interested", Snackbar.LENGTH_LONG).show();
                        star.setTag(star_yellow);
                        star.setImageResource(star_yellow);
                        String x="-";
                        if ((Integer) tick_mark.getTag() == tick_yellow) {
                            tick_mark.setTag(tick);
                            tick_mark.setImageResource(R.drawable.going_man);
                            x="going";
                        }
                        try {
                            databaseHandler.updateCount(databaseHandler.getEvent(eventId),"interested",x);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            databaseHandler.updateMarker(databaseHandler.getEvent(eventId),"interested");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addMarker("interested");
                    }
                }
            });
            tick_mark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = tick_mark.getTag();
                    if (tag != null && (Integer) tag == tick_yellow) {
                        Snackbar.make(constraintLayout, "Unmarked", Snackbar.LENGTH_LONG).show();
                        tick_mark.setTag(tick);
                        tick_mark.setImageResource(R.drawable.going_man);
                        try {
                            databaseHandler.updateCount(databaseHandler.getEvent(eventId),"-","going");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            databaseHandler.updateMarker(databaseHandler.getEvent(eventId),"none");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        deleteRequest();
                    } else {
                        Snackbar.make(constraintLayout, "Marked as going.", Snackbar.LENGTH_LONG).show();
                        tick_mark.setTag(tick_yellow);
                        tick_mark.setImageResource(R.drawable.going_man_yellow);
                        String x="-";
                        if ((Integer) star.getTag() == star_yellow) {
                            star.setTag(R.drawable.star_img);
                            star.setImageResource(R.drawable.star_img);
                            x="interested";
                        }
                        try {
                            databaseHandler.updateCount(databaseHandler.getEvent(eventId),"going",x);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            databaseHandler.updateMarker(databaseHandler.getEvent(eventId),"going");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addMarker("going");
                    }
                }
            });
            going.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = tick_mark.getTag();
                    if (tag != null && (Integer) tag == tick_yellow) {
                        Snackbar.make(constraintLayout, "Unmarked", Snackbar.LENGTH_LONG).show();
                        tick_mark.setTag(tick);
                        tick_mark.setImageResource(R.drawable.going_man);
                        try {
                            databaseHandler.updateCount(databaseHandler.getEvent(eventId),"-","going");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            databaseHandler.updateMarker(databaseHandler.getEvent(eventId),"none");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        deleteRequest();
                    } else {
                        Snackbar.make(constraintLayout, "Marked as going.", Snackbar.LENGTH_LONG).show();
                        tick_mark.setTag(tick_yellow);
                        tick_mark.setImageResource(R.drawable.going_man_yellow);
                        String x="-";
                        if ((Integer) star.getTag() == star_yellow) {
                            star.setTag(R.drawable.star_img);
                            star.setImageResource(R.drawable.star_img);
                            x="interested";
                        }
                        try {
                            databaseHandler.updateCount(databaseHandler.getEvent(eventId),"going",x);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            databaseHandler.updateMarker(databaseHandler.getEvent(eventId),"going");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addMarker("going");
                    }
                }
            });
        }
        else{
            star.setVisibility(View.GONE);
            interested.setVisibility(View.GONE);
            going.setVisibility(View.GONE);
            tick_mark.setVisibility(View.GONE);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addAttachmentList(String url,int count,String nameOfAttachment){
        attachment_text.setVisibility(View.VISIBLE);
        final LinearLayout linearLayout = new LinearLayout(this);
        final int id = View.generateViewId();
        mapUrl.put(String.valueOf(id),url);
        linearLayout.setId(id);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        Resources r = EventActivity.this.getResources();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                r.getDisplayMetrics()
        );
        layout_attachment.addView(linearLayout,params);
        TextView name= new TextView(this);
        name.setSingleLine();
        name.setTextSize(14);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/montserratmedium.ttf");
        name.setTypeface(face);
        name.setTextColor(Color.WHITE);
        name.setText(nameOfAttachment);
        name.setPadding(16,0,16,0);
        name.setBackgroundResource(R.drawable.attachment_background);
        name.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        250,
                        r.getDisplayMetrics()),(int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40,
                r.getDisplayMetrics()));
//        textParams.gravity= Gravity.CENTER;
//        name.setGravity(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(name,0,textParams);
        ImageView imageView =new ImageView(this);
        imageView.setPadding(30,30,30,30);
        imageView.setImageResource(R.drawable.download);
        imageView.setBackgroundResource(R.drawable.ellipse_dark);
        LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        42,
                        r.getDisplayMetrics()
                ),
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        42,
                        r.getDisplayMetrics()
                ));
        paramsBtn.leftMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                r.getDisplayMetrics()
        );
        paramsBtn.gravity = Gravity.CENTER;
        linearLayout.addView(imageView,1,paramsBtn);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl.get(String.valueOf(linearLayout.getId()))));
                startActivity(browserIntent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setIcon(){
        Log.d("markxxx",marker);
        if(marker.equals("interested")){
            star.setImageResource(star_yellow);
            star.setTag(star_yellow);
        }
        else if(marker.equals("going")){
            tick_mark.setImageResource(R.drawable.going_man_yellow);
            tick_mark.setTag(tick_yellow);
            Log.d("set","true");
        }
        else{
            star.setTag(star_img);
            tick_mark.setTag(tick);
        }
        if(arrayList.size()!=0) {
            layout_attachment.setVisibility(View.VISIBLE);
            for (int i = 0; i < arrayList.size(); i++) {
                addAttachmentList(arrayList.get(i), i,nameList.get(i));
            }
        }
    }
    @Override
    public void onBackPressed(){
        if(screen.equals("home")) {
            Intent intent=new Intent(EventActivity.this, MainActivity2.class);
            intent.putExtra("action","db");
            startActivity(intent);
        }
        else if(screen.equals("profile")){
           Intent intent= new Intent(EventActivity.this,MainActivity2.class);
           intent.putExtra("Fragment","profile");
            startActivity(intent);
        }
        else if(screen.equals("list")){
            Intent intent= new Intent(EventActivity.this,MainActivity2.class);
            intent.putExtra("Fragment","list");
            startActivity(intent);
        }
        else
            super.onBackPressed();
    }
    public void deleteRequest(){
//        sendBroadcast("none");
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        marker="none";
        final String url="https://socupdate.herokuapp.com/events/"+eventId+"/mark/delete";
        if(user!=null){
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if(task.getResult().getToken()!=null){
                        HashMap<String,String> mapToken=new HashMap<String, String>();
                        mapToken.put("token",task.getResult().getToken());
                        Log.d("deleteToken", String.valueOf(new JSONObject(mapToken)));
                        final RequestQueue requstQueue = Volley.newRequestQueue(EventActivity.this);
                        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(mapToken),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("respone",String.valueOf(response));
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("error",error.toString());
                                    }
                                }
                        );
                        requstQueue.add(jsonobj);
                    }
                }
            });
        }
    }
    public void addMarker(final String mark){
//        sendBroadcast(mark);
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        marker=mark;
        final String url="https://socupdate.herokuapp.com/events/"+eventId+"/mark";
        if(user!=null){
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if(task.getResult().getToken()!=null){
                        HashMap<String,String> mapToken=new HashMap<String, String>();
                        mapToken.put("token",task.getResult().getToken());
                        mapToken.put("mark",mark);
                        Log.d("deleteToken", String.valueOf(new JSONObject(mapToken)));
                        final RequestQueue requstQueue = Volley.newRequestQueue(EventActivity.this);
                        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(mapToken),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("respone",String.valueOf(response));
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("error",error.toString());
                                    }
                                }
                        );
                        requstQueue.add(jsonobj);
                    }
                }
            });
        }
    }
    public void setItems(){
        final String urlPost = "https://socupdate.herokuapp.com/events/marked";
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if(task.isSuccessful()){
                        final HashMap<String,String> mapToken=new HashMap<String, String>();
                        mapToken.put("token",task.getResult().getToken());
                        Log.d("PostToken",task.getResult().getToken());
                        final ProgressDialog progressDialog = new ProgressDialog(EventActivity.this,R.style.ProgressDialogTheme);
                        progressDialog.setMessage("Loading data....");
                        progressDialog.show();
                        RequestQueue requstQueue = Volley.newRequestQueue(EventActivity.this);
                        Log.d("PostObject", String.valueOf(new JSONObject(mapToken)));
                        final JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, urlPost,new JSONObject(mapToken),
                                new Response.Listener<JSONObject>() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Iterator<String> keys = response.keys();
                                        int position=0;
                                        while(keys.hasNext()){
                                            String event= keys.next();
                                            try {
                                                JSONObject jsonObject = response.getJSONObject(event);
                                                if(event.equals(eventId)){
                                                    String venue_event = jsonObject.getString("venue");
                                                    String time_event = jsonObject.getString("time");
                                                    String dateEvent = jsonObject.getString("date");
                                                    if(jsonObject.has("markedAs")){
                                                        marker=jsonObject.getString("markedAs");
                                                        Log.d("xx",marker);
                                                    }
                                                    if(jsonObject.has("attachments")) {
                                                        attachment_text.setVisibility(View.VISIBLE);
                                                        JSONObject attachmentsJsonObj = jsonObject.getJSONObject("attachments");
                                                        Iterator iterator= attachmentsJsonObj.keys();
                                                        while (iterator.hasNext()){
                                                            String name=iterator.next().toString();
                                                            arrayList.add(attachmentsJsonObj.getString(name));
                                                            StringBuilder convertString = new StringBuilder(name);
                                                            for(int y=0;y<name.length();y++){
                                                                if(name.charAt(y)=='-'){
                                                                    convertString.setCharAt(y, '.');
                                                                }
                                                            }
                                                            nameList.add(convertString.toString());
                                                        }
                                                    }
                                                    Date date_event = null;
                                                    try {
                                                        date_event = dateFormat.parse(dateEvent);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    eventName.setText(jsonObject.getString("name"));
                                                    byName.setText(jsonObject.getString("byName"));
                                                    desc.setText(jsonObject.getString("desc"));
                                                    Linkify.addLinks(desc , Linkify.WEB_URLS);
                                                    venue.setText(venue_event);
                                                    date.setText(sdf.format(date_event).substring(0, 3) + ", " + d.format(date_event) + " " + format.format(date_event) + " " + y.format(date_event) + " "+time_event);
                                                    progressDialog.dismiss();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    Log.d("xxx",marker);
                                    setIcon();

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }
                        );
                        requstQueue.add(jsonobj);
                    }
                }
            });
        }
    }


}
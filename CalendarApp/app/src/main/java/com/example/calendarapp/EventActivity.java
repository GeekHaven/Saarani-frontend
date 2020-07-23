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
    TextView eventName,desc,date,time,venue,byName;
    String eventId;
    ImageView download_url,button_back;
    String screen;
    String marker="";
    LinearLayout layout_attachment;
    ArrayList<String> arrayList=new ArrayList<>();
    ImageView star,tick_mark;
    TextView interested,going;
    private ProgressDialog pDialog;
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
        eventName=findViewById(R.id.name);
        desc=findViewById(R.id.desc);
        date=findViewById(R.id.date);
        time=findViewById(R.id.time);
        venue=findViewById(R.id.venue);
        byName=findViewById(R.id.society);
        download_url=findViewById(R.id.download);
        star=findViewById(R.id.star);
        tick_mark=findViewById(R.id.tick);
        interested=findViewById(R.id.text_interested);
        going=findViewById(R.id.text_going);
        button_back=findViewById(R.id.button);
        layout_attachment=findViewById(R.id.attachmentLayout);
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
            date.setText(sdf.format(date_event).substring(0, 3) + ", " + d.format(date_event) + " " + format.format(date_event) + " " + y.format(date_event));
            time.setText(time_event.substring(6));
        }
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        if(prefs.getString("society", "false").equals("false")) {
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = star.getTag();
                    if (tag != null && (Integer) tag == star_yellow) {
                        Toast.makeText(EventActivity.this, "Unmarked", Toast.LENGTH_SHORT).show();
                        star.setTag(R.drawable.star_img);
                        star.setImageResource(R.drawable.star_img);
                        deleteRequest();
                    } else {
                        Toast.makeText(EventActivity.this, "Marked", Toast.LENGTH_SHORT).show();
                        star.setTag(star_yellow);
                        star.setImageResource(star_yellow);
                        if ((Integer) tick_mark.getTag() == tick_yellow) {
                            tick_mark.setTag(tick);
                            tick_mark.setImageResource(R.drawable.tick);
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
                        Toast.makeText(EventActivity.this, "Unmarked", Toast.LENGTH_SHORT).show();
                        star.setTag(R.drawable.star_img);
                        star.setImageResource(R.drawable.star_img);
                        deleteRequest();
                    } else {
                        Toast.makeText(EventActivity.this, "Marked", Toast.LENGTH_SHORT).show();
                        star.setTag(star_yellow);
                        star.setImageResource(star_yellow);
                        if ((Integer) tick_mark.getTag() == tick_yellow) {
                            tick_mark.setTag(tick);
                            tick_mark.setImageResource(R.drawable.tick);
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
                        Toast.makeText(EventActivity.this, "Unmarked", Toast.LENGTH_SHORT).show();
                        tick_mark.setTag(tick);
                        tick_mark.setImageResource(R.drawable.tick);
                        deleteRequest();
                    } else {
                        Toast.makeText(EventActivity.this, "Marked", Toast.LENGTH_SHORT).show();
                        tick_mark.setTag(tick_yellow);
                        tick_mark.setImageResource(R.drawable.tick_yellow);
                        if ((Integer) star.getTag() == star_yellow) {
                            star.setTag(R.drawable.star_img);
                            star.setImageResource(R.drawable.star_img);
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
                        Toast.makeText(EventActivity.this, "Unmarked", Toast.LENGTH_SHORT).show();
                        tick_mark.setTag(tick);
                        tick_mark.setImageResource(R.drawable.tick);
                        deleteRequest();
                    } else {
                        Toast.makeText(EventActivity.this, "Marked", Toast.LENGTH_SHORT).show();
                        tick_mark.setTag(tick_yellow);
                        tick_mark.setImageResource(R.drawable.tick_yellow);
                        if ((Integer) star.getTag() == star_yellow) {
                            star.setTag(R.drawable.star_img);
                            star.setImageResource(R.drawable.star_img);
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
        download_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
////                    download_files();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
//    public void sendBroadcast(String current){
//        Intent intent = new Intent("markerChange");
//        intent.putExtra("eventId",eventId);
//        intent.putExtra("markedAs",current);
//        LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(intent);
//    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addAttachmentList(String url,int count){
        final LinearLayout linearLayout = new LinearLayout(this);
        final int id = View.generateViewId();
        mapUrl.put(String.valueOf(id),url);
        linearLayout.setId(id);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        Resources r = EventActivity.this.getResources();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        60,
                        r.getDisplayMetrics()));
        params.bottomMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                r.getDisplayMetrics()
        );
        linearLayout.setWeightSum(10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            linearLayout.setElevation(10);
        }
        linearLayout.setBackgroundResource(R.drawable.background_event);
        layout_attachment.addView(linearLayout,params);
        TextView name= new TextView(this);
        name.setSingleLine();
        name.setTextSize(20);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/montserratmedium.ttf");
        name.setTypeface(face);
        name.setTextColor(Color.WHITE);
        if(arrayList.size()>1)
        name.setText("Attachment "+(count+1));
        else
        name.setText("Attachment");
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.gravity= Gravity.CENTER;
//        name.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textParams.leftMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                r.getDisplayMetrics()
        );
        textParams.weight=9;
        LinearLayout imageLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.rightMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                r.getDisplayMetrics()
        );
        layoutParams.weight = 1;
        linearLayout.addView(name,0,textParams);
        linearLayout.addView(imageLayout,1,layoutParams);
        ImageView imageView =new ImageView(this);
        imageView.setImageResource(R.drawable.download);
        LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        30,
                        r.getDisplayMetrics()
                ),
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        30,
                        r.getDisplayMetrics()
                ));
        paramsBtn.gravity = Gravity.CENTER;
        imageLayout.addView(imageView, 0, paramsBtn);
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
            tick_mark.setImageResource(R.drawable.tick_yellow);
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
                addAttachmentList(arrayList.get(i), i);
            }
        }
    }
    @Override
    public void onBackPressed(){
        if(screen.equals("home")) {
            startActivity(new Intent(EventActivity.this, MainActivity2.class));
        }
        else if(screen.equals("profile")){
           Intent intent= new Intent(EventActivity.this,MainActivity2.class);
           intent.putExtra("Fragment","profile");
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
                        final ProgressDialog progressDialog = new ProgressDialog(EventActivity.this);
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
                                                        JSONArray jsonArray = jsonObject.getJSONArray("attachments");
                                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                            arrayList.add(jsonArray.getString(i));
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
                                                    date.setText(sdf.format(date_event).substring(0, 3) + ", " + d.format(date_event) + " " + format.format(date_event) + " " + y.format(date_event));
                                                    time.setText(time_event);
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
    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
    public void download_files() throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for(i=0;i<arrayList.size();i++) {
            StorageReference httpsReference = storage.getReferenceFromUrl(arrayList.get(i));
            File rootPath = new File(Environment.getExternalStorageDirectory(), "download");
            if(!rootPath.exists()) {
                rootPath.mkdirs();
            }
            final File localFile = new File(rootPath,"name.jpg");
            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Toast.makeText(EventActivity.this,"File downloaded",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Toast.makeText(EventActivity.this,"Download failed",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
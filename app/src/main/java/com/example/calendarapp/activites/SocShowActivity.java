package com.example.calendarapp.activites;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.calendarapp.database.DatabaseHandler;
import com.example.calendarapp.data.ListItems;
import com.example.calendarapp.R;
import com.example.calendarapp.adapters.SocietyCardAdaptor;
import com.tomer.fadingtextview.FadingTextView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SocShowActivity extends AppCompatActivity {
    int i;
    String id,gmail="",fb="",desc,insta="";
    Boolean k=false;
    ImageView imageView,instagram,facebook,mail,back_btn;
    RecyclerView recyclerView;
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    Date date =new Date();
    FadingTextView fadingTextView;
    AVLoadingIndicatorView av;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayout;
    LayoutInflater layoutInflater;
    DatabaseHandler databaseHandler;
    RecyclerView.Adapter adapter;
    List<ListItems> listItems =new ArrayList<>();
    TextView soc_name,soc_desc,view_more,defaultText;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soc_show);
        databaseHandler=new DatabaseHandler(this);
        constraintLayout=findViewById(R.id.layout_constraint);
        fadingTextView=findViewById(R.id.fading_text_view);
        av=findViewById(R.id.avi);
        String[] texts = {"Gathering Resources",
                "Checking All the Dates",
                "Marking the Calendar",
                "Generating Buttons",
                "Entering Cheat Codes",
                "Downloading Hacks",
                "Leaking Nuclear Codes"};
//        fadingTextView.pause();
        constraintLayout.setVisibility(View.INVISIBLE);
        constraintLayout.animate().alpha(0.0f);
        av.show();
        fadingTextView.setTexts(texts);
        fadingTextView.setTimeout(1,FadingTextView.SECONDS);

        soc_name=findViewById(R.id.soc_name);
        imageView=findViewById(R.id.image);
        soc_desc=findViewById(R.id.soc_desc);
        view_more=findViewById(R.id.view_more);
        defaultText=findViewById(R.id.default_text);
        linearLayout=findViewById(R.id.linearLayout);
        instagram=findViewById(R.id.insta);
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInsta(insta);
            }
        });
        facebook=findViewById(R.id.fb);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFb(fb);
            }
        });
        mail=findViewById(R.id.gmail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailIntent(gmail);
            }
        });
        back_btn=findViewById(R.id.backArrow);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SocShowActivity.this));


        view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(k){
                    view_more.setText("View More");
                    adapter=new SocietyCardAdaptor(listItems.subList(0,3),  SocShowActivity.this,"eventPage");
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    view_more.setText("View Less");
                    adapter=new SocietyCardAdaptor(listItems,  SocShowActivity.this,"eventPage");
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                k=!k;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        Intent intent =getIntent();
        id=intent.getExtras().getString("id");
        addBackImage(imageView);
        getEventData();
        getCordiData();

    }
    public void goToFb(String x){
        Uri uri = Uri.parse(x);
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + x);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void goToInsta(String x){
        Uri uri = Uri.parse("http://instagram.com/_u/"+x);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/"+x)));
        }

    }
    public void emailIntent(String url){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"+url));
        startActivity(emailIntent);
    }
    public void addBackImage(ImageView imageView){
        if(id.equals("effervescence")){
            soc_name.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 21.50);
            soc_desc.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 11.25);
            imageView.setImageResource(R.drawable.effe_background);
        }
        if(id.equals("asmita")){
            imageView.setImageResource(R.drawable.asmita_background);
        }
        if(id.equals("gymkhana")) {
            soc_name.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 24.75);
            soc_desc.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 15.75);
            imageView.setImageResource(R.drawable.gymkhana_background);
        }
        if(id.equals("dance")) {
            soc_name.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 22.50);
            soc_desc.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 11.25);
            imageView.setImageResource(R.drawable.image);
        }
        if(id.equals("virtuosi"))
            imageView.setImageResource(R.drawable.virtuosi_background);
        if(id.equals("rangtarangini")) {
            soc_name.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 20.25);
            soc_desc.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 15.75);
            imageView.setImageResource(R.drawable.rang_background);
        }
        if(id.equals("nirmiti"))
            imageView.setImageResource(R.drawable.nirmiti_background);
        if(id.equals("ams"))
            imageView.setImageResource(R.drawable.ams_background);
        if(id.equals("sarasva"))
            imageView.setImageResource(R.drawable.sarasva_background);
        if(id.equals("sports"))
            imageView.setImageResource(R.drawable.spirit_background);
        if(id.equals("iiic"))
            imageView.setImageResource(R.drawable.iiic_background);
        if(id.equals("aparoksha")) {
            soc_name.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 24.75);
            soc_desc.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 13.50);
            imageView.setImageResource(R.drawable.apk_background);
        }
        if(id.equals("geekhaven")) {
            soc_name.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 24.75);
            soc_desc.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 13.50);
            imageView.setImageResource(R.drawable.geekhaven_background);
        }
        if(id.equals("tesla")) {
            imageView.setImageResource(R.drawable.tesla_background);
            soc_desc.setTextSize(TypedValue.COMPLEX_UNIT_PT, (float) 15.75);
        }

        if(id.equals("gravity")){
            imageView.setImageResource(R.drawable.gravity);
        }
    }
    public void addCordisToView(ArrayList<String> arrayList){
        for(i=0;i<arrayList.size();i++){

            if(i%2==0){
                View view=getLayoutInflater().inflate(R.layout.soc_profile_cordi,null);
                TextView name=view.findViewById(R.id.cordi_name);
                name.setText(arrayList.get(i));
                linearLayout.addView(view);
            }
            else{
                View view=getLayoutInflater().inflate(R.layout.soc_profile_cordi_alt,null);
                TextView name=view.findViewById(R.id.cordi_name);
                name.setText(arrayList.get(i));
                linearLayout.addView(view);
            }
        }


    }
    public void getCordiData(){
        String url = "https://socupdate.herokuapp.com/societies";
//        final ProgressDialog progressDialog = new ProgressDialog(SocShowActivity.this);
//        progressDialog.setMessage("Loading data....");
//        progressDialog.show();
        StringRequest stringRequest =new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<String> cordis = new ArrayList<>();
                    JSONObject json =new JSONObject(response);
                    Iterator<String> iterator= json.keys();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        JSONObject jsonObject = json.getJSONObject(key);
                        if(key.equals(id)) {
                            if (jsonObject.has("coordinators")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("coordinators");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    cordis.add(jsonArray.getString(i));
                                }
                            }
                            gmail=jsonObject.getString("email");
                            if(jsonObject.has("fb"))
                                fb=jsonObject.getString("fb");
                            else
                                facebook.setVisibility(View.GONE);
                            if(jsonObject.has("insta"))
                                insta=jsonObject.getString("insta");
                            else
                                instagram.setVisibility(View.GONE);

                            desc=jsonObject.getString("name");
                            soc_desc.setText(jsonObject.getString("desc"));
                            soc_name.setText(desc);
                            break;

                        }
                    }
                    addCordisToView(cordis);
//                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SocShowActivity.this);
        requestQueue.add(stringRequest);
    }
    public void getEventData(){
        String url = "https://socupdate.herokuapp.com/societies/"+id+"/events";
//        final ProgressDialog progressDialog = new ProgressDialog(SocShowActivity.this);
//        progressDialog.setMessage("Loading data....");
//        progressDialog.show();
        StringRequest stringRequest =new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json =new JSONObject(response);
                    Iterator<String> iterator= json.keys();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        JSONObject jsonObject = json.getJSONObject(key);
                        ArrayList<String> attachmentsList =new ArrayList<>();
                        ArrayList<String> attachmentNameList =new ArrayList<>();
                        if(jsonObject.has("attachments")){
                            JSONObject jsonObject1 = jsonObject.getJSONObject("attachments");
                            Iterator iterator1 = jsonObject1.keys();
                            while(iterator1.hasNext()){
                                String name= iterator1.next().toString();
                                attachmentsList.add(jsonObject1.getString(name));
                                attachmentNameList.add(name);
                            }
                        }
                        String marker=databaseHandler.getEvent(key).getMarker();
                        ListItems items = new ListItems(
                                jsonObject.getString("name"),
                                jsonObject.getString("desc"),
                                jsonObject.getString("byName"),jsonObject.getString("date"),
                                "Time: "+jsonObject.getString("time"),
                                "Venue: "+jsonObject.getString("venue"),marker,key,attachmentsList
                        );
                        Integer going_count=0;
                        Integer interested_count=0;
                        if(jsonObject.has("markedBy")) {
                            JSONObject markings = jsonObject.getJSONObject("markedBy");
                            Iterator<String> k = markings.keys();
                            while (k.hasNext()) {
                                String user = k.next();
                                if (markings.getString(user).equals("going")) {
                                    going_count++;
                                } else {
                                    interested_count++;
                                }
                            }
                        }
                        items.setInterested(interested_count);
                        items.setGoing(going_count);
                        items.setNameList(attachmentNameList);
                        if(f.parse(f.format(date)).compareTo(f.parse(items.getDate()))<0||(f.parse(f.format(date)).compareTo(f.parse(items.getDate()))==0 && LocalTime.now().isBefore(LocalTime.parse(items.getTime().split(" ")[1]))))
                        listItems.add(items);
                    }
                    Log.d("soc",listItems.toString());
                    if(listItems.size()==0){
                        defaultText.setVisibility(View.VISIBLE);
                        view_more.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else if(k || listItems.size()<=3){
                        recyclerView.setVisibility(View.VISIBLE);
                        if(listItems.size()<=3)
                            view_more.setVisibility(View.GONE);
                        adapter=new SocietyCardAdaptor(listItems, SocShowActivity.this,"eventPage");
                    }
                    else{
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter=new SocietyCardAdaptor(listItems.subList(0,3), SocShowActivity.this,"eventPage");
                    }
                    recyclerView.setAdapter(adapter);
//                    progressDialog.dismiss();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                av.hide();
                fadingTextView.stop();
                fadingTextView.setElevation(0);
//                constraintLayout.setVisibility(View.VISIBLE);
                constraintLayout.animate().alpha(1.0f);
                constraintLayout.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SocShow_volley_error", String.valueOf(error));
                fadingTextView.setVisibility(View.INVISIBLE);
                fadingTextView.stop();
                av.hide();
                constraintLayout.setVisibility(View.VISIBLE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SocShowActivity.this);
        requestQueue.add(stringRequest);
    }
}

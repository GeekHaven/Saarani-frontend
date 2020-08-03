package com.example.calendarapp;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.text.Edits;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainFragment extends Fragment {
    private MainViewModel mViewModel;
    TextView month,year,today,textLay,default_text;
    CompactCalendarView compactCalendar;
    int x=0;
    private RecyclerView recyclerView;
    private CardView cardView;
    private RecyclerView.Adapter adapter;
    final Date date = new Date();
    private List<ListItems> listItems,recylerViewList;
    private static String URL_DATA="https://socupdate.herokuapp.com/events";
    HashMap<String, Integer> map
            = new HashMap<>();
    HashMap<String,Integer> mapPosition =new HashMap<>();
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM", Locale.getDefault());
    private SimpleDateFormat dateFormatYear =new SimpleDateFormat("YYYY",Locale.getDefault());
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment, container, false);
        listItems=new ArrayList<>();
        final DateFormat dateFormat = new SimpleDateFormat("MM",Locale.getDefault());
        final SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.getDefault());
        final DateFormat dateFormat1= new SimpleDateFormat("dd",Locale.getDefault());
        final DateFormat yearFormat= new SimpleDateFormat("YYYY",Locale.getDefault());
        final String[] Selected = new String[]{"January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December"};
        default_text=view.findViewById(R.id.default_text);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        textLay=view.findViewById(R.id.textLay);
        today=view.findViewById(R.id.today);
        month =view.findViewById(R.id.month);
        year=view.findViewById(R.id.year);
        year.setText(yearFormat.format(date));
        month.setText(Selected[Integer.parseInt(dateFormat.format(date))-1]);
        textLay.setText(sdf.format(date)+", "+dateFormat1.format(date)+" "+Selected[Integer.parseInt(dateFormat.format(date))-1]);
        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        Log.d("date",date.toString());
        textLay.setText(sdf.format(date).substring(0,3)+", "+dateFormat1.format(date)+" "+Selected[Integer.parseInt(dateFormat.format(date))-1]);
        loadRecyclerViewData();

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getContext();
                if(dateFormat1.format(date).equals(dateFormat1.format(dateClicked))&&dateFormat.format(date).equals(dateFormat.format(dateClicked))&&yearFormat.format(date).equals(yearFormat.format(dateClicked))){
                    today.setVisibility(View.VISIBLE);
                }
                else
                    today.setVisibility(View.GONE);
                textLay.setText(sdf.format(dateClicked).substring(0,3)+", "+dateFormat1.format(dateClicked)+" "+Selected[Integer.parseInt(dateFormat.format(dateClicked))-1]);
                showRecyclerView(dateClicked);
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                month.setText(dateFormatMonth.format(firstDayOfNewMonth));
                year.setText(dateFormatYear.format(firstDayOfNewMonth));
            }


    });
        showRecyclerView(date);
        return view;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String eventId = intent.getStringExtra("eventId");
            String markedAs = intent.getStringExtra("markedAs");
            ListItems item =new ListItems(listItems.get(mapPosition.get(eventId)).getName(),listItems.get(mapPosition.get(eventId)).getDesc(),listItems.get(mapPosition.get(eventId)).getByName(),listItems.get(mapPosition.get(eventId)).getDate(),listItems.get(mapPosition.get(eventId)).getTime(),listItems.get(mapPosition.get(eventId)).getVenue(),markedAs,eventId,listItems.get(mapPosition.get(eventId)).getArrayList());
            listItems.set(mapPosition.get(eventId),item);
//            Toast.makeText(getContext(),ItemName +" "+qty ,Toast.LENGTH_SHORT).show();

        }
    };
    public void showRecyclerView(Date dateClicked){
        Log.d("called","yes");
        default_text.setVisibility(View.GONE);
        recylerViewList=new ArrayList<>();
        int flag=0;
        Log.d("listitem", String.valueOf(listItems.size()));
        for(int i=0;i<listItems.size();i++){
            ListItems item= listItems.get(i);
            Log.d("eventDate",item.getDate());
            Log.d("eventDate1",f.format(dateClicked));
            if(item.getDate().equals(f.format(dateClicked))){
                Log.d("match","yes");
                recylerViewList.add(item);
                Log.d("length",String.valueOf(recylerViewList.size()));
                flag=0;
            }
        }
        if(recylerViewList.size()!=0) {
            Log.d("what","do");
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new AdaptorActivity(recylerViewList, new ClickListener() {
                @Override
                public void onPositionClicked(int position) {

                }

                @Override
                public void onLongClicked(int position) {

                }
            }, getContext(),"home");
            recyclerView.setAdapter(adapter);
        }
        else{
            recyclerView.setVisibility(View.GONE);
            default_text.setVisibility(View.VISIBLE);
        }
    }
    public void loadRecyclerViewData(){
        final String urlPost = "https://socupdate.herokuapp.com/events/marked";
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading data....");
        progressDialog.show();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if(task.isSuccessful()){
                        final HashMap<String,String> mapToken=new HashMap<String, String>();
                        mapToken.put("token",task.getResult().getToken());
                        Log.d("PostToken",task.getResult().getToken());
                        RequestQueue requstQueue = Volley.newRequestQueue(requireContext());
                        Log.d("PostObject", String.valueOf(new JSONObject(mapToken)));
                        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, urlPost,new JSONObject(mapToken),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        progressDialog.dismiss();
                                        Iterator<String> keys = response.keys();
                                        int position=0;
                                        while(keys.hasNext()){
                                            String eventId= keys.next();
                                            try {
                                                JSONObject jsonObject = response.getJSONObject(eventId);
                                                String marker="none";
                                                if(jsonObject.has("markedAs")){
                                                    marker=jsonObject.getString("markedAs");
                                                }
                                                ArrayList<String> attachmentsList= new ArrayList<>();
                                                ArrayList<String> attachmentNameList=new ArrayList<>();
                                                if(jsonObject.has("attachments")) {
                                                    JSONObject attachmentJson = jsonObject.getJSONObject("attachments");
                                                    Iterator iterator = attachmentJson.keys();
                                                    while(iterator.hasNext()){
                                                        String name_of_attachment= (String) iterator.next();
                                                        attachmentsList.add(attachmentJson.getString(name_of_attachment));
                                                        attachmentNameList.add(name_of_attachment);
                                                    }
                                                }
                                                ListItems item =new ListItems(
                                                        jsonObject.getString("name"),
                                                        jsonObject.getString("desc"),
                                                        jsonObject.getString("byName"),
                                                        jsonObject.getString("date"),
                                                        "Time: "+jsonObject.getString("time"),
                                                        "Venue: "+jsonObject.getString("venue"),marker,eventId,attachmentsList
                                                );
                                                item.setNameList(attachmentNameList);
                                                listItems.add(item);
                                                mapPosition.put(eventId,position);
                                                position++;
                                                try {
                                                    Date d = f.parse(jsonObject.getString("date"));
                                                    assert d != null;
                                                    long milliseconds = d.getTime();

                                                    Event eventx;
                                                    if(map.get(String.valueOf(milliseconds))==null||map.get(String.valueOf(milliseconds))%2==0) {
                                                        eventx = new Event(Color.RED, milliseconds);
                                                        map.put(String.valueOf(milliseconds),x);
                                                        x++;
                                                    }
                                                    else {
                                                        eventx = new Event(Color.GREEN, milliseconds);
                                                        map.put(String.valueOf(milliseconds),x);
                                                        x++;
                                                    }
                                                    compactCalendar.addEvent(eventx);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Log.d("size",String.valueOf(listItems.size()));
                                        showRecyclerView(date);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }
                        );
                        requstQueue.add(jsonobj);
                        //Log.d("size",String.valueOf(listItems.size()));
                    }
                }
            });
        }
        //Log.d("size",String.valueOf(listItems.size()));
    }
}
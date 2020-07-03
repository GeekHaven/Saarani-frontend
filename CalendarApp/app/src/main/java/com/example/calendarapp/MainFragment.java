package com.example.calendarapp;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainFragment extends Fragment {
    private MainViewModel mViewModel;
    TextView month,year,today,textLay,default_text;
    CompactCalendarView compactCalendar;
    private RecyclerView recyclerView;
    private CardView cardView;
    private RecyclerView.Adapter adapter;
    private List<ListItems> listItems,recylerViewList;
    private static String URL_DATA="https://socupdate.herokuapp.com/events";
    HashMap<String, Integer> map
            = new HashMap<>();
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
        loadRecyclerViewData();
        final DateFormat dateFormat = new SimpleDateFormat("MM",Locale.getDefault());
        final SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.getDefault());
        final DateFormat dateFormat1= new SimpleDateFormat("dd",Locale.getDefault());
        final DateFormat yearFormat= new SimpleDateFormat("YYYY",Locale.getDefault());
        final String[] Selected = new String[]{"January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December"};
        final Date date = new Date();
        default_text=view.findViewById(R.id.default_text);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        showRecyclerView(date);
        textLay=view.findViewById(R.id.textLay);
        today=view.findViewById(R.id.today);
        month =view.findViewById(R.id.month);
        year=view.findViewById(R.id.year);
        year.setText(yearFormat.format(date));
        month.setText(Selected[Integer.parseInt(dateFormat.format(date))-1]);
        textLay.setText(sdf.format(date)+", "+dateFormat1.format(date)+" "+Selected[Integer.parseInt(dateFormat.format(date))-1]);
        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

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
        return view;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }
    public void showRecyclerView(Date dateClicked){
        default_text.setVisibility(View.GONE);
        recylerViewList=new ArrayList<>();
        int flag=0;
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
            adapter = new AdaptorActivity(recylerViewList, getContext());
            recyclerView.setAdapter(adapter);
        }
        else{
            recyclerView.setVisibility(View.GONE);
            default_text.setVisibility(View.VISIBLE);
        }
    }
    public void loadRecyclerViewData(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),R.style.Theme_AppCompat);
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
                        JSONObject obj=jsonObject.getJSONObject(key);
                        ListItems items=new ListItems(
                                obj.getString("name"),
                                obj.getString("desc"),
                                obj.getString("byName"),
                                obj.getString("date"),
                                "TIME : "+obj.getString("time"),
                                "VENUE : " + obj.getString("venue")
                        );
                        listItems.add(items);

                        try {
                            Date d = f.parse(obj.getString("date"));
                            assert d != null;
                            long milliseconds = d.getTime();

                            Event eventx;
                            if(map.get(String.valueOf(milliseconds))==null) {
                                eventx = new Event(Color.RED, milliseconds);
                                map.put(String.valueOf(milliseconds),1);
                            }
                            else {
                                eventx = new Event(Color.GREEN, milliseconds);
                            }
                            compactCalendar.addEvent(eventx);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
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

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }
}
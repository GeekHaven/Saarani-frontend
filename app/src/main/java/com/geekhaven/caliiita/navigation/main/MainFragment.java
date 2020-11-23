package com.geekhaven.caliiita.navigation.main;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.geekhaven.caliiita.R;
import com.geekhaven.caliiita.adapters.AdaptorActivity;
import com.geekhaven.caliiita.customSwipeRefresh.CustomSwipeRefreshLayout;
import com.geekhaven.caliiita.data.ListItems;
import com.geekhaven.caliiita.database.DatabaseHandler;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.tomer.fadingtextview.FadingTextView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {
    private MainViewModel mViewModel;
    TextView month,year,today,textLay,default_text;
    CompactCalendarView compactCalendar;
    ConstraintLayout constraintLayout;
    int x=0;
    private RecyclerView recyclerView;
    String loadDataFrom,dateClick;
    private CardView cardView;
    private RecyclerView.Adapter adapter;
    final Date date = new Date();
    DatabaseHandler databaseHandler;
    FadingTextView fadingTextView;
    private List<ListItems> listItems,recylerViewList;
    private static String URL_DATA="https://socupdate.herokuapp.com/events";
    HashMap<String, Integer> checkEvent
            = new HashMap<>();
    AVLoadingIndicatorView av;
    String[] texts = {"Gathering Resources",
            "Checking All the Dates",
            "Marking the Calendar",
            "Generating Buttons",
            "Entering Cheat Codes",
            "Downloading Hacks",
            "Leaking Nuclear Codes"};
    HashMap<String,Integer> mapPosition =new HashMap<>();
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
    final SimpleDateFormat time_sdf = new SimpleDateFormat("HH:mm",Locale.getDefault());
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM", Locale.getDefault());
    private SimpleDateFormat dateFormatYear =new SimpleDateFormat("YYYY",Locale.getDefault());

    final DateFormat dateFormat = new SimpleDateFormat("MM",Locale.getDefault());
    final SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.getDefault());
    final DateFormat dateFormat1= new SimpleDateFormat("dd",Locale.getDefault());
    final DateFormat yearFormat= new SimpleDateFormat("YYYY",Locale.getDefault());
    final String[] Selected = new String[]{"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};


    private CustomSwipeRefreshLayout swipeRefreshLayout;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment, container, false);
        Log.d("result","created");
        Bundle bundle=getArguments();

        if (bundle != null) {
            loadDataFrom=bundle.getString("loadFrom");
            dateClick=bundle.getString("date");
        }
        databaseHandler = new DatabaseHandler(getContext());
        listItems=new ArrayList<>();

        av=view.findViewById(R.id.avi);
        fadingTextView=view.findViewById(R.id.fading_text_view);
        fadingTextView.setVisibility(View.INVISIBLE);
        fadingTextView.setTexts(texts);
        fadingTextView.setTimeout(1,FadingTextView.SECONDS);
        fadingTextView.stop();
        //fadingTextView.pause();
        cardView=view.findViewById(R.id.cardView);
        default_text=view.findViewById(R.id.default_text);
        constraintLayout=view.findViewById(R.id.layout);
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshMain);

        if(!isOnline()){
            Snackbar.make(constraintLayout,"Not connected to network! Changes will not be saved.",Snackbar.LENGTH_LONG);
        }
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
        //textLay.setText(sdf.format(date)+", "+dateFormat1.format(date)+" "+Selected[Integer.parseInt(dateFormat.format(date))-1]);
        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setNestedScrollingEnabled(false);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        Log.d("date",date.toString());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(swipeRefreshLayout.isRefreshing()){
                    if(isOnline()) {
                        try {
                            compactCalendar.removeAllEvents();
                            recyclerView.setVisibility(View.GONE);
                            loadRecyclerViewData();
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "No response!! please check your\nInternet Connectivity", Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },1000);
                    }
                }

            }
        });
        textLay.setText(sdf.format(date).substring(0,3)+", "+dateFormat1.format(date)+" "+Selected[Integer.parseInt(dateFormat.format(date))-1]);
        if(loadDataFrom!=null&&loadDataFrom.equals("server")) {
            try {
                loadRecyclerViewData();
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                loadDatabase();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(dateClick!=null){
            try {
                Date dateClicked = f.parse(dateClick);
                compactCalendar.setCurrentDate(dateClicked);
                if(dateFormat1.format(date).equals(dateFormat1.format(dateClicked))&&dateFormat.format(date).equals(dateFormat.format(dateClicked))&&yearFormat.format(date).equals(yearFormat.format(dateClicked))){
                    today.setVisibility(View.VISIBLE);
                }
                else
                    today.setVisibility(View.GONE);
                textLay.setText(sdf.format(dateClicked).substring(0,3)+", "+dateFormat1.format(dateClicked)+" "+Selected[Integer.parseInt(dateFormat.format(dateClicked))-1]);
                showRecyclerView(dateClicked);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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
        if(dateClick==null)
        showRecyclerView(date);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("refresh"));
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        Log.d("tag","paused");
    }
    public void onResume() {
        super.onResume();
        Log.d("result","resume");
        IntentFilter iff= new IntentFilter("refresh");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, iff);
        final SharedPreferences pref = requireActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        Log.d("notice",String.valueOf(pref.getBoolean("newEvent",false)));
        if(pref.getBoolean("newEvent",false)){
            makeSnacBar();
        }
        editor.putBoolean("newEvent",false);
    }
    public void makeSnacBar(){
        final Snackbar snackbar = Snackbar
                .make(constraintLayout, "Events Updated!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Refresh", new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        SharedPreferences pref = requireActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("newEvent",false);
                        editor.apply();
                        try {
                            compactCalendar.removeAllEvents();
                            loadDatabase();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("valueOf",String.valueOf(pref.getBoolean("newEvent",false)));
                    }
                });

        snackbar.show();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras().getString("value").equals("true")){
                makeSnacBar();
            }
        }
    };
    public boolean isInForeground(){
        return ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public boolean isOnline() {
    ConnectivityManager cm =
            (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnected();
}
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadDatabase() throws JSONException {
        checkEvent=new HashMap<>();
        year.setText(yearFormat.format(date));
        month.setText(Selected[Integer.parseInt(dateFormat.format(date))-1]);
        textLay.setText(sdf.format(date).substring(0,3)+", "+dateFormat1.format(date)+" "+Selected[Integer.parseInt(dateFormat.format(date))-1]);
        today.setVisibility(View.VISIBLE);
        compactCalendar.setCurrentDate(date);
        listItems=databaseHandler.getAllEvents();
        Log.d("MainFragListItemSize", String.valueOf(listItems.size()));
        for(int i=0;i<listItems.size();i++){
            if(checkEvent.getOrDefault(listItems.get(i).getEventId(),0)!=0){
                listItems.remove(i);
                /*databaseHandler.deleteEvent(listItems.get(i));*/
                i--;
            }
            else{
                checkEvent.put(listItems.get(i).getEventId(),1);
            }
        }
        Log.d("db_list_size", String.valueOf(listItems.size()));
        int pos=0;
        for(int i=0;i<listItems.size();i++){
            ListItems item = listItems.get(i);
            mapPosition.put("position",pos);
            pos++;
            try {
                Date d = f.parse(item.getDate());
                assert d != null;
                long milliseconds = d.getTime();
                if(f.parse(f.format(date)).compareTo(d)==0 && LocalTime.now().isAfter(LocalTime.parse(item.getTime().split(" ")[1]))){
                    item.setState("completed");
                    databaseHandler.updateState(item,"completed");
                }
                Event eventx;
                eventx = new Event(Color.RED, milliseconds);
                if(f.parse(f.format(date)).compareTo(d)<=0)
                compactCalendar.addEvent(eventx);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        showRecyclerView(date);
    }
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
            adapter = new AdaptorActivity(recylerViewList, getContext(),"home",getActivity());
            recyclerView.setAdapter(adapter);
        }
        else{
            recyclerView.setVisibility(View.GONE);
            default_text.setVisibility(View.VISIBLE);
        }
    }
    private void sendMessage(String msg) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("toolbar-visibility");
        // You can also include some extra data.
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void ResetCurrentDate(){
        year.setText(yearFormat.format(date));
        compactCalendar.setCurrentDate(date);
        month.setText(Selected[Integer.parseInt(dateFormat.format(date))-1]);
        textLay.setText(sdf.format(date).substring(0,3)+", "+dateFormat1.format(date)+" "+Selected[Integer.parseInt(dateFormat.format(date))-1]);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadRecyclerViewData() throws JSONException, ParseException {

        final String urlPost = "https://socupdate.herokuapp.com/events/marked";
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Loading data....");
//        progressDialog.show();
        sendMessage("false");

        av.show();
        fadingTextView.setVisibility(View.VISIBLE);
        fadingTextView.restart();
        //fadingTextView.resume();
        //fadingTextView.startAnimation(android.view.animation.Animation animation);
        av.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.INVISIBLE);
        default_text.setVisibility(View.INVISIBLE);
        default_text.setText(" ");
        textLay.setVisibility(View.INVISIBLE);
        month.setVisibility(View.INVISIBLE);
        year.setVisibility(View.INVISIBLE);
        today.setVisibility(View.INVISIBLE);
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
                                        try {

                                            databaseHandler.deleteDatabase();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
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
                                                item.setPhotoUrl(jsonObject.getString("photoURL"));
                                                item.setState("upcoming");
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
                                                item.setGoing(going_count);
                                                item.setInterested(interested_count);
                                                //listItems.add(item);
                                                databaseHandler.addEvent(item);
                                                /*mapPosition.put(eventId,position);
                                                position++;*/
                                                /*try {
                                                    Date d = f.parse(jsonObject.getString("date"));
                                                    assert d != null;
                                                    long milliseconds = d.getTime();
                                                    Event eventx;
                                                    eventx = new Event(Color.RED, milliseconds);
                                                    compactCalendar.addEvent(eventx);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }*/
                                            } catch (JSONException e) {
                                                e.printStackTrace();

                                            }

                                        }
                                        try {
                                            /*listItems=databaseHandler.getAllEvents();*/
                                            loadDatabase();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("size",String.valueOf(listItems.size()));
                                        /*showRecyclerView(date);*/
                                        databaseHandler.close();
                                        ResetCurrentDate();
                                        swipeRefreshLayout.setRefreshing(false);
//                                        progressDialog.dismiss();
                                        cardView.setVisibility(View.VISIBLE);
                                        today.setVisibility(View.VISIBLE);
                                        default_text.setVisibility(View.VISIBLE);
                                        textLay.setVisibility(View.VISIBLE);
                                        month.setVisibility(View.VISIBLE);
                                        year.setVisibility(View.VISIBLE);
                                        sendMessage("true");
                                        av.hide();
                                        fadingTextView.stop();
                                        fadingTextView.setVisibility(View.INVISIBLE);
                                        default_text.setText("No Event Found");
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
//                                        progressDialog.dismiss();
                                        av.hide();
                                        fadingTextView.stop();
                                        fadingTextView.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(),"Event fetch failed!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        jsonobj.setRetryPolicy(new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requstQueue.add(jsonobj);
                        //Log.d("size",String.valueOf(listItems.size()));
                    }
                }
            });
        }
        //Log.d("size",String.valueOf(listItems.size()));
    }
}
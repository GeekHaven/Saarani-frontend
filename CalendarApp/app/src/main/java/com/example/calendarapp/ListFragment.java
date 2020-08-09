package com.example.calendarapp;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class ListFragment extends Fragment {

    private ListViewModel mViewModel;
    TextView date_setUp,today_text,tomorrow_text,default_text1,default_text2;
    List<ListItems> listItems,recylerViewList,recyclerViewListTom;
    private RecyclerView recyclerViewToday,recyclerViewTom;
    private CardView cardView;
    Date dateX=new Date();
    private RecyclerView.Adapter adapter,adapter2;
    private String tomorrowDate;
    DatabaseHandler databaseHandler;
    final DateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
    final SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.getDefault());
    final DateFormat dateFormat1= new SimpleDateFormat("dd",Locale.getDefault());
    final DateFormat yearFormat= new SimpleDateFormat("YYYY",Locale.getDefault());
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM", Locale.getDefault());
    private Map<String,Integer> map =  new HashMap<>();
    final String[] Selected = new String[]{"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};
    HorizontalCalendar horizontalCalendar;
    HorizontalCalendar.Builder builder;
    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.list_fragment, container, false);
        listItems=new ArrayList<>();
        databaseHandler=new DatabaseHandler(getContext());
        try {
            listItems=databaseHandler.getAllEvents();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Date date= new Date();
        date_setUp=view.findViewById(R.id.month_title);

        default_text1=view.findViewById(R.id.default_text1);
        default_text2=view.findViewById(R.id.default_text2);

        today_text=view.findViewById(R.id.today_text);
        tomorrow_text=view.findViewById(R.id.tomorrow_text);

        recyclerViewToday=view.findViewById(R.id.recyclerViewToday);
        recyclerViewToday.setHasFixedSize(true);
        recyclerViewToday.setAdapter(adapter);
        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewTom=view.findViewById(R.id.recyclerViewTomorrow);
        recyclerViewTom.setHasFixedSize(true);
        recyclerViewTom.setAdapter(adapter2);
        recyclerViewTom.setLayoutManager(new LinearLayoutManager(getContext()));

        builder= new HorizontalCalendar.Builder(view,R.id.calendarView);
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
        tomorrowDate=f.format(gc.getTime());
        date_setUp.setText(dateFormatMonth.format(date)+", "+yearFormat.format(date));
        String t="<b>"+getString(R.string.today)+"</b>"+"  "+sdf.format(date).substring(0,3)+", "+dateFormat1.format(date)+" "+dateFormatMonth.format(date);
        today_text.setText(Html.fromHtml(t));
        String to="<b>"+getString(R.string.tomorrow)+"</b>"+"  "+sdf.format(gc.getTime()).substring(0,3)+", "+dateFormat1.format(gc.getTime())+" "+dateFormatMonth.format(gc.getTime());
        tomorrow_text.setText(Html.fromHtml(to));
//        addEventsToCal();
        Date d=new Date();
        try {
            addDataToRV(f.format(d),tomorrowDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        addEvents(listItems);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        // TODO: Use the ViewModel
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addDataToRV(String dateSelected, String dateTomSelected) throws ParseException {
        default_text1.setVisibility(View.GONE);
        default_text2.setVisibility(View.GONE);
        recylerViewList=new ArrayList<>();
        recyclerViewListTom=new ArrayList<>();
        int flag=0;
        Log.d("listitem", String.valueOf(listItems.size()));
        for(int i=0;i<listItems.size();i++){
            ListItems item= listItems.get(i);
            if(item.getDate().equals(dateSelected)){
                Log.d("match","yes");
                Date d= f.parse(item.getDate());
                if(f.parse(f.format(dateX)).compareTo(d)==0 && LocalTime.now().isAfter(LocalTime.parse(item.getTime().split(" ")[1]))){
                    item.setState(true);
                }
                else
                    item.setState(false);
                recylerViewList.add(item);
                Log.d("length",String.valueOf(recylerViewList.size()));
            }
            if(item.getDate().equals(dateTomSelected)){
                recyclerViewListTom.add(item);
            }
        }
        if(recylerViewList.size()!=0) {
            Log.d("what","do");
            recyclerViewToday.setVisibility(View.VISIBLE);
            default_text1.setVisibility(View.GONE);
            adapter = new AdaptorActivity(recylerViewList, new ClickListener() {
                @Override
                public void onPositionClicked(int position) {

                }

                @Override
                public void onLongClicked(int position) {

                }
            }, getContext(),"list");
            recyclerViewToday.setAdapter(adapter);
        }
        else{
            recyclerViewToday.setVisibility(View.GONE);
            default_text1.setVisibility(View.VISIBLE);
        }
        if(recyclerViewListTom.size()!=0) {
            Log.d("what","do");
            recyclerViewTom.setVisibility(View.VISIBLE);
            default_text2.setVisibility(View.GONE);
            adapter2 = new AdaptorActivity(recyclerViewListTom, new ClickListener() {
                @Override
                public void onPositionClicked(int position) {

                }

                @Override
                public void onLongClicked(int position) {

                }
            }, getContext(),"list");
            recyclerViewTom.setAdapter(adapter2);
        }
        else{
            recyclerViewTom.setVisibility(View.GONE);
            default_text2.setVisibility(View.VISIBLE);
        }
    }
    public void addEvents(final List<ListItems> temp){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading data....");
        progressDialog.show();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH,0);
        startDate.add(Calendar.DATE,-5);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 12);
        builder.addEvents(new CalendarEventsPredicate() {
            @Override
            public List<CalendarEvent> events(Calendar date) {
                Log.d("date",f.format(date.getTime()));
                Log.d("length", String.valueOf(temp.size()));
                List<CalendarEvent> event = new ArrayList<>();
                for(int i=0;i<temp.size();i++) {
                    try {
                        if (f.format(date.getTime()).equals(temp.get(i).getDate())&&f.parse(f.format(dateX)).compareTo(f.parse(temp.get(i).getDate()))<=0) {
                            event.add(new CalendarEvent(Color.rgb(240, 212, 83)));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return event;
            }
        });
        horizontalCalendar = builder
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure()
                .showTopText(false)
                .end()
                .build();
        progressDialog.dismiss();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSelected(Calendar date, int position) {
                date_setUp.setText(Selected[date.get(Calendar.MONTH)]+", " +date.get(Calendar.YEAR));
                Date dateToday= new Date();
                String currentDate=f.format(dateToday);
                String selectedDate=f.format(date.getTime());
//                Log.d("f1",f.format(date.getTime()));
//                Log.d("f2",f.format(dateToday));
                String selectedDateDay=sdf.format(date.getTime()).substring(0, 3);
                int selectedDateDate=date.get(Calendar.DATE);
                String selectedDateMonth=Selected[date.get(Calendar.MONTH)];
                Calendar dateTom;
                dateTom=date;
                dateTom.add(Calendar.DATE,1);
                String selectedDateD=f.format(dateTom.getTime());
                String selectedDateTomDay=sdf.format(dateTom.getTime()).substring(0, 3);
                String selectedDateTomDate=dateFormat1.format(dateTom.getTime());
                String selectedDateTomMonth=dateFormatMonth.format(dateTom.getTime());
                int dateST=date.get(Calendar.DATE);
                String dateMT=Selected[date.get(Calendar.MONTH)];
                Log.d("f1",f.format(date.getTime()));
                Log.d("f2",f.format(dateToday));
                if(selectedDate.equals(currentDate)){
                    String t="<b>"+getString(R.string.today)+"</b>"+"  "+selectedDateDay+", "+selectedDateDate+" "+selectedDateMonth;
                    today_text.setText(Html.fromHtml(t));
                    String to="<b>"+getString(R.string.tomorrow)+"</b>"+"  "+selectedDateTomDay+", "+selectedDateTomDate+" "+selectedDateTomMonth;
                    tomorrow_text.setText(Html.fromHtml(to));
                }
                else {
                    today_text.setText(selectedDateDay+", "+selectedDateDate+" "+selectedDateMonth);
                    tomorrow_text.setText(selectedDateTomDay+", "+selectedDateTomDate+" "+selectedDateTomMonth);
                }
                try {
                    addDataToRV(selectedDate,selectedDateD);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy){
            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });
        horizontalCalendar.refresh();
    }
    public void addEventsToCal(){
        final List<ListItems> temp= new ArrayList<>();
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
                        final JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, urlPost,new JSONObject(mapToken),
                                new Response.Listener<JSONObject>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Iterator<String> keys = response.keys();
                                        int position=0;
                                        while(keys.hasNext()){
                                            String eventId= keys.next();
                                            try {
                                                final JSONObject jsonObject = response.getJSONObject(eventId);
                                                String marker="none";
                                                if(jsonObject.has("markedAs")){
                                                    marker=jsonObject.getString("markedAs");
                                                }
                                                ArrayList<String> attachmentsList= new ArrayList<>();
                                                ArrayList<String> attachmentNameList =new ArrayList<>();
                                                if(jsonObject.has("attachments")) {
                                                    JSONObject attachmentsObj = jsonObject.getJSONObject("attachments");
                                                    Iterator iterator= attachmentsObj.keys();
                                                    while (iterator.hasNext()){
                                                        String name_of_attachment=iterator.next().toString();
                                                        attachmentsList.add(attachmentsObj.getString(name_of_attachment));
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
                                                final String date0= jsonObject.getString("date");
                                                temp.add(item);
                                                listItems.add(item);
                                                Date d=new Date();
                                                try {
                                                    addDataToRV(f.format(d),tomorrowDate);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Log.d("tempSize",String.valueOf(temp.size()));
                                        progressDialog.dismiss();
                                        addEvents(temp);
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
    }
}
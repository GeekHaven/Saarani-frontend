package com.geekhaven.caliiita.inflator;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekhaven.caliiita.R;
import com.geekhaven.caliiita.adapters.AdaptorActivity;
import com.geekhaven.caliiita.data.ListItems;
import com.geekhaven.caliiita.database.DatabaseHandler;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileRVFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileRVFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    TextView noEventTextView;
    private RecyclerView.Adapter adapter;
    DatabaseHandler databaseHandler;
    ArrayList<ListItems> listItems,showListItems;
    private Map<String ,Integer> map=new HashMap<>();
    Date date=new Date();
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileRVFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileRVFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileRVFragment2 newInstance(String param1, String param2) {
        ProfileRVFragment2 fragment = new ProfileRVFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile_r_v, container, false);
        databaseHandler=new DatabaseHandler(getContext());
        listItems=new ArrayList<>();
        showListItems=new ArrayList<>();
        recyclerView=view.findViewById(R.id.recyclerView);
        noEventTextView=view.findViewById(R.id.no_event);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(fetchData()){
            if(showListItems.size()==0){
                noEventTextView.setText("No event marked as Going!");
                noEventTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else{
                loadRecyclerView();
            }
        }
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean fetchData(){
        try {
            map=new HashMap<>();
            List<ListItems> allEvents=databaseHandler.getAllEvents();
            for(int i=0;i<allEvents.size();i++) {
                ListItems item = allEvents.get(i);
                if (!item.getState().equals("cancelled") && (f.parse(f.format(date)).compareTo(f.parse(item.getDate())) < 0 || (f.parse(f.format(date)).compareTo(f.parse(item.getDate())) == 0 && LocalTime.now().isBefore(LocalTime.parse(item.getTime().split(" ")[1]))))) {
                    if (item.getMarker().equals("going")) {
                        showListItems.add(item);
                    } //else if (item.getMarker().equals("interested")){
                        //showListItems.add(item);}
                }
            }
            for(int i=0;i<showListItems.size();i++){
                if(map.getOrDefault(showListItems.get(i).getEventId(),0)!=0){
                    showListItems.remove(i);
                    /*databaseHandler.deleteEvent(listItems.get(i));*/
                    i--;
                }
                else{
                    map.put(showListItems.get(i).getEventId(),1);
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void loadRecyclerView(){
        noEventTextView.setVisibility(View.GONE);
        adapter = new AdaptorActivity(showListItems, getContext(),"profile",getActivity());
        recyclerView.setAdapter(adapter);
    }
}
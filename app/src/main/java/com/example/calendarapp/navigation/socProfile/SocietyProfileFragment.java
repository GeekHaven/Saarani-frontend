package com.example.calendarapp.navigation.socProfile;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.calendarapp.R;
import com.example.calendarapp.adapters.SocietyCardAdaptor;
import com.example.calendarapp.data.ListItems;
import com.example.calendarapp.database.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SocietyProfileFragment extends Fragment  {

    private SocietyProfileViewModel mViewModel;
    TextView name,desc,email,no_event_text_view;
    CircleImageView imageView;
    String key,displayName;
    private RecyclerView recyclerView;
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
    private CardView cardView;
    private RecyclerView.Adapter adapter;
    final Date date = new Date();
    DatabaseHandler databaseHandler;
    HashMap<String,Integer> map = new HashMap<>();
    private List<ListItems> listItems;

    public static SocietyProfileFragment newInstance() {
        return new SocietyProfileFragment();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.society_profile_fragment, container, false);
        databaseHandler=new DatabaseHandler(getContext());
        listItems=new ArrayList<>();
        name=view.findViewById(R.id.society_name);
        email=view.findViewById(R.id.society_email);
        imageView=view.findViewById(R.id.society_profile);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        displayName=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        no_event_text_view=view.findViewById(R.id.no_event_text_view);



        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            Picasso
                    .get()
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .resize(110,110)
                    .transform(new CropCircleTransformation())
                    .into(imageView);
        }
        String nameo =FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(nameo.contains(" ")){
            String[] sep= nameo.split(" ");
            String nameNew="";
            for(int i=0;i<sep.length;i++) {
                if(i!=0)
                    sep[i] = sep[i].trim();
                sep[i] = sep[i].toLowerCase();
                sep[i] = sep[i].substring(0, 1).toUpperCase() + sep[i].substring(1);
                if(i==sep.length-1)
                    nameNew=nameNew+sep[i];
                else
                    nameNew=nameNew+sep[i]+" ";
            }
            name.setText(nameNew);
        }
        else{
            nameo=nameo.toLowerCase();
            nameo=nameo.substring(0, 1).toUpperCase() + nameo.substring(1);
            name.setText(nameo);
        }

        try {
            List<ListItems> allEvents=new ArrayList<>();
            map=new HashMap<>();
            allEvents=databaseHandler.getAllEvents();

            for(int i=0;i<allEvents.size();i++){
                ListItems item=allEvents.get(i);
                if(item.getByName().equals(displayName)){
                    if(!item.getState().equals("cancelled")&&(f.parse(f.format(date)).compareTo(f.parse(item.getDate()))<0||(f.parse(f.format(date)).compareTo(f.parse(item.getDate()))==0&&(f.parse(f.format(date)).compareTo(f.parse(item.getDate()))==0 && LocalTime.now().isBefore(LocalTime.parse(item.getTime().split(" ")[1]))))))
                    listItems.add(item);
                }
            }
            if(!listItems.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                no_event_text_view.setVisibility(View.INVISIBLE);
                for (int i = 0; i < listItems.size(); i++) {
                    if (map.getOrDefault(listItems.get(i).getEventId(), 0) != 0) {
                        listItems.remove(i);
                        /*databaseHandler.deleteEvent(listItems.get(i));*/
                        i--;
                    } else {
                        map.put(listItems.get(i).getEventId(), 1);
                    }
                }
                adapter = new SocietyCardAdaptor(listItems, getContext(), "alertDialog");
                recyclerView.setAdapter(adapter);
            }
            else{
                recyclerView.setVisibility(View.GONE);
                no_event_text_view.setVisibility(View.VISIBLE);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom"));
        mViewModel = ViewModelProviders.of(this).get(SocietyProfileViewModel.class);
        // TODO: Use the ViewModel
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            listItems.remove(intent.getExtras().getInt("position"));
            adapter.notifyDataSetChanged();
            Log.d("vary","brr");
        }
    };
}
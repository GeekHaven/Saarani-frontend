package com.example.calendarapp;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

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

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SocietyProfileFragment extends Fragment  {

    private SocietyProfileViewModel mViewModel;
    TextView name,desc,email;
    ImageView imageView;
    String key,displayName;
    private RecyclerView recyclerView;
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
    private CardView cardView;
    FloatingActionButton floatBtnAdd;
    private RecyclerView.Adapter adapter;
    final Date date = new Date();
    DatabaseHandler databaseHandler;
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
        //floatBtnAdd=view.findViewById(R.id.floatBtnProfile);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        displayName=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

//        floatBtnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent =new Intent(getActivity(),AddEventActivity.class);
//                intent.putExtra("type","add");
//                intent.putExtra("from","Profile");
//                startActivity(intent);
//            }
//        });

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
            allEvents=databaseHandler.getAllEvents();
            for(int i=0;i<allEvents.size();i++){
                ListItems item=allEvents.get(i);
                if(item.getByName().equals(displayName)){
                    if(!item.getState().equals("cancelled")&&(f.parse(f.format(date)).compareTo(f.parse(item.getDate()))<0||(f.parse(f.format(date)).compareTo(f.parse(item.getDate()))==0&&(f.parse(f.format(date)).compareTo(f.parse(item.getDate()))==0 && LocalTime.now().isBefore(LocalTime.parse(item.getTime().split(" ")[1]))))))
                    listItems.add(item);
                }
            }
            adapter=new SocietyCardAdaptor(listItems, new ClickListener() {
                @Override
                public void onPositionClicked(int position) {

                }

                @Override
                public void onLongClicked(int position) {

                }
            }, getContext(),"alertDialog");
            recyclerView.setAdapter(adapter);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

//        getSocKey();
//        addDataToRV();

        return view;
    }


    public void getSocKey(){
        String nameSoc = "";
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        FirebaseUser user =mAuth.getCurrentUser();
        String email= user.getEmail();
        String[] part= email.split("@");
        if(part[0].contains(".")){
            String[] namex= part[0].split("\\.");
            if(namex[1].equals("society")){
                nameSoc=namex[0];
            }
            else if(namex[1].equals("council")){
                nameSoc=namex[0];
            }
            else
                nameSoc=namex[1];
        }
        else{
            nameSoc=part[0];
        }
        key=nameSoc;
    }
    public void addDataToRV(){
        Log.d("key",key);
        String url = "https://socupdate.herokuapp.com/societies/"+key+"/events";
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading data....");
        progressDialog.show();
        StringRequest stringRequest =new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
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
                            Iterator iterator1= jsonObject1.keys();
                            while(iterator1.hasNext()){
                                String name= iterator1.next().toString();
                                attachmentsList.add(jsonObject1.getString(name));
                                attachmentNameList.add(name);
                            }
                        }
                        ListItems items = new ListItems(
                                jsonObject.getString("name"),
                                jsonObject.getString("desc"),
                                jsonObject.getString("byName"),
                                "Date: "+jsonObject.getString("date"),
                                "Time: "+jsonObject.getString("time"),
                                "Venue: "+jsonObject.getString("venue"),"",key,attachmentsList
                        );
                        items.setNameList(attachmentNameList);
                        listItems.add(items);
                    }
                    adapter=new SocietyCardAdaptor(listItems, new ClickListener() {
                        @Override
                        public void onPositionClicked(int position) {

                        }

                        @Override
                        public void onLongClicked(int position) {

                        }
                    }, getContext(),"alertDialog");
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
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
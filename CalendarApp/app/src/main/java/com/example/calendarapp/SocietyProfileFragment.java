package com.example.calendarapp;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SocietyProfileFragment extends Fragment  {

    private SocietyProfileViewModel mViewModel;
    TextView name,desc;
    ImageView imageView;
    String key;
    private RecyclerView recyclerView;
    private CardView cardView;
    private RecyclerView.Adapter adapter;
    final Date date = new Date();
    private List<ListItems> listItems;

    public static SocietyProfileFragment newInstance() {
        return new SocietyProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.society_profile_fragment, container, false);
        listItems=new ArrayList<>();
        name=view.findViewById(R.id.society_name);
        imageView=view.findViewById(R.id.society_profile);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        getSocKey();
        addDataToRV();

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
                        if(jsonObject.has("attachments")){
                            JSONArray jsonArray = jsonObject.getJSONArray("attachments");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                attachmentsList.add(jsonArray.getString(i));
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
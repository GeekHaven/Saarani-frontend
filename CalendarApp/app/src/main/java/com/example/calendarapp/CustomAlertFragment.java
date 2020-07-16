package com.example.calendarapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomAlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomAlertFragment extends DialogFragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView edit,delete,reminder,cancel;
    int position;
    TextView name,time,venue,date;
    AlertDialog.Builder builder;
    String eventName,byName,eventDate,eventVenue,eventTime,eventDesc,eventId;
    ArrayList<String> arrayList =new ArrayList<>();
    Context context;
//    MyDialogCloseListener closeListener;
    private DialogInterface.OnDismissListener onDismissListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    public interface MyDialogCloseListener {
//        public void handleDialogClose(DialogInterface dialog);
//    }
//
//    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
//        this.onDismissListener = onDismissListener;
//    }
//
//    public void DismissListener(MyDialogCloseListener closeListener) {
//        this.closeListener = closeListener;
//    }
    public CustomAlertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomAlertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomAlertFragment newInstance(String param1, String param2) {
        CustomAlertFragment fragment = new CustomAlertFragment();
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
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        if (onDismissListener != null) {
//            dialog.dismiss();
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.custom_alert_box, container, false);
        return view;
    }
    public int confirmation(String msg){
        final int[] val = {0};

        return val[0];
    }
    public void postRequest(final String msg){
        CustomAlertFragment.this.getDialog().dismiss();
        final String url="https://socupdate.herokuapp.com/events/"+eventId+"/"+msg;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        if(msg.equals("delete"))
            progressDialog.setMessage("Deleting Event...");
        else
            progressDialog.setMessage("Setting Reminder...");
        progressDialog.show();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if(task.isSuccessful()){
                        final HashMap<String,String> mapToken=new HashMap<String, String>();
                        mapToken.put("token",task.getResult().getToken());
                        Log.d("PostToken",task.getResult().getToken());
                        RequestQueue requstQueue = Volley.newRequestQueue(context);
                        progressDialog.dismiss();
                        Log.d("PostObject", String.valueOf(new JSONObject(mapToken)));
                        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(mapToken),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if(msg.equals("delete"))
                                            Toast.makeText(context,"Deletion Successful!",Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(context,"Reminder set successfully",Toast.LENGTH_SHORT).show();
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
    public void editEvent(){
        CustomAlertFragment.this.getDialog().dismiss();
        Intent intent=new Intent(getContext(),AddEventActivity.class);
        intent.putExtra("name",eventName);
        intent.putExtra("desc",eventDesc);
        intent.putExtra("byName",byName);
        intent.putExtra("date",eventDate);
        intent.putExtra("time",eventTime);
        intent.putExtra("venue",eventVenue);
        intent.putExtra("eventId",eventId);
        intent.putExtra("attachments",arrayList);
        intent.putExtra("type","edit");
        startActivity(intent);

    }
    public void sendBroadcast(String msg,int position){
        Intent intent = new Intent("custom");
        intent.putExtra("position",position);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        builder = new AlertDialog.Builder(getActivity());
        context=getActivity();
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view=inflater.inflate(R.layout.custom_alert_box, null);
        name=view.findViewById(R.id.eventTitle);
        date=view.findViewById(R.id.date);
        venue=view.findViewById(R.id.location);
        time =view.findViewById(R.id.time);
        cancel=view.findViewById(R.id.cancel);
        edit=view.findViewById(R.id.edit);
        delete=view.findViewById(R.id.delete);
        reminder=view.findViewById(R.id.reminder);
        eventName=getArguments().getString("name");
        eventDate=getArguments().getString("date");
        eventTime=getArguments().getString("time");
        eventVenue=getArguments().getString("venue");
        eventId=getArguments().getString("eventId");
        eventDesc=getArguments().getString("desc");
        byName=getArguments().getString("byName");
        arrayList=getArguments().getStringArrayList("attachments");
        position=getArguments().getInt("position");
        name.setText(eventName);
        date.setText(eventDate);
        time.setText(eventTime);
        venue.setText(eventVenue);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlertFragment.this.getDialog().dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder build = new AlertDialog.Builder(context);
                build.setMessage("Are you sure, you want to delete this event?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                postRequest("delete");
                                dialog.dismiss();
                                sendBroadcast("",position);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog=build.create();
                dialog.show();

            }
        });
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder build = new AlertDialog.Builder(context);
                build.setMessage("Are you sure, you want to send reminder for this event now ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                postRequest("remind");
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog=build.create();
                dialog.show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder build = new AlertDialog.Builder(context);
                build.setMessage("Are you sure, you want to edit this event ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                editEvent();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog=build.create();
                dialog.show();
            }
        });
        builder.setView(view);

        return builder.create();
    }
}
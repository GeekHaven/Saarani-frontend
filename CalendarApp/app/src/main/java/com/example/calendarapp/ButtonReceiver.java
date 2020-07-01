package com.example.calendarapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class ButtonReceiver extends BroadcastReceiver {
    FirebaseAuth mAuth;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(intent.getExtras().getString("action").equals("going")||intent.getExtras().getString("action").equals("interested")){
            final String url;
            final String mark=intent.getExtras().getString("action");
            String temp="https://socupdate.herokuapp.com/events/";
            url=temp+intent.getExtras().getString("eventId")+"/mark";
            String msg="Marked as " + intent.getExtras().getString("action");
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            mAuth=FirebaseAuth.getInstance();
            final FirebaseUser user= mAuth.getCurrentUser();
            if(user!=null){
                user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().getToken()!=null) {
                                HashMap<String,String> map=new HashMap<String, String>();
                                map.put("token",task.getResult().getToken());
                                map.put("mark",mark);
                                RequestQueue requstQueue = Volley.newRequestQueue(context);
                                JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(map),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

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
                    }
                });
            }
            int id = Objects.requireNonNull(intent.getExtras()).getInt("notifid", 0);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(id);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
            NotificationCompat.Builder mb = new NotificationCompat.Builder(context);
            mb.setContentIntent(resultPendingIntent);
        }
        else {
            int id = Objects.requireNonNull(intent.getExtras()).getInt("notifid", 0);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(id);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
            NotificationCompat.Builder mb = new NotificationCompat.Builder(context);
            mb.setContentIntent(resultPendingIntent);
        }
    }
}

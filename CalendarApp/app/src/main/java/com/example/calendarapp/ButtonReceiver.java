package com.example.calendarapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class ButtonReceiver extends BroadcastReceiver {
    FirebaseAuth mAuth;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras().getString("action").equals("going")||intent.getExtras().getString("action").equals("interested")){
            String url;
            String mark=intent.getExtras().getString("action");
            String temp="https://socupdate.herokuapp.com/events/";
            url=temp+intent.getExtras().getString("eventId")+"/mark";
            HashMap<String,String> map=new HashMap<String, String>();
            map.put("token",intent.getExtras().getString("idToken"));
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

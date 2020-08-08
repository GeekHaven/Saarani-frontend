package com.example.calendarapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyWorker extends Worker {
    Context context_i;
    private static String url="https://socupdate.herokuapp.com/events/marked";
    public MyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        context_i=context;
    }

    @Override
    public Result doWork() {
        final DatabaseHandler databaseHandler=new DatabaseHandler(context_i);
        try {
            databaseHandler.deleteDatabase();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String,String> map_post=getPostObject();

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map_post), future, future);

        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
        try {

            JSONObject response = future.get(60, TimeUnit.SECONDS);
            Iterator<String> keys = response.keys();
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
                    databaseHandler.addEvent(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            databaseHandler.close();
            Log.d("worker_tag", response.toString());
            return Result.success();
        } catch (InterruptedException e) {
            e.printStackTrace();
            // exception handling
            return Result.failure();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Result.failure();
            // exception handling
        } catch (TimeoutException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
    public HashMap<String,String> getPostObject() {
        final HashMap<String,String> mapToken=new HashMap<String, String>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    try {
                        Tasks.await(task);
                        if(task.isSuccessful()){
                            mapToken.put("token", Objects.requireNonNull(task.getResult()).getToken());
                            Log.d("PostToken", Objects.requireNonNull(task.getResult().getToken()));
                            Log.d("PostObject", String.valueOf(new JSONObject(mapToken)));

                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return mapToken;
    }
}
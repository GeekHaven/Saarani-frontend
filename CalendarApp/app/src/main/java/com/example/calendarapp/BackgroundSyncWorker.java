package com.example.calendarapp;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BackgroundSyncWorker extends Worker {
    Context context_i;
    private static String url="https://socupdate.herokuapp.com/events/marked";
    public BackgroundSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        context_i=context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public Result doWork() {

        final DatabaseHandler databaseHandler=new DatabaseHandler(context_i);
        HashMap<String,String> map_post=getPostObject();

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map_post), future, future);

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
        try {
            JSONObject response = future.get(60, TimeUnit.SECONDS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                databaseHandler.deleteDatabase();
            }
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
                    item.setGoing(going_count);
                    item.setInterested(interested_count);
                    //databaseHandler.updateCount(item,"going",)
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
        } catch (ParseException e) {
            e.printStackTrace();
            return Result.failure();
        } catch (JSONException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public HashMap<String,String> getPostObject() {
        final HashMap<String,String> mapToken=new HashMap<String, String>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Task<GetTokenResult> task = user.getIdToken(true);
            try {
                GetTokenResult result = Tasks.await(task);
                mapToken.put("token", Objects.requireNonNull(result).getToken());
                Log.d("PostToken", Objects.requireNonNull(Objects.requireNonNull(result).getToken()));
                Log.d("PostObject", String.valueOf(new JSONObject(mapToken)));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mapToken;
    }
}

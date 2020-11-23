package com.geekhaven.caliiita.singleton;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;
    private VolleySingleton(Context context){
        mContext = context;
        //Get the requiest queue
        mRequestQueue = getmRequestQueue();
    }
    public static synchronized VolleySingleton getmInstance(Context context){
        //If instance is null then initialize new instance
        if(mInstance == null){
            mInstance = new VolleySingleton(context);
        }
        //Return MySingleton new instance
        return mInstance;
    }
    public RequestQueue getmRequestQueue() {
        //If RequestQueue is null the intialize new RequestQueue
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public<T> void addToRequestQueue(Request<T> request){
        //Add the specified request to the request queue
        getmRequestQueue().add(request);
    }
}

package com.geekhaven.caliiita.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.geekhaven.caliiita.alertDialog.CustomAlertFragment;
import com.geekhaven.caliiita.data.ListItems;
import com.geekhaven.caliiita.R;
import com.geekhaven.caliiita.activites.EventActivity;
import com.geekhaven.caliiita.database.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.geekhaven.caliiita.R.drawable.star_img;
import static com.geekhaven.caliiita.R.drawable.star_yellow;
import static com.geekhaven.caliiita.R.drawable.tick;
import static com.geekhaven.caliiita.R.drawable.tick_yellow;


public class SocietyCardAdaptor extends RecyclerView.Adapter<SocietyCardAdaptor.ViewHolder> {
    private List<ListItems> listItems,listUpdated;
    private final Context context;
    private String eventId,marker,clickAction;
    DialogFragment newFragment = new CustomAlertFragment();
    HashMap<Integer,String>map =new HashMap<>();
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    final String[] Selected = new String[]{"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.getDefault());
    Date date=new Date();
    DatabaseHandler databaseHandler;
    SharedPreferences prefs ;
    boolean isSociety=false;

    public SocietyCardAdaptor(List<ListItems> listItems, Context context, String clickAction) {
        this.listItems = listItems;
        this.context = context;
        this.clickAction=clickAction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_event_item, parent, false);
        prefs=context.getSharedPreferences("user", MODE_PRIVATE);
        isSociety= !prefs.getString("society", "false").equals("false");
        databaseHandler=new DatabaseHandler(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItems listItem= listItems.get(position);
        String name=listItem.getName();
        if(listItem.getName().length()>16){
            name=name.substring(0,16)+"...";
        }
        holder.name.setText(name);
        holder.venue.setText(listItem.getVenue());
        holder.date.setText(listItem.getDate());
        holder.time.setText(listItem.getTime());
        if(listItem.getInterested()!=null || listItem.getGoing()!=null) {
            if (listItem.getInterested() != 0)
                holder.interestedCount.setText("" + listItem.getInterested());
            else
                holder.interestedCount.setText("0");
            if (listItem.getGoing() != 0)
                holder.goingCount.setText("" + listItem.getGoing());
            else
                holder.goingCount.setText("0");
        }
        if(listItem.getMarker().equals("interested") && !isSociety){
            holder.interested.setChecked(true);
            holder.interested.setTag(star_yellow);
        }
        else if(listItem.getMarker().equals("going") && !isSociety){
            holder.going.setChecked(true);
            holder.going.setTag(tick_yellow);
            Log.d("set","true");
        }
        else{
            holder.interested.setTag(star_img);
            holder.going.setTag(tick);
        }

        eventId=listItem.getEventId();
        map.put(position,eventId);

        Date date_parsed = null;
        try {
            date_parsed = f.parse(listItem.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] date_split= listItem.getDate().split("/");
        String date_cal= sdf.format(date_parsed).substring(0,3) + ", "+ date_split[0] + " " +Selected[Integer.parseInt(date_split[1])-1];
        if(position==0){
            holder.headerText.setVisibility(View.VISIBLE);
            try {
                if(f.parse(f.format(date)).compareTo(f.parse(listItem.getDate()))==0){
                    String sourceString = "<b>" + "TODAY  " + "</b> " + date_cal;
                    holder.headerText.setText(Html.fromHtml(sourceString));
                }
                else{
                    holder.headerText.setText(date_cal);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            ListItems listItemsPrev= listItems.get(position-1);
            try {
                if(f.parse(listItem.getDate()).compareTo(f.parse(listItemsPrev.getDate()))!=0){
                    holder.headerText.setVisibility(View.VISIBLE);
                    holder.headerText.setText(date_cal);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name;
        TextView venue;
        TextView date;
        TextView time;
        TextView headerText;
        TextView interestedCount, goingCount;
        ShineButton interested,going;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            venue = itemView.findViewById(R.id.venue);
            interested=itemView.findViewById(R.id.mark);
            going=itemView.findViewById(R.id.going);
            interestedCount=itemView.findViewById(R.id.interested_count);
            goingCount=itemView.findViewById(R.id.going_count);
            headerText=itemView.findViewById(R.id.date_strip);
            interested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClicked(v);
                }
            });
            going.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClicked(v);
                }
            });
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        public void buttonClicked(View v){
            if (v.getId() == interested.getId()) {
                if (prefs.getString("society", "false").equals("false")) {
                    String action="none";
                    if(isOnline()) {
                        Object tag = interested.getTag();
                        if (tag != null && (Integer) tag == star_yellow) {
                            action="unmarked";
                            Snackbar.make(v, "Unmarked", Snackbar.LENGTH_SHORT).show();
                            interested.setTag(R.drawable.star_img);
                            interestedCount.setText(String.valueOf(listItems.get(getAdapterPosition()).getInterested() - 1));
                            try {
                                databaseHandler.updateCount(listItems.get(getAdapterPosition()), "-", "interested");
                                listItems.get(getAdapterPosition()).setInterested(listItems.get(getAdapterPosition()).getInterested() - 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            interested.setChecked(false, true);
                            try {
                                deleteRequest(this.getAdapterPosition());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            action ="marked";
                            Snackbar.make(v, "Marked as interested", Snackbar.LENGTH_LONG).show();
                            interested.setTag(star_yellow);
                            interested.setChecked(true, true);
                            listItems.get(getAdapterPosition()).setInterested(listItems.get(getAdapterPosition()).getInterested() + 1);
                            interestedCount.setText(String.valueOf(listItems.get(getAdapterPosition()).getInterested()));
                            String x = "-";
                            int fg=0;
                            if ((Integer) going.getTag() == tick_yellow) {
                                fg=1;
                                going.setTag(tick);
                                going.setChecked(false);
                                listItems.get(getAdapterPosition()).setGoing(listItems.get(getAdapterPosition()).getGoing() - 1);
                                goingCount.setText(String.valueOf(listItems.get(getAdapterPosition()).getGoing()));
                                x = "going";
                                action="marked|going";
                            }
                            try {
                                databaseHandler.updateCount(listItems.get(getAdapterPosition()), "interested", x);
                                //listItems.get(getAdapterPosition()).setInterested(listItems.get(getAdapterPosition()).getInterested()+1);
                                //listItems.get(getAdapterPosition()).setGoing(listItems.get(getAdapterPosition()).getGoing()-1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                addMarker(this.getAdapterPosition(), "interested");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else{
                        if(interested.isChecked()) {
                            if(listItems.get(getAdapterPosition()).getMarker().equals("none")) {
                                interested.setChecked(false);
                            }
                            else if(listItems.get(getAdapterPosition()).getMarker().equals("going")){
                                interested.setChecked(false);
                                going.setChecked(true);
                            }

                        }
                        else {
                            interested.setChecked(true);
                        }
                        Snackbar.make(v, "Not connected to internet! Please try again later.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    going.setChecked(false);
                    interested.setChecked(false);
                    Snackbar.make(v, "This feature is not available for societies", Snackbar.LENGTH_LONG).show();
                }
            } else if (v.getId() == going.getId()) {
                if (prefs.getString("society", "false").equals("false")) {
                    if(isOnline()) {
                        Object tag = going.getTag();
                        if (tag != null && (Integer) tag == tick_yellow) {
                            Snackbar.make(v, "Unmarked", Snackbar.LENGTH_LONG).show();
                            going.setTag(tick);
                            going.setChecked(false, true);
                            listItems.get(getAdapterPosition()).setGoing(listItems.get(getAdapterPosition()).getGoing() - 1);
                            goingCount.setText(String.valueOf(listItems.get(getAdapterPosition()).getGoing()));
                            try {
                                databaseHandler.updateCount(listItems.get(getAdapterPosition()), "-", "going");
                                //listItems.get(getAdapterPosition()).setGoing(listItems.get(getAdapterPosition()).getGoing()-1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                deleteRequest(this.getAdapterPosition());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Snackbar.make(v, "Marked as going.", Snackbar.LENGTH_LONG).show();
                            going.setTag(tick_yellow);
                            going.setChecked(true, true);
                            String x = "-";
                            listItems.get(getAdapterPosition()).setGoing(listItems.get(getAdapterPosition()).getGoing() + 1);
                            goingCount.setText(String.valueOf(listItems.get(getAdapterPosition()).getGoing()));
                            int fg=0;
                            if ((Integer) interested.getTag() == star_yellow) {
                                fg=1;
                                interested.setTag(R.drawable.star_img);
                                interested.setChecked(false);
                                x = "interested";
                                listItems.get(getAdapterPosition()).setInterested(listItems.get(getAdapterPosition()).getInterested() - 1);
                                interestedCount.setText(String.valueOf(listItems.get(getAdapterPosition()).getInterested()));
                            }
                            try {
                                databaseHandler.updateCount(listItems.get(getAdapterPosition()), "going", x);
                                //listItems.get(getAdapterPosition()).setInterested(listItems.get(getAdapterPosition()).getInterested()-1);
                                //listItems.get(getAdapterPosition()).setGoing(listItems.get(getAdapterPosition()).getGoing()+1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                addMarker(this.getAdapterPosition(), "going");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else {

                        if(going.isChecked()) {
                            if(listItems.get(getAdapterPosition()).getMarker().equals("none")) {
                                going.setChecked(false);
                            }
                            else if(listItems.get(getAdapterPosition()).getMarker().equals("interested")){
                                interested.setChecked(true);
                                going.setChecked(false);
                            }

                        }
                        else {
                            going.setChecked(true);
                        }

                        Snackbar.make(v, "Not connected to internet! Please try again later.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    going.setChecked(false);
                    interested.setChecked(false);
                    Snackbar.make(v, "This feature is not available for societies", Snackbar.LENGTH_LONG).show();
                }

            }
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }

        public void deleteRequest(int position) throws JSONException {
            FirebaseAuth mAuth= FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            databaseHandler.updateMarker(listItems.get(position),"none");
            marker="none";
            listItems.get(position).setMarker("none");
            //mapMarker.put(position,"none");
            final String url="https://socupdate.herokuapp.com/events/"+listItems.get(position).getEventId()+"/mark/delete";
            if(user!=null){
                user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if(task.getResult().getToken()!=null){
                            HashMap<String,String> mapToken=new HashMap<String, String>();
                            mapToken.put("token",task.getResult().getToken());
                            Log.d("deleteToken", String.valueOf(new JSONObject(mapToken)));
                            final RequestQueue requstQueue = Volley.newRequestQueue(context);
                            JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(mapToken),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d("respone",String.valueOf(response));
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("error",error.toString());
                                        }
                                    }
                            );
                            requstQueue.add(jsonobj);
                        }
                    }
                });
            }
        }

        public void addMarker(int position, final String mark) throws JSONException {
            FirebaseAuth mAuth= FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            databaseHandler.updateMarker(listItems.get(position),mark);
            Log.d("updated",mark);
            Log.d("up",listItems.get(position).getMarker());
            marker=mark;
            //mapMarker.put(position,mark);
            listItems.get(position).setMarker(mark);
            //listItems.get(position).set
            final String url="https://socupdate.herokuapp.com/events/"+listItems.get(position).getEventId()+"/mark";
            if(user!=null){
                user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if(task.getResult().getToken()!=null){
                            HashMap<String,String> mapToken=new HashMap<String, String>();
                            mapToken.put("token",task.getResult().getToken());
                            mapToken.put("mark",mark);
                            Log.d("deleteToken", String.valueOf(new JSONObject(mapToken)));
                            final RequestQueue requstQueue = Volley.newRequestQueue(context);
                            JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(mapToken),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d("respone",String.valueOf(response));
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("error",error.toString());
                                        }
                                    }
                            );
                            requstQueue.add(jsonobj);
                        }
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            if(clickAction.equals("alertDialog")) {

                ListItems items = listItems.get(getAdapterPosition());

                Bundle bundle = new Bundle();
                bundle.putString("name", items.getName());
                bundle.putString("byName", items.getByName());
                bundle.putString("desc", items.getDesc());
                bundle.putString("date", items.getDate());
                bundle.putString("venue", items.getVenue());
                bundle.putString("time", items.getTime());
                bundle.putString("eventId", items.getEventId());
                bundle.putStringArrayList("attachments", items.getArrayList());
                //Log.d("attach", String.valueOf(items.getArrayList()));
                bundle.putStringArrayList("attachments_name",items.getNameList());
                bundle.putInt("position", getAdapterPosition());
                newFragment.setArguments(bundle);
                newFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "alertDialog");
            }
            else{
                Intent intent =new Intent(context, EventActivity.class);
                ListItems items = listItems.get(getAdapterPosition());
                intent.putExtra("name",items.getName());
                intent.putExtra("byName",items.getByName());
                intent.putExtra("desc",items.getDesc());
                intent.putExtra("time",items.getTime());
                intent.putExtra("venue",items.getVenue());
                intent.putExtra("date",items.getDate());
                intent.putExtra("marker",items.getMarker());
                intent.putExtra("eventId",items.getEventId());
                intent.putExtra("type","event");
                intent.putExtra("screen","soc");
                intent.putExtra("interested",items.getInterested());
                intent.putExtra("going",items.getGoing());
                intent.putStringArrayListExtra("attachments", items.getArrayList());
                intent.putStringArrayListExtra("attachments_name",items.getNameList());
                context.startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}


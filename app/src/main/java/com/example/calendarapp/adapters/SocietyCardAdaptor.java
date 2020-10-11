package com.example.calendarapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapp.alertDialog.CustomAlertFragment;
import com.example.calendarapp.data.ListItems;
import com.example.calendarapp.R;
import com.example.calendarapp.activites.EventActivity;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class SocietyCardAdaptor extends RecyclerView.Adapter<SocietyCardAdaptor.ViewHolder> {
    private List<ListItems> listItems,listUpdated;
    private Context context;
    private String eventId,marker,clickAction;
    DialogFragment newFragment = new CustomAlertFragment();
    HashMap<Integer,String>map =new HashMap<>();
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    final String[] Selected = new String[]{"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.getDefault());
    Date date=new Date();

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
        holder.interested.setChecked(true);
        holder.going.setChecked(true);
        holder.going.setClickable(false);
        holder.interested.setChecked(true);
        holder.interested.setClickable(false);
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
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
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


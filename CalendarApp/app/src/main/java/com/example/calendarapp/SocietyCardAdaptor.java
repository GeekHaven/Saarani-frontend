package com.example.calendarapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SocietyCardAdaptor extends RecyclerView.Adapter<SocietyCardAdaptor.ViewHolder> {
    private List<ListItems> listItems,listUpdated;
    private final ClickListener listener;
    private Context context;
    private String eventId,marker;
    DialogFragment newFragment = new CustomAlertFragment();
    HashMap<Integer,String>map =new HashMap<>();


    SocietyCardAdaptor(List<ListItems> listItems, ClickListener listener, Context context) {
        this.listItems = listItems;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.society_single_event, parent, false);
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
        eventId=listItem.getEventId();
        map.put(position,eventId);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name;
        TextView venue;
        TextView date;
        TextView time;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            venue = itemView.findViewById(R.id.venue);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ListItems items =listItems.get(getAdapterPosition());

            Bundle bundle=new Bundle();
            bundle.putString("name",items.getName() );
            bundle.putString("byName",items.getByName());
            bundle.putString("desc",items.getDesc());
            bundle.putString("date",items.getDate());
            bundle.putString("venue",items.getVenue());
            bundle.putString("time",items.getTime());
            bundle.putString("eventId",items.getEventId());
            bundle.putStringArrayList("attachments",items.getArrayList());
            bundle.putInt("position",getAdapterPosition());
            newFragment.setArguments(bundle);
            newFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "alertDialog");
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}

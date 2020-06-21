package com.example.calendarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptorActivity extends RecyclerView.Adapter<AdaptorActivity.ViewHolder> {

    private List<ListItems> listItems;
    private Context context;

    AdaptorActivity(List<ListItems> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItems listItem = listItems.get(position);
        holder.name.setText(listItem.getName());
        holder.desc.setText(listItem.getDesc());
        holder.byName.setText(listItem.getByName());
        holder.venue.setText(listItem.getVenue());
        holder.date.setText(listItem.getDate());
        holder.time.setText(listItem.getTime());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView desc;
        TextView byName;
        TextView venue;
        TextView date;
        TextView time;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            desc = itemView.findViewById(R.id.desc);
            byName=itemView.findViewById(R.id.eventBy);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            venue = itemView.findViewById(R.id.venue);
        }
    }

}


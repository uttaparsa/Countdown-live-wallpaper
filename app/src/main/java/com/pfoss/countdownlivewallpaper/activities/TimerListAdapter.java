package com.pfoss.countdownlivewallpaper.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;

import java.util.ArrayList;

public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.TimerListViewHolder> {
    private ArrayList<TimerRecord> mTimersData;
    public interface OnItemClickListener{
        void onItemClick(int position);

    }
    OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;

    }
    public static class TimerListViewHolder extends RecyclerView.ViewHolder {
        public TextView mTimerName;
        public TextView mTimerDate;

        public TimerListViewHolder(@NonNull View itemView , final OnItemClickListener listener) {
            super(itemView);
            mTimerName = itemView.findViewById(R.id.timer_name);
            mTimerDate = itemView.findViewById(R.id.timer_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }

                    }

                }
            });
        }
    }

    public TimerListAdapter(ArrayList<TimerRecord> timers) {
        this.mTimersData = timers;

    }

    @NonNull
    @Override
    public TimerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_list, parent, false);
        TimerListViewHolder tlvh = new TimerListViewHolder(v , mListener);
        return tlvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TimerListViewHolder holder, int position) {
        TimerRecord currentTimerData = mTimersData.get(position);
        holder.mTimerName.setText(currentTimerData.getLabel());
        holder.mTimerDate.setText(currentTimerData.getDate());



    }

    @Override
    public int getItemCount() {
        return mTimersData.size();
    }
}

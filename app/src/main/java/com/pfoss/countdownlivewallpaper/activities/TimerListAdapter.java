package com.pfoss.countdownlivewallpaper.activities;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.ActiveShowUnits;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.utils.RuntimeTools;
import com.pfoss.countdownlivewallpaper.utils.UnitType;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pfoss.countdownlivewallpaper.utils.BitmapHelper.decodeSampledBitmapFromFile;

public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.TimerListViewHolder> {
    private ArrayList<TimerRecord> mTimersData;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

    public static class TimerListViewHolder extends RecyclerView.ViewHolder {
        public TextView mTimerName;
        public TextView mRemaining;
        public TextView mDateTime;
        public TextView[] mNumbersTextViews = new TextView[12];
        private RelativeLayout timerListItemHolder;
        public ImageView timerItemBackgroundImage;

        public TimerListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTimerName = itemView.findViewById(R.id.timer_name);
            mNumbersTextViews[0] = itemView.findViewById(R.id.year_place_holder_timer_list);
            mNumbersTextViews[1] = itemView.findViewById(R.id.year_unit_place_holder_timer_list);
            mNumbersTextViews[2] = itemView.findViewById(R.id.month_place_holder_timer_list);
            mNumbersTextViews[3] = itemView.findViewById(R.id.month_unit_place_holder_timer_list);
            mNumbersTextViews[4] = itemView.findViewById(R.id.day_place_holder_timer_list);
            mNumbersTextViews[5] = itemView.findViewById(R.id.day_unit_place_holder_timer_list);
            mNumbersTextViews[6] = itemView.findViewById(R.id.hour_place_holder_timer_list);
            mNumbersTextViews[7] = itemView.findViewById(R.id.hour_unit_place_holder_timer_list);
            mNumbersTextViews[8] = itemView.findViewById(R.id.minute_place_holder_timer_list);
            mNumbersTextViews[9] = itemView.findViewById(R.id.minute_unit_place_holder_timer_list);
            mNumbersTextViews[10] = itemView.findViewById(R.id.second_place_holder_timer_list);
            mNumbersTextViews[11] = itemView.findViewById(R.id.second_unit_place_holder_timer_list);
            mRemaining = itemView.findViewById(R.id.remaining_timer_list_item);
            timerListItemHolder = itemView.findViewById(R.id.relativePar);
            mDateTime = itemView.findViewById(R.id.datetime_text_view);
            timerItemBackgroundImage = itemView.findViewById(R.id.timer_item_background_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
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
        TimerListViewHolder tlvh = new TimerListViewHolder(v, mListener);
        context = parent.getContext();
        return tlvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TimerListViewHolder holder, int position) {
        TimerRecord currentTimerData = mTimersData.get(position);
        setTimerListItemBackground(holder, currentTimerData);
        holder.mTimerName.setText(currentTimerData.getLabel());
        printRemainingTimeString(currentTimerData, holder);
        printTimerDateTime(holder, currentTimerData);

    }

    private void printTimerDateTime(@NonNull TimerListViewHolder holder, TimerRecord currentRecord) {
        PersianCalendar persianCalendar = new PersianCalendar(currentRecord.getDateInstance().getTime());
        String date = (RuntimeTools.isPersian()) ? persianCalendar.getPersianShortDate() : DateFormat.getDateInstance().format(currentRecord.getDateInstance());
        holder.mDateTime.setText(date);
    }

    private void setTimerListItemBackground(@NonNull TimerListViewHolder holder, TimerRecord timerRecord) {

        switch (timerRecord.getBackgroundTheme()) {
            case GRADIENT:
                holder.timerListItemHolder.setBackgroundColor(Color.TRANSPARENT);
                holder.timerItemBackgroundImage.setVisibility(View.INVISIBLE);
                holder.timerListItemHolder.setBackgroundResource(R.drawable.imageview_background);
                break;
            case PICTURE:
                holder.timerListItemHolder.setBackgroundResource(0);
                holder.timerItemBackgroundImage.setMaxHeight(holder.timerListItemHolder.getHeight());
                holder.timerItemBackgroundImage.setVisibility(View.VISIBLE);

                try {
                    holder.timerItemBackgroundImage.setImageBitmap(decodeSampledBitmapFromFile(timerRecord, holder.timerListItemHolder.getWidth(), holder.timerListItemHolder.getHeight()));
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                }
                break;
            case SOLID:
                holder.timerListItemHolder.setBackgroundResource(0);
                holder.timerItemBackgroundImage.setVisibility(View.INVISIBLE);
                holder.timerListItemHolder.setBackgroundColor(timerRecord.getBackGroundColor());
                break;

        }

    }

    public void printRemainingTimeString(TimerRecord timerRecord, TimerListViewHolder holder) {
        int timeDifference = timerRecord.getTimeDifference(Calendar.getInstance().getTime());
        if (timeDifference > 0) {
            holder.mRemaining.setText(context.getResources().getString(R.string.since));
        } else {
            holder.mRemaining.setText(context.getResources().getString(R.string.until));
        }
        AtomicInteger timeDifferenceInSeconds = new AtomicInteger(Math.abs(timeDifference));
        drawTimeUnit(UnitType.YEAR, timeDifferenceInSeconds, holder, 0, timerRecord.getActiveShowUnits());
        drawTimeUnit(UnitType.MONTH, timeDifferenceInSeconds, holder, 2, timerRecord.getActiveShowUnits());
        drawTimeUnit(UnitType.DAY, timeDifferenceInSeconds, holder, 4, timerRecord.getActiveShowUnits());
        drawTimeUnit(UnitType.HOUR, timeDifferenceInSeconds, holder, 6, timerRecord.getActiveShowUnits());
        drawTimeUnit(UnitType.MINUTE, timeDifferenceInSeconds, holder, 8, timerRecord.getActiveShowUnits());
        holder.mRemaining.setTextColor(timerRecord.getTextColor());
        holder.mTimerName.setTextColor(timerRecord.getTextColor());
        holder.mDateTime.setTextColor(timerRecord.getTextColor());
        for (int i = 0; i < holder.mNumbersTextViews.length; ++i) {
            holder.mNumbersTextViews[i].setTextColor(timerRecord.getTextColor());

        }
    }

    private void drawTimeUnit(UnitType unitType, AtomicInteger timeDifferenceInSeconds, TimerListViewHolder holder, int i, ActiveShowUnits asu) {

        int unitValue = 0;
        if (asu.getActiveShowUnitsBoolArray()[i / 2]) {
            if (timeDifferenceInSeconds.get() > unitType.getUnitInSecond()) {
                holder.mNumbersTextViews[i].setVisibility(View.VISIBLE);
                holder.mNumbersTextViews[i + 1].setVisibility(View.VISIBLE);
                unitValue = (timeDifferenceInSeconds.get() / unitType.getUnitInSecond());
                holder.mNumbersTextViews[i].setText(String.valueOf(unitValue));
                String unitName = context.getResources().getString(unitType.getStringResource());

                if (RuntimeTools.isPersian()) {
                    holder.mNumbersTextViews[i + 1].setText(unitName);
                } else {
                    if (unitType.equals(UnitType.MINUTE)) {
                        holder.mNumbersTextViews[i + 1].setText(String.valueOf(unitName.charAt(0)).toLowerCase());
                    } else {
                        holder.mNumbersTextViews[i + 1].setText(String.valueOf(unitName.charAt(0)));
                    }

                }
                timeDifferenceInSeconds.set(timeDifferenceInSeconds.get() - (unitValue * unitType.getUnitInSecond()));
            } else {
                holder.mNumbersTextViews[i].setVisibility(View.GONE);
                holder.mNumbersTextViews[i + 1].setVisibility(View.GONE);
            }

        } else {
            holder.mNumbersTextViews[i].setVisibility(View.GONE);
            holder.mNumbersTextViews[i + 1].setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mTimersData.size();
    }
}

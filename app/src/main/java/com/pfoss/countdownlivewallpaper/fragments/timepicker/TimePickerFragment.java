package com.pfoss.countdownlivewallpaper.fragments.timepicker;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

public abstract class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    TimePickerFragment.OnTimeSetListener mListener;

    public interface OnTimeSetListener {
        void onTimeSet(int hour, int minute);
    }

    public TimePickerFragment(TimePickerFragment.OnTimeSetListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {//Gregorian calender picker listener
        this.mListener.onTimeSet(hour, minute);
    }

    public abstract void displayTimePicker(Context context);
}
package com.pfoss.countdownlivewallpaper.fragments.datepicker;

import android.content.Context;

import androidx.fragment.app.DialogFragment;

public abstract class DatePickerFragment extends DialogFragment {
    OnDateSetListener mListener;

    public interface OnDateSetListener {
        void onDateSet(int year, int month, int day);
    }

    public DatePickerFragment(OnDateSetListener listener) {
        this.mListener = listener;
    }

    public abstract void displayCalenderDialog(Context context);
}
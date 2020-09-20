package com.pfoss.countdownlivewallpaper.fragments.datepicker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

public class GregorianDatePicker extends DatePickerFragment implements DatePickerDialog.OnDateSetListener {

    public GregorianDatePicker(OnDateSetListener listener) {
        super(listener);
    }

    @Override
    public void displayCalenderDialog(Context context) {
        Calendar today = Calendar.getInstance();
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(context,
                this,
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {//Gregorian calender picker listener
        this.mListener.onDateSet(year, month, day);
    }

}
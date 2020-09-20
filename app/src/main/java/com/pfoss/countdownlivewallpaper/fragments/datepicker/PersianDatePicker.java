package com.pfoss.countdownlivewallpaper.fragments.datepicker;

import android.app.Activity;
import android.content.Context;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

public class PersianDatePicker extends DatePickerFragment implements com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    public PersianDatePicker(OnDateSetListener listener) {
        super(listener);
    }

    @Override
    public void displayCalenderDialog(Context context) {
        PersianCalendar persianCalendar = new PersianCalendar();
        com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog persianPickerDialog = com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog.newInstance(
                this,
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );
        persianPickerDialog.show(((Activity) context).getFragmentManager(), "persianPickerDialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mListener.onDateSet(year, monthOfYear, dayOfMonth);
    }
}

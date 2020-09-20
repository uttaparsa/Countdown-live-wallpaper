package com.pfoss.countdownlivewallpaper.fragments.timepicker;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;

public class GregorianTimePicker extends TimePickerFragment {

    public GregorianTimePicker(OnTimeSetListener listener) {
        super(listener);
    }

    public void displayTimePicker(Context context) {
        TimePickerDialog timePickerDialog;
        Calendar newDate = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(context,
                this,
                newDate.get(Calendar.HOUR_OF_DAY),
                newDate.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(context));

        timePickerDialog.show();

    }
}
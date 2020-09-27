package com.pfoss.countdownlivewallpaper.fragments.timepicker;


import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;

import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

public class PersianTimePicker extends TimePickerFragment implements com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {
    public PersianTimePicker(OnTimeSetListener listener) {
        super(listener);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        this.mListener.onTimeSet(hourOfDay, minute);
    }

    @Override
    public void displayTimePicker(Context context) {
        com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog persianTimePicker;
        PersianCalendar now = new PersianCalendar();
        persianTimePicker = com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog.newInstance(
                this,
                now.get(PersianCalendar.HOUR_OF_DAY),
                now.get(PersianCalendar.MINUTE),
                DateFormat.is24HourFormat(context));


        persianTimePicker.show(((Activity) context).getFragmentManager(), "persianTimePicker");
    }
}

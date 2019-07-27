package com.pfoss.countdownlivewallpaper;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;


public class WallpaperCreateActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    DatePickerDialog datePickerDialog;
    SharedPreferences timersSharedPrefrences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_create);

        final Button createButton = (Button) findViewById(R.id.createTimeButton);
        final Button dateSetButton = (Button) findViewById(R.id.setDateAndTimeDialogButton);
        dateSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("GOT","date button click");
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, day);
                DatePickerDialog datePickerDialog = new DatePickerDialog(WallpaperCreateActivity.this, WallpaperCreateActivity.this, year, month, day);
                datePickerDialog.show();;

            }
        });
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1+1;
        dayFinal = i2;
        Calendar newDate = Calendar.getInstance();
        hour = newDate.get(Calendar.HOUR_OF_DAY);
        minute = newDate.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(WallpaperCreateActivity.this,WallpaperCreateActivity.this , hour,minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;
        Log.i("GOT","This is in order: "+yearFinal + " "+monthFinal + " "+dayFinal + " "+hourFinal + " "+minuteFinal + " ");
    }

    protected  void createNewTimer(View view){
       timersSharedPrefrences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
    }
}

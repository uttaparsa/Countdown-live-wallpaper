package com.pfoss.countdownlivewallpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


public class WallpaperCreateActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    int day, month, year, hour, minute, second;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, secondFinal;
    EditText labelEditText;
    Button dateSetButton;
    Button createButton;
    DatePickerDialog datePickerDialog;
    SharedPreferences timersSharedPrefrences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_create);

        createInstanceOfViews();

        dateSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("GOT", "date button click");
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, day);
                DatePickerDialog datePickerDialog = new DatePickerDialog(WallpaperCreateActivity.this, WallpaperCreateActivity.this, year, month, day);
                datePickerDialog.show();

            }
        });
    }

    private void createInstanceOfViews() {
        createButton = (Button) findViewById(R.id.createTimeButton);
        dateSetButton = (Button) findViewById(R.id.setDateAndTimeDialogButton);
        labelEditText = (EditText) findViewById(R.id.labelEditText);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;
        Calendar newDate = Calendar.getInstance();
        hour = newDate.get(Calendar.HOUR_OF_DAY);
        minute = newDate.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(WallpaperCreateActivity.this, WallpaperCreateActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;
        Log.i("GOT", "This is in order: " + yearFinal + " " + monthFinal + " " + dayFinal + " " + hourFinal + " " + minuteFinal + " ");
    }

    protected void createNewTimer(View view) {

        TimerRecord newRecord = createRecordObject();
        saveRecord(newRecord);
    }

    private TimerRecord createRecordObject() {
        TimerRecord timerRecord = new TimerRecord();
        timerRecord.setDate(new Date(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal, secondFinal));
        timerRecord.setLabel(labelEditText.getText().toString());
        return timerRecord;
    }

    private void saveRecord(TimerRecord record) {
        timersSharedPrefrences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        try {
            timersSharedPrefrences.edit().putString(record.getLabel(), ObjectSerializer.serialize(record));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

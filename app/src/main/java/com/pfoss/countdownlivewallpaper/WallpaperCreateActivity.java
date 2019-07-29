package com.pfoss.countdownlivewallpaper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import Helpers.BitmapHelper;


public class WallpaperCreateActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    int day, month, year, hour, minute, second;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, secondFinal;
    EditText labelEditText;
    Bitmap bitmapOfUser;
    Button dateSetButton;
    Button createButton;
    ImageView backgroundImagePreview;
    boolean hasUserSetDateAndTime = false;
    DatePickerDialog datePickerDialog;
    SharedPreferences timersSharedPreferences;
    TimerRecord timerRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_create);

        createInstanceOfViews();
        timerRecord = new TimerRecord();
        dateSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("GOT", "date button click");
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, day);
                datePickerDialog = new DatePickerDialog(WallpaperCreateActivity.this, WallpaperCreateActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void createInstanceOfViews() {
        createButton = (Button) findViewById(R.id.createTimeButton);
        dateSetButton = (Button) findViewById(R.id.setDateAndTimeDialogButton);
        labelEditText = (EditText) findViewById(R.id.labelEditText);
        backgroundImagePreview = (ImageView) findViewById(R.id.backgroundImageView);
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
        hasUserSetDateAndTime = true;
        Log.i("GOT", "This is in order: " + yearFinal + " " + monthFinal + " " + dayFinal + " " + hourFinal + " " + minuteFinal + " ");
    }

    protected void createNewTimerClickable(View view) {
        if (hasUserSetDateAndTime) {
            initializeRecordObject(timerRecord);
            saveRecord(timerRecord);
            Log.i("SAVE", "new record has been saved");
            Intent createIntent = new Intent(this, MainActivity.class);
            createIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(createIntent);
            this.finish();
        } else {
            Toast.makeText(this, this.getString(R.string.toast_date_not_set), Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeRecordObject(TimerRecord timerRecord) {

        timerRecord.setPriorToShow(true);
        timerRecord.setDate(new Date(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal, secondFinal));
        timerRecord.setLabel(labelEditText.getText().toString());
    }


    private void saveRecord(TimerRecord newRecord) {
        timersSharedPreferences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        ArrayList<TimerRecord> newTimerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        RecordManager.setAllElementsFlagToFalse(newTimerRecords);
        newTimerRecords.add(newRecord);
        RecordManager.updateRecordsInSharedPreferences(timersSharedPreferences, newTimerRecords);
    }


    protected void chooseImageClickable(View view) {
        Intent imagePickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imagePickIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                bitmapOfUser = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                DisplayMetrics metrics = getResources().getDisplayMetrics();

                int screenWidth = metrics.widthPixels;
                int screenHeight = metrics.heightPixels;

                Bitmap scaledBitmap = BitmapHelper.scaleImageCenteredCrop(bitmapOfUser, screenHeight, screenWidth);

                backgroundImagePreview.setImageBitmap(scaledBitmap);
                RecordManager.saveImageToInternalStorage(scaledBitmap, new ContextWrapper(getApplicationContext()), timerRecord);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

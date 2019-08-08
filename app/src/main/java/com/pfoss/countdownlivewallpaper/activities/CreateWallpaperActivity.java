package com.pfoss.countdownlivewallpaper.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.utils.RecordManager;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;

import com.pfoss.countdownlivewallpaper.utils.BitmapHelper;

import yuku.ambilwarna.AmbilWarnaDialog;

//TODO: this class is messy , must be cleaned

public class CreateWallpaperActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    int day, month, year, hour, minute, second;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, secondFinal;
    EditText labelEditText;
    Bitmap userSelectedBitmap;
    int userSelectedColor;
    Button dateSetButton;
    Button createButton;
    ImageView backgroundImagePreview;
    boolean hasUserSetDateAndTime = false;
    DatePickerDialog datePickerDialog;
    SharedPreferences timersSharedPreferences;
    TimerRecord timerRecord;
    int itemSelected;
    private static final int GRADIENT_BACKGROUND = 0;
    private static final int IMAGE_BACKGROUND = 1;
    private static final int SOLID_BACKGROUND = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_wallpaper_activity);

        createInstanceOfViews();
        timerRecord = new TimerRecord();
        dateSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("GOT", "date button click");
                Calendar today = Calendar.getInstance();
                Log.d("CREATE" , "today DATE: " + today.get(Calendar.YEAR) + " " + today.get(Calendar.MONTH) + " " + today.get(Calendar.DAY_OF_MONTH) );
                datePickerDialog = new DatePickerDialog(CreateWallpaperActivity.this, CreateWallpaperActivity.this, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateWallpaperActivity.this, CreateWallpaperActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;
        hasUserSetDateAndTime = true;
        Log.i("CREATE-TIMESET", "This is in order: " + yearFinal + " " + monthFinal + " " + dayFinal + " " + hourFinal + " " + minuteFinal + " ");
    }

    public void createNewTimerClickable(View view) {
        if (hasUserSetDateAndTime) {
            initializeRecordObject(timerRecord);
            saveNewRecord(timerRecord);
            Log.i("SAVE", "new record has been saved");
            Intent createCountdownIntent = new Intent(this, MainActivity.class);
            createCountdownIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(createCountdownIntent);
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


    private void saveNewRecord(TimerRecord newRecord) {
        timersSharedPreferences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        ArrayList<TimerRecord> newTimerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        RecordManager.setAllElementsFlagToFalse(newTimerRecords);
        newTimerRecords.add(newRecord);
        RecordManager.updateRecordsInSharedPreferences(timersSharedPreferences, newTimerRecords);
    }


    public void chooseBackgroundClickable( View view) {
        final View passView = view;
        String[] themeChoiceItems = getResources().getStringArray(R.array._choose_timer_theme_dialog_multi_choice_array);
        itemSelected = 0;
        new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setTitle(getResources().getString(R.string.choose_theme_dialog_title))
                .setSingleChoiceItems(themeChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                        itemSelected = selectedIndex;
                        switch (selectedIndex) {
                            case GRADIENT_BACKGROUND:

                                break;
                            case IMAGE_BACKGROUND:
                                Intent imagePickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(imagePickIntent, 1);
                                break;
                            case SOLID_BACKGROUND:
                                AmbilWarnaDialog dialog = new AmbilWarnaDialog(passView.getContext(), R.attr.colorPrimary, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                    @Override
                                    public void onOk(AmbilWarnaDialog dialog, int color) {
                                        // color is the color selected by the user.
                                        userSelectedColor = color;
                                    }

                                    @Override
                                    public void onCancel(AmbilWarnaDialog dialog) {
                                        // cancel was selected by the user
                                        userSelectedColor = Color.WHITE;//default color is white
                                    }
                                });

                                dialog.show();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .

                        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("CREATE-OK", "item-selected is :" + itemSelected);
                                switch (itemSelected) {
                                    case GRADIENT_BACKGROUND:

                                        changePreviewToGradientPreview();
                                        timerRecord.setBackgroundTheme(TimerRecord.BackgroundTheme.GRADIENT);
                                        Log.d("CREATE-OK", "theme was set to gradient");

                                        break;
                                    case IMAGE_BACKGROUND:

                                        Log.d("CREATE-OK", "theme was set to image");
                                        changePreviewToImagePreviewAndStoreImageFileInMemory();
                                        timerRecord.setBackgroundTheme(TimerRecord.BackgroundTheme.PICTURE);

                                        break;
                                    case SOLID_BACKGROUND:

                                        Log.d("CREATE-OK", "theme was set to solid");
                                        changePreviewToSolidPreview();
                                        timerRecord.setBackGroundColor(userSelectedColor);
                                        timerRecord.setBackgroundTheme(TimerRecord.BackgroundTheme.SOLID);

                                        break;
                                    default:
                                        Log.d("CREATE-OK", "theme was set to default");
                                        break;
                                }
                            }
                        })
                .

                        setNegativeButton("Cancel", null)
                .

                        show();

    }

    private void changePreviewToSolidPreview() {
        backgroundImagePreview.setImageBitmap(null);
        backgroundImagePreview.setBackgroundColor(userSelectedColor);
    }

    private void changePreviewToImagePreviewAndStoreImageFileInMemory() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels + getNavigationBarHeight();
        Log.d("CREATE" , "screen height is :" + screenHeight + " screen width is " + screenWidth);

        Bitmap scaledBitmap = BitmapHelper.scaleImageCenteredCrop(userSelectedBitmap, screenHeight, screenWidth);
        backgroundImagePreview.setImageBitmap(scaledBitmap);
        RecordManager.saveImageToInternalStorage(scaledBitmap, new ContextWrapper(getApplicationContext()), timerRecord);
    }

    private void changePreviewToGradientPreview() {
        backgroundImagePreview.setImageBitmap(null);
        backgroundImagePreview.setBackground(getResources().getDrawable(R.drawable.imageview_background));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                userSelectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void goBackClickableDrawable(View view) {
        this.onBackPressed();
    }
    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

}

package com.pfoss.countdownlivewallpaper.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.utils.BitmapHelper;
import com.pfoss.countdownlivewallpaper.utils.RecordManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import yuku.ambilwarna.AmbilWarnaDialog;

//TODO: this class is messy , must be cleaned

public class CreateWallpaperActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog.OnDateSetListener,
        com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {


    private static final String TAG = "CreateWallpaperActivity";
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, secondFinal;
    private EditText labelEditText;
    private Bitmap userSelectedBitmap;
    private int userSelectedColor;
    private Button dateSetButton;
    private ImageView backgroundImagePreview;
    private boolean hasUserSetDateAndTime = false;
    private boolean hasUserSetBackground = false;


    private SharedPreferences timersSharedPreferences;
    private TimerRecord timerRecord;
    private int itemSelected;
    public static final int GRADIENT_BACKGROUND = 0;
    public static final int IMAGE_BACKGROUND = 1;
    public static final int SOLID_BACKGROUND = 2;
    boolean isPersian = Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallpaper);

        createInstanceOfViews();
        timerRecord = new TimerRecord();
        dateSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("GOT", "date button click");
                Calendar today = Calendar.getInstance();
                if (isPersian) {
                    PersianCalendar persianCalendar = new PersianCalendar();
                    com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog persianPickerDialog = com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog.newInstance(
                            CreateWallpaperActivity.this,
                            persianCalendar.getPersianYear(),
                            persianCalendar.getPersianMonth(),
                            persianCalendar.getPersianDay()
                    );
                    persianPickerDialog.show(getFragmentManager(), "persianPickerDialog");
                } else {
                    DatePickerDialog datePickerDialog;
                    datePickerDialog = new DatePickerDialog(CreateWallpaperActivity.this,
                            CreateWallpaperActivity.this,
                            today.get(Calendar.YEAR),
                            today.get(Calendar.MONTH),
                            today.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.show();
                }


            }
        });
    }

    private void createInstanceOfViews() {
        dateSetButton = (Button) findViewById(R.id.setDateAndTimeDialogButton);
        labelEditText = (EditText) findViewById(R.id.labelEditText);
        backgroundImagePreview = (ImageView) findViewById(R.id.backgroundImageView);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {//Gregorian calender picker listener
        yearFinal = i;
        monthFinal = i1;
        dayFinal = i2;
        Calendar newDate = Calendar.getInstance();
        TimePickerDialog timePickerDialog;

        timePickerDialog = new TimePickerDialog(CreateWallpaperActivity.this,
                CreateWallpaperActivity.this,
                newDate.get(Calendar.HOUR_OF_DAY),
                newDate.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {//Gregorian calender picker listener
        hourFinal = i;
        minuteFinal = i1;
        hasUserSetDateAndTime = true;
        Log.i("CREATE-TIMESET", "This is in order: " + yearFinal + " " + monthFinal + " " + dayFinal + " " + hourFinal + " " + minuteFinal + " ");
    }

    public void createNewTimerClickable(View view) {
        if (hasUserSetDateAndTime && hasUserSetBackground) {
            initializeRecordObject(timerRecord);
            saveNewRecord(timerRecord);
            Log.i("SAVE", "new record has been saved");

            goToMainActivity();

        } else if (!hasUserSetDateAndTime) {
            Toast.makeText(this, this.getString(R.string.toast_date_not_set), Toast.LENGTH_SHORT).show();
        } else if (!hasUserSetBackground) {
            Toast.makeText(this, this.getString(R.string.toast_background_not_set), Toast.LENGTH_SHORT).show();
        }

    }

    private void goToMainActivity() {
        Intent createCountdownIntent = new Intent(this, MainActivity.class);
        clearLastActivity(createCountdownIntent);
        startActivity(createCountdownIntent);
        this.finish();
    }

    private void clearLastActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    private void initializeRecordObject(TimerRecord timerRecord) {
        timerRecord.setPriorToShow(true);
        if (isPersian) {
            PersianCalendar persianCalendar = new PersianCalendar();
            persianCalendar.setPersianDate(yearFinal, monthFinal, dayFinal);

            Date time = persianCalendar.getTime();
            time.setHours(hourFinal);
            time.setMinutes(minuteFinal);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getDefault());
            timerRecord.setDate(sdf.format(time));

            Log.d(TAG, "initializeRecordObject: persiandate " + timerRecord.getDate());
        } else {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            cal.set(Calendar.YEAR, yearFinal);
            cal.set(Calendar.MONTH, monthFinal);
            cal.set(Calendar.DAY_OF_MONTH, dayFinal);
            cal.set(Calendar.HOUR_OF_DAY, hourFinal);
            cal.set(Calendar.MINUTE, minuteFinal);
            cal.set(Calendar.SECOND, secondFinal);
            cal.set(Calendar.MILLISECOND, 0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getDefault());

            timerRecord.setDate(sdf.format(cal.getTime()));
        }
        timerRecord.setLabel(labelEditText.getText().toString());
    }


    private void saveNewRecord(TimerRecord newRecord) {
        timersSharedPreferences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        ArrayList<TimerRecord> newTimerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        RecordManager.setAllElementsFlagToFalse(newTimerRecords);
        newTimerRecords.add(newRecord);
        RecordManager.updateRecordsInSharedPreferences(timersSharedPreferences, newTimerRecords);
    }


    public void chooseBackgroundClickable(View view) {
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

                        setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("CREATE-OK", "item-selected is :" + itemSelected);
                                hasUserSetBackground = true;
                                switch (itemSelected) {
                                    case GRADIENT_BACKGROUND:

                                        changePreviewToGradientPreview();
                                        timerRecord.setBackgroundTheme(BackgroundTheme.GRADIENT);
                                        Log.d("CREATE-OK", "theme was set to gradient");

                                        break;
                                    case IMAGE_BACKGROUND:

                                        Log.d("CREATE-OK", "theme was set to image");
                                        changePreviewToImagePreviewAndStoreImageFileInMemory();
                                        timerRecord.setBackgroundTheme(BackgroundTheme.PICTURE);

                                        break;
                                    case SOLID_BACKGROUND:

                                        Log.d("CREATE-OK", "theme was set to solid");
                                        changePreviewToSolidPreview();
                                        timerRecord.setBackGroundColor(userSelectedColor);
                                        timerRecord.setBackgroundTheme(BackgroundTheme.SOLID);

                                        break;
                                    default:
                                        Log.d("CREATE-OK", "theme was set to default");
                                        break;
                                }
                            }
                        })
                .

                        setNegativeButton(getResources().getString(R.string.cancel), null)
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
        Log.d("CREATE", "screen height is :" + screenHeight + " screen width is " + screenWidth);

        Bitmap scaledBitmap = BitmapHelper.scaleImageCenteredCrop(userSelectedBitmap, screenHeight, screenWidth);
        backgroundImagePreview.setImageBitmap(scaledBitmap);
        RecordManager.saveImageToInternalStorage(scaledBitmap, new ContextWrapper(getApplicationContext()), timerRecord);
    }

    private void changePreviewToGradientPreview() {
        backgroundImagePreview.setImageBitmap(null);
        backgroundImagePreview.setBackground(getResources().getDrawable(R.drawable.imageview_background));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//Image picker activity result
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


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {//Persian calender date set listener
        hourFinal = hourOfDay;
        minuteFinal = minute;
        hasUserSetDateAndTime = true;
        Log.i("CREATE-TIMESET", "This is in order: " + yearFinal + " " + monthFinal + " " + dayFinal + " " + hourFinal + " " + minuteFinal + " ");
    }

    @Override
    public void onDateSet(com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {//Persian calender date set listener
        yearFinal = year;
        monthFinal = monthOfYear;
        dayFinal = dayOfMonth;
        com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog persianTimePicker;
        PersianCalendar now = new PersianCalendar();
        persianTimePicker = com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog.newInstance(
                CreateWallpaperActivity.this,
                now.get(PersianCalendar.HOUR_OF_DAY),
                now.get(PersianCalendar.MINUTE),
                DateFormat.is24HourFormat(this));


        persianTimePicker.show(getFragmentManager(), "persianTimePicker");

    }
}

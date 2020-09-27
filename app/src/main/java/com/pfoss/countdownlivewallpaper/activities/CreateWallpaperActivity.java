package com.pfoss.countdownlivewallpaper.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.fragments.BackgroundSelectorDialog;
import com.pfoss.countdownlivewallpaper.fragments.datepicker.DatePickerFragment;
import com.pfoss.countdownlivewallpaper.fragments.datepicker.GregorianDatePicker;
import com.pfoss.countdownlivewallpaper.fragments.datepicker.PersianDatePicker;
import com.pfoss.countdownlivewallpaper.fragments.timepicker.GregorianTimePicker;
import com.pfoss.countdownlivewallpaper.fragments.timepicker.PersianTimePicker;
import com.pfoss.countdownlivewallpaper.fragments.timepicker.TimePickerFragment;
import com.pfoss.countdownlivewallpaper.services.ImagePickerException;
import com.pfoss.countdownlivewallpaper.utils.BitmapHelper;
import com.pfoss.countdownlivewallpaper.utils.RuntimeTools;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.pfoss.countdownlivewallpaper.activities.EditCountDownActivity.IMAGE_BACKGROUND;
import static com.pfoss.countdownlivewallpaper.activities.EditCountDownActivity.SOLID_BACKGROUND;
import static com.pfoss.countdownlivewallpaper.fragments.BackgroundSelectorDialog.GRADIENT_BACKGROUND;

public class CreateWallpaperActivity extends AppCompatActivity implements DialogInterface.OnClickListener {


    private static final String TAG = "CWActivity";

    private int dayFinal;
    private int monthFinal;
    private int yearFinal;
    private int hourFinal;
    private int minuteFinal;
    private EditText labelEditText;
    private Bitmap userSelectedBitmap;
    private Button dateSetButton;
    private ImageView backgroundImagePreview;
    private boolean hasUserSetDateAndTime = false;
    private boolean hasUserSetBackground = false;


    private TimerRecord newTimerRecord;
    private TimerViewModel timerViewModel;
    private BackgroundSelectorDialog backgroundSelectorDialog;

    boolean isPersian = Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallpaper);
        createInstanceOfViews();
        newTimerRecord = new TimerRecord();
        timerViewModel = new TimerViewModel(getApplicationContext());
        backgroundSelectorDialog = new BackgroundSelectorDialog(this);
        dateSetButton.setOnClickListener(new View.OnClickListener() {//TODO : change this listener place
            @Override
            public void onClick(View view) {
                Log.i("GOT", "date button click");
                if (isPersian) {
                    DatePickerFragment datePickerFragment = new PersianDatePicker(onPersianDateSetListener);
                    datePickerFragment.displayCalenderDialog(CreateWallpaperActivity.this);
                } else {
                    DatePickerFragment datePickerFragment = new GregorianDatePicker(onGregorianDateSetListener);
                    datePickerFragment.displayCalenderDialog(CreateWallpaperActivity.this);
                }

            }
        });
        if (RuntimeTools.isFirstRun(this)) {
            showIntro();
            RuntimeTools.markFirstRun(this);
        }

    }


    private void showIntro() {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(R.id.backgroundImageView, this))
                .setContentTitle(getResources().getString(R.string.set_background_intro_showcase_title))
                .setContentText(getResources().getString(R.string.set_background_intro_showcase))
                .hideOnTouchOutside()
                .build()
                .show();
    }

    private void createInstanceOfViews() {
        dateSetButton = findViewById(R.id.setDateAndTimeDialogButton);
        labelEditText = findViewById(R.id.labelEditText);
        backgroundImagePreview = findViewById(R.id.backgroundImageView);
    }

    private TimePickerFragment.OnTimeSetListener onTimeSetListener = new GregorianTimePicker.OnTimeSetListener() {
        @Override
        public void onTimeSet(int hour, int minute) {
            hourFinal = hour;
            minuteFinal = minute;
            hasUserSetDateAndTime = true;
            Log.i("CREATE-TIMESET", "This is in order: " + yearFinal + " " + monthFinal + " " + dayFinal + " " + hourFinal + " " + minuteFinal + " ");
        }
    };
    private DatePickerFragment.OnDateSetListener onGregorianDateSetListener = new DatePickerFragment.OnDateSetListener() {
        @Override
        public void onDateSet(int year, int month, int day) {
            yearFinal = year;
            monthFinal = month;
            dayFinal = day;

            TimePickerFragment timePickerFragment = new GregorianTimePicker(onTimeSetListener);
            timePickerFragment.displayTimePicker(CreateWallpaperActivity.this);
        }
    };
    private DatePickerFragment.OnDateSetListener onPersianDateSetListener = new DatePickerFragment.OnDateSetListener() {
        @Override
        public void onDateSet(int year, int month, int day) {
            yearFinal = year;
            monthFinal = month;
            dayFinal = day;
            TimePickerFragment timePickerFragment = new PersianTimePicker(onTimeSetListener);
            timePickerFragment.displayTimePicker(CreateWallpaperActivity.this);
        }
    };

    public void createNewTimerClickable(View view) {
        if (hasUserSetDateAndTime && hasUserSetBackground) {
            initializeRecordObject(newTimerRecord);
            timerViewModel.saveNewRecord(newTimerRecord);
            Log.i("SAVE", "new record has been saved");

            goToMainActivity();

        } else if (!hasUserSetDateAndTime) {
            Toast.makeText(this, this.getString(R.string.toast_date_not_set), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, this.getString(R.string.toast_background_not_set), Toast.LENGTH_SHORT).show();
        }

    }

    // on click listener for background selection ok button
    @Override
    public void onClick(DialogInterface dialogInterface, int useless) {

        int selectedItem = backgroundSelectorDialog.getSelectedItem();

        Log.d("CREATE-OK", "item-selected is :" + selectedItem);
        hasUserSetBackground = true;
        switch (selectedItem) {
            case GRADIENT_BACKGROUND:

                changePreviewToGradientPreview();
                newTimerRecord.setBackgroundTheme(BackgroundTheme.GRADIENT);
                Log.d("CREATE-OK", "theme was set to gradient");

                break;
            case IMAGE_BACKGROUND:

                Log.d("CREATE-OK", "theme was set to image");
                changePreviewToUserSetPreviewAndStoreImageFileInMemory();
                newTimerRecord.setBackgroundTheme(BackgroundTheme.PICTURE);

                break;
            case SOLID_BACKGROUND:

                Log.d("CREATE-OK", "theme was set to solid");
                changePreviewToSolidPreview();
                newTimerRecord.setBackGroundColor(backgroundSelectorDialog.getColorSelectedByUser());
                newTimerRecord.setBackgroundTheme(BackgroundTheme.SOLID);

                break;
            default:
                Log.d("CREATE-OK", "theme was set to default");
                break;
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
            timerRecord.setDate(getFormattedPersianDateTime());
        } else {
            timerRecord.setDate(getFormattedGregorianDateTime());
        }
        timerRecord.setLabel(labelEditText.getText().toString());
    }


    public void chooseBackgroundClickable(View view) {
        backgroundSelectorDialog.show(view, this);
    }

    private void changePreviewToSolidPreview() {
        backgroundImagePreview.setImageBitmap(null);
        backgroundImagePreview.setBackgroundColor(backgroundSelectorDialog.getColorSelectedByUser());
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE || requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            backgroundSelectorDialog.getBackgroundImagePicker().onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void changePreviewToUserSetPreviewAndStoreImageFileInMemory() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels + RuntimeTools.getNavigationBarHeight(this);
        Log.d("CREATE", "screen height is :" + screenHeight + " screen width is " + screenWidth);

        Bitmap scaledBitmap = BitmapHelper.scaleImageCenteredCrop(userSelectedBitmap, screenHeight, screenWidth);
        backgroundImagePreview.setImageBitmap(scaledBitmap);
        timerViewModel.saveImageToInternalStorage(scaledBitmap, new ContextWrapper(getApplicationContext()), newTimerRecord);
    }

    private void changePreviewToGradientPreview() {
        backgroundImagePreview.setImageBitmap(null);

        backgroundImagePreview.setBackgroundResource(R.drawable.imageview_background);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//Image picker activity result
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            backgroundSelectorDialog.getBackgroundImagePicker().imagePickerListener(requestCode, resultCode, data);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                userSelectedBitmap = BitmapHelper.decodeUriAsBitmap(backgroundSelectorDialog.getBackgroundImagePicker().imageFetch(requestCode, resultCode, data), this);
            } catch (ImagePickerException e) {
                e.printStackTrace();
            }
        }
    }

    public void goBackClickableDrawable(View view) {
        this.onBackPressed();
    }


    private String getFormattedPersianDateTime() {
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.setPersianDate(yearFinal, monthFinal, dayFinal);

        Date time = persianCalendar.getTime();
        time.setHours(hourFinal);
        time.setMinutes(minuteFinal);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(time);

    }

    private String getFormattedGregorianDateTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.YEAR, yearFinal);
        cal.set(Calendar.MONTH, monthFinal);
        cal.set(Calendar.DAY_OF_MONTH, dayFinal);
        cal.set(Calendar.HOUR_OF_DAY, hourFinal);
        cal.set(Calendar.MINUTE, minuteFinal);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());

    }


}

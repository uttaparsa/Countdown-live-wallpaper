package com.pfoss.countdownlivewallpaper.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.pfoss.countdownlivewallpaper.data.TimerBuilder;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.data.timerbuilderexception.BackgroundNotSetException;
import com.pfoss.countdownlivewallpaper.data.timerbuilderexception.DateNotSetException;
import com.pfoss.countdownlivewallpaper.dialogs.BackgroundSelectorDialog;
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
import java.util.TimeZone;

import static com.pfoss.countdownlivewallpaper.activities.EditCountDownActivity.IMAGE_BACKGROUND;
import static com.pfoss.countdownlivewallpaper.activities.EditCountDownActivity.SOLID_BACKGROUND;
import static com.pfoss.countdownlivewallpaper.dialogs.BackgroundSelectorDialog.GRADIENT_BACKGROUND;

public class CreateWallpaperActivity extends AppCompatActivity implements DialogInterface.OnClickListener {


    private static final String TAG = "CWActivity";


    private EditText labelEditText;
    private Bitmap userSelectedBitmap;
    private Button dateSetButton;
    private ImageView backgroundImagePreview;
    private TimerBuilder timerBuilder;
    private ShowcaseView backgroundImageIntro;
    private TimerViewModel timerViewModel;
    private BackgroundSelectorDialog backgroundSelectorDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallpaper);
        createInstanceOfViews();
        timerBuilder = new TimerBuilder();
        timerViewModel = new TimerViewModel(getApplicationContext());
        backgroundSelectorDialog = new BackgroundSelectorDialog(this);
        initBackgroundImageIntro();
        dateSetButton.setOnClickListener(new View.OnClickListener() {//TODO : change this listener place
            @Override
            public void onClick(View view) {
                Log.i("GOT", "date button click");
                if (RuntimeTools.isPersian()) {
                    DatePickerFragment datePickerFragment = new PersianDatePicker(onPersianDateSetListener);
                    datePickerFragment.displayCalenderDialog(CreateWallpaperActivity.this);
                } else {
                    DatePickerFragment datePickerFragment = new GregorianDatePicker(onGregorianDateSetListener);
                    datePickerFragment.displayCalenderDialog(CreateWallpaperActivity.this);
                }

            }
        });
        if (RuntimeTools.isFirstRun(this)) {
            backgroundImageIntro.show();
            RuntimeTools.markFirstRun(this);
        }

    }

    private void initBackgroundImageIntro() {
        backgroundImageIntro = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(R.id.backgroundImageView, this))
                .setContentTitle(getResources().getString(R.string.set_background_intro_showcase_title))
                .setContentText(getResources().getString(R.string.set_background_intro_showcase))
                .hideOnTouchOutside()
                .build();
    }

    private void createInstanceOfViews() {
        dateSetButton = findViewById(R.id.setDateAndTimeDialogButton);
        labelEditText = findViewById(R.id.labelEditText);
        backgroundImagePreview = findViewById(R.id.backgroundImageView);
    }

    private TimePickerFragment.OnTimeSetListener onTimeSetListener = new GregorianTimePicker.OnTimeSetListener() {
        @Override
        public void onTimeSet(int hour, int minute) {
            timerBuilder.setHourFinal(hour);
            timerBuilder.setMinuteFinal(minute);
            Log.i("CREATE-TIMESET", "This is in order: " + timerBuilder.getYearFinal() + " " + timerBuilder.getMonthFinal() + " " + timerBuilder.getDayFinal() + " " + timerBuilder.getHourFinal() + " " + timerBuilder.getMinuteFinal() + " ");
        }
    };
    private DatePickerFragment.OnDateSetListener onGregorianDateSetListener = new DatePickerFragment.OnDateSetListener() {
        @Override
        public void onDateSet(int year, int month, int day) {
            timerBuilder.setYearFinal(year);
            timerBuilder.setMonthFinal(month);
            timerBuilder.setDayFinal(day);

            TimePickerFragment timePickerFragment = new GregorianTimePicker(onTimeSetListener);
            timePickerFragment.displayTimePicker(CreateWallpaperActivity.this);
        }
    };
    private DatePickerFragment.OnDateSetListener onPersianDateSetListener = new DatePickerFragment.OnDateSetListener() {
        @Override
        public void onDateSet(int year, int month, int day) {
            timerBuilder.setYearFinal(year);
            timerBuilder.setMonthFinal(month);
            timerBuilder.setDayFinal(day);
            TimePickerFragment timePickerFragment = new PersianTimePicker(onTimeSetListener);
            timePickerFragment.displayTimePicker(CreateWallpaperActivity.this);
        }
    };

    public void createNewTimerClickable(View view) {

        try {
            TimerRecord newTimerRecord = timerBuilder.build();
            newTimerRecord.setImagePath(TimerViewModel.saveImageToInternalStorage(userSelectedBitmap, new ContextWrapper(getApplicationContext()), newTimerRecord));
            timerBuilder.setLabel(labelEditText.getText().toString());

            timerViewModel.saveNewRecord(newTimerRecord);
            Log.i("SAVE", "new record has been saved");

            goToMainActivity();

        } catch (DateNotSetException dnse) {
            Toast.makeText(this, this.getString(R.string.toast_date_not_set), Toast.LENGTH_SHORT).show();
        } catch (BackgroundNotSetException bnse) {
            Toast.makeText(this, this.getString(R.string.toast_background_not_set), Toast.LENGTH_SHORT).show();
            backgroundImageIntro.show();
        }

    }

    // on click listener for background selection ok button
    @Override
    public void onClick(DialogInterface dialogInterface, int useless) {

        int selectedItem = backgroundSelectorDialog.getSelectedItem();

        Log.d("CREATE-OK", "item-selected is :" + selectedItem);
        switch (selectedItem) {
            case GRADIENT_BACKGROUND:

                changePreviewToGradientPreview();
                timerBuilder.setBackgroundTheme(BackgroundTheme.GRADIENT);
                Log.d("CREATE-OK", "theme was set to gradient");

                break;
            case IMAGE_BACKGROUND:

                Log.d("CREATE-OK", "theme was set to image");
                changePreviewToUserSetPreview();
                timerBuilder.setBackgroundTheme(BackgroundTheme.PICTURE);

                break;
            case SOLID_BACKGROUND:

                Log.d("CREATE-OK", "theme was set to solid");
                changePreviewToSolidPreview();
                timerBuilder.setBackGroundColor(backgroundSelectorDialog.getColorSelectedByUser());
                timerBuilder.setBackgroundTheme(BackgroundTheme.SOLID);

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

    private void changePreviewToUserSetPreview() {
        backgroundImagePreview.setImageBitmap(userSelectedBitmap);
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


}

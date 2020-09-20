package com.pfoss.countdownlivewallpaper.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.fragments.datepicker.DatePickerFragment;
import com.pfoss.countdownlivewallpaper.fragments.datepicker.GregorianDatePicker;
import com.pfoss.countdownlivewallpaper.fragments.datepicker.PersianDatePicker;
import com.pfoss.countdownlivewallpaper.fragments.timepicker.GregorianTimePicker;
import com.pfoss.countdownlivewallpaper.fragments.timepicker.PersianTimePicker;
import com.pfoss.countdownlivewallpaper.fragments.timepicker.TimePickerFragment;
import com.pfoss.countdownlivewallpaper.services.BackgroundPicker;
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

import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateWallpaperActivity extends AppCompatActivity {


    private static final String TAG = "CreateWallpaperActivity";

    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, secondFinal;
    private EditText labelEditText;
    private Bitmap userSelectedBitmap;
    private int colorSelectedByUser;
    private Button dateSetButton;
    private ImageView backgroundImagePreview;
    private boolean hasUserSetDateAndTime = false;
    private boolean hasUserSetBackground = false;


    private TimerRecord newTimerRecord;
    private TimerViewModel timerViewModel;
    private int itemSelected;
    private BackgroundPicker backgroundPicker;

    public static final int GRADIENT_BACKGROUND = 0;
    public static final int IMAGE_BACKGROUND = 1;
    public static final int SOLID_BACKGROUND = 2;
    boolean isPersian = Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallpaper);

        createInstanceOfViews();
        newTimerRecord = new TimerRecord();
        timerViewModel = new TimerViewModel(getApplicationContext());
        dateSetButton.setOnClickListener(new View.OnClickListener() {//TODO : change this listener place
            @Override
            public void onClick(View view) {
                Log.i("GOT", "date button click");
                if (isPersian) {
                    DatePickerFragment datePickerFragment = new PersianDatePicker(onGregorianDateSetListener);
                    datePickerFragment.displayCalenderDialog(CreateWallpaperActivity.this);
                } else {
                    DatePickerFragment datePickerFragment = new GregorianDatePicker(onPersianDateSetListener);
                    datePickerFragment.displayCalenderDialog(CreateWallpaperActivity.this);
                }

            }
        });
        if (RuntimeTools.isFirstRun(this)) {
            showIntro();
            RuntimeTools.markFirstRun(this);
        }

        backgroundPicker = new BackgroundPicker(this);
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
                                backgroundPicker.startCropImageActivity();
                                break;
                            case SOLID_BACKGROUND:
                                AmbilWarnaDialog dialog = new AmbilWarnaDialog(passView.getContext(), R.attr.colorPrimary, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                    @Override
                                    public void onOk(AmbilWarnaDialog dialog, int color) {
                                        // color is the color selected by the user.
                                        colorSelectedByUser = color;
                                    }

                                    @Override
                                    public void onCancel(AmbilWarnaDialog dialog) {
                                        // cancel was selected by the user
                                        colorSelectedByUser = Color.WHITE;// default color is white
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
                                        newTimerRecord.setBackGroundColor(colorSelectedByUser);
                                        newTimerRecord.setBackgroundTheme(BackgroundTheme.SOLID);

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
        backgroundImagePreview.setBackgroundColor(colorSelectedByUser);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE || requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            backgroundPicker.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

        backgroundImagePreview.setBackground(getResources().getDrawable(R.drawable.imageview_background));
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//Image picker activity result
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            backgroundPicker.imagePicker(requestCode, resultCode, data);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                userSelectedBitmap = BitmapHelper.decodeUriAsBitmap(backgroundPicker.imageFetch(requestCode, resultCode, data), this);
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
        cal.set(Calendar.SECOND, secondFinal);
        cal.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());

    }


}

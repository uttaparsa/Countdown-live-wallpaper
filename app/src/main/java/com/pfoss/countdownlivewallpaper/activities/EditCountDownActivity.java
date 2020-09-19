package com.pfoss.countdownlivewallpaper.activities;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.dialogs.MultiSelectDialog;
import com.pfoss.countdownlivewallpaper.services.BackgroundPicker;
import com.pfoss.countdownlivewallpaper.services.ImagePickerException;
import com.pfoss.countdownlivewallpaper.utils.BitmapHelper;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EditCountDownActivity extends AppCompatActivity {
    private TimerRecord timerToEdit;
    private TimerViewModel timerViewModel;
    CardView textViewFrame;
    TextView textColorPreviewTextView;
    private Bitmap userSelectedBitmap;
    private int userSelectedColor;
    boolean changedBeenMade = false;
    private int itemSelected;
    private BackgroundPicker backgroundPicker;
    public static final int GRADIENT_BACKGROUND = 0;
    public static final int IMAGE_BACKGROUND = 1;
    public static final int SOLID_BACKGROUND = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_count_down);
        timerViewModel = new TimerViewModel(getApplicationContext());
        loadTimerRecords();
        initializeToolbar();
        selectedUnits = timerToEdit.getActiveShowUnits().getActiveShowUnitsBoolArray();
        backgroundPicker = new BackgroundPicker(this);

        textViewFrame = findViewById(R.id.textColorPreviewBackground);
        textColorPreviewTextView = findViewById(R.id.textColorPreview);
        updateTextColorPreview();

    }

    private void updateTextColorPreview() {
        textColorPreviewTextView.setTextColor(timerToEdit.getTextColor());
        setTextViewDrawableColor(textColorPreviewTextView, timerToEdit.getTextColor());
        textViewFrame.setCardBackgroundColor(timerToEdit.getBackGroundColor());

    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawablesRelative()) {
            Log.d("EDIT", "setTextViewDrawableColor: found one");
            if (drawable != null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable.setTint(color);
                } else {
                    DrawableCompat.setTint(DrawableCompat.wrap(drawable), color);
                }
        }
    }


    private void loadTimerRecords() {
        timerToEdit = timerViewModel.getLastSelectedTimer();
    }


    public void setTextColorClickable(View view) {
        AmbilWarnaDialog colorSelectDialog = new AmbilWarnaDialog(view.getContext(), R.attr.colorPrimary, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
                timerToEdit.setTextColor(color);
                updateTextColorPreview();
                changedBeenMade = true;
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }
        });
        colorSelectDialog.show();

    }

    public void setBackgroundClickable(View view) {
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
                                changedBeenMade = true;
                                switch (itemSelected) {
                                    case GRADIENT_BACKGROUND:

                                        timerToEdit.setBackgroundTheme(BackgroundTheme.GRADIENT);
                                        Log.d("CREATE-OK", "theme was set to gradient");

                                        break;
                                    case IMAGE_BACKGROUND:
                                        saveImageFile();
                                        Log.d("CREATE-OK", "theme was set to image");
                                        timerToEdit.setBackgroundTheme(BackgroundTheme.PICTURE);

                                        break;
                                    case SOLID_BACKGROUND:

                                        Log.d("CREATE-OK", "theme was set to solid");
                                        timerToEdit.setBackGroundColor(userSelectedColor);
                                        timerToEdit.setBackgroundTheme(BackgroundTheme.SOLID);

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE || requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            backgroundPicker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void saveImageFile() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels + getNavigationBarHeight();
        Log.d("CREATE", "screen height is :" + screenHeight + " screen width is " + screenWidth);

        Bitmap scaledBitmap = BitmapHelper.scaleImageCenteredCrop(userSelectedBitmap, screenHeight, screenWidth);
        timerViewModel.saveImageToInternalStorage(scaledBitmap, new ContextWrapper(getApplicationContext()), timerToEdit);
    }

    @Override
    public void finish() {
        super.finish();
        if (changedBeenMade) {
            timerViewModel.updateRecordsInSharedPreferences();
        }

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

    private void initializeToolbar() {
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.edit_count_down_title));
        setSupportActionBar(toolbar);

    }

    public void setActiveUnits(View view) {

        MultiSelectDialog activeUnitsSelectDialog = new MultiSelectDialog(unitChoiceClickListener, unitChoiceOkButtonListener);
        activeUnitsSelectDialog.setCheckedItems(timerToEdit.getActiveShowUnits().getActiveShowUnitsBoolArray());
        activeUnitsSelectDialog.show(this.getSupportFragmentManager(), "selectTag");
    }

    boolean[] selectedUnits;
    DialogInterface.OnMultiChoiceClickListener unitChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
            selectedUnits[i] = b;

        }
    };
    DialogInterface.OnClickListener unitChoiceOkButtonListener = new DialogInterface.OnClickListener() {//On positive button click listener
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Log.d("unitChoiceOk", "onClick: changing active units: " + timerToEdit.getActiveShowUnits().toString());
            timerToEdit.getActiveShowUnits().setActiveShowUnits(selectedUnits);
            changedBeenMade = true;
            Log.d("unitChoiceOk", "onClick: changing active units: " + timerToEdit.getActiveShowUnits().toString());
        }
    };

}

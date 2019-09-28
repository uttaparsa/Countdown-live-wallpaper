package com.pfoss.countdownlivewallpaper.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.ActiveShowUnits;
import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.dialogs.MultiSelectDialog;
import com.pfoss.countdownlivewallpaper.utils.BitmapHelper;
import com.pfoss.countdownlivewallpaper.utils.RecordManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EditCountDownActivity extends AppCompatActivity {
    private SharedPreferences timersSharedPreferences;
    private ArrayList<TimerRecord> timerRecords;
    private TimerRecord currentRecord;
    FrameLayout textViewFrame;
    TextView textColorPreviewTextView;
    FrameLayout backgroundViewFrame;
    private Bitmap userSelectedBitmap;
    private int userSelectedColor;
    boolean changedBeenMade = false;
    private int itemSelected;
    public static final int GRADIENT_BACKGROUND = 0;
    public static final int IMAGE_BACKGROUND = 1;
    public static final int SOLID_BACKGROUND = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_count_down);
        textViewFrame = findViewById(R.id.textColorPreviewBackground);
        textColorPreviewTextView = findViewById(R.id.textColorPreview);
        backgroundViewFrame = findViewById(R.id.backgroundPreview);
//        backgroundPreviewText = findViewById( R.id.backgroundPreviewText);
        loadRecords();
        initTextColorPreview();
        initializeToolbar();
        selectedUnits = currentRecord.getActiveShowUnits().getActiveShowUnitsBoolArray();
    }

    private void initTextColorPreview() {

        textColorPreviewTextView.setTextColor(currentRecord.getTextColor());
        textViewFrame.setBackgroundColor(currentRecord.getBackGroundColor());
    }



    private void loadRecords() {
        timersSharedPreferences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        currentRecord = RecordManager.getPriorToShowRecord(timerRecords);

    }


    public void setTextColorClickable(View view) {
        AmbilWarnaDialog colorSelectDialog = new AmbilWarnaDialog(view.getContext(), R.attr.colorPrimary, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
                currentRecord.setTextColor(color);
                initTextColorPreview();
                changedBeenMade = true;
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }
        });
        colorSelectDialog.show();

    }
    public void setBackgroundClickable(View view){
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
                                changedBeenMade = true;
                                switch (itemSelected) {
                                    case GRADIENT_BACKGROUND:

                                        currentRecord.setBackgroundTheme(BackgroundTheme.GRADIENT);
                                        Log.d("CREATE-OK", "theme was set to gradient");

                                        break;
                                    case IMAGE_BACKGROUND:
                                        saveImageFile();
                                        Log.d("CREATE-OK", "theme was set to image");
                                        currentRecord.setBackgroundTheme(BackgroundTheme.PICTURE);

                                        break;
                                    case SOLID_BACKGROUND:

                                        Log.d("CREATE-OK", "theme was set to solid");
                                        currentRecord.setBackGroundColor(userSelectedColor);
                                        currentRecord.setBackgroundTheme(BackgroundTheme.SOLID);

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


        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                userSelectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageFile() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels + getNavigationBarHeight();
        Log.d("CREATE" , "screen height is :" + screenHeight + " screen width is " + screenWidth);

        Bitmap scaledBitmap = BitmapHelper.scaleImageCenteredCrop(userSelectedBitmap, screenHeight, screenWidth);
        RecordManager.saveImageToInternalStorage(scaledBitmap, new ContextWrapper(getApplicationContext()), currentRecord);
    }
    @Override
    public void finish() {
        super.finish();
        if (changedBeenMade) {
            RecordManager.updateRecordsInSharedPreferences(timersSharedPreferences, timerRecords);
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

        MultiSelectDialog activeUnitsSelectDialog = new MultiSelectDialog(unitChoiceClickListener , unitChoiceOkButtonListener);
        activeUnitsSelectDialog.setCheckedItems(currentRecord.getActiveShowUnits().getActiveShowUnitsBoolArray());
        activeUnitsSelectDialog.show(this.getSupportFragmentManager(),"selectTag");
    }
    boolean[] selectedUnits;
    DialogInterface.OnMultiChoiceClickListener unitChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
            if(b){
                selectedUnits[i] = true;
            }else {
                selectedUnits[i] = false;
            }

        }
    };
    DialogInterface.OnClickListener unitChoiceOkButtonListener = new DialogInterface.OnClickListener() {//On positive button click listener
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Log.d("unitChoiceOk", "onClick: changing active units: "+currentRecord.getActiveShowUnits().toString());
            currentRecord.getActiveShowUnits().setActiveShowUnits(selectedUnits);
            changedBeenMade = true;
            Log.d("unitChoiceOk", "onClick: changing active units: "+currentRecord.getActiveShowUnits().toString());
        }
    };

}

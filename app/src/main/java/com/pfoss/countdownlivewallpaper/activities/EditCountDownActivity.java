package com.pfoss.countdownlivewallpaper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.themes.Style;
import com.pfoss.countdownlivewallpaper.utils.RecordManager;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EditCountDownActivity extends AppCompatActivity {
    private SharedPreferences timersSharedPreferences;
    private ArrayList<TimerRecord> timerRecords;
    private TimerRecord currentRecord;
    FrameLayout textVeiwFrame;
    TextView textColorPreviewTextView;
    boolean changedBeenMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_count_down);
        textVeiwFrame = findViewById(R.id.textColorPreviewBackground);
        textColorPreviewTextView = findViewById(R.id.textColorPreview);
        loadRecords();
        initTextColorPreview();

    }

    private void initTextColorPreview() {

        textColorPreviewTextView.setTextColor(currentRecord.getTextColor());
        textVeiwFrame.setBackgroundColor(currentRecord.getBackGroundColor());
    }

    private void loadRecords() {
        timersSharedPreferences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        currentRecord = RecordManager.getPriorToShowRecord(timerRecords);
    }


    public void setTextColorClickable(View view) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.getContext(), R.attr.colorPrimary, new AmbilWarnaDialog.OnAmbilWarnaListener() {
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
        dialog.show();

    }

    @Override
    public void finish() {
        super.finish();
        if (changedBeenMade) {
            RecordManager.updateRecordsInSharedPreferences(timersSharedPreferences, timerRecords);
        }

    }
}

package com.pfoss.countdownlivewallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

//import static com.pfoss.countdownlivewallpaper.WallpaperCreateActivity.fetchRecords;


public class MainActivity extends AppCompatActivity {
    SharedPreferences timersSharedPreferences;
    Toolbar toolbar;
    ArrayList<TimerRecord> timerRecords;
    ImageView backgroundImageView;
    TimerRecord currentRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        backgroundImageView = findViewById(R.id.backgroundImageView);
        initializeToolbar();
        timersSharedPreferences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        loadMain();
        Log.i("MAIN", "creating main");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMain();
        Log.i("MAIN", "resuming main");
    }

    private void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void onAddTimerButtonClick(View view) {
        startActivity(new Intent(this, WallpaperCreateActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i("Main-options", "is in option");
        switch (item.getItemId()) {
            case R.id.settingsMenuItem:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.countDownListMenuItem:
                break;
            case R.id.plusItem:
                Intent intent = new Intent(
                        WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(this, SimpleWallpaper.class));
                startActivity(intent);
                break;
            case R.id.deleteTimer:
                RecordManager.deleteRecord(timersSharedPreferences, timerRecords, currentRecord);
                Log.i("Main-options", "Deleting a record");
                refreshPreferences();
                loadMain();
                break;
            default:
                return false;
        }
        return false;
    }

    private void refreshPreferences() {
        timersSharedPreferences = this.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
    }

    private void findPriorTimerToShow() {

        for (int i = 0; i < timerRecords.size(); i++) {
            Log.i("findPriorTimerToShow", "saw a timer in for loop ");
            if (timerRecords.get(i).isPriorToShow()) {
                Log.i("findPriorTimerToShow", "showing " + i + " th timer");
                showTimer(timerRecords.get(i));
            }
        }

    }

    protected void loadMain() {//MUST CHANGE THIS NAME
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        if (timerRecords.size() == 0) {
            Log.i("LOADMAIN", "no timers exist right now");
            backgroundImageView.setVisibility(View.INVISIBLE);
        } else {
            findPriorTimerToShow();
        }
    }

    private void showTimer(TimerRecord timerRecord) {
        currentRecord = timerRecord;
        if (timerRecord.hasImage()) {
            showRecordImage(timerRecord);
        } else {
            backgroundImageView.setVisibility(View.INVISIBLE);
        }
        Log.i("showTimer", "trying to show : " + timerRecord.toString());
    }

    private void showRecordImage(TimerRecord record) {
        try {
            backgroundImageView.setImageBitmap(record.getBitmap());
            backgroundImageView.setVisibility(View.VISIBLE);
            backgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.i("showRecordImage", "image is set ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

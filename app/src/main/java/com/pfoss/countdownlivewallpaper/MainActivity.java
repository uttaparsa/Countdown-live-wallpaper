package com.pfoss.countdownlivewallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private CountDownDisplay countDownDisplayFragment;
    private int FPS = CountDownWallpaperService.getFPS();
    //change
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeToolbar();
        countDownDisplayFragment = (CountDownDisplay) getSupportFragmentManager().findFragmentById(R.id.surfaceFragment);

        Log.i("MAIN", "creating main");


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MAIN", "resuming main");
    }


    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        switch (item.getItemId()) {
            case R.id.settingsMenuItem:
                startActivity(new Intent(this, SettingsActivity.class));

                break;
            case R.id.countDownListMenuItem:
                break;
            case R.id.plusItem:
                Log.i("Main-options", "timer records is empty:" + countDownDisplayFragment.getTimerRecords().isEmpty());
                if (countDownDisplayFragment.getTimerRecords().isEmpty()) {
                    Toast.makeText(this, this.getString(R.string.no_timer_yet), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(
                            WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            new ComponentName(this, CountDownWallpaperService.class));
                    startActivity(intent);
                }
                break;
            case R.id.deleteTimer:
                countDownDisplayFragment.deleteTimer();

                break;
            default:
                return false;
        }
        return false;
    }
}




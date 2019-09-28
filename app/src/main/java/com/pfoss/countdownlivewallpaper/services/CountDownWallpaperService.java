package com.pfoss.countdownlivewallpaper.services;

import android.content.SharedPreferences;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pfoss.countdownlivewallpaper.CountDownDrawer;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.utils.RecordManager;

import java.util.ArrayList;


public class CountDownWallpaperService extends WallpaperService {
SharedPreferences sharedPreferences;
ArrayList<TimerRecord> timerRecords = new ArrayList<TimerRecord>();
TimerRecord currentRecord;

    public void loadRecords() {
        sharedPreferences = getSharedPreferences("com.pfoss.countdownlivewallpaper", MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(sharedPreferences);
        currentRecord = RecordManager.getPriorToShowRecord(timerRecords);
    }

    private CountDownDrawer drawer;

    @Override
    public Engine onCreateEngine() {

        return new SimpleWallpaperEngine();
    }



    private class SimpleWallpaperEngine extends Engine {

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            drawer.setHolder(holder);
            Log.i("SurfaceCreated", "runnning");
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.i("SurfaceDestroyed", "removed callbacks");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d("Wallpaper", "onVisibilityChanged: runnning");
            super.onVisibilityChanged(visible);
            if (visible) {
                drawer.start();
            } else {
                drawer.stop();
            }
        }


        public SimpleWallpaperEngine() {
            loadRecords();
            drawer = new CountDownDrawer(getBaseContext() , currentRecord,this);
            Log.d("Wallpaper", "SimpleWallpaperEngine: runnning");

        }


    }


}

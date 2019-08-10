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

    private SharedPreferences timersSharedPreferences;
    private ArrayList<TimerRecord> timerRecords;
    private TimerRecord currentRecord;
    private CountDownDrawer drawer;

    @Override
    public Engine onCreateEngine() {
        timersSharedPreferences = getSharedPreferences("com.pfoss.countdownlivewallpaper", MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        currentRecord = RecordManager.getPriorToShowRecord(timerRecords);
        drawer = new CountDownDrawer(this , currentRecord);
        return new SimpleWallpaperEngine();
    }

    private class SimpleWallpaperEngine extends Engine {

        private boolean hasSurface = true;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            hasSurface = true;
            drawer.setHolder(holder);
            drawer.initRunnable();
            drawer.start();
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.hasSurface = false;
            drawer.stop();
            Log.i("SurfaceDestroyed", "removed callbacks");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            this.hasSurface = visible;
            if (visible) {
                drawer.start();
            } else {
                drawer.stop();
            }
        }


        public SimpleWallpaperEngine() {


        }


    }


}

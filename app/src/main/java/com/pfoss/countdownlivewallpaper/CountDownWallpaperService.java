package com.pfoss.countdownlivewallpaper;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import java.util.ArrayList;


public class CountDownWallpaperService extends WallpaperService {

    private  SharedPreferences timersSharedPreferences;
    private  ArrayList<TimerRecord> timerRecords;
    private  TimerRecord currentRecord;
    private  Handler handler = new Handler();
    private  CountDownDrawer drawer;
    @Override
    public Engine onCreateEngine() {
        timersSharedPreferences = getSharedPreferences("com.pfoss.countdownlivewallpaper", MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        currentRecord = RecordManager.getPriorToShowRecord(timerRecords);
        drawer = new CountDownDrawer(handler , this , currentRecord );
        return new SimpleWallpaperEngine();
    }

    private class SimpleWallpaperEngine extends Engine {

        private boolean visible = true;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            drawer.initRunnable(getSurfaceHolder() , visible);

        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            drawer.start();

        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            handler.removeCallbacks(drawer.getRunnable());
            this.visible = false;
            Log.i("SurfaceDestroyed", "removed callbacks");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            this.visible = visible;
            if (visible) {
                drawer.start();
            } else {
                handler.removeCallbacks(drawer.getRunnable());
            }
        }


        public SimpleWallpaperEngine() {



        }



    }


}

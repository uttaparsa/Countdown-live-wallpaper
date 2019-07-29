package com.pfoss.countdownlivewallpaper;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import Helpers.BitmapHelper;


public class CountDownWallpaperService extends WallpaperService {
    private static final int FPS = 1;
    SharedPreferences timersSharedPreferences;
    ArrayList<TimerRecord> timerRecords;
    TimerRecord currentRecord;

    @Override
    public Engine onCreateEngine() {
        timersSharedPreferences = getSharedPreferences("com.pfoss.countdownlivewallpaper", MODE_PRIVATE);
        return new SimpleWallpaperEngine();
    }

    private class SimpleWallpaperEngine extends Engine {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                drawPage();
            }
        };
        Canvas canvas;
        boolean visible = true;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);


        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            handler.removeCallbacks(runnable);
            this.visible = false;
            Log.i("SurfaceDestroyed", "removed callbacks");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            this.visible = visible;
            if (visible) {
                handler.post(runnable);
            } else {
                handler.removeCallbacks(runnable);
            }
        }

        private void drawPage() {
            canvas = null;
            SurfaceHolder holder = getSurfaceHolder();
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    // Draw on the Canvas!
                    drawBackground();
                    drawHourText();
                    Log.i("RUNNABLE", "draw text finished");
                }
            } catch (Exception e) {
                Log.e("MYERROR", "Error While using canvas");
                e.printStackTrace();
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
                Log.i("RUNNABLE", "unlocked canvas in runnable");
            }
            // Schedule the next frame
            handler.removeCallbacks(runnable);
            if (visible) {
                handler.postDelayed(runnable, 2000 / FPS);
            }
        }

        private void drawHourText() {
            Paint unitsStyle = new Paint();
            unitsStyle.setTextSize(HOUR_TEXT_SIZE);
//            unitsStyle.setTextAlign(Paint.Align.CENTER);
            unitsStyle.setColor(Color.WHITE);
            canvas.drawText(getResources().getString(R.string.day_text), DAY_TEXT_X_AXIS, DAY_TEXT_Y_AXIS, unitsStyle);
            canvas.drawText(getResources().getString(R.string.hour_text), HOUR_TEXT_X_AXIS, HOUR_TEXT_Y_AXIS, unitsStyle);
            canvas.drawText(getResources().getString(R.string.minute_text), MINUTE_TEXT_X_AXIS, MINUTE_TEXT_Y_AXIS, unitsStyle);
            canvas.drawText(getResources().getString(R.string.second_text), SECOND_TEXT_X_AXIS, SECOND_TEXT_Y_AXIS, unitsStyle);
        }

        private void drawBackground() {
            if (currentRecord.hasImage()) {
                scaleAndDrawImage(canvas);
                Log.i("WALLPAPER", "wallpaper background set to image");
            } else {
                canvas.drawColor(Color.WHITE);
                Log.i("WALLPAPER", "wallpaper background set to color");
            }
        }

        private void scaleAndDrawImage(Canvas canvas) {

            DisplayMetrics metrics = getResources().getDisplayMetrics();

            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;
//
//            Bitmap destinationBitmap = BitmapHelper.scaleImageCenteredCrop(currentRecord.getBitmap() , screenHeight , screenWidth);
            Rect frameToDraw = new Rect(0, 0, screenWidth, screenHeight);
            RectF whereToDraw = new RectF(0, 0, screenWidth, screenHeight);

            canvas.drawBitmap(currentRecord.getBitmap(), frameToDraw, whereToDraw, null);
        }

        public int dipToPixel(int dipValue) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dipValue, getResources().getDisplayMetrics());
        }

        public SimpleWallpaperEngine() {

            timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
            currentRecord = RecordManager.getPriorToShowRecord(timerRecords);
            convertDipToPixel();
            handler.post(runnable);
        }

        int DAY_TEXT_SIZE;
        int DAY_TEXT_Y_AXIS;
        int DAY_TEXT_X_AXIS;
        int HOUR_TEXT_SIZE;
        int HOUR_TEXT_Y_AXIS;
        int HOUR_TEXT_X_AXIS;
        int MINUTE_TEXT_SIZE;
        int MINUTE_TEXT_Y_AXIS;
        int MINUTE_TEXT_X_AXIS;
        int SECOND_TEXT_SIZE;
        int SECOND_TEXT_Y_AXIS;
        int SECOND_TEXT_X_AXIS;

        private void convertDipToPixel() {
            DAY_TEXT_SIZE = dipToPixel(getResources().getInteger(R.integer.day_text_size));
            DAY_TEXT_Y_AXIS = dipToPixel(getResources().getInteger(R.integer.day_text_y_axis));
            DAY_TEXT_X_AXIS = dipToPixel(getResources().getInteger(R.integer.day_text_x_axis));

            HOUR_TEXT_SIZE = dipToPixel(getResources().getInteger(R.integer.hour_text_size));
            HOUR_TEXT_Y_AXIS = dipToPixel(getResources().getInteger(R.integer.hour_text_y_axis));
            HOUR_TEXT_X_AXIS = dipToPixel(getResources().getInteger(R.integer.hour_text_x_axis));

            MINUTE_TEXT_SIZE = dipToPixel(getResources().getInteger(R.integer.minute_text_size));
            MINUTE_TEXT_Y_AXIS = dipToPixel(getResources().getInteger(R.integer.minute_text_y_axis));
            MINUTE_TEXT_X_AXIS = dipToPixel(getResources().getInteger(R.integer.minute_text_x_axis));

            SECOND_TEXT_SIZE = dipToPixel(getResources().getInteger(R.integer.second_text_size));
            SECOND_TEXT_Y_AXIS = dipToPixel(getResources().getInteger(R.integer.second_text_y_axis));
            SECOND_TEXT_X_AXIS = dipToPixel(getResources().getInteger(R.integer.second_text_x_axis));
        }

    }
}

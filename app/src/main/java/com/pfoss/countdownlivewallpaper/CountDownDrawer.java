package com.pfoss.countdownlivewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import java.util.ArrayList;


public class CountDownDrawer {

    private Canvas canvas;
    private  Handler handler;
    private Context context;
    private  TimerRecord currentRecord;
    SurfaceHolder surfaceHolder ;
    boolean visible;
    private static final int FPS = 1;

    private Runnable runnable;

    CountDownDrawer(Handler handler  , Context context , TimerRecord currentRecord){
        this.handler = handler;
        this.context = context;
        this.currentRecord = currentRecord;
        convertDipToPixel();


    }

    public void initRunnable(final SurfaceHolder holder  , final boolean visible){
        this.surfaceHolder = holder;
        runnable = new Runnable() {
            @Override
            public void run() {
                drawPage(surfaceHolder ,visible);
            }
        };
    }

    public void start(){
        handler.post(runnable);
    }

    private void drawPage(SurfaceHolder holder,boolean visible) {
        canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                // Draw on the Canvas!
                drawBackground();
                drawUnitTexts();
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

    private void drawUnitTexts() {
        Paint unitsStyle = new Paint();
        unitsStyle.setTextSize(HOUR_TEXT_SIZE);
        unitsStyle.setColor(Color.WHITE);
        canvas.drawText(context.getResources().getString(R.string.day_text), DAY_TEXT_X_AXIS, DAY_TEXT_Y_AXIS, unitsStyle);
        canvas.drawText(context.getResources().getString(R.string.hour_text), HOUR_TEXT_X_AXIS, HOUR_TEXT_Y_AXIS, unitsStyle);
        canvas.drawText(context.getResources().getString(R.string.minute_text), MINUTE_TEXT_X_AXIS, MINUTE_TEXT_Y_AXIS, unitsStyle);
        canvas.drawText(context.getResources().getString(R.string.second_text), SECOND_TEXT_X_AXIS, SECOND_TEXT_Y_AXIS, unitsStyle);
    }

    private void drawBackground() {
        if (currentRecord.hasImage()) {
            drawImageBackground(canvas);
            Log.i("WALLPAPER", "wallpaper background set to image");
        } else {
            canvas.drawColor(Color.WHITE);
            Log.i("WALLPAPER", "wallpaper background set to color");
        }
    }

    private void drawImageBackground(Canvas canvas) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        Rect frameToDraw = new Rect(0, 0, screenWidth, screenHeight);
        RectF whereToDraw = new RectF(0, 0, screenWidth, screenHeight);

        canvas.drawBitmap(currentRecord.getBitmap(), frameToDraw, whereToDraw, null);
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
        DAY_TEXT_SIZE =   dipToPixel(context.getResources().getInteger(R.integer.day_text_size));
        DAY_TEXT_Y_AXIS = dipToPixel(context.getResources().getInteger(R.integer.day_text_y_axis));
        DAY_TEXT_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.day_text_x_axis));

        HOUR_TEXT_SIZE =   dipToPixel(context.getResources().getInteger(R.integer.hour_text_size));
        HOUR_TEXT_Y_AXIS = dipToPixel(context.getResources().getInteger(R.integer.hour_text_y_axis));
        HOUR_TEXT_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.hour_text_x_axis));

        MINUTE_TEXT_SIZE =   dipToPixel(context.getResources().getInteger(R.integer.minute_text_size));
        MINUTE_TEXT_Y_AXIS = dipToPixel(context.getResources().getInteger(R.integer.minute_text_y_axis));
        MINUTE_TEXT_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.minute_text_x_axis));

        SECOND_TEXT_SIZE =   dipToPixel(context.getResources().getInteger(R.integer.second_text_size));
        SECOND_TEXT_Y_AXIS = dipToPixel(context.getResources().getInteger(R.integer.second_text_y_axis));
        SECOND_TEXT_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.second_text_x_axis));
    }

    public int dipToPixel(int dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, context.getResources().getDisplayMetrics());
    }

    public static int getFPS() {
        return FPS;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}

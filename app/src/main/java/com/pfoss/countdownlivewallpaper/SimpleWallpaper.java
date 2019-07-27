package com.pfoss.countdownlivewallpaper;

import android.app.DatePickerDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import java.util.Random;


public class SimpleWallpaper extends WallpaperService {
    private static final int FPS = 1;

    @Override
    public Engine onCreateEngine() {
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

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            drawPage();
        }

        private void drawPage() {
            Log.i("myLog", "sanie ha migozarad");
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    // Draw on the Canvas!
                    Paint hourStyle = new Paint();
                    canvas.drawPaint(hourStyle);
                    hourStyle.setColor(Color.WHITE);
                    int MY_DIP_VALUE = 40;
                    int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            MY_DIP_VALUE, getResources ().getDisplayMetrics());
                    hourStyle.setTextSize(pixel);
                    hourStyle.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText("This is Hourrr", 350, 500, hourStyle);

                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            // Schedule the next frame
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000 / FPS);
        }

        public SimpleWallpaperEngine() {
            runnable.run();
        }

    }
}

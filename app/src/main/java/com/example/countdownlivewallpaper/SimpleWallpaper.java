package com.example.countdownlivewallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
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
                    canvas.drawColor(Color.RED);
                    setRandomColorAsBackground(canvas);

                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            // Schedule the next frame
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000 / FPS);
        }
        private  void setRandomColorAsBackground(Canvas canvas){
            Random rnd = new Random();
            canvas.drawARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        }

        public SimpleWallpaperEngine() {
            runnable.run();
        }

    }
}

package com.pfoss.countdownlivewallpaper.utils;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public abstract class Gradient extends Paint {

    protected static int getRandomColor(){
        Random rnd = new Random(System.currentTimeMillis());
        int waste = rnd.nextInt(5);
        for (int i = 0; i < waste; ++i) {
            rnd.nextInt();
        }
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


}

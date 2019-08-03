package com.pfoss.countdownlivewallpaper.themes;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

public abstract class Style {

    public static int invertColor(int input){
        int invertedRed = 255 - Color.red(input);
        int invertedGreen = 255 - Color.green(input);
        int invertedBlue = 255 - Color.blue(input);
        return Color.rgb(invertedRed , invertedGreen , invertedBlue);
    }
    public static int dipToPixel(int dipValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, context.getResources().getDisplayMetrics());
    }

}

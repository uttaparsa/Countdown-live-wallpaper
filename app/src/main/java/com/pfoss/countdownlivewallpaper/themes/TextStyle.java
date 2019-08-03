package com.pfoss.countdownlivewallpaper.themes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;

public class TextStyle extends Style {
    int UNIT_TEXT_SIZE;
    int NUMBER_TEXT_SIZE;
    int LABEL_TEXT_SIZE;

    TimerRecord currentRecord;

    public TextStyle(Context context , TimerRecord currentRecord){
        UNIT_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.unit_text_size) , context);
        NUMBER_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.number_text_size), context);
        LABEL_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.label_text_size), context);
        this.currentRecord = currentRecord;
    }

    public Paint getNumbersStyle(Paint textTheme) {
        int color = (isColorDark(currentRecord.getColor())) ? Color.WHITE : Color.BLACK;
        textTheme = new Paint();
        textTheme.setTextSize(NUMBER_TEXT_SIZE);
        textTheme.setColor(color);
        return textTheme;
    }

    public Paint getUnitsStyle(Paint textTheme) {
        int color = (isColorDark(currentRecord.getColor())) ? Color.WHITE : Color.BLACK;
        textTheme = new Paint();
        textTheme.setTextSize(UNIT_TEXT_SIZE);
        textTheme.setColor(color);
        return textTheme;
    }

    public Paint getLabelStyle(Paint textTheme) {
        int color = (isColorDark(currentRecord.getColor())) ? Color.WHITE : Color.BLACK;
        textTheme = new Paint();
        textTheme.setTextSize(LABEL_TEXT_SIZE);
        textTheme.setColor(color);
        textTheme.setTextAlign(Paint.Align.CENTER);
        return textTheme;
    }

    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return false; // It's a light color
        } else {
            return true; // It's a dark color
        }
    }


}

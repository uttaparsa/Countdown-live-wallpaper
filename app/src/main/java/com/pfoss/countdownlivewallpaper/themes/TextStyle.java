package com.pfoss.countdownlivewallpaper.themes;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;

public class TextStyle extends Style {
    int UNIT_TEXT_SIZE;
    int NUMBER_TEXT_SIZE;
    int LABEL_TEXT_SIZE;

    TimerRecord currentRecord;
    AssetManager assetManager;
    Typeface numbersFont;
    Typeface unitsFont;
    Typeface labelFont;
    public TextStyle(Context context , TimerRecord currentRecord){
        UNIT_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.unit_text_size_in_dip) , context);
        NUMBER_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.number_text_size_in_dip), context);
        LABEL_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.label_text_size), context);
        this.currentRecord = currentRecord;
        numbersFont = ResourcesCompat.getFont(context,R.font.roboto_medium);
        unitsFont = ResourcesCompat.getFont(context,R.font.roboto_thin);
        labelFont = ResourcesCompat.getFont(context,R.font.amita_bold);
         assetManager = context.getApplicationContext().getAssets();
    }

    public Paint getNumbersStyle(Paint textTheme) {
        int color = (isColorDark(currentRecord.getBackGroundColor())) ? Color.WHITE : Color.BLACK;
        textTheme = new Paint();
        textTheme.setTextSize(NUMBER_TEXT_SIZE);
        textTheme.setColor(currentRecord.getTextColor());
        textTheme.setTypeface(numbersFont);
        textTheme.setTextAlign(Paint.Align.RIGHT);
        return textTheme;
    }

    public Paint getUnitsStyle(Paint textTheme) {
        int color = (isColorDark(currentRecord.getBackGroundColor())) ? Color.WHITE : Color.BLACK;
        textTheme = new Paint();
        textTheme.setTextSize(UNIT_TEXT_SIZE);
        textTheme.setColor(currentRecord.getTextColor());
        textTheme.setTypeface(unitsFont);
        textTheme.setTextAlign(Paint.Align.LEFT);
        return textTheme;
    }

    public Paint getLabelStyle(Paint textTheme) {
        int color = (isColorDark(currentRecord.getBackGroundColor())) ? Color.WHITE : Color.BLACK;
        textTheme = new Paint();
        textTheme.setTextSize(LABEL_TEXT_SIZE);
        textTheme.setColor(currentRecord.getTextColor());
        textTheme.setTextAlign(Paint.Align.CENTER);
        textTheme.setTypeface(labelFont);
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

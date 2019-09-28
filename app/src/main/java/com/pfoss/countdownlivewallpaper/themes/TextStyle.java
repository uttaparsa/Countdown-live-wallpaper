package com.pfoss.countdownlivewallpaper.themes;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;

import java.util.Locale;

public class TextStyle extends Style {
    private int UNIT_TEXT_SIZE;
    private int NUMBER_TEXT_SIZE;
    private int LABEL_TEXT_SIZE;
    private int DATE_TEXT_SIZE;
    private int SINCE_OR_UNTIL_TEXT_SIZE;
    private boolean isPersian = Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());
    private TimerRecord currentRecord;
    private Typeface numbersFont;
    private Typeface unitsFont;
    private Typeface labelFont;

    public TextStyle(Context context , TimerRecord currentRecord){
        UNIT_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.unit_text_size_in_dip) , context);
        NUMBER_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.number_text_size_in_dip), context);
        LABEL_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.label_text_size), context);
        DATE_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.date_text_size),context);
        SINCE_OR_UNTIL_TEXT_SIZE = dipToPixel(context.getResources().getInteger(R.integer.since_or_until_text_size),context);

        this.currentRecord = currentRecord;
        if(isPersian){
            numbersFont = ResourcesCompat.getFont(context,R.font.vazir_fd);
            unitsFont = ResourcesCompat.getFont(context,R.font.vazir_medium);
            labelFont = ResourcesCompat.getFont(context,R.font.vazir_medium);
        }else{
            numbersFont = ResourcesCompat.getFont(context,R.font.roboto_medium);
            unitsFont = ResourcesCompat.getFont(context,R.font.roboto_thin);
            labelFont = ResourcesCompat.getFont(context,R.font.amita_bold);
        }

    }

    public Paint getNumbersStyle() {
        Paint textTheme = new Paint();

        textTheme.setTextSize(NUMBER_TEXT_SIZE);
        textTheme.setColor(currentRecord.getTextColor());
        textTheme.setTypeface(numbersFont);
        if(isPersian)textTheme.setTextAlign(Paint.Align.LEFT); else textTheme.setTextAlign(Paint.Align.RIGHT);

        textTheme.setAntiAlias(true);
        return textTheme;
    }

    public Paint getUnitsStyle() {
        Paint textTheme = new Paint();

        textTheme.setTextSize(UNIT_TEXT_SIZE);
        textTheme.setColor(currentRecord.getTextColor());
        textTheme.setTypeface(unitsFont);
        if(isPersian)textTheme.setTextAlign(Paint.Align.RIGHT); else textTheme.setTextAlign(Paint.Align.LEFT);
        textTheme.setAntiAlias(true);

        return textTheme;
    }

    public Paint getLabelStyle() {
        Paint textTheme = new Paint();

        textTheme.setTextSize(LABEL_TEXT_SIZE);
        textTheme.setColor(currentRecord.getTextColor());
        textTheme.setTextAlign(Paint.Align.CENTER);
        textTheme.setTypeface(labelFont);
        textTheme.setAntiAlias(true);

        return textTheme;
    }
    public Paint getDateAndTimeTextStyle(){
        Paint textTheme = new Paint();

        textTheme.setTextSize(DATE_TEXT_SIZE);
        textTheme.setColor(currentRecord.getTextColor());
        if(isPersian)textTheme.setTextAlign(Paint.Align.RIGHT); else textTheme.setTextAlign(Paint.Align.LEFT);
        textTheme.setTypeface(labelFont);
        textTheme.setAntiAlias(true);

        return textTheme;
    }
    public Paint getUntilTextStyle(){
        Paint textTheme = new Paint();

        textTheme.setTextSize(SINCE_OR_UNTIL_TEXT_SIZE);
        textTheme.setColor(currentRecord.getTextColor());
        if(isPersian)textTheme.setTextAlign(Paint.Align.LEFT); else textTheme.setTextAlign(Paint.Align.RIGHT);
        textTheme.setTypeface(labelFont);
        textTheme.setAntiAlias(true);

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

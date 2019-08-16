package com.pfoss.countdownlivewallpaper.data;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

public class TimerRecord {
    private String label;
    private boolean priorToShow;
    private String date;
    private String imagePath;
    private String id;
    private BackgroundTheme backgroundTheme;
    private int backGroundColor;
    private int textColor;

    public TimerRecord() {
        this.id = UUID.randomUUID().toString();
        Log.d("TIMER-RECORD", "TimerRecord:  id to string: " + this.id);
        this.label = "Label";
        this.setBackGroundColor(android.graphics.Color.BLACK);
        this.setTextColor(Color.WHITE);
    }

    public TimerRecord(String label, String date, String imagePath, int backgroundColor, int textColor, boolean priorToShow, BackgroundTheme backgroundTheme, String id) {
        this.label = label;
        this.date = date;
        this.imagePath = imagePath;
        this.backGroundColor = backgroundColor;
        this.textColor = textColor;
        this.priorToShow = priorToShow;
        this.backgroundTheme = backgroundTheme;
        this.id = id;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int color) {
        this.backGroundColor = color;
    }

    public String getDate() {
        return date;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isPriorToShow() {
        return priorToShow;
    }

    public void setPriorToShow(boolean priorToShow) {
        this.priorToShow = priorToShow;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        try {
            File f = new File(this.getImagePath(), this.getId() + ".png");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return label;

    }

    public BackgroundTheme getBackgroundTheme() {
        return backgroundTheme;
    }

    public void setBackgroundTheme(BackgroundTheme backgroundTheme) {
        this.backgroundTheme = backgroundTheme;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean hasImage() {
        return (this.getImagePath() != null);
    }


}

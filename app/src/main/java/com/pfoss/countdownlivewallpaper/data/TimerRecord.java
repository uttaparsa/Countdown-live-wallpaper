package com.pfoss.countdownlivewallpaper.data;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.pfoss.countdownlivewallpaper.utils.UnitType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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
    private ActiveShowUnits activeShowUnits;

    //This is the constructor that is used to make a new record
    public TimerRecord() {
        this.id = UUID.randomUUID().toString();
        Log.d("TIMER-RECORD", "TimerRecord:  id to string: " + this.id);
        this.label = "Label";
        this.setBackGroundColor(android.graphics.Color.BLACK);
        this.setTextColor(Color.WHITE);
        this.setActiveShowUnits(ActiveShowUnits.getDefault());
    }

    //This is the constructor used to retrieve saved records in shared preferences
    public TimerRecord(String label, String date, String imagePath, int backgroundColor, int textColor, boolean priorToShow, BackgroundTheme backgroundTheme,String id, ActiveShowUnits activeShowUnits) {
        this.label = label;
        this.date = date;
        this.imagePath = imagePath;
        this.backGroundColor = backgroundColor;
        this.textColor = textColor;
        this.priorToShow = priorToShow;
        this.backgroundTheme = backgroundTheme;
        this.id = id;
        this.activeShowUnits = activeShowUnits;
    }

    public ActiveShowUnits getActiveShowUnits() {
        return activeShowUnits;
    }

    public void setActiveShowUnits(ActiveShowUnits activeShowUnits) {
        this.activeShowUnits = activeShowUnits;
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

    public Date getDateInstance(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = sdf.parse(this.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
    public int getTimeDifference(Date inputDate){

        Log.d("tagdate", "getTimeDifferenceInSeconds: today's date : " + inputDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        long timeDifferenceInMillis = 0;
        try {

            timeDifferenceInMillis = inputDate.getTime() - sdf.parse(this.getDate()).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        return (int) (timeDifferenceInMillis / 1000);
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

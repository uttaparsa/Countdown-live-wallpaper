package com.pfoss.countdownlivewallpaper.data;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class TimerRecord implements Serializable {
    private String label;
    private boolean priorToShow;
    private Date date;
    private String imagePath;
    private UUID id;
    private BackgroundTheme backgroundTheme;
    private int Color;
    public TimerRecord() {
        this.id = UUID.randomUUID();
        this.label = "Label";
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "  label: " + label
                + " date: " + date
                + "bitmapBase64: " + imagePath;
    }

    public BackgroundTheme getBackgroundTheme() {
        return backgroundTheme;
    }

    public void setBackgroundTheme(BackgroundTheme backgroundTheme) {
        this.backgroundTheme = backgroundTheme;
    }

    public boolean hasImage() {
       return  (this.getImagePath() != null);
    }

    public enum BackgroundTheme{
        GRADIENT , PICTURE , SOLID;
    }
}

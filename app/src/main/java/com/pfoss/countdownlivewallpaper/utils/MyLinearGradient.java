package com.pfoss.countdownlivewallpaper.utils;

import android.graphics.LinearGradient;
import android.graphics.Shader;

public class MyLinearGradient extends Gradient {
    private int startColor;
    private int endColor;
    private float x0;
    private float x1;
    private float y0;
    private float y1;

    public MyLinearGradient(float x0,
                            float y0,
                            float x1,
                            float y1,
    Shader.TileMode tile){
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.startColor = getRandomColor();
        this.endColor = getRandomColor();


        this.setShader(new LinearGradient(x0, y0, x1, y1, startColor, endColor, tile));
    }

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
        this.setShader(new LinearGradient(x0, y0, x1, y1, startColor, endColor, Shader.TileMode.CLAMP));

    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
        this.setShader(new LinearGradient(x0, y0, x1, y1, startColor, endColor, Shader.TileMode.CLAMP));

    }

    public int getStartColor() {
        return startColor;
    }
}

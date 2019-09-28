package com.pfoss.countdownlivewallpaper.data;

import androidx.annotation.NonNull;

import java.util.Arrays;

/*
 vahed haye ghebele moshahede
* */
public class ActiveShowUnits {
    private static final int TOTAL_UNITS = 6;
    private boolean[] activeShowUnits;
    private static final int YEAR = 0;
    private static final int MONTH = 1;
    private static final int DAY = 2;
    private static final int HOUR = 3;
    private static final int MINUTE = 4;
    private static final int SECOND = 5;

    public ActiveShowUnits(boolean[] value) {
        this.activeShowUnits = value;
    }

    public static ActiveShowUnits getDefault() {
        boolean[] activeUnits = new boolean[ActiveShowUnits.getTotalUnitsCount()];
        Arrays.fill(activeUnits,Boolean.FALSE);
        activeUnits[ActiveShowUnits.getDAY()] = true;
        activeUnits[ActiveShowUnits.getMINUTE()] = true;
        activeUnits[ActiveShowUnits.getHOUR()] = true;
        activeUnits[ActiveShowUnits.getSECOND()] = true;
        return new ActiveShowUnits(activeUnits);
    }

    public void setActiveShowUnits(boolean[] activeShowUnits) {
        this.activeShowUnits = activeShowUnits;
    }

    public boolean[] getActiveShowUnitsBoolArray() {
        return activeShowUnits;
    }

    public boolean yearIsActive() { return activeShowUnits[YEAR]; }

    public boolean monthIsActive() {
        return activeShowUnits[MONTH];
    }

    public boolean dayIsActive() {
        return activeShowUnits[DAY];
    }

    public boolean hourIsActive() {
        return activeShowUnits[HOUR];
    }

    public boolean minuteIsActive() {
        return activeShowUnits[MINUTE];
    }

    public boolean secondIsActive() {
        return activeShowUnits[SECOND];
    }

    @NonNull
    @Override
    public String toString() {
        return "ActiveUnits: "
                + " ( "
                + (yearIsActive() ? "year" : "") + " , "
                + (monthIsActive() ? "month" : "") + " , "
                + (dayIsActive() ? "day" : "") + " , "
                + (hourIsActive() ? "hour" : "") + " , "
                + (minuteIsActive() ? "minute" : "") + " , "
                + (secondIsActive() ? "second" : "") + " )";
    }

    public int count() {
        int count = 0;
        for (boolean activeShowUnit : activeShowUnits) {
            if (activeShowUnit) {
                count++;
            }

        }

        return count;
    }

    public static int getDAY() {
        return DAY;
    }

    public static int getHOUR() {
        return HOUR;
    }

    public static int getMINUTE() {
        return MINUTE;
    }

    public static int getSECOND() {
        return SECOND;
    }

    public static int getTotalUnitsCount() {
        return TOTAL_UNITS;
    }
}

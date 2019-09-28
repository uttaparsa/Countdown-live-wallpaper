package com.pfoss.countdownlivewallpaper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Set;

public class PreferenceHelper {
    @Deprecated
    public static ArrayList<String> fetchPreferences(Context context) {//This might throw nullpointer
//        ArrayList<UnitType> unitTypeArrayList = new ArrayList<UnitType>();
        try {
            SharedPreferences timerPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> unitSelectionsSet = timerPreferences.getStringSet("multi_select_list_preference_1", null);
            ArrayList<String> unitSelectionArray = new ArrayList<>(unitSelectionsSet);
            ArrayList<String> unitSelectionArraySorted = new ArrayList<>();

            if (unitSelectionArray.contains("year")) {
                unitSelectionArraySorted.add("year");
            }
            if (unitSelectionArray.contains("month")) {
                unitSelectionArraySorted.add("month");
            }
            if (unitSelectionArray.contains("day")) {
                unitSelectionArraySorted.add("day");
            }
            if (unitSelectionArray.contains("hour")) {
                unitSelectionArraySorted.add("hour");
            }
            if (unitSelectionArray.contains("minute")) {
                unitSelectionArraySorted.add("minute");
            }
            if (unitSelectionArray.contains("second")) {
                unitSelectionArraySorted.add("second");
            }
            return unitSelectionArraySorted;
        } catch (NullPointerException e) {// only adds default values
            ArrayList<String> defaultValues = new ArrayList<String>();
            defaultValues.add("day");
            defaultValues.add("hour");
            defaultValues.add("minute");
            defaultValues.add("second");
            return defaultValues;
        }
    }
}


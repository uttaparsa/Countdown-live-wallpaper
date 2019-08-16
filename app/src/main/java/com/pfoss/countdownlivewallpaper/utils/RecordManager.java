package com.pfoss.countdownlivewallpaper.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RecordManager {
    private static final String RECORDS = "recordsIDS";
    private static final int LABEL = 0;
    private static final int DATE = 1;
    private static final int IMAGE_PATH = 2;
    private static final int BACKGROUND_COLOR = 3;
    private static final int TEXT_COLOR = 4;
    private static final int PRIOR_TO_SHOW = 5;
    private static final int BACKGROUND_THEME = 6;
    private static final int RECORD_ID = 7;


    public static void setAllElementsFlagToFalse(ArrayList<TimerRecord> arrayList) {
        for (TimerRecord element : arrayList) {
            element.setPriorToShow(false);
        }
    }

    public static TimerRecord getPriorToShowRecord(ArrayList<TimerRecord> records) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).isPriorToShow()) {
                return records.get(i);
            }
        }
        return null;
    }


    public static void removeById(ArrayList<TimerRecord> records, String id) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId().equals(id)) {
                records.remove(i);
                Log.i("TimerManager", "timer removed");
            }
        }

    }

    public static void updateRecordsInSharedPreferences(SharedPreferences sharedPreferences, ArrayList<TimerRecord> timerRecords) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < timerRecords.size(); i++) {

            sb.append(timerRecords.get(i).getId()).append(",");
            sharedPreferences.edit().putString(getMapID(timerRecords.get(i).getId(), LABEL), timerRecords.get(i).getLabel()).apply();
            sharedPreferences.edit().putString(getMapID(timerRecords.get(i).getId(), DATE), timerRecords.get(i).getDate()).apply();
            sharedPreferences.edit().putString(getMapID(timerRecords.get(i).getId(), IMAGE_PATH), timerRecords.get(i).getImagePath()).apply();
            sharedPreferences.edit().putString(getMapID(timerRecords.get(i).getId(), BACKGROUND_COLOR), String.valueOf(timerRecords.get(i).getBackGroundColor())).apply();
            sharedPreferences.edit().putString(getMapID(timerRecords.get(i).getId(), TEXT_COLOR), String.valueOf(timerRecords.get(i).getTextColor())).apply();
            sharedPreferences.edit().putString(getMapID(timerRecords.get(i).getId(), PRIOR_TO_SHOW), String.valueOf(timerRecords.get(i).isPriorToShow())).apply();
            sharedPreferences.edit().putString(getMapID(timerRecords.get(i).getId(), BACKGROUND_THEME), timerRecords.get(i).getBackgroundTheme().toString()).apply();
            sharedPreferences.edit().putString(getMapID(timerRecords.get(i).getId(), RECORD_ID), timerRecords.get(i).getId()).apply();
            Log.d("tagdate", "updateRecordsInSharedPreferences: tagdate to string " + timerRecords.get(i).getDate());
        }
        sharedPreferences.edit().putString(RECORDS, sb.toString()).apply();

    }

    private static String getMapID(String id, int name) {
        return id + name;
    }

    public static ArrayList<TimerRecord> fetchRecords(SharedPreferences sharedPreferences) {
        ArrayList<TimerRecord> newTimerRecords = new ArrayList<TimerRecord>();
        String recordIds = sharedPreferences.getString(RECORDS, null);
        if ((recordIds == null) ||
        recordIds.equals("")) return newTimerRecords;

        String[] recordIdArray = recordIds.split(",");
        Log.d(TAG, "fetchRecords: recordIdArray size : " + recordIdArray.length);
        Log.d(TAG, "fetchRecords: recordIds : " + recordIds);
        Log.d(TAG, "fetchRecords: timerREcords " + newTimerRecords);
        Log.d(TAG, "fetchRecords: timerREcords size " + newTimerRecords.size());

        for (int i = 0; i < recordIdArray.length; i++) {
            newTimerRecords.add(buildRecordObjectFromID(recordIdArray[i], sharedPreferences));
        }


        return newTimerRecords;
    }

    static TimerRecord buildRecordObjectFromID(String id, SharedPreferences sharedPreferences) {

        ArrayList<String> componentsList = getComponents(sharedPreferences, id);

        TimerRecord timerRecord = null;


            timerRecord = new TimerRecord(componentsList.get(LABEL),
                    componentsList.get(DATE),
                    componentsList.get(IMAGE_PATH),
                    Integer.valueOf(componentsList.get(BACKGROUND_COLOR)),
                    Integer.valueOf(componentsList.get(TEXT_COLOR)),
                    Boolean.valueOf(componentsList.get(PRIOR_TO_SHOW)),
                    BackgroundTheme.getValueOf(componentsList.get(BACKGROUND_THEME)),
                    componentsList.get(RECORD_ID));
            Log.d(TAG, "buildRecordObjectFromID: TRYING");


        Log.d(TAG, "buildRecordObjectFromID:component list includes: " + componentsList);
        Log.d("tagdate", "buildRecordObjectFromID: tagdate : "+timerRecord.getDate().toString());
        return timerRecord;
    }

    private static ArrayList<String> getComponents(SharedPreferences sharedPreferences, String id) {
        ArrayList<String> components = new ArrayList<>();
        components.add(sharedPreferences.getString(getMapID(id, LABEL), "FAKE"));
        components.add(sharedPreferences.getString(getMapID(id, DATE), "FAKE"));
        components.add(sharedPreferences.getString(getMapID(id, IMAGE_PATH), "FAKE"));
        components.add(sharedPreferences.getString(getMapID(id, BACKGROUND_COLOR), "FAKE"));
        components.add(sharedPreferences.getString(getMapID(id, TEXT_COLOR), "FAKE"));
        components.add(sharedPreferences.getString(getMapID(id, PRIOR_TO_SHOW), "FAKE"));
        components.add(sharedPreferences.getString(getMapID(id, BACKGROUND_THEME), "FAKE"));
        components.add(sharedPreferences.getString(getMapID(id, RECORD_ID), "FAKE"));
        return components;
    }
//     public static ArrayList<TimerRecord> fetchRecords(SharedPreferences sharedPreferences) {
//        ArrayList<TimerRecord> newTimerRecords = new ArrayList<>();
//        try {
//            newTimerRecords = (ArrayList<TimerRecord>) ObjectSerializer.deserialize(sharedPreferences.getString("records", ObjectSerializer.serialize(new ArrayList<TimerRecord>())));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return newTimerRecords;
//    }

    public static String saveImageToInternalStorage(Bitmap bitmapImage, ContextWrapper cw, TimerRecord timerRecord) {

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, timerRecord.getId().toString() + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.d("TimerRecordManager", "image was saved to " + directory.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        timerRecord.setImagePath(directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }

    public static void deleteRecord(SharedPreferences sharedPreferences, ArrayList<TimerRecord> timerRecords, TimerRecord currentRecord) {
        Log.d("ARRAY", "before " + timerRecords.toString());
        sharedPreferences.edit().remove(currentRecord.getId()).apply();
        RecordManager.removeById(timerRecords, currentRecord.getId());
        if (timerRecords.size() > 0) {
            timerRecords.get(timerRecords.size() - 1).setPriorToShow(true);
        }
        Log.d("ARRAY", "after " + timerRecords.toString());
        RecordManager.updateRecordsInSharedPreferences(sharedPreferences, timerRecords);
    }

    private static final String arabic = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";

    private static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }
}
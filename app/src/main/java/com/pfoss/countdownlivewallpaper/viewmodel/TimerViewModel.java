package com.pfoss.countdownlivewallpaper.viewmodel;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.pfoss.countdownlivewallpaper.data.ActiveShowUnits;
import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

//some comment
public class TimerViewModel {
    private static final String RECORDS = "recordsIDS";
    private static final int LABEL = 0;
    private static final int DATE = 1;
    private static final int IMAGE_PATH = 2;
    private static final int BACKGROUND_COLOR = 3;
    private static final int TEXT_COLOR = 4;
    private static final int PRIOR_TO_SHOW = 5;
    private static final int BACKGROUND_THEME = 6;
    private static final int RECORD_ID = 7;
    private static final int ACTIVE_SHOW_UNITS = 8;


    private ArrayList<TimerRecord> mTimerRecords;
    private SharedPreferences mSharedPreferences;

    public TimerViewModel(Context context) {
        mSharedPreferences = context.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        this.fetchRecords();
    }

    public ArrayList<TimerRecord> getTimerRecords() {
        return mTimerRecords;
    }

    public void saveNewRecord(TimerRecord newRecord) {

        this.setAllTimersLastDisplayFlagToFalse();
        this.getTimerRecords().add(newRecord);
        this.updateRecordsInSharedPreferences();
    }

    public void setAllTimersLastDisplayFlagToFalse() {
        for (TimerRecord element : mTimerRecords) {
            element.setPriorToShow(false);
        }
    }

    public TimerRecord getLastSelectedTimer() {
        for (int i = 0; i < mTimerRecords.size(); i++) {
            if (mTimerRecords.get(i).isPriorToShow()) {
                return mTimerRecords.get(i);
            }
        }
        return null;
    }


    public void removeById(String id) {
        for (int i = 0; i < mTimerRecords.size(); i++) {
            if (mTimerRecords.get(i).getId().equals(id)) {
                mTimerRecords.remove(i);
                Log.i("TimerManager", "timer removed");
            }
        }

    }

    public void updateRecordsInSharedPreferences() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mTimerRecords.size(); i++) {

            sb.append(mTimerRecords.get(i).getId()).append(",");
            mSharedPreferences.edit().putString(getSaveId(mTimerRecords.get(i).getId(), LABEL), mTimerRecords.get(i).getLabel()).apply();
            mSharedPreferences.edit().putString(getSaveId(mTimerRecords.get(i).getId(), DATE), mTimerRecords.get(i).getDate()).apply();
            mSharedPreferences.edit().putString(getSaveId(mTimerRecords.get(i).getId(), IMAGE_PATH), mTimerRecords.get(i).getImagePath()).apply();
            mSharedPreferences.edit().putString(getSaveId(mTimerRecords.get(i).getId(), BACKGROUND_COLOR), String.valueOf(mTimerRecords.get(i).getBackGroundColor())).apply();
            mSharedPreferences.edit().putString(getSaveId(mTimerRecords.get(i).getId(), TEXT_COLOR), String.valueOf(mTimerRecords.get(i).getTextColor())).apply();
            mSharedPreferences.edit().putString(getSaveId(mTimerRecords.get(i).getId(), PRIOR_TO_SHOW), String.valueOf(mTimerRecords.get(i).isPriorToShow())).apply();
            mSharedPreferences.edit().putString(getSaveId(mTimerRecords.get(i).getId(), BACKGROUND_THEME), mTimerRecords.get(i).getBackgroundTheme().toString()).apply();
            mSharedPreferences.edit().putString(getSaveId(mTimerRecords.get(i).getId(), RECORD_ID), mTimerRecords.get(i).getId()).apply();
            saveActiveUnitsArray(mSharedPreferences
                    , mTimerRecords.get(i).getActiveShowUnits().getActiveShowUnitsBoolArray()
                    , getSaveId(mTimerRecords.get(i).getId(), ACTIVE_SHOW_UNITS)
            );
            Log.d("tagdate", "updateRecordsInSharedPreferences: tagdate to string " + mTimerRecords.get(i).getDate());
        }
        mSharedPreferences.edit().putString(RECORDS, sb.toString()).apply();

    }


    private static String getSaveId(String id, int name) {
        return id + name;
    }

    public void fetchRecords() {
        mTimerRecords = new ArrayList<TimerRecord>();
        String recordIds = mSharedPreferences.getString(RECORDS, null);
        if ((recordIds == null) ||
                recordIds.equals("")) return;

        String[] recordIdArray = recordIds.split(",");
        Log.d(TAG, "fetchRecords: recordIdArray size : " + recordIdArray.length);
        Log.d(TAG, "fetchRecords: recordIds : " + recordIds);
        Log.d(TAG, "fetchRecords: timerREcords " + mTimerRecords);
        Log.d(TAG, "fetchRecords: timerREcords size " + mTimerRecords.size());

        for (String id : recordIdArray)
            mTimerRecords.add(buildRecordObjectFromID(id));

    }

    TimerRecord buildRecordObjectFromID(String id) {

        ArrayList<String> componentsList = this.getComponents(id);

        TimerRecord timerRecord = null;


        timerRecord = new TimerRecord(componentsList.get(LABEL),
                componentsList.get(DATE),
                componentsList.get(IMAGE_PATH),
                Integer.parseInt(componentsList.get(BACKGROUND_COLOR)),
                Integer.parseInt(componentsList.get(TEXT_COLOR)),
                Boolean.parseBoolean(componentsList.get(PRIOR_TO_SHOW)),
                BackgroundTheme.getValueOf(componentsList.get(BACKGROUND_THEME)),
                componentsList.get(RECORD_ID),
                new ActiveShowUnits(this.loadActiveUnitsArray(getSaveId(id, ACTIVE_SHOW_UNITS)))
        );

        Log.d(TAG, "buildRecordObjectFromID:component list includes: " + componentsList);
        return timerRecord;
    }

    private boolean[] loadActiveUnitsArray(String arrayName) {

        int size = mSharedPreferences.getInt(arrayName + "_size", 6);
        boolean[] array = new boolean[size];
        for (int i = 0; i < size; i++)
            array[i] = mSharedPreferences.getBoolean(arrayName + "_" + i, true);

        return array;
    }

    private boolean saveActiveUnitsArray(SharedPreferences sharedPreferences, boolean[] array, String arrayName) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(arrayName + "_size", array.length);

        for (int i = 0; i < array.length; i++)
            editor.putBoolean(arrayName + "_" + i, array[i]);

        return editor.commit();
    }


    private ArrayList<String> getComponents(String id) {
        ArrayList<String> components = new ArrayList<>();
        components.add(mSharedPreferences.getString(getSaveId(id, LABEL), "FAKE"));
        components.add(mSharedPreferences.getString(getSaveId(id, DATE), "FAKE"));
        components.add(mSharedPreferences.getString(getSaveId(id, IMAGE_PATH), "FAKE"));
        components.add(mSharedPreferences.getString(getSaveId(id, BACKGROUND_COLOR), "FAKE"));
        components.add(mSharedPreferences.getString(getSaveId(id, TEXT_COLOR), "FAKE"));
        components.add(mSharedPreferences.getString(getSaveId(id, PRIOR_TO_SHOW), "FAKE"));
        components.add(mSharedPreferences.getString(getSaveId(id, BACKGROUND_THEME), "FAKE"));
        components.add(mSharedPreferences.getString(getSaveId(id, RECORD_ID), "FAKE"));
        return components;
    }


    public static String saveImageToInternalStorage(Bitmap bitmapImage, ContextWrapper cw, TimerRecord timerRecord) {

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, timerRecord.getId() + ".png");

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
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void deleteRecord(TimerRecord currentRecord) {
        Log.d("ARRAY", "before " + mTimerRecords.toString());
        mSharedPreferences.edit().remove(currentRecord.getId()).apply();
        this.removeById(currentRecord.getId());
        if (mTimerRecords.size() > 0) {
            mTimerRecords.get(mTimerRecords.size() - 1).setPriorToShow(true);
        }
        Log.d("ARRAY", "after " + mTimerRecords.toString());
        this.updateRecordsInSharedPreferences();
    }

    public TimerRecord getTimerRecordByWidgetId(int widgetId) throws NullPointerException {
        TimerRecord timerRecord = null;
        int currentCountDownIndexInRecordsArray = mSharedPreferences.getInt(String.valueOf(widgetId), -1);
        Log.d(TAG, "getCurrent: currentCountDownIndexInRecordsArray value is : " + currentCountDownIndexInRecordsArray);

        try {
            timerRecord = mTimerRecords.get(currentCountDownIndexInRecordsArray);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.i(TAG, "getCurrent: index was not valid");
        }
        return timerRecord;
    }

}
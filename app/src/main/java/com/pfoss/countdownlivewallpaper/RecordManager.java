package com.pfoss.countdownlivewallpaper;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class RecordManager {
    public static void setAllElementsFlagToFalse(ArrayList<TimerRecord> arrayList) {
        for (TimerRecord element : arrayList) {
            element.setPriorToShow(false);
        }
    }

    public static void removeById(ArrayList<TimerRecord> records, UUID id) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId().equals(id)) {
                records.remove(i);
                Log.i("TimerManager", "timer removed");
            }
        }

    }

    public static void updateRecordsInSharedPreferences(SharedPreferences sharedPreferences, ArrayList<TimerRecord> newTimerRecords) {

        try {
            sharedPreferences.edit().putString("records", ObjectSerializer.serialize(newTimerRecords)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected static ArrayList<TimerRecord> fetchRecords(SharedPreferences sharedPreferences) {
        ArrayList<TimerRecord> newTimerRecords = new ArrayList<>();
        try {
            newTimerRecords = (ArrayList<TimerRecord>) ObjectSerializer.deserialize(sharedPreferences.getString("records", ObjectSerializer.serialize(new ArrayList<TimerRecord>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newTimerRecords;
    }

    public static String saveImageToInternalStorage(Bitmap bitmapImage, ContextWrapper cw,TimerRecord timerRecord){

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,timerRecord.getId().toString()+".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.d("TimerRecordManager" , "image was saved to "+directory.getAbsolutePath());
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
    public static void deleteRecord(SharedPreferences sharedPreferences ,ArrayList<TimerRecord> timerRecords , TimerRecord  currentRecord){
        RecordManager.removeById(timerRecords , currentRecord.getId());
        if(timerRecords.size()>0){
            timerRecords.get(timerRecords.size()-1).setPriorToShow(true);
        }
        RecordManager.updateRecordsInSharedPreferences(sharedPreferences , timerRecords);
    }

}
package com.pfoss.countdownlivewallpaper;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CountDownDisplay extends Fragment implements SurfaceHolder.Callback {
    private SharedPreferences timersSharedPreferences;
    private TimerRecord currentRecord;
    private ArrayList<TimerRecord> timerRecords;
    private ImageView backgroundImageView;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    public CountDownDisplay() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeRecords();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countdown_display, container, false);
//        backgroundImageView = view.findViewById(R.id.backgroundImageView);

        // Inflate the layout for this fragment
        surfaceView =  view.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        return view;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }



    private void refreshPreferences() {
        timersSharedPreferences = this.getActivity().getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
    }
    private void initializeRecords() {

        timersSharedPreferences = this.getActivity().getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
    }

    private void showRecordImage(TimerRecord record) {
        try {
            backgroundImageView.setImageBitmap(record.getBitmap());
            backgroundImageView.setVisibility(View.VISIBLE);
            backgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.i("showRecordImage", "image is set ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void deleteTimer() {
        Log.i("Main-options", "Deleting a record");
        RecordManager.deleteRecord(timersSharedPreferences, timerRecords, currentRecord);
        refreshPreferences();
    }

    public ArrayList<TimerRecord> getTimerRecords() {
        return timerRecords;
    }
}

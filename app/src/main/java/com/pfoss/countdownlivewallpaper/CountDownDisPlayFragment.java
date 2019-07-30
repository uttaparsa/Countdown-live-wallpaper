package com.pfoss.countdownlivewallpaper;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
public class CountDownDisPlayFragment extends Fragment implements SurfaceHolder.Callback {
    private SharedPreferences timersSharedPreferences;
    private ArrayList<TimerRecord> timerRecords;
    private TimerRecord currentRecord;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private  CountDownDrawer drawer ;
    private boolean visible = false;
    private Handler handler = new Handler();
    public CountDownDisPlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeRecords();
        timersSharedPreferences = getContext().getSharedPreferences("com.pfoss.countdownlivewallpaper", MainActivity.MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        currentRecord = RecordManager.getPriorToShowRecord(timerRecords);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countdown_display, container, false);


        // Inflate the layout for this fragment
        surfaceView =  view.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        drawer = new CountDownDrawer(handler , getActivity() , currentRecord );
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        drawer.initRunnable(surfaceHolder , visible);
        drawer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        handler.removeCallbacks(drawer.getRunnable());
        this.visible = false;
    }


    private void refreshPreferences() {
        timersSharedPreferences = this.getActivity().getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
    }
    private void initializeRecords() {

        timersSharedPreferences = this.getActivity().getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
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

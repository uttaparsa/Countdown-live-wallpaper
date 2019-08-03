package com.pfoss.countdownlivewallpaper.fragments;


import android.app.Activity;
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
import android.widget.Toast;

import com.pfoss.countdownlivewallpaper.CountDownDrawer;
import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.utils.RecordManager;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CountDownDisplayFragment extends Fragment implements SurfaceHolder.Callback {
    private SharedPreferences timersSharedPreferences;
    private ArrayList<TimerRecord> timerRecords;
    private TimerRecord currentRecord;
    private CountDownDrawer drawer;
    private boolean visible = true;
    private SurfaceView surfaceView;
    private Handler handler = new Handler();
    private ImageView emptyImageView;

    public CountDownDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DISPLAY", "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DISPLAY", "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countdown_display, container, false);
        // Inflate the layout for this fragment

        surfaceView = view.findViewById(R.id.surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);//don't know what it does
        emptyImageView = view.findViewById(R.id.noTimerFound);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadRecords();

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!timerRecords.isEmpty()) {
            drawer = new CountDownDrawer(handler, getActivity(), currentRecord);
            drawer.initRunnable(surfaceHolder, visible);
            drawer.start();
            Log.i("DISPLAY-SURFACE", "records weren't empty ,starting drawer");
        } else {
            emptyImageView.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
            Log.i("DISPLAY-SURFACE", "records were empty , showing empty image view");
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (!timerRecords.isEmpty()) {
            handler.removeCallbacks(drawer.getRunnable());
            this.visible = false;
        }
        Log.i("DISPLAY-SURFACE", "surface destroyed");
    }


//    private void refreshPreferences() {
//        timersSharedPreferences = this.getActivity().getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
//        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
//    }

    private void loadRecords() {

        timersSharedPreferences = this.getActivity().getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        currentRecord = RecordManager.getPriorToShowRecord(timerRecords);
    }


    public void deleteTimer() {
        if (timerRecords.isEmpty()) {
            Log.i("DISPLAY", "No record to delete");
            Toast.makeText(getContext(),
                    currentRecord.getLabel() + " " + getActivity().getResources().getString(R.string.no_record_to_delete),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            Log.i("DISPLAY", "Deleting a record");
            Toast.makeText(getContext(),
                    currentRecord.getLabel() + " " + getActivity().getResources().getString(R.string.delete_done),
                    Toast.LENGTH_SHORT)
                    .show();
            RecordManager.deleteRecord(timersSharedPreferences, timerRecords, currentRecord);
            loadRecords();
            drawer.stop();
            mCallbacks.deletePlease();
        }
    }

    private Callbacks mCallbacks;


    public interface Callbacks {
        //Callback for when button clicked.
        public void deletePlease();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activities containing this fragment must implement its callbacks
        mCallbacks = (Callbacks) activity;

    }

    public ArrayList<TimerRecord> getTimerRecords() {
        return timerRecords;
    }
}

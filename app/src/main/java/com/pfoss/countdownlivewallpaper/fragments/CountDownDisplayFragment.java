package com.pfoss.countdownlivewallpaper.fragments;


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
    private ImageView noTimerFoundImageView;
    private static final String TAG = "displayFragment";

    public CountDownDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadRecords();
        drawer = new CountDownDrawer(getContext());
        Log.d("DISPLAY", "onCreate");
    }

    private void showSurface() {
        if (!timerRecords.isEmpty()) {
            startDrawer();
        } else {
            noTimerFoundImageView.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
        }
    }

    private void startDrawer() {
        drawer.setCurrentRecord(currentRecord);
        drawer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecords();
        Log.d("DISPLAY", "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countdown_display, container, false);
        // Inflate the layout for this fragment

        surfaceView = view.findViewById(R.id.surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        drawer.setHolder(surfaceHolder);
        surfaceHolder.addCallback(this);//don't know what it does
        noTimerFoundImageView = view.findViewById(R.id.noTimerFound);
        Log.d("DISPLAY", "onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("DISPLAY", "onActivityCreated");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!timerRecords.isEmpty()) {
            showSurface();
        } else {
            noTimerFoundImageView.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
        }

        Log.d("DISPLAY", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d("DISPLAY", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (!timerRecords.isEmpty()) {
            drawer.stop();
        }
        Log.i("DISPLAY-SURFACE", "surfaceDestroyed");
    }


    private void loadRecords( ) {

        timersSharedPreferences = this.getActivity().getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        timerRecords = RecordManager.fetchRecords(timersSharedPreferences);
        currentRecord = RecordManager.getPriorToShowRecord(timerRecords);
        Log.d(TAG, " reloading records");
    }


    public void deleteTimer() {
        if (timerRecords.isEmpty()) {
            Log.d(TAG, "No record to delete");
            Toast.makeText(getContext(),
                    getActivity().getResources().getString(R.string.no_record_to_delete),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            Log.d(TAG, "Deleting a record");
            Toast.makeText(getContext(),
                    currentRecord.getLabel() + " " + getActivity().getResources().getString(R.string.delete_done),
                    Toast.LENGTH_SHORT)
                    .show();
            drawer.stop();
            RecordManager.deleteRecord(timersSharedPreferences, timerRecords, currentRecord);
            loadRecords();
            if (!timerRecords.isEmpty()) {
                startDrawer();
            } else {
                noTimerFoundImageView.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.INVISIBLE);
            }

        }

    }

    public void drawTimer(int index) {
        if (index < timerRecords.size()) {
            try {
                drawer.stop();
            }catch (NullPointerException ex){
                Log.d(TAG, "drawTimer: no drawer found but it's ok :)");
            }

            RecordManager.setAllElementsFlagToFalse(timerRecords);
            timerRecords.get(index).setPriorToShow(true);
            RecordManager.updateRecordsInSharedPreferences(timersSharedPreferences, timerRecords);
            loadRecords();
            startDrawer();
            Log.d("DISPLAY", "called drawTimer ");
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public CountDownDrawer getDrawer() {
        return drawer;
    }

    public ArrayList<TimerRecord> getTimerRecords() {
        return timerRecords;
    }
}

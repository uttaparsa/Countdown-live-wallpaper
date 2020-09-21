package com.pfoss.countdownlivewallpaper.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pfoss.countdownlivewallpaper.CountDownDrawer;
import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class CountDownDisplayFragment extends Fragment implements SurfaceHolder.Callback {
    private TimerRecord timerToDisplay;
    private CountDownDrawer drawer;
    private boolean visible = true;
    private SurfaceView surfaceView;
    private ImageView noTimerFoundImageView;
    private static final String TAG = "displayFragment";
    private TimerViewModel timerViewModel;

    public CountDownDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerViewModel = new TimerViewModel(getContext());
        timerToDisplay = timerViewModel.getLastSelectedTimer();
        drawer = new CountDownDrawer(getContext());

        Log.d("DISPLAY", "onCreate");
    }

    private void showSurface() {
        if (!timerViewModel.getTimerRecords().isEmpty()) {
            startDrawer();
        } else {
            noTimerFoundImageView.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
        }
    }

    private void startDrawer() {
        drawer.setCurrentRecord(timerToDisplay);
        drawer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        timerViewModel.fetchRecords();
        timerToDisplay = timerViewModel.getLastSelectedTimer();
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
        if (!timerViewModel.getTimerRecords().isEmpty()) {
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
        if (!timerViewModel.getTimerRecords().isEmpty()) {
            drawer.stop();
        }
        Log.i("DISPLAY-SURFACE", "surfaceDestroyed");
    }



    public void deleteTimer() {
        if (timerViewModel.getTimerRecords().isEmpty()) {
            Log.d(TAG, "No record to delete");
            Toast.makeText(getContext(),
                    getActivity().getResources().getString(R.string.no_record_to_delete),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            Log.d(TAG, "Deleting a record");
            Toast.makeText(getContext(),
                    timerToDisplay.getLabel() + " " + getActivity().getResources().getString(R.string.delete_done),
                    Toast.LENGTH_SHORT)
                    .show();
            drawer.stop();
            timerViewModel.deleteRecord(timerToDisplay);
            timerViewModel.fetchRecords();
            timerToDisplay = timerViewModel.getLastSelectedTimer();
            if (!timerViewModel.getTimerRecords().isEmpty()) {
                startDrawer();
            } else {
                noTimerFoundImageView.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.INVISIBLE);
            }

        }

    }

    public void drawTimer(int index) {
        if (index < timerViewModel.getTimerRecords().size()) {
            try {
                drawer.stop();
            } catch (NullPointerException ex) {
                Log.d(TAG, "drawTimer: no drawer found but it's ok :)");
            }

            timerViewModel.setAllTimersLastDisplayFlagToFalse();
            timerViewModel.getTimerRecords().get(index).setPriorToShow(true);
            timerViewModel.updateRecordsInSharedPreferences();
            startDrawer();
            Log.d("DISPLAY", "called drawTimer ");
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public CountDownDrawer getDrawer() {
        return drawer;
    }

}

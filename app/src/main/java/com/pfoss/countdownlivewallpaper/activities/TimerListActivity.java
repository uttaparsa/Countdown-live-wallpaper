package com.pfoss.countdownlivewallpaper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;

import java.util.ArrayList;

public class TimerListActivity extends AppCompatActivity {
    ArrayList<TimerRecord> timersData;
    private RecyclerView mTimersListRecyclerView;
    private TimerListAdapter mTimersListAdapter;
    private RecyclerView.LayoutManager mTimersListLayoutManager;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_list);
        initializeToolbar();
        initRecords();

        mTimersListRecyclerView = findViewById(R.id.timerListView);
        mTimersListRecyclerView.setHasFixedSize(true);
        mTimersListLayoutManager = new LinearLayoutManager(this);
        mTimersListAdapter = new TimerListAdapter(timersData);

        mTimersListRecyclerView.setLayoutManager(mTimersListLayoutManager);
        mTimersListRecyclerView.setAdapter(mTimersListAdapter);

        mTimersListAdapter.setOnItemClickListener(new TimerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                requestToShowTimer(position);
            }
        });
        handler = new Handler();

        final Runnable timersThread = new Runnable() {
            public void run() {
                mTimersListAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(timersThread);


    }

    private void initRecords() {
        SharedPreferences packageSharedPreferences = getSharedPreferences("com.pfoss.countdownlivewallpaper", MODE_PRIVATE);
        timersData = TimerViewModel.fetchRecords(packageSharedPreferences);
    }
    private void initializeToolbar() {
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarBigTitle = findViewById(R.id.titleText);
        toolbarBigTitle.setText(getResources().getString(R.string.timers_list_title));
        toolbarBigTitle.setTextColor(Color.BLACK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
            //getWindow().setStatusBarColor(Color.GRAY);
        }

        setSupportActionBar(toolbar);

    }
    public void requestToShowTimer(int index){
        Intent requestShowTimerInMain = new Intent(this , MainActivity.class);
        requestShowTimerInMain.putExtra("timer_show_request" , index);
        requestShowTimerInMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(requestShowTimerInMain);
        overridePendingTransition(R.anim.slide_in_up , R.anim.slide_out_up);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up , R.anim.slide_out_up);
    }
}

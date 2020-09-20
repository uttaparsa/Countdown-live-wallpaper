package com.pfoss.countdownlivewallpaper.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;

public class TimerListActivity extends AppCompatActivity {
    private RecyclerView mTimersListRecyclerView;
    private TimerListAdapter mTimersListAdapter;
    private RecyclerView.LayoutManager mTimersListLayoutManager;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_list);
        initializeToolbar();

        TimerViewModel timerViewModel = new TimerViewModel(this.getApplicationContext());
        timerViewModel.fetchRecords();

        mTimersListRecyclerView = findViewById(R.id.timerListView);
        mTimersListRecyclerView.setHasFixedSize(true);
        mTimersListLayoutManager = new LinearLayoutManager(this);
        mTimersListAdapter = new TimerListAdapter(timerViewModel.getTimerRecords());

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

    private void initializeToolbar() {
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarBigTitle = findViewById(R.id.titleText);
        toolbarBigTitle.setText(getResources().getString(R.string.timers_list_title));
        toolbarBigTitle.setTextColor(Color.BLACK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
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

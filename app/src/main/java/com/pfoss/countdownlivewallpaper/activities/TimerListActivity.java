package com.pfoss.countdownlivewallpaper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.utils.RecordManager;

import java.util.ArrayList;

public class TimerListActivity extends AppCompatActivity {
    ArrayList<TimerRecord> records;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_list);
        initializeToolbar();
        ListView recordsListView = findViewById(R.id.timerListView);
        initRecords();

        ArrayAdapter<TimerRecord> timerListArrayAdapter = new ArrayAdapter<TimerRecord>(this, android.R.layout.simple_list_item_1, records);
        recordsListView.setAdapter(timerListArrayAdapter);

        recordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                requestToShowTimer(i);
            }
        });
    }

    private void initRecords() {
        SharedPreferences packageSharedPreferences = getSharedPreferences("com.pfoss.countdownlivewallpaper", MODE_PRIVATE);
        records = RecordManager.fetchRecords(packageSharedPreferences);
    }
    private void initializeToolbar() {
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.timers_list_title));
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

package com.pfoss.countdownlivewallpaper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.services.CountDownWidget;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;

import java.util.ArrayList;

public class WidgetConfigurationActivity extends AppCompatActivity {
    private int appWidgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    private ArrayList<TimerRecord> records;
    private SharedPreferences packageSharedPreferences;
    private static String TAG = "WidgetConfig";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_configuration);
        initializeToolbar();

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if (extras != null) {
            appWidgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        //check if appwidget id is valid
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
        setResult(RESULT_CANCELED, resultValue);
        //close the app if app widget id is not valid
        if (appWidgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        prepareListOfCountDowns();


    }

    private void prepareListOfCountDowns() {
        ListView recordsListView = findViewById(R.id.timerListView);
        initRecords();
        ArrayAdapter<TimerRecord> timerListArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, records);
        recordsListView.setAdapter(timerListArrayAdapter);
        recordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                saveAsTimerToShow(i);
            }
        });
    }



    private void saveAsTimerToShow(int selectedIndex) {
        Log.d(TAG, "saveAsTimerToShow: selected timer number : " + selectedIndex);
        packageSharedPreferences.edit().putInt(String.valueOf(appWidgetID), selectedIndex).apply();
        Log.d(TAG, "saveAsTimerToShow: app widget id is : " + appWidgetID);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
        setResult(RESULT_OK, resultValue);

        callUpdateWidget();// To retrieve saved index from shared preferences
        finish();
    }

    private void callUpdateWidget() {
        Intent intent = new Intent(this, CountDownWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] widgetIDs = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CountDownWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs);
        sendBroadcast(intent);
    }

    private void initializeToolbar() {
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.widget_configuration_activity));
        setSupportActionBar(toolbar);

    }

    private void initRecords() {
        packageSharedPreferences = getSharedPreferences("com.pfoss.countdownlivewallpaper", MODE_PRIVATE);
        records = TimerViewModel.fetchRecords(packageSharedPreferences);
    }
}

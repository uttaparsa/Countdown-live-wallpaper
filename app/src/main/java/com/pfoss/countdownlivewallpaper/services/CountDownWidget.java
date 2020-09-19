package com.pfoss.countdownlivewallpaper.services;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.activities.MainActivity;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.themes.WidgetStyles;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;
import com.pfoss.countdownlivewallpaper.utils.UnitType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CountDownWidget extends AppWidgetProvider {
    private boolean isPersian = Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());

    private static final String ACTION_CLICK = "ACTION_CLICK";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) { //called every hour

        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                CountDownWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            Log.d(TAG, "onUpdate: app widget id is : " + widgetId);

            TimerRecord countDownToDisplay = getCurrent(context, widgetId);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_small);
            AtomicInteger timeDifferenceInSeconds = null;//using atomic integer to pass values by reference
            WidgetStyles widgetStyles = new WidgetStyles();

            UnitType[] unitTypes  = {UnitType.YEAR , UnitType.MONTH , UnitType.DAY , UnitType.HOUR};

            try {
                timeDifferenceInSeconds = new AtomicInteger(Math.abs(countDownToDisplay.getTimeDifference(Calendar.getInstance().getTime())));
                remoteViews.removeAllViews(R.id.numbersLinearLayout);
                remoteViews.removeAllViews(R.id.unitsLinearLayout);
                for (UnitType unitType : unitTypes) {
                    if (timeDifferenceInSeconds.get() >= unitType.getUnitInSecond()) {
                        int number = calculateNumber(timeDifferenceInSeconds, unitType);
                        boolean isPlural = (number != 1);

                        remoteViews.addView(R.id.numbersLinearLayout, widgetStyles.getStyledNumbersText(context, String.valueOf(number)));
                        remoteViews.addView(R.id.unitsLinearLayout, widgetStyles.getStyledUnitsText(context, context.getResources().getString(unitType.getStringResource()) + ((isPlural && !isPersian) ? "s" : "")));

                    }
                }
                remoteViews.addView(R.id.numbersLinearLayout ,widgetStyles.getStyledLabelText(context , countDownToDisplay.getLabel() , Color.WHITE) );
                remoteViews.addView(R.id.unitsLinearLayout, widgetStyles.getStyledUnitsText(context,  new String(new char[countDownToDisplay.getLabel().length()]).replace('\0', ' ')));

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            // Register an onClickListener
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);

            Log.i("WidgetExample", "displaying timer " + countDownToDisplay);

            remoteViews.setOnClickPendingIntent(R.id.numbersLinearLayout, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private int calculateNumber(AtomicInteger timeDifferenceInSeconds, UnitType unitType) {
        int unitValue = (timeDifferenceInSeconds.get() / unitType.getUnitInSecond());
        timeDifferenceInSeconds.set(timeDifferenceInSeconds.get() - (unitValue * unitType.getUnitInSecond()));
        return unitValue;
    }


    private TimerRecord getCurrent(Context context, int widgetId) {
        TimerRecord countDown = null;
        SharedPreferences timersSharedPreferences = context.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        ArrayList<TimerRecord> timerRecords = TimerViewModel.fetchRecords(timersSharedPreferences);

        int currentCountDownIndexInRecordsArray = timersSharedPreferences.getInt(String.valueOf(widgetId), -1);
        Log.d(TAG, "getCurrent: currentCountDownIndexInRecordsArray value is : " + currentCountDownIndexInRecordsArray);

        try {
            countDown = timerRecords.get(currentCountDownIndexInRecordsArray);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.i(TAG, "getCurrent: index was not valid");
        }
        return countDown;

    }
}

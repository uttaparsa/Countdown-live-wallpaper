package com.pfoss.countdownlivewallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import androidx.preference.PreferenceManager;

import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.themes.TextStyle;
import com.pfoss.countdownlivewallpaper.utils.Gradient;
import com.pfoss.countdownlivewallpaper.utils.MyLinearGradient;
import com.pfoss.countdownlivewallpaper.utils.PreferenceHelper;
import com.pfoss.countdownlivewallpaper.utils.UnitType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CountDownDrawer {
    private static final String TAG = "CountDownDrawer";
    private Canvas canvas;
    private Handler handler;
    private Context context;
    private TimerRecord currentRecord;
    private SurfaceHolder holder;
    private boolean visible;
    private Gradient gradient;
    private static int FPS = 1;
    private ArrayList<String> activeTimeUnitsArray;
    private Runnable runnable;
    private Paint unitsStyle;
    private Paint numbersStyle;
    private Paint labelStyle;
    private TextStyle textStyle;
    private int navbarSize;
    boolean isPersian = Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());

    int screenWidthInPixels;
    int screenHeightInPixels;

    public CountDownDrawer(Context context, TimerRecord currentRecord ) {
        this.context = context;
        activeTimeUnitsArray = PreferenceHelper.fetchPreferences(context);
        Log.i(TAG, "prefs " + activeTimeUnitsArray);
        convertDipToPixel();
        setCurrentRecord(currentRecord);
        initRunnable();

    }
    public CountDownDrawer(Context context) {
        this.context = context;
        activeTimeUnitsArray = PreferenceHelper.fetchPreferences(context);
        Log.i(TAG, "prefs " + activeTimeUnitsArray);
        convertDipToPixel();
        initRunnable();

    }
    public void setCurrentRecord( TimerRecord currentRecord ){
        this.currentRecord  = currentRecord;
        if (currentRecord.getBackgroundTheme() == TimerRecord.BackgroundTheme.GRADIENT) {
            initGradient();
            FPS = 10;
        }else{FPS = 1;}
        initStyles();
    }

    private void initStyles() {
        textStyle = new TextStyle(context, currentRecord);
        unitsStyle = textStyle.getUnitsStyle(unitsStyle);
        numbersStyle = textStyle.getNumbersStyle(numbersStyle);
        labelStyle = textStyle.getLabelStyle(labelStyle);
    }

    public void initRunnable( ) {
        runnable = new Runnable() {
            @Override
            public void run() {
                drawPage();
            }
        };

        Log.i(TAG, "runnable initialized");
    }

    public void setHolder(SurfaceHolder holder) {
        this.holder = holder;
    }

    private void initGradient() {
        this.gradient = new MyLinearGradient(GRADIENT_START_X_AXIS,
                GRADIENT_START_Y_AXIS,
                GRADIENT_END_X_AXIS,
                GRADIENT_END_Y_AXIS,
                Shader.TileMode.CLAMP);
        gradient.setAntiAlias(true);
        Log.i(TAG, "gradient initialized");
    }


    public void start() {
        this.visible = true;
        handler = new Handler();
        handler.post(runnable);
    }

    private void drawPage() {
        canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                // Draw on the Canvas!
                drawBackground();
                drawNumbers();
                drawLabel();
                Log.i(TAG, "updating text");

            }
        } catch (Exception e) {
            Log.e(TAG, "Error While using canvas");
            e.printStackTrace();
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
            Log.i(TAG ,"unlocked canvas in runnable");
        }
        // Schedule the next frame
        handler.removeCallbacks(runnable);
        if (visible) {
            handler.postDelayed(runnable, 1000 / FPS);
        }

    }

    private void drawLabel() {
        canvas.drawText(currentRecord.getLabel(), LABEL_X_AXIS, LABEL_Y_AXIS, labelStyle);
    }

    private void drawBackground() {
        Log.d(TAG, "current record theme: " + currentRecord.getBackgroundTheme());
        switch (currentRecord.getBackgroundTheme()) {
            case GRADIENT:
                drawGradientBackground(canvas);
                break;
            case PICTURE:
                drawImageBackground(canvas);
                break;
            case SOLID:
                drawSolidBackground(canvas);
                break;
        }
    }


    private void drawNumbers() {

        int timerDifferenceInSeconds = getTimeDifferenceInSeconds();
        Log.d(TAG, "difference in seconds is  : " + timerDifferenceInSeconds);
        for (int i = 0; i < activeTimeUnitsArray.size(); i++) {
            switch (activeTimeUnitsArray.get(i)) {//this is like a converter , a one with dirty code:/
                case "year":
                    int yearValue = 0;
                    if (timerDifferenceInSeconds > 31556952) {
                        yearValue = (timerDifferenceInSeconds / 31556952);
                        timerDifferenceInSeconds -= (yearValue * 31556952);
                        drawNumber(UnitType.YEAR, yearValue, i);
                    }
                    break;
                case "month":
                    int monthValue = 0;
                    if (timerDifferenceInSeconds > 2629746) {
                        monthValue = timerDifferenceInSeconds / 2629746;
                        timerDifferenceInSeconds -= (monthValue * 2629746);
                        drawNumber(UnitType.MONTH, monthValue, i);
                    }
                    break;
                case "day":
                    int dayValue = 0;
                    if (timerDifferenceInSeconds > 86400) {
                        dayValue = (timerDifferenceInSeconds / 86400);
                        timerDifferenceInSeconds -= (dayValue * 86400);
                        drawNumber(UnitType.DAY, dayValue, i);
                    }
                    break;
                case "hour":
                    int hourValue = 0;
                    if (timerDifferenceInSeconds > 3600) {
                        hourValue = (timerDifferenceInSeconds / 3600);
                        timerDifferenceInSeconds -= (hourValue * 3600);
                        drawNumber(UnitType.HOUR, hourValue, i);
                    }
                    break;
                case "minute":
                    int minuteValue = 0;
                    if (timerDifferenceInSeconds > 60) {
                        minuteValue = (timerDifferenceInSeconds / 60);
                        timerDifferenceInSeconds -= (minuteValue * 60);
                        drawNumber(UnitType.MINUTE, minuteValue, i);
                    }
                    break;
                case "second":
                    drawNumber(UnitType.SECOND, timerDifferenceInSeconds, i);
                    break;
                default:
                    Log.w(TAG, "This the switch case isn't working for a string");

            }
        }

    }

    private int getTimeDifferenceInSeconds() {
        Calendar todayRightNow = Calendar.getInstance();
        Date nowDate = new Date(todayRightNow.get(Calendar.YEAR), todayRightNow.get(Calendar.MONTH), todayRightNow.get(Calendar.DAY_OF_MONTH), todayRightNow.get(Calendar.HOUR_OF_DAY), todayRightNow.get(Calendar.MINUTE), todayRightNow.get(Calendar.SECOND));
        long timerDifferenceInMillis = Math.abs(nowDate.getTime() - currentRecord.getDate().getTime());

        return (int) (timerDifferenceInMillis / 1000);
    }


    private void drawNumber(UnitType unitName, int numberValue, int numberIndex) {
        int numberYAxis = FIRST_UNIT_Y_AXIS + numberIndex * (SPACE_BETWEEN_UNITS  + (int)((6-activeTimeUnitsArray.size())*SPACE_BETWEEN_UNITS*0.10));
        numbersStyle = textStyle.getNumbersStyle(numbersStyle);
        Log.d(TAG , " space size is : "+(int)((6-activeTimeUnitsArray.size())*SPACE_BETWEEN_UNITS*0.55));
        numbersStyle.setTextSize((int)((numbersStyle.getTextSize()*(activeTimeUnitsArray.size()-numberIndex-1))*0.25) + numbersStyle.getTextSize() );
        canvas.drawText(String.valueOf(numberValue),  NUMBERS_X_AXIS, numberYAxis, numbersStyle);

        if (numberValue == 1 || isPersian) {
            drawSingularUnitName(unitName, numberYAxis);
        } else {
            drawPluralUnitName(unitName, numberYAxis);
        }
    }

    private void drawSingularUnitName(UnitType unitName, int Yaxis) {
        canvas.drawText(context.getResources().getString(unitName.getStringResource()), UNITS_X_AXIS, Yaxis, unitsStyle);
    }

    private void drawPluralUnitName(UnitType unitName, int Yaxis) {
        canvas.drawText(context.getResources().getString(unitName.getStringResource()) + "s", UNITS_X_AXIS, Yaxis, unitsStyle);
    }

    private void drawSolidBackground(Canvas canvas) {
        canvas.drawColor(currentRecord.getBackGroundColor());
        Log.v(TAG, "Drawing solid background");
    }

    private void drawGradientBackground(Canvas canvas) {
        Log.v(TAG, "Drawing gradient background");
        canvas.drawRect(new RectF(0, 0, GRADIENT_END_X_AXIS, GRADIENT_END_Y_AXIS), gradient);
        updateGradient(gradient);

    }

    private void updateGradient(Gradient linearGradient) {//TODO: DOES NOT GO THROUGH ALL COLORS
            MyLinearGradient myLinearGradient = ((MyLinearGradient) linearGradient);
            int changeValue = 1;
            Log.i(TAG, "gradient color is : " + myLinearGradient.getStartColor());
            myLinearGradient.setStartColor(myLinearGradient.getStartColor() + changeValue);
            myLinearGradient.setEndColor(myLinearGradient.getEndColor() - changeValue );
            myLinearGradient.setAntiAlias(true);

    }

    private void drawImageBackground(Canvas canvas) {



        Rect frameToDraw = new Rect(0, 0, screenWidthInPixels, screenHeightInPixels);
        RectF whereToDraw = new RectF(0, 0, screenWidthInPixels, screenHeightInPixels);

        canvas.drawBitmap(currentRecord.getBitmap(), frameToDraw, whereToDraw, null);
        Log.i(TAG, "Drawing image background");
    }


    int GRADIENT_START_X_AXIS;
    int GRADIENT_START_Y_AXIS;
    int GRADIENT_END_X_AXIS;
    int GRADIENT_END_Y_AXIS;

    int SPACE_BETWEEN_UNITS;
    int FIRST_UNIT_Y_AXIS;
    int NUMBERS_X_AXIS;
    int UNITS_X_AXIS;

    int LABEL_X_AXIS;
    int LABEL_Y_AXIS;
    int LABEL_SPACE_TO_FIRST_NUMBER;

    private void convertDipToPixel() {//TODO: use point objects to organize values
        this.navbarSize = PreferenceManager.getDefaultSharedPreferences(context).getInt("navbar_height", 10);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Log.d("HEIGHT", "current height is " + navbarSize);
        Log.i("DRAWER-CONSTRUCTOR", "CURRENT RECORD: " + this.currentRecord);
        screenWidthInPixels = metrics.widthPixels;
        screenHeightInPixels = metrics.heightPixels + navbarSize;

//        SCREEN_HEIGHT_IN_DIP = dipToPixel(context.getResources().getInteger(R.integer.screen_height_in_dip));
//        SCREEN_WIDTH_IN_DIP = dipToPixel(context.getResources().getInteger(R.integer.screen_width_in_dip));
//
//        UNITS_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.units_x_axis_in_dip));
//        NUMBERS_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.numbers_x_axis_in_dip));
//        SPACE_BETWEEN_UNITS = dipToPixel(context.getResources().getInteger(R.integer.space_between_units_in_dip));
//        FIRST_UNIT_Y_AXIS = SCREEN_HEIGHT_IN_DIP / 2 - (((activeTimeUnitsArray.size() - 1) * SPACE_BETWEEN_UNITS) / 2);
//
//        LABEL_SPACE_TO_FIRST_NUMBER = dipToPixel(context.getResources().getInteger(R.integer.label_space_to_first_number_in_dip));
//        LABEL_Y_AXIS = FIRST_UNIT_Y_AXIS - LABEL_SPACE_TO_FIRST_NUMBER;
//        LABEL_X_AXIS = SCREEN_WIDTH_IN_DIP / 2;
//
//        GRADIENT_START_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.gradient_start_x_axis));
//        GRADIENT_START_Y_AXIS = dipToPixel(context.getResources().getInteger(R.integer.gradient_start_y_axis));
//        GRADIENT_END_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.gradient_end_x_axis_in_dip));
//        GRADIENT_END_Y_AXIS = dipToPixel(context.getResources().getInteger(R.integer.gradient_end_y_axis_in_dip));
//                SCREEN_HEIGHT_IN_DIP = dipToPixel(context.getResources().getInteger(R.integer.screen_height_in_dip));
//        SCREEN_WIDTH_IN_DIP = dipToPixel(context.getResources().getInteger(R.integer.screen_width_in_dip));




        UNITS_X_AXIS = screenWidthInPixels /2;
        NUMBERS_X_AXIS = screenWidthInPixels /2;
        SPACE_BETWEEN_UNITS = dipToPixel(context.getResources().getInteger(R.integer.space_between_units_in_dip));
        FIRST_UNIT_Y_AXIS = screenHeightInPixels / 2 - (((activeTimeUnitsArray.size() - 1) * SPACE_BETWEEN_UNITS) / 2);

        LABEL_SPACE_TO_FIRST_NUMBER = dipToPixel(context.getResources().getInteger(R.integer.label_space_to_first_number_in_dip));
        LABEL_Y_AXIS = FIRST_UNIT_Y_AXIS - LABEL_SPACE_TO_FIRST_NUMBER;
        LABEL_X_AXIS = screenWidthInPixels / 2;

        GRADIENT_START_X_AXIS = 0;
        GRADIENT_START_Y_AXIS = 0;
        GRADIENT_END_X_AXIS = screenWidthInPixels;
        GRADIENT_END_Y_AXIS = screenHeightInPixels;


    }


    public static int getFPS() {
        return FPS;
    }



    public void stop() {
        this.visible = false;
        handler.removeCallbacksAndMessages(null);
        Log.i(TAG, "called stop function");
    }


    public int dipToPixel(int dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, context.getResources().getDisplayMetrics());
    }
}


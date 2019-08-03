package com.pfoss.countdownlivewallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.themes.TextStyle;
import com.pfoss.countdownlivewallpaper.utils.Gradient;
import com.pfoss.countdownlivewallpaper.utils.MyLinearGradient;
import com.pfoss.countdownlivewallpaper.utils.PreferenceHelper;
import com.pfoss.countdownlivewallpaper.utils.UnitType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CountDownDrawer {

    private Canvas canvas;
    private Handler handler;
    private Context context;
    private TimerRecord currentRecord;
    private SurfaceHolder surfaceHolder;
    private boolean visible;
    private Gradient gradient;
    private static final int FPS = 1;
    private ArrayList<String> activeTimeUnitsArray;
    private Runnable runnable;
    private Paint unitsStyle;
    private Paint numbersStyle;
    private Paint labelStyle;
    private TextStyle textStyle;

    public CountDownDrawer(Handler handler, Context context, TimerRecord currentRecord) {
        this.handler = handler;
        this.context = context;
        this.currentRecord = currentRecord;
        Log.i("DRAWER-CONSTRUCTOR", "CURRENT RECORD: " + this.currentRecord);
        activeTimeUnitsArray = PreferenceHelper.fetchPreferences(context);
        Log.i("DRAWER-CONSTRUCTOR", "prefs " + activeTimeUnitsArray);
        convertDipToPixel();
        if (currentRecord.getBackgroundTheme() == TimerRecord.BackgroundTheme.GRADIENT)
            initGradient();

        initStyles();


    }
    private void initStyles(){
        textStyle = new TextStyle(context , currentRecord);
        unitsStyle = textStyle.getUnitsStyle(unitsStyle);
        numbersStyle = textStyle.getNumbersStyle(numbersStyle);
        labelStyle = textStyle.getLabelStyle(labelStyle);
    }

    public void initRunnable(final SurfaceHolder holder, final boolean visible) {
        this.surfaceHolder = holder;
        runnable = new Runnable() {
            @Override
            public void run() {
                drawPage(surfaceHolder, visible);
            }
        };

        Log.i("DRAWER-INITRUNNABLE", "runnable initialized");
    }

    private void initGradient() {
        this.gradient = new MyLinearGradient(GRADIENT_START_X_AXIS,
                GRADIENT_START_Y_AXIS,
                GRADIENT_END_X_AXIS,
                GRADIENT_END_Y_AXIS,
                Shader.TileMode.CLAMP);
        Log.i("DRAWER-INITGRADIENT", "gradient initialized");
    }

    public void start() {
        handler.post(runnable);
    }

    private void drawPage(SurfaceHolder holder, boolean visible) {
        canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                // Draw on the Canvas!
                drawBackground();
                drawNumbers();
                drawLabel();
                Log.i("DRAWER-DRAWPAGE", "draw text finished");
            }
        } catch (Exception e) {
            Log.e("DRAWER-DRAWPAGE", "Error While using canvas");
            e.printStackTrace();
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
            Log.i("RUNNABLE", "unlocked canvas in runnable");
        }
        // Schedule the next frame
        handler.removeCallbacks(runnable);
        if (visible) {//TODO: figure out what this if statement does

        }
        handler.postDelayed(runnable, 1000 / FPS);
    }

    private void drawLabel() {
        canvas.drawText(currentRecord.getLabel() , LABEL_X_AXIS , LABEL_Y_AXIS , labelStyle);
    }

    private void drawBackground() {
        Log.d("DRAWER-BACKGROUND", "current record theme: " + currentRecord.getBackgroundTheme());
        Log.d("DRAWER-BACKGROUND", "current record: " + currentRecord);
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
        Log.d("DRAWER", "difference in seconds is  : " + timerDifferenceInSeconds);
        for (int i = 0; i < activeTimeUnitsArray.size(); i++) {
            switch (activeTimeUnitsArray.get(i)) {//this is like a converter , a one with dirty code:/
                case "year":
                    int yearValue = 0;
                    if (timerDifferenceInSeconds > 31556952) {
                        yearValue = (timerDifferenceInSeconds / 31556952);
                        timerDifferenceInSeconds -= (yearValue * 31556952);
                        drawNumber(UnitType.YEAR , yearValue , i );
                    }
                    break;
                case "month":
                    int monthValue = 0;
                    if (timerDifferenceInSeconds > 2629746) {
                        monthValue = timerDifferenceInSeconds / 2629746;
                        timerDifferenceInSeconds -= (monthValue * 2629746);
                        drawNumber(UnitType.MONTH , monthValue , i );
                    }
                    break;
                case "day":
                    int dayValue = 0;
                    if (timerDifferenceInSeconds > 86400) {
                        dayValue = (timerDifferenceInSeconds / 86400);
                        timerDifferenceInSeconds -= (dayValue * 86400);
                        drawNumber(UnitType.DAY , dayValue , i );
                    }
                    break;
                case "hour":
                    int hourValue = 0;
                    if (timerDifferenceInSeconds > 3600) {
                        hourValue = (timerDifferenceInSeconds / 3600);
                        timerDifferenceInSeconds -= (hourValue * 3600);
                        drawNumber(UnitType.HOUR , hourValue , i);
                    }
                    break;
                case "minute":
                    int minuteValue = 0;
                    if (timerDifferenceInSeconds > 60) {
                        minuteValue = (timerDifferenceInSeconds / 60);
                        timerDifferenceInSeconds -= (minuteValue * 60);
                        drawNumber(UnitType.MINUTE , minuteValue , i);
                    }
                    break;
                case "second":
                    drawNumber(UnitType.SECOND , timerDifferenceInSeconds , i);
                    break;
                default:
                    Log.w("DRAWER", "This the switch case isn't working for a string");

            }
        }

    }

    private int getTimeDifferenceInSeconds() {
        Calendar todayRightNow = Calendar.getInstance();
        Date nowDate = new Date(todayRightNow.get(Calendar.YEAR), todayRightNow.get(Calendar.MONTH), todayRightNow.get(Calendar.DAY_OF_MONTH), todayRightNow.get(Calendar.HOUR_OF_DAY), todayRightNow.get(Calendar.MINUTE), todayRightNow.get(Calendar.SECOND));
        long timerDifferenceInMillis = Math.abs(nowDate.getTime() - currentRecord.getDate().getTime());

        return   (int) (timerDifferenceInMillis / 1000);
    }


    private void drawNumber(UnitType unitName, int numberValue , int numberIndex) {
        int numberYaxis = FIRST_UNIT_Y_AXIS + numberIndex*SPACE_BETWEEN_UNITS;
        canvas.drawText(String.valueOf(numberValue), NUMBERS_X_AXIS, numberYaxis, numbersStyle);
        if (numberValue == 1) {
            drawSingularUnitName(unitName , numberYaxis);
        } else {
            drawPluralUnitName(unitName , numberYaxis);
        }
    }

    private void drawSingularUnitName(UnitType unitName , int Yaxis) {
        canvas.drawText(unitName.toString(), UNITS_X_AXIS, Yaxis, unitsStyle);
    }
    private void drawPluralUnitName(UnitType unitName, int Yaxis) {
        canvas.drawText(unitName.toString()+"s", UNITS_X_AXIS, Yaxis, unitsStyle);
    }

    private void drawSolidBackground(Canvas canvas) {
        canvas.drawColor(currentRecord.getColor());
        Log.i("DRAWER", "Drawing gradient background");
    }

    private void drawGradientBackground(Canvas canvas) {
        updateGradient(gradient);
        canvas.drawRect(new RectF(0, 0, GRADIENT_END_X_AXIS, GRADIENT_END_Y_AXIS), gradient);
        Log.i("DRAWER", "Drawing gradient background");
    }

    private void updateGradient(Gradient linearGradient) {
        MyLinearGradient myLinearGradient = ((MyLinearGradient) linearGradient);
        int changeValue = (getFPS() > 30) ? 1 : 30 / getFPS();
        Log.i("DRAWER-COLORCHANGE", "gradient color is : " + myLinearGradient.getStartColor());
        myLinearGradient.setStartColor(myLinearGradient.getStartColor() + changeValue);
    }

    private void drawImageBackground(Canvas canvas) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        Rect frameToDraw = new Rect(0, 0, screenWidth, screenHeight);
        RectF whereToDraw = new RectF(0, 0, screenWidth, screenHeight);

        canvas.drawBitmap(currentRecord.getBitmap(), frameToDraw, whereToDraw, null);
        Log.i("DRAWER", "Drawing image background");
    }

    int SCREEN_HEIGHT_IN_DIP ;
    int SCREEN_WIDTH_IN_DIP ;

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

        SCREEN_HEIGHT_IN_DIP = dipToPixel(context.getResources().getInteger(R.integer.screen_height_in_dip));
        SCREEN_WIDTH_IN_DIP = dipToPixel(context.getResources().getInteger(R.integer.screen_width_in_dip));

        UNITS_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.units_x_axis));
        NUMBERS_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.numbers_x_axis));
        SPACE_BETWEEN_UNITS = dipToPixel(context.getResources().getInteger(R.integer.space_between_units));
        FIRST_UNIT_Y_AXIS = SCREEN_HEIGHT_IN_DIP/2-(((activeTimeUnitsArray.size() - 1) * SPACE_BETWEEN_UNITS) / 2);

        LABEL_SPACE_TO_FIRST_NUMBER = dipToPixel(context.getResources().getInteger(R.integer.label_space_to_first_number));
        LABEL_Y_AXIS = FIRST_UNIT_Y_AXIS - LABEL_SPACE_TO_FIRST_NUMBER;
        LABEL_X_AXIS = SCREEN_WIDTH_IN_DIP/2;

        GRADIENT_START_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.gradient_start_x_axis));
        GRADIENT_START_Y_AXIS = dipToPixel(context.getResources().getInteger(R.integer.gradient_start_y_axis));
        GRADIENT_END_X_AXIS = dipToPixel(context.getResources().getInteger(R.integer.gradient_end_x_axis));
        GRADIENT_END_Y_AXIS = dipToPixel(context.getResources().getInteger(R.integer.gradient_end_y_axis));

    }



    public static int getFPS() {
        return FPS;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        Log.i("DRAWER-STOP", "called stop function");
    }


    public int dipToPixel(int dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, context.getResources().getDisplayMetrics());
    }
}

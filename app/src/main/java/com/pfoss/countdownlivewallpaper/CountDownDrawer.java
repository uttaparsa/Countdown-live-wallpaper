package com.pfoss.countdownlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import androidx.preference.PreferenceManager;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.pfoss.countdownlivewallpaper.data.ActiveShowUnits;
import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;
import com.pfoss.countdownlivewallpaper.data.TimerRecord;
import com.pfoss.countdownlivewallpaper.services.CountDownWallpaperService;
import com.pfoss.countdownlivewallpaper.themes.TextStyle;
import com.pfoss.countdownlivewallpaper.utils.Gradient;
import com.pfoss.countdownlivewallpaper.utils.MyLinearGradient;
import com.pfoss.countdownlivewallpaper.utils.UnitType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;


public class CountDownDrawer {
    private static final String TAG = "CountDownDrawer";
    private static int FPS = 1;
    private Canvas canvas;
    private Handler handler;
    private Context context;
    private TimerRecord currentRecord;
    private SurfaceHolder holder;
    private boolean visible;
    private Paint gradient;
    private Runnable runnable;
    private Paint unitsStyle;
    private Paint numbersStyle;
    private Paint labelStyle;
    private Paint untilStyle;
    private Paint dateAndTimeStyle;
    private TextStyle textStyle;
    private boolean isPersian = Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());

    private int screenWidthInPixels;
    private int screenHeightInPixels;
    private CountDownWallpaperService.Engine wallEngine;

    public CountDownDrawer(Context context, TimerRecord currentRecord, CountDownWallpaperService.Engine wallEngine) {//Constructor for live wallpaper
        this.context = context;
        setCurrentRecord(currentRecord);
        this.wallEngine = wallEngine;
        this.initRunnable();
    }

    public CountDownDrawer(Context context) {//Constructor for fragment
        this.context = context;
        initRunnable();
    }

    public void setCurrentRecord(TimerRecord currentRecord) {
        this.currentRecord = currentRecord;
        Log.d(TAG, "setCurrentRecord: current record active unit s" + this.currentRecord.getActiveShowUnits().toString());
        convertDipToPixel();
        if (currentRecord.getBackgroundTheme() == BackgroundTheme.GRADIENT) initGradient();
        initStyles();
        initRunnable();

        Log.d(TAG, "setCurrentRecord: current record active unit s" + this.currentRecord.getActiveShowUnits().toString());

    }

    private void initStyles() {
        textStyle = new TextStyle(context, currentRecord);
        unitsStyle = textStyle.getUnitsStyle();
        numbersStyle = textStyle.getNumbersStyle();
        labelStyle = textStyle.getLabelStyle();
        dateAndTimeStyle = textStyle.getDateAndTimeTextStyle();
        untilStyle = textStyle.getUntilTextStyle();
    }

    public void initRunnable() {
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
                Shader.TileMode.MIRROR);

        Log.i(TAG, "gradient initialized");

    }


    public void start() {
        Log.d(TAG, "start: active units : " + currentRecord.getActiveShowUnits().toString());
        this.visible = true;
        handler = new Handler();
        handler.post(runnable);

    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private void drawPage() {
        if (this.wallEngine != null) {
            holder = wallEngine.getSurfaceHolder();
        }
        canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                // Draw on the Canvas!
                drawBackground();
                drawNumbers();
                drawLabel();
                drawDate();
                drawUntil();
                drawTime();
                Log.i(TAG, "updating text");

            }
        } catch (Exception e) {
            Log.e(TAG, "Error While using canvas");
            e.printStackTrace();
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
            Log.i(TAG, "unlocked canvas in runnable");
        }
        // Schedule the next frame
        handler.removeCallbacks(runnable);
        if (visible) {
            handler.postDelayed(runnable, 1000 / FPS);
        }

    }


    private void drawUntil() {
        String textToShow = (datePassed) ? context.getResources().getString(R.string.since) : context.getResources().getString(R.string.until);

        canvas.drawText(textToShow, UNITS_X_AXIS, UNTIL_Y_AXIS, untilStyle);
    }

    private void drawDate() {

        PersianCalendar persianCalendar = new PersianCalendar(currentRecord.getDateInstance().getTime());
        String date = (isPersian) ? persianCalendar.getPersianShortDate() : DateFormat.getDateInstance().format(currentRecord.getDateInstance());
        canvas.drawText(date, DATE_X_AXIS, DATE_Y_AXIS, dateAndTimeStyle);
    }

    private void drawTime() {
        String time = DateFormat.getTimeInstance().format(currentRecord.getDateInstance());
        if (isPersian) time = removeSeconds(time);
        canvas.drawText(time, CLOCK_X_AXIS, CLOCK_Y_AXIS, dateAndTimeStyle);
    }

    private String removeSeconds(String time) {
        return time.substring(0, time.length() - 3);
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
        int timeDifference = currentRecord.getTimeDifference(Calendar.getInstance().getTime());
        datePassed = timeDifference >= 0;
        AtomicInteger timeDifferenceInSeconds = new AtomicInteger(Math.abs(timeDifference));//using atomic integer to pass values by reference
        Log.d(TAG, "difference in seconds is  : " + timeDifferenceInSeconds);
        Log.d(TAG, "drawNumbers: active units : " + currentRecord.getActiveShowUnits().toString());


        int spaceIndex = 0;
        if (currentRecord.getActiveShowUnits().yearIsActive()) {
            spaceIndex = calculateAndDraw(UnitType.YEAR, timeDifferenceInSeconds, spaceIndex);
        }

        if (currentRecord.getActiveShowUnits().monthIsActive()) {
            spaceIndex = calculateAndDraw(UnitType.MONTH, timeDifferenceInSeconds, spaceIndex);
        }

        if (currentRecord.getActiveShowUnits().dayIsActive()) {
            spaceIndex = calculateAndDraw(UnitType.DAY, timeDifferenceInSeconds, spaceIndex);
        }

        if (currentRecord.getActiveShowUnits().hourIsActive()) {
            spaceIndex = calculateAndDraw(UnitType.HOUR, timeDifferenceInSeconds, spaceIndex);
        }

        if (currentRecord.getActiveShowUnits().minuteIsActive()) {
            spaceIndex = calculateAndDraw(UnitType.MINUTE, timeDifferenceInSeconds, spaceIndex);
        }

        if (currentRecord.getActiveShowUnits().secondIsActive()) {
            drawNumber(UnitType.SECOND, timeDifferenceInSeconds.get(), spaceIndex);
        }


    }

    private int calculateAndDraw(UnitType unitType, AtomicInteger timeDifferenceInSeconds, int spaceIndex) {
        int unitValue;

        if (timeDifferenceInSeconds.get() > unitType.getUnitInSecond()) {
            unitValue = (timeDifferenceInSeconds.get() / unitType.getUnitInSecond());
            timeDifferenceInSeconds.set(timeDifferenceInSeconds.get() - (unitValue * unitType.getUnitInSecond()));
            drawNumber(unitType, unitValue, spaceIndex);
            spaceIndex++;
        }
        return spaceIndex;
    }

    private boolean datePassed;

    private int getTimeDifferenceInSeconds() {
        Calendar todayRightNow = Calendar.getInstance();

        Date nowDate = todayRightNow.getTime();
        Log.d("tagdate", "getTimeDifferenceInSeconds: today's date : " + nowDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        long timeDifferenceInMillis = 0;
        try {

            timeDifferenceInMillis = nowDate.getTime() - sdf.parse(currentRecord.getDate()).getTime();
            datePassed = timeDifferenceInMillis >= 0;

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        timeDifferenceInMillis = Math.abs(timeDifferenceInMillis);

        return (int) (timeDifferenceInMillis / 1000);
    }


    private void drawNumber(UnitType unitName, int numberValue, int numberIndex) {
        int numberYAxis = FIRST_UNIT_Y_AXIS + numberIndex * (SPACE_BETWEEN_UNITS + (int) ((ActiveShowUnits.getTotalUnitsCount() - currentRecord.getActiveShowUnits().count()) * SPACE_BETWEEN_UNITS * 0.10));
        numbersStyle = textStyle.getNumbersStyle();
        Log.d(TAG, " space size is : " + (int) ((ActiveShowUnits.getTotalUnitsCount() - currentRecord.getActiveShowUnits().count()) * SPACE_BETWEEN_UNITS * 0.55));
        numbersStyle.setTextSize((int) ((numbersStyle.getTextSize() * (currentRecord.getActiveShowUnits().count() - numberIndex - 1)) * 0.25) + numbersStyle.getTextSize());
        canvas.drawText(String.valueOf(numberValue), NUMBERS_X_AXIS, numberYAxis, numbersStyle);

        if (numberValue == 1 || isPersian) {
            drawSingularUnitName(unitName, numberYAxis);
        } else {
            drawPluralUnitName(unitName, numberYAxis);
        }
    }

    private void drawSingularUnitName(UnitType unitName, int yAxis) {
        canvas.drawText(context.getResources().getString(unitName.getStringResource()), UNITS_X_AXIS, yAxis, unitsStyle);
    }

    private void drawPluralUnitName(UnitType unitName, int yAxis) {
        canvas.drawText(context.getResources().getString(unitName.getStringResource()) + "s", UNITS_X_AXIS, yAxis, unitsStyle);
    }

    private void drawSolidBackground(Canvas canvas) {
        canvas.drawColor(currentRecord.getBackGroundColor());
        Log.v(TAG, "Drawing solid background");
    }

    private void drawGradientBackground(Canvas canvas) {
        Log.v(TAG, "Drawing gradient background");
        canvas.drawPaint(gradient);
        updateGradient((MyLinearGradient) gradient);


    }

    private void updateGradient(MyLinearGradient linearGradient) {
        int changeValue = 1;
        Log.i(TAG, "gradient color is : " + linearGradient.getStartColor());
        linearGradient.setStartColor(linearGradient.getStartColor() + changeValue);
        linearGradient.setEndColor(linearGradient.getEndColor() + changeValue);
        linearGradient.setAntiAlias(true);
        linearGradient.setDither(true);

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

    int DATE_X_AXIS;
    int DATE_Y_AXIS;

    int CLOCK_X_AXIS;
    int CLOCK_Y_AXIS;

    int UNTIL_X_AXIS;
    int UNTIL_Y_AXIS;

    int SPACE_BETWEEN_DATE_AND_CLOCK;
    int SPACE_BETWEEN_NUMBERS_AND_DATE;

    private void convertDipToPixel() {//TODO: use point objects to organize values
        int navbarSize = PreferenceManager.getDefaultSharedPreferences(context).getInt("navbar_height", 10);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Log.d("HEIGHT", "current height is " + navbarSize);
        Log.i("DRAWER-CONSTRUCTOR", "CURRENT RECORD: " + this.currentRecord);
        screenWidthInPixels = metrics.widthPixels;
        screenHeightInPixels = metrics.heightPixels + navbarSize;


        UNITS_X_AXIS = screenWidthInPixels / 2;
        NUMBERS_X_AXIS = screenWidthInPixels / 2;
        SPACE_BETWEEN_UNITS = dipToPixel(context.getResources().getInteger(R.integer.space_between_units_in_dip));
        FIRST_UNIT_Y_AXIS = screenHeightInPixels / 2 - (((currentRecord.getActiveShowUnits().count() - 1) * SPACE_BETWEEN_UNITS) / 2);

        LABEL_SPACE_TO_FIRST_NUMBER = dipToPixel(context.getResources().getInteger(R.integer.label_space_to_first_number_in_dip));
        LABEL_Y_AXIS = FIRST_UNIT_Y_AXIS - LABEL_SPACE_TO_FIRST_NUMBER;
        LABEL_X_AXIS = screenWidthInPixels / 2;

        SPACE_BETWEEN_NUMBERS_AND_DATE = context.getResources().getInteger(R.integer.space_between_numbers_and_date);
        DATE_X_AXIS = screenWidthInPixels / 2;
        DATE_Y_AXIS = FIRST_UNIT_Y_AXIS + currentRecord.getActiveShowUnits().count() * SPACE_BETWEEN_UNITS + SPACE_BETWEEN_NUMBERS_AND_DATE;

        SPACE_BETWEEN_DATE_AND_CLOCK = dipToPixel(context.getResources().getInteger(R.integer.space_between_date_and_clock));

        CLOCK_X_AXIS = screenWidthInPixels / 2;
        CLOCK_Y_AXIS = DATE_Y_AXIS + SPACE_BETWEEN_DATE_AND_CLOCK;

        UNTIL_X_AXIS = screenWidthInPixels / 2;
        UNTIL_Y_AXIS = (DATE_Y_AXIS + CLOCK_Y_AXIS) / 2;

        Log.d(TAG, "convertDipToPixel: screenHeightInPixels : " + screenHeightInPixels);
        GRADIENT_START_X_AXIS = 0;
        GRADIENT_START_Y_AXIS = 0;
        GRADIENT_END_X_AXIS = 0;
        GRADIENT_END_Y_AXIS = screenHeightInPixels;


    }


    public void stop() {
        this.visible = false;
        handler.removeCallbacksAndMessages(null);
        Log.i(TAG, "called stop function");
    }


    private int dipToPixel(int dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, context.getResources().getDisplayMetrics());
    }
}


package com.pfoss.countdownlivewallpaper.data;

import android.content.ContextWrapper;
import android.graphics.Bitmap;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.pfoss.countdownlivewallpaper.data.timerbuilderexception.BackgroundNotSetException;
import com.pfoss.countdownlivewallpaper.data.timerbuilderexception.DateNotSetException;
import com.pfoss.countdownlivewallpaper.utils.RuntimeTools;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimerBuilder {
    private int dayFinal = 0;
    private int monthFinal = 0;
    private int yearFinal = 0;
    private int hourFinal = 0;
    private int minuteFinal = 0;
    private String label;
    private BackgroundTheme backgroundTheme;
    private int backGroundColor;
    private Bitmap userSelectedBitmap;

    private String getFormattedPersianDateTime() {
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.setPersianDate(yearFinal, monthFinal, dayFinal);

        Date time = persianCalendar.getTime();
        time.setHours(hourFinal);
        time.setMinutes(minuteFinal);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(time);

    }

    private String getFormattedGregorianDateTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.YEAR, yearFinal);
        cal.set(Calendar.MONTH, monthFinal);
        cal.set(Calendar.DAY_OF_MONTH, dayFinal);
        cal.set(Calendar.HOUR_OF_DAY, hourFinal);
        cal.set(Calendar.MINUTE, minuteFinal);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());

    }


    public TimerRecord build() throws DateNotSetException, BackgroundNotSetException {
        if (backgroundTheme == null)
            throw new BackgroundNotSetException();
        if (minuteFinal == 0)
            throw new BackgroundNotSetException();

        TimerRecord timerRecord = new TimerRecord();
        if (RuntimeTools.isPersian()) {
            timerRecord.setDate(getFormattedPersianDateTime());
        } else {
            timerRecord.setDate(getFormattedGregorianDateTime());
        }
        timerRecord.setBackgroundTheme(this.backgroundTheme);
        timerRecord.setBackGroundColor(this.backGroundColor);
        timerRecord.setPriorToShow(true);
        timerRecord.setLabel(this.getLabel());

        return timerRecord;
    }

    public void setDayFinal(int dayFinal) {
        this.dayFinal = dayFinal;
    }

    public void setHourFinal(int hourFinal) {
        this.hourFinal = hourFinal;
    }

    public void setMonthFinal(int monthFinal) {
        this.monthFinal = monthFinal;
    }

    public void setYearFinal(int yearFinal) {
        this.yearFinal = yearFinal;
    }

    public void setMinuteFinal(int minuteFinal) {
        this.minuteFinal = minuteFinal;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getDayFinal() {
        return dayFinal;
    }

    public int getMonthFinal() {
        return monthFinal;
    }

    public int getYearFinal() {
        return yearFinal;
    }

    public int getHourFinal() {
        return hourFinal;
    }

    public int getMinuteFinal() {
        return minuteFinal;
    }


    public BackgroundTheme getBackgroundTheme() {
        return backgroundTheme;
    }

    public void setBackgroundTheme(BackgroundTheme backgroundTheme) {
        this.backgroundTheme = backgroundTheme;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
    }
}

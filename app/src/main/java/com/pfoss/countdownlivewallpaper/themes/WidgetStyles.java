package com.pfoss.countdownlivewallpaper.themes;

import android.content.Context;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.pfoss.countdownlivewallpaper.R;

public class WidgetStyles extends Style {

    public RemoteViews getStyledNumbersText(Context context, String text) {
        RemoteViews numberTextView = new RemoteViews(context.getPackageName(), R.layout.numbers_textview_layout);
        numberTextView.setTextViewText(R.id.numberTextView, String.valueOf(text));
        numberTextView.setTextColor(R.id.numberTextView, Color.WHITE);
        return numberTextView;
    }

    public RemoteViews getStyledUnitsText(Context context, String text) {
        RemoteViews numberTextView = new RemoteViews(context.getPackageName(), R.layout.units_view_layout);
        numberTextView.setTextViewText(R.id.unitsTextView, String.valueOf(text));
        numberTextView.setTextColor(R.id.unitsTextView, Color.WHITE);
        return numberTextView;
    }
    public RemoteViews getStyledLabelText(Context context, String text , int textColor) {
        RemoteViews numberTextView = new RemoteViews(context.getPackageName(), R.layout.label_textview);
        numberTextView.setTextViewText(R.id.labelTextView, String.valueOf(text));
        numberTextView.setTextColor(R.id.labelTextView, textColor);
        return numberTextView;
    }
}
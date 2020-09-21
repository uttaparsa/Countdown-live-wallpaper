package com.pfoss.countdownlivewallpaper.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.services.BackgroundImagePicker;

import yuku.ambilwarna.AmbilWarnaDialog;

public class BackgroundSelectorDialog implements DialogInterface.OnClickListener, AmbilWarnaDialog.OnAmbilWarnaListener {
    private int selectedItem;
    private BackgroundImagePicker backgroundImagePicker;
    public static final int GRADIENT_BACKGROUND = 0;
    public static final int IMAGE_BACKGROUND = 1;
    public static final int SOLID_BACKGROUND = 2;
    private int colorSelectedByUser;

    Activity callerActivity;

    public int getColorSelectedByUser() {
        return colorSelectedByUser;
    }

    public BackgroundSelectorDialog(Activity activity) {
        this.callerActivity = activity;
        backgroundImagePicker = new BackgroundImagePicker(callerActivity);
    }

    public void show(View view, DialogInterface.OnClickListener finishDialogListener) {
        String[] themeChoiceItems = view.getContext().getResources().getStringArray(R.array._choose_timer_theme_dialog_multi_choice_array);
        selectedItem = 0;

        new AlertDialog.Builder(view.getContext(), R.style.CustomDialogTheme)
                .setTitle(view.getContext().getResources().getString(R.string.choose_theme_dialog_title))
                .setSingleChoiceItems(themeChoiceItems, selectedItem, this)
                .setPositiveButton(view.getContext().getResources().getString(R.string.ok), finishDialogListener)
                .setNegativeButton(view.getContext().getResources().getString(R.string.cancel), null)
                .show();
    }

    public BackgroundImagePicker getBackgroundImagePicker() {
        return backgroundImagePicker;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
        selectedItem = selectedIndex;
        switch (selectedIndex) {
            case GRADIENT_BACKGROUND:
                // do nothing
                break;
            case IMAGE_BACKGROUND:
                backgroundImagePicker.startCropImageActivity();
                break;
            case SOLID_BACKGROUND:
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(callerActivity, R.attr.colorPrimary, this);
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        // color is the color selected by the user.
        colorSelectedByUser = color;
    }

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {
        // cancel was selected by the user
        colorSelectedByUser = Color.WHITE;// default color is white
    }

    public int getSelectedItem() {
        return selectedItem;
    }
}

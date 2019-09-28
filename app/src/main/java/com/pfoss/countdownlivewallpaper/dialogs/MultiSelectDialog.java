package com.pfoss.countdownlivewallpaper.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.data.BackgroundTheme;

public class MultiSelectDialog extends DialogFragment {
    private static final String TAG = "MultiSelectDialog";
    private DialogInterface.OnClickListener onOkListener;
    private DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener;
    public MultiSelectDialog(DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener , DialogInterface.OnClickListener onOkListener){
        super();
        this.multiChoiceClickListener = multiChoiceClickListener;
        this.onOkListener = onOkListener;
    }
    boolean[] checkedItems ;
    public void setCheckedItems(boolean[] checkedItems){
        this.checkedItems = checkedItems;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] items = getContext().getResources().getStringArray(R.array.units_prefs_entries);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle(getContext().getResources().getString(R.string.unit_select_title))//TODO:add negative and positive button
                .setMultiChoiceItems(items, checkedItems,multiChoiceClickListener)
                .setPositiveButton(getResources().getString(R.string.ok),onOkListener)
                .setNegativeButton(getResources().getString(R.string.cancel), null);

        return builder.create();
    }
}

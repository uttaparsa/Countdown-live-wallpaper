package com.pfoss.countdownlivewallpaper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

public class RuntimeTools {

    public static boolean isFirstRun(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("firstrun", true);
    }

    public static void markFirstRun(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("com.pfoss.countdownlivewallpaper", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("firstrun", false).apply();
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static boolean isPersian() {
        return Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());
    }
}

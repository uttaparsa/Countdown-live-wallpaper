package com.pfoss.countdownlivewallpaper.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.pfoss.countdownlivewallpaper.data.TimerRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;

public class BitmapHelper {
    public static Bitmap scaleImageCenteredCrop(Bitmap sourceBitmap, int targetHeightInPixels, int targetWidthInPixels) {

        double targetRatio = (double) targetHeightInPixels / (double) targetWidthInPixels;


        double pictureRatio = (double) sourceBitmap.getHeight() / (double) sourceBitmap.getWidth();
        Log.i("SCALAR", "picture ratio height and width are :" + sourceBitmap.getHeight() + " Width : " + sourceBitmap.getWidth());
        Log.i("SCALAR", "target ratio height and width are :" + targetHeightInPixels + " Width : " + targetWidthInPixels);
        Log.i("SCALAR", "picture ratio :" + pictureRatio);
        Log.i("SCALAR", "targetRatio :" + targetRatio);


        Bitmap destinationBitmap;
        if (targetRatio < pictureRatio) {

            Log.i("SCALAR", "case 1");
            double someVal = (double) targetRatio * (double) targetWidthInPixels;
            destinationBitmap = Bitmap.createScaledBitmap(sourceBitmap, targetWidthInPixels, (int) someVal, false);
        } else if (targetRatio == pictureRatio) {


            if (sourceBitmap.getWidth() != targetWidthInPixels) {

                Log.i("SCALAR", "Inequality");
                destinationBitmap = Bitmap.createScaledBitmap(sourceBitmap, targetWidthInPixels, targetHeightInPixels, false);

            } else {
                Log.i("SCALAR", "Equality");
                destinationBitmap = sourceBitmap;
            }

        } else {

            Log.i("SCALAR", "case 2");
            double someVal = targetHeightInPixels / pictureRatio;
            destinationBitmap = Bitmap.createScaledBitmap(sourceBitmap, (int) someVal, targetHeightInPixels, false);
        }
        Log.i("SCALAR", "destinationBitmap width , height : " + destinationBitmap.getWidth() + " , " + destinationBitmap.getHeight());
        return destinationBitmap;
    }

    public static Bitmap decodeUriAsBitmap(Uri uri, Activity caller) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(caller.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromFile(TimerRecord timerRecord,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        File f = new File(timerRecord.getImagePath(), timerRecord.getId() + ".png");
        BitmapFactory.decodeFile(f.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(f.getAbsolutePath(), options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}

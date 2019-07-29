package Helpers;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapHelper {
    public static Bitmap scaleImageCenteredCrop(Bitmap sourceBitmap, int targetHeight, int targetWidth) {

        double targetRatio = (double) targetHeight / (double) targetWidth;


        double pictureRatio = (double) sourceBitmap.getHeight() / (double) sourceBitmap.getWidth();
        Log.i("SCALER", "picture ratio height and width are :" + sourceBitmap.getHeight() + " Width : " + sourceBitmap.getWidth());
        Log.i("SCALER", "screen ratio height and width are :" + targetHeight + " Width : " + targetWidth);
        Log.i("SCALER", "pictureratio :" + pictureRatio);
        Log.i("SCALER", "screenratio :" + targetRatio);


        Bitmap destinationBitmap;
        if (targetRatio < pictureRatio) {

            Log.i("SCALER", "case 1");
            Double someVal = pictureRatio * targetWidth;
            destinationBitmap = Bitmap.createScaledBitmap(sourceBitmap, targetWidth, someVal.intValue(), false);
        } else if (targetRatio == pictureRatio) {


            if (sourceBitmap.getWidth() != targetWidth) {

                Log.i("SCALER", "Inequality");
                destinationBitmap = Bitmap.createScaledBitmap(sourceBitmap, targetWidth, targetHeight, false);

            } else {
                Log.i("SCALER", "Equality");
                destinationBitmap = sourceBitmap;
            }

        } else {

            Log.i("SCALER", "case 2");
            Double someVal = targetHeight / pictureRatio;
            destinationBitmap = Bitmap.createScaledBitmap(sourceBitmap, someVal.intValue(), targetHeight, false);
        }
        return destinationBitmap;
    }


}

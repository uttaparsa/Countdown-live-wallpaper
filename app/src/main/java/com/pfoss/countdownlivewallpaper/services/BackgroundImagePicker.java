package com.pfoss.countdownlivewallpaper.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.pfoss.countdownlivewallpaper.utils.RuntimeTools;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_OK;

public class BackgroundImagePicker {
    Activity mCallerActivity;
    private Uri tempUri = null;

    public BackgroundImagePicker(Activity activity) {
        mCallerActivity = activity;
    }

    @SuppressLint("NewApi")
    public void imagePickerListener(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(mCallerActivity, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(mCallerActivity, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                tempUri = imageUri;
                mCallerActivity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                tempUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public Uri imageFetch(int requestCode, int resultCode, Intent data) throws ImagePickerException {

        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            return tempUri = result.getUri();
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            throw new ImagePickerException();
        }

        throw new ImagePickerException();

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(mCallerActivity);
            } else {
                Toast.makeText(mCallerActivity, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (tempUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                this.startCropImageActivity();
            } else {
                Toast.makeText(mCallerActivity, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void startCropImageActivity() {
        DisplayMetrics metrics = mCallerActivity.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels + RuntimeTools.getNavigationBarHeight(mCallerActivity) + RuntimeTools.getStatusBarHeight(mCallerActivity);
        CropImage.activity(tempUri)
                .setFixAspectRatio(true)
                .setAspectRatio(screenWidth, screenHeight)
                .start(mCallerActivity);
    }


}

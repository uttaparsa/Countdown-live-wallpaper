package com.pfoss.countdownlivewallpaper.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pfoss.countdownlivewallpaper.R;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        initializeToolbar();
    }
    private void initializeToolbar() {
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.credits_page_title));
        setSupportActionBar(toolbar);

    }
}

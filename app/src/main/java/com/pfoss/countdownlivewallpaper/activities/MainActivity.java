package com.pfoss.countdownlivewallpaper.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.pfoss.countdownlivewallpaper.TimerListActivity;
import com.pfoss.countdownlivewallpaper.fragments.CountDownDisplayFragment;
import com.pfoss.countdownlivewallpaper.CountDownDrawer;
import com.pfoss.countdownlivewallpaper.services.CountDownWallpaperService;
import com.pfoss.countdownlivewallpaper.R;



public class MainActivity extends AppCompatActivity implements CountDownDisplayFragment.Callbacks {

    private CountDownDisplayFragment countDownDisplayFragment;
    private int FPS = CountDownDrawer.getFPS();
    private BoomMenuButton boomMenuButton;

    //change
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeToolbar();

        boomMenuButton = findViewById(R.id.boomMenuButton);
        buildHamButtons();

        countDownDisplayFragment = (CountDownDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.surfaceFragment);
        Log.i("MAIN", "creating main");


    }

    public void restartFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
    }

    private void buildHamButtons() {
        boomMenuButton.setButtonEnum(ButtonEnum.Ham);
        boomMenuButton.setPiecePlaceEnum(PiecePlaceEnum.HAM_2);
        boomMenuButton.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);

        for (int i = 0; i < boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
            if (i == 0) {// it's just the way boom works!
                HamButton.Builder builder = new HamButton.Builder().normalText(getResources().getString(R.string.add_new_entry)).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startActivity(new Intent(MainActivity.this, CreateWallpaperActivity.class));
                    }
                });
                boomMenuButton.addBuilder(builder);
            } else if (i == 1) {
                HamButton.Builder builder = new HamButton.Builder().normalText(getResources().getString(R.string.set_as_wallpaper)).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        if (countDownDisplayFragment.getTimerRecords().isEmpty()) {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.no_timer_yet), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(
                                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    new ComponentName(MainActivity.this, CountDownWallpaperService.class));
                            startActivity(intent);
                        }
                    }
                });
                boomMenuButton.addBuilder(builder);
            }


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MAIN", "resuming main");
    }


    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settingsMenuItem:
                Intent timerPreferences = new Intent(this, TimerSettingsActivity.class);
                startActivity(timerPreferences);
                break;
            case R.id.countDownListMenuItem:
                Intent i2 = new Intent(MainActivity.this, TimerListActivity.class);
                startActivity(i2);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                break;
            case R.id.deleteTimer:
                countDownDisplayFragment.deleteTimer();
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void deletePlease() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.surfaceFragment, new CountDownDisplayFragment())
                .commit();
    }



}




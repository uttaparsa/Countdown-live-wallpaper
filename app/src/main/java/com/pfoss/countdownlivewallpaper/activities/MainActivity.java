package com.pfoss.countdownlivewallpaper.activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.fragments.CountDownDisplayFragment;
import com.pfoss.countdownlivewallpaper.services.CountDownWallpaperService;
import com.pfoss.countdownlivewallpaper.utils.RuntimeTools;
import com.pfoss.countdownlivewallpaper.viewmodel.TimerViewModel;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public static final String TIMER_DUE = "timer_due";
    private CountDownDisplayFragment countDownDisplayFragment;
    private BoomMenuButton boomMenuButton;
    private Toolbar toolbar;
    boolean isPersian = Locale.getDefault().getLanguage().equals(new Locale("fa").getLanguage());

//    private static final boolean AUTO_HIDE = true;
//
//    /**
//     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
//     * user interaction before hiding the system UI.
//     */
//    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeToolbar();

        //save navigation bar height to memory , will be used in drawer
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("navbar_height", RuntimeTools.getNavigationBarHeight(this)).apply();

        boomMenuButton = findViewById(R.id.boomMenuButton);
        buildHamButtons();


        countDownDisplayFragment = (CountDownDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.surfaceFragment);
        checkBundle();//TODO: what does it mean?

        Log.i("MAIN", "creating main");
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.surfaceFragment);
        createNotificationChannel();

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        if (RuntimeTools.isFirstRun(this)) {

            showIntro();
            RuntimeTools.markFirstRun(this);
        }
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.


    }


    private void showIntro() {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(R.id.boomMenuButton, this))
                .setContentTitle(getResources().getString(R.string.main_intro_showcase_title))
                .setContentText(getResources().getString(R.string.main_intro_showcase))
                .hideOnTouchOutside()
                .build()
                .show();
    }

    private void checkBundle() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("MAIN", "nothing in bundle");
            return;

        }
        // get data via the key
        int timerToShowIndex = extras.getInt("timer_show_request");
        countDownDisplayFragment.drawTimer(timerToShowIndex);
        Log.d("MAIN", "running request");
    }

    private void buildHamButtons() {
        boomMenuButton.setButtonEnum(ButtonEnum.Ham);
        boomMenuButton.setPiecePlaceEnum(PiecePlaceEnum.HAM_2);
        boomMenuButton.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);

        for (int i = 0; i < boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
            Typeface persianTypeFace = ResourcesCompat.getFont(this, R.font.vazir_medium);
            Typeface latinTypeFace = ResourcesCompat.getFont(this, R.font.baumans);
            Typeface typeface;
            if (isPersian) {
                typeface = persianTypeFace;
            } else {
                typeface = latinTypeFace;
            }
            if (i == 0) {// it's just the way boom works!
                HamButton.Builder builder = new HamButton.Builder().normalText(getResources().getString(R.string.add_new_entry)).typeface(typeface).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startActivity(new Intent(MainActivity.this, CreateWallpaperActivity.class));
                    }
                });
                boomMenuButton.addBuilder(builder);
            } else if (i == 1) {
                HamButton.Builder builder = new HamButton.Builder().normalText(getResources().getString(R.string.set_as_wallpaper)).typeface(typeface).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        TimerViewModel timerViewModel = new TimerViewModel(getApplicationContext());
                        timerViewModel.fetchRecords();
                        if (timerViewModel.getTimerRecords().isEmpty()) {
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
        toolbar = findViewById(R.id.toolbar);
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
//            case R.id.settingsMenuItem:
//                Intent timerPreferences = new Intent(this, SettingsActivity.class);
//                startActivity(timerPreferences);
//                break;
            case R.id.countDownListMenuItem:
                Intent i2 = new Intent(MainActivity.this, TimerListActivity.class);
                startActivity(i2);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                break;
            case R.id.deleteTimer:
                countDownDisplayFragment.deleteTimer();
                break;
            case R.id.editCountDown:
                Intent countDownEditIntent = new Intent(this, EditCountDownActivity.class);
                try {
                    countDownDisplayFragment.getDrawer().stop();
                    startActivityForResult(countDownEditIntent, 10);
                } catch (NullPointerException ex) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.no_timer_yet), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ContactUs:
                Intent loadContactUS = new Intent(MainActivity.this, CreditsActivity.class);
                startActivity(loadContactUS);
            default:
                return false;
        }
        return false;
    }



    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);

    }



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(TIMER_DUE , name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}






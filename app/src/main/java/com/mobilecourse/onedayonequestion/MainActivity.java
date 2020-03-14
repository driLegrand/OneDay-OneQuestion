package com.mobilecourse.onedayonequestion;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.mobilecourse.onedayonequestion.fragment.Quizz;
import com.mobilecourse.onedayonequestion.fragment.oneDay_oneQuestionFragment;

import java.util.Calendar;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    //FOR FRAGMENTS
    // Declare fragment handled by Navigation Drawer
    private Fragment fragmentOne;
    private Fragment fragmentQuizz;

    //FOR DATAS
    //Identify each fragment with a number
    private static final int FRAGMENT_ONE = 0;
    private static final int FRAGMENT_QUIZZ = 1;

    private String origin;

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    private SharedPreferences prefs;

    private ShakeDetector shakeDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        //creation of the intent and of the alarm which will send this intent
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Configure all views

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        this.showFirstFragment();

        createNotificationChannel();

        //scheduling of the notification
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long currentTime = (System.currentTimeMillis() - c.getTimeInMillis());
        long time = prefs.getLong("time", 0) - currentTime;
        if (time > 0) {
            scheduleNotification(time);
        } else {
            scheduleNotification(time + 24 * 3600 * 1000);
        }

        //creation of the shake detector which will open parameters
        ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2f);
        this.shakeDetector = new ShakeDetector(options).start(this, new ShakeCallback() {
            @Override
            public void onShake() {
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(settings, 400);
            }
        });
    }

    //close the menu on back pressed
    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //destroy the shake detector
    @Override
    protected void onDestroy() {
        shakeDetector.destroy(getBaseContext());
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shakeDetector.isRunning()){
            shakeDetector.destroy(getBaseContext());
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_main_drawer_onequestion:
                this.showFragment(FRAGMENT_ONE);
                break;
            case R.id.activity_main_drawer_quizz:
                this.showFragment(FRAGMENT_QUIZZ);
                break;
            case R.id.activity_main_parameters:
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(settings, 400);
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    //Obtain the origin of the previous intent (when we come back from parameters
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 400) {
            if (resultCode == RESULT_OK) {
                origin = data.getStringExtra("fragment");
            }
        }
    }

    //if we come back from parameters, we display the main screen
    @Override
    public void onResume() {
        super.onResume();
        if (origin != null && origin.equals("settings")) {
            showFirstFragment();
        }
    }


    //Configure Toolbar
    private void configureToolBar() {
        this.toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    //Configure Drawer Layout
    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    //Configure NavigationView
    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void showFragment(int fragmentIdentifier) {
        switch (fragmentIdentifier) {
            case FRAGMENT_ONE:
                this.showOneFragment();
                break;
            case FRAGMENT_QUIZZ:
                this.showQuizzFragment();
                break;
            default:
                break;
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        //if the user don't have the good version, he will not have notifications, which not really important
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notif";
            String description = "notif_for_a_new_question";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notif", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    //Create each fragment page and show it
    private void showOneFragment() {
        this.fragmentOne = oneDay_oneQuestionFragment.newInstance();
        this.startTransactionFragment(this.fragmentOne);
    }

    private void showQuizzFragment() {
        if (this.fragmentQuizz == null) this.fragmentQuizz = Quizz.newInstance();
        this.startTransactionFragment(this.fragmentQuizz);
    }

    // Generic method that will replace and show a fragment inside the MainActivity Frame Layout
    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment).commit();
        }
    }

    // Show first fragment when activity is created and when we come back from paramters
    private void showFirstFragment() {
        // Show News Fragment
        this.showFragment(FRAGMENT_ONE);
        // Mark as selected the menu item corresponding to NewsFragment
        this.navigationView.setCheckedItem(R.id.activity_main_drawer_onequestion);
    }

    private void scheduleNotification(long delay) {
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delay, pendingIntent);
    }
}

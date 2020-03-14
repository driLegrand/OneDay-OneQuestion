package com.mobilecourse.onedayonequestion;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private TimePicker time_quizz;
    private Spinner spinner_difficulties;
    private Spinner spinner_categories;
    private Button save;
    private Button quit;

    private  String difficulty;
    private int category;
    private  long time;
    private String welcomeMessage;


    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;


    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        time_quizz = findViewById(R.id.preference_time_value);
        save = findViewById(R.id.save);
        quit = findViewById(R.id.quit);
        EditText welcomMessage_value = findViewById(R.id.editWelcomeMessage_value);

        //init the list in the spinners
        spinner_difficulties = (Spinner) findViewById(R.id.preference_difficulty_values);
        ArrayAdapter<CharSequence> adapter_difficulties = ArrayAdapter.createFromResource(this,
                R.array.difficulties_entries, android.R.layout.simple_spinner_item);
        adapter_difficulties.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_difficulties.setAdapter(adapter_difficulties);

        spinner_categories = (Spinner) findViewById(R.id.preference_category_values);
        ArrayAdapter<CharSequence> adapter_categories = ArrayAdapter.createFromResource(this,
                R.array.categories_entries, android.R.layout.simple_spinner_item);
        adapter_categories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_categories.setAdapter(adapter_categories);

        //Init the items selected in the spinners and the time picker
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //get the preferences
        difficulty = prefs.getString("difficulty","any");
        category = prefs.getInt("category",0);
        time = prefs.getLong("time",0);
        welcomeMessage = prefs.getString("welcomeMessage","");

        //set the parameters to the actual parameters
        welcomMessage_value.setText(welcomeMessage);
        spinner_categories.setSelection(category - 8);
        spinner_difficulties.setSelection(((ArrayAdapter)spinner_difficulties.getAdapter()).getPosition(difficulty));
        time_quizz.setHour((int) ((time / (1000*60*60)) % 24));
        time_quizz.setMinute((int) (time / (1000*60)) % 60);
        time_quizz.setIs24HourView(true);


        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //update the preferences
                time = time_quizz.getHour() * (3600 * 1000) + time_quizz.getMinute() * 60000;
                category = ((Integer) ((Spinner) findViewById(R.id.preference_category_values)).getSelectedItemPosition()) + 8;
                difficulty = ((String) ((Spinner) findViewById(R.id.preference_difficulty_values)).getSelectedItem());
                welcomeMessage = ((EditText) findViewById(R.id.editWelcomeMessage_value)).getText() + "";
                prefs.edit().putString("difficulty", difficulty).apply();
                prefs.edit().putInt("category",category).apply();
                prefs.edit().putLong("time",time).apply();
                prefs.edit().putString("welcomeMessage",welcomeMessage).apply();

                //when we change the time of the question, we reshedule the notification
                Intent data = new Intent();
                data.putExtra("fragment","settings");
                setResult(RESULT_OK,data);
                Calendar c  = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long currentTime = (System.currentTimeMillis() - c.getTimeInMillis());
                time = time - currentTime;
                if (time > 0 ){
                    scheduleNotification(time);
                }
                else{
                    scheduleNotification(time + 24 * 3600 * 1000);
                }

                //---close the activity---
                finish();
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void scheduleNotification(long delay) {
        Log.i("cool",delay+"");
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delay, pendingIntent);
    }
}
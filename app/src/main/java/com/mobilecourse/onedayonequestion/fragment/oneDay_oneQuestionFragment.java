package com.mobilecourse.onedayonequestion.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mobilecourse.onedayonequestion.R;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Locale;

public class oneDay_oneQuestionFragment extends Fragment {
    public static oneDay_oneQuestionFragment newInstance() {
        return (new oneDay_oneQuestionFragment());
    }

    private CountDownTimer mCountDownTimer;

    private TextView mTextViewCountDown;

    private long mTimeLeftInMillis;

    private Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.one_day_one_question_fragment, container, false);

        mTextViewCountDown = view.findViewById(R.id.text_view_countdown);
        TextView welcomeMessage = view.findViewById(R.id.welcomeMessage);

        //Initialize countdown timer at the remaining time before quizz start and start it
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(
                        getActivity().getApplicationContext());
        long timeQuizzStart = prefs.getLong("time", 0);
        Calendar c  = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long currentTime = (System.currentTimeMillis() - c.getTimeInMillis());
        mTimeLeftInMillis = timeQuizzStart - currentTime;

        //if the time for the test is already passed, we display the daily question
        if (mTimeLeftInMillis <= 0){
            displayQuestionOfTheDay();
        }

        updateCountDownText();
        startTimer();

        //display of the welcome message from the shared preferences
        String welcomeMessage_value = prefs.getString("welcomeMessage","Welcome in 'One day, One question'");
        if (welcomeMessage_value == ""){
            welcomeMessage_value = "Welcome in 'One day, One question'";
        }
        welcomeMessage.setText(welcomeMessage_value);

        return view;
    }


    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 3600) / 1000;
        int minutes = (int) ((mTimeLeftInMillis - hours * 3600 *1000) / 60) / 1000;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes,seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void startTimer() {
        //the timer will update at each second
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                displayQuestionOfTheDay();
            }
        }.start();
    }

    private void displayQuestionOfTheDay(){
        if (this.fragment == null) this.fragment = dailyQuizz.newInstance();
        if (!fragment.isVisible()){
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment).commit();
        }
    }
}

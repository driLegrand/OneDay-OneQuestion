package com.mobilecourse.onedayonequestion.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobilecourse.onedayonequestion.R;

public class Quizz extends Fragment  implements View.OnClickListener {

    public static Quizz newInstance() {
        return new Quizz();
    }


    private Fragment fragment;
    SharedPreferences prefs;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quizz_fragment, container, false);

        prefs = getActivity().getSharedPreferences(null, Context.MODE_PRIVATE);
        Button buttons[] = new Button[24];
        String categories[] = getResources().getStringArray(R.array.categories_entries);

        //creation of the buttons for the categories of the quizz and setOnClickListener
        for (int i = 9; i <= 32; i++) {
            String buttonID = "button" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
            buttons[i-9] = view.findViewById(resID);
            buttons[i-9].setOnClickListener(Quizz.this);
            buttons[i-9].setText(categories[i-8] + "\nHighScore : " + prefs.getInt("highscore"+(i-8),0));
        }

        // it will go back to the main page of the application when we press the back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = oneDay_oneQuestionFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_frame_layout, fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return view;
    }

    //in function of the button clicked, we will not put the same id of categories in the bundle
    @Override
    public void onClick(View v) {
        boolean continueSwitch = true;
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.button9:
                b.putInt("Category",9);
                break;

            case R.id.button10:
                b.putInt("Category",10);
                break;

            case R.id.button11:
                b.putInt("Category",11);
                break;

            case R.id.button12:
                b.putInt("Category",12);
                break;

            case R.id.button13:
                b.putInt("Category",13);
                break;

            case R.id.button14:
                b.putInt("Category",14);
                break;

            case R.id.button15:
                b.putInt("Category",15);
                break;

            case R.id.button16:
                b.putInt("Category",16);
                break;

            case R.id.button17:
                b.putInt("Category",17);
                break;

            case R.id.button18:
                b.putInt("Category",18);
                break;

            case R.id.button19:
                b.putInt("Category",19);
                break;

            case R.id.button20:
                b.putInt("Category",20);
                break;

            case R.id.button21:
                b.putInt("Category",21);
                break;

            case R.id.button22:
                b.putInt("Category",22);
                break;

            case R.id.button23:
                b.putInt("Category",23);
                break;

            case R.id.button24:
                b.putInt("Category",24);
                break;

            case R.id.button25:
                b.putInt("Category",25);
                break;

            case R.id.button26:
                b.putInt("Category",26);
                break;

            case R.id.button27:
                b.putInt("Category",27);
                break;

            case R.id.button28:
                b.putInt("Category",28);
                break;

            case R.id.button29:
                b.putInt("Category",29);
                break;

            case R.id.button30:
                b.putInt("Category",30);
                break;

            case R.id.button31:
                b.putInt("Category",31);
                break;

            case R.id.button32:
                b.putInt("Category",32);
                break;

            default:
                continueSwitch = false;
                break;
        }

        //redirection to the fragment questionQuizz with the id of the category
        if (continueSwitch){
            if (this.fragment == null) this.fragment = questionQuizz.newInstance();
            if (!fragment.isVisible()){
                fragment.setArguments(b);
                getFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_frame_layout, fragment).commit();
            }
        }
    }
}

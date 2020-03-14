package com.mobilecourse.onedayonequestion.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobilecourse.onedayonequestion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class questionQuizz extends Fragment {

    public static questionQuizz newInstance() {
        return new questionQuizz();
    }


    SharedPreferences prefs;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.question_quizz_fragment, container, false);

        int i = 8;

        Bundle b = this.getArguments();

        if(b != null){
            //i represents the category of the questions
            i = b.getInt("Category");

            //Initialisation of the components of the layout
            TextView question = v.findViewById(R.id.question);
            final Button answer1 = v.findViewById(R.id.True1);
            final Button answer2 = v.findViewById(R.id.False2);
            final Button answer3 = v.findViewById(R.id.Answer3);
            final Button answer4 = v.findViewById(R.id.Answer4);
            final Button next = v.findViewById(R.id.Next);
            TextView compte = v.findViewById(R.id.compte);
            ProgressBar loading = v.findViewById(R.id.loading);

            //in this case, the 10 questions are in the bundle, in a JSON send as a string, given
            //that it is not the first question
            if (i == 0){

                loading.setVisibility(View.INVISIBLE);

                final int points = b.getInt("Points");
                final String result = b.getString("Questions");
                final int number = b.getInt("Number");

                //this array is the list of the buttons
                final Button[] listAnswer = {
                        answer1,
                        answer2,
                        answer3,
                        answer4
                };

                //In this case, the quizz is finished, and we have the score of the player
                if (number == 11){

                    //initialisation of the JSON and of his content
                    JSONObject json = null;
                    JSONArray jsonArray = null;
                    try {
                        json = new JSONObject(result);
                        jsonArray = json.getJSONArray("results");
                        final JSONObject object1 = jsonArray.getJSONObject(number - 2);

                        //obtention of the id of the category
                        String category = object1.getString("category");
                        String categories[] = getResources().getStringArray(R.array.categories_entries);
                        i = Arrays.asList(categories).indexOf(category);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    answer1.setVisibility(View.INVISIBLE);
                    answer2.setVisibility(View.INVISIBLE);
                    answer3.setVisibility(View.INVISIBLE);
                    answer4.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.INVISIBLE);
                    compte.setVisibility(View.INVISIBLE);

                    prefs = getActivity().getSharedPreferences(null, Context.MODE_PRIVATE);

                    //In this case, the player as reach the highest score possible, but he can also
                    //have a new highscore or even don't reach his highscore
                    if (points == 10){
                        question.setText("You have reach the highest score possible, which is 10, so well done !");

                        //the i is used in ordered to have one highscore for each category
                        prefs.edit().putInt("highscore"+i,points).apply();
                    }
                    else if (points > prefs.getInt("highscore" + i, 0)){
                        question.setText("You have done " + points + " !   And it is your new high score, well done !!");
                        prefs.edit().putInt("highscore"+i,points).apply();
                    }
                    else{
                        question.setText("You have done only " + points + ", too bad, you haven't reach your high score.");
                    }
                }
                else{

                    //Initialisation of the JSON
                    JSONObject json = null;
                    JSONArray jsonArray = null;

                    //We indicate the current position
                    compte.setText("Question " + number +"/10");

                    try {
                        //initialisation of the content of the JSON
                        json = new JSONObject(result);
                        jsonArray = json.getJSONArray("results");
                        final JSONObject object1 = jsonArray.getJSONObject(number - 1);

                        //we obtain the question from the JSON
                        question.setText(object1.getString("question").replace("&quot;","'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));

                        //In this case, the question is true or false
                        if (object1.getString("type").equals("boolean")){
                            answer3.setVisibility(View.INVISIBLE);
                            answer4.setVisibility(View.INVISIBLE);
                            answer1.setText("True");
                            answer2.setText("False");

                            //if we click on True, the onCLickListener is call
                            answer1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //if we haven't click on an answer yet
                                    if (!answer1.getText().equals("True ")){
                                        try {

                                            //Verify if the answer is correct or not
                                            if (object1.getString("correct_answer").equals("True")){
                                                answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                            }
                                            else{
                                                answer1.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        //Indicate that we click on an answer
                                        answer1.setText("True ");
                                        answer2.setText("False ");
                                    }
                                }
                            });


                            //if we click on False, the onCLickListener is call
                            answer2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //if we haven't click on an answer yet
                                    if (!answer2.getText().equals("False ")){
                                        try {

                                            //Verify if the answer is correct or not
                                            if (object1.getString("correct_answer").equals("False")){
                                                answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                            }
                                            else{
                                                answer2.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        //Indicate that we click on an answer
                                        answer2.setText("False ");
                                        answer1.setText("True ");

                                    }
                                }
                            });
                        }

                        //In this case, the player has the choice between 4 answers
                        else {
                            answer3.setVisibility(View.VISIBLE);
                            answer4.setVisibility(View.VISIBLE);

                            //Initialisation of the incorrect answers
                            JSONArray falseAnswer = null;
                            ArrayList<String> answer = new ArrayList<>();
                            try {
                                falseAnswer = object1.getJSONArray("incorrect_answers");

                                //Initialisation of an arrayList which contains all the answers possible
                                answer.add(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));
                                answer.add(falseAnswer.getString(0).replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));
                                answer.add(falseAnswer.getString(1).replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));
                                answer.add(falseAnswer.getString(2).replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //filling of the button answer with the answers in a random order
                            int k = 4;
                            Random r = new Random();
                            while (k > 0) {
                                int temp = r.nextInt(k);
                                listAnswer[k - 1].setText(answer.get(temp));
                                answer.remove(temp);
                                k = k - 1;
                            }

                            //we fill an array with the text of the answers
                            final String[] answers = {
                                    (String) answer1.getText(),
                                    (String) answer2.getText(),
                                    (String) answer3.getText(),
                                    (String) answer4.getText()
                            };



                            //Creation of the onCLickListeners
                            answer1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //if we haven't answer yet
                                    if (!Pattern.matches("^.*? $",answer4.getText()) || !Pattern.matches("^.*? $",answer1.getText()) || !Pattern.matches("^.*? $",answer2.getText()) || !Pattern.matches("^.*? $",answer3.getText())) {
                                        try {
                                            if (object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é").equals(answer1.getText())) {
                                                answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                            } else {
                                                answer1.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                                if (answers[2].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer3.setBackgroundColor(getResources().getColor(R.color.valid));
                                                } else if (answers[1].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                                } else if (answers[3].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer4.setBackgroundColor(getResources().getColor(R.color.valid));
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        //we indicate that the player answer the question
                                        answer1.setText(answer1.getText() + " ");
                                        answer2.setText(answer2.getText() + " ");
                                        answer3.setText(answer3.getText() + " ");
                                        answer4.setText(answer4.getText() + " ");
                                    }
                                }
                            });

                            answer2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //if we haven't answer yet
                                    if (!Pattern.matches("^.*? $",answer4.getText()) || !Pattern.matches("^.*? $",answer1.getText()) || !Pattern.matches("^.*? $",answer2.getText()) || !Pattern.matches("^.*? $",answer3.getText())) {
                                        try {
                                            if (object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é").equals(answer2.getText())) {
                                                answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                            } else {
                                                answer2.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                                if (answers[0].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                                } else if (answers[2].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer3.setBackgroundColor(getResources().getColor(R.color.valid));
                                                } else if (answers[3].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer4.setBackgroundColor(getResources().getColor(R.color.valid));
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        //we indicate that the player answer the question
                                        answer1.setText(answer1.getText() + " ");
                                        answer2.setText(answer2.getText() + " ");
                                        answer3.setText(answer3.getText() + " ");
                                        answer4.setText(answer4.getText() + " ");
                                    }
                                }
                            });

                            answer3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //if we haven't answer yet
                                    if (!Pattern.matches("^.*? $",answer4.getText()) || !Pattern.matches("^.*? $",answer1.getText()) || !Pattern.matches("^.*? $",answer2.getText()) || !Pattern.matches("^.*? $",answer3.getText())) {
                                        try {
                                            if (object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é").equals(answer3.getText())) {
                                                answer3.setBackgroundColor(getResources().getColor(R.color.valid));
                                            } else {
                                                answer3.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                                if (answers[0].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                                } else if (answers[1].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                                } else if (answers[3].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer4.setBackgroundColor(getResources().getColor(R.color.valid));
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        //we indicate that the player answer the question
                                        answer1.setText(answer1.getText() + " ");
                                        answer2.setText(answer2.getText() + " ");
                                        answer3.setText(answer3.getText() + " ");
                                        answer4.setText(answer4.getText() + " ");
                                    }
                                }
                            });

                            answer4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //if we haven't answer yet
                                    if (!Pattern.matches("^.*? $",answer4.getText()) || !Pattern.matches("^.*? $",answer1.getText()) || !Pattern.matches("^.*? $",answer2.getText()) || !Pattern.matches("^.*? $",answer3.getText())) {
                                        try {
                                            if (object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é").equals(answer4.getText())) {
                                                answer4.setBackgroundColor(getResources().getColor(R.color.valid));
                                            } else {
                                                answer4.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                                if (answers[0].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                                } else if (answers[1].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                                } else if (answers[2].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))) {
                                                    answer3.setBackgroundColor(getResources().getColor(R.color.valid));
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        //we indicate that the player answer the question
                                        answer1.setText(answer1.getText() + " ");
                                        answer2.setText(answer2.getText() + " ");
                                        answer3.setText(answer3.getText() + " ");
                                        answer4.setText(answer4.getText() + " ");
                                    }
                                }
                            });
                        }

                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Creation of the bundle
                                Bundle b = new Bundle();
                                b.putString("Questions", result);
                                b.putInt("Number", number + 1);
                                boolean invalid = false;
                                boolean valid = false;

                                //Check if the player is right with his answer or not, and give points
                                for (int k = 0; k < 4; k++){
                                    Drawable background = listAnswer[k].getBackground();
                                    if (background instanceof ColorDrawable){
                                        if (getResources().getColor(R.color.incorrect) == ((ColorDrawable) background).getColor()) {
                                            invalid = true;
                                        }
                                        if (getResources().getColor(R.color.valid) == ((ColorDrawable) background).getColor()) {
                                            valid = true;
                                        }
                                    }
                                }
                                if (!invalid && valid){
                                    b.putInt("Points",points+1);
                                }
                                else{
                                    b.putInt("Points",points);
                                }

                                //redirection to the same fragment, but with others data, in order to
                                //ask the next answer or to finich the quizz
                                Fragment fragment = questionQuizz.newInstance();
                                fragment.setArguments(b);
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.activity_main_frame_layout, fragment).commit();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            //In this case, it's the first time that we load this fragment, so we must also ask for
            //the JSON
            else{
                question.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
                answer1.setVisibility(View.INVISIBLE);
                answer2.setVisibility(View.INVISIBLE);
                answer3.setVisibility(View.INVISIBLE);
                answer4.setVisibility(View.INVISIBLE);
                compte.setVisibility(View.INVISIBLE);
                (new questionQuizz.AsyncFlickrJSONData()).execute("https://opentdb.com/api.php?amount=10&category=" + i);
            }

        }

        // In order to go back the screen of the categories when we press the back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = Quizz.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_frame_layout, fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return v;
    }

    //Creation of the async task which will ask the questions and answers to the database
    public class AsyncFlickrJSONData extends AsyncTask<String, Void, JSONArray> {

        private String result;

        @Override
        protected JSONArray doInBackground(String... strings) {
            //We ask for the JSON containing all the answers and questions
            URL url = null;
            HttpsURLConnection urlConnection = null;
            result = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection(); // Open
                InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream

                result = readStream(in); // Read stream
            }
            catch (MalformedURLException e) { e.printStackTrace(); }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }

            //Initialisation of the JSON and filling of it with the previous string
            JSONObject json = null;
            JSONArray listQuestion = null;
            try {
                json = new JSONObject(result);
                listQuestion = json.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return listQuestion;
        }

        private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while (i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            try {
                //Initialisation of all the components of the interface
                TextView actualQuestion = getActivity().findViewById(R.id.question);
                final Button answer1 = getActivity().findViewById(R.id.True1);
                final Button answer2 = getActivity().findViewById(R.id.False2);
                final Button answer3 = getActivity().findViewById(R.id.Answer3);
                final Button answer4 = getActivity().findViewById(R.id.Answer4);
                final Button next = getActivity().findViewById((R.id.Next));
                TextView compte = getActivity().findViewById(R.id.compte);
                ProgressBar loading = getActivity().findViewById(R.id.loading);

                //It's always the first question
                compte.setText("Question 1/10");


                final JSONObject object1 = jsonArray.getJSONObject(0);
                actualQuestion.setText(object1.getString("question").replace("&quot;","'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));

                final Button[] listAnswer = {
                        answer1,
                        answer2,
                        answer3,
                        answer4
                };

                actualQuestion.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                answer1.setVisibility(View.VISIBLE);
                answer2.setVisibility(View.VISIBLE);
                answer3.setVisibility(View.VISIBLE);
                answer4.setVisibility(View.VISIBLE);
                compte.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

                //In this case, the answer is true or false
                if (object1.getString("type").equals("boolean")){
                    answer3.setVisibility(View.INVISIBLE);
                    answer4.setVisibility(View.INVISIBLE);
                    answer1.setText("True");
                    answer2.setText("False");


                    answer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if we haven't answer yet
                            if (!answer1.getText().equals("True ")){
                                try {
                                    if (object1.getString("correct_answer").equals("True")){
                                        answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                    }
                                    else{
                                        answer1.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //we indicate that the player answer the question
                                answer1.setText("True ");
                                answer2.setText("False ");
                            }
                        }
                    });

                    answer2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if we haven't answer yet
                            if (!answer2.getText().equals("False ")){
                                try {
                                    if (object1.getString("correct_answer").equals("False")){
                                        answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                    }
                                    else{
                                        answer2.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //we indicate that the player answer the question
                                answer2.setText("False ");
                                answer1.setText("True ");

                            }
                        }
                    });
                }

                //In this case, the player has the choice between 4 answers
                else{
                    answer3.setVisibility(View.VISIBLE);
                    answer4.setVisibility(View.VISIBLE);

                    //Initialisation of the incorrect answers
                    JSONArray falseAnswer = object1.getJSONArray("incorrect_answers");

                    //Initialisation of an arrayList which contains all the answers possible
                    ArrayList<String> answer = new ArrayList<>();
                    answer.add(object1.getString("correct_answer").replace("&quot;","'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));
                    answer.add(falseAnswer.getString(0).replace("&quot;","'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));
                    answer.add(falseAnswer.getString(1).replace("&quot;","'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));
                    answer.add(falseAnswer.getString(2).replace("&quot;","'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"));

                    //filling of the button answer with the answers in a random order
                    int k = 4;
                    Random r =new Random();
                    while(k > 0){
                        int temp = r.nextInt(k);
                        listAnswer[k-1].setText(answer.get(temp));
                        answer.remove(temp);
                        k = k-1;
                    }

                    //we fill an array with the text of the answers
                    final String[] answers = {
                            (String) answer1.getText(),
                            (String) answer2.getText(),
                            (String) answer3.getText(),
                            (String) answer4.getText()
                    };

                    //Creation of the onCLickListeners
                    answer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if we haven't answer yet
                            if(!Pattern.matches("^.*? $",answer4.getText()) || !Pattern.matches("^.*? $",answer1.getText()) || !Pattern.matches("^.*? $",answer2.getText()) || !Pattern.matches("^.*? $",answer3.getText())){
                                try {
                                    if (object1.getString("correct_answer").equals(answer1.getText())) {
                                        answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                    } else {
                                        answer1.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                        if(answers[2].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer3.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                        else if(answers[1].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                        else if(answers[3].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer4.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //we indicate that the player answer the question
                                answer1.setText(answer1.getText() + " ");
                                answer2.setText(answer2.getText() + " ");
                                answer3.setText(answer3.getText() + " ");
                                answer4.setText(answer4.getText() + " ");
                            }
                        }
                    });

                    answer2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if we haven't answer yet
                            if(!Pattern.matches("^.*? $",answer4.getText()) || !Pattern.matches("^.*? $",answer1.getText()) || !Pattern.matches("^.*? $",answer2.getText()) || !Pattern.matches("^.*? $",answer3.getText())){
                                try {
                                    if (object1.getString("correct_answer").equals(answer2.getText())) {
                                        answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                    } else {
                                        answer2.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                        if(answers[0].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                        else if(answers[2].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer3.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                        else if(answers[3].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer4.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //we indicate that the player answer the question
                                answer1.setText(answer1.getText() + " ");
                                answer2.setText(answer2.getText() + " ");
                                answer3.setText(answer3.getText() + " ");
                                answer4.setText(answer4.getText() + " ");
                            }
                        }
                    });

                    answer3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if we haven't click on an answer yet
                            if(!Pattern.matches("^.*? $",answer4.getText()) || !Pattern.matches("^.*? $",answer1.getText()) || !Pattern.matches("^.*? $",answer2.getText()) || !Pattern.matches("^.*? $",answer3.getText())){
                                try {
                                    if (object1.getString("correct_answer").equals(answer3.getText())) {
                                        answer3.setBackgroundColor(getResources().getColor(R.color.valid));
                                    } else {
                                        answer3.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                        if(answers[0].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                        else if(answers[1].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                        else if(answers[3].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer4.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //we indicate that the player answer the question
                                answer1.setText(answer1.getText() + " ");
                                answer2.setText(answer2.getText() + " ");
                                answer3.setText(answer3.getText() + " ");
                                answer4.setText(answer4.getText() + " ");
                            }
                        }
                    });

                    answer4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if we haven't answer yet
                            if(!Pattern.matches("^.*? $",answer4.getText()) || !Pattern.matches("^.*? $",answer1.getText()) || !Pattern.matches("^.*? $",answer2.getText()) || !Pattern.matches("^.*? $",answer3.getText())){
                                try {
                                    if (object1.getString("correct_answer").equals(answer4.getText())) {
                                        answer4.setBackgroundColor(getResources().getColor(R.color.valid));
                                    } else {
                                        answer4.setBackgroundColor(getResources().getColor(R.color.incorrect));
                                        if(answers[0].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer1.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                        else if(answers[1].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer2.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                        else if(answers[2].equals(object1.getString("correct_answer").replace("&quot;", "'").replace("&#039;","'").replace("&amp;","&").replace("&eacute;","é"))){
                                            answer3.setBackgroundColor(getResources().getColor(R.color.valid));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //we indicate that the player answer the question
                                answer1.setText(answer1.getText() + " ");
                                answer2.setText(answer2.getText() + " ");
                                answer3.setText(answer3.getText() + " ");
                                answer4.setText(answer4.getText() + " ");
                            }
                        }
                    });
                }

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Creation of the bundle
                        Bundle b = new Bundle();
                        boolean invalid = false;
                        boolean valid = false;

                        //Check if the player is right with his answer or not, and give points
                        for (int k = 0; k < 4; k++){
                            Drawable background = listAnswer[k].getBackground();
                            if (background instanceof ColorDrawable){
                                if (getResources().getColor(R.color.incorrect) == ((ColorDrawable) background).getColor()){
                                    invalid = true;
                                }
                                if (getResources().getColor(R.color.valid) == ((ColorDrawable) background).getColor()){
                                    valid = true;
                                }
                            }
                        }
                        int points = 0;
                        if (!invalid && valid){
                            points = 1;
                        }

                        //redirection to the same fragment, but with others data, in order to
                        //ask the next answer
                        b.putString("Questions",result);
                        b.putInt("Number",2);
                        b.putInt("Points",points);
                        Fragment fragment = questionQuizz.newInstance();
                        fragment.setArguments(b);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.activity_main_frame_layout, fragment).commit();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

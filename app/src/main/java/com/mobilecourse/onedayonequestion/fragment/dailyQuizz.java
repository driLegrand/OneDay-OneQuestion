package com.mobilecourse.onedayonequestion.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobilecourse.onedayonequestion.MainActivity;
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
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class dailyQuizz extends Fragment {

    private SharedPreferences prefs;

    static dailyQuizz newInstance() {
        return new dailyQuizz();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.daily_quizz_fragment, container, false);

        prefs = getActivity().getSharedPreferences(null,Context.MODE_PRIVATE);

        //Initialisation of the component of the interface
        Button falsebtn = view.findViewById(R.id.False);
        Button truebtn = view.findViewById(R.id.True);
        TextView dailyQuestion = view.findViewById(R.id.dailyQuestion);
        TextView message = view.findViewById(R.id.message);
        ProgressBar loading = view.findViewById(R.id.loadingDaily);

        //get the actual date
        Date completeDate = new Date();
        CharSequence formatDate  = DateFormat.format("MMMM d, yyyy ", completeDate.getTime());


        //In this case, the player has already answered the question of the day
        if (prefs.getString("Done","Error").equals("Ok") && prefs.getString("dailyDate","Error").equals(formatDate+"")){
            message.setText("You had already answer the question today.");
            message.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
            falsebtn.setVisibility(View.INVISIBLE);
            truebtn.setVisibility(View.INVISIBLE);
            dailyQuestion.setVisibility(View.INVISIBLE);
            Log.i("Ad","Merde");
        }
        else{
            //We display the question of the day
            message.setVisibility(View.INVISIBLE);
            falsebtn.setVisibility(View.VISIBLE);
            truebtn.setVisibility(View.VISIBLE);
            dailyQuestion.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);

            //Set on click answer
            falsebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerFalse(v);
                }
            });
            truebtn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerTrue(v);
                }
            });
            Log.i("Ad","Ok");

            Log.i("Ad",completeDate+"");
            //if we haven't load the question of the day
            if (!prefs.getString("dailyDate","Error").equals(formatDate  + "")){
                prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                String category = "&category=" + prefs.getInt("category",0);
                String difficulty = "&difficulty=" + prefs.getString("difficulty","any").toLowerCase();
                if (difficulty.equals("&difficulty=any")){
                    difficulty="";
                }
                if (category.equals("&category=8")){
                    category="";
                }

                message.setVisibility(View.INVISIBLE);
                falsebtn.setVisibility(View.INVISIBLE);
                truebtn.setVisibility(View.INVISIBLE);
                dailyQuestion.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);

                //ask the JSON containing the daily question
                (new AsyncFlickrJSONData()).execute("https://opentdb.com/api.php?amount=1&type=boolean" + category + difficulty);
            }
            else{
                //load the question from the shared preferences
                dailyQuestion.setText(prefs.getString("dailyQuestion","Error").replace("&quot;", "'").replace("&#039;","'"));
            }
        }

        return view;
    }

    private  void answerTrue(View view){
        prefs = getActivity().getSharedPreferences(null, Context.MODE_PRIVATE);

        Date completeDate = new Date();
        CharSequence formatDate  = DateFormat.format("MMMM d, yyyy ", completeDate.getTime());

        //if we haven't answer yet
        if (!prefs.getString("Done","Error").equals("Ok") || !prefs.getString("dailyDate","Error").equals(formatDate+"")){

            Button truebtn = getActivity().findViewById(R.id.True);
            if (prefs.getString("dailyAnswer","Error").equals("True")){
                truebtn.setBackgroundColor(getResources().getColor(R.color.valid));
            }
            else{
                truebtn.setBackgroundColor(getResources().getColor(R.color.incorrect));
            }

            //we indicate that the player answer the question
            prefs.edit().putString("Done","Ok").apply();
        }
    }
    private  void answerFalse(View view) {
        prefs = getActivity().getSharedPreferences(null, Context.MODE_PRIVATE);

        Date completeDate = new Date();
        CharSequence formatDate  = DateFormat.format("MMMM d, yyyy ", completeDate.getTime());

        //if we haven't answer yet
        if (!prefs.getString("Done","Error").equals("Ok") || !prefs.getString("dailyDate","Error").equals(formatDate+"")) {

            Button falsebtn = getActivity().findViewById(R.id.False);
            if (prefs.getString("dailyAnswer", "Error").equals("False")) {
                falsebtn.setBackgroundColor(getResources().getColor(R.color.valid));
            } else {
                falsebtn.setBackgroundColor(getResources().getColor(R.color.incorrect));
            }

            //We indicate that the player answer question
            prefs.edit().putString("Done", "Ok").apply();
        }

    }

    //it's the asyncTask that will ask the JSON containing the daily question
    public class AsyncFlickrJSONData extends AsyncTask<String, Void, JSONObject> {

        private SharedPreferences prefs;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prefs = getActivity().getSharedPreferences(null, Context.MODE_PRIVATE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            //ask for the JSON
            URL url;
            HttpsURLConnection urlConnection = null;
            String result = null;
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


            //filling the JSON with the previous string (result)
            JSONObject json = null;
            try {
                //put the daily question and answer in the shared preferences
                json = new JSONObject(result);
                JSONArray listQuestion = json.getJSONArray("results");
                prefs.edit().putString("dailyQuestion",listQuestion.getJSONObject(0).getString("question")).apply();
                Date d = new Date();
                CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
                prefs.edit().putString("dailyDate", s + "").apply();
                prefs.edit().putString("dailyAnswer",listQuestion.getJSONObject(0).getString("correct_answer")).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
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
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            //change the interface in order to display the daily question
            TextView dailyQuestion = getActivity().findViewById(R.id.dailyQuestion);
            dailyQuestion.setText(prefs.getString("dailyQuestion","There is no question with your criteria").replace("&quot;", "'").replace("&#039;","'"));

            Button falsebtn = getActivity().findViewById(R.id.False);
            Button truebtn = getActivity().findViewById(R.id.True);
            ProgressBar loading = getActivity().findViewById(R.id.loadingDaily);

            falsebtn.setVisibility(View.VISIBLE);
            truebtn.setVisibility(View.VISIBLE);
            dailyQuestion.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
        }
    }

}

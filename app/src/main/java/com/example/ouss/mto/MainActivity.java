package com.example.ouss.mto;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        EditText ville = findViewById(R.id.eit);
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" + ville.getText().toString() + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        new MyAsyncTask().execute(url);
    }

    public class MyAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(1000);
                String dataJson = convertStreamToString(urlConnection.getInputStream());
                publishProgress(dataJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            try {
                JSONObject json = new JSONObject((String) values[0]);
                JSONObject query = json.getJSONObject("query");
                JSONObject results = query.getJSONObject("results");
                JSONObject channel = results.getJSONObject("channel");
                JSONObject wind = channel.getJSONObject("wind");
                String direction = wind.getString("direction");
                TextView textView = findViewById(R.id.view);
                textView.setText(direction);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String convertStreamToString(InputStream stream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String line;
            String allStrings = "";
            do {
                line = bufferedReader.readLine();
                if (line != null) {
                    allStrings += line;
                }
            } while (line != null);
            return allStrings;
        }
    }
}
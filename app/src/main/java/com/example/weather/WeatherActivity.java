package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import static com.example.weather.MainActivity.EXTRA_MESSAGE;
import static com.example.weather.MainActivity.RETURN_MESSAGE;

public class WeatherActivity extends AppCompatActivity {

    // api key: 4a16259e4f5af2ce7fd19235af4ecdb6
    String temp;
    String clouds;

    static final String LOCATION = "LOCATION";
    static final String RETURNING = "";
    static final String DEGREE_SYMBOL = "°";
    String location;
    RequestQueue requestQueue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getLocation();
        updateGUI();

    }

    // get the locations and sets the title
    private void getLocation() {
        try {
            Bundle locations = getIntent().getExtras();
            location = locations.getString(EXTRA_MESSAGE);
            this.setTitle(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // for loop that updates gui using api calls
    private void updateGUI() {

        TextView city = (TextView) findViewById(R.id.textCity);
        city.setText(location);
        getCityInformation();
    }


    // calls api and sets current data equal to entry information
    private void getCityInformation() {
        // API call

        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" +
                        location.toLowerCase() +
                        "&APPID=4a16259e4f5af2ce7fd19235af4ecdb6";
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    for (int i = 0; i < 40; i += 8) {
                                        JSONArray theList = response.getJSONArray("list");
                                        int itr = i / 8;

                                    // TEMPERATURE
                                        JSONObject element = theList.getJSONObject(i);
                                        String temperature="["+element.getJSONObject("main").toString()+"]";
                                        JSONArray tempArray = new JSONArray(temperature);
                                        JSONObject obj = tempArray.getJSONObject(0);

                                        int tempDescription = obj.getInt("temp");

                                        temp = Integer.toString((int)(tempDescription - 273.15) * 9/5 + 32);
                                        int tempID = getResources().getIdentifier("temp" + Integer.toString(itr+1), "id", getPackageName());
                                        TextView tempText = findViewById(tempID);
                                        tempText.setText(String.format("%s%s%s", temp, DEGREE_SYMBOL,"F"));
                                        tempText.setTextSize(25);

                                    // WEATHER
                                        JSONArray newArray = element.getJSONArray("weather");
                                        JSONObject newObject = newArray.getJSONObject(0);
                                        String theClouds = newObject.getString("description");

                                        clouds = theClouds;
                                        String iconType = newObject.getString("icon");

                                        // Text
                                        int cloudsID = getResources().getIdentifier("clouds" + Integer.toString(itr + 1), "id", getPackageName());
                                        TextView cloudsText =  (TextView) findViewById(cloudsID);
                                        cloudsText.setText(clouds);
                                        cloudsText.setTextSize(25);

                                        // Image
                                        int imageID = getResources().getIdentifier("image" + Integer.toString(itr + 1), "id", getPackageName());
                                        ImageView image = (ImageView) findViewById(imageID);
                                        String imageURL = String.format("%s%s%s","https://openweathermap.org/img/wn/",iconType.substring(0,2),"d@2x.png");
                                        new DownloadImageTask(image).execute(imageURL);

                                    }


                                } catch (JSONException ex) {
                                    System.out.println("start stack trace");
                                    ex.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {}
                        }
                );

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        // save preferences
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LOCATION, RETURNING);
        editor.apply();
        // create main activity intent
        Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
        intent.putExtra(RETURN_MESSAGE, RETURNING);
        startActivity(intent);
        // close this activity
        finish();
        super.onBackPressed();
    }

    // on save instance
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(LOCATION, RETURNING);
    }

    // on pause
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LOCATION, RETURNING);
        editor.apply();
    }


    // source: https://medium.com/@crossphd/android-image-loading-from-a-string-url-6c8290b82c5e
    // processes image from the internet and displays it in the app
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}

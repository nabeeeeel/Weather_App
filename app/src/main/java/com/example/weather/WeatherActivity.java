package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;

import static com.example.weather.MainActivity.EXTRA_MESSAGE;
import static com.example.weather.MainActivity.RETURN_MESSAGE;

public class WeatherActivity extends AppCompatActivity {

    static final String LOCATION = "LOCATION";
    static final String RETURNING = "";
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getLocation();
    }

    // get the locations
    private void getLocation() {
        try {
            Bundle locations = getIntent().getExtras();
            location = locations.getString(EXTRA_MESSAGE);
            this.setTitle(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//comment
    // on pause
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LOCATION, RETURNING);
        editor.apply();
    }

}

package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.weather";
    public static final String RETURN_MESSAGE = "com.example.MainActivity";
    static final String LOCATION = "LOCATION";
    String location = "";
    Spinner locationSpinner;
    Button selectOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set objects
        locationSpinner = (Spinner) findViewById(R.id.spinner);
        selectOption = (Button) findViewById(R.id.button);

        // create adapter with spinner items
        final ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(
                MainActivity.this,
                R.layout.spinner_resource,
                getResources().getStringArray(R.array.locations));
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        // spinner selector
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = locationSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        // button
        selectOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        if (savedInstanceState != null){
            SharedPreferences settings = getPreferences(MODE_PRIVATE);
            location = settings.getString(LOCATION, "");
            getLocation();
            sendMessage();
        }

    }

    public void sendMessage() {

        if (location.equals("Select city…")) {
            Toast toast = Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (location.equals("")) {
            location = "Select city…";
            return;
        };
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra(EXTRA_MESSAGE, location);
        startActivity(intent);
    }


    // get the locations
    private void getLocation() {
        try {
            Bundle locations = getIntent().getExtras();
            if (locations == null) return;
            location = locations.getString(RETURN_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        location = savedInstanceState.getString(LOCATION);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(LOCATION, location);
        getLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LOCATION, location);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        location = settings.getString(LOCATION, "");
        getLocation();
        sendMessage();
    }
}

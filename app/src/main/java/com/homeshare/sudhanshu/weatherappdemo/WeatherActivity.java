package com.homeshare.sudhanshu.weatherappdemo;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.URI;

import static android.R.attr.permission;

public class WeatherActivity extends AppCompatActivity implements WeatherFragment.OnFragmentInteractionListener{

    private String lat, lon;
    private RemoteFetch remoteFetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Log.d("LOC", "onCreate: ");

        if(checkPermission()){
            GeoLoaction geoLoaction = new GeoLoaction(this);
            geoLoaction.getLocation();
        }else{
            Log.d("LOC", "onCreate: Failed to get permissions");
        }

        final Context context = this.getBaseContext();
        final RemoteFetch remoteFetch = new RemoteFetch(context);


        SharedPreferences sharedPrefs = this.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        sharedPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("PREF", "onSharedPreferenceChanged: ");
                remoteFetch.getJSON();
            }
        });

        String s = sharedPrefs.getString(Constants.LON, "Missing");
        Log.d("PREF", "onCreate: " + s);

        if(savedInstanceState == null) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            WeatherFragment fragment = new WeatherFragment();
            fragmentTransaction.add(R.id.container, fragment);
            fragmentTransaction.commit();

        }
    }

    public boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("INTER", "onFragmentInteraction: ");
    }
}

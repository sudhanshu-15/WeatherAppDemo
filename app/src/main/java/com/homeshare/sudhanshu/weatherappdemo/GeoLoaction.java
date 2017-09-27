package com.homeshare.sudhanshu.weatherappdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by sudhanshu on 9/26/17.
 */

public class GeoLoaction {

    private FusedLocationProviderClient mFusedLocationClient;
    private Location lastLocation;
    private Activity activity;

    public GeoLoaction(Activity activity){
        this.activity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    @SuppressWarnings("MissingPermission")
    public void getLocation(){

        Log.d("LOC", "getLocation: ");
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lastLocation = location;
                        createLatLongString();
                        Log.d("LOC", "onSuccess: " + lastLocation.getLatitude());
                    }else{
                        Log.d("LOC", "onSuccess: Null");
                    }
                }
        });
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void createLatLongString(){
        SharedPreferences sharePref = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString(Constants.LAT, Double.toString(lastLocation.getLatitude()));
        editor.putString(Constants.LON, Double.toString(lastLocation.getLongitude()));
        editor.commit();
        Log.d("PREF", "createLatLongString: Commited");
    }
}

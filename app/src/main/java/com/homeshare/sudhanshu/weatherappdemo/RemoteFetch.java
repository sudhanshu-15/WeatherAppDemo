package com.homeshare.sudhanshu.weatherappdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sudhanshu on 9/26/17.
 */

public class RemoteFetch {
    public static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String API_KEY = "&units=metric&APPID=";
    private JSONObject data;
    private Context context;
    private String lat, lon;
    private String latlong;
    private String url;

    public RemoteFetch(Context context){
        this.context = context;

        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        lat = sharedPref.getString(Constants.LAT, "Not Found");
        lon = sharedPref.getString(Constants.LON, "Not Found");
        if (!lat.equals("Not Found") && !lon.equals("Not Found")){
            latlong = "lat="+lat+"&lon="+lon;
            Log.d("LATLON", "RemoteFetch: " + latlong);
            url = OPEN_WEATHER_MAP_API+latlong+API_KEY+Constants.API_KEY;
            Log.d("URL", "RemoteFetch: "+url);
            getJSON();
        }
    }
    public void getJSON(){

        RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    data = response;
                    Log.d("VOL", "onResponse: " + data.toString());
                    parseWeatherData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error: ", error.getMessage());
                    data = null;
                }
            });
        requestQueue.add(jsonObjectRequest);
    }

    private void parseWeatherData(JSONObject jsonData){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        try{
            String city = jsonData.getString("name");
            Log.d("CITY", "parseWeatherData: " + city);
            editor.putString(Constants.CITY, city);
            JSONObject details = jsonData.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonData.getJSONObject("main");

            String detailField = details.getString("description") +
                                "\n" + "Humidity: " + main.getString("humidity") + "%" +
                                "\n" + "Pressure: " + main.getString("pressure") + "hPa";
            editor.putString(Constants.DETAIL, detailField);
            Log.d("DET", "parseWeatherData: " + detailField);

            String temp = String.format("%.2f", main.getDouble("temp")) + " C";
            Log.d("TEMP", "parseWeatherData: " + temp);
            editor.putString(Constants.TEMP, temp);

            String icon = setWeatherIcon(details.getInt("id"), jsonData.getJSONObject("sys").getLong("sunrise") * 1000, jsonData.getJSONObject("sys").getLong("sunset") * 1000);
            editor.putString(Constants.ICON, icon);
            Log.d("ICON", "parseWeatherData: "+icon);

            DateFormat df = DateFormat.getDateInstance();
            String update = df.format(new Date(jsonData.getLong("dt")* 1000));
            Log.d("UPDATE", "parseWeatherData: "+update);
            editor.putString(Constants.UPDATE, update);

            editor.commit();
        }catch (JSONException e){
            Log.d("JSON", "parseWeatherData: NOT FOUND");
        }
    }

    private String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId/100;
        String icon = "";
        if (actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime >= sunrise && currentTime <= sunset) {
                icon = context.getResources().getString(R.string.weather_sunny);
            }else {
                icon = context.getResources().getString(R.string.weather_clear_night);
            }
        }else{
            switch (id){
                case 2:
                    icon = context.getResources().getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = context.getResources().getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = context.getResources().getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = context.getResources().getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = context.getResources().getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = context.getResources().getString(R.string.weather_rainy_day);
                    break;
            }
        }
        return icon;
    }
}

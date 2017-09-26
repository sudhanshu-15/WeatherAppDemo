package com.homeshare.sudhanshu.weatherappdemo;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sudhanshu on 9/26/17.
 */

public class RemoteFetch {
    public static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?zip=%s,us&units=metric&APPID=";
    private JSONObject data;

    public JSONObject getJSON(Context context, String zip){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        try {
            String url = String.format(OPEN_WEATHER_MAP_API, zip);
            url = url + Constants.API_KEY;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    data = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error: ", error.getMessage());
                    data = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            data = null;
        }
        return data;
    }
}

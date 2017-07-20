package com.quocngay.carparkbooking.tasks;

import android.os.AsyncTask;

import com.quocngay.carparkbooking.other.DirectionsJSONParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Quang Si on 7/19/2017.
 */

public class DirectionParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... params) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(params[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }
}

package com.quocngay.carparkbooking.tasks;

/**
 * Created by Quang Si on 8/2/2017.
 */

import android.os.AsyncTask;

import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.other.DirectionsJSONParser;

import org.json.JSONObject;

public class GetLocationDistanceDuration extends AsyncTask<String, Integer, LocationDataModel> {

    private GarageModel garageModel;

    public GetLocationDistanceDuration(GarageModel garageModel) {
        this.garageModel = garageModel;
    }

    @Override
    protected LocationDataModel doInBackground(String... params) {
        JSONObject jObject;
        LocationDataModel result = null;
        try {
            jObject = new JSONObject(params[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser(garageModel);
            result = parser.parseWithoutRoutes(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
//            progressBar.setProgress(values[0], true);
    }
}
